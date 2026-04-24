from __future__ import annotations

import csv
import html
import json
import math
from pathlib import Path
from typing import Any


REPO_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = REPO_ROOT / "data"
LOG_DIR = REPO_ROOT / "logs"
OUTPUT_DIR = REPO_ROOT / "output"
OUTPUT_PATH = OUTPUT_DIR / "merged_summary.html"

CONDITION_LABELS = {
    "cond_continue": "Required continue",
    "cond_required_continue": "Required continue",
    "required_continue": "Required continue",
    "continue": "Required continue",

    "cond_pause": "Required pauses",
    "cond_pauses": "Required pauses",
    "cond_required_pauses": "Required pauses",
    "required_pauses": "Required pauses",
    "pause": "Required pauses",

    "cond_choice": "Optional pauses",
    "cond_optional_pauses": "Optional pauses",
    "optional_pauses": "Optional pauses",
    "choice": "Optional pauses",

    "unknown": "Unknown condition",
}

CONDITION_ORDER = [
    "Required continue",
    "Required pauses",
    "Optional pauses",
    "Unknown condition",
]

CL_CHAPTER_LABELS = [
    "Topics were very complex",
    "Creature characteristics seemed very complex",
    "Differences between creatures seemed very complex",
    "Game-world elements made learning unclear",
    "Game world was ineffective for learning",
    "Game world was full of irrelevant content",
    "Relevant learning information was difficult to find",
]

ENG_CHAPTER_LABELS = [
    "I lost myself in this chapter",
    "Time just slipped away",
    "I was absorbed",
    "Chapter looked good",
    "I felt interested",
]

CL_OVERALL_LABELS = [
    "Instructions/explanations were very unclear",
    "Instructions/explanations were ineffective for learning",
    "Instructions/explanations were full of unclear content",
    "Interaction was very unclear",
    "Interaction was ineffective for learning",
    "Interaction made it harder to learn",
    "Interaction was difficult to master",
    "Enhanced understanding of creatures covered",
    "Enhanced knowledge of creature worlds",
    "Enhanced understanding of creature characteristics",
    "Enhanced understanding of concepts and differences",
]

ENG_OVERALL_LABELS = [
    "Felt frustrated while playing",
    "Game was confusing to play",
    "Playing was worth my time",
    "Experience was rewarding",
]


def item(
    *,
    item_id: str,
    label: str,
    columns: list[str],
    min_value: float,
    max_value: float,
    reverse: bool = False,
) -> dict[str, Any]:
    return {
        "id": item_id,
        "label": label,
        "columns": columns,
        "min_value": min_value,
        "max_value": max_value,
        "reverse": reverse,
    }


def chapter_items(
    prefix: str,
    labels: list[str],
    indices: list[int],
    min_value: float,
    max_value: float,
) -> list[dict[str, Any]]:
    items: list[dict[str, Any]] = []

    for chapter in (1, 2, 3):
        for index in indices:
            label = labels[index - 1]
            items.append(
                item(
                    item_id=f"{prefix}_ch{chapter}_{index}",
                    label=f"Chapter {chapter}: {label}",
                    columns=[f"{prefix}_ch{chapter}_scores_{index}"],
                    min_value=min_value,
                    max_value=max_value,
                )
            )

    return items


def chapter_stem_items(
    prefix: str,
    labels: list[str],
    indices: list[int],
    min_value: float,
    max_value: float,
) -> list[dict[str, Any]]:
    items: list[dict[str, Any]] = []

    for index in indices:
        label = labels[index - 1]
        items.append(
            item(
                item_id=f"{prefix}_chapter_stem_average_{index}",
                label=f"Chapter-average: {label}",
                columns=[
                    f"{prefix}_ch1_scores_{index}",
                    f"{prefix}_ch2_scores_{index}",
                    f"{prefix}_ch3_scores_{index}",
                ],
                min_value=min_value,
                max_value=max_value,
            )
        )

    return items


def overall_items(
    prefix: str,
    labels: list[str],
    indices: list[int],
    min_value: float,
    max_value: float,
    reverse_indices: set[int] | None = None,
) -> list[dict[str, Any]]:
    reverse_indices = reverse_indices or set()
    items: list[dict[str, Any]] = []

    for index in indices:
        label = labels[index - 1]
        items.append(
            item(
                item_id=f"{prefix}_overall_{index}",
                label=f"Overall: {label}",
                columns=[f"{prefix}_overall_scores_{index}"],
                min_value=min_value,
                max_value=max_value,
                reverse=index in reverse_indices,
            )
        )

    return items


