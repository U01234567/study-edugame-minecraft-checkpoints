from __future__ import annotations

import base64
import json
from pathlib import Path

import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parent
GLTF_PATH = ROOT / "source" / "model.gltf"
SOURCE_TEXTURE_PATH = ROOT / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE_PATH = ROOT / "model.png"

ATLAS_WIDTH = 512
ATLAS_HEIGHT = 128

# One entry per generated addBox(...) in the existing GrandGrasslingFather model.
# Each tuple is: (tex_u, tex_v, size_x, size_y, size_z)
BOX_LAYOUTS = [
    (0, 0, 6, 11, 6),
    (26, 0, 6, 11, 6),
    (52, 0, 18, 17, 19),
    (128, 0, 6, 20, 6),
    (154, 0, 6, 8, 6),
    (180, 0, 6, 11, 1),
    (196, 0, 1, 8, 6),
    (212, 0, 6, 11, 1),
    (228, 0, 2, 2, 2),
    (238, 0, 1, 10, 6),
    (254, 0, 1, 10, 6),
    (270, 0, 1, 8, 6),
    (286, 0, 1, 8, 6),
    (302, 0, 1, 8, 6),
    (318, 0, 1, 8, 6),
    (334, 0, 1, 6, 2),
    (342, 0, 1, 6, 2),
    (350, 0, 1, 6, 2),
    (358, 0, 1, 6, 2),
    (366, 0, 1, 6, 2),
    (374, 0, 1, 6, 2),
    (382, 0, 8, 8, 9),
    (418, 0, 1, 4, 5),
    (432, 0, 4, 4, 1),
    (444, 0, 4, 2, 1),
    (456, 0, 1, 4, 5),
    (470, 0, 1, 2, 2),
    (0, 38, 76, 1, 76),
    (306, 38, 8, 32, 1),
    (326, 38, 8, 32, 1),
    (346, 38, 8, 32, 1),
    (366, 38, 8, 32, 1),
    (386, 38, 8, 32, 1),
    (406, 38, 8, 32, 1),
    (426, 38, 8, 25, 1),
    (446, 38, 8, 25, 1),
    (466, 38, 8, 25, 1),
    (486, 38, 8, 25, 1),
    (0, 117, 6, 7, 1),
    (16, 117, 6, 7, 1),
    (32, 117, 6, 7, 1),
    (48, 117, 6, 7, 1),
    (64, 117, 6, 7, 1),
    (80, 117, 6, 7, 1),
    (96, 117, 6, 7, 1),
    (112, 117, 6, 7, 1),
    (128, 117, 6, 7, 1),
    (144, 117, 6, 7, 1),
    (160, 117, 6, 7, 1),
    (176, 117, 6, 7, 1),
    (192, 117, 6, 7, 1),
    (208, 117, 6, 7, 1),
]

FACE_TO_REGION = {
    (0, -1): "left",    # -X
    (0, 1): "right",    # +X
    (1, 1): "top",      # +Y
    (1, -1): "bottom",  # -Y
    (2, -1): "front",   # -Z
    (2, 1): "back",     # +Z
}


