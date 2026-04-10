from __future__ import annotations

from pathlib import Path

from PIL import Image

SOURCE_TEXTURE = Path("./textures/gltf_embedded_0.png")
OUTPUT_TEXTURE = Path("./model.png")
ATLAS_SIZE = (128, 32)

# For mushroom_bup, the face rects line up with the sidecar PNG exactly as written
# in ./textures/gltf_embedded_0.png.
#
# Do NOT vertically flip the source image here.
# The prior version did that, and it caused many crops to become fully transparent,
# which is why the creature appeared see-through in-game.
#
# gltf_embedded_1.png is a fully transparent 1x1 placeholder used by the hitbox
# material and is intentionally ignored.

CUBES = [
    {
        "name": "hat_brim",
        "origin": (0, 0),
        "faces": {
            "west": (24, 48, 38, 52),
            "north": (24, 60, 38, 64),
            "east": (24, 56, 38, 60),
            "south": (24, 52, 38, 56),
            "up": (0, 50, 14, 64),
            "down": (0, 36, 14, 50),
        },
    },
    {
        "name": "hat_cap",
        "origin": (58, 0),
        "faces": {
            "west": (36, 36, 46, 38),
            "north": (36, 42, 46, 44),
            "east": (36, 40, 46, 42),
            "south": (36, 38, 46, 40),
            "up": (14, 54, 24, 64),
            "down": (14, 44, 24, 54),
        },
    },
    {
        "name": "head_cube",
        "origin": (100, 0),
        "faces": {
            "west": (19, 24, 25, 28),
            "north": (0, 26, 6, 30),
            "east": (6, 26, 12, 30),
            "south": (36, 44, 42, 48),
            "up": (30, 42, 36, 48),
            "down": (30, 36, 36, 42),
        },
    },
    {
        "name": "torso_cube",
        "origin": (0, 20),
        "faces": {
            "west": (12, 27, 19, 32),
            "north": (14, 32, 22, 37),
            "east": (30, 31, 37, 36),
            "south": (22, 32, 30, 37),
            "up": (14, 37, 22, 44),
            "down": (22, 37, 30, 44),
        },
    },
    {
        "name": "left_leg_cube",
        "origin": (32, 20),
        "faces": {
            "west": (38, 60, 41, 62),
            "north": (35, 25, 38, 27),
            "east": (0, 24, 3, 26),
            "south": (38, 62, 41, 64),
            "up": (16, 24, 19, 27),
            "down": (37, 33, 40, 36),
        },
    },
    {
        "name": "right_leg_cube",
        "origin": (46, 20),
        "faces": {
            "west": (9, 24, 12, 26),
            "north": (6, 24, 9, 26),
            "east": (38, 56, 41, 58),
            "south": (38, 54, 41, 56),
            "up": (29, 24, 32, 27),
            "down": (37, 30, 40, 33),
        },
    },
    {
        "name": "left_arm_cube",
        "origin": (60, 20),
        "faces": {
            "west": (38, 58, 41, 60),
            "north": (36, 23, 37, 25),
            "east": (3, 24, 6, 26),
            "south": (37, 23, 38, 25),
            "up": (12, 33, 13, 36),
            "down": (13, 33, 14, 36),
        },
    },
    {
        "name": "right_arm_cube",
        "origin": (70, 20),
        "faces": {
            "west": (37, 27, 40, 30),
            "north": (38, 48, 39, 51),
            "east": (32, 24, 35, 27),
            "south": (38, 24, 39, 27),
            "up": (39, 48, 40, 51),
            "down": (35, 22, 36, 25),
        },
    },
    {
        "name": "hat_top",
        "origin": (80, 20),
        "faces": {
            "west": (38, 51, 42, 52),
            "north": (25, 31, 29, 32),
            "east": (38, 53, 42, 54),
            "south": (38, 52, 42, 53),
            "up": (12, 23, 16, 27),
            "down": (25, 23, 29, 27),
        },
    },
    {
        "name": "left_foot_cube",
        "origin": (98, 20),
        "faces": {
            "west": (40, 49, 41, 50),
            "north": (3, 23, 6, 24),
            "east": (29, 31, 30, 32),
            "south": (6, 23, 9, 24),
            "up": (9, 23, 12, 24),
            "down": (40, 50, 43, 51),
        },
    },
    {
        "name": "right_foot_cube",
        "origin": (108, 20),
        "faces": {
            "west": (13, 32, 14, 33),
            "north": (39, 26, 42, 27),
            "east": (12, 32, 13, 33),
            "south": (39, 25, 42, 26),
            "up": (39, 24, 42, 25),
            "down": (0, 23, 3, 24),
        },
    },
]


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


def crop_rect(image: Image.Image, rect: tuple[int, int, int, int]) -> Image.Image:
    return image.crop(rect)


def has_any_visible_pixel(image: Image.Image) -> bool:
    return any(pixel[3] > 0 for pixel in image.getdata())


def main() -> None:
    source = Image.open(SOURCE_TEXTURE).convert("RGBA")

    if source.size != (64, 64):
        raise ValueError(f"Expected {SOURCE_TEXTURE} to be 64x64, got {source.size}")

    atlas = Image.new("RGBA", ATLAS_SIZE, (0, 0, 0, 0))

    for cube in CUBES:
        west = cube["faces"]["west"]
        north = cube["faces"]["north"]

        dx = max(1, north[2] - north[0])
        dy = max(1, north[3] - north[1])
        dz = max(1, west[2] - west[0])

        layout = face_layout(cube["origin"][0], cube["origin"][1], dx, dy, dz)

        for face_name, target in layout.items():
            patch = crop_rect(source, cube["faces"][face_name])

            if not has_any_visible_pixel(patch):
                raise ValueError(
                    f"{cube['name']}:{face_name} cropped fully transparent pixels. "
                    "For mushroom_bup this means the source texture orientation is wrong."
                )

            target_u, target_v, target_w, target_h = target
            if patch.size != (target_w, target_h):
                patch = patch.resize((target_w, target_h), Image.NEAREST)

            atlas.paste(patch, (target_u, target_v), patch)

    atlas.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE} ({ATLAS_SIZE[0]}x{ATLAS_SIZE[1]})")


if __name__ == "__main__":
    main()