BLOCKS = [
    {
        "id": "il_per_chapter",
        "tab": "per_chapter",
        "kind": "cl",
        "title": "Per-chapter intrinsic cognitive load",
        "description": "Chapter-specific perceived complexity of the learning content.",
        "score_note": "Higher scores indicate higher intrinsic cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": chapter_items("cl", CL_CHAPTER_LABELS, [1, 2, 3], 0, 10),
    },
    {
        "id": "environment_el_per_chapter",
        "tab": "per_chapter",
        "kind": "cl",
        "title": "Per-chapter environment-related extraneous cognitive load",
        "description": "Chapter-specific extraneous load attributed to the game world/environment.",
        "score_note": "Higher scores indicate higher environment-related extraneous cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": chapter_items("cl", CL_CHAPTER_LABELS, [4, 5, 6, 7], 0, 10),
    },
    {
        "id": "eng_per_chapter",
        "tab": "per_chapter",
        "kind": "eng",
        "title": "Per-chapter engagement",
        "description": "Chapter-specific focused-attention, aesthetic-appeal, and interest items.",
        "score_note": "Higher scores indicate higher engagement.",
        "scale_min": 1,
        "scale_max": 7,
        "items": chapter_items("eng", ENG_CHAPTER_LABELS, [1, 2, 3, 4, 5], 1, 7),
    },
    {
        "id": "instruction_el_overall",
        "tab": "overall",
        "kind": "cl",
        "title": "Overall instruction-related extraneous cognitive load",
        "description": "Overall extraneous load attributed to instructions and explanations.",
        "score_note": "Higher scores indicate higher instruction-related extraneous cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": overall_items("cl", CL_OVERALL_LABELS, [1, 2, 3], 0, 10),
    },
    {
        "id": "interaction_el_overall",
        "tab": "overall",
        "kind": "cl",
        "title": "Overall interaction-related extraneous cognitive load",
        "description": "Overall extraneous load attributed to interacting with the game.",
        "score_note": "Higher scores indicate higher interaction-related extraneous cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": overall_items("cl", CL_OVERALL_LABELS, [4, 5, 6, 7], 0, 10),
    },
    {
        "id": "gl_overall",
        "tab": "overall",
        "kind": "cl",
        "title": "Overall germane cognitive load",
        "description": "Overall perceived learning-related understanding and productive processing.",
        "score_note": "Higher scores indicate higher germane cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": overall_items("cl", CL_OVERALL_LABELS, [8, 9, 10, 11], 0, 10),
    },
    {
        "id": "eng_overall",
        "tab": "overall",
        "kind": "eng",
        "title": "Overall-game engagement",
        "description": "Overall negative-usability and reward/evaluative-engagement items.",
        "score_note": "Frustration and confusion are reverse-coded, so higher scores indicate higher engagement.",
        "scale_min": 1,
        "scale_max": 7,
        "items": overall_items("eng", ENG_OVERALL_LABELS, [1, 2, 3, 4], 1, 7, reverse_indices={1, 2}),
    },
    {
        "id": "il_main",
        "tab": "main_model",
        "kind": "cl",
        "title": "Main-model intrinsic cognitive load",
        "description": "Per-chapter intrinsic-load stems averaged across chapters and then averaged into one participant-level score.",
        "score_note": "Higher scores indicate higher intrinsic cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": chapter_stem_items("cl", CL_CHAPTER_LABELS, [1, 2, 3], 0, 10),
    },
    {
        "id": "environment_el_main",
        "tab": "main_model",
        "kind": "cl",
        "title": "Main-model environment-related extraneous cognitive load",
        "description": "Per-chapter environment-related extraneous-load stems averaged across chapters and then averaged into one participant-level score.",
        "score_note": "Higher scores indicate higher environment-related extraneous cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": chapter_stem_items("cl", CL_CHAPTER_LABELS, [4, 5, 6, 7], 0, 10),
    },
    {
        "id": "gl_main",
        "tab": "main_model",
        "kind": "cl",
        "title": "Main-model germane cognitive load",
        "description": "Overall-game germane-load items averaged into one participant-level score.",
        "score_note": "Higher scores indicate higher germane cognitive load.",
        "scale_min": 0,
        "scale_max": 10,
        "items": overall_items("cl", CL_OVERALL_LABELS, [8, 9, 10, 11], 0, 10),
    },
    {
        "id": "eng_main",
        "tab": "main_model",
        "kind": "eng",
        "title": "Main-model engagement",
        "description": "Per-chapter engagement stems are averaged across chapters and then combined with the overall-game engagement items.",
        "score_note": "Frustration and confusion are reverse-coded, so higher scores indicate higher engagement.",
        "scale_min": 1,
        "scale_max": 7,
        "items": chapter_stem_items("eng", ENG_CHAPTER_LABELS, [1, 2, 3, 4, 5], 1, 7)
        + overall_items("eng", ENG_OVERALL_LABELS, [1, 2, 3, 4], 1, 7, reverse_indices={1, 2}),
    },
    {
        "id": "el_main",
        "tab": "main_model",
        "kind": "cl",
        "title": "Main-model extraneous cognitive load",
        "description": "Equal-source-weighted mean of environment-related, instruction-related, and interaction-related extraneous cognitive load.",
        "score_note": "Higher scores indicate higher extraneous cognitive load. Each extraneous-load source contributes equally.",
        "scale_min": 0,
        "scale_max": 10,
        "components": [
            "environment_el_main",
            "instruction_el_overall",
            "interaction_el_overall",
        ],
    },
]


