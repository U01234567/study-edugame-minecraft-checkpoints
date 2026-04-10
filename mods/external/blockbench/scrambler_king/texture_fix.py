#!/usr/bin/env python3
from __future__ import annotations

import base64
import json
import struct
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from PIL import Image

ROOT = Path(__file__).resolve().parent
GLTF_PATH = ROOT / "source" / "model.gltf"
SOURCE_TEXTURE_PATH = ROOT / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE_PATH = ROOT / "model.png"

# Ordered to match the 26 mesh primitives in source/model.gltf and the 26 cuboids
# already present in the existing model.java that you are keeping.
CUBES = [
    {"u": 116, "v": 69, "dx": 12, "dy": 7, "dz": 8},
    {"u": 0, "v": 118, "dx": 10, "dy": 7, "dz": 8},
    {"u": 104, "v": 102, "dx": 8, "dy": 9, "dz": 10},
    {"u": 116, "v": 54, "dx": 12, "dy": 7, "dz": 8},
    {"u": 0, "v": 118, "dx": 10, "dy": 7, "dz": 8},
    {"u": 104, "v": 102, "dx": 8, "dy": 9, "dz": 10},
    {"u": 116, "v": 69, "dx": 12, "dy": 7, "dz": 8},
    {"u": 0, "v": 118, "dx": 10, "dy": 7, "dz": 8},
    {"u": 104, "v": 102, "dx": 8, "dy": 9, "dz": 10},
    {"u": 116, "v": 54, "dx": 12, "dy": 7, "dz": 8},
    {"u": 0, "v": 118, "dx": 10, "dy": 7, "dz": 8},
    {"u": 104, "v": 102, "dx": 8, "dy": 9, "dz": 10},
    {"u": 0, "v": 0, "dx": 26, "dy": 25, "dz": 21},
    {"u": 94, "v": 0, "dx": 24, "dy": 23, "dz": 5},
    {"u": 58, "v": 58, "dx": 13, "dy": 12, "dz": 32},
    {"u": 64, "v": 102, "dx": 13, "dy": 12, "dz": 7},
    {"u": 0, "v": 46, "dx": 12, "dy": 0, "dz": 8},
    {"u": 0, "v": 46, "dx": 13, "dy": 12, "dz": 32},
    {"u": 0, "v": 90, "dx": 19, "dy": 15, "dz": 13},
    {"u": 0, "v": 46, "dx": 12, "dy": 0, "dz": 8},
    {"u": 58, "v": 48, "dx": 8, "dy": 9, "dz": 6},
    {"u": 0, "v": 54, "dx": 8, "dy": 9, "dz": 6},
    {"u": 85, "v": 37, "dx": 12, "dy": 8, "dz": 9},
    {"u": 58, "v": 48, "dx": 8, "dy": 9, "dz": 6},
    {"u": 0, "v": 54, "dx": 8, "dy": 9, "dz": 6},
    {"u": 85, "v": 37, "dx": 12, "dy": 8, "dz": 9},
]

_COMPONENT_FORMAT = {
    5120: "b",   # BYTE
    5121: "B",   # UNSIGNED_BYTE
    5122: "h",   # SHORT
    5123: "H",   # UNSIGNED_SHORT
    5125: "I",   # UNSIGNED_INT
    5126: "f",   # FLOAT
}

_TYPE_COMPONENTS = {
    "SCALAR": 1,
    "VEC2": 2,
    "VEC3": 3,
    "VEC4": 4,
}


@dataclass
class PrimitiveCube:
    dx: int
    dy: int
    dz: int
    faces: dict[str, tuple[float, float, float, float]]  # min_u, min_v, max_u, max_v


def _quantized(value: float) -> int:
    return int(round(value * 1_000_000.0))


def _load_gltf(path: Path) -> tuple[dict[str, Any], list[bytes]]:
    root = json.loads(path.read_text(encoding="utf-8"))
    buffers: list[bytes] = []

    for buffer_def in root.get("buffers", []):
        uri = buffer_def.get("uri")
        if uri is None:
            raise ValueError("This texture fixer expects explicit buffer URIs.")
        if uri.startswith("data:"):
            _, encoded = uri.split(",", 1)
            buffers.append(base64.b64decode(encoded))
        else:
            buffers.append((path.parent / uri).read_bytes())

    return root, buffers


def _read_accessor(root: dict[str, Any], buffers: list[bytes], accessor_index: int) -> list[tuple[float, ...]]:
    accessor = root["accessors"][accessor_index]
    buffer_view = root["bufferViews"][accessor["bufferView"]]
    raw = buffers[buffer_view["buffer"]]

    fmt_char = _COMPONENT_FORMAT[accessor["componentType"]]
    component_count = _TYPE_COMPONENTS[accessor["type"]]
    component_size = struct.calcsize("<" + fmt_char)
    stride = buffer_view.get("byteStride", component_count * component_size)
    offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
    total_size = component_count * component_size

    values: list[tuple[float, ...]] = []
    for index in range(accessor["count"]):
        start = offset + index * stride
        chunk = raw[start:start + total_size]
        unpacked = struct.unpack("<" + fmt_char * component_count, chunk)
        values.append(tuple(float(v) for v in unpacked))
    return values


