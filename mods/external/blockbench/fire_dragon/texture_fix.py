#!/usr/bin/env python3
"""
Per-creature texture rebake for fire_dragon.

Builds ./model.png from:
  - ./source/model.gltf
  - ./textures/gltf_embedded_0.png

Why this exists:
  * fire_dragon is fully exact-cuboid, so the existing model.java is usable.
  * the source texture file beside the GLTF is vertically flipped relative to the
    image embedded inside the GLTF, so naive cropping produces wrong face art.
  * we keep the current packed 512-wide atlas shape expected by model.java and
    only rebuild the texture atlas deterministically for this creature.
"""

from __future__ import annotations

import base64
import io
import json
import math
import struct
from pathlib import Path

from PIL import Image, ImageChops

SOURCE_GLTF = Path("./source/model.gltf")
SOURCE_TEXTURE = Path("./textures/gltf_embedded_0.png")
OUTPUT_TEXTURE = Path("./model.png")
ATLAS_WIDTH = 512
SCALE_FACTOR = 16.0

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


def load_gltf(gltf_path: Path) -> tuple[dict, list[bytes]]:
    root = json.loads(gltf_path.read_text(encoding="utf-8"))
    buffers: list[bytes] = []
    for entry in root.get("buffers", []):
        uri = entry.get("uri", "")
        if uri.startswith("data:"):
            buffers.append(base64.b64decode(uri.split(",", 1)[1]))
        else:
            buffers.append((gltf_path.parent / uri).read_bytes())
    return root, buffers


def read_accessor(root: dict, buffers: list[bytes], accessor_index: int) -> list[tuple[float, ...]]:
    accessor = root["accessors"][accessor_index]
    buffer_view = root["bufferViews"][accessor["bufferView"]]
    component_type = accessor["componentType"]
    component_count = _NUM_COMPONENTS[accessor["type"]]
    fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
    stride = buffer_view.get("byteStride", struct.calcsize(fmt))
    byte_offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
    raw = buffers[buffer_view["buffer"]]

    out: list[tuple[float, ...]] = []
    for idx in range(accessor["count"]):
        start = byte_offset + idx * stride
        out.append(struct.unpack_from(fmt, raw, start))
    return out


