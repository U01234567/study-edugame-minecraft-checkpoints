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

# model.java contains 15 visible cubes, in the same mesh order as model.gltf meshes 0..14.
# Mesh 15 is the hitbox helper and uses the 1x1 placeholder texture; it is intentionally ignored.
BOX_LAYOUTS = [
    # mesh_index, tex_u, tex_v, size_x, size_y, size_z
    (0, 0, 0, 5, 5, 8),
    (1, 28, 0, 4, 1, 8),
    (2, 54, 0, 4, 1, 8),
    (3, 80, 0, 3, 3, 3),
    (4, 94, 0, 5, 3, 1),
    (5, 108, 0, 5, 3, 1),
    (6, 122, 0, 7, 1, 4),
    (7, 146, 0, 1, 3, 1),
    (8, 152, 0, 1, 3, 1),
    (9, 158, 0, 1, 3, 2),
    (10, 166, 0, 1, 3, 2),
    (11, 174, 0, 1, 3, 2),
    (12, 182, 0, 1, 3, 2),
    (13, 190, 0, 1, 3, 2),
    (14, 198, 0, 1, 3, 2),
]

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
}


def quantized(value: float) -> float:
    return round(value, 6)


def decode_buffer(gltf: dict) -> bytes:
    buffers = gltf.get("buffers", [])
    if len(buffers) != 1:
        raise ValueError(f"Expected exactly 1 buffer, found {len(buffers)}.")
    uri = buffers[0].get("uri", "")
    if not uri.startswith("data:"):
        raise ValueError("This script expects an embedded data URI buffer in source/model.gltf.")
    return base64.b64decode(uri.split(",", 1)[1])


def read_accessor(gltf: dict, blob: bytes, accessor_index: int) -> list[tuple[float, ...]]:
    accessor = gltf["accessors"][accessor_index]
    buffer_view = gltf["bufferViews"][accessor["bufferView"]]
    component_type = accessor["componentType"]
    component_count = _NUM_COMPONENTS[accessor["type"]]
    fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
    stride = buffer_view.get("byteStride", struct.calcsize(fmt))
    byte_offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)

    out: list[tuple[float, ...]] = []
    for index in range(accessor["count"]):
        start = byte_offset + (index * stride)
        out.append(struct.unpack_from(fmt, blob, start))
    return out


def primitive_vertices(gltf: dict, blob: bytes, primitive: dict) -> tuple[list[tuple[float, ...]], list[tuple[float, ...]]]:
    positions = read_accessor(gltf, blob, primitive["attributes"]["POSITION"])
    texcoords = read_accessor(gltf, blob, primitive["attributes"]["TEXCOORD_0"])

    if "indices" in primitive:
        indices = [int(value[0]) for value in read_accessor(gltf, blob, primitive["indices"])]
        positions = [positions[index] for index in indices]
        texcoords = [texcoords[index] for index in indices]

    return positions, texcoords


def primitive_face_rects(gltf: dict, blob: bytes, primitive: dict, image_width: int, image_height: int) -> dict[str, tuple[int, int, int, int]]:
    positions, texcoords = primitive_vertices(gltf, blob, primitive)

    xs = [position[0] for position in positions]
    ys = [position[1] for position in positions]
    zs = [position[2] for position in positions]
    local_min = (min(xs), min(ys), min(zs))

    faces: dict[str, list[tuple[float, float]]] = defaultdict(list)

    for index in range(0, len(positions) - 2, 3):
        tri_positions = positions[index:index + 3]
        tri_uvs = texcoords[index:index + 3]

        tri_x = {quantized(position[0]) for position in tri_positions}
        tri_y = {quantized(position[1]) for position in tri_positions}
        tri_z = {quantized(position[2]) for position in tri_positions}

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

        left = math.floor(min_u * image_width)
        right = math.ceil(max_u * image_width)
        top = math.floor((1.0 - max_v) * image_height)
        bottom = math.ceil((1.0 - min_v) * image_height)
        out[face_name] = (left, top, right, bottom)

    return out


def face_layout(origin_u: int, origin_v: int, size_x: int, size_y: int, size_z: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + size_z, size_z, size_y),
        "north": (origin_u + size_z, origin_v + size_z, size_x, size_y),
        "east": (origin_u + size_z + size_x, origin_v + size_z, size_z, size_y),
        "south": (origin_u + size_z + size_x + size_z, origin_v + size_z, size_x, size_y),
        "up": (origin_u + size_z, origin_v, size_x, size_z),
        "down": (origin_u + size_z + size_x, origin_v, size_x, size_z),
    }


def crop_face_or_transparent(image: Image.Image, rect: tuple[int, int, int, int] | None, width: int, height: int) -> Image.Image:
    if rect is None:
        return Image.new("RGBA", (width, height), (0, 0, 0, 0))

    left, top, right, bottom = rect
    if right <= left or bottom <= top:
        return Image.new("RGBA", (width, height), (0, 0, 0, 0))

    patch = image.crop((left, top, right, bottom))
    if patch.size != (width, height):
        patch = patch.resize((width, height), Image.NEAREST)
    return patch


def main() -> None:
    if not GLTF_PATH.exists():
        raise FileNotFoundError(f"Missing GLTF: {GLTF_PATH}")
    if not SOURCE_TEXTURE.exists():
        raise FileNotFoundError(f"Missing source texture: {SOURCE_TEXTURE}")

    gltf = json.loads(GLTF_PATH.read_text(encoding="utf-8"))
    blob = decode_buffer(gltf)
    source = Image.open(SOURCE_TEXTURE).convert("RGBA")

    atlas = Image.new("RGBA", (512, 16), (0, 0, 0, 0))

    for mesh_index, tex_u, tex_v, size_x, size_y, size_z in BOX_LAYOUTS:
        primitive = gltf["meshes"][mesh_index]["primitives"][0]
        rects = primitive_face_rects(gltf, blob, primitive, source.width, source.height)

        for face_name, (target_u, target_v, target_w, target_h) in face_layout(tex_u, tex_v, size_x, size_y, size_z).items():
            patch = crop_face_or_transparent(source, rects.get(face_name), target_w, target_h)
            atlas.paste(patch, (target_u, target_v))

    atlas.save(OUTPUT_TEXTURE)

    print(f"Wrote {OUTPUT_TEXTURE}")
    print("Note: degenerate side faces on the thin front claws are kept transparent instead of using an opaque fallback pixel.")


if __name__ == "__main__":
    main()