def clean(value: object) -> str:
    if value is None:
        return ""
    return str(value).strip()


def detect_text_encoding(path: Path) -> str:
    with path.open("rb") as handle:
        start = handle.read(4)

    if start.startswith((b"\xff\xfe", b"\xfe\xff")):
        return "utf-16"
    if start.startswith(b"\xef\xbb\xbf"):
        return "utf-8-sig"
    return "utf-8"


def newest_file(directory: Path, suffix: str) -> Path | None:
    if not directory.exists():
        return None

    files = [
        path
        for path in directory.iterdir()
        if path.is_file() and path.suffix.lower() == suffix
    ]
    if not files:
        return None

    return max(files, key=lambda path: path.stat().st_mtime)


def parse_numeric(value: object) -> float | None:
    text = clean(value)
    if not text:
        return None

    try:
        number = float(text)
    except ValueError:
        return None

    if math.isnan(number):
        return None

    return number


def extract_import_id(value: str) -> str | None:
    text = clean(value)
    if not text or '"ImportId"' not in text:
        return None

    try:
        payload = json.loads(text)
    except json.JSONDecodeError:
        return None

    import_id = payload.get("ImportId")
    return clean(import_id) or None


def looks_like_qualtrics_label_row(row: list[str]) -> bool:
    if not row:
        return False

    first = clean(row[0]).lower()
    if first in {"start date", "startdate"}:
        return True

    return any(" - " in clean(cell) for cell in row[:80])


