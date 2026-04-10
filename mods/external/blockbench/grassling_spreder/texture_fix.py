#!/usr/bin/env python3
from __future__ import annotations

import json
import math
from collections import defaultdict
from pathlib import Path

from PIL import Image


def quantized(value: float) -> float:
    return round(value, 6)


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


def io_bytes(blob: bytes):
    from io import BytesIO
    return BytesIO(blob)


class GltfReader:
    def __init__(self, gltf_path: Path) -> None:
        import base64
        import struct

        self._base64 = base64
        self._struct = struct
        self.gltf_path = gltf_path
        self.root = json.loads(gltf_path.read_text(encoding="utf-8"))
        self.buffers = []
        for entry in self.root.get("buffers", []):
            uri = entry.get("uri", "")
            if uri.startswith("data:"):
                self.buffers.append(base64.b64decode(uri.split(",", 1)[1]))
            else:
                self.buffers.append((gltf_path.parent / uri).read_bytes())

    def accessor(self, accessor_index: int) -> list[tuple[float, ...]]:
        accessor = self.root["accessors"][accessor_index]
        buffer_view = self.root["bufferViews"][accessor["bufferView"]]
        component_type = accessor["componentType"]
        component_count = _NUM_COMPONENTS[accessor["type"]]
        fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
        stride = buffer_view.get("byteStride", self._struct.calcsize(fmt))
        byte_offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
        raw = self.buffers[buffer_view["buffer"]]
        out: list[tuple[float, ...]] = []
        for idx in range(accessor["count"]):
            start = byte_offset + idx * stride
            out.append(self._struct.unpack_from(fmt, raw, start))
        return out


def primitive_vertices(reader: GltfReader, primitive: dict) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [tuple(map(float, xyz)) for xyz in reader.accessor(primitive["attributes"]["POSITION"])]
    texcoords = [tuple(map(float, uv)) for uv in reader.accessor(primitive["attributes"]["TEXCOORD_0"])]
    if "indices" in primitive:
        indices = [int(v[0]) for v in reader.accessor(primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]
    return positions, texcoords


def extract_cuboids(reader: GltfReader) -> list[dict]:
    cuboids: list[dict] = []
    for mesh in reader.root.get("meshes", []):
        for primitive in mesh.get("primitives", []):
            positions, texcoords = primitive_vertices(reader, primitive)
            if not positions:
                continue

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
                raise SystemExit("grassling_spreder texture_fix.py only supports the exact-cuboid case for this creature.")

            faces: dict[str, list[tuple[float, float]]] = defaultdict(list)
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

                faces[face].extend(tri_uvs)

            source_faces: dict[str, tuple[float, float, float, float]] = {}
            for face_name, uv_points in faces.items():
                us = [uv[0] for uv in uv_points]
                vs = [uv[1] for uv in uv_points]
                source_faces[face_name] = (min(us), min(vs), max(us), max(vs))

            cuboids.append({
                "local_min": local_min,
                "local_max": local_max,
                "source_faces": source_faces,
            })
    return cuboids


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def crop_face_from_sidecar(source: Image.Image, rect: tuple[float, float, float, float] | None) -> Image.Image:
    if rect is None:
        return Image.new("RGBA", (1, 1), (0, 0, 0, 0))

    min_u, min_v, max_u, max_v = rect
    width, height = source.size

    left = max(0, min(width - 1, math.floor(min_u * width)))
    right = max(left + 1, min(width, math.ceil(max_u * width)))

    # IMPORTANT:
    # The sidecar ./textures/gltf_embedded_0.png is vertically flipped relative
    # to the embedded image inside the GLTF. The GLTF UVs for this creature line
    # up correctly against the sidecar only when we keep the converter's V-flip
    # logic here.
    top = max(0, min(height - 1, math.floor((1.0 - max_v) * height)))
    bottom = max(top + 1, min(height, math.ceil((1.0 - min_v) * height)))

    return source.crop((left, top, right, bottom))


def choose_scale_factor(cuboids: list[dict]) -> float:
    sizes = []
    for cuboid in cuboids:
        local_min = cuboid["local_min"]
        local_max = cuboid["local_max"]
        dx = local_max[0] - local_min[0]
        dy = local_max[1] - local_min[1]
        dz = local_max[2] - local_min[2]
        sizes.append(max(dx, dy, dz))
    if not sizes:
        return 16.0
    sizes.sort()
    median = sizes[len(sizes) // 2]
    return 16.0 if median <= 4.0 else 1.0


def allocate_tiles(cuboids: list[dict], scale_factor: float) -> tuple[list[tuple[dict, tuple[int, int, int, int]]], int, int]:
    shelf_u = 0
    shelf_v = 0
    shelf_h = 0
    atlas_w = 512
    provisional_h = 0
    allocated: list[tuple[dict, tuple[int, int, int, int]]] = []

    for cuboid in cuboids:
        local_min = cuboid["local_min"]
        local_max = cuboid["local_max"]
        dx = max(1, int(round((local_max[0] - local_min[0]) * scale_factor)))
        dy = max(1, int(round((local_max[1] - local_min[1]) * scale_factor)))
        dz = max(1, int(round((local_max[2] - local_min[2]) * scale_factor)))
        tile_w = 2 * (dx + dz)
        tile_h = dy + dz

        if shelf_u + tile_w > atlas_w:
            shelf_u = 0
            shelf_v += shelf_h + 2
            shelf_h = 0

        allocated.append((cuboid, (shelf_u, shelf_v, dx, dy, dz)))
        shelf_u += tile_w + 2
        shelf_h = max(shelf_h, tile_h)
        provisional_h = max(provisional_h, shelf_v + tile_h)

    atlas_h = 1
    while atlas_h < provisional_h + 2:
        atlas_h *= 2
    return allocated, atlas_w, max(16, atlas_h)


def main() -> None:
    base = Path(__file__).resolve().parent
    gltf_candidates = sorted((base / "source").glob("*.gltf"))
    if not gltf_candidates:
        raise SystemExit("No .gltf file found under ./source/")
    gltf_path = gltf_candidates[0]

    texture_path = base / "textures" / "gltf_embedded_0.png"
    if not texture_path.exists():
        raise SystemExit("Expected ./textures/gltf_embedded_0.png")

    reader = GltfReader(gltf_path)
    cuboids = extract_cuboids(reader)
    scale_factor = choose_scale_factor(cuboids)
    allocated, atlas_w, atlas_h = allocate_tiles(cuboids, scale_factor)

    source = Image.open(texture_path).convert("RGBA")
    atlas = Image.new("RGBA", (atlas_w, atlas_h), (0, 0, 0, 0))

    for cuboid, (origin_u, origin_v, dx, dy, dz) in allocated:
        layout = face_layout(origin_u, origin_v, dx, dy, dz)
        for face_name, (target_u, target_v, target_w, target_h) in layout.items():
            patch = crop_face_from_sidecar(source, cuboid["source_faces"].get(face_name))
            patch = patch.resize((max(1, target_w), max(1, target_h)), Image.NEAREST)
            atlas.paste(patch, (target_u, target_v))

    out_path = base / "model.png"
    atlas.save(out_path)
    print(f"Wrote {out_path} ({atlas_w}x{atlas_h})")


if __name__ == "__main__":
    main()