def _primitive_vertices(
    root: dict[str, Any],
    buffers: list[bytes],
    primitive: dict[str, Any],
) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    attributes = primitive["attributes"]
    positions = [tuple(v) for v in _read_accessor(root, buffers, attributes["POSITION"])]
    texcoords = [tuple(v) for v in _read_accessor(root, buffers, attributes["TEXCOORD_0"])]

    if "indices" in primitive:
        indices = [int(v[0]) for v in _read_accessor(root, buffers, primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def _extract_cube(root: dict[str, Any], buffers: list[bytes], primitive: dict[str, Any]) -> PrimitiveCube:
    positions, texcoords = _primitive_vertices(root, buffers, primitive)

    xs = [p[0] for p in positions]
    ys = [p[1] for p in positions]
    zs = [p[2] for p in positions]

    local_min = (min(xs), min(ys), min(zs))
    local_max = (max(xs), max(ys), max(zs))

    unique_x = {_quantized(v) for v in xs}
    unique_y = {_quantized(v) for v in ys}
    unique_z = {_quantized(v) for v in zs}
    if len(unique_x) > 2 or len(unique_y) > 2 or len(unique_z) > 2:
        raise ValueError("scrambler_king texture_fix.py only supports exact cuboids.")

    face_uvs: dict[str, list[tuple[float, float]]] = {
        "west": [],
        "east": [],
        "north": [],
        "south": [],
        "up": [],
        "down": [],
    }

    for tri_index in range(0, len(positions), 3):
        tri_positions = positions[tri_index:tri_index + 3]
        tri_uvs = texcoords[tri_index:tri_index + 3]
        tri_x = {_quantized(p[0]) for p in tri_positions}
        tri_y = {_quantized(p[1]) for p in tri_positions}
        tri_z = {_quantized(p[2]) for p in tri_positions}

        if len(tri_x) == 1:
            face_name = "west" if next(iter(tri_x)) == _quantized(local_min[0]) else "east"
        elif len(tri_y) == 1:
            face_name = "down" if next(iter(tri_y)) == _quantized(local_min[1]) else "up"
        elif len(tri_z) == 1:
            face_name = "north" if next(iter(tri_z)) == _quantized(local_min[2]) else "south"
        else:
            continue

        face_uvs[face_name].extend(tri_uvs)

    faces: dict[str, tuple[float, float, float, float]] = {}
    for face_name, uv_points in face_uvs.items():
        if not uv_points:
            raise ValueError(f"Missing UVs for face {face_name}.")
        us = [uv[0] for uv in uv_points]
        vs = [uv[1] for uv in uv_points]
        faces[face_name] = (min(us), min(vs), max(us), max(vs))

    return PrimitiveCube(
        dx=int(round((local_max[0] - local_min[0]) * 16.0)),
        dy=int(round((local_max[1] - local_min[1]) * 16.0)),
        dz=int(round((local_max[2] - local_min[2]) * 16.0)),
        faces=faces,
    )


def _face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def _crop_face(source: Image.Image, rect: tuple[float, float, float, float]) -> Image.Image:
    min_u, min_v, max_u, max_v = rect

    # The sidecar PNG in ./textures/ is vertically flipped relative to the UV
    # space stored inside model.gltf. Convert from GLTF UV space to PNG pixels here.
    left = int(round(min_u * source.width))
    right = int(round(max_u * source.width))
    top = int(round((1.0 - max_v) * source.height))
    bottom = int(round((1.0 - min_v) * source.height))

    return source.crop((left, top, right, bottom))


def main() -> None:
    if not GLTF_PATH.exists():
        raise SystemExit(f"Missing GLTF: {GLTF_PATH}")
    if not SOURCE_TEXTURE_PATH.exists():
        raise SystemExit(f"Missing source texture: {SOURCE_TEXTURE_PATH}")

    source = Image.open(SOURCE_TEXTURE_PATH).convert("RGBA")
    if source.size != (256, 256):
        raise SystemExit(f"Expected a 256x256 source texture, got {source.size}.")

    root, buffers = _load_gltf(GLTF_PATH)

    extracted_cubes: list[PrimitiveCube] = []
    for mesh in root.get("meshes", []):
        for primitive in mesh.get("primitives", []):
            extracted_cubes.append(_extract_cube(root, buffers, primitive))

    if len(extracted_cubes) != len(CUBES):
        raise SystemExit(f"Expected {len(CUBES)} cuboids, found {len(extracted_cubes)}.")

    atlas = Image.new("RGBA", source.size, (0, 0, 0, 0))

    for index, (spec, cube) in enumerate(zip(CUBES, extracted_cubes)):
        expected_size = (spec["dx"], spec["dy"], spec["dz"])
        actual_size = (cube.dx, cube.dy, cube.dz)
        if actual_size != expected_size:
            raise SystemExit(
                f"Cube {index} size mismatch. Expected {expected_size}, got {actual_size}."
            )

        layout = _face_layout(spec["u"], spec["v"], spec["dx"], spec["dy"], spec["dz"])
        for face_name, (target_u, target_v, target_w, target_h) in layout.items():
            if target_w <= 0 or target_h <= 0:
                continue
            patch = _crop_face(source, cube.faces[face_name])
            if patch.size != (target_w, target_h):
                patch = patch.resize((target_w, target_h), Image.NEAREST)
            atlas.paste(patch, (target_u, target_v))

    atlas.save(OUTPUT_TEXTURE_PATH)
    print(
        f"Wrote {OUTPUT_TEXTURE_PATH.name} from {SOURCE_TEXTURE_PATH.name} using "
        f"{len(extracted_cubes)} cuboids from {GLTF_PATH.name}."
    )


if __name__ == "__main__":
    main()