from __future__ import annotations

import base64
import json
import re
import struct
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent
MODEL_JAVA = ROOT / "model.java"
SOURCE_DIR = ROOT / "source"
SOURCE_TEXTURE = ROOT / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE = ROOT / "model.png"

CUBOID_RE = re.compile(
    r"\.uv\((\d+),\s*(\d+)\)\.cuboid\([^,]+,\s*[^,]+,\s*[^,]+,\s*([0-9.]+)F,\s*([0-9.]+)F,\s*([0-9.]+)F",
    re.MULTILINE,
)
TEXTURE_SIZE_RE = re.compile(r"TexturedModelData\.of\(modelData,\s*(\d+),\s*(\d+)\)")

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
FACE_ORDER = ("west", "north", "east", "south", "up", "down")


def _find_gltf() -> Path:
    gltfs = sorted(SOURCE_DIR.glob("*.gltf"))
    if len(gltfs) != 1:
        raise SystemExit(f"Expected exactly one source GLTF in {SOURCE_DIR}, found {len(gltfs)}.")
    return gltfs[0]


class GltfReader:
    def __init__(self, gltf_path: Path) -> None:
        self.gltf_path = gltf_path
        self.root = json.loads(gltf_path.read_text(encoding="utf-8"))
        self.buffers = self._load_buffers()

    def _load_buffers(self) -> list[bytes]:
        out: list[bytes] = []
        for entry in self.root.get("buffers", []):
            uri = entry.get("uri", "")
            if uri.startswith("data:"):
                out.append(base64.b64decode(uri.split(",", 1)[1]))
            else:
                out.append((self.gltf_path.parent / uri).read_bytes())
        return out

    def accessor(self, accessor_index: int) -> list[tuple[float, ...]]:
        accessor = self.root["accessors"][accessor_index]
        buffer_view = self.root["bufferViews"][accessor["bufferView"]]
        component_type = accessor["componentType"]
        component_count = _NUM_COMPONENTS[accessor["type"]]
        fmt = "<" + (_COMPONENT_FORMATS[component_type] * component_count)
        stride = buffer_view.get("byteStride", struct.calcsize(fmt))
        byte_offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
        raw = self.buffers[buffer_view["buffer"]]

        out: list[tuple[float, ...]] = []
        for idx in range(accessor["count"]):
            start = byte_offset + (idx * stride)
            out.append(struct.unpack_from(fmt, raw, start))
        return out


def _parse_model_java() -> tuple[list[tuple[int, int, int, int, int]], tuple[int, int]]:
    text = MODEL_JAVA.read_text(encoding="utf-8")

    cubes: list[tuple[int, int, int, int, int]] = []
    for match in CUBOID_RE.finditer(text):
        u, v, dx, dy, dz = match.groups()
        cubes.append(
            (
                int(u),
                int(v),
                max(1, int(round(float(dx)))),
                max(1, int(round(float(dy)))),
                max(1, int(round(float(dz)))),
            )
        )

    if not cubes:
        raise SystemExit(f"No cuboids were found in {MODEL_JAVA}.")

    texture_match = TEXTURE_SIZE_RE.search(text)
    if texture_match:
        texture_size = (int(texture_match.group(1)), int(texture_match.group(2)))
    else:
        max_u = 0
        max_v = 0
        for u, v, dx, dy, dz in cubes:
            tile_w = (2 * dz) + (2 * dx)
            tile_h = dz + dy
            max_u = max(max_u, u + tile_w)
            max_v = max(max_v, v + tile_h)
        texture_size = (max_u, max_v)

    return cubes, texture_size


def _preorder_meshes(gltf_root: dict) -> list[int]:
    scene_index = gltf_root.get("scene", 0)
    scene = gltf_root["scenes"][scene_index]
    nodes = gltf_root["nodes"]

    mesh_indices: list[int] = []

    def visit(node_index: int) -> None:
        node = nodes[node_index]
        mesh_index = node.get("mesh")
        if mesh_index is not None:
            mesh_indices.append(mesh_index)
        for child_index in node.get("children", []):
            visit(child_index)

    for top_level_index in scene.get("nodes", []):
        visit(top_level_index)

    return mesh_indices