def primitive_vertices(
    root: dict,
    buffers: list[bytes],
    primitive: dict,
) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [
        tuple(map(float, xyz))
        for xyz in read_accessor(root, buffers, primitive["attributes"]["POSITION"])
    ]
    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [
            tuple(map(float, uv))
            for uv in read_accessor(root, buffers, primitive["attributes"]["TEXCOORD_0"])
        ]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(v[0]) for v in read_accessor(root, buffers, primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def extract_cuboid(root: dict, buffers: list[bytes], primitive: dict) -> dict | None:
    positions, texcoords = primitive_vertices(root, buffers, primitive)
    if not positions:
        return None

    xs = [p[0] for p in positions]
    ys = [p[1] for p in positions]
    zs = [p[2] for p in positions]
    local_min = (min(xs), min(ys), min(zs))
    local_max = (max(xs), max(ys), max(zs))

    unique_x = {quantized(v) for v in xs}
    unique_y = {quantized(v) for v in ys}
    unique_z = {quantized(v) for v in zs}
    exact_cuboid = len(unique_x) <= 2 and len(unique_y) <= 2 and len(unique_z) <= 2
    if not exact_cuboid:
        return None

    faces: dict[str, list[tuple[float, float]]] = {}
    for idx in range(0, len(positions) - 2, 3):
        tri_positions = positions[idx:idx + 3]
        tri_uvs = texcoords[idx:idx + 3]

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

        faces.setdefault(face, []).extend(tri_uvs)

    source_faces: dict[str, tuple[float, float, float, float]] = {}
    for face_name, uv_points in faces.items():
        us = [uv[0] for uv in uv_points]
        vs = [uv[1] for uv in uv_points]
        source_faces[face_name] = (min(us), min(vs), max(us), max(vs))

    if len(source_faces) != 6:
        raise SystemExit(
            "fire_dragon texture_fix.py expects six UV-addressable cuboid faces for every primitive."
        )

    return {
        "local_min": local_min,
        "local_max": local_max,
        "source_faces": source_faces,
    }


def load_source_texture(root: dict) -> Image.Image:
    if not SOURCE_TEXTURE.exists():
        raise SystemExit(f"Missing {SOURCE_TEXTURE}")

    sidecar = Image.open(SOURCE_TEXTURE).convert("RGBA")

    embedded_image: Image.Image | None = None
    images = root.get("images", [])
    if images:
        uri = images[0].get("uri", "")
        if uri.startswith("data:"):
            embedded_bytes = base64.b64decode(uri.split(",", 1)[1])
            embedded_image = Image.open(io.BytesIO(embedded_bytes)).convert("RGBA")

    flipped_sidecar = sidecar.transpose(Image.FLIP_TOP_BOTTOM)

    if embedded_image is None:
        return flipped_sidecar

    if sidecar.size != embedded_image.size:
        raise SystemExit(
            "fire_dragon texture_fix.py found mismatched embedded and sidecar texture sizes."
        )

    if ImageChops.difference(flipped_sidecar, embedded_image).getbbox() is None:
        return embedded_image

    if ImageChops.difference(sidecar, embedded_image).getbbox() is None:
        return embedded_image

    raise SystemExit(
        "fire_dragon texture_fix.py could not reconcile ./textures/gltf_embedded_0.png with the GLTF-embedded image."
    )


def crop_face(image: Image.Image, rect: tuple[float, float, float, float]) -> Image.Image:
    min_u, min_v, max_u, max_v = rect
    width, height = image.size

    left = max(0, min(width - 1, math.floor(min_u * width)))
    right = max(left + 1, min(width, math.ceil(max_u * width)))
    top = max(0, min(height - 1, math.floor((1.0 - max_v) * height)))
    bottom = max(top + 1, min(height, math.ceil((1.0 - min_v) * height)))

    return image.crop((left, top, right, bottom))


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def main() -> None:
    if not SOURCE_GLTF.exists():
        raise SystemExit(f"Missing {SOURCE_GLTF}")

    root, buffers = load_gltf(SOURCE_GLTF)
    texture = load_source_texture(root)

    mesh_cache: dict[int, list[dict]] = {}
    for mesh_index, mesh in enumerate(root.get("meshes", [])):
        cubes: list[dict] = []
        for primitive in mesh.get("primitives", []):
            cube = extract_cuboid(root, buffers, primitive)
            if cube is None:
                raise SystemExit(
                    "fire_dragon texture_fix.py only supports the exact-cuboid case for this creature."
                )
            cubes.append(cube)
        mesh_cache[mesh_index] = cubes

    nodes = root.get("nodes", [])
    children = {child for node in nodes for child in node.get("children", [])}
    top_level = [index for index in range(len(nodes)) if index not in children]

    atlas_tiles: list[tuple[dict, int, int, int]] = []

    def visit(node_index: int) -> None:
        node = nodes[node_index]
        for cube in mesh_cache.get(node.get("mesh", -1), []):
            dx = max(1, int(round((cube["local_max"][0] - cube["local_min"][0]) * SCALE_FACTOR)))
            dy = max(1, int(round((cube["local_max"][1] - cube["local_min"][1]) * SCALE_FACTOR)))
            dz = max(1, int(round((cube["local_max"][2] - cube["local_min"][2]) * SCALE_FACTOR)))
            atlas_tiles.append((cube, dx, dy, dz))
        for child_index in node.get("children", []):
            visit(child_index)

    for node_index in top_level:
        visit(node_index)

    shelf_u = 0
    shelf_v = 0
    shelf_h = 0
    provisional_h = 0
    placements: list[tuple[dict, int, int, int, int, int]] = []

    for cube, dx, dy, dz in atlas_tiles:
        tile_w = 2 * (dx + dz)
        tile_h = dy + dz

        if shelf_u + tile_w > ATLAS_WIDTH:
            shelf_u = 0
            shelf_v += shelf_h + 2
            shelf_h = 0

        placements.append((cube, shelf_u, shelf_v, dx, dy, dz))
        shelf_u += tile_w + 2
        shelf_h = max(shelf_h, tile_h)
        provisional_h = max(provisional_h, shelf_v + tile_h)

    atlas_h = 1
    while atlas_h < provisional_h + 2:
        atlas_h *= 2

    atlas = Image.new("RGBA", (ATLAS_WIDTH, max(16, atlas_h)), (0, 0, 0, 0))

    for cube, origin_u, origin_v, dx, dy, dz in placements:
        layout = face_layout(origin_u, origin_v, dx, dy, dz)
        for face_name, (target_u, target_v, target_w, target_h) in layout.items():
            patch = crop_face(texture, cube["source_faces"][face_name])
            patch = patch.resize((max(1, target_w), max(1, target_h)), Image.NEAREST)
            atlas.paste(patch, (target_u, target_v))

    atlas.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE} ({atlas.width}x{atlas.height}) from {SOURCE_TEXTURE}")


if __name__ == "__main__":
    main()