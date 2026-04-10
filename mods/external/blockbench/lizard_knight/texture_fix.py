from __future__ import annotations

import base64
import json
import math
import struct
from collections import defaultdict
from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parent
GLTF_PATH = ROOT / "source" / "model.gltf"
SOURCE_TEXTURE = ROOT / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE = ROOT / "model.png"

_COMPONENT_FORMATS = {
    5120: "b",
    5121: "B",
    5122: "h",
    5123: "H",
    5125: "I",
    5126: "f",
}

_NUM_COMPONENTS = {
    "SCALAR": 1,
    "VEC2": 2,
    "VEC3": 3,
    "VEC4": 4,
    "MAT2": 4,
    "MAT3": 9,
    "MAT4": 16,
}


def load_gltf(path: Path) -> tuple[dict, list[bytes]]:
    root = json.loads(path.read_text(encoding="utf-8"))
    buffers: list[bytes] = []
    for entry in root.get("buffers", []):
        uri = entry.get("uri", "")
        if uri.startswith("data:"):
            buffers.append(base64.b64decode(uri.split(",", 1)[1]))
        else:
            buffers.append((path.parent / uri).read_bytes())
    return root, buffers


def accessor(root: dict, buffers: list[bytes], accessor_index: int) -> list[tuple[float, ...]]:
    entry = root["accessors"][accessor_index]
    buffer_view = root["bufferViews"][entry["bufferView"]]
    component_type = entry["componentType"]
    component_count = _NUM_COMPONENTS[entry["type"]]
    fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
    stride = buffer_view.get("byteStride", struct.calcsize(fmt))
    byte_offset = buffer_view.get("byteOffset", 0) + entry.get("byteOffset", 0)
    raw = buffers[buffer_view["buffer"]]

    out: list[tuple[float, ...]] = []
    for index in range(entry["count"]):
        start = byte_offset + index * stride
        out.append(struct.unpack_from(fmt, raw, start))
    return out


def quantized(value: float) -> float:
    return round(value, 6)


def primitive_vertices(root: dict, buffers: list[bytes], primitive: dict) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [tuple(map(float, xyz)) for xyz in accessor(root, buffers, primitive["attributes"]["POSITION"])]
    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [tuple(map(float, uv)) for uv in accessor(root, buffers, primitive["attributes"]["TEXCOORD_0"])]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(value[0]) for value in accessor(root, buffers, primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def primitive_face_rects(root: dict, buffers: list[bytes], primitive: dict, image_width: int, image_height: int) -> dict[str, tuple[int, int, int, int]]:
    positions, texcoords = primitive_vertices(root, buffers, primitive)
    xs = [p[0] for p in positions]
    ys = [p[1] for p in positions]
    zs = [p[2] for p in positions]

    local_min = (min(xs), min(ys), min(zs))
    faces: dict[str, list[tuple[float, float]]] = defaultdict(list)

    for index in range(0, len(positions) - 2, 3):
        tri_positions = positions[index:index + 3]
        tri_uvs = texcoords[index:index + 3]

        tri_x = {quantized(p[0]) for p in tri_positions}
        tri_y = {quantized(p[1]) for p in tri_positions}
        tri_z = {quantized(p[2]) for p in tri_positions}

        if len(tri_x) == 1:
            face = "west" if quantized(next(iter(tri_x))) == quantized(local_min[0]) else "east"
        elif len(tri_y) == 1:
            face = "down" if quantized(next(iter(tri_y))) == quantized(local_min[1]) else "up"
        elif len(tri_z) == 1:
            face = "north" if quantized(next(iter(tri_z))) == quantized(local_min[2]) else "south"
        else:
            continue

        faces[face].extend(tri_uvs)

    out: dict[str, tuple[int, int, int, int]] = {}
    for face_name, uv_points in faces.items():
        us = [uv[0] for uv in uv_points]
        vs = [uv[1] for uv in uv_points]
        min_u = min(us)
        min_v = min(vs)
        max_u = max(us)
        max_v = max(vs)

        left = round(min_u * image_width)
        right = round(max_u * image_width)
        top = round((1.0 - max_v) * image_height)
        bottom = round((1.0 - min_v) * image_height)
        out[face_name] = (left, top, right, bottom)

    return out


def is_mirrored_box(rects: dict[str, tuple[int, int, int, int]]) -> bool:
    if "east" not in rects or "west" not in rects:
        return False
    return rects["west"][0] < rects["east"][0]


def swap_rectangles(image: Image.Image, rect_a: tuple[int, int, int, int], rect_b: tuple[int, int, int, int]) -> None:
    patch_a = image.crop(rect_a)
    patch_b = image.crop(rect_b)
    image.paste(patch_b, rect_a)
    image.paste(patch_a, rect_b)


def main() -> None:
    if not GLTF_PATH.exists():
        raise SystemExit(f"Missing GLTF: {GLTF_PATH}")
    if not SOURCE_TEXTURE.exists():
        raise SystemExit(f"Missing source texture: {SOURCE_TEXTURE}")

    root, buffers = load_gltf(GLTF_PATH)
    image = Image.open(SOURCE_TEXTURE).convert("RGBA")
    output = image.copy()

    mirrored_mesh_indices: list[int] = []

    for mesh_index, mesh in enumerate(root.get("meshes", [])):
        for primitive in mesh.get("primitives", []):
            rects = primitive_face_rects(root, buffers, primitive, image.width, image.height)
            if not is_mirrored_box(rects):
                continue

            east_rect = rects["east"]
            west_rect = rects["west"]

            if (east_rect[2] - east_rect[0]) != (west_rect[2] - west_rect[0]) or (east_rect[3] - east_rect[1]) != (west_rect[3] - west_rect[1]):
                raise SystemExit(
                    f"Cannot swap mirrored mesh {mesh_index}: east/west face sizes differ "
                    f"{east_rect} vs {west_rect}"
                )

            swap_rectangles(output, east_rect, west_rect)
            mirrored_mesh_indices.append(mesh_index)

    output.save(OUTPUT_TEXTURE)

    print(f"Wrote {OUTPUT_TEXTURE}")
    if mirrored_mesh_indices:
        print("Swapped east/west box faces for mirrored meshes:", ", ".join(str(index) for index in mirrored_mesh_indices))
    else:
        print("No mirrored box faces detected; output texture is an unchanged copy of the source texture.")


if __name__ == "__main__":
    main()