def load_gltf(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def decode_buffer(gltf: dict) -> bytes:
    buffers = gltf.get("buffers", [])
    if len(buffers) != 1:
        raise ValueError(f"Expected exactly 1 buffer, found {len(buffers)}.")
    uri = buffers[0].get("uri", "")
    if not uri.startswith("data:"):
        raise ValueError("This script expects an embedded data URI buffer in model.gltf.")
    return base64.b64decode(uri.split(",", 1)[1])


def read_accessor(gltf: dict, blob: bytes, accessor_index: int) -> np.ndarray:
    accessor = gltf["accessors"][accessor_index]
    buffer_view = gltf["bufferViews"][accessor["bufferView"]]
    offset = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
    count = accessor["count"]
    component_type = accessor["componentType"]
    accessor_type = accessor["type"]

    components_per_element = {
        "SCALAR": 1,
        "VEC2": 2,
        "VEC3": 3,
        "VEC4": 4,
        "MAT4": 16,
    }[accessor_type]
    dtype = {
        5121: np.uint8,
        5123: np.uint16,
        5125: np.uint32,
        5126: np.float32,
    }[component_type]

    flat = np.frombuffer(
        blob,
        dtype=dtype,
        count=count * components_per_element,
        offset=offset,
    )
    return flat.reshape(count, components_per_element)


def mesh_face_uv_boxes(
    gltf: dict,
    blob: bytes,
    mesh_index: int,
    image_size: tuple[int, int],
) -> dict[tuple[int, int], tuple[int, int, int, int]]:
    primitive = gltf["meshes"][mesh_index]["primitives"][0]
    positions = read_accessor(gltf, blob, primitive["attributes"]["POSITION"])
    texcoords = read_accessor(gltf, blob, primitive["attributes"]["TEXCOORD_0"])
    indices = read_accessor(gltf, blob, primitive["indices"]).reshape(-1)

    mins = positions.min(axis=0)
    maxs = positions.max(axis=0)
    epsilon = 1.0e-5

    faces: dict[tuple[int, int], list[np.ndarray]] = {}

    for tri in indices.reshape(-1, 3):
        tri_positions = positions[tri]
        tri_uvs = texcoords[tri]

        face_key = None
        for axis in range(3):
            if np.allclose(tri_positions[:, axis], mins[axis], atol=epsilon):
                face_key = (axis, -1)
                break
            if np.allclose(tri_positions[:, axis], maxs[axis], atol=epsilon):
                face_key = (axis, 1)
                break

        if face_key is None:
            axis = int(np.argmin(np.ptp(tri_positions, axis=0)))
            sign = -1 if abs(float(tri_positions[0, axis] - mins[axis])) < abs(float(tri_positions[0, axis] - maxs[axis])) else 1
            face_key = (axis, sign)

        faces.setdefault(face_key, []).append(tri_uvs)

    width, height = image_size
    face_uv_boxes: dict[tuple[int, int], tuple[int, int, int, int]] = {}

    for face_key, uv_chunks in faces.items():
        uv = np.concatenate(uv_chunks, axis=0)
        min_u, min_v = uv.min(axis=0)
        max_u, max_v = uv.max(axis=0)

        left = int(round(float(min_u * width)))
        right = int(round(float(max_u * width)))
        top = int(round(float((1.0 - max_v) * height)))
        bottom = int(round(float((1.0 - min_v) * height)))

        if right <= left:
            right = left + 1
        if bottom <= top:
            bottom = top + 1

        face_uv_boxes[face_key] = (left, top, right, bottom)

    return face_uv_boxes


def target_regions(
    tex_u: int,
    tex_v: int,
    size_x: int,
    size_y: int,
    size_z: int,
) -> dict[str, tuple[int, int, int, int]]:
    return {
        "top": (
            tex_u + size_z,
            tex_v,
            tex_u + size_z + size_x,
            tex_v + size_z,
        ),
        "bottom": (
            tex_u + size_z + size_x,
            tex_v,
            tex_u + (2 * size_z) + (2 * size_x),
            tex_v + size_z,
        ),
        "left": (
            tex_u,
            tex_v + size_z,
            tex_u + size_z,
            tex_v + size_z + size_y,
        ),
        "front": (
            tex_u + size_z,
            tex_v + size_z,
            tex_u + size_z + size_x,
            tex_v + size_z + size_y,
        ),
        "right": (
            tex_u + size_z + size_x,
            tex_v + size_z,
            tex_u + (2 * size_z) + size_x,
            tex_v + size_z + size_y,
        ),
        "back": (
            tex_u + (2 * size_z) + size_x,
            tex_v + size_z,
            tex_u + (2 * size_z) + (2 * size_x),
            tex_v + size_z + size_y,
        ),
    }


def paste_resized(
    atlas: Image.Image,
    source: Image.Image,
    source_box: tuple[int, int, int, int],
    dest_box: tuple[int, int, int, int],
) -> None:
    dest_width = max(1, dest_box[2] - dest_box[0])
    dest_height = max(1, dest_box[3] - dest_box[1])
    patch = source.crop(source_box).resize((dest_width, dest_height), Image.NEAREST)
    atlas.paste(patch, dest_box)


def main() -> None:
    if not GLTF_PATH.exists():
        raise FileNotFoundError(
            f"Missing {GLTF_PATH}. Copy your GLTF there as source/model.gltf first."
        )
    if not SOURCE_TEXTURE_PATH.exists():
        raise FileNotFoundError(
            f"Missing {SOURCE_TEXTURE_PATH}. Copy the source texture there first."
        )

    gltf = load_gltf(GLTF_PATH)
    blob = decode_buffer(gltf)
    source_texture = Image.open(SOURCE_TEXTURE_PATH).convert("RGBA")

    mesh_count = len(gltf.get("meshes", []))
    if mesh_count != len(BOX_LAYOUTS):
        raise ValueError(
            f"Mesh/layout mismatch: GLTF has {mesh_count} meshes but BOX_LAYOUTS has {len(BOX_LAYOUTS)} entries."
        )

    atlas = Image.new("RGBA", (ATLAS_WIDTH, ATLAS_HEIGHT), (0, 0, 0, 0))

    for mesh_index, (tex_u, tex_v, size_x, size_y, size_z) in enumerate(BOX_LAYOUTS):
        face_boxes = mesh_face_uv_boxes(gltf, blob, mesh_index, source_texture.size)
        regions = target_regions(tex_u, tex_v, size_x, size_y, size_z)

        for face_key, source_box in face_boxes.items():
            region_name = FACE_TO_REGION.get(face_key)
            if region_name is None:
                continue
            paste_resized(atlas, source_texture, source_box, regions[region_name])

    atlas.save(OUTPUT_TEXTURE_PATH)
    print(f"Wrote {OUTPUT_TEXTURE_PATH}")


if __name__ == "__main__":
    main()