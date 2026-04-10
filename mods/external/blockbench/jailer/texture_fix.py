#!/usr/bin/env python3
from __future__ import annotations

import base64
import json
import math
import struct
import sys
from collections import defaultdict
from pathlib import Path

from PIL import Image, ImageOps

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

EXPECTED_TEXTURE = (
    "7741e83c3a2937ad44f3c9a95894e434ebd06a1d11f4b5a41f6a4c6a3fa42477",
    10083,
)
EXPECTED_SCALE_FACTOR = 16.0
ATLAS_WIDTH = 512

# Minecraft cuboid UV orientation, expressed as source-corner lookup order:
# [top-left, top-right, bottom-left, bottom-right]
MC_FACE_CORNERS = {
    "west": [
        ("minx", "miny", "maxz"),
        ("minx", "miny", "minz"),
        ("minx", "maxy", "maxz"),
        ("minx", "maxy", "minz"),
    ],
    "north": [
        ("minx", "miny", "minz"),
        ("maxx", "miny", "minz"),
        ("minx", "maxy", "minz"),
        ("maxx", "maxy", "minz"),
    ],
    "east": [
        ("maxx", "miny", "minz"),
        ("maxx", "miny", "maxz"),
        ("maxx", "maxy", "minz"),
        ("maxx", "maxy", "maxz"),
    ],
    "south": [
        ("maxx", "miny", "maxz"),
        ("minx", "miny", "maxz"),
        ("maxx", "maxy", "maxz"),
        ("minx", "maxy", "maxz"),
    ],
    "up": [
        ("minx", "maxy", "minz"),
        ("maxx", "maxy", "minz"),
        ("minx", "maxy", "maxz"),
        ("maxx", "maxy", "maxz"),
    ],
    "down": [
        ("minx", "miny", "maxz"),
        ("maxx", "miny", "maxz"),
        ("minx", "miny", "minz"),
        ("maxx", "miny", "minz"),
    ],
}


def sha256_file(path: Path) -> str:
    import hashlib

    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()


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
    acc = root["accessors"][accessor_index]
    view = root["bufferViews"][acc["bufferView"]]
    component_type = acc["componentType"]
    component_count = _NUM_COMPONENTS[acc["type"]]
    fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
    stride = view.get("byteStride", struct.calcsize(fmt))
    byte_offset = view.get("byteOffset", 0) + acc.get("byteOffset", 0)
    raw = buffers[view["buffer"]]
    out: list[tuple[float, ...]] = []
    for idx in range(acc["count"]):
        start = byte_offset + idx * stride
        out.append(struct.unpack_from(fmt, raw, start))
    return out


def quantized(value: float) -> float:
    return round(value, 6)


def key_point(point: tuple[float, float, float]) -> tuple[float, float, float]:
    return tuple(quantized(v) for v in point)


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
        indices = [int(v[0]) for v in accessor(root, buffers, primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def classify_face(
    tri_positions: list[tuple[float, float, float]],
    local_min: tuple[float, float, float],
    local_max: tuple[float, float, float],
) -> str | None:
    tri_x = {quantized(p[0]) for p in tri_positions}
    tri_y = {quantized(p[1]) for p in tri_positions}
    tri_z = {quantized(p[2]) for p in tri_positions}

    if len(tri_x) == 1:
        return "west" if quantized(next(iter(tri_x))) == quantized(local_min[0]) else "east"
    if len(tri_y) == 1:
        return "down" if quantized(next(iter(tri_y))) == quantized(local_min[1]) else "up"
    if len(tri_z) == 1:
        return "north" if quantized(next(iter(tri_z))) == quantized(local_min[2]) else "south"
    return None


def extract_exact_cuboid(root: dict, buffers: list[bytes], primitive: dict) -> dict:
    positions, texcoords = primitive_vertices(root, buffers, primitive)
    if not positions:
        raise ValueError("Primitive has no positions")

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
        raise ValueError("Encountered non-cuboid primitive in jailer texture fixer")

    face_corner_uvs_raw: dict[str, dict[tuple[float, float, float], list[tuple[float, float]]]] = {
        "west": defaultdict(list),
        "north": defaultdict(list),
        "east": defaultdict(list),
        "south": defaultdict(list),
        "up": defaultdict(list),
        "down": defaultdict(list),
    }

    for idx in range(0, len(positions) - 2, 3):
        tri_positions = positions[idx:idx + 3]
        tri_uvs = texcoords[idx:idx + 3]
        face = classify_face(tri_positions, local_min, local_max)
        if face is None:
            continue
        for point, uv in zip(tri_positions, tri_uvs):
            face_corner_uvs_raw[face][key_point(point)].append(uv)

    source_face_corners: dict[str, list[tuple[float, float]]] = {}
    for face_name, corner_specs in MC_FACE_CORNERS.items():
        resolved: list[tuple[float, float]] = []
        for x_name, y_name, z_name in corner_specs:
            x = local_min[0] if x_name == "minx" else local_max[0]
            y = local_min[1] if y_name == "miny" else local_max[1]
            z = local_min[2] if z_name == "minz" else local_max[2]
            key = key_point((x, y, z))
            matches = face_corner_uvs_raw[face_name].get(key)
            if not matches:
                raise ValueError(
                    f"Missing UV corner for face {face_name} at {key} in jailer texture fixer"
                )
            avg_u = sum(uv[0] for uv in matches) / len(matches)
            avg_v = sum(uv[1] for uv in matches) / len(matches)
            resolved.append((avg_u, avg_v))
        source_face_corners[face_name] = resolved

    return {
        "local_min": local_min,
        "local_max": local_max,
        "source_face_corners": source_face_corners,
    }


def choose_scale_factor(cubes: list[dict]) -> float:
    sizes = []
    for cube in cubes:
        dx = cube["local_max"][0] - cube["local_min"][0]
        dy = cube["local_max"][1] - cube["local_min"][1]
        dz = cube["local_max"][2] - cube["local_min"][2]
        sizes.append(max(dx, dy, dz))
    if not sizes:
        return 16.0
    sizes.sort()
    median = sizes[len(sizes) // 2]
    return 16.0 if median <= 4.0 else 1.0


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    # This matches Minecraft's cuboid UV layout for .uv(u, v).cuboid(dx, dy, dz):
    # top row: down, up
    # side row: west, north, east, south
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "down": (origin_u + dz, origin_v, dx, dz),
        "up": (origin_u + dz + dx, origin_v, dx, dz),
    }


def bilerp(
    top_left: tuple[float, float],
    top_right: tuple[float, float],
    bottom_left: tuple[float, float],
    bottom_right: tuple[float, float],
    s: float,
    t: float,
) -> tuple[float, float]:
    u = (
        top_left[0] * (1.0 - s) * (1.0 - t)
        + top_right[0] * s * (1.0 - t)
        + bottom_left[0] * (1.0 - s) * t
        + bottom_right[0] * s * t
    )
    v = (
        top_left[1] * (1.0 - s) * (1.0 - t)
        + top_right[1] * s * (1.0 - t)
        + bottom_left[1] * (1.0 - s) * t
        + bottom_right[1] * s * t
    )
    return u, v


def render_face_exact(
    image: Image.Image,
    face_corners: list[tuple[float, float]],
    target_w: int,
    target_h: int,
) -> Image.Image:
    target_w = max(1, target_w)
    target_h = max(1, target_h)

    out = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
    src = image.load()
    dst = out.load()
    width, height = image.size

    top_left, top_right, bottom_left, bottom_right = face_corners

    for y in range(target_h):
        t = (y + 0.5) / target_h
        for x in range(target_w):
            s = (x + 0.5) / target_w
            u, v = bilerp(top_left, top_right, bottom_left, bottom_right, s, t)

            px = max(0, min(width - 1, int(math.floor(u * width))))
            py = max(0, min(height - 1, int(math.floor((1.0 - v) * height))))
            dst[x, y] = src[px, py]

    return out


def main() -> int:
    root_dir = Path(__file__).resolve().parent
    gltf_path = root_dir / "source" / "model.gltf"
    texture_path = root_dir / "textures" / "gltf_embedded_0.png"
    out_path = root_dir / "model.png"

    if not gltf_path.exists():
        print(f"Missing source GLTF: {gltf_path}", file=sys.stderr)
        return 1
    if not texture_path.exists():
        print(f"Missing source texture: {texture_path}", file=sys.stderr)
        return 1

    actual_size = texture_path.stat().st_size
    actual_hash = sha256_file(texture_path)
    expected_hash, expected_size = EXPECTED_TEXTURE
    if actual_size != expected_size or actual_hash != expected_hash:
        print(
            f"Source texture mismatch for {texture_path}\n"
            f"  expected size/hash: {expected_size} / {expected_hash}\n"
            f"  actual size/hash:   {actual_size} / {actual_hash}",
            file=sys.stderr,
        )
        return 1

    gltf_root, buffers = load_gltf(gltf_path)

    # The sidecar PNG is vertically flipped relative to the image embedded in the GLTF.
    # Flip it here so UV sampling matches the embedded source image orientation.
    image = ImageOps.flip(Image.open(texture_path).convert("RGBA"))

    mesh_cache: dict[int, list[dict]] = {}
    cubes_for_scale: list[dict] = []
    for mesh_index, mesh in enumerate(gltf_root.get("meshes", [])):
        mesh_cubes: list[dict] = []
        for primitive in mesh.get("primitives", []):
            cube = extract_exact_cuboid(gltf_root, buffers, primitive)
            mesh_cubes.append(cube)
            cubes_for_scale.append(cube)
        mesh_cache[mesh_index] = mesh_cubes

    scale_factor = choose_scale_factor(cubes_for_scale)
    if abs(scale_factor - EXPECTED_SCALE_FACTOR) > 1.0e-6:
        print(
            f"Unexpected scale factor: {scale_factor} (expected {EXPECTED_SCALE_FACTOR})",
            file=sys.stderr,
        )
        return 1

    atlas_tiles: list[tuple[dict, int, int, int]] = []

    def visit(node_index: int) -> None:
        node = gltf_root["nodes"][node_index]
        for cube in mesh_cache.get(node.get("mesh", -1), []):
            dx = max(1, int(round((cube["local_max"][0] - cube["local_min"][0]) * scale_factor)))
            dy = max(1, int(round((cube["local_max"][1] - cube["local_min"][1]) * scale_factor)))
            dz = max(1, int(round((cube["local_max"][2] - cube["local_min"][2]) * scale_factor)))
            atlas_tiles.append((cube, dx, dy, dz))
        for child_index in node.get("children", []):
            visit(child_index)

    scene = gltf_root["scenes"][gltf_root.get("scene", 0)]
    for node_index in scene.get("nodes", []):
        visit(node_index)

    shelf_u = 0
    shelf_v = 0
    shelf_h = 0
    provisional_h = 0
    placed_tiles: list[tuple[dict, int, int, int, int, int]] = []

    for cube, dx, dy, dz in atlas_tiles:
        tile_w = 2 * (dx + dz)
        tile_h = dy + dz

        if shelf_u + tile_w > ATLAS_WIDTH:
            shelf_u = 0
            shelf_v += shelf_h + 2
            shelf_h = 0

        placed_tiles.append((cube, shelf_u, shelf_v, dx, dy, dz))
        shelf_u += tile_w + 2
        shelf_h = max(shelf_h, tile_h)
        provisional_h = max(provisional_h, shelf_v + tile_h)

    atlas_h = 1
    while atlas_h < provisional_h + 2:
        atlas_h *= 2

    atlas = Image.new("RGBA", (ATLAS_WIDTH, max(16, atlas_h)), (0, 0, 0, 0))
    for cube, origin_u, origin_v, dx, dy, dz in placed_tiles:
        layout = face_layout(origin_u, origin_v, dx, dy, dz)
        for face_name, (target_u, target_v, target_w, target_h) in layout.items():
            patch = render_face_exact(
                image,
                cube["source_face_corners"][face_name],
                target_w,
                target_h,
            )
            atlas.paste(patch, (target_u, target_v))

    atlas.save(out_path)
    print(f"Wrote {out_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())