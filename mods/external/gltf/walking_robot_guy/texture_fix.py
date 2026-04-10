from pathlib import Path

from PIL import Image

try:
    NEAREST = Image.Resampling.NEAREST
except AttributeError:
    NEAREST = Image.NEAREST


def main() -> None:
    source_path = Path("./textures/gltf_embedded_0.png")
    output_path = Path("./model.png")

    if not source_path.exists():
        raise SystemExit(f"Missing source texture: {source_path}")

    image = Image.open(source_path).convert("RGBA")

    if image.size != (128, 128):
        raise SystemExit(
            f"Unexpected source texture size {image.size}; expected (128, 128) for walking_robot_guy"
        )

    # This creature's source texture is a 2x version of the 64x64 box-UV layout
    # expected by the current model.java. The previous package also flipped it
    # vertically, which is what caused the see-through / wrong-pixel result.
    fixed = image.resize((64, 64), NEAREST)

    fixed.save(output_path)
    print(f"Wrote {output_path} from {source_path} ({image.size} -> {fixed.size})")


if __name__ == "__main__":
    main()