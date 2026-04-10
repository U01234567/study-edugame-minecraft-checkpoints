#!/usr/bin/env python3
"""
Per-creature texture bake for killer_crab.

Run this from the creature folder root after you have copied:
  ./source/model.gltf
  ./textures/gltf_embedded_0.png ... ./textures/gltf_embedded_11.png

Output:
  ./model.png

Why this exists:
- killer_crab uses 12 source textures, so it cannot be Class A under the current
  one-runtime-texture blockbench contract.
- the previous bake path for this creature double-flipped the sidecar PNGs and
  trimmed each face crop to alpha bounds, which collapses the intended UV layout
  into skinny strips/white patches.
- this replacement keeps the exact UV rectangles, preserves transparency, and
  auto-selects the per-image vertical orientation that best matches the face UVs.
"""

from __future__ import annotations

import base64
import json
import math
import struct
from collections import defaultdict
from pathlib import Path

try:
    from PIL import Image, ImageOps
except ImportError as exc:
    raise SystemExit("Pillow is required: pip install pillow") from exc


ROOT = Path(__file__).resolve().parent
GLTF_PATH = ROOT / "source" / "model.gltf"
TEXTURE_DIR = ROOT / "textures"
OUTPUT_PATH = ROOT / "model.png"
ATLAS_WIDTH = 512
SCALE_FACTOR = 16.0
EXPECTED_CUBE_COUNT = 45

COMPONENT_FORMATS = {
    5120: "b",
    5121: "B",
    5122: "h",
    5123: "H",
    5125: "I",
    5126: "f",
}

NUM_COMPONENTS = {
    "SCALAR": 1,
    "VEC2": 2,
    "VEC3": 3,
    "VEC4": 4,
    "MAT2": 4,
    "MAT3": 9,
    "MAT4": 16,
}


class GltfReader:
    def __init__(self, path: Path):
        self.path = path
        self.root = json.loads(path.read_text(encoding="utf-8"))
        self.buffers = self._load_buffers()

    def _load_buffers(self) -> list[bytes]:
        out: list[bytes] = []
        for entry in self.root.get("buffers", []):
            uri = entry.get("uri", "")
            if uri.startswith("data:"):
                out.append(base64.b64decode(uri.split(",", 1)[1]))
            else:
                out.append((self.path.parent / uri).read_bytes())
        return out

    def accessor(self, accessor_index: int):
        accessor = self.root["accessors"][accessor_index]
        buffer_view = self.root["bufferViews"][accessor["bufferView"]]
        component_type = accessor["componentType"]
        component_count = NUM_COMPONENTS[accessor["type"]]
        fmt = "<" + (COMPONENT_FORMATS[component_type] * component_count)
        stride = buffer_view.get("byteStride", struct.calcsize(fmt))
        byte_offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
        raw = self.buffers[buffer_view["buffer"]]
        out = []
        for idx in range(accessor["count"]):
            start = byte_offset + idx * stride
            out.append(struct.unpack_from(fmt, raw, start))
        return out

    def primitive_texture_source_index(self, primitive: dict) -> int:
        material_index = primitive.get("material")
        if material_index is None:
            return 0
        material = self.root["materials"][material_index]
        texture_index = material["pbrMetallicRoughness"]["baseColorTexture"]["index"]
        return self.root["textures"][texture_index]["source"]

    def image_by_index(self, image_index: int) -> Image.Image:
        path = TEXTURE_DIR / f"gltf_embedded_{image_index}.png"
        if not path.exists():
            raise FileNotFoundError(f"Missing texture sidecar: {path}")
        return Image.open(path).convert("RGBA")


def quantized(value: float) -> float:
    return round(value, 6)