def normalize_headers(
    raw_header: list[str],
    labels: list[str] | None = None,
    import_row: list[str] | None = None,
) -> list[str]:
    labels = labels or []
    import_row = import_row or []
    normalised: list[str] = []

    for index, raw_name in enumerate(raw_header):
        name = clean(raw_name)
        label = clean(labels[index]) if index < len(labels) else ""
        import_id = extract_import_id(import_row[index]) if index < len(import_row) else None

        if import_id:
            if name and not name.startswith("QID"):
                if import_id.startswith("QID"):
                    normalised.append(name)
                else:
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
    encoding = detect_text_encoding(path)

    with path.open("r", encoding=encoding, newline="") as handle:
        raw_rows = list(csv.reader(handle, delimiter="\t"))

    if not raw_rows:
        return [], []

    has_qualtrics_labels = len(raw_rows) > 1 and looks_like_qualtrics_label_row(raw_rows[1])

    has_import_row = False
    if len(raw_rows) > 2:
        import_id_cells = sum(1 for cell in raw_rows[2] if '"ImportId"' in clean(cell))
        has_import_row = import_id_cells >= max(3, len(raw_rows[0]) // 4)

    labels = raw_rows[1] if has_qualtrics_labels else []
    import_row = raw_rows[2] if has_import_row else []
    header = normalize_headers(raw_rows[0], labels, import_row)

    if has_import_row:
        start_index = 3
    elif has_qualtrics_labels:
        start_index = 2
    else:
        start_index = 1

    rows: list[dict[str, str]] = []
    for raw_row in raw_rows[start_index:]:
        if not any(clean(cell) for cell in raw_row):
            continue

        padded = raw_row + [""] * max(0, len(header) - len(raw_row))
        rows.append({header[index]: padded[index] for index in range(len(header))})

    return rows, header


def parse_log_fields(line: str) -> tuple[str, dict[str, str]]:
    parts = [part.strip() for part in line.split("|")]
    if len(parts) < 3:
        return "", {}

    event = parts[1]
    fields: dict[str, str] = {}

    for part in parts[2:]:
        if "=" not in part:
            continue

        key, value = part.split("=", 1)
        fields[clean(key)] = clean(value)

    return event, fields


def canonical_condition(raw_condition: str) -> str:
    key = clean(raw_condition).lower()
    return CONDITION_LABELS.get(key, raw_condition or "Unknown condition")


def build_condition_lookup(log_dir: Path) -> dict[str, dict[str, str]]:
    condition_by_mcid: dict[str, dict[str, str]] = {}

    if not log_dir.exists():
        return condition_by_mcid

    log_paths = []
    log_paths.extend(log_dir.glob("*.log"))
    log_paths.extend(log_dir.glob("*.txt"))

    for path in sorted(log_paths, key=lambda p: p.stat().st_mtime):
        if path.name.startswith("terminal-"):
            continue

        try:
            lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
        except OSError:
            continue

        fallback_records: list[tuple[str, str]] = []
        assigned_records: list[tuple[str, str]] = []

        for line in lines:
            event, fields = parse_log_fields(line)
            session_id = clean(fields.get("session_id"))
            condition = clean(fields.get("condition"))

            if not session_id or not condition:
                continue

            if event == "experiment_condition_assigned":
                assigned_records.append((session_id, condition))
            elif event in {"checkpoint_displayed", "checkpoint_choice_made", "checkpoint_choice_context"}:
                fallback_records.append((session_id, condition))

        records = assigned_records or fallback_records
        for session_id, condition in records:
            condition_by_mcid[session_id] = {
                "condition_raw": condition,
                "condition": canonical_condition(condition),
                "source_log": path.name,
            }

    return condition_by_mcid


def adjusted_item_value(row: dict[str, str], item_spec: dict[str, Any]) -> float | None:
    values: list[float] = []

    for column in item_spec["columns"]:
        value = parse_numeric(row.get(column, ""))
        if value is not None:
            values.append(value)

    if not values:
        return None

    value = sum(values) / len(values)

    if item_spec.get("reverse"):
        value = float(item_spec["min_value"]) + float(item_spec["max_value"]) - value

    return value


def mean(values: list[float]) -> float | None:
    valid = [value for value in values if value is not None and not math.isnan(value)]
    if not valid:
        return None
    return sum(valid) / len(valid)


def round_or_none(value: float | None, digits: int = 3) -> float | None:
    if value is None:
        return None
    return round(float(value), digits)


def summarise(values: list[float]) -> dict[str, Any]:
    valid = sorted(value for value in values if value is not None and not math.isnan(value))

    if not valid:
        return {
            "n": 0,
            "mean": None,
            "median": None,
            "sd": None,
            "min": None,
            "max": None,
        }

    n = len(valid)
    middle = n // 2

    if n % 2:
        median = valid[middle]
    else:
        median = (valid[middle - 1] + valid[middle]) / 2

    if n > 1:
        avg = sum(valid) / n
        sd = math.sqrt(sum((value - avg) ** 2 for value in valid) / (n - 1))
    else:
        sd = None

    return {
        "n": n,
        "mean": round_or_none(sum(valid) / n),
        "median": round_or_none(median),
        "sd": round_or_none(sd),
        "min": round_or_none(valid[0]),
        "max": round_or_none(valid[-1]),
    }


def compute_item_block_score(row: dict[str, str], block: dict[str, Any]) -> dict[str, Any]:
    numeric_values: list[float] = []
    item_values: dict[str, float | None] = {}

    for item_spec in block.get("items", []):
        value = adjusted_item_value(row, item_spec)
        rounded_value = round_or_none(value)
        item_values[item_spec["id"]] = rounded_value

        if value is not None:
            numeric_values.append(value)

    return {
        "mean": round_or_none(mean(numeric_values)),
        "n_items_answered": len(numeric_values),
        "n_items_total": len(block.get("items", [])),
        "complete": len(numeric_values) == len(block.get("items", [])),
        "item_values": item_values,
    }


def build_participants(rows: list[dict[str, str]], condition_lookup: dict[str, dict[str, str]]) -> list[dict[str, Any]]:
    participants: list[dict[str, Any]] = []

    item_blocks = [block for block in BLOCKS if "items" in block]
    component_blocks = [block for block in BLOCKS if "components" in block]

    for row_index, row in enumerate(rows, start=1):
        delayed = clean(row.get("DELAYED"))
        if delayed == "1":
            continue

        mcid = clean(row.get("MCID")) or clean(row.get("_recordId")) or clean(row.get("ResponseId")) or f"row-{row_index}"

        condition_info = condition_lookup.get(mcid, {})
        condition = condition_info.get("condition", "Unknown condition")
        condition_raw = condition_info.get("condition_raw", "")
        source_log = condition_info.get("source_log", "")

        block_scores: dict[str, Any] = {}
        block_item_values: dict[str, dict[str, Any]] = {}

        for block in item_blocks:
            score = compute_item_block_score(row, block)
            block_scores[block["id"]] = {
                "mean": score["mean"],
                "n_items_answered": score["n_items_answered"],
                "n_items_total": score["n_items_total"],
                "complete": score["complete"],
            }
            block_item_values[block["id"]] = score["item_values"]

        for block in component_blocks:
            component_values = [
                block_scores[component_id]["mean"]
                for component_id in block["components"]
                if component_id in block_scores and block_scores[component_id]["mean"] is not None
            ]

            block_scores[block["id"]] = {
                "mean": round_or_none(mean([float(value) for value in component_values])),
                "n_items_answered": len(component_values),
                "n_items_total": len(block["components"]),
                "complete": len(component_values) == len(block["components"]),
            }
            block_item_values[block["id"]] = {}

        participants.append(
            {
                "mcid": mcid,
                "condition": condition,
                "condition_raw": condition_raw,
                "source_log": source_log,
                "has_condition": condition != "Unknown condition",
                "startDate": clean(row.get("startDate") or row.get("StartDate")),
                "block_scores": block_scores,
                "block_item_values": block_item_values,
            }
        )

    return participants


def build_block_summaries(participants: list[dict[str, Any]]) -> dict[str, Any]:
    summaries: dict[str, Any] = {}

    for block in BLOCKS:
        block_id = block["id"]
        summaries[block_id] = {}

        for condition in CONDITION_ORDER:
            values = [
                participant["block_scores"][block_id]["mean"]
                for participant in participants
                if participant["condition"] == condition
                and participant["block_scores"][block_id]["mean"] is not None
            ]
            summaries[block_id][condition] = summarise([float(value) for value in values])

    return summaries


HTML_TEMPLATE = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Merged questionnaire summary</title>
<script src="https://cdn.plot.ly/plotly-2.35.2.min.js"></script>
<style>
:root {
  --bg: #f5f7f8;
  --card: #ffffff;
  --line: #d7dee3;
  --text: #142126;
  --muted: #5d6c74;
  --accent: #0f766e;
  --accent-light: #e7f5f3;
}
* {
  box-sizing: border-box;
}
body {
  margin: 0;
  background: var(--bg);
  color: var(--text);
  font-family: Arial, sans-serif;
}
main {
  max-width: 1600px;
  margin: 0 auto;
  padding: 18px;
}
h1, h2, h3 {
  margin: 0 0 10px 0;
}
p {
  margin: 0 0 9px 0;
}
.card {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.small {
  font-size: 12px;
  color: var(--muted);
}
.tabs,
.subtabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}
.tab-btn,
.subtab-btn {
  border: 1px solid var(--line);
  background: var(--card);
  border-radius: 999px;
  padding: 9px 14px;
  cursor: pointer;
  font-weight: 700;
}
.tab-btn.active,
.subtab-btn.active {
  background: var(--accent);
  color: white;
  border-color: var(--accent);
}
.tab-panel,
.subtab-panel {
  display: none;
}
.tab-panel.active,
.subtab-panel.active {
  display: block;
}
.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.metric {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 12px;
  background: #fbfcfd;
}
.metric .label {
  font-size: 12px;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: .04em;
}
.metric .value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 4px;
}
.plot {
  width: 100%;
  height: calc(100vh - 230px);
  min-height: 560px;
  margin-bottom: 30px;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  table-layout: fixed;
  margin: 10px 0 28px 0;
}
th, td {
  border: 1px solid var(--line);
  padding: 8px 9px;
  vertical-align: top;
  overflow-wrap: anywhere;
}
th {
  background: #f2f5f7;
  text-align: left;
}
.num {
  text-align: right;
  white-space: nowrap;
}
.warning {
  border-left: 4px solid #b45309;
  background: #fff7ed;
}
@media (max-width: 1000px) {
  .metric-grid {
    grid-template-columns: 1fr;
  }
  .plot {
    height: 70vh;
    min-height: 460px;
  }
}
</style>
</head>
<body>
<main>
  <div class="card">
    <h1>Merged questionnaire summary</h1>
    <p class="small">Newest TSV plus condition assignment parsed from study logs. Boxplots use fixed questionnaire scale ranges to avoid misleading visual differences.</p>
    <p class="small"><strong>TSV:</strong> <span id="source-tsv"></span></p>
  </div>

  <div class="tabs">
    <button class="tab-btn active" data-tab="overview">Overview</button>
    <button class="tab-btn" data-tab="per_chapter">Per-chapter</button>
    <button class="tab-btn" data-tab="overall">Overall game</button>
    <button class="tab-btn" data-tab="main_model">Main-model scores</button>
  </div>

  <section class="tab-panel active" id="tab-overview"></section>
  <section class="tab-panel" id="tab-per_chapter"></section>
  <section class="tab-panel" id="tab-overall"></section>
  <section class="tab-panel" id="tab-main_model"></section>
