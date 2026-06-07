from __future__ import annotations

import csv
import hashlib
import html
import json
import math
import re
import unicodedata
from collections import Counter, defaultdict
from datetime import datetime
from pathlib import Path
from typing import Any

from ._delayed_response_filter import DELAYED_INCLUDED_COLUMN
from ._ret_main import parse_seen_details, retention_column_name
from ._shared import (
    CREATURE_NAME_BY_ID,
    DATA_CONFIG_DIR,
    DATA_DIR,
    MAX_RETENTION_SLOTS,
    RESOURCES_DIR,
    RETENTION_QUESTION_SPECS,
    SURVEY_EXPORT_PATH,
    clean,
    delayed_flag,
    delayed_included_flag,
    first_present,
    mcid_from_row,
    parse_numeric,
)
from ._survey_io import detect_text_encoding

RETENTION_ANSWERS_PATH = DATA_DIR / "retention_answers.tsv"
RETENTION_MERGED_PATH = DATA_DIR / "retention_scores_merged.tsv"
GENAI_PROMPT_PATH = DATA_CONFIG_DIR / "genai_prompt.txt"
SCORING_RUBRICS_HTML_PATH = DATA_CONFIG_DIR / "scoring_rubrics.html"
CREATURE_INFO_HTML_PATH = DATA_CONFIG_DIR / "creature_info.html"
RUBRIC_JSON_PATH = RESOURCES_DIR / "retention_rubrics.json"
SCORE_BACKUPS_DIR = DATA_DIR.parent / "score_backups"

# Number of independent GenAI score files to generate in the private route.
# AMOUNT_GENAI=1 writes data/retention_scores_genai.tsv for backward compatibility.
# AMOUNT_GENAI>1 writes data/retention_scores_genai1.tsv,
# data/retention_scores_genai2.tsv, ... data/retention_scores_genai{n}.tsv.
AMOUNT_GENAI = 2

GENAI_SCORE_PREFIX = "retention_scores_genai"
GRADER_SCORE_PREFIX = "retention_scores_grader"
GENAI_FILENAME_RE = re.compile(r"^retention_scores_genai(\d*)\.tsv$")
GRADER_FILENAME_RE = re.compile(r"^retention_scores_grader(\d+)\.tsv$")

LOW_CONFIDENCE_THRESHOLD = 80.0
VALIDATION_SAMPLE_FRACTION = 0.25

RETENTION_ANSWER_FIELDNAMES = [
    "MCID",
    "creature",
    "question",
    "answer",
    "answer_std",
]

GENAI_SCORE_FIELDNAMES = [
    "question",
    "creature",
    "answer_std",
    "score (0-4)",
    "confidence (0-100%)",
    "note (optional)",
]

GRADER_SCORE_FIELDNAMES = [
    "question",
    "creature",
    "answer_std",
    "score (0-4)",
    "status",
    "note (optional)",
    "updated_at",
    "task_id",
]

MERGED_SCORE_BASE_FIELDNAMES = [
    "MCID",
    "creature",
    "question",
    "answer",
    "answer_std",
]

# Backward-compatible aliases that summarise the primary GenAI source.
MERGED_SCORE_GENAI_ALIAS_FIELDNAMES = [
    "genai_score",
    "genai_confidence",
    "genai_note",
]

MERGED_SCORE_METADATA_FIELDNAMES = [
    "moment",
    "creature_id",
    "question_key",
    "question_label",
    "task_id",
    "occurrence_weight",
]

MERGED_SCORE_FINAL_FIELDNAMES = [
    "final_status",
    "final_score",
    "final_note_auto",
    "final_note_manual",
]

FINAL_SCORE_PLACEHOLDER = "[resolve conflict]"
FINAL_NOTE_MANUAL_NOT_NEEDED = "—"

QUESTION_BY_KEY = {
    key: f"Q{index}"
    for index, (key, _label) in enumerate(RETENTION_QUESTION_SPECS, start=1)
}
QUESTION_KEY_BY_QUESTION = {value: key for key, value in QUESTION_BY_KEY.items()}
QUESTION_LABEL_BY_QUESTION = {
    QUESTION_BY_KEY[key]: label
    for key, label in RETENTION_QUESTION_SPECS
}
QUESTION_ORDER = list(QUESTION_KEY_BY_QUESTION)
QUESTION_SORT_INDEX = {question: index for index, question in enumerate(QUESTION_ORDER)}
FORM_ORDER_COLUMNS = [
    "retention_form_order",
    "retention_immediate_form_order",
    "retention_delayed_form_order",
    "form_order",
    "FORM_ORDER",
    "START",
    "INIT_START",
    "start",
    "init_start",
]


def stable_id(*parts: object, length: int = 24) -> str:
    raw = "|".join(clean(part) for part in parts)
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()[:length]


def positive_int(value: object, *, default: int = 1) -> int:
    try:
        number = int(value)
    except (TypeError, ValueError):
        return default
    return number if number >= 1 else default


def genai_score_path(index: int = 1, *, amount: int | None = None) -> Path:
    """Return the configured GenAI score path for a 1-based source index."""
    amount = positive_int(AMOUNT_GENAI if amount is None else amount)
    index = positive_int(index)
    if amount == 1 and index == 1:
        return DATA_DIR / f"{GENAI_SCORE_PREFIX}.tsv"
    return DATA_DIR / f"{GENAI_SCORE_PREFIX}{index}.tsv"


def configured_genai_score_paths(amount: int | None = None) -> list[Path]:
    """Return the GenAI score files that sum_merged should generate."""
    amount = positive_int(AMOUNT_GENAI if amount is None else amount)
    return [genai_score_path(index, amount=amount) for index in range(1, amount + 1)]


def grader_score_path(grader: int) -> Path:
    """Return the score file path for any positive integer grader id."""
    grader = positive_int(grader, default=0)
    if grader < 1:
        raise ValueError("grader must be a positive integer")
    return DATA_DIR / f"{GRADER_SCORE_PREFIX}{grader}.tsv"


def retention_score_source_sort_key(path: Path, filename_re: re.Pattern[str]) -> tuple[int, int, str]:
    match = filename_re.match(path.name)
    if not match:
        return (10**12, 1, path.name)
    raw_index = match.group(1)
    index = int(raw_index) if raw_index else 1
    # Unsuffixed retention_scores_genai.tsv sorts before retention_scores_genai1.tsv.
    suffix_rank = 1 if raw_index else 0
    return (index, suffix_rank, path.name)


def discover_genai_score_paths(data_dir: Path = DATA_DIR) -> list[Path]:
    if not data_dir.exists():
        return []
    return sorted(
        (path for path in data_dir.glob(f"{GENAI_SCORE_PREFIX}*.tsv") if path.is_file() and GENAI_FILENAME_RE.match(path.name)),
        key=lambda path: retention_score_source_sort_key(path, GENAI_FILENAME_RE),
    )


def discover_grader_score_paths(data_dir: Path = DATA_DIR) -> list[Path]:
    if not data_dir.exists():
        return []
    return sorted(
        (path for path in data_dir.glob(f"{GRADER_SCORE_PREFIX}*.tsv") if path.is_file() and GRADER_FILENAME_RE.match(path.name)),
        key=lambda path: retention_score_source_sort_key(path, GRADER_FILENAME_RE),
    )


def source_label(path: Path, *, kind: str) -> str:
    filename_re = GENAI_FILENAME_RE if kind == "genai" else GRADER_FILENAME_RE
    match = filename_re.match(path.name)
    raw_index = match.group(1) if match else ""
    index = int(raw_index) if raw_index else 1
    return f"{kind}{index}"


def labelled_source_paths(paths: list[Path], *, kind: str) -> list[tuple[str, Path]]:
    """Return stable source labels, disambiguating rare filename collisions."""
    seen: dict[str, int] = defaultdict(int)
    labelled: list[tuple[str, Path]] = []
    for path in paths:
        base_label = source_label(path, kind=kind)
        seen[base_label] += 1
        label = base_label if seen[base_label] == 1 else f"{base_label}_{seen[base_label]}"
        labelled.append((label, path))
    return labelled


def natural_source_key(label: str) -> list[Any]:
    return [int(part) if part.isdigit() else part for part in re.split(r"(\d+)", label)]


def source_fields(label: str, fields: tuple[str, ...]) -> list[str]:
    return [f"{label}_{field}" for field in fields]


def merged_score_fieldnames(
    genai_labels: list[str],
    grader_labels: list[str],
    *,
    extra_final_fieldnames: list[str] | None = None,
) -> list[str]:
    extra_final_fieldnames = [
        field
        for field in (extra_final_fieldnames or [])
        if field.startswith("final_") and field not in MERGED_SCORE_FINAL_FIELDNAMES
    ]

    fields: list[str] = []
    fields.extend(MERGED_SCORE_BASE_FIELDNAMES)
    fields.extend(MERGED_SCORE_GENAI_ALIAS_FIELDNAMES)
    for label in genai_labels:
        fields.extend(source_fields(label, ("score", "confidence", "note")))
    for label in grader_labels:
        fields.extend(source_fields(label, ("score", "status", "note")))
    fields.extend(MERGED_SCORE_METADATA_FIELDNAMES)
    fields.extend(extra_final_fieldnames)
    fields.extend(MERGED_SCORE_FINAL_FIELDNAMES)

    # Preserve order while avoiding duplicate columns if a future source label
    # happens to match a backward-compatible alias.
    return list(dict.fromkeys(fields))


def merged_score_fieldnames_from_rows(
    rows: list[dict[str, Any]],
    *,
    extra_final_fieldnames: list[str] | None = None,
) -> list[str]:
    genai_labels: set[str] = set()
    grader_labels: set[str] = set()
    for row in rows:
        for key in row:
            genai_match = re.match(r"^(genai\d+(?:_\d+)?)_score$", key)
            if genai_match:
                genai_labels.add(genai_match.group(1))
            grader_match = re.match(r"^(grader\d+(?:_\d+)?)_score$", key)
            if grader_match:
                grader_labels.add(grader_match.group(1))
    return merged_score_fieldnames(
        sorted(genai_labels, key=natural_source_key),
        sorted(grader_labels, key=natural_source_key),
        extra_final_fieldnames=extra_final_fieldnames,
    )


def is_retention_tsv_for_backup(path: Path) -> bool:
    return (
        path.name == RETENTION_ANSWERS_PATH.name
        or path.name == RETENTION_MERGED_PATH.name
        or GENAI_FILENAME_RE.match(path.name) is not None
        or GRADER_FILENAME_RE.match(path.name) is not None
    )


def standardise_answer(value: object) -> str:
    text = clean(value)
    if not text:
        return ""
    text = unicodedata.normalize("NFKC", text)
    text = text.lower()
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def read_tsv(path: Path) -> list[dict[str, str]]:
    if not path.exists():
        return []
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        reader = csv.DictReader(handle, delimiter="\t")
        return [dict(row) for row in reader]


def backup_retention_tsv(path: Path) -> None:
    """Copy newly written retention TSVs to score_backups without ever restoring from backups."""
    if not is_retention_tsv_for_backup(path) or not path.exists():
        return
    SCORE_BACKUPS_DIR.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S-%f")
    backup_path = SCORE_BACKUPS_DIR / f"{path.stem}-{timestamp}{path.suffix}"
    backup_path.write_bytes(path.read_bytes())


def write_tsv(path: Path, fieldnames: list[str], rows: list[dict[str, Any]], *, backup: bool = True) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames, delimiter="\t", extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({field: clean(row.get(field)) for field in fieldnames})
    if backup:
        backup_retention_tsv(path)


def load_rubric_json(path: Path = RUBRIC_JSON_PATH) -> dict[str, Any]:
    if not path.exists():
        raise FileNotFoundError(f"Missing rubric JSON source: {path}")
    return json.loads(path.read_text(encoding="utf-8"))


def normalise_form_order(value: object) -> str:
    text = clean(value).lower().replace("-", "_").replace(" ", "_")
    if not text:
        return ""
    image_tokens = {"image_first", "images_first", "img_first", "img", "image", "1"}
    name_tokens = {"name_first", "names_first", "name", "2"}
    if text in image_tokens or ("image" in text and "first" in text) or ("img" in text and "first" in text):
        return "image_first"
    if text in name_tokens or ("name" in text and "first" in text):
        return "name_first"
    return ""


def form_order_from_row(row: dict[str, Any], *, delayed: bool) -> str:
    if delayed:
        delayed_specific = first_present(row, ["retention_delayed_form_order", "delayed_form_order"])
        normalised = normalise_form_order(delayed_specific)
        if normalised:
            return normalised

    immediate_specific = first_present(row, ["retention_immediate_form_order", "retention_form_order", "form_order", "START", "INIT_START"])
    immediate_order = normalise_form_order(immediate_specific)
    if not immediate_order:
        for column in FORM_ORDER_COLUMNS:
            immediate_order = normalise_form_order(row.get(column))
            if immediate_order:
                break

    if not immediate_order:
        return ""
    if not delayed:
        return immediate_order
    return "name_first" if immediate_order == "image_first" else "image_first"


def question_pair_for_slot(row: dict[str, Any], slot_index: int, seen_count: int, *, delayed: bool) -> list[str]:
    image_keys = ["img1", "img2"]
    name_keys = ["name1", "name2"]
    image_has_answer = any(clean(row.get(retention_column_name(slot_index, key))) for key in image_keys)
    name_has_answer = any(clean(row.get(retention_column_name(slot_index, key))) for key in name_keys)

    if image_has_answer and not name_has_answer:
        return image_keys
    if name_has_answer and not image_has_answer:
        return name_keys
    if image_has_answer and name_has_answer:
        # Structurally unusual, but the safest audit behaviour is to retain all non-empty pairs.
        return [*image_keys, *name_keys]

    order = form_order_from_row(row, delayed=delayed)
    if not order:
        return []

    # For odd counts, the first half is rounded up. This only affects fully blank
    # administered pairs; non-empty columns above override the inferred half.
    first_half_end = math.ceil(max(0, seen_count) / 2)
    first_half = slot_index <= first_half_end
    image_for_this_slot = first_half if order == "image_first" else not first_half
    return image_keys if image_for_this_slot else name_keys


def build_prompt_rows_from_survey(survey_rows: list[dict[str, str]]) -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []

    for row in survey_rows:
        participant_id = mcid_from_row(row)
        if not participant_id:
            continue
        is_delayed = delayed_flag(row)
        if is_delayed and not delayed_included_flag(row):
            continue
        moment = "Delayed" if is_delayed else "Immediate"
        seen_creatures, _invalid = parse_seen_details(row.get("SEEN"))
        seen_creatures = seen_creatures[:MAX_RETENTION_SLOTS]

        for slot_index, creature_id in enumerate(seen_creatures, start=1):
            administered_keys = question_pair_for_slot(row, slot_index, len(seen_creatures), delayed=is_delayed)
            for question_key in administered_keys:
                question = QUESTION_BY_KEY[question_key]
                answer = clean(row.get(retention_column_name(slot_index, question_key)))
                rows.append({
                    "participant_id": participant_id,
                    "moment": moment,
                    "creature_id": creature_id,
                    "creature": CREATURE_NAME_BY_ID.get(creature_id, creature_id),
                    "question": question,
                    "question_key": question_key,
                    "question_label": QUESTION_LABEL_BY_QUESTION.get(question, question_key),
                    "answer": answer,
                    "answer_std": standardise_answer(answer),
                })

    rows.sort(key=lambda item: (
        clean(item.get("participant_id")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("moment")),
    ))
    return rows


def build_retention_answer_rows(prompt_rows: list[dict[str, str]]) -> list[dict[str, str]]:
    rows = [
        {
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "question": row["question"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
        }
        for row in prompt_rows
    ]
    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("answer_std")),
    ))
    return rows


def genai_group_key(row: dict[str, str], multi_creature_keys: set[tuple[str, str]]) -> tuple[str, str, str]:
    question = clean(row.get("question"))
    answer_std = clean(row.get("answer_std"))
    creature = clean(row.get("creature"))
    # Hybrid duplicate rule: normally question + answer_std; if the same
    # standardised answer for that question appears for multiple creatures, split by creature.
    if (question, answer_std) in multi_creature_keys:
        return question, creature, answer_std
    return question, creature, answer_std


def existing_genai_lookup(path: Path | None = None) -> dict[tuple[str, str, str], dict[str, str]]:
    path = path or genai_score_path()
    lookup: dict[tuple[str, str, str], dict[str, str]] = {}
    for row in read_tsv(path):
        key = (clean(row.get("question")), clean(row.get("creature")), clean(row.get("answer_std")))
        if all(key):
            lookup[key] = row
    return lookup


def build_unique_genai_rows(prompt_rows: list[dict[str, str]], *, existing_path: Path | None = None) -> list[dict[str, str]]:
    nonblank = [row for row in prompt_rows if clean(row.get("answer_std"))]
    creatures_by_question_answer: dict[tuple[str, str], set[str]] = defaultdict(set)
    for row in nonblank:
        creatures_by_question_answer[(row["question"], row["answer_std"])].add(row["creature"])
    multi_creature_keys = {key for key, creatures in creatures_by_question_answer.items() if len(creatures) > 1}

    grouped: dict[tuple[str, str, str], dict[str, str]] = {}
    for row in nonblank:
        key = genai_group_key(row, multi_creature_keys)
        grouped.setdefault(key, {
            "question": key[0],
            "creature": key[1],
            "answer_std": key[2],
            "score (0-4)": "",
            "confidence (0-100%)": "",
            "note (optional)": "",
        })

    existing = existing_genai_lookup(existing_path)
    rows: list[dict[str, str]] = []
    for key, row in grouped.items():
        previous = existing.get(key, {})
        merged = dict(row)
        for field in ("score (0-4)", "confidence (0-100%)", "note (optional)"):
            if clean(previous.get(field)):
                merged[field] = clean(previous.get(field))
        rows.append(merged)

    rows.sort(key=lambda item: (
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("creature")).lower(),
        clean(item.get("answer_std")),
    ))
    return rows


def html_text(value: object) -> str:
    return html.escape(clean(value)).replace("\n", "<br>")


def html_text_with_tokens(value: object) -> str:
    escaped = html_text(value)
    return re.sub(
        r"\[(SRC|FAN)\]",
        r'<span class="rubric-token-cobalt">[\1]</span>',
        escaped,
    )


def render_rubric_content_html(content: Any) -> str:
    if isinstance(content, dict):
        rows = []
        for left, right in content.items():
            rows.append(
                "<tr>"
                f"<td>{html_text(left)}</td>"
                f"<td>{html_text_with_tokens(right)}</td>"
                "</tr>"
            )
        if not rows:
            return ""
        return (
            '<table class="rubric-inner-table generated-rubric-inner-table"><tbody>'
            + "".join(rows)
            + "</tbody></table>"
        )

    if isinstance(content, list):
        items: list[str] = []
        for item in content:
            if isinstance(item, dict):
                text = "\n".join(f"{key}: {value}" for key, value in item.items())
            else:
                text = clean(item)
            if clean(text):
                items.append(f"<li>{html_text_with_tokens(text)}</li>")
        return f'<ul class="rubric-content-list">{"".join(items)}</ul>' if items else ""

    if clean(content):
        return f'<p class="rubric-content-text">{html_text_with_tokens(content)}</p>'
    return ""


def expanded_rubric_rows(table: dict[str, Any], score_scale: list[Any]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    score_order = [str(score) for score in score_scale]

    def append_score_rows(scores_by_value: dict[str, Any], base_row: dict[str, str]) -> None:
        ordered_scores = [score for score in score_order if score in scores_by_value]
        ordered_scores.extend(score for score in scores_by_value if score not in ordered_scores)
        for score in ordered_scores:
            rows.append({
                **base_row,
                "score": score,
                "content": scores_by_value.get(score),
            })

    if isinstance(table.get("scores"), dict):
        append_score_rows(table["scores"], {
            "creature_id": clean(table.get("creature_id")),
            "creature": clean(table.get("creature") or "All creatures"),
        })

    for entry in table.get("rows") or []:
        if isinstance(entry, dict) and isinstance(entry.get("scores"), dict):
            append_score_rows(entry["scores"], {
                "creature_id": clean(entry.get("creature_id")),
                "creature": clean(entry.get("creature") or ""),
            })

    return rows


def grouped_rubric_body_html(rows: list[dict[str, Any]]) -> str:
    groups: list[dict[str, Any]] = []
    for row in rows:
        creature_key = clean(row.get("creature_id")) or clean(row.get("creature"))
        if not groups or groups[-1]["key"] != creature_key:
            groups.append({"key": creature_key, "creature": clean(row.get("creature")), "rows": []})
        groups[-1]["rows"].append(row)

    parts: list[str] = []
    for group in groups:
        group_rows = group["rows"]
        row_span = max(1, len(group_rows))
        for row_index, row in enumerate(group_rows):
            creature_cell = f'<td rowspan="{row_span}">{html_text(group["creature"])}</td>' if row_index == 0 else ""
            parts.append(
                "<tr>"
                + creature_cell
                + f'<td><span class="score-number">{html_text(row.get("score"))}</span></td>'
                + f'<td class="rubric-note">{render_rubric_content_html(row.get("content"))}</td>'
                + "</tr>"
            )
    return "\n".join(parts)


def render_scoring_rubrics_html(rubric: dict[str, Any]) -> str:
    question_tables = rubric.get("question_rubric_tables") or {}
    question_order = [key for key, _label in RETENTION_QUESTION_SPECS if key in question_tables]
    first_question = question_order[0] if question_order else ""
    score_scale = rubric.get("score_scale", [0, 1, 2, 3, 4])
    question_labels = rubric.get("question_short_labels") or {}
    css_path = "../../resources/static/scoring_app.css"
    generated_at = datetime.now().strftime("%d %B %Y at %H:%M")

    question_tabs = "\n".join(
        f'''<button class="rubric-subtab-button {'active' if question_key == first_question else ''}" type="button" role="tab" data-rubric-question="{html.escape(question_key)}" aria-selected="{'true' if question_key == first_question else 'false'}">
              {html.escape(clean(question_labels.get(question_key)) or QUESTION_BY_KEY.get(question_key, question_key))}
            </button>'''
        for question_key in question_order
    )

    question_panels: list[str] = []
    for question_key in question_order:
        table = question_tables.get(question_key) or {}
        rows = expanded_rubric_rows(table, score_scale)
        intro_html = f"<p>{html.escape(clean(table.get('intro')))}</p>" if clean(table.get("intro")) else ""
        question_panels.append(f'''
          <section class="rubric-subtab-panel {'active' if question_key == first_question else ''}" role="tabpanel" data-rubric-question-panel="{html.escape(question_key)}">
            <section class="full-rubric-section">
              <h3>{html.escape(clean(table.get('short_title')) or QUESTION_BY_KEY.get(question_key, question_key))}</h3>
              <p class="small">{html.escape(clean(table.get('title')))}</p>
              {intro_html}
              <table class="full-rubric-table">
                <colgroup>
                  <col class="full-rubric-creature-col">
                  <col class="full-rubric-score-col">
                  <col>
                </colgroup>
                <thead>
                  <tr><th>Creature</th><th>Score</th><th>Label / Content</th></tr>
                </thead>
                <tbody>
                  {grouped_rubric_body_html(rows)}
                </tbody>
              </table>
            </section>
          </section>''')

    instructions = clean(rubric.get("instructions_html")) or "<p>No general scoring instructions are configured.</p>"

    return f'''<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Retention scoring rubrics</title>
  <link rel="stylesheet" href="{css_path}">
  <style>
    body {{ padding: 0; }}
    #topbar {{ align-items: center; }}
    #topbar .small {{ margin: 4px 0 0; }}
    main {{ padding: 18px; }}
    .doc-card {{ max-width: 1280px; margin: 0 auto 18px; }}
    .tab-panel {{ display: none; }}
    .tab-panel.active {{ display: block; }}
    .rubric-subtabs {{ top: 0; }}
    .full-rubric-table {{ table-layout: fixed; }}
    .full-rubric-table td:first-child {{ font-weight: 800; }}
    .generated-rubric-inner-table td:first-child {{ width: 38%; font-weight: 700; }}
  </style>
</head>
<body>
  <header id="topbar">
    <div>
      <h1 id="app-title">Retention scoring rubrics</h1>
      <p class="small">Generated directly from <code>resources/retention_rubrics.json</code> on {html.escape(generated_at)}.</p>
    </div>
    <nav id="tabs" aria-label="Document tabs">
      <button class="tab-button active" type="button" data-tab="instructions">Instructions</button>
      <button class="tab-button" type="button" data-tab="all-rubrics">All rubrics</button>
    </nav>
  </header>

  <main>
    <section class="tab-panel active" data-panel="instructions">
      <article class="doc-card">{instructions}</article>
    </section>

    <section class="tab-panel" data-panel="all-rubrics">
      <article class="doc-card">
        <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
          {question_tabs}
        </div>
        <div class="rubric-subtab-panels">
          {''.join(question_panels)}
        </div>
      </article>
    </section>
  </main>

  <script>
    const activateTopTab = (tabName) => {{
      document.querySelectorAll('.tab-button').forEach((button) => {{
        const active = button.dataset.tab === tabName;
        button.classList.toggle('active', active);
        button.setAttribute('aria-selected', active ? 'true' : 'false');
      }});
      document.querySelectorAll('.tab-panel').forEach((panel) => {{
        panel.classList.toggle('active', panel.dataset.panel === tabName);
      }});
    }};

    document.querySelectorAll('.tab-button').forEach((button) => {{
      button.addEventListener('click', () => activateTopTab(button.dataset.tab));
    }});

    const activateRubricTab = (questionKey) => {{
      document.querySelectorAll('.rubric-subtab-button').forEach((button) => {{
        const active = button.dataset.rubricQuestion === questionKey;
        button.classList.toggle('active', active);
        button.setAttribute('aria-selected', active ? 'true' : 'false');
      }});
      document.querySelectorAll('.rubric-subtab-panel').forEach((panel) => {{
        panel.classList.toggle('active', panel.dataset.rubricQuestionPanel === questionKey);
      }});
    }};

    document.querySelectorAll('.rubric-subtab-button').forEach((button) => {{
      button.addEventListener('click', () => activateRubricTab(button.dataset.rubricQuestion));
    }});
  </script>
</body>
</html>'''


def render_creature_info_html(rubric: dict[str, Any]) -> str:
    parts = [
        "<!doctype html><html><head><meta charset='utf-8'><title>Creature information</title>",
        "<style>body{font-family:system-ui,sans-serif;line-height:1.45;margin:2rem;max-width:1100px}.creature{border-top:1px solid #ccd;padding:1rem 0}img{max-width:220px;height:auto;border:1px solid #ccd;background:#f7f7f7}dt{font-weight:700}dd{margin:0 0 .5rem 0}</style>",
        "</head><body>",
        "<h1>Creature information</h1>",
        "<p>This file is generated directly from <code>resources/retention_rubrics.json</code>. Image paths are relative to this file in <code>data/config/</code>.</p>",
    ]
    creatures = rubric.get("creatures") or {}
    for creature_id, creature in sorted(creatures.items(), key=lambda item: clean((item[1] or {}).get("name")).lower()):
        image_name = Path(clean(creature.get("image"))).name
        image_path = f"../../resources/static/creatures/{image_name}" if image_name else ""
        facts = creature.get("facts") or []
        parts.append("<section class='creature'>")
        parts.append(f"<h2>{html.escape(clean(creature.get('name')) or creature_id)}</h2>")
        if image_path:
            parts.append(f"<img src='{html.escape(image_path)}' alt='{html.escape(clean(creature.get('name')))}'>")
        parts.append("<dl>")
        parts.append(f"<dt>Creature id</dt><dd>{html.escape(creature_id)}</dd>")
        parts.append(f"<dt>Chapter</dt><dd>{html.escape(clean(creature.get('chapter')))}</dd>")
        parts.append(f"<dt>Environment</dt><dd>{html.escape(clean(creature.get('environment')))}</dd>")
        parts.append(f"<dt>Appearance</dt><dd>{html.escape(clean(creature.get('appearance')))}</dd>")
        parts.append("<dt>Facts</dt><dd><ul>" + "".join(f"<li>{html.escape(clean(fact))}</li>" for fact in facts) + "</ul></dd>")
        parts.append("</dl></section>")
    parts.append("</body></html>")
    return "\n".join(parts)


def genai_prompt_text() -> str:
    return """In the attachment, you should find exactly these files:
- one generated retention_scores_genai*.tsv source file
- scoring_rubrics.html
- creature_info.html

If any file is missing, inaccessible, unreadable, or clearly incomplete, do not continue. Stop your response and state which file(s) are missing or unusable.

Strict source rule:
Use only the attached files. Do not browse the internet. Do not look up creature names, animal facts, game facts, images, or locations online. Do not use external knowledge, model memory, or assumptions about real animals, games, Minecraft, fantasy creatures, or naming conventions. These are study-specific fictional learning materials. If something is not supported by the attached rubric or creature information, treat it as unknown.

Task:
Fill in the attached retention_scores_genai*.tsv file. Keep the row order exactly the same. Do not add, delete, reorder, rename, or reformat columns. For every row, evaluate the standardised retention answer using only the attached rubric and creature information.

Columns to fill:
- score (0-4): an integer from 0 to 4 only.
- confidence (0-100%): your confidence percentage as a number from 0 to 100 only. Do not include a percent sign.
- note (optional): leave this cell empty unless a note is genuinely needed. Add a note only for ambiguity, borderline scores, uncertainty, suspected rubric tension, missing/unclear source information, or a reason a human should inspect the row. Do not add routine notes for evident answers.

Important scoring rules:
- Score the value in answer_std for the listed question and creature.
- Use the four question rubrics exactly as supplied.
- The answer text has already been standardised; do not punish lowercase, stripped whitespace, or simple formatting loss.
- Do not reward an answer for being plausible in general. Reward it only when it matches the supplied study-specific creature information and rubric.
- Do not infer hidden intent when the answer is vague. If the answer could refer to multiple things and the rubric does not make it clearly correct, lower the score and add a short note.
- If the answer is blank, equivalent to "I do not know", nonsensical, or off-topic, score it 0.
- If an answer contains both correct and incorrect elements, apply the rubric rather than automatically giving full credit. Use the note only if the mixed answer needs human inspection.
- The same answer may occur for many participants. Score the row once as the score for all identical cases represented by that row.
- If the same answer seems correct for one creature but not another, score each row according to the listed creature only.
- If the rubric and creature information appear to conflict, follow the rubric where possible and add a note.

TSV output rules:
- Preserve valid TSV format.
- Preserve the original header exactly.
- Preserve the original row order exactly.
- Preserve all existing cell values except the three columns you are asked to fill.
- Do not add markdown, explanations, comments, code fences, bullet points, or surrounding text around the TSV.
- Return only the completed TSV content or a completed TSV attachment, depending on the interface you are using.
"""


def write_prompt_support_files(rubric_path: Path = RUBRIC_JSON_PATH) -> None:
    rubric = load_rubric_json(rubric_path)
    DATA_CONFIG_DIR.mkdir(parents=True, exist_ok=True)
    GENAI_PROMPT_PATH.write_text(genai_prompt_text(), encoding="utf-8")
    SCORING_RUBRICS_HTML_PATH.write_text(render_scoring_rubrics_html(rubric), encoding="utf-8")
    CREATURE_INFO_HTML_PATH.write_text(render_creature_info_html(rubric), encoding="utf-8")


def prepare_retention_answer_files(survey_rows: list[dict[str, str]]) -> dict[str, Any]:
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    retention_rows = build_retention_answer_rows(prompt_rows)
    genai_paths = configured_genai_score_paths()
    genai_rows_by_path: dict[Path, list[dict[str, str]]] = {
        path: build_unique_genai_rows(prompt_rows, existing_path=path)
        for path in genai_paths
    }
    write_tsv(RETENTION_ANSWERS_PATH, RETENTION_ANSWER_FIELDNAMES, retention_rows)
    for path, genai_rows in genai_rows_by_path.items():
        write_tsv(path, GENAI_SCORE_FIELDNAMES, genai_rows)
    write_prompt_support_files()
    first_genai_rows = next(iter(genai_rows_by_path.values()), [])
    return {
        "prompt_rows": len(prompt_rows),
        "retention_answer_rows": len(retention_rows),
        "unique_genai_rows_per_file": len(first_genai_rows),
        "genai_file_count": len(genai_paths),
        "genai_files": ", ".join(path.name for path in genai_paths),
        "blank_prompt_rows": sum(1 for row in prompt_rows if not clean(row.get("answer_std"))),
        "nonblank_prompt_rows": sum(1 for row in prompt_rows if clean(row.get("answer_std"))),
    }


def score_is_valid(value: object) -> bool:
    number = parse_numeric(value)
    return number is not None and float(number).is_integer() and 0 <= int(number) <= 4


def score_text(value: object) -> str:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return ""
    score = int(number)
    return str(score) if 0 <= score <= 4 else ""


def confidence_value(value: object) -> float | None:
    text = clean(value).replace("%", "")
    number = parse_numeric(text)
    if number is None or number < 0 or number > 100:
        return None
    return float(number)


def load_one_genai_scores(path: Path) -> tuple[dict[tuple[str, str, str], dict[str, str]], list[str]]:
    if not path.exists():
        return {}, [
            f"Missing {path}. Run sum_merged with PUBLIC_ROUTE=False to create the GenAI prompt files, "
            "then use data/config/genai_prompt.txt to fill the generated retention_scores_genai*.tsv file(s)."
        ]
    rows = read_tsv(path)
    if not rows:
        return {}, [f"{path} is empty."]
    missing_columns = [column for column in GENAI_SCORE_FIELDNAMES if column not in rows[0]]
    if missing_columns:
        return {}, [f"{path} is missing column(s): {', '.join(missing_columns)}"]
    lookup: dict[tuple[str, str, str], dict[str, str]] = {}
    problems: list[str] = []
    for index, row in enumerate(rows, start=2):
        key = (clean(row.get("question")), clean(row.get("creature")), clean(row.get("answer_std")))
        if not all(key):
            problems.append(f"{path.name} row {index}: question, creature, and answer_std must all be filled.")
            continue
        if key in lookup:
            problems.append(f"{path.name} row {index}: duplicate GenAI score key: {key[0]} / {key[1]} / {key[2]}")
        if not score_is_valid(row.get("score (0-4)")):
            problems.append(f"{path.name} row {index}: score (0-4) must be an integer from 0 to 4.")
        if confidence_value(row.get("confidence (0-100%)")) is None:
            problems.append(f"{path.name} row {index}: confidence (0-100%) must be a number from 0 to 100.")
        lookup[key] = row
    return lookup, problems


def load_genai_score_sources() -> tuple[dict[str, dict[tuple[str, str, str], dict[str, str]]], list[str]]:
    labelled_paths = labelled_source_paths(discover_genai_score_paths(), kind="genai")
    if not labelled_paths:
        return {}, [
            "No retention_scores_genai*.tsv files found. Run sum_merged with PUBLIC_ROUTE=False to generate "
            "the GenAI score file(s), then fill the score/confidence columns using data/config/genai_prompt.txt."
        ]

    sources: dict[str, dict[tuple[str, str, str], dict[str, str]]] = {}
    problems: list[str] = []
    for label, path in labelled_paths:
        lookup, source_problems = load_one_genai_scores(path)
        sources[label] = lookup
        problems.extend(source_problems)
    return sources, problems


def merge_genai_sources_for_lookup(
    sources: dict[str, dict[tuple[str, str, str], dict[str, str]]]
) -> dict[tuple[str, str, str], dict[str, str]]:
    """Return a primary lookup for review-task selection and legacy callers."""
    merged: dict[tuple[str, str, str], dict[str, str]] = {}
    for label in sorted(sources, key=natural_source_key):
        for key, row in sources[label].items():
            if key not in merged:
                copy = dict(row)
                copy["_source_label"] = label
                merged[key] = copy
    return merged


def load_genai_scores(path: Path | None = None) -> tuple[dict[tuple[str, str, str], dict[str, str]], list[str]]:
    """Load one GenAI file, or all discovered GenAI files when path is omitted."""
    if path is not None:
        return load_one_genai_scores(path)
    sources, problems = load_genai_score_sources()
    return merge_genai_sources_for_lookup(sources), problems


def load_grader_score_sources() -> tuple[dict[str, dict[str, dict[str, str]]], list[str]]:
    labelled_paths = labelled_source_paths(discover_grader_score_paths(), kind="grader")
    sources: dict[str, dict[str, dict[str, str]]] = {}
    problems: list[str] = []
    for label, path in labelled_paths:
        sources[label] = load_grader_scores(path)
    return sources, problems



def task_id_for_unique(question: str, creature: str, answer_std: str) -> str:
    return stable_id(question, creature, answer_std)


def unique_task_lookup(prompt_rows: list[dict[str, str]]) -> dict[str, dict[str, Any]]:
    genai_rows = build_unique_genai_rows(prompt_rows)
    occurrence_counts = Counter(
        (row["question"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    raw_examples: dict[tuple[str, str, str], list[str]] = defaultdict(list)
    creature_id_by_key: dict[tuple[str, str, str], str] = {}
    question_key_by_question: dict[str, str] = {}
    for row in prompt_rows:
        if not clean(row.get("answer_std")):
            continue
        key = (row["question"], row["creature"], row["answer_std"])
        if len(raw_examples[key]) < 5 and clean(row.get("answer")) not in raw_examples[key]:
            raw_examples[key].append(clean(row.get("answer")))
        creature_id_by_key[key] = row["creature_id"]
        question_key_by_question[row["question"]] = row["question_key"]

    tasks: dict[str, dict[str, Any]] = {}
    for row in genai_rows:
        key = (row["question"], row["creature"], row["answer_std"])
        task_id = task_id_for_unique(*key)
        question_key = question_key_by_question.get(row["question"], QUESTION_KEY_BY_QUESTION.get(row["question"], ""))
        creature_id = creature_id_by_key.get(key, "")
        tasks[task_id] = {
            "task_id": task_id,
            "question": row["question"],
            "question_key": question_key,
            "question_label": QUESTION_LABEL_BY_QUESTION.get(row["question"], question_key),
            "creature": row["creature"],
            "creature_name": row["creature"],
            "creature_id": creature_id,
            "answer_std": row["answer_std"],
            "answer": row["answer_std"],
            "occurrence_count": occurrence_counts.get(key, 0),
            "raw_examples": raw_examples.get(key, []),
        }
    return tasks


def confidence_bucket(value: object) -> str:
    confidence = confidence_value(value)
    if confidence is None:
        return "missing"
    if confidence < 60:
        return "00-59"
    if confidence < 80:
        return "60-79"
    return "80-100"


def deterministic_order(seed: str, task: dict[str, Any]) -> str:
    return hashlib.sha256((seed + "|" + clean(task.get("task_id"))).encode("utf-8")).hexdigest()


def build_review_tasks(prompt_rows: list[dict[str, str]], genai_lookup: dict[tuple[str, str, str], dict[str, str]], *, low_confidence_threshold: float = LOW_CONFIDENCE_THRESHOLD) -> list[dict[str, Any]]:
    tasks_by_id = unique_task_lookup(prompt_rows)
    tasks = list(tasks_by_id.values())
    genai_by_task_id: dict[str, dict[str, str]] = {}
    for key, row in genai_lookup.items():
        genai_by_task_id[task_id_for_unique(*key)] = row

    for task in tasks:
        genai_row = genai_by_task_id.get(task["task_id"], {})
        task["genai_score"] = score_text(genai_row.get("score (0-4)"))
        confidence = confidence_value(genai_row.get("confidence (0-100%)"))
        task["genai_confidence"] = "" if confidence is None else str(int(confidence) if confidence.is_integer() else confidence)
        task["genai_note"] = clean(genai_row.get("note (optional)"))
        task["confidence_bucket"] = confidence_bucket(genai_row.get("confidence (0-100%)"))
        task["low_confidence"] = confidence is not None and confidence < low_confidence_threshold

    validation_ids: set[str] = set()
    low_confidence_ids: set[str] = set()
    note_ids: set[str] = set()
    groups: dict[tuple[str, str], list[dict[str, Any]]] = defaultdict(list)
    for task in tasks:
        groups[(clean(task.get("question")), clean(task.get("confidence_bucket")))].append(task)

    for group_key, group in groups.items():
        ordered = sorted(group, key=lambda item: deterministic_order(f"validation-{group_key[0]}-{group_key[1]}-v1", item))
        n_to_take = max(1, round(len(ordered) * VALIDATION_SAMPLE_FRACTION))
        validation_ids.update(task["task_id"] for task in ordered[:n_to_take])

    for task in tasks:
        if task.get("low_confidence"):
            low_confidence_ids.add(task["task_id"])
        if clean(task.get("genai_note")):
            note_ids.add(task["task_id"])

    selected_ids = validation_ids | low_confidence_ids | note_ids
    selected = [task for task in tasks if task["task_id"] in selected_ids]
    for task in selected:
        reasons = []
        if task["task_id"] in validation_ids:
            # The UI does not reveal this per task during blind scoring, but the
            # payload keeps the reason for auditability.
            reasons.append("validation_sample")
        if task["task_id"] in low_confidence_ids:
            reasons.append("low_confidence")
        if task["task_id"] in note_ids:
            reasons.append("genai_note")
        task["review_reasons"] = ",".join(dict.fromkeys(reasons))

    selected.sort(key=lambda task: (
        QUESTION_SORT_INDEX.get(clean(task.get("question")), 999),
        clean(task.get("creature")).lower(),
        clean(task.get("answer_std")),
    ))
    return selected


def load_grader_scores(path: Path) -> dict[str, dict[str, str]]:
    rows = read_tsv(path)
    lookup: dict[str, dict[str, str]] = {}
    for row in rows:
        task_id = clean(row.get("task_id"))
        if task_id:
            lookup[task_id] = row
    return lookup


def write_grader_scores(path: Path, rows_by_task_id: dict[str, dict[str, Any]]) -> None:
    rows = sorted(rows_by_task_id.values(), key=lambda row: (
        QUESTION_SORT_INDEX.get(clean(row.get("question")), 999),
        clean(row.get("creature")).lower(),
        clean(row.get("answer_std")),
    ))
    write_tsv(path, GRADER_SCORE_FIELDNAMES, rows)


def validate_genai_completeness(prompt_rows: list[dict[str, str]], genai_lookup: dict[tuple[str, str, str], dict[str, str]]) -> list[str]:
    expected = {
        (row["question"], row["creature"], row["answer_std"])
        for row in build_unique_genai_rows(prompt_rows)
    }
    missing = sorted(expected - set(genai_lookup), key=lambda key: (QUESTION_SORT_INDEX.get(key[0], 999), key[1].lower(), key[2]))
    if not missing:
        return []
    preview = [f"{q} / {creature} / {answer[:80]}" for q, creature, answer in missing[:10]]
    suffix = f"; plus {len(missing) - 10} more" if len(missing) > 10 else ""
    return ["Missing GenAI score rows for: " + " | ".join(preview) + suffix]


def append_problem(problems: list[str], message: str, *, limit: int = 75) -> None:
    if len(problems) < limit:
        problems.append(message)


def score_source_values(source_rows: dict[str, dict[str, str]], *, score_field: str = "score (0-4)") -> dict[str, str]:
    return {
        label: score_text(row.get(score_field))
        for label, row in source_rows.items()
        if row
    }


def consensus_score(scores: dict[str, str]) -> str:
    valid = [score for score in scores.values() if score]
    if not valid:
        return ""
    return valid[0] if all(score == valid[0] for score in valid) else ""


def primary_source_row(source_rows: dict[str, dict[str, str]]) -> dict[str, str]:
    for label in sorted(source_rows, key=natural_source_key):
        row = source_rows.get(label) or {}
        if row:
            return row
    return {}


def _agreement_phrase(count: int, singular: str, plural: str) -> str:
    label = singular if count == 1 else plural
    return f"{label} ({count}x)"


def _all_scores_agree(score_groups: list[dict[str, str]]) -> str:
    scores = [score for group in score_groups for score in group.values() if score]
    if not scores:
        return ""
    return scores[0] if all(score == scores[0] for score in scores) else ""


def auto_final_note_for_scores(genai_scores: dict[str, str], grader_scores: dict[str, str]) -> str:
    """Describe why the automatic final score was or was not resolved."""
    genai_scores = {label: score for label, score in genai_scores.items() if score}
    grader_scores = {label: score for label, score in grader_scores.items() if score}
    if _all_scores_agree([genai_scores, grader_scores]):
        return "full agreement"

    genai_count = len(genai_scores)
    grader_count = len(grader_scores)
    genai_group = _agreement_phrase(genai_count, "GenAI", "GenAI") if genai_count else ""
    grader_group = _agreement_phrase(grader_count, "grader", "graders") if grader_count else ""
    genai_agreement = consensus_score(genai_scores) if genai_count else ""
    grader_agreement = consensus_score(grader_scores) if grader_count else ""

    if genai_count and not grader_count:
        return f"{genai_group} do not agree"
    if grader_count and not genai_count:
        return f"{grader_group} do not agree"

    if genai_count == 1 and grader_count == 1:
        return "grader (1x) and GenAI (1x) do not agree"

    if genai_count == 1 and grader_count >= 2:
        if grader_agreement:
            return f"graders ({grader_count}x) agree with each other, but not with GenAI (1x)"
        return f"graders ({grader_count}x) do not agree with each other or with GenAI (1x)"

    if genai_count >= 2 and grader_count == 1:
        if genai_agreement:
            return f"grader (1x) does not agree with GenAI ({genai_count}x), which agree with each other"
        return f"grader (1x) and GenAI ({genai_count}x) have no agreement at all"

    if genai_count >= 2 and grader_count >= 2:
        if genai_agreement and grader_agreement:
            return f"graders ({grader_count}x) agree with each other and GenAI ({genai_count}x) agree with each other, but the groups do not agree"
        if genai_agreement and not grader_agreement:
            return f"GenAI ({genai_count}x) agree with each other, but graders ({grader_count}x) do not agree"
        if grader_agreement and not genai_agreement:
            return f"graders ({grader_count}x) agree with each other, but GenAI ({genai_count}x) do not agree"
        return f"graders ({grader_count}x) and GenAI ({genai_count}x) have no agreement at all"

    return "score disagreement"


def auto_final_fields_for_row(
    *,
    answer_std: str,
    genai_scores: dict[str, str],
    grader_scores: dict[str, str],
    missing_labels: list[str],
    old_status: str,
) -> tuple[str, str, str, str]:
    if not answer_std:
        return "0", "auto_blank", "blank answer", FINAL_NOTE_MANUAL_NOT_NEEDED

    if missing_labels:
        return FINAL_SCORE_PLACEHOLDER, "needs_scores", "missing scores: " + ", ".join(missing_labels), ""

    agreed_score = _all_scores_agree([genai_scores, grader_scores])
    if agreed_score:
        return agreed_score, old_status or "full_agreement", "full agreement", FINAL_NOTE_MANUAL_NOT_NEEDED

    return FINAL_SCORE_PLACEHOLDER, "needs_adjudication", auto_final_note_for_scores(genai_scores, grader_scores), ""


def merged_row_exact_key(row: dict[str, Any]) -> tuple[str, str, str, str, str]:
    return (
        clean(row.get("MCID")),
        clean(row.get("moment")),
        clean(row.get("creature_id")),
        clean(row.get("question_key")),
        clean(row.get("answer_std")),
    )


def merged_row_identity_key(row: dict[str, Any]) -> tuple[str, str, str, str]:
    return (
        clean(row.get("MCID")),
        clean(row.get("moment")),
        clean(row.get("creature_id")),
        clean(row.get("question_key")),
    )


def load_existing_merged_rows(path: Path = RETENTION_MERGED_PATH) -> tuple[list[dict[str, str]], list[str], bool]:
    if not path.exists():
        return [], [], False
    rows = read_tsv(path)
    header = list(rows[0]) if rows else []
    final_columns = [field for field in header if field.startswith("final_")]
    # Older generated files already had final_score/final_status, but those were
    # not manual audit columns. Preserve final_* cells only after the new manual
    # audit schema has appeared at least once.
    preserve_final_values = "final_note_auto" in header or "final_note_manual" in header
    return rows, final_columns, preserve_final_values


def apply_existing_final_values(
    rows: list[dict[str, Any]],
    existing_rows: list[dict[str, str]],
    final_columns: list[str],
    *,
    preserve_final_values: bool,
) -> None:
    if not preserve_final_values or not existing_rows or not final_columns:
        return

    existing_by_key = {merged_row_exact_key(row): row for row in existing_rows}
    for row in rows:
        existing = existing_by_key.get(merged_row_exact_key(row))
        if not existing:
            continue
        for column in final_columns:
            value = clean(existing.get(column))
            if value:
                row[column] = value


def warn_on_non_final_drift(
    rows: list[dict[str, Any]],
    existing_rows: list[dict[str, str]],
    fieldnames: list[str],
    *,
    limit: int = 25,
) -> None:
    if not existing_rows:
        return

    existing_by_identity: dict[tuple[str, str, str, str], dict[str, str]] = {}
    for row in existing_rows:
        key = merged_row_identity_key(row)
        if all(key):
            existing_by_identity.setdefault(key, row)

    printed = 0
    extra = 0
    non_final_fields = [field for field in fieldnames if not field.startswith("final_")]
    for row in rows:
        existing = existing_by_identity.get(merged_row_identity_key(row))
        if not existing:
            continue
        changed = [
            field
            for field in non_final_fields
            if clean(existing.get(field)) != clean(row.get(field))
        ]
        if not changed:
            continue
        if printed < limit:
            identity = ", ".join([
                f"MCID={clean(row.get('MCID')) or 'NA'}",
                f"moment={clean(row.get('moment')) or 'NA'}",
                f"creature_id={clean(row.get('creature_id')) or 'NA'}",
                f"question_key={clean(row.get('question_key')) or 'NA'}",
            ])
            preview = ", ".join(changed[:12])
            suffix = f" (+{len(changed) - 12} more)" if len(changed) > 12 else ""
            print(
                "[retention_scores_merged] WARNING: regenerated non-final source columns differ "
                f"from the existing file for {identity}. Changed column(s): {preview}{suffix}",
                flush=True,
            )
            printed += 1
        else:
            extra += 1

    if extra:
        print(
            f"[retention_scores_merged] WARNING: {extra} additional row(s) had regenerated non-final source-column changes not shown.",
            flush=True,
        )


def build_prompt_score_rows(
    survey_rows: list[dict[str, str]],
    *,
    require_complete_review: bool = True,
) -> tuple[list[dict[str, Any]], list[str]]:
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    genai_sources, genai_problems = load_genai_score_sources()
    genai_lookup = merge_genai_sources_for_lookup(genai_sources)
    problems = list(genai_problems)
    problems.extend(validate_genai_completeness(prompt_rows, genai_lookup))

    review_tasks = build_review_tasks(prompt_rows, genai_lookup)
    required_review_ids = {task["task_id"] for task in review_tasks}
    occurrence_counts = Counter(
        (row["question"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    grader_sources, grader_problems = load_grader_score_sources()
    problems.extend(grader_problems)
    if required_review_ids and not grader_sources:
        problems.append(
            "No retention_scores_grader*.tsv files found. Run python main.py score_ret grader=1 "
            "to create a human-validation file, and repeat with another positive integer for additional graders."
        )

    genai_labels = sorted(genai_sources, key=natural_source_key)
    grader_labels = sorted(grader_sources, key=natural_source_key)

    rows: list[dict[str, Any]] = []
    for row in prompt_rows:
        key = (row["question"], row["creature"], row["answer_std"])
        task_id = "" if not row["answer_std"] else task_id_for_unique(*key)

        genai_rows_for_key = {
            label: source_lookup.get(key, {})
            for label, source_lookup in genai_sources.items()
            if row["answer_std"]
        }
        primary_genai = primary_source_row(genai_rows_for_key)
        genai_scores = score_source_values(genai_rows_for_key)
        genai_agreement_score = consensus_score(genai_scores)
        genai_missing_labels = [
            label
            for label in genai_labels
            if row["answer_std"] and not score_text(genai_rows_for_key.get(label, {}).get("score (0-4)"))
        ]

        grader_rows_for_task = {
            label: source_lookup.get(task_id, {})
            for label, source_lookup in grader_sources.items()
            if task_id
        }
        graded_human_scores = {
            label: score_text(source_row.get("score (0-4)"))
            for label, source_row in grader_rows_for_task.items()
            if clean(source_row.get("status")) == "graded"
        }
        human_agreement_score = consensus_score(graded_human_scores)
        human_missing_labels = [
            label
            for label in grader_labels
            if task_id
            and (
                clean(grader_rows_for_task.get(label, {}).get("status")) != "graded"
                or not score_text(grader_rows_for_task.get(label, {}).get("score (0-4)"))
            )
        ]

        old_status = ""
        missing_final_labels: list[str] = []
        if row["answer_std"]:
            if not genai_labels:
                missing_final_labels.append("all GenAI files")
            else:
                missing_final_labels.extend(genai_missing_labels)

            if task_id in required_review_ids:
                if not grader_labels:
                    missing_final_labels.append("all grader files")
                else:
                    missing_final_labels.extend(human_missing_labels)

        if not row["answer_std"]:
            old_status = "auto_blank"
        elif task_id in required_review_ids:
            if not grader_labels or human_missing_labels:
                old_status = "needs_human_scores"
                if require_complete_review:
                    missing_text = ", ".join(human_missing_labels) if human_missing_labels else "all grader files"
                    append_problem(problems, f"Human review incomplete for {row['question']} / {row['creature']} / {row['answer_std'][:80]} (missing: {missing_text})")
            elif not human_agreement_score:
                old_status = "needs_adjudication"
                if require_complete_review:
                    score_texts = ", ".join(f"{label}={score or 'missing'}" for label, score in graded_human_scores.items())
                    append_problem(problems, f"Human disagreement unresolved for {row['question']} / {row['creature']} / {row['answer_std'][:80]}: {score_texts}")
            else:
                old_status = "human_agreement"
        else:
            if not genai_labels or genai_missing_labels:
                old_status = "needs_genai_scores"
                if require_complete_review:
                    missing_text = ", ".join(genai_missing_labels) if genai_missing_labels else "all GenAI files"
                    append_problem(problems, f"GenAI scoring incomplete for {row['question']} / {row['creature']} / {row['answer_std'][:80]} (missing: {missing_text})")
            elif not genai_agreement_score:
                old_status = "needs_genai_adjudication"
                if require_complete_review:
                    score_texts = ", ".join(f"{label}={score or 'missing'}" for label, score in genai_scores.items())
                    append_problem(problems, f"GenAI source disagreement for {row['question']} / {row['creature']} / {row['answer_std'][:80]}: {score_texts}")
            else:
                old_status = "genai" if len(genai_labels) <= 1 else "genai_agreement"

        final_score, final_status, final_note_auto, final_note_manual = auto_final_fields_for_row(
            answer_std=row["answer_std"],
            genai_scores=genai_scores,
            grader_scores=graded_human_scores if task_id in required_review_ids else {},
            missing_labels=list(dict.fromkeys(missing_final_labels)),
            old_status=old_status,
        )
        if (
            require_complete_review
            and row["answer_std"]
            and final_score == FINAL_SCORE_PLACEHOLDER
            and not missing_final_labels
        ):
            append_problem(
                problems,
                f"Retention score conflict for {row['question']} / {row['creature']} / {row['answer_std'][:80]}: {final_note_auto}",
            )

        merged_row: dict[str, Any] = {
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "question": row["question"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
            "genai_score": score_text(primary_genai.get("score (0-4)")),
            "genai_confidence": clean(primary_genai.get("confidence (0-100%)")),
            "genai_note": clean(primary_genai.get("note (optional)")),
            "moment": row["moment"],
            "creature_id": row["creature_id"],
            "question_key": row["question_key"],
            "question_label": row["question_label"],
            "task_id": task_id,
            "occurrence_weight": occurrence_counts.get(key, 1) if row["answer_std"] else 1,
            "final_status": final_status,
            "final_score": final_score,
            "final_note_auto": final_note_auto,
            "final_note_manual": final_note_manual,
        }

        for label in genai_labels:
            source_row = genai_rows_for_key.get(label, {})
            merged_row[f"{label}_score"] = score_text(source_row.get("score (0-4)"))
            merged_row[f"{label}_confidence"] = clean(source_row.get("confidence (0-100%)"))
            merged_row[f"{label}_note"] = clean(source_row.get("note (optional)"))

        for label in grader_labels:
            source_row = grader_rows_for_task.get(label, {})
            merged_row[f"{label}_score"] = score_text(source_row.get("score (0-4)")) if clean(source_row.get("status")) == "graded" else ""
            merged_row[f"{label}_status"] = clean(source_row.get("status"))
            merged_row[f"{label}_note"] = clean(source_row.get("note (optional)"))

        rows.append(merged_row)

    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("moment")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
    ))
    return rows, problems


def write_prompt_score_file(survey_rows: list[dict[str, str]], *, require_complete_review: bool = False) -> tuple[list[dict[str, Any]], list[str]]:
    existing_rows, existing_final_columns, preserve_final_values = load_existing_merged_rows(RETENTION_MERGED_PATH)
    rows, problems = build_prompt_score_rows(survey_rows, require_complete_review=require_complete_review)
    if rows:
        apply_existing_final_values(
            rows,
            existing_rows,
            existing_final_columns,
            preserve_final_values=preserve_final_values,
        )
        fieldnames = merged_score_fieldnames_from_rows(rows, extra_final_fieldnames=existing_final_columns)
        warn_on_non_final_drift(rows, existing_rows, fieldnames)
        write_tsv(RETENTION_MERGED_PATH, fieldnames, rows)
    return rows, problems


def refresh_retention_answers_from_genai(survey_rows: list[dict[str, str]]) -> tuple[int, list[str]]:
    """Regenerate retention_answers.tsv without adding scores.

    This helper name is kept for older callers, but retention_answers.tsv is now
    deliberately an answer-extraction file only. Scores live in the GenAI, grader,
    and merged score files.
    """
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    rows = build_retention_answer_rows(prompt_rows)
    write_tsv(RETENTION_ANSWERS_PATH, RETENTION_ANSWER_FIELDNAMES, rows)
    return len(rows), []