def _primitive_vertices(
    reader: GltfReader,
    primitive: dict,
) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [tuple(map(float, xyz)) for xyz in reader.accessor(primitive["attributes"]["POSITION"])]
    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [tuple(map(float, uv)) for uv in reader.accessor(primitive["attributes"]["TEXCOORD_0"])]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(value[0]) for value in reader.accessor(primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def _classify_cuboid_faces(
    reader: GltfReader,
    mesh_index: int,
) -> tuple[
    tuple[float, float, float],
    tuple[float, float, float],
    dict[str, list[tuple[list[tuple[float, float, float]], list[tuple[float, float]]]]],
]:
    primitive = reader.root["meshes"][mesh_index]["primitives"][0]
    positions, texcoords = _primitive_vertices(reader, primitive)

    xs = [p[0] for p in positions]
    ys = [p[1] for p in positions]
    zs = [p[2] for p in positions]
    local_min = (min(xs), min(ys), min(zs))
    local_max = (max(xs), max(ys), max(zs))

    unique_x = {round(v, 6) for v in xs}
    unique_y = {round(v, 6) for v in ys}
    unique_z = {round(v, 6) for v in zs}
    if not (len(unique_x) <= 2 and len(unique_y) <= 2 and len(unique_z) <= 2):
        raise SystemExit(
            f"Mesh {mesh_index} is not an exact cuboid; this texture fix is intentionally Class-B cuboid-only."
        )

    faces: dict[str, list[tuple[list[tuple[float, float, float]], list[tuple[float, float]]]]] = {
        face: [] for face in FACE_ORDER
    }

    for idx in range(0, len(positions), 3):
        tri_positions = positions[idx:idx + 3]
        tri_texcoords = texcoords[idx:idx + 3]

        tri_x = {round(p[0], 6) for p in tri_positions}
        tri_y = {round(p[1], 6) for p in tri_positions}
        tri_z = {round(p[2], 6) for p in tri_positions}

        face_name: str | None = None
        if len(tri_x) == 1:
            face_name = "west" if round(next(iter(tri_x)), 6) == round(local_min[0], 6) else "east"
        elif len(tri_y) == 1:
            face_name = "down" if round(next(iter(tri_y)), 6) == round(local_min[1], 6) else "up"
        elif len(tri_z) == 1:
            face_name = "north" if round(next(iter(tri_z)), 6) == round(local_min[2], 6) else "south"

        if face_name is not None:
            faces[face_name].append((tri_positions, tri_texcoords))

    for face_name in FACE_ORDER:
        if not faces[face_name]:
            raise SystemExit(f"Mesh {mesh_index} is missing face data for {face_name}.")

    return local_min, local_max, faces


def _face_local_uv(
    point: tuple[float, float, float],
    local_min: tuple[float, float, float],
    local_max: tuple[float, float, float],
    face_name: str,
) -> tuple[float, float]:
    x, y, z = point
    min_x, min_y, min_z = local_min
    max_x, max_y, max_z = local_max

    if face_name == "west":
        s = (z - min_z) / max(max_z - min_z, 1.0e-8)
        t = (max_y - y) / max(max_y - min_y, 1.0e-8)
    elif face_name == "east":
        s = (max_z - z) / max(max_z - min_z, 1.0e-8)
        t = (max_y - y) / max(max_y - min_y, 1.0e-8)
    elif face_name == "north":
        s = (max_x - x) / max(max_x - min_x, 1.0e-8)
        t = (max_y - y) / max(max_y - min_y, 1.0e-8)
    elif face_name == "south":
        s = (x - min_x) / max(max_x - min_x, 1.0e-8)
        t = (max_y - y) / max(max_y - min_y, 1.0e-8)
    elif face_name == "up":
        s = (x - min_x) / max(max_x - min_x, 1.0e-8)
        t = (z - min_z) / max(max_z - min_z, 1.0e-8)
    elif face_name == "down":
        s = (x - min_x) / max(max_x - min_x, 1.0e-8)
        t = (max_z - z) / max(max_z - min_z, 1.0e-8)
    else:
        raise ValueError(face_name)

    return s, t


def _barycentric(
    p: tuple[float, float],
    a: tuple[float, float],
    b: tuple[float, float],
    c: tuple[float, float],
) -> tuple[float, float, float] | None:
    denom = ((b[1] - c[1]) * (a[0] - c[0])) + ((c[0] - b[0]) * (a[1] - c[1]))
    if abs(denom) < 1.0e-8:
        return None

    w1 = (((b[1] - c[1]) * (p[0] - c[0])) + ((c[0] - b[0]) * (p[1] - c[1]))) / denom
    w2 = (((c[1] - a[1]) * (p[0] - c[0])) + ((a[0] - c[0]) * (p[1] - c[1]))) / denom
    w3 = 1.0 - w1 - w2
    return w1, w2, w3


def _sample_texture(image: Image.Image, uv: tuple[float, float]) -> tuple[int, int, int, int]:
    u, v = uv
    x = min(image.size[0] - 1, max(0, int(round(u * (image.size[0] - 1)))))
    y = min(image.size[1] - 1, max(0, int(round((1.0 - v) * (image.size[1] - 1)))))
    return image.getpixel((x, y))


def _face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def _bake_face(
    source_image: Image.Image,
    local_min: tuple[float, float, float],
    local_max: tuple[float, float, float],
    triangles: list[tuple[list[tuple[float, float, float]], list[tuple[float, float]]]],
    face_name: str,
    width: int,
    height: int,
) -> Image.Image:
    patch = Image.new("RGBA", (width, height), (0, 0, 0, 0))

    normalized_tris: list[tuple[list[tuple[float, float]], list[tuple[float, float]]]] = []
    for tri_positions, tri_texcoords in triangles:
        normalized_tris.append(
            (
                [_face_local_uv(point, local_min, local_max, face_name) for point in tri_positions],
                tri_texcoords,
            )
        )

    for py in range(height):
        for px in range(width):
            point = ((px + 0.5) / width, (py + 0.5) / height)
            color: tuple[int, int, int, int] | None = None

            for tri_points, tri_texcoords in normalized_tris:
                weights = _barycentric(point, tri_points[0], tri_points[1], tri_points[2])
                if weights is None:
                    continue
                if min(weights) < -1.0e-5 or max(weights) > 1.0 + 1.0e-5:
                    continue

                uv = (
                    (weights[0] * tri_texcoords[0][0]) + (weights[1] * tri_texcoords[1][0]) + (weights[2] * tri_texcoords[2][0]),
                    (weights[0] * tri_texcoords[0][1]) + (weights[1] * tri_texcoords[1][1]) + (weights[2] * tri_texcoords[2][1]),
                )
                color = _sample_texture(source_image, uv)
                break

            if color is not None:
                patch.putpixel((px, py), color)

    return patch


def main() -> None:
    gltf_path = _find_gltf()
    cubes, texture_size = _parse_model_java()
    reader = GltfReader(gltf_path)
    source_image = Image.open(SOURCE_TEXTURE).convert("RGBA")

    mesh_order = _preorder_meshes(reader.root)
    if len(mesh_order) != len(cubes):
        raise SystemExit(
            f"model.java has {len(cubes)} cuboids but the GLTF traversal found {len(mesh_order)} mesh nodes."
        )

    atlas = Image.new("RGBA", texture_size, (0, 0, 0, 0))

    for mesh_index, (origin_u, origin_v, dx, dy, dz) in zip(mesh_order, cubes):
        local_min, local_max, faces = _classify_cuboid_faces(reader, mesh_index)
        for face_name, (target_u, target_v, target_w, target_h) in _face_layout(origin_u, origin_v, dx, dy, dz).items():
            patch = _bake_face(
                source_image=source_image,
                local_min=local_min,
                local_max=local_max,
                triangles=faces[face_name],
                face_name=face_name,
                width=target_w,
                height=target_h,
            )
            atlas.paste(patch, (target_u, target_v))

    atlas.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE}")


if __name__ == "__main__":
    main()