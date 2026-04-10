from pathlib import Path
from PIL import Image


def main() -> None:
    root = Path(__file__).resolve().parent
    base_path = root / "textures" / "gltf_embedded_0.png"
    glow_path = root / "textures" / "deer_glow_layer.png"
    out_path = root / "model.png"

    if not base_path.exists():
        raise FileNotFoundError(f"Missing base texture: {base_path}")
    if not glow_path.exists():
        raise FileNotFoundError(f"Missing glow texture: {glow_path}")

    base = Image.open(base_path).convert("RGBA")
    glow = Image.open(glow_path).convert("RGBA")

    if glow.size != base.size:
        glow = glow.resize(base.size, Image.NEAREST)

    merged = Image.alpha_composite(base, glow)
    merged.save(out_path)

    print(f"Wrote {out_path}")


if __name__ == "__main__":
    main()