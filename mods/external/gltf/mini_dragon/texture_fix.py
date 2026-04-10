#!/usr/bin/env python3
"""
mini_dragon texture fixer

This creature cannot use any single original source PNG unchanged.
Run this script inside the creature folder to produce ./model.png.
The script verifies the expected source texture files first, then writes
the precomputed merged atlas used by model.java.
"""

from __future__ import annotations

import base64
import hashlib
from pathlib import Path
import sys

EXPECTED = {
    "textures/gltf_embedded_0.png": ("3fade2fe0a70818522e25fe05c25468faef44dcb0698f398b807497fe0dcf049", 5538),
    "textures/gltf_embedded_1.png": ("8c6bd49affa715af6d2757b20916c8bccdef57c190dd5f13cc1221aa5c30e713", 236),
    "textures/gltf_embedded_2.png": ("c63ecfcec16cb503833b1524141c77581d6c169f287040e858ad0abbd288227d", 388),
    "textures/gltf_embedded_3.png": ("8f2302e42aca649ae37f4f2190f03fa253be82b765e3c59a1f196c7c488091fe", 372),
    "textures/gltf_embedded_4.png": ("0856573d6fb8f9fb0076f249cf24f37b4fe5266f3f6e6ca9b4c7aa05ef907930", 772),
    "textures/gltf_embedded_5.png": ("127c4eae0363ed327bbe27e418d4c99b3a5619c07579a24eca81c9cf61dbc3a8", 879),
}

MODEL_PNG_BASE64 = """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
ttGnrKFnPgAAAABJRU5ErkJggg=="""

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