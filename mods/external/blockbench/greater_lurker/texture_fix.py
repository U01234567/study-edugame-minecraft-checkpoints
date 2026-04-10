from __future__ import annotations

import base64
import json
from pathlib import Path

from PIL import Image

SOURCE_GLTF = Path("./source/model.gltf")
SOURCE_TEXTURE = Path("./textures/gltf_embedded_1.png")
OUTPUT_TEXTURE = Path("./model.png")
ATLAS_SIZE = (256, 256)

# Only visible mesh nodes are included. The GLTF hitbox node uses material 0 and is intentionally ignored.
PARTS = [
    {"node": 1, "name": "head_2", "u": 0, "v": 0, "dx": 13, "dy": 13, "dz": 8},
    {"node": 2, "name": "neck", "u": 44, "v": 0, "dx": 12, "dy": 14, "dz": 20},
    {"node": 3, "name": "ljaw", "u": 110, "v": 0, "dx": 15, "dy": 7, "dz": 34},
    {"node": 4, "name": "tooth", "u": 210, "v": 0, "dx": 2, "dy": 4, "dz": 1},
    {"node": 5, "name": "tooth_2", "u": 218, "v": 0, "dx": 1, "dy": 4, "dz": 2},
    {"node": 6, "name": "tooth_3", "u": 226, "v": 0, "dx": 1, "dy": 4, "dz": 2},
    {"node": 7, "name": "tooth_4", "u": 234, "v": 0, "dx": 2, "dy": 8, "dz": 2},
    {"node": 8, "name": "tooth_5", "u": 244, "v": 0, "dx": 2, "dy": 5, "dz": 1},
    {"node": 9, "name": "tooth_6", "u": 0, "v": 43, "dx": 1, "dy": 4, "dz": 2},
    {"node": 10, "name": "tooth_7", "u": 8, "v": 43, "dx": 1, "dy": 4, "dz": 2},
    {"node": 11, "name": "tooth_8", "u": 16, "v": 43, "dx": 1, "dy": 4, "dz": 2},
    {"node": 12, "name": "tooth_9", "u": 24, "v": 43, "dx": 2, "dy": 2, "dz": 2},
    {"node": 16, "name": "ujaw", "u": 34, "v": 43, "dx": 12, "dy": 6, "dz": 25},
    {"node": 17, "name": "ujaw_2", "u": 110, "v": 43, "dx": 11, "dy": 5, "dz": 7},
    {"node": 18, "name": "cube", "u": 148, "v": 43, "dx": 3, "dy": 2, "dz": 3},
    {"node": 19, "name": "cube_2", "u": 162, "v": 43, "dx": 2, "dy": 1, "dz": 2},
    {"node": 20, "name": "cube_3", "u": 172, "v": 43, "dx": 1, "dy": 1, "dz": 1},
    {"node": 21, "name": "cube_4", "u": 178, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 22, "name": "cube_5", "u": 184, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 23, "name": "cube_6", "u": 190, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 24, "name": "cube_7", "u": 196, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 25, "name": "cube_8", "u": 202, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 26, "name": "cube_9", "u": 208, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 27, "name": "cube_10", "u": 214, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 28, "name": "cube_11", "u": 220, "v": 43, "dx": 1, "dy": 3, "dz": 1},
    {"node": 29, "name": "cube_12", "u": 226, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 30, "name": "cube_13", "u": 232, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 31, "name": "cube_14", "u": 238, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 32, "name": "cube_15", "u": 244, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 33, "name": "cube_16", "u": 250, "v": 43, "dx": 1, "dy": 2, "dz": 1},
    {"node": 34, "name": "cube_17", "u": 0, "v": 76, "dx": 1, "dy": 2, "dz": 1},
    {"node": 35, "name": "cube_18", "u": 6, "v": 76, "dx": 1, "dy": 3, "dz": 1},
    {"node": 40, "name": "cube_19", "u": 12, "v": 76, "dx": 6, "dy": 2, "dz": 11},
    {"node": 42, "name": "cube_20", "u": 48, "v": 76, "dx": 6, "dy": 2, "dz": 11},
    {"node": 44, "name": "lbase", "u": 84, "v": 76, "dx": 8, "dy": 4, "dz": 5},
    {"node": 45, "name": "flipper", "u": 112, "v": 76, "dx": 9, "dy": 2, "dz": 15},
    {"node": 46, "name": "claw1", "u": 162, "v": 76, "dx": 2, "dy": 1, "dz": 5},
    {"node": 47, "name": "claw2", "u": 178, "v": 76, "dx": 2, "dy": 1, "dz": 4},
    {"node": 48, "name": "claw3", "u": 192, "v": 76, "dx": 1, "dy": 1, "dz": 2},
    {"node": 50, "name": "lbase_2", "u": 200, "v": 76, "dx": 8, "dy": 4, "dz": 5},
    {"node": 51, "name": "flipper_2", "u": 0, "v": 95, "dx": 9, "dy": 2, "dz": 15},
    {"node": 52, "name": "claw1_2", "u": 50, "v": 95, "dx": 2, "dy": 1, "dz": 5},
    {"node": 53, "name": "claw2_2", "u": 66, "v": 95, "dx": 2, "dy": 1, "dz": 4},
    {"node": 54, "name": "claw3_2", "u": 80, "v": 95, "dx": 1, "dy": 1, "dz": 2},
    {"node": 57, "name": "bodyfront", "u": 88, "v": 95, "dx": 17, "dy": 18, "dz": 5},
    {"node": 58, "name": "body_2", "u": 134, "v": 95, "dx": 20, "dy": 19, "dz": 35},
    {"node": 59, "name": "bodyback", "u": 0, "v": 151, "dx": 17, "dy": 18, "dz": 5},
    {"node": 60, "name": "bonescale", "u": 46, "v": 151, "dx": 4, "dy": 2, "dz": 3},
    {"node": 61, "name": "bonescale_2", "u": 62, "v": 151, "dx": 3, "dy": 2, "dz": 3},
    {"node": 62, "name": "bonescale_3", "u": 76, "v": 151, "dx": 3, "dy": 2, "dz": 2},
    {"node": 63, "name": "bonescale_4", "u": 88, "v": 151, "dx": 2, "dy": 2, "dz": 2},
    {"node": 66, "name": "tail1", "u": 98, "v": 151, "dx": 13, "dy": 15, "dz": 11},
    {"node": 67, "name": "skale", "u": 148, "v": 151, "dx": 2, "dy": 5, "dz": 5},
    {"node": 68, "name": "tail2_2", "u": 164, "v": 151, "dx": 10, "dy": 12, "dz": 11},
    {"node": 69, "name": "skale_2", "u": 208, "v": 151, "dx": 2, "dy": 5, "dz": 5},
    {"node": 70, "name": "tail3_2", "u": 0, "v": 179, "dx": 8, "dy": 9, "dz": 12},
    {"node": 71, "name": "skale_3", "u": 42, "v": 179, "dx": 2, "dy": 6, "dz": 6},
    {"node": 72, "name": "tail4_2", "u": 60, "v": 179, "dx": 6, "dy": 7, "dz": 19},
    {"node": 73, "name": "fluke1", "u": 112, "v": 179, "dx": 2, "dy": 17, "dz": 7},
    {"node": 74, "name": "fluke2", "u": 132, "v": 179, "dx": 2, "dy": 12, "dz": 5},
    {"node": 75, "name": "fluke3", "u": 148, "v": 179, "dx": 2, "dy": 8, "dz": 3},
    {"node": 76, "name": "skale_4", "u": 160, "v": 179, "dx": 2, "dy": 7, "dz": 6},
]

