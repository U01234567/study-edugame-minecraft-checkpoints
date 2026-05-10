from __future__ import annotations

import csv
import json
from pathlib import Path

from ._shared import SURVEY_EXPORT_PATH, clean


def detect_text_encoding(path: Path) -> str:
    """Detect the small set of encodings Qualtrics exports normally use."""
    with path.open("rb") as handle:
        start = handle.read(4)

    if start.startswith((b"\xff\xfe", b"\xfe\xff")):
        return "utf-16"
    if start.startswith(b"\xef\xbb\xbf"):
        return "utf-8-sig"
    return "utf-8"


def extract_import_id(value: object) -> str | None:
    """Extract a Qualtrics ImportId from a metadata cell, when present."""
    text = clean(value)
    if '"ImportId"' not in text:
        return None

    try:
        payload = json.loads(text)
    except json.JSONDecodeError:
        return None

    import_id = clean(payload.get("ImportId"))
    return import_id or None


def looks_like_qualtrics_label_row(row: list[str]) -> bool:
    """Heuristically detect the Qualtrics question-label row."""
    if not row:
        return False

    first_cell = clean(row[0]).lower()
    return first_cell in {"start date", "startdate"} or any(" - " in clean(cell) for cell in row[:80])


def normalise_headers(raw_header: list[str], labels: list[str], import_row: list[str]) -> list[str]:
    """Prefer stable Qualtrics export IDs over generated QID labels where possible."""
    normalised: list[str] = []

    for index, raw_name in enumerate(raw_header):
        name = clean(raw_name)
        label = clean(labels[index]) if index < len(labels) else ""
        import_id = extract_import_id(import_row[index]) if index < len(import_row) else None

        if import_id:
            if name and not name.startswith("QID"):
                normalised.append(name if import_id.startswith("QID") else import_id)
                continue

            normalised.append(import_id)
            continue

        if name.startswith("QID") and " - " in label:
            custom_prefix = clean(label.split(" - ", 1)[0])
            if custom_prefix:
                normalised.append(custom_prefix)
                continue

        normalised.append(name)

    return normalised


def load_tsv(path: Path) -> tuple[list[dict[str, str]], list[str]]:
    """Load a TSV export while skipping optional Qualtrics metadata rows."""
    encoding = detect_text_encoding(path)

    with path.open("r", encoding=encoding, newline="") as handle:
        raw_rows = list(csv.reader(handle, delimiter="\t"))

    if not raw_rows:
        return [], []

    has_label_row = len(raw_rows) > 1 and looks_like_qualtrics_label_row(raw_rows[1])

    has_import_row = False
    if len(raw_rows) > 2:
        import_cells = sum(1 for cell in raw_rows[2] if '"ImportId"' in clean(cell))
        has_import_row = import_cells >= max(3, len(raw_rows[0]) // 4)

    labels = raw_rows[1] if has_label_row else []
    import_row = raw_rows[2] if has_import_row else []
    header = normalise_headers(raw_rows[0], labels, import_row)

    data_start_index = 3 if has_import_row else 2 if has_label_row else 1

    rows: list[dict[str, str]] = []
    for raw_row in raw_rows[data_start_index:]:
        if not any(clean(cell) for cell in raw_row):
            continue

        padded = raw_row + [""] * max(0, len(header) - len(raw_row))
        rows.append({header[column_index]: padded[column_index] for column_index in range(len(header))})

    return rows, header


def load_survey_export(path: Path = SURVEY_EXPORT_PATH) -> tuple[list[dict[str, str]], list[str]]:
    """Load the one survey export used by the merged app: data/survey_export.tsv."""
    if not path.exists():
        raise FileNotFoundError(f"Expected survey export at {path}")

    return load_tsv(path)