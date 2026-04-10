from __future__ import annotations

from pathlib import Path
from statistics import median

from PIL import Image, ImageDraw


BASE_DIR = Path(__file__).resolve().parent
SOURCE_TEXTURE = BASE_DIR / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE = BASE_DIR / "model.png"

TEXTURE_WIDTH = 96
TEXTURE_HEIGHT = 64

# Extra opaque underlay UV blocks used by model.java
BODY_CORE_UV = (66, 0)   # 8x12x4  -> 24x16 box-UV footprint
ARM_CORE_UV = (66, 20)   # 4x14x4  -> 16x18 box-UV footprint
LEG_CORE_UV = (66, 40)   # 4x12x4  -> 16x16 box-UV footprint


def median_opaque_color(
    image: Image.Image,
    box: tuple[int, int, int, int],
    fallback: tuple[int, int, int, int],
) -> tuple[int, int, int, int]:
    crop = image.crop(box).convert("RGBA")
    pixels = [(r, g, b, a) for (r, g, b, a) in crop.getdata() if a >= 128]
    if not pixels:
        return fallback
    return tuple(int(median(channel)) for channel in zip(*pixels))


def fill_box_layout(
    draw: ImageDraw.ImageDraw,
    uv_x: int,
    uv_y: int,
    width: int,
    height: int,
    depth: int,
    color: tuple[int, int, int, int],
) -> None:
    """
    Paint a solid-color Minecraft box UV footprint.

    For a solid inner underlay, filling the full canonical box-UV bounding
    rectangle is sufficient and keeps every face opaque:
      width_px  = depth + width + depth + width
      height_px = depth + height
    """
    total_width = depth + width + depth + width
    total_height = depth + height
    draw.rectangle(
        (uv_x, uv_y, uv_x + total_width - 1, uv_y + total_height - 1),
        fill=color,
    )


def build_runtime_texture() -> None:
    if not SOURCE_TEXTURE.exists():
        raise FileNotFoundError(
            f"Missing source texture: {SOURCE_TEXTURE}\n"
            "Expected you to copy the original /textures folder beside this script."
        )

    # The sidecar PNG in the zip is vertically flipped relative to the image
    # embedded inside source/model.gltf, so flip it first.
    raw = Image.open(SOURCE_TEXTURE).convert("RGBA")
    flipped = raw.transpose(Image.Transpose.FLIP_TOP_BOTTOM)

    # One TV head cap samples 1px past the left edge in the GLTF and relies on
    # clamp-to-edge. Recreate that safely by adding a duplicated 1px left gutter.
    out = Image.new("RGBA", (TEXTURE_WIDTH, TEXTURE_HEIGHT), (0, 0, 0, 0))
    left_column = flipped.crop((0, 0, 1, flipped.height))
    out.paste(left_column, (0, 0))
    out.paste(flipped, (1, 0))

    # Sample dark, already-authored colors from the flipped source texture for
    # opaque inner underlays. These sit just inside the alpha-cut shell so the
    # model no longer reads as hollow at the shoulders / limbs.
    body_color = median_opaque_color(
        flipped,
        (16, 48, 24, 60),   # torso front region
        (28, 28, 28, 255),
    )
    arm_color = median_opaque_color(
        flipped,
        (22, 21, 39, 35),   # arm side/front region
        (24, 24, 24, 255),
    )
    leg_color = median_opaque_color(
        flipped,
        (24, 48, 40, 60),   # leg/body dark lower region
        (22, 22, 22, 255),
    )

    draw = ImageDraw.Draw(out)

    fill_box_layout(draw, BODY_CORE_UV[0], BODY_CORE_UV[1], width=8, height=12, depth=4, color=body_color)
    fill_box_layout(draw, ARM_CORE_UV[0], ARM_CORE_UV[1], width=4, height=14, depth=4, color=arm_color)
    fill_box_layout(draw, LEG_CORE_UV[0], LEG_CORE_UV[1], width=4, height=12, depth=4, color=leg_color)

    out.save(OUTPUT_TEXTURE)
    print(f"Wrote {OUTPUT_TEXTURE} ({out.width}x{out.height})")


if __name__ == "__main__":
    build_runtime_texture()