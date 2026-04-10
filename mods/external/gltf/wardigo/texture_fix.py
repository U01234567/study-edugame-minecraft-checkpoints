from __future__ import annotations

import base64
import json
import math
import struct
from collections import defaultdict
from pathlib import Path

from PIL import Image

GLTF_PATH = Path("./source/model.gltf")
SOURCE_TEXTURE_PATH = Path("./textures/gltf_embedded_0.png")
OUTPUT_TEXTURE_PATH = Path("./model.png")

SCALE_FACTOR = 16.0
ATLAS_WIDTH = 512
ATLAS_HEIGHT = 1024

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


def primitive_vertices(root: dict, buffers: list[bytes], primitive: dict) -> tuple[list[tuple[float, float, float]], list[tuple[float, float]]]:
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


def extract_mesh_cubes(root: dict, buffers: list[bytes]) -> dict[int, list[tuple[tuple[int, int, int], dict[str, tuple[float, float, float, float]]]]]:
    mesh_cache: dict[int, list[tuple[tuple[int, int, int], dict[str, tuple[float, float, float, float]]]]] = {}

    for mesh_index, mesh in enumerate(root.get("meshes", [])):
        cubes: list[tuple[tuple[int, int, int], dict[str, tuple[float, float, float, float]]]] = []
        for primitive in mesh.get("primitives", []):
            positions, texcoords = primitive_vertices(root, buffers, primitive)
            if not positions:
                continue

            xs = [position[0] for position in positions]
            ys = [position[1] for position in positions]
            zs = [position[2] for position in positions]
            local_min = (min(xs), min(ys), min(zs))
            local_max = (max(xs), max(ys), max(zs))

            unique_x = {quantized(value) for value in xs}
            unique_y = {quantized(value) for value in ys}
            unique_z = {quantized(value) for value in zs}
            if not (len(unique_x) <= 2 and len(unique_y) <= 2 and len(unique_z) <= 2):
                raise ValueError(f"Mesh {mesh_index} is not an exact cuboid. Wardigo should not hit this path.")

            faces: dict[str, list[tuple[float, float]]] = defaultdict(list)
            for tri_index in range(0, len(positions) - 2, 3):
                tri_positions = positions[tri_index:tri_index + 3]
                tri_uvs = texcoords[tri_index:tri_index + 3]

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

            source_faces: dict[str, tuple[float, float, float, float]] = {}
            for face_name, uv_points in faces.items():
                us = [uv[0] for uv in uv_points]
                vs = [uv[1] for uv in uv_points]
                source_faces[face_name] = (min(us), min(vs), max(us), max(vs))

            dims = (
                max(1, round((local_max[0] - local_min[0]) * SCALE_FACTOR)),
                max(1, round((local_max[1] - local_min[1]) * SCALE_FACTOR)),
                max(1, round((local_max[2] - local_min[2]) * SCALE_FACTOR)),
            )
            cubes.append((dims, source_faces))
        mesh_cache[mesh_index] = cubes

    return mesh_cache


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def crop_to_alpha_bounds(image: Image.Image) -> Image.Image:
    alpha = image.getchannel("A")
    bbox = alpha.getbbox()
    if bbox is None:
        return Image.new("RGBA", (1, 1), (255, 255, 255, 255))
    return image.crop(bbox)


def crop_face(image: Image.Image, rect: tuple[float, float, float, float] | None) -> Image.Image:
    if rect is None:
        return crop_to_alpha_bounds(image)

    min_u, min_v, max_u, max_v = rect
    width, height = image.size

    left = max(0, min(width - 1, math.floor(min_u * width)))
    right = max(left + 1, min(width, math.ceil(max_u * width)))
    top = max(0, min(height - 1, math.floor((1.0 - max_v) * height)))
    bottom = max(top + 1, min(height, math.ceil((1.0 - min_v) * height)))

    patch = image.crop((left, top, right, bottom))
    return crop_to_alpha_bounds(patch)


def build_model_png() -> None:
    root, buffers = load_gltf(GLTF_PATH)
    mesh_cache = extract_mesh_cubes(root, buffers)

    # The sidecar texture is vertically flipped relative to the image embedded in model.gltf.
    # Flip it back here, then use the same UV-to-pixel math as the converter.
    source_image = Image.open(SOURCE_TEXTURE_PATH).convert("RGBA").transpose(Image.FLIP_TOP_BOTTOM)
    atlas = Image.new("RGBA", (ATLAS_WIDTH, ATLAS_HEIGHT), (0, 0, 0, 0))

    scenes = root.get("scenes", [])
    scene_index = root.get("scene", 0) if scenes else None
    if scene_index is None:
        raise ValueError("Wardigo GLTF is missing a default scene.")
    top_level = list(scenes[scene_index].get("nodes", []))

    shelf_u = 0
    shelf_v = 0
    shelf_h = 0

    def visit(node_index: int) -> None:
        nonlocal shelf_u, shelf_v, shelf_h

        node = root["nodes"][node_index]
        mesh_index = node.get("mesh", -1)
        for (dx, dy, dz), source_faces in mesh_cache.get(mesh_index, []):
            tile_w = 2 * (dx + dz)
            tile_h = dy + dz
            if shelf_u + tile_w > ATLAS_WIDTH:
                shelf_u = 0
                shelf_v += shelf_h + 2
                shelf_h = 0

            if shelf_v + tile_h > ATLAS_HEIGHT:
                raise ValueError("Packed texture exceeds the fixed atlas used by wardigo/model.java.")

            layout = face_layout(shelf_u, shelf_v, dx, dy, dz)
            for face_name, (target_u, target_v, target_w, target_h) in layout.items():
                patch = crop_face(source_image, source_faces.get(face_name))
                patch = patch.resize((max(1, target_w), max(1, target_h)), Image.NEAREST)
                atlas.paste(patch, (target_u, target_v))

            shelf_u += tile_w + 2
            shelf_h = max(shelf_h, tile_h)

        for child_index in node.get("children", []):
            visit(child_index)

    for node_index in top_level:
        visit(node_index)

    atlas.save(OUTPUT_TEXTURE_PATH)
    print(f"Wrote {OUTPUT_TEXTURE_PATH} ({ATLAS_WIDTH}x{ATLAS_HEIGHT})")


if __name__ == "__main__":
    build_model_png()