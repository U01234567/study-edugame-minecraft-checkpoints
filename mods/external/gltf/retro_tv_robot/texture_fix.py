#!/usr/bin/env python3
from __future__ import annotations

import base64
import json
import struct
from pathlib import Path

from PIL import Image

GLTF_PATH = Path("./source/model.gltf")
SOURCE_TEXTURE_PATH = Path("./textures/gltf_embedded_0.png")
OUTPUT_TEXTURE_PATH = Path("./model.png")

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


def quantized(value: float) -> float:
    return round(value, 6)


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
    component_count = _NUM_COMPONENTS[entry["type"]]
    fmt = "<" + (_COMPONENT_FORMATS[entry["componentType"]] * component_count)
    stride = buffer_view.get("byteStride", struct.calcsize(fmt))
    byte_offset = buffer_view.get("byteOffset", 0) + entry.get("byteOffset", 0)
    raw = buffers[buffer_view["buffer"]]

    out: list[tuple[float, ...]] = []
    for index in range(entry["count"]):
        start = byte_offset + (index * stride)
        out.append(struct.unpack_from(fmt, raw, start))
    return out


def primitive_vertices(
    root: dict,
    buffers: list[bytes],
    primitive: dict,
) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [tuple(map(float, xyz)) for xyz in accessor(root, buffers, primitive["attributes"]["POSITION"])]

    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [tuple(map(float, uv)) for uv in accessor(root, buffers, primitive["attributes"]["TEXCOORD_0"])]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(value[0]) for value in accessor(root, buffers, primitive["indices"])]
        positions = [positions[index] for index in indices]
        texcoords = [texcoords[index] for index in indices]

    return positions, texcoords


def half_scale_face_rect(rect: tuple[float, float, float, float]) -> tuple[int, int, int, int]:
    min_u, min_v, max_u, max_v = rect
    left = round(min_u * 128 / 2)
    top = round((1.0 - max_v) * 128 / 2)
    width = round((max_u - min_u) * 128 / 2)
    height = round((max_v - min_v) * 128 / 2)
    return left, top, width, height


def collect_boxes(root: dict, buffers: list[bytes]) -> list[dict]:
    boxes: list[dict] = []

    for mesh in root.get("meshes", []):
        primitives = mesh.get("primitives", [])
        if not primitives:
            continue

        all_positions: list[tuple[float, float, float]] = []
        primitive_cache: list[tuple[list[tuple[float, float, float]], list[tuple[float, float]]]] = []

        for primitive in primitives:
            positions, texcoords = primitive_vertices(root, buffers, primitive)
            primitive_cache.append((positions, texcoords))
            all_positions.extend(positions)

        xs = [position[0] for position in all_positions]
        ys = [position[1] for position in all_positions]
        zs = [position[2] for position in all_positions]
        mesh_min = (min(xs), min(ys), min(zs))
        mesh_max = (max(xs), max(ys), max(zs))

        face_rects: dict[str, tuple[int, int, int, int]] = {}

        for positions, texcoords in primitive_cache:
            constant_x = {quantized(position[0]) for position in positions}
            constant_y = {quantized(position[1]) for position in positions}
            constant_z = {quantized(position[2]) for position in positions}

            if len(constant_x) == 1:
                value = next(iter(constant_x))
                face = "west" if abs(value - mesh_min[0]) < 1.0e-5 else "east"
            elif len(constant_y) == 1:
                value = next(iter(constant_y))
                face = "down" if abs(value - mesh_min[1]) < 1.0e-5 else "up"
            elif len(constant_z) == 1:
                value = next(iter(constant_z))
                face = "north" if abs(value - mesh_min[2]) < 1.0e-5 else "south"
            else:
                raise ValueError("Encountered a primitive that is not a single cuboid face.")

            us = [uv[0] for uv in texcoords]
            vs = [uv[1] for uv in texcoords]
            face_rects[face] = half_scale_face_rect((min(us), min(vs), max(us), max(vs)))

        required_faces = {"west", "north", "east", "south", "up", "down"}
        if set(face_rects) != required_faces:
            missing = sorted(required_faces.difference(face_rects))
            raise ValueError(f"Mesh is missing expected cuboid faces: {missing}")

        boxes.append(
            {
                "swap_west_east": face_rects["west"][0] > face_rects["east"][0],
                "west": face_rects["west"],
                "east": face_rects["east"],
            }
        )

    return boxes


def swap_patch(image: Image.Image, left_rect: tuple[int, int, int, int], right_rect: tuple[int, int, int, int]) -> None:
    left_x, left_y, left_w, left_h = left_rect
    right_x, right_y, right_w, right_h = right_rect

    if left_w <= 0 or left_h <= 0 or right_w <= 0 or right_h <= 0:
        return

    left_patch = image.crop((left_x, left_y, left_x + left_w, left_y + left_h)).copy()
    right_patch = image.crop((right_x, right_y, right_x + right_w, right_y + right_h)).copy()

    image.paste(right_patch, (left_x, left_y))
    image.paste(left_patch, (right_x, right_y))


def main() -> None:
    root, buffers = load_gltf(GLTF_PATH)
    source = Image.open(SOURCE_TEXTURE_PATH).convert("RGBA")

    if source.size != (128, 128):
        raise ValueError(f"Expected 128x128 source texture, got {source.size[0]}x{source.size[1]}")

    output = source.resize((64, 64), Image.NEAREST)
    boxes = collect_boxes(root, buffers)

    for box in boxes:
        if box["swap_west_east"]:
            swap_patch(output, box["west"], box["east"])

    output.save(OUTPUT_TEXTURE_PATH)
    print(f"Wrote {OUTPUT_TEXTURE_PATH} ({output.size[0]}x{output.size[1]})")


if __name__ == "__main__":
    main()