def primitive_vertices(reader: GltfReader, primitive: dict):
    positions = [tuple(map(float, xyz)) for xyz in reader.accessor(primitive["attributes"]["POSITION"])]
    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [tuple(map(float, uv)) for uv in reader.accessor(primitive["attributes"]["TEXCOORD_0"])]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(v[0]) for v in reader.accessor(primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def try_extract_cuboid(reader: GltfReader, primitive: dict):
    positions, texcoords = primitive_vertices(reader, primitive)
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
        raise RuntimeError(
            "killer_crab unexpectedly contains a non-cuboid primitive; this Class B script assumes exact cuboids/planes only."
        )

    faces: dict[str, list[tuple[float, float]]] = defaultdict(list)
    for idx in range(0, len(positions) - 2, 3):
        tri_positions = positions[idx : idx + 3]
        tri_uvs = texcoords[idx : idx + 3]

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

    source_faces: dict[str, tuple[float, float, float, float]] = {}
    for face_name, uv_points in faces.items():
        us = [uv[0] for uv in uv_points]
        vs = [uv[1] for uv in uv_points]
        source_faces[face_name] = (min(us), min(vs), max(us), max(vs))

    return {
        "local_min": local_min,
        "local_max": local_max,
        "source_faces": source_faces,
        "image_index": reader.primitive_texture_source_index(primitive),
    }


def crop_uv_rect(image: Image.Image, rect: tuple[float, float, float, float] | None) -> Image.Image:
    if rect is None:
        return Image.new("RGBA", (1, 1), (0, 0, 0, 0))

    min_u, min_v, max_u, max_v = rect
    width, height = image.size

    left = max(0, min(width - 1, math.floor(min_u * width)))
    right = max(left + 1, min(width, math.ceil(max_u * width)))
    top = max(0, min(height - 1, math.floor((1.0 - max_v) * height)))
    bottom = max(top + 1, min(height, math.ceil((1.0 - min_v) * height)))

    return image.crop((left, top, right, bottom))


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int):
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def collect_cubes(reader: GltfReader):
    mesh_cache = {}
    for mesh_index, mesh in enumerate(reader.root.get("meshes", [])):
        cube_list = []
        for primitive in mesh.get("primitives", []):
            cube = try_extract_cuboid(reader, primitive)
            if cube is not None:
                cube_list.append(cube)
        mesh_cache[mesh_index] = cube_list

    nodes = reader.root["nodes"]
    scene_index = reader.root.get("scene", 0)
    top_level = list(reader.root["scenes"][scene_index]["nodes"])
    ordered = []

    def visit(node_index: int):
        node = nodes[node_index]
        ordered.extend(mesh_cache.get(node.get("mesh", -1), []))
        for child_index in node.get("children", []):
            visit(child_index)

    for node_index in top_level:
        visit(node_index)

    return ordered


def choose_image_orientations(reader: GltfReader, cubes: list[dict]) -> dict[int, Image.Image]:
    faces_per_image: dict[int, list[tuple[float, float, float, float]]] = defaultdict(list)
    for cube in cubes:
        for rect in cube["source_faces"].values():
            faces_per_image[cube["image_index"]].append(rect)

    selected: dict[int, Image.Image] = {}
    for image_index, rects in faces_per_image.items():
        raw = reader.image_by_index(image_index)
        flipped = ImageOps.flip(raw)

        def score(candidate: Image.Image) -> int:
            total = 0
            for rect in rects:
                patch = crop_uv_rect(candidate, rect)
                total += sum(patch.getchannel("A").getdata())
            return total

        raw_score = score(raw)
        flipped_score = score(flipped)
        selected[image_index] = raw if raw_score >= flipped_score else flipped
        chosen = "raw-sidecar" if raw_score >= flipped_score else "vertically-flipped"
        print(f"image {image_index}: {chosen} (raw={raw_score}, flipped={flipped_score})")

    return selected


def bake_atlas(cubes: list[dict], image_lookup: dict[int, Image.Image]) -> Image.Image:
    entries = []
    shelf_u = 0
    shelf_v = 0
    shelf_h = 0
    provisional_h = 0

    for cube in cubes:
        dx = max(1, int(round((cube["local_max"][0] - cube["local_min"][0]) * SCALE_FACTOR)))
        dy = max(1, int(round((cube["local_max"][1] - cube["local_min"][1]) * SCALE_FACTOR)))
        dz = max(1, int(round((cube["local_max"][2] - cube["local_min"][2]) * SCALE_FACTOR)))

        tile_w = 2 * (dx + dz)
        tile_h = dy + dz

        if shelf_u + tile_w > ATLAS_WIDTH:
            shelf_u = 0
            shelf_v += shelf_h + 2
            shelf_h = 0

        entries.append((cube, shelf_u, shelf_v, dx, dy, dz))
        shelf_u += tile_w + 2
        shelf_h = max(shelf_h, tile_h)
        provisional_h = max(provisional_h, shelf_v + tile_h)

    atlas_h = 1
    while atlas_h < provisional_h + 2:
        atlas_h *= 2

    atlas = Image.new("RGBA", (ATLAS_WIDTH, max(16, atlas_h)), (0, 0, 0, 0))
    for cube, origin_u, origin_v, dx, dy, dz in entries:
        source_image = image_lookup[cube["image_index"]]
        for face_name, (target_u, target_v, target_w, target_h) in face_layout(origin_u, origin_v, dx, dy, dz).items():
            patch = crop_uv_rect(source_image, cube["source_faces"].get(face_name))
            patch = patch.resize((max(1, target_w), max(1, target_h)), Image.NEAREST)
            atlas.paste(patch, (target_u, target_v))

    return atlas


def main():
    reader = GltfReader(GLTF_PATH)
    cubes = collect_cubes(reader)
    if len(cubes) != EXPECTED_CUBE_COUNT:
        raise RuntimeError(f"Expected {EXPECTED_CUBE_COUNT} cuboids for killer_crab, found {len(cubes)}.")

    image_lookup = choose_image_orientations(reader, cubes)
    atlas = bake_atlas(cubes, image_lookup)
    atlas.save(OUTPUT_PATH)
    print(f"Wrote {OUTPUT_PATH} ({atlas.size[0]}x{atlas.size[1]})")


if __name__ == "__main__":
    main()