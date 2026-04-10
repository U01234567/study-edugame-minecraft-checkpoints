from __future__ import annotations

from pathlib import Path

from PIL import Image


BASE_DIR = Path(__file__).resolve().parent
SOURCE_TEXTURE = BASE_DIR / "textures" / "gltf_embedded_0.png"
OUTPUT_TEXTURE = BASE_DIR / "model.png"


def build_runtime_texture() -> None:
    if not SOURCE_TEXTURE.exists():
        raise FileNotFoundError(
            f"Missing source texture: {SOURCE_TEXTURE}\n"
            "Expected you to copy the original /textures folder beside this script."
        )

    # The sidecar PNG in the creature zip is vertically flipped relative to the
    # image embedded inside source/model.gltf. For this creature, a direct
    # top-to-bottom flip is sufficient; no atlas rebuild is needed.
    image = Image.open(SOURCE_TEXTURE).convert("RGBA")
    fixed = image.transpose(Image.Transpose.FLIP_TOP_BOTTOM)
    fixed.save(OUTPUT_TEXTURE)

    print(f"Wrote {OUTPUT_TEXTURE} ({fixed.width}x{fixed.height})")


if __name__ == "__main__":
    build_runtime_texture()