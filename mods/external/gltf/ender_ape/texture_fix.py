# Build ./model.png from ./textures/gltf_embedded_0.png for the ender_ape blockbench package.
# Requires Pillow: pip install pillow

from pathlib import Path

from PIL import Image


SOURCE_TEXTURE = Path("./textures/gltf_embedded_0.png")
OUTPUT_TEXTURE = Path("./model.png")
ATLAS_SIZE = (128, 64)


def face_layout(origin_u: int, origin_v: int, dx: int, dy: int, dz: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "west": (origin_u, origin_v + dz, dz, dy),
        "north": (origin_u + dz, origin_v + dz, dx, dy),
        "east": (origin_u + dz + dx, origin_v + dz, dz, dy),
        "south": (origin_u + dz + dx + dz, origin_v + dz, dx, dy),
        "up": (origin_u + dz, origin_v, dx, dz),
        "down": (origin_u + dz + dx, origin_v, dx, dz),
    }


CUBES = {
    "body_main": {
        "dims": (12, 16, 8),
        "origin": (0, 0),
        "source": {
            "west": (8, 32, 16, 48),
            "north": (0, 48, 12, 64),
            "east": (0, 32, 8, 48),
            "south": (12, 48, 24, 64),
            "up": (16, 40, 28, 48),
            "down": (24, 56, 36, 64),
        },
    },
    "head_main": {
        "dims": (8, 8, 8),
        "origin": (40, 0),
        "source": {
            "west": (28, 40, 36, 48),
            "north": (24, 48, 32, 56),
            "east": (16, 32, 24, 40),
            "south": (24, 32, 32, 40),
            "up": (0, 24, 8, 32),
            "down": (8, 24, 16, 32),
        },
    },
    "headshooter_main": {
        "dims": (6, 4, 6),
        "origin": (72, 0),
        "source": {
            "west": (6, 20, 12, 24),
            "north": (0, 20, 6, 24),
            "east": (40, 60, 46, 64),
            "south": (40, 56, 46, 60),
            "up": (32, 22, 38, 28),
            "down": (38, 22, 44, 28),
        },
    },
    "headshooter_core": {
        "dims": (2, 2, 2),
        "origin": (96, 0),
        "source": {
            "west": (2, 18, 4, 20),
            "north": (40, 20, 42, 22),
            "east": (42, 20, 44, 22),
            "south": (0, 18, 2, 20),
            "up": (4, 18, 6, 20),
            "down": (6, 18, 8, 20),
        },
    },
    "left_ear": {
        "dims": (1, 4, 4),
        "origin": (104, 0),
        "source": {
            "west": (32, 48, 36, 52),
            "north": (8, 16, 9, 20),
            "east": (32, 52, 36, 56),
            "south": (44, 52, 45, 56),
            "up": (9, 16, 10, 20),
            "down": (10, 16, 11, 20),
        },
    },
    "right_ear": {
        "dims": (1, 4, 4),
        "origin": (104, 8),
        "source": {
            "west": (12, 20, 16, 24),
            "north": (11, 16, 12, 20),
            "east": (40, 52, 44, 56),
            "south": (12, 16, 13, 20),
            "up": (44, 48, 45, 52),
            "down": (13, 16, 14, 20),
        },
    },
    "left_hand_main": {
        "dims": (4, 12, 4),
        "origin": (0, 24),
        "source": {
            "west": (32, 28, 36, 40),
            "north": (16, 20, 20, 32),
            "east": (20, 20, 24, 32),
            "south": (24, 20, 28, 32),
            "up": (40, 48, 44, 52),
            "down": (40, 44, 44, 48),
        },
    },
    "right_hand_main": {
        "dims": (4, 12, 4),
        "origin": (16, 24),
        "source": {
            "west": (36, 28, 40, 40),
            "north": (28, 20, 32, 32),
            "east": (36, 52, 40, 64),
            "south": (36, 40, 40, 52),
            "up": (40, 40, 44, 44),
            "down": (40, 36, 44, 40),
        },
    },
    "left_foot_main": {
        "dims": (2, 4, 2),
        "origin": (32, 24),
        "source": {
            "west": (32, 18, 34, 22),
            "north": (40, 32, 42, 36),
            "east": (40, 28, 42, 32),
            "south": (42, 32, 44, 36),
            "up": (14, 18, 16, 20),
            "down": (16, 18, 18, 20),
        },
    },
    "right_foot_main": {
        "dims": (2, 4, 2),
        "origin": (40, 24),
        "source": {
            "west": (38, 18, 40, 22),
            "north": (42, 28, 44, 32),
            "east": (34, 18, 36, 22),
            "south": (36, 18, 38, 22),
            "up": (44, 46, 46, 48),
            "down": (18, 18, 20, 20),
        },
    },
}


def main() -> None:
    if not SOURCE_TEXTURE.exists():
        raise SystemExit(f"Missing source texture: {SOURCE_TEXTURE}")

    source = Image.open(SOURCE_TEXTURE).convert("RGBA")
    atlas = Image.new("RGBA", ATLAS_SIZE, (0, 0, 0, 0))

    for cube in CUBES.values():
        dx, dy, dz = cube["dims"]
        origin_u, origin_v = cube["origin"]
        layout = face_layout(origin_u, origin_v, dx, dy, dz)

        for face_name, target in layout.items():
            target_u, target_v, target_w, target_h = target
            src_left, src_top, src_right, src_bottom = cube["source"][face_name]
            patch = source.crop((src_left, src_top, src_right, src_bottom))

            if patch.size != (target_w, target_h):
                patch = patch.resize((target_w, target_h), Image.NEAREST)

            atlas.paste(patch, (target_u, target_v))

    OUTPUT_TEXTURE.parent.mkdir(parents=True, exist_ok=True)
    atlas.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE} ({atlas.width}x{atlas.height})")


if __name__ == "__main__":
    main()