FACE_LAYOUT = {
    "west":  lambda part: (part["u"], part["v"] + part["dz"]),
    "north": lambda part: (part["u"] + part["dz"], part["v"] + part["dz"]),
    "east":  lambda part: (part["u"] + part["dz"] + part["dx"], part["v"] + part["dz"]),
    "south": lambda part: (part["u"] + (2 * part["dz"]) + part["dx"], part["v"] + part["dz"]),
    "up":    lambda part: (part["u"] + part["dz"], part["v"]),
    "down":  lambda part: (part["u"] + part["dz"] + part["dx"], part["v"]),
}

CORNER_TRANSFORMS = {
    "identity": {(0, 0): (0, 0), (1, 0): (1, 0), (0, 1): (0, 1), (1, 1): (1, 1)},
    "flip_h": {(0, 0): (1, 0), (1, 0): (0, 0), (0, 1): (1, 1), (1, 1): (0, 1)},
    "flip_v": {(0, 0): (0, 1), (1, 0): (1, 1), (0, 1): (0, 0), (1, 1): (1, 0)},
    "rot180": {(0, 0): (1, 1), (1, 0): (0, 1), (0, 1): (1, 0), (1, 1): (0, 0)},
    "transpose": {(0, 0): (0, 0), (1, 0): (0, 1), (0, 1): (1, 0), (1, 1): (1, 1)},
    "rot90": {(0, 0): (1, 0), (1, 0): (1, 1), (0, 1): (0, 0), (1, 1): (0, 1)},
    "rot270": {(0, 0): (0, 1), (1, 0): (0, 0), (0, 1): (1, 1), (1, 1): (1, 0)},
    "transverse": {(0, 0): (1, 1), (1, 0): (1, 0), (0, 1): (0, 1), (1, 1): (0, 0)},
}

