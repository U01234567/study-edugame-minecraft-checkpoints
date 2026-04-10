from __future__ import annotations

import argparse
import base64
import collections
import json
import math
import struct
import sys
from collections import defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

try:
    from PIL import Image
except ImportError as exc:  # pragma: no cover
    raise SystemExit("Pillow is required for GLTF texture baking: pip install pillow") from exc

sys.path.append(str(Path(__file__).resolve().parent))

from study_creature_codegen import (
    AnimationClipDef,
    AnimationTrackDef,
    CreatureModelDef,
    CubeDef,
    PartDef,
    emit_model_java,
    snake_to_pascal,
)


_COMPONENT_FORMATS = {
    5120: "b",   # BYTE
    5121: "B",   # UNSIGNED_BYTE
    5122: "h",   # SHORT
    5123: "H",   # UNSIGNED_SHORT
    5125: "I",   # UNSIGNED_INT
    5126: "f",   # FLOAT
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

# When a non-cuboid mesh is approximated as a box, one dimension can collapse to
# ~0.001 after scaling. That produces paper-thin “shells” that look transparent in game.
APPROXIMATE_THIN_DIM_THRESHOLD = 0.10
APPROXIMATE_MIN_THICKNESS = 1.00

MAX_ANIMATION_CLIPS_PER_MODEL = 2
MAX_KEYS_PER_TRACK = 40
TRANSLATION_EPSILON = 0.02
ROTATION_EPSILON = 0.01
SCALE_EPSILON = 0.01


@dataclass
class MeshCube:
    local_min: tuple[float, float, float]
    local_max: tuple[float, float, float]
    source_faces: dict[str, tuple[float, float, float, float]]
    material_image: Image.Image | None
    texture_source_index: int | None
    approximate: bool = False


@dataclass
class ConversionResult:
    model: CreatureModelDef
    atlas: Image.Image
    outer_model: CreatureModelDef | None
    outer_atlas: Image.Image | None
    report: dict[str, Any]


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
            start = byte_offset + idx * stride
            out.append(struct.unpack_from(fmt, raw, start))
        return out

    def primitive_texture_source_index(self, primitive: dict[str, Any]) -> int | None:
        material_index = primitive.get("material")
        if material_index is None:
            images = self.root.get("images", [])
            return 0 if images else None

        material = self.root.get("materials", [])[material_index]
        pbr = material.get("pbrMetallicRoughness", {})
        base_color_tex = pbr.get("baseColorTexture")
        if not base_color_tex:
            images = self.root.get("images", [])
            return 0 if images else None

        texture_index = base_color_tex["index"]
        texture = self.root["textures"][texture_index]
        return texture["source"]

    def primitive_image(self, primitive: dict[str, Any]) -> Image.Image | None:
        material_index = primitive.get("material")
        if material_index is None:
            return self.first_image()

        material = self.root.get("materials", [])[material_index]
        pbr = material.get("pbrMetallicRoughness", {})
        base_color_tex = pbr.get("baseColorTexture")
        if not base_color_tex:
            return self.first_image()

        texture_index = base_color_tex["index"]
        texture = self.root["textures"][texture_index]
        source_index = texture["source"]
        return self.image_by_index(source_index)

    def first_image(self) -> Image.Image | None:
        if not self.root.get("images"):
            return None
        return self.image_by_index(0)

    def image_by_index(self, image_index: int) -> Image.Image | None:
        image = self.root["images"][image_index]
        uri = image.get("uri")
        if uri:
            if uri.startswith("data:"):
                blob = base64.b64decode(uri.split(",", 1)[1])
                return Image.open(io_bytes(blob)).convert("RGBA")
            return Image.open(self.gltf_path.parent / uri).convert("RGBA")

        buffer_view_index = image.get("bufferView")
        if buffer_view_index is None:
            return None
        buffer_view = self.root["bufferViews"][buffer_view_index]
        raw = self.buffers[buffer_view["buffer"]]
        start = buffer_view.get("byteOffset", 0)
        end = start + buffer_view["byteLength"]
        return Image.open(io_bytes(raw[start:end])).convert("RGBA")


def io_bytes(blob: bytes):
    from io import BytesIO
    return BytesIO(blob)


def quantized(value: float) -> float:
    return round(value, 6)


def first_opaque_pixel(image: Image.Image | None) -> tuple[int, int, int, int]:
    if image is None:
        return (255, 255, 255, 255)
    for pixel in image.convert("RGBA").getdata():
        if pixel[3] > 0:
            return pixel
    return (255, 255, 255, 255)


def crop_to_alpha_bounds(image: Image.Image) -> Image.Image:
    alpha = image.getchannel("A")
    bbox = alpha.getbbox()
    if bbox is None:
        return Image.new("RGBA", (1, 1), first_opaque_pixel(image))
    return image.crop(bbox)


def regularize_approximate_box(
    x: float,
    y: float,
    z: float,
    dx: float,
    dy: float,
    dz: float,
    approximate: bool,
) -> tuple[float, float, float, float, float, float] | None:
    if not approximate:
        return x, y, z, max(0.001, dx), max(0.001, dy), max(0.001, dz)

    dims = [dx, dy, dz]
    thin_axes = [index for index, dim in enumerate(dims) if dim < APPROXIMATE_THIN_DIM_THRESHOLD]

    # If two or three axes collapsed, this is effectively a line/point artifact.
    if len(thin_axes) >= 2:
        return None

    if len(thin_axes) == 1:
        axis = thin_axes[0]
        if axis == 0:
            center = x + (dx / 2.0)
            dx = APPROXIMATE_MIN_THICKNESS
            x = center - (dx / 2.0)
        elif axis == 1:
            center = y + (dy / 2.0)
            dy = APPROXIMATE_MIN_THICKNESS
            y = center - (dy / 2.0)
        else:
            center = z + (dz / 2.0)
            dz = APPROXIMATE_MIN_THICKNESS
            z = center - (dz / 2.0)

    return x, y, z, max(0.001, dx), max(0.001, dy), max(0.001, dz)


def quaternion_normalize(q: tuple[float, float, float, float]) -> tuple[float, float, float, float]:
    x, y, z, w = q
    length = math.sqrt((x * x) + (y * y) + (z * z) + (w * w))
    if length <= 1.0e-8:
        return 0.0, 0.0, 0.0, 1.0
    return x / length, y / length, z / length, w / length


def quaternion_conjugate(q: tuple[float, float, float, float]) -> tuple[float, float, float, float]:
    x, y, z, w = q
    return -x, -y, -z, w


def quaternion_multiply(
    a: tuple[float, float, float, float],
    b: tuple[float, float, float, float],
) -> tuple[float, float, float, float]:
    ax, ay, az, aw = a
    bx, by, bz, bw = b
    return (
        (aw * bx) + (ax * bw) + (ay * bz) - (az * by),
        (aw * by) - (ax * bz) + (ay * bw) + (az * bx),
        (aw * bz) + (ax * by) - (ay * bx) + (az * bw),
        (aw * bw) - (ax * bx) - (ay * by) - (az * bz),
    )


def quaternion_inverse(q: tuple[float, float, float, float]) -> tuple[float, float, float, float]:
    return quaternion_conjugate(quaternion_normalize(q))


def choose_named_clip(clips: list[AnimationClipDef], keywords: tuple[str, ...]) -> str | None:
    lowered = [(clip.name, clip.name.lower()) for clip in clips]
    for original, lower in lowered:
        if any(keyword in lower for keyword in keywords):
            return original
    return None


def vector_delta_sq(a: tuple[float, ...], b: tuple[float, ...]) -> float:
    return sum((float(ax) - float(bx)) ** 2 for ax, bx in zip(a, b))


def remove_redundant_keyframes(
    times: list[float],
    values: list[tuple[float, ...]],
    epsilon: float,
) -> tuple[list[float], list[tuple[float, ...]]]:
    if not times or not values:
        return [], []

    count = min(len(times), len(values))
    if count == 1:
        return [times[0]], [values[0]]

    keep_indices = [0]
    epsilon_sq = epsilon * epsilon

    for index in range(1, count - 1):
        previous_index = keep_indices[-1]
        if vector_delta_sq(values[index], values[previous_index]) > epsilon_sq:
            keep_indices.append(index)

    if keep_indices[-1] != count - 1:
        keep_indices.append(count - 1)

    reduced_times = [times[index] for index in keep_indices]
    reduced_values = [values[index] for index in keep_indices]

    if len(reduced_times) >= 2 and vector_delta_sq(reduced_values[0], reduced_values[-1]) <= epsilon_sq:
        return [reduced_times[0]], [reduced_values[0]]

    return reduced_times, reduced_values


def evenly_sample_keyframes(
    times: list[float],
    values: list[tuple[float, ...]],
    max_keys: int,
) -> tuple[list[float], list[tuple[float, ...]]]:
    if len(times) <= max_keys:
        return times, values

    if max_keys <= 1:
        return [times[0]], [values[0]]

    last_index = len(times) - 1
    chosen_indices = {
        0,
        last_index,
    }

    for slot in range(1, max_keys - 1):
        chosen_indices.add(round((slot * last_index) / (max_keys - 1)))

    ordered = sorted(chosen_indices)
    sampled_times = [times[index] for index in ordered]
    sampled_values = [values[index] for index in ordered]
    return sampled_times, sampled_values


def simplify_track_keyframes(
    times: list[float],
    values: list[tuple[float, ...]],
    *,
    epsilon: float,
    max_keys: int = MAX_KEYS_PER_TRACK,
) -> tuple[list[float], list[tuple[float, ...]]]:
    reduced_times, reduced_values = remove_redundant_keyframes(times, values, epsilon)
    if len(reduced_times) > max_keys:
        reduced_times, reduced_values = evenly_sample_keyframes(reduced_times, reduced_values, max_keys)
    return reduced_times, reduced_values


def select_animation_clips(clips: list[AnimationClipDef]) -> list[AnimationClipDef]:
    if len(clips) <= MAX_ANIMATION_CLIPS_PER_MODEL:
        return clips

    selected_names: list[str] = []

    for keywords in (("idle", "base", "pose", "stand"), ("walk", "run", "move")):
        name = choose_named_clip(clips, keywords)
        if name and name not in selected_names:
            selected_names.append(name)

    for clip in clips:
        if clip.name not in selected_names:
            selected_names.append(clip.name)
        if len(selected_names) >= MAX_ANIMATION_CLIPS_PER_MODEL:
            break

    selected_set = set(selected_names[:MAX_ANIMATION_CLIPS_PER_MODEL])
    return [clip for clip in clips if clip.name in selected_set]


def animation_output_values(
    raw_values: list[tuple[float, ...]],
    interpolation: str,
) -> list[tuple[float, ...]]:
    if interpolation == "CUBICSPLINE":
        return raw_values[1::3]
    return raw_values


def subtree_node_indices(nodes: list[dict[str, Any]], node_index: int) -> list[int]:
    out: list[int] = []
    stack = [node_index]
    while stack:
        current = stack.pop()
        out.append(current)
        stack.extend(nodes[current].get("children", []))
    return out


def cube_volume(cube: MeshCube) -> float:
    return abs(
        (cube.local_max[0] - cube.local_min[0])
        * (cube.local_max[1] - cube.local_min[1])
        * (cube.local_max[2] - cube.local_min[2])
    )


def find_layered_nodes(
    nodes: list[dict[str, Any]],
    top_level: list[int],
    mesh_cache: dict[int, list[MeshCube]],
) -> dict[int, str]:
    def subtree_metrics(node_index: int) -> dict[str, Any]:
        texture_counts: collections.Counter[int | None] = collections.Counter()
        cube_count = 0
        total_volume = 0.0

        for sub_index in subtree_node_indices(nodes, node_index):
            mesh_index = nodes[sub_index].get("mesh", -1)
            for cube in mesh_cache.get(mesh_index, []):
                cube_count += 1
                total_volume += cube_volume(cube)
                texture_counts[cube.texture_source_index] += 1

        dominant_texture = texture_counts.most_common(1)[0][0] if texture_counts else None
        return {
            "cube_count": cube_count,
            "total_volume": total_volume,
            "dominant_texture": dominant_texture,
        }

    queue = list(top_level)
    while queue:
        node_index = queue.pop(0)
        children = nodes[node_index].get("children", [])
        child_infos = [(child_index, subtree_metrics(child_index)) for child_index in children]
        child_infos = [info for info in child_infos if info[1]["cube_count"] > 0]

        if len(child_infos) == 2:
            left_texture = child_infos[0][1]["dominant_texture"]
            right_texture = child_infos[1][1]["dominant_texture"]
            if left_texture is not None and right_texture is not None and left_texture != right_texture:
                left_name = (nodes[child_infos[0][0]].get("name") or "").lower()
                right_name = (nodes[child_infos[1][0]].get("name") or "").lower()

                def layer_score(info: tuple[int, dict[str, Any]]) -> tuple[float, float]:
                    child_index, metrics = info
                    node_name = (nodes[child_index].get("name") or "").lower()
                    name_hint = 0.0 if any(token in node_name for token in ("outer", "overlay", "layer", "shell", "coat")) else 1.0
                    return (name_hint, metrics["total_volume"])

                outer_child, _ = min(child_infos, key=layer_score)
                base_child = child_infos[1][0] if child_infos[0][0] == outer_child else child_infos[0][0]

                result: dict[int, str] = {}
                for sub_index in subtree_node_indices(nodes, base_child):
                    result[sub_index] = "base"
                for sub_index in subtree_node_indices(nodes, outer_child):
                    result[sub_index] = "outer"
                return result

        queue.extend(children)

    return {}


def quaternion_reflect_y(q: tuple[float, float, float, float]) -> tuple[float, float, float, float]:
    x, y, z, w = q
    return (-x, y, -z, w)


def quaternion_to_euler_xyz(q: tuple[float, float, float, float]) -> tuple[float, float, float]:
    x, y, z, w = q

    sinr_cosp = 2.0 * (w * x + y * z)
    cosr_cosp = 1.0 - 2.0 * (x * x + y * y)
    rx = math.atan2(sinr_cosp, cosr_cosp)

    sinp = 2.0 * (w * y - z * x)
    if abs(sinp) >= 1.0:
        ry = math.copysign(math.pi / 2.0, sinp)
    else:
        ry = math.asin(sinp)

    siny_cosp = 2.0 * (w * z + x * y)
    cosy_cosp = 1.0 - 2.0 * (y * y + z * z)
    rz = math.atan2(siny_cosp, cosy_cosp)

    return rx, ry, rz


def decompose_matrix(matrix16: list[float]) -> tuple[tuple[float, float, float], tuple[float, float, float, float], tuple[float, float, float]]:
    m = matrix16
    tx, ty, tz = m[12], m[13], m[14]

    sx = math.sqrt(m[0] ** 2 + m[1] ** 2 + m[2] ** 2)
    sy = math.sqrt(m[4] ** 2 + m[5] ** 2 + m[6] ** 2)
    sz = math.sqrt(m[8] ** 2 + m[9] ** 2 + m[10] ** 2)

    r00, r01, r02 = m[0] / sx, m[4] / sy, m[8] / sz
    r10, r11, r12 = m[1] / sx, m[5] / sy, m[9] / sz
    r20, r21, r22 = m[2] / sx, m[6] / sy, m[10] / sz

    trace = r00 + r11 + r22
    if trace > 0.0:
        s = math.sqrt(trace + 1.0) * 2.0
        w = 0.25 * s
        x = (r21 - r12) / s
        y = (r02 - r20) / s
        z = (r10 - r01) / s
    elif r00 > r11 and r00 > r22:
        s = math.sqrt(1.0 + r00 - r11 - r22) * 2.0
        w = (r21 - r12) / s
        x = 0.25 * s
        y = (r01 + r10) / s
        z = (r02 + r20) / s
    elif r11 > r22:
        s = math.sqrt(1.0 + r11 - r00 - r22) * 2.0
        w = (r02 - r20) / s
        x = (r01 + r10) / s
        y = 0.25 * s
        z = (r12 + r21) / s
    else:
        s = math.sqrt(1.0 + r22 - r00 - r11) * 2.0
        w = (r10 - r01) / s
        x = (r02 + r20) / s
        y = (r12 + r21) / s
        z = 0.25 * s

    return (tx, ty, tz), (x, y, z, w), (sx, sy, sz)


def node_trs(node: dict[str, Any]) -> tuple[tuple[float, float, float], tuple[float, float, float, float], tuple[float, float, float]]:
    if "matrix" in node:
        return decompose_matrix(node["matrix"])

    translation = tuple(node.get("translation", [0.0, 0.0, 0.0]))
    rotation = tuple(node.get("rotation", [0.0, 0.0, 0.0, 1.0]))
    scale = tuple(node.get("scale", [1.0, 1.0, 1.0]))
    return translation, rotation, scale


def primitive_vertices(reader: GltfReader, primitive: dict[str, Any]) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
    positions = [tuple(map(float, xyz)) for xyz in reader.accessor(primitive["attributes"]["POSITION"])]
    texcoords: list[tuple[float, float]]
    if "TEXCOORD_0" in primitive["attributes"]:
        texcoords = [tuple(map(float, uv)) for uv in reader.accessor(primitive["attributes"]["TEXCOORD_0"])]
    else:
        texcoords = [(0.0, 0.0)] * len(positions)

    if "indices" in primitive:
        indices = [int(v[0]) for v in reader.accessor(primitive["indices"])]
        positions = [positions[i] for i in indices]
        texcoords = [texcoords[i] for i in indices]

    return positions, texcoords


def try_extract_cuboid(reader: GltfReader, primitive: dict[str, Any], mesh_fallback: str) -> MeshCube | None:
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

    if not exact_cuboid and mesh_fallback == "skip":
        return None

    faces: dict[str, list[tuple[float, float]]] = defaultdict(list)
    if exact_cuboid:
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

    return MeshCube(
        local_min=local_min,
        local_max=local_max,
        source_faces=source_faces,
        material_image=reader.primitive_image(primitive),
        texture_source_index=reader.primitive_texture_source_index(primitive),
        approximate=not exact_cuboid,
    )


def choose_scale_factor(cubes: list[MeshCube]) -> float:
    sizes = []
    for cube in cubes:
        dx = cube.local_max[0] - cube.local_min[0]
        dy = cube.local_max[1] - cube.local_min[1]
        dz = cube.local_max[2] - cube.local_min[2]
        sizes.append(max(dx, dy, dz))
    if not sizes:
        return 16.0
    sizes.sort()
    median = sizes[len(sizes) // 2]
    return 16.0 if median <= 4.0 else 1.0


def crop_face(image: Image.Image | None, rect: tuple[float, float, float, float] | None) -> Image.Image:
    if image is None:
        return Image.new("RGBA", (1, 1), (255, 255, 255, 255))

    if rect is None:
        # Whole-image fallback causes transparent atlas space to be pasted onto cube faces.
        return crop_to_alpha_bounds(image)

    min_u, min_v, max_u, max_v = rect
    width, height = image.size

    left = max(0, min(width - 1, math.floor(min_u * width)))
    right = max(left + 1, min(width, math.ceil(max_u * width)))
    top = max(0, min(height - 1, math.floor((1.0 - max_v) * height)))
    bottom = max(top + 1, min(height, math.ceil((1.0 - min_v) * height)))

    patch = image.crop((left, top, right, bottom))
    return crop_to_alpha_bounds(patch)


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def build_parts_and_atlas(
    nodes: list[dict[str, Any]],
    top_level: list[int],
    mesh_cache: dict[int, list[MeshCube]],
    scale_factor: float,
    layer_assignment: dict[int, str],
    layer_filter: str | None,
    deformation: float,
) -> tuple[list[PartDef], Image.Image, dict[str, Any], dict[int, str]]:
    synthetic_root = PartDef(name="root", parent=None, pivot=(0.0, 24.0, 0.0), rotation=(0.0, 0.0, 0.0), cubes=[])
    parts: list[PartDef] = [synthetic_root]
    used_names: dict[str, int] = {}
    atlas_tiles: list[tuple[MeshCube, CubeDef]] = []
    node_to_part_name: dict[int, str] = {}

    def unique_part_name(raw: str) -> str:
        base = raw.strip() or "node"
        base = "".join(ch if (ch.isalnum() or ch in "_-") else "_" for ch in base)
        count = used_names.get(base, 0)
        used_names[base] = count + 1
        return base if count == 0 else f"{base}_{count + 1}"

    def visit(node_index: int, parent_name: str) -> None:
        node = nodes[node_index]
        raw_name = node.get("name") or f"node_{node_index}"
        part_name = unique_part_name(raw_name)
        node_to_part_name[node_index] = part_name

        translation, rotation, scale = node_trs(node)
        q = quaternion_reflect_y(tuple(map(float, rotation)))
        rx, ry, rz = quaternion_to_euler_xyz(q)

        pivot = (
            float(translation[0]) * scale_factor,
            float(-translation[1]) * scale_factor,
            float(translation[2]) * scale_factor,
        )

        part = PartDef(
            name=part_name,
            parent=parent_name,
            pivot=pivot,
            rotation=(rx, ry, rz),
            cubes=[],
        )

        include_node = layer_filter is None or layer_assignment.get(node_index, "base") == layer_filter
        for cube in mesh_cache.get(node.get("mesh", -1), []):
            if not include_node:
                continue

            local_min = cube.local_min
            local_max = cube.local_max
            x = local_min[0] * scale_factor
            y = -local_max[1] * scale_factor
            z = local_min[2] * scale_factor
            dx = (local_max[0] - local_min[0]) * scale_factor
            dy = (local_max[1] - local_min[1]) * scale_factor
            dz = (local_max[2] - local_min[2]) * scale_factor

            regularized = regularize_approximate_box(x, y, z, dx, dy, dz, cube.approximate)
            if regularized is None:
                continue
            x, y, z, dx, dy, dz = regularized

            cube_def = CubeDef(
                u=0,
                v=0,
                x=x,
                y=y,
                z=z,
                dx=dx,
                dy=dy,
                dz=dz,
                deformation=deformation,
            )
            part.cubes.append(cube_def)
            atlas_tiles.append((cube, cube_def))

        parts.append(part)
        for child_index in node.get("children", []):
            visit(child_index, part_name)

    for node_index in top_level:
        visit(node_index, "root")

    shelf_u = 0
    shelf_v = 0
    shelf_h = 0
    atlas_w = 512
    provisional_h = 0

    for _, cube_def in atlas_tiles:
        dx = max(1, int(round(cube_def.dx)))
        dy = max(1, int(round(cube_def.dy)))
        dz = max(1, int(round(cube_def.dz)))
        tile_w = 2 * (dx + dz)
        tile_h = dy + dz

        if shelf_u + tile_w > atlas_w:
            shelf_u = 0
            shelf_v += shelf_h + 2
            shelf_h = 0

        cube_def.u = shelf_u
        cube_def.v = shelf_v
        shelf_u += tile_w + 2
        shelf_h = max(shelf_h, tile_h)
        provisional_h = max(provisional_h, shelf_v + tile_h)

    atlas_h = 1
    while atlas_h < provisional_h + 2:
        atlas_h *= 2
    atlas = Image.new("RGBA", (atlas_w, max(16, atlas_h)), (0, 0, 0, 0))

    approximated = 0
    for cube, cube_def in atlas_tiles:
        dx = max(1, int(round(cube_def.dx)))
        dy = max(1, int(round(cube_def.dy)))
        dz = max(1, int(round(cube_def.dz)))

        if cube.approximate:
            approximated += 1

        layout = face_layout(cube_def.u, cube_def.v, dx, dy, dz)
        for face_name, (target_u, target_v, target_w, target_h) in layout.items():
            patch = crop_face(cube.material_image, cube.source_faces.get(face_name))
            patch = patch.resize((max(1, target_w), max(1, target_h)), Image.NEAREST)
            atlas.paste(patch, (target_u, target_v))

    return parts, atlas, {
        "cubes": len(atlas_tiles),
        "approximated_meshes": approximated,
    }, node_to_part_name


def extract_animation_clips(
    reader: GltfReader,
    nodes: list[dict[str, Any]],
    node_to_part_name: dict[int, str],
    scale_factor: float,
) -> list[AnimationClipDef]:
    clips: list[AnimationClipDef] = []

    for animation_index, animation in enumerate(reader.root.get("animations", [])):
        clip_name = animation.get("name", f"animation_{animation_index}")
        tracks_by_part: dict[str, AnimationTrackDef] = {}
        clip_length = 0.0

        for channel in animation.get("channels", []):
            target = channel.get("target", {})
            node_index = target.get("node")
            path = target.get("path")
            if node_index is None or path not in {"translation", "rotation", "scale"}:
                continue
            if node_index not in node_to_part_name:
                continue

            sampler = animation["samplers"][channel["sampler"]]
            times = [float(value[0]) for value in reader.accessor(sampler["input"])]
            if not times:
                continue

            raw_values = reader.accessor(sampler["output"])
            values = animation_output_values(raw_values, sampler.get("interpolation", "LINEAR"))
            if not values:
                continue

            translation_base, rotation_base, scale_base = node_trs(nodes[node_index])
            base_rotation = quaternion_reflect_y(tuple(map(float, rotation_base)))
            track = tracks_by_part.setdefault(node_to_part_name[node_index], AnimationTrackDef(part_name=node_to_part_name[node_index]))

            if path == "translation":
                translated_values: list[tuple[float, float, float]] = []
                for value in values[:len(times)]:
                    translated_values.append((
                        (float(value[0]) - float(translation_base[0])) * scale_factor,
                        -(float(value[1]) - float(translation_base[1])) * scale_factor,
                        (float(value[2]) - float(translation_base[2])) * scale_factor,
                    ))
                simplified_times, simplified_values = simplify_track_keyframes(
                    times,
                    translated_values,
                    epsilon=TRANSLATION_EPSILON,
                )
                if simplified_times:
                    track.translation_times = simplified_times
                    track.translation_values = simplified_values

            elif path == "rotation":
                rotated_values: list[tuple[float, float, float]] = []
                base_inverse = quaternion_inverse(base_rotation)
                for value in values[:len(times)]:
                    reflected = quaternion_reflect_y(tuple(map(float, value[:4])))
                    delta_q = quaternion_multiply(reflected, base_inverse)
                    rotated_values.append(quaternion_to_euler_xyz(quaternion_normalize(delta_q)))
                simplified_times, simplified_values = simplify_track_keyframes(
                    times,
                    rotated_values,
                    epsilon=ROTATION_EPSILON,
                )
                if simplified_times:
                    track.rotation_times = simplified_times
                    track.rotation_values = simplified_values

            elif path == "scale":
                scaled_values: list[tuple[float, float, float]] = []
                sx = float(scale_base[0]) if abs(float(scale_base[0])) > 1.0e-6 else 1.0
                sy = float(scale_base[1]) if abs(float(scale_base[1])) > 1.0e-6 else 1.0
                sz = float(scale_base[2]) if abs(float(scale_base[2])) > 1.0e-6 else 1.0
                for value in values[:len(times)]:
                    scaled_values.append((
                        float(value[0]) / sx,
                        float(value[1]) / sy,
                        float(value[2]) / sz,
                    ))
                simplified_times, simplified_values = simplify_track_keyframes(
                    times,
                    scaled_values,
                    epsilon=SCALE_EPSILON,
                )
                if simplified_times:
                    track.scale_times = simplified_times
                    track.scale_values = simplified_values

            clip_length = max(clip_length, max(times))

        filtered_tracks = [
            track
            for track in tracks_by_part.values()
            if track.translation_times or track.rotation_times or track.scale_times
        ]

        if filtered_tracks:
            clips.append(AnimationClipDef(
                name=clip_name,
                length=max(clip_length, 0.001),
                tracks=filtered_tracks,
            ))

    return clips


def pack_texture_and_emit_parts(creature_id: str, reader: GltfReader, mesh_fallback: str) -> tuple[CreatureModelDef, Image.Image, CreatureModelDef | None, Image.Image | None, dict[str, Any]]:
    nodes = reader.root.get("nodes", [])
    scenes = reader.root.get("scenes", [])
    scene_index = reader.root.get("scene", 0) if scenes else None

    child_indices = set()
    for node in nodes:
        child_indices.update(node.get("children", []))

    if scene_index is not None:
        top_level = list(scenes[scene_index].get("nodes", []))
    else:
        top_level = [idx for idx in range(len(nodes)) if idx not in child_indices]

    mesh_cache: dict[int, list[MeshCube]] = {}
    all_cubes: list[MeshCube] = []

    for mesh_index, mesh in enumerate(reader.root.get("meshes", [])):
        cube_list: list[MeshCube] = []
        for primitive in mesh.get("primitives", []):
            cube = try_extract_cuboid(reader, primitive, mesh_fallback=mesh_fallback)
            if cube is not None:
                cube_list.append(cube)
                all_cubes.append(cube)
        mesh_cache[mesh_index] = cube_list

    scale_factor = choose_scale_factor(all_cubes)

    layer_assignment = find_layered_nodes(nodes, top_level, mesh_cache)
    has_outer_layer = any(value == "outer" for value in layer_assignment.values())

    base_parts, base_atlas, base_stats, node_to_part_name = build_parts_and_atlas(
        nodes=nodes,
        top_level=top_level,
        mesh_cache=mesh_cache,
        scale_factor=scale_factor,
        layer_assignment=layer_assignment,
        layer_filter="base" if has_outer_layer else None,
        deformation=0.0,
    )
    outer_parts: list[PartDef] | None = None
    outer_atlas: Image.Image | None = None
    outer_stats: dict[str, Any] | None = None
    if has_outer_layer:
        outer_parts, outer_atlas, outer_stats, _ = build_parts_and_atlas(
            nodes=nodes,
            top_level=top_level,
            mesh_cache=mesh_cache,
            scale_factor=scale_factor,
            layer_assignment=layer_assignment,
            layer_filter="outer",
            deformation=0.25,
        )

    clips = select_animation_clips(
        extract_animation_clips(reader, nodes, node_to_part_name, scale_factor)
    )
    animation_names = [clip.name for clip in clips]
    notes = []
    if animation_names:
        notes.append("GLTF animations detected and wired into the generated model.")
        notes.append("Available clips: " + ", ".join(animation_names))

    preferred_idle = choose_named_clip(clips, ("idle", "base", "pose", "stand"))
    preferred_walk = choose_named_clip(clips, ("walk", "run", "move"))
    if preferred_idle is None and clips:
        preferred_idle = clips[0].name

    base_model = CreatureModelDef(
        creature_id=creature_id,
        class_name=f"{snake_to_pascal(creature_id)}Model",
        texture_width=base_atlas.width,
        texture_height=base_atlas.height,
        parts=base_parts,
        animation_notes=list(notes),
        animation_clips=clips,
        preferred_idle_clip=preferred_idle,
        preferred_walk_clip=preferred_walk,
    )

    outer_model = None
    if outer_parts is not None and outer_atlas is not None and outer_stats is not None and outer_stats["cubes"] > 0:
        outer_model = CreatureModelDef(
            creature_id=creature_id,
            class_name=f"{snake_to_pascal(creature_id)}OuterModel",
            texture_width=outer_atlas.width,
            texture_height=outer_atlas.height,
            parts=outer_parts,
            animation_notes=list(notes),
            animation_clips=clips,
            preferred_idle_clip=preferred_idle,
            preferred_walk_clip=preferred_walk,
        )

    report = {
        "creature_id": creature_id,
        "scale_factor": scale_factor,
        "parts": len(base_parts),
        "cubes": base_stats["cubes"],
        "approximated_meshes": base_stats["approximated_meshes"],
        "has_outer_layer": has_outer_layer,
        "outer_cubes": 0 if outer_stats is None else outer_stats["cubes"],
        "animation_clips": animation_names,
    }
    return base_model, base_atlas, outer_model, outer_atlas, report


def estimate_hitbox(parts: list[PartDef]) -> tuple[float, float]:
    min_x = 0.0
    max_x = 0.0
    min_y = 0.0
    max_y = 0.0

    pivot_by_name = {part.name: part.pivot for part in parts}

    for part in parts:
        px, py, _ = pivot_by_name[part.name]
        for cube in part.cubes:
            min_x = min(min_x, px + cube.x)
            max_x = max(max_x, px + cube.x + cube.dx)
            min_y = min(min_y, py + cube.y)
            max_y = max(max_y, py + cube.y + cube.dy)

    width = max(0.4, min(4.0, (max_x - min_x) / 16.0))
    height = max(0.4, min(4.0, (max_y - min_y) / 16.0))
    return round(width, 3), round(height, 3)


def convert_gltf_creature(creature_id: str, gltf_path: Path, mesh_fallback: str = "bounds") -> ConversionResult:
    reader = GltfReader(gltf_path)
    model, atlas, outer_model, outer_atlas, report = pack_texture_and_emit_parts(creature_id, reader, mesh_fallback=mesh_fallback)
    hitbox_w, hitbox_h = estimate_hitbox(model.parts)
    model.hitbox_width = hitbox_w
    model.hitbox_height = hitbox_h
    if outer_model is not None:
        outer_model.hitbox_width = hitbox_w
        outer_model.hitbox_height = hitbox_h
    return ConversionResult(model=model, atlas=atlas, outer_model=outer_model, outer_atlas=outer_atlas, report=report)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--creature-id", required=True)
    parser.add_argument("--gltf", required=True)
    parser.add_argument("--output-java", required=True)
    parser.add_argument("--output-texture", required=True)
    parser.add_argument("--output-outer-java")
    parser.add_argument("--output-outer-texture")
    parser.add_argument("--mesh-fallback", default="bounds", choices=["bounds", "skip"])
    args = parser.parse_args()

    result = convert_gltf_creature(
        creature_id=args.creature_id,
        gltf_path=Path(args.gltf),
        mesh_fallback=args.mesh_fallback,
    )

    output_java = Path(args.output_java)
    output_java.parent.mkdir(parents=True, exist_ok=True)
    output_java.write_text(emit_model_java(result.model), encoding="utf-8")

    output_texture = Path(args.output_texture)
    output_texture.parent.mkdir(parents=True, exist_ok=True)
    result.atlas.save(output_texture)

    if result.outer_model is not None and result.outer_atlas is not None and args.output_outer_java and args.output_outer_texture:
        output_outer_java = Path(args.output_outer_java)
        output_outer_java.parent.mkdir(parents=True, exist_ok=True)
        output_outer_java.write_text(emit_model_java(result.outer_model), encoding="utf-8")

        output_outer_texture = Path(args.output_outer_texture)
        output_outer_texture.parent.mkdir(parents=True, exist_ok=True)
        result.outer_atlas.save(output_outer_texture)

    print(json.dumps(result.report, indent=2))


if __name__ == "__main__":
    main()