</main>

<script>
const REPORT_DATA = __REPORT_JSON__;

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatNumber(value) {
  if (value === null || value === undefined || Number.isNaN(value)) return "";
  return Number(value).toFixed(3).replace(/\\.000$/, "");
}

function metric(label, value) {
  return `
    <div class="metric">
      <div class="label">${escapeHtml(label)}</div>
      <div class="value">${escapeHtml(value)}</div>
    </div>
  `;
}

function conditionCounts() {
  const counts = {};
  for (const participant of REPORT_DATA.participants) {
    counts[participant.condition] = (counts[participant.condition] || 0) + 1;
  }
  return counts;
}

function renderConditionOverviewTable() {
  const counts = conditionCounts();

  const rows = REPORT_DATA.condition_order.map(condition => `
    <tr>
      <td>${escapeHtml(condition)}</td>
      <td class="num">${counts[condition] || 0}</td>
    </tr>
  `).join("");

  return `
    <table>
      <tr>
        <th>Condition</th>
        <th class="num">Participants</th>
      </tr>
      ${rows}
    </table>
  `;
}

function renderMissingConditionTable() {
  const missing = REPORT_DATA.participants.filter(participant => !participant.has_condition);

  if (missing.length === 0) {
    return `<p>No participants with missing condition assignment were found.</p>`;
  }

  const rows = missing.map(participant => `
    <tr>
      <td>${escapeHtml(participant.mcid)}</td>
      <td>${escapeHtml(participant.startDate)}</td>
      <td>${escapeHtml(participant.source_log)}</td>
    </tr>
  `).join("");

  return `
    <table>
      <tr>
        <th>MCID</th>
        <th>Start date</th>
        <th>Source log</th>
      </tr>
      ${rows}
    </table>
  `;
}