PIL_TRANSPOSE = {
    "identity": None,
    "flip_h": Image.Transpose.FLIP_LEFT_RIGHT,
    "flip_v": Image.Transpose.FLIP_TOP_BOTTOM,
    "rot180": Image.Transpose.ROTATE_180,
    "transpose": Image.Transpose.TRANSPOSE,
    "rot90": Image.Transpose.ROTATE_270,
    "rot270": Image.Transpose.ROTATE_90,
    "transverse": Image.Transpose.TRANSVERSE,
}

COMPONENT_COUNTS = {"SCALAR": 1, "VEC2": 2, "VEC3": 3, "VEC4": 4, "MAT2": 4, "MAT3": 9, "MAT4": 16}
DTYPES = {5120: "b", 5121: "B", 5122: "h", 5123: "H", 5125: "I", 5126: "f"}

def _face_basis(face: str, position: tuple[float, float, float]) -> tuple[float, float]:
    x, y, z = position
    if face == "west":
        return z, -y
    if face == "east":
        return -z, -y
    if face == "north":
        return x, -y
    if face == "south":
        return -x, -y
    if face == "up":
        return x, z
    if face == "down":
        return x, -z
    raise KeyError(face)

def _extract_accessor(gltf: dict, buffer_bytes: bytes, accessor_index: int):
    import struct
    accessor = gltf["accessors"][accessor_index]
    view = gltf["bufferViews"][accessor["bufferView"]]
    offset = view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
    count = accessor["count"]
    arity = COMPONENT_COUNTS[accessor["type"]]
    fmt = "<" + DTYPES[accessor["componentType"]] * (count * arity)
    values = struct.unpack_from(fmt, buffer_bytes, offset)
    rows = []
    for row_index in range(count):
        start = row_index * arity
        rows.append(tuple(float(v) for v in values[start:start + arity]))
    return rows

def _load_gltf(path: Path) -> tuple[dict, bytes]:
    gltf = json.loads(path.read_text(encoding="utf-8"))
    uri = gltf["buffers"][0]["uri"]
    if not uri.startswith("data:"):
        raise ValueError("Expected an embedded-buffer GLTF for greater_lurker")
    buffer_bytes = base64.b64decode(uri.split(",", 1)[1])
    return gltf, buffer_bytes

