#!/usr/bin/env python3
"""
cave_dweller texture fixer

This creature cannot use ./textures/gltf_embedded_0.png unchanged with box UVs.
Run this script inside the creature folder to produce ./model.png.
It verifies the expected source texture first and then writes the precomputed atlas
used by model.java.
"""

from __future__ import annotations

import base64
import hashlib
from pathlib import Path
import sys

EXPECTED = {
    "textures/gltf_embedded_0.png": ("de4616299ac77720cde5eeb060b6aae2dafbb9f5885c67cc484b0f0cadfff758", 1376),
}

MODEL_PNG_BASE64 = """iVBORw0KGgoAAAANSUhEUgAAAgAAAAAQCAYAAABgMcdIAAACAUlEQVR4nO3cMU/CQBTAcargpl/BxFkXEldHJxkcjIObYfbDMBM2BicGmRxdTVxkMyFhdpORGBzMg3Lc0V577V3p/zfJu7u+V2x710JoNAyWClO/JHm2Yzs2Sy51TJqxpj6m+Hgy08bn075VfFfdLut9eryyqiupFlftruJl1WH6vxf9fun62PQtOh+AwLk6kfNsx3Zslly2Fy5pVydDudircXltiqv5lj/v2v5Z93U+7S+ltvFktuonk/z31/PGeImriwDTfku/+Bib9qT331XcRx269z0pz12nvYo93La32jvXFzvHp80j4jmS9i+eO2s+AOFo+i6gaqIoitavuqu/bs5PtfHjs260fm2Kx7Z/cqndTlb/ebb1Bm9RbyBN99r4ut28363WYa76Nrebvt02nperOoqqTyRNutLuqo6y8wFwJ7gFwK4LSrxNLii2/VGuvAuEIoRYkzjKeUaWfZxzXgHVFdwCoGhpH0uq/Xxf6NLeaQnf9YrF4td3CQAAjdotAKoqlAkdALAfmlnviEWaR/E227Gd6JgY/ariHX4VawYA1w58FwAAAMpXu48AeGKAkA1HH9EwdogOlcP15fXT6SGs5lOpyTh9gP3BEwAUKuRv3ANAnbEAAACghlgAoFB84Q4AwtTkM3EAAOrH2eSf93e/WYgAAAAAAAAAAODSH3TkYNG7Fp4cAAAAAElFTkSuQmCC"""

def sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()

def main() -> int:
    root = Path(__file__).resolve().parent

    for rel_path, (expected_hash, expected_size) in EXPECTED.items():
        path = root / rel_path
        if not path.exists():
            print(f"Missing required source texture: {path}", file=sys.stderr)
            return 1

        actual_size = path.stat().st_size
        actual_hash = sha256_file(path)
        if actual_size != expected_size or actual_hash != expected_hash:
            print(
                f"Source texture mismatch for {path}\n"
                f"  expected size/hash: {expected_size} / {expected_hash}\n"
                f"  actual size/hash:   {actual_size} / {actual_hash}",
                file=sys.stderr,
            )
            return 1

    output_path = root / "model.png"
    output_path.write_bytes(base64.b64decode(MODEL_PNG_BASE64))
    print(f"Wrote {output_path}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())