function renderSourceLogTable() {
  const rows = REPORT_DATA.participants.map(participant => `
    <tr>
      <td>${escapeHtml(participant.mcid)}</td>
      <td>${escapeHtml(participant.condition)}</td>
      <td>${escapeHtml(participant.condition_raw)}</td>
      <td>${escapeHtml(participant.source_log)}</td>
    </tr>
  `).join("");

  return `
    <table>
      <tr>
        <th>MCID</th>
        <th>Parsed condition</th>
        <th>Raw condition</th>
        <th>Source log</th>
      </tr>
      ${rows}
    </table>
  `;
}

function renderSummaryTable(block) {
  const rows = REPORT_DATA.condition_order.map(condition => {
    const stats = REPORT_DATA.block_summaries[block.id][condition];
    return `
      <tr>
        <td>${escapeHtml(condition)}</td>
        <td class="num">${stats.n}</td>
        <td class="num">${formatNumber(stats.mean)}</td>
        <td class="num">${formatNumber(stats.median)}</td>
        <td class="num">${formatNumber(stats.sd)}</td>
        <td class="num">${formatNumber(stats.min)}</td>
        <td class="num">${formatNumber(stats.max)}</td>
      </tr>
    `;
  }).join("");

  return `
    <h3>Descriptive table</h3>
    <table>
      <tr>
        <th>Condition</th>
        <th class="num">n</th>
        <th class="num">Mean</th>
        <th class="num">Median</th>
        <th class="num">SD</th>
        <th class="num">Observed min</th>
        <th class="num">Observed max</th>
      </tr>
      ${rows}
    </table>
  `;
}

function renderBlock(block) {
  return `
    <div class="card">
      <h2>${escapeHtml(block.title)}</h2>
      <p class="small">${escapeHtml(block.description)}</p>
      <p class="small"><strong>Score note:</strong> ${escapeHtml(block.score_note)}</p>
      <p class="small"><strong>Displayed scale range:</strong> ${block.scale_min} to ${block.scale_max}</p>

      <h3>Boxplot by condition</h3>
      <div class="plot" id="plot-${block.id}"></div>

      ${renderSummaryTable(block)}
    </div>
  `;
}

function renderSubtabs(tabId) {
  const blocks = REPORT_DATA.blocks.filter(block => block.tab === tabId);
  const clBlocks = blocks.filter(block => block.kind === "cl");
  const engBlocks = blocks.filter(block => block.kind === "eng");

  return `
    <div class="subtabs">
      <button class="subtab-btn active" data-parent="${tabId}" data-subtab="cl">Cognitive load</button>
      <button class="subtab-btn" data-parent="${tabId}" data-subtab="eng">Engagement</button>
    </div>

    <section class="subtab-panel active" id="subtab-${tabId}-cl">
      ${clBlocks.map(renderBlock).join("")}
    </section>

    <section class="subtab-panel" id="subtab-${tabId}-eng">
      ${engBlocks.map(renderBlock).join("")}
    </section>
  `;
}

function renderOverview() {
  const counts = conditionCounts();
  const total = REPORT_DATA.participants.length;
  const unknown = counts["Unknown condition"] || 0;
  const withCondition = total - unknown;
  const withLog = REPORT_DATA.participants.filter(participant => participant.source_log).length;

  const blockRows = REPORT_DATA.blocks.map(block => `
    <tr>
      <td>${escapeHtml(block.title)}</td>
      <td>${escapeHtml(block.tab_label)}</td>
      <td>${escapeHtml(block.kind_label)}</td>
      <td>${escapeHtml(block.description)}</td>
      <td class="num">${block.scale_min}–${block.scale_max}</td>
    </tr>
  `).join("");

  document.getElementById("tab-overview").innerHTML = `
    <div class="card">
      <h2>Participants found</h2>
      <div class="metric-grid">
        ${metric("Included participants", total)}
        ${metric("Condition found", withCondition)}
        ${metric("Missing condition", unknown)}
        ${metric("Source log found", withLog)}
      </div>
    </div>

    <div class="card">
      <h2>Participants per condition</h2>
      ${renderConditionOverviewTable()}
    </div>

    <div class="card ${unknown > 0 ? "warning" : ""}">
      <h2>Participants with missing condition</h2>
      ${renderMissingConditionTable()}
    </div>

    <div class="card">
      <h2>Main-model scoring decisions</h2>
      <p>Intrinsic cognitive load is computed from per-chapter intrinsic-load stems averaged across chapters.</p>
      <p>Extraneous cognitive load is computed as an equal-source-weighted mean of environment-related, instruction-related, and interaction-related extraneous cognitive load.</p>
      <p>Germane cognitive load is computed from the overall-game germane-load items.</p>
      <p>Engagement is computed from chapter-averaged engagement stems plus the overall-game engagement items, after reverse-coding frustration and confusion.</p>
      <p class="small">No single overall cognitive-load score is produced, because intrinsic, extraneous, and germane cognitive load are conceptually distinct.</p>
    </div>

    <div class="card">
      <h2>Included visualisations</h2>
      <table>
        <tr>
          <th>Scale</th>
          <th>Main tab</th>
          <th>Subtab</th>
          <th>Description</th>
          <th class="num">Scale range</th>
        </tr>
        ${blockRows}
      </table>
    </div>

    <div class="card">
      <h2>Condition source audit</h2>
      <p class="small">This table shows which log file supplied each condition label.</p>
      ${renderSourceLogTable()}
    </div>
  `;
}

function renderMainTabs() {
  document.getElementById("source-tsv").textContent = REPORT_DATA.source_tsv;

  renderOverview();

  const tabLabels = {
    per_chapter: "Per-chapter",
    overall: "Overall game",
    main_model: "Main-model scores",
  };

  for (const tabId of ["per_chapter", "overall", "main_model"]) {
    document.getElementById(`tab-${tabId}`).innerHTML = `
      <div class="card">
        <h2>${escapeHtml(tabLabels[tabId])}</h2>
        <p class="small">${escapeHtml(REPORT_DATA.tab_notes[tabId])}</p>
      </div>
      ${renderSubtabs(tabId)}
    `;
  }
}

function resizeAllPlots() {
  for (const block of REPORT_DATA.blocks) {
    const element = document.getElementById(`plot-${block.id}`);
    if (element) Plotly.Plots.resize(element);
  }
}

function activateMainTabs() {
  document.querySelectorAll(".tab-btn").forEach(button => {
    button.addEventListener("click", () => {
      const tab = button.dataset.tab;

      document.querySelectorAll(".tab-btn").forEach(item => {
        item.classList.toggle("active", item.dataset.tab === tab);
      });

      document.querySelectorAll(".tab-panel").forEach(panel => {
        panel.classList.toggle("active", panel.id === `tab-${tab}`);
      });

      setTimeout(resizeAllPlots, 50);
    });
  });
}

function activateSubtabs() {
  document.querySelectorAll(".subtab-btn").forEach(button => {
    button.addEventListener("click", () => {
      const parent = button.dataset.parent;
      const subtab = button.dataset.subtab;

      document.querySelectorAll(`.subtab-btn[data-parent="${parent}"]`).forEach(item => {
        item.classList.toggle("active", item.dataset.subtab === subtab);
      });

      document.querySelectorAll(`#tab-${parent} .subtab-panel`).forEach(panel => {
        panel.classList.toggle("active", panel.id === `subtab-${parent}-${subtab}`);
      });

      setTimeout(resizeAllPlots, 50);
    });
  });
}

function drawBoxplots() {
  for (const block of REPORT_DATA.blocks) {
    const traces = REPORT_DATA.condition_order.map(condition => {
      const y = REPORT_DATA.participants
        .filter(participant => participant.condition === condition)
        .map(participant => participant.block_scores[block.id].mean)
        .filter(value => value !== null && value !== undefined && !Number.isNaN(value));

      return {
        type: "box",
        name: condition,
        y,
        boxpoints: "all",
        jitter: 0.35,
        pointpos: 0,
        boxmean: true,
      };
    });

    const element = document.getElementById(`plot-${block.id}`);
    if (!element) continue;

    Plotly.newPlot(
      element,
      traces,
      {
        margin: { t: 35, r: 25, b: 75, l: 70 },
        yaxis: {
          title: "Participant mean score",
          range: [block.scale_min, block.scale_max],
          autorange: false,
          zeroline: false,
        },
        xaxis: {
          title: "Checkpoint condition",
        },
        showlegend: false,
      },
      { responsive: true }
    );
  }
}

renderMainTabs();
activateMainTabs();
activateSubtabs();
drawBoxplots();
</script>
</body>
</html>
"""


def render_html(
    *,
    source_tsv: Path,
    participants: list[dict[str, Any]],
    block_summaries: dict[str, Any],
) -> str:
    tab_labels = {
        "per_chapter": "Per-chapter",
        "overall": "Overall game",
        "main_model": "Main-model scores",
    }

    kind_labels = {
        "cl": "Cognitive load",
        "eng": "Engagement",
    }

    report_data = {
        "source_tsv": str(source_tsv.name),
        "participants": participants,
        "blocks": [
            {
                "id": block["id"],
                "tab": block["tab"],
                "tab_label": tab_labels[block["tab"]],
                "kind": block["kind"],
                "kind_label": kind_labels[block["kind"]],
                "title": block["title"],
                "description": block["description"],
                "score_note": block["score_note"],
                "scale_min": block["scale_min"],
                "scale_max": block["scale_max"],
            }
            for block in BLOCKS
        ],
        "tab_notes": {
            "per_chapter": "These plots summarise chapter-level questionnaire responses across the three game chapters.",
            "overall": "These plots summarise the items that were asked about the game as a whole.",
            "main_model": "These plots show the participant-level scores intended for the main statistical models.",
        },
        "condition_order": CONDITION_ORDER,
        "block_summaries": block_summaries,
    }

    report_json = json.dumps(report_data, ensure_ascii=False).replace("</", "<\\/")
    return HTML_TEMPLATE.replace("__REPORT_JSON__", report_json)


def main() -> int:
    source_tsv = newest_file(DATA_DIR, ".tsv")
    if source_tsv is None:
        raise FileNotFoundError(f"No TSV files found in {DATA_DIR}")

    rows, _header = load_tsv(source_tsv)
    condition_lookup = build_condition_lookup(LOG_DIR)
    participants = build_participants(rows, condition_lookup)
    block_summaries = build_block_summaries(participants)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(
        render_html(
            source_tsv=source_tsv,
            participants=participants,
            block_summaries=block_summaries,
        ),
        encoding="utf-8",
    )

    print(f"Wrote {OUTPUT_PATH}")
    print(f"Participants included: {len(participants)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())