def _read_face_data(gltf: dict, buffer_bytes: bytes, node_index: int, image_size: tuple[int, int]) -> dict[str, dict]:
    node = gltf["nodes"][node_index]
    mesh = gltf["meshes"][node["mesh"]]
    primitive = mesh["primitives"][0]
    positions = _extract_accessor(gltf, buffer_bytes, primitive["attributes"]["POSITION"])
    uvs = _extract_accessor(gltf, buffer_bytes, primitive["attributes"]["TEXCOORD_0"])
    indices = [int(row[0]) for row in _extract_accessor(gltf, buffer_bytes, primitive["indices"])]
    xs = [p[0] for p in positions]
    ys = [p[1] for p in positions]
    zs = [p[2] for p in positions]
    mins = (min(xs), min(ys), min(zs))
    maxs = (max(xs), max(ys), max(zs))
    face_entries: dict[str, list[tuple[tuple[float, float, float], tuple[float, float]]]] = {}

    def cross(a, b):
        return (a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0])

    for tri_index in range(0, len(indices), 3):
        tri = indices[tri_index:tri_index + 3]
        p0, p1, p2 = (positions[tri[0]], positions[tri[1]], positions[tri[2]])
        ab = (p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2])
        ac = (p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2])
        normal = cross(ab, ac)
        axis = max(range(3), key=lambda idx: abs(normal[idx]))
        coord = sum(point[axis] for point in (p0, p1, p2)) / 3.0
        if abs(coord - mins[axis]) < 1e-5:
            side = "min"
        elif abs(coord - maxs[axis]) < 1e-5:
            side = "max"
        else:
            continue
        face = {0: {"min": "west", "max": "east"}, 1: {"min": "down", "max": "up"}, 2: {"min": "north", "max": "south"}}[axis][side]
        face_entries.setdefault(face, [])
        for vertex_index in tri:
            face_entries[face].append((positions[vertex_index], uvs[vertex_index]))

    width, height = image_size
    face_data: dict[str, dict] = {}
    for face, entries in face_entries.items():
        unique_entries = []
        seen = set()
        for position, uv in entries:
            key = (tuple(round(v, 6) for v in position), tuple(round(v, 6) for v in uv))
            if key in seen:
                continue
            seen.add(key)
            unique_entries.append((position, uv))
        if len(unique_entries) != 4:
            raise ValueError(f"{node_index}:{face} expected 4 unique vertices, got {len(unique_entries)}")

        image_points = []
        basis_values = []
        for position, uv in unique_entries:
            image_points.append((uv[0] * width, (1.0 - uv[1]) * height))
            basis_values.append(_face_basis(face, position))

        sx_values = [point[0] for point in image_points]
        sy_values = [point[1] for point in image_points]
        bx_values = [point[0] for point in basis_values]
        by_values = [point[1] for point in basis_values]
        sx_min, sx_max = min(sx_values), max(sx_values)
        sy_min, sy_max = min(sy_values), max(sy_values)
        bx_min, bx_max = min(bx_values), max(bx_values)
        by_min, by_max = min(by_values), max(by_values)

        mapping = {}
        for (position, _uv), image_point, basis_point in zip(unique_entries, image_points, basis_values):
            source_corner = (0 if abs(image_point[0] - sx_min) < 1e-4 else 1, 0 if abs(image_point[1] - sy_min) < 1e-4 else 1)
            target_corner = (0 if abs(basis_point[0] - bx_min) < 1e-4 else 1, 0 if abs(basis_point[1] - by_min) < 1e-4 else 1)
            mapping[source_corner] = target_corner

        transform_name = None
        for candidate_name, candidate_mapping in CORNER_TRANSFORMS.items():
            if all(candidate_mapping[source_corner] == target_corner for source_corner, target_corner in mapping.items()):
                transform_name = candidate_name
                break
        if transform_name is None:
            raise ValueError(f"{node_index}:{face} could not resolve a face transform")

        face_data[face] = {
            "rect": (int(round(sx_min)), int(round(sy_min)), int(round(sx_max)), int(round(sx_max if False else sy_max))) if False else (int(round(sx_min)), int(round(sy_min)), int(round(sx_max)), int(round(sy_max))),
            "transform": transform_name,
        }
    return face_data

def main() -> None:
    gltf, buffer_bytes = _load_gltf(SOURCE_GLTF)
    source = Image.open(SOURCE_TEXTURE).convert("RGBA")
    atlas = Image.new("RGBA", ATLAS_SIZE, (0, 0, 0, 0))

    for part in PARTS:
        face_data = _read_face_data(gltf, buffer_bytes, part["node"], source.size)
        for face_name, placement in FACE_LAYOUT.items():
            rect = face_data[face_name]["rect"]
            patch = source.crop(rect)
            transpose_mode = PIL_TRANSPOSE[face_data[face_name]["transform"]]
            if transpose_mode is not None:
                patch = patch.transpose(transpose_mode)
            target_x, target_y = placement(part)
            atlas.paste(patch, (target_x, target_y))

    atlas.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE} ({ATLAS_SIZE[0]}x{ATLAS_SIZE[1]})")

if __name__ == "__main__":
    main()