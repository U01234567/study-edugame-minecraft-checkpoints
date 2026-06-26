from __future__ import annotations

import csv
import hashlib
import io
import html
import json
import math
import re
import shutil
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
    RETENTION_ELEMENT_LABEL_BY_KEY,
    RETENTION_ELEMENT_SPECS,
    RETENTION_COMPONENT_SPECS,
    RETENTION_FINAL_SCORES_PATH,
    RETENTION_PROMPT_TO_ELEMENTS,
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
CREATURE_INFO_PDF_PATH = DATA_CONFIG_DIR / "creature_info.pdf"
SCORING_RUBRICS_PDF_PATH = DATA_CONFIG_DIR / "scoring_rubrics.pdf"
GENAI_PROMPT_RESOURCE_PATH = RESOURCES_DIR / "retention_genai_prompt.txt"
REVIEW_TASKS_PATH = DATA_DIR / "retention_review_tasks.tsv"
RUBRIC_JSON_PATH = RESOURCES_DIR / "retention_rubrics.json"
SCORE_BACKUPS_DIR = DATA_DIR.parent / "score_backups"

# Number of independent GenAI score files to generate in the private route.
# AMOUNT_GENAI=1 writes data/retention_scores_genai.tsv for backward compatibility.
# AMOUNT_GENAI>1 writes data/retention_scores_genai1.tsv,
# data/retention_scores_genai2.tsv, ... data/retention_scores_genai{n}.tsv.
AMOUNT_GENAI = 2
# Number of human-coder files expected by the scoring and merge workflow.
AMOUNT_HUMAN = 2

GENAI_SCORE_PREFIX = "retention_scores_genai"
GRADER_SCORE_PREFIX = "retention_scores_grader"
GENAI_FILENAME_RE = re.compile(r"^retention_scores_genai(\d*)\.tsv$")
GRADER_FILENAME_RE = re.compile(r"^retention_scores_grader(\d+)\.tsv$")

GENAI_LOW_CONFIDENCE_THRESHOLD = 80.0
# Backward-compatible alias for older callers/imports.
LOW_CONFIDENCE_THRESHOLD = GENAI_LOW_CONFIDENCE_THRESHOLD
VALIDATION_SAMPLE_FRACTION = 0.25

RETENTION_ANSWER_FIELDNAMES = [
    "MCID",
    "creature",
    "q_element",
    "answer",
    "answer_std",
]

GENAI_SCORE_FIELDNAMES = [
    "q_element",
    "creature",
    "answer_std",
    "score (0-2)",
    "confidence (0-100%)",
    "note (optional)",
]

GRADER_SCORE_FIELDNAMES = [
    "q_element",
    "creature",
    "answer_std",
    "score (0-2)",
    "status",
    "note (optional)",
    "updated_at",
    "task_id",
]

REVIEW_TASK_FIELDNAMES = [
    "task_id",
    "q_element",
    "question_key",
    "question_label",
    "creature",
    "creature_id",
    "answer_std",
    "answer",
    "occurrence_count",
    "review_reasons",
]

MERGED_SCORE_BASE_FIELDNAMES = [
    "MCID",
    "creature",
    "q_element",
    "answer",
    "answer_std",
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

RETENTION_FINAL_SCORE_FIELDNAMES = ["MCID", "score_immediate", "score_delayed"]

FINAL_SCORE_PLACEHOLDER = "[resolve conflict]"
FINAL_NOTE_MANUAL_NOT_NEEDED = "—"

Q_ELEMENT_ORDER = [key for key, _label in RETENTION_ELEMENT_SPECS]
Q_ELEMENT_SORT_INDEX = {q_element: index for index, q_element in enumerate(Q_ELEMENT_ORDER)}
Q_ELEMENT_LABELS = dict(RETENTION_ELEMENT_SPECS)
# Backward-compatible names retained for older UI code paths.
QUESTION_BY_KEY = {key: key for key in Q_ELEMENT_ORDER}
QUESTION_KEY_BY_QUESTION = {key: key for key in Q_ELEMENT_ORDER}
QUESTION_LABEL_BY_QUESTION = Q_ELEMENT_LABELS
QUESTION_ORDER = Q_ELEMENT_ORDER
QUESTION_SORT_INDEX = Q_ELEMENT_SORT_INDEX
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


def configured_grader_score_paths(amount: int | None = None) -> list[Path]:
    """Return the expected human-coder files for the configured workflow."""
    amount = positive_int(AMOUNT_HUMAN if amount is None else amount)
    return [grader_score_path(index) for index in range(1, amount + 1)]


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


def _normalise_retention_moment(value: object) -> str:
    text = clean(value).lower()
    if text.startswith("imm"):
        return "Immediate"
    if text.startswith("del"):
        return "Delayed"
    return clean(value)


def _valid_final_score(value: object) -> float | None:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer() or int(number) not in {0, 1, 2}:
        return None
    return float(int(number))


def _final_retention_score_text(value: float | None) -> str:
    if value is None:
        return ""
    return f"{float(value):.6g}"


RETENTION_FINAL_SCORE_MODE = "clean"
# Available modes:
#   "clean"                     = mean of creature averages over creatures with administered scores
#   "divide_by_18"              = sum of creature averages divided by 18
#   "card_open_count_penalty"   = each creature average divided by card-open count, then averaged
#   "card_read_seconds_penalty" = each creature average divided by total card-open seconds, then averaged
RETENTION_FINAL_EXPECTED_CREATURE_COUNT = 18
RETENTION_FINAL_MIN_CARD_OPEN_COUNT = 1
RETENTION_FINAL_MIN_CARD_READ_SECONDS = 1.0


def _normalise_retention_creature_id(value: object) -> str:
    """Return a creature id comparable across retention rows and raw logs."""
    text = clean(value).lower()
    if not text:
        return ""
    text = text.replace("minecraft:", "")
    text = re.sub(r"_[a-z]$", "", text)
    text = re.sub(r"[^a-z0-9]+", "_", text).strip("_")

    label_lookup = {
        re.sub(r"[^a-z0-9]+", "_", clean(label).lower()).strip("_"): creature_id
        for creature_id, label in CREATURE_NAME_BY_ID.items()
    }
    return label_lookup.get(text, text)


def _retention_creature_scores_for_participant_moment(
    participant_id: str,
    moment: str,
    creature_ids: set[str],
    scores_by_creature: dict[tuple[str, str, str], dict[str, float]],
) -> dict[str, float]:
    """Return one creature-level score per participant/test occasion/creature.

    This keeps the currently correct logic: Immediate and Delayed are occasions,
    not fixed mappings to Q1/Q2 or Q3/Q4. For each creature, use whichever
    q_elements are actually present in retention_scores_merged for that
    participant and occasion.
    """
    creature_scores: dict[str, float] = {}

    for raw_creature_id in sorted(creature_ids):
        creature_id = _normalise_retention_creature_id(raw_creature_id)
        element_scores = scores_by_creature.get((participant_id, moment, creature_id), {})
        if not element_scores:
            continue

        question_means: list[float] = []
        for _component_key, _component_label, q_elements in RETENTION_COMPONENT_SPECS:
            values = [
                element_scores[q_element]
                for q_element in q_elements
                if q_element in element_scores
            ]
            if values:
                question_means.append(sum(values) / len(values))

        if question_means:
            creature_scores[creature_id] = sum(question_means) / len(question_means)

    return creature_scores


def _candidate_retention_log_paths() -> list[Path]:
    """Return plausible individual-log files without depending on other modules."""
    roots = [
        DATA_DIR / "logs",
        DATA_DIR / "log",
        DATA_DIR / "raw_logs",
        DATA_DIR / "raw",
        DATA_DIR.parent / "logs",
        DATA_DIR.parent / "log",
        DATA_DIR.parent / "raw_logs",
        DATA_DIR.parent / "raw",
    ]
    suffixes = {".csv", ".jsonl", ".ndjson", ".json", ".log", ".txt"}
    paths: list[Path] = []
    seen: set[Path] = set()
    for root in roots:
        if not root.exists() or not root.is_dir():
            continue
        for path in root.rglob("*"):
            if not path.is_file() or path.suffix.lower() not in suffixes:
                continue
            if path.name.startswith("retention_scores_"):
                continue
            resolved = path.resolve()
            if resolved in seen:
                continue
            seen.add(resolved)
            paths.append(path)
    return sorted(paths)


def _coerce_log_datetime(value: object) -> datetime | None:
    text = clean(value)
    if not text:
        return None
    number = parse_numeric(text)
    if number is not None:
        # Treat very large numeric timestamps as milliseconds; otherwise seconds.
        if number > 10_000_000_000:
            number = number / 1000.0
        try:
            return datetime.fromtimestamp(float(number))
        except (OSError, OverflowError, ValueError):
            return None
    try:
        return datetime.fromisoformat(text.replace("Z", "+00:00")).replace(tzinfo=None)
    except ValueError:
        return None


def _event_value(event: dict[str, Any], names: list[str]) -> str:
    fields = event.get("fields") if isinstance(event.get("fields"), dict) else {}
    for name in names:
        value = event.get(name)
        if clean(value):
            return clean(value)
        value = fields.get(name)
        if clean(value):
            return clean(value)
    return ""


def _event_number(event: dict[str, Any], names: list[str]) -> float | None:
    text = _event_value(event, names)
    return parse_numeric(text)


def _event_datetime(event: dict[str, Any]) -> datetime | None:
    return _coerce_log_datetime(_event_value(event, [
        "timestamp",
        "timestamp_dt",
        "datetime",
        "date_time",
        "time",
        "created_at",
        "logged_at",
    ]))


def _event_type(event: dict[str, Any]) -> str:
    return clean(_event_value(event, [
        "event",
        "event_type",
        "type",
        "name",
        "action",
        "message",
    ])).lower()


def _event_participant_id(event: dict[str, Any], fallback: str) -> str:
    value = _event_value(event, [
        "MCID",
        "mcid",
        "participant_id",
        "participant",
        "session_id",
        "session",
        "user_id",
        "player_id",
    ])
    return clean(value or fallback).upper()


def _event_creature_id(event: dict[str, Any]) -> str:
    return _normalise_retention_creature_id(_event_value(event, [
        "creature_id",
        "creature",
        "creature_name",
        "species",
        "entity",
        "entity_type",
        "mob",
        "mob_type",
        "card_creature",
    ]))


def _read_retention_log_events(path: Path) -> list[dict[str, Any]]:
    """Parse CSV, JSON/JSONL, or simple text logs into event-like dictionaries."""
    try:
        encoding = detect_text_encoding(path)
        text = path.read_text(encoding=encoding, errors="replace")
    except OSError:
        return []

    if path.suffix.lower() == ".csv":
        try:
            return [dict(row) for row in csv.DictReader(io.StringIO(text))]
        except csv.Error:
            return []

    stripped = text.strip()
    if not stripped:
        return []

    if path.suffix.lower() == ".json":
        try:
            payload = json.loads(stripped)
        except json.JSONDecodeError:
            payload = None
        if isinstance(payload, list):
            return [item for item in payload if isinstance(item, dict)]
        if isinstance(payload, dict):
            rows = payload.get("events") or payload.get("rows") or payload.get("data")
            if isinstance(rows, list):
                return [item for item in rows if isinstance(item, dict)]
            return [payload]

    events: list[dict[str, Any]] = []
    key_value_re = re.compile(r"([A-Za-z_][A-Za-z0-9_]*)=(\"[^\"]*\"|'[^']*'|[^,\s]+)")
    for line in text.splitlines():
        line = line.strip()
        if not line:
            continue
        try:
            payload = json.loads(line)
        except json.JSONDecodeError:
            payload = None
        if isinstance(payload, dict):
            events.append(payload)
            continue

        lowered = line.lower()
        if "creature_card_open" not in lowered and "creature_card_close" not in lowered:
            continue
        event_name = "creature_card_closed" if "creature_card_close" in lowered else "creature_card_opened"
        event: dict[str, Any] = {"event": event_name, "raw": line}
        for key, value in key_value_re.findall(line):
            event[key] = value.strip("'\"")
        events.append(event)
    return events


def _retention_card_exposure_index() -> tuple[dict[tuple[str, str], int], dict[tuple[str, str], float], list[str]]:
    """Return per-participant/per-creature card-open counts and card-open seconds.

    This is intentionally local to _retention_coding.py. It does not rely on
    sum_merged internals. It counts creature_card_opened events and estimates
    card-open seconds from explicit duration fields on close events, falling back
    to close timestamp minus the latest unmatched open timestamp for the same
    participant/creature.
    """
    open_counts: dict[tuple[str, str], int] = defaultdict(int)
    open_seconds: dict[tuple[str, str], float] = defaultdict(float)
    open_stacks: dict[tuple[str, str], list[datetime]] = defaultdict(list)
    warnings: list[str] = []

    paths = _candidate_retention_log_paths()
    if not paths:
        return open_counts, open_seconds, [
            "No individual log files were found for the selected retention final-score card-exposure penalty."
        ]

    for path in paths:
        events = _read_retention_log_events(path)
        if not events:
            continue
        fallback_participant = path.stem
        for event in events:
            event_name = _event_type(event)
            if "creature_card" not in event_name:
                continue
            is_open = "open" in event_name and "close" not in event_name
            is_close = "close" in event_name or "closed" in event_name
            if not is_open and not is_close:
                continue

            participant_id = _event_participant_id(event, fallback_participant)
            creature_id = _event_creature_id(event)
            if not participant_id or not creature_id:
                continue

            key = (participant_id, creature_id)
            if is_open:
                open_counts[key] += 1
                timestamp = _event_datetime(event)
                if timestamp is not None:
                    open_stacks[key].append(timestamp)
                continue

            duration_ms = _event_number(event, [
                "read_duration_ms",
                "duration_ms",
                "open_duration_ms",
                "card_open_duration_ms",
                "card_duration_ms",
                "elapsed_ms",
            ])
            if duration_ms is not None and duration_ms >= 0:
                open_seconds[key] += float(duration_ms) / 1000.0
                if open_stacks.get(key):
                    open_stacks[key].pop()
                continue

            duration_seconds = _event_number(event, [
                "read_duration_seconds",
                "duration_seconds",
                "open_duration_seconds",
                "card_open_duration_seconds",
                "card_duration_seconds",
                "elapsed_seconds",
                "seconds",
            ])
            if duration_seconds is not None and duration_seconds >= 0:
                open_seconds[key] += float(duration_seconds)
                if open_stacks.get(key):
                    open_stacks[key].pop()
                continue

            closed_at = _event_datetime(event)
            if closed_at is not None and open_stacks.get(key):
                opened_at = open_stacks[key].pop()
                seconds = (closed_at - opened_at).total_seconds()
                if seconds >= 0:
                    open_seconds[key] += seconds

    if not open_counts and not open_seconds:
        warnings.append(
            "Individual log files were found, but no creature-card exposure events could be matched."
        )
    return open_counts, open_seconds, warnings


def _participant_moment_retention_final_score(
    participant_id: str,
    moment: str,
    creature_ids: set[str],
    scores_by_creature: dict[tuple[str, str, str], dict[str, float]],
    *,
    card_open_counts: dict[tuple[str, str], int] | None = None,
    card_open_seconds: dict[tuple[str, str], float] | None = None,
) -> float | None:
    """Return the final participant/moment score using the selected mode.

    The base creature score always uses the correct administered-q_element logic:
    it does not map Immediate to Q1/Q2 or Delayed to Q3/Q4.
    """
    creature_scores = _retention_creature_scores_for_participant_moment(
        participant_id,
        moment,
        creature_ids,
        scores_by_creature,
    )
    if not creature_scores:
        return None

    if RETENTION_FINAL_SCORE_MODE == "clean":
        return sum(creature_scores.values()) / len(creature_scores)

    if RETENTION_FINAL_SCORE_MODE == "divide_by_18":
        return sum(creature_scores.values()) / float(RETENTION_FINAL_EXPECTED_CREATURE_COUNT)

    if RETENTION_FINAL_SCORE_MODE == "card_open_count_penalty":
        adjusted_scores: list[float] = []
        for creature_id, score in creature_scores.items():
            opened = RETENTION_FINAL_MIN_CARD_OPEN_COUNT
            if card_open_counts is not None:
                opened = max(
                    RETENTION_FINAL_MIN_CARD_OPEN_COUNT,
                    int(card_open_counts.get((participant_id, creature_id), 0) or 0),
                )
            adjusted_scores.append(score / opened)
        return sum(adjusted_scores) / len(adjusted_scores) if adjusted_scores else None

    if RETENTION_FINAL_SCORE_MODE == "card_read_seconds_penalty":
        adjusted_scores = []
        for creature_id, score in creature_scores.items():
            seconds = RETENTION_FINAL_MIN_CARD_READ_SECONDS
            if card_open_seconds is not None:
                seconds = max(
                    RETENTION_FINAL_MIN_CARD_READ_SECONDS,
                    float(card_open_seconds.get((participant_id, creature_id), 0.0) or 0.0),
                )
            adjusted_scores.append(score / seconds)
        return sum(adjusted_scores) / len(adjusted_scores) if adjusted_scores else None

    raise ValueError(
        "RETENTION_FINAL_SCORE_MODE must be one of: "
        "clean, divide_by_18, card_open_count_penalty, card_read_seconds_penalty"
    )


def build_retention_scores_final_rows(scoring_rows: list[dict[str, Any]]) -> tuple[list[dict[str, str]], list[str]]:
    """Build participant-level final retention scores from complete merged final_score rows.

    The output is intentionally narrow: MCID, score_immediate, and score_delayed.
    Scores stay on the 0-2 rubric scale before any selected penalty mode is
    applied. Missing test occasions are written as empty cells. If any available
    q_element row has a missing/invalid final_score, no rows are returned so
    callers do not publish a partial final-score file.

    Scoring is creature-first: within each participant and moment, each creature
    receives one average from the administered question components present for
    that creature. Immediate and Delayed are test occasions, not fixed mappings
    to Q1/Q2 or Q3/Q4.
    """
    warnings: list[str] = []
    if not scoring_rows:
        return [], ["No retention_scores_merged rows were available for retention_scores_final.tsv."]

    known_elements = {key for key, _label in RETENTION_ELEMENT_SPECS}
    scores_by_creature: dict[tuple[str, str, str], dict[str, float]] = defaultdict(dict)
    creatures_by_participant_moment: dict[tuple[str, str], set[str]] = defaultdict(set)
    all_mcids: set[str] = set()
    invalid_rows: list[str] = []
    skipped_rows = 0

    for index, row in enumerate(scoring_rows, start=2):
        participant_id = clean(row.get("MCID")).upper()
        moment = _normalise_retention_moment(row.get("moment"))
        creature_id = _normalise_retention_creature_id(row.get("creature_id"))
        q_element = clean(row.get("q_element"))
        if not participant_id:
            skipped_rows += 1
            continue
        all_mcids.add(participant_id)
        if moment not in {"Immediate", "Delayed"} or not creature_id or q_element not in known_elements:
            skipped_rows += 1
            continue

        creatures_by_participant_moment[(participant_id, moment)].add(creature_id)
        score = _valid_final_score(row.get("final_score"))
        if score is None:
            invalid_rows.append(str(index))
            continue
        scores_by_creature[(participant_id, moment, creature_id)][q_element] = score

    if invalid_rows:
        preview = ", ".join(invalid_rows[:25])
        suffix = f"; plus {len(invalid_rows) - 25} more" if len(invalid_rows) > 25 else ""
        return [], [
            "retention_scores_final.tsv was not written because retention_scores_merged.tsv still has "
            f"missing/invalid final_score value(s) on row(s): {preview}{suffix}."
        ]

    if skipped_rows:
        return [], [
            "retention_scores_final.tsv was not written because retention_scores_merged.tsv has "
            f"{skipped_rows} row(s) with missing/unknown MCID, moment, creature_id, or q_element."
        ]

    card_open_counts: dict[tuple[str, str], int] | None = None
    card_open_seconds: dict[tuple[str, str], float] | None = None
    if RETENTION_FINAL_SCORE_MODE in {"card_open_count_penalty", "card_read_seconds_penalty"}:
        card_open_counts, card_open_seconds, exposure_warnings = _retention_card_exposure_index()
        warnings.extend(exposure_warnings)

    participant_scores: dict[str, dict[str, float | None]] = {
        participant_id: {"Immediate": None, "Delayed": None}
        for participant_id in all_mcids
    }

    for (participant_id, moment), creature_ids in creatures_by_participant_moment.items():
        participant_scores.setdefault(participant_id, {"Immediate": None, "Delayed": None})[moment] = (
            _participant_moment_retention_final_score(
                participant_id,
                moment,
                creature_ids,
                scores_by_creature,
                card_open_counts=card_open_counts,
                card_open_seconds=card_open_seconds,
            )
        )

    output_rows = [
        {
            "MCID": participant_id,
            "score_immediate": _final_retention_score_text(scores.get("Immediate")),
            "score_delayed": _final_retention_score_text(scores.get("Delayed")),
        }
        for participant_id, scores in sorted(participant_scores.items())
    ]
    return output_rows, warnings


def write_retention_scores_final_if_complete(
    scoring_rows: list[dict[str, Any]],
    path: Path = RETENTION_FINAL_SCORES_PATH,
) -> tuple[bool, int, list[str]]:
    """Write data/retention_scores_final.tsv only when all merged final_score values are usable."""
    rows, warnings = build_retention_scores_final_rows(scoring_rows)
    if not rows:
        return False, 0, warnings
    write_tsv(path, RETENTION_FINAL_SCORE_FIELDNAMES, rows)
    return True, len(rows), warnings


def _score_key(value: object) -> str:
    text = clean(value)
    if not text:
        return ""
    try:
        number = int(float(text))
        return str(number)
    except (TypeError, ValueError):
        return text


def _rubric_template_label_entries(score_content: Any) -> list[dict[str, str]]:
    """Return ordered label definitions for a compact rubric-template score."""
    entries: list[dict[str, str]] = []
    if isinstance(score_content, list):
        for index, item in enumerate(score_content, start=1):
            if isinstance(item, dict):
                label = clean(item.get("label") or item.get("text") or item.get("description"))
                label_id = clean(item.get("id")) or f"label_{index:02d}"
            else:
                label = clean(item)
                label_id = f"label_{index:02d}"
            if label:
                entries.append({"id": label_id, "label": label})
        return entries

    if isinstance(score_content, dict):
        for index, (label_id, label_value) in enumerate(score_content.items(), start=1):
            if isinstance(label_value, dict):
                label = clean(label_value.get("label") or label_value.get("text") or label_value.get("description"))
                entry_id = clean(label_value.get("id")) or clean(label_id) or f"label_{index:02d}"
            else:
                label = clean(label_value)
                entry_id = clean(label_id) or f"label_{index:02d}"
            if label:
                entries.append({"id": entry_id, "label": label})
        return entries

    label = clean(score_content)
    return [{"id": "label_01", "label": label}] if label else []


def _has_compact_template_labels(template_scores: dict[str, Any]) -> bool:
    return any(_rubric_template_label_entries(content) for content in (template_scores or {}).values())


def _has_compact_examples(example_entry: dict[str, Any]) -> bool:
    scores = example_entry.get("scores") if isinstance(example_entry, dict) else None
    if not isinstance(scores, dict):
        return False
    for score_content in scores.values():
        if isinstance(score_content, dict):
            if any(clean(value) for value in score_content.values()):
                return True
        elif isinstance(score_content, list):
            if any(clean(value) for value in score_content):
                return True
        elif clean(score_content):
            return True
    return False


def _materialise_compact_scores(template_scores: dict[str, Any], example_scores: dict[str, Any]) -> dict[str, Any]:
    """Connect creature-specific examples to the score-label templates.

    In resources/retention_rubrics.json, repeated score labels live once in
    rubric_templates. Creature-specific sections only store examples keyed by
    those label ids, plus a note. This function expands that compact source to
    the table shape consumed by the scoring app and PDF renderers.
    """
    merged: dict[str, Any] = {}
    all_scores = ["2", "1", "0"]
    for key in template_scores:
        score = _score_key(key)
        if score and score not in all_scores:
            all_scores.append(score)
    for key in example_scores:
        score = _score_key(key)
        if score and score not in all_scores:
            all_scores.append(score)

    for score in all_scores:
        template_content = template_scores.get(score, template_scores.get(int(score)) if score.isdigit() else None)
        example_content = example_scores.get(score, example_scores.get(int(score)) if score.isdigit() else None)
        label_entries = _rubric_template_label_entries(template_content)
        if label_entries:
            score_rows: dict[str, str] = {}
            examples_by_id = example_content if isinstance(example_content, dict) else {}
            for label_entry in label_entries:
                label_id = clean(label_entry.get("id"))
                label = clean(label_entry.get("label"))
                example_value = ""
                if isinstance(examples_by_id, dict):
                    example_value = clean(examples_by_id.get(label_id)) or clean(examples_by_id.get(label))
                score_rows[label] = example_value
            merged[score] = score_rows
        elif isinstance(example_content, dict):
            merged[score] = {clean(key): clean(value) for key, value in example_content.items() if clean(key) or clean(value)}
        elif isinstance(example_content, list):
            merged[score] = [clean(value) for value in example_content if clean(value)]
        elif clean(example_content):
            merged[score] = clean(example_content)
    return merged


def _question_config(rubric: dict[str, Any], q_element: str, default_label: str) -> dict[str, Any]:
    questions = rubric.get("questions") or {}
    raw = questions.get(q_element)
    if isinstance(raw, dict):
        config = dict(raw)
    else:
        config = {"title": clean(raw) or default_label}
    config.setdefault("title", default_label)
    config.setdefault("short_title", clean((rubric.get("question_short_labels") or {}).get(q_element)) or default_label)
    config.setdefault("template", q_element)
    return config


def normalise_rubric_json(rubric: dict[str, Any]) -> dict[str, Any]:
    """Expand the compact q_element rubric schema used by this repository.

    The JSON source intentionally stores repeated score-label links only once in
    rubric_templates. Creature-specific rubric dictionaries store only a note and
    examples keyed to those template labels. This normaliser materialises the
    older question_rubric_tables/rubrics shape used by the existing scoring apps
    and PDF renderers.
    """
    rubric = dict(rubric)
    question_short_labels = dict(rubric.get("question_short_labels") or {})
    templates = rubric.get("rubric_templates") or {}
    examples = rubric.get("rubric_examples") or rubric.get("question_rubric_examples") or {}
    creatures = rubric.get("creatures") or {}

    if templates and not rubric.get("question_rubric_tables"):
        tables: dict[str, Any] = {}
        for q_element, label in RETENTION_ELEMENT_SPECS:
            config = _question_config(rubric, q_element, label)
            template_key = clean(config.get("template")) or q_element
            template = templates.get(template_key) or templates.get(q_element) or {}
            title = clean(config.get("title")) or clean(template.get("title")) or label
            short_title = clean(config.get("short_title")) or clean(template.get("short_title")) or clean(question_short_labels.get(q_element)) or label
            intro = clean(config.get("intro")) or clean(template.get("intro"))
            template_scores = template.get("scores") or {}
            per_creature = examples.get(q_element) or {}
            should_render_rows = _has_compact_template_labels(template_scores) or any(
                _has_compact_examples(entry if isinstance(entry, dict) else {})
                for entry in (per_creature or {}).values()
            )
            rows: list[dict[str, Any]] = []
            if should_render_rows:
                for creature_id, creature in sorted(creatures.items(), key=lambda item: clean((item[1] or {}).get("name")).lower()):
                    example_entry = per_creature.get(creature_id) or {}
                    if not isinstance(example_entry, dict):
                        example_entry = {"scores": example_entry}
                    rows.append({
                        "creature_id": creature_id,
                        "creature": clean((creature or {}).get("name")) or creature_id,
                        "note": clean(example_entry.get("note")),
                        "scores": _materialise_compact_scores(template_scores, example_entry.get("scores") or {}),
                    })
            tables[q_element] = {
                "title": title,
                "short_title": short_title,
                "intro": intro,
                "rows": rows,
            }
        rubric["question_rubric_tables"] = tables

    rubric.setdefault("questions", {key: label for key, label in RETENTION_ELEMENT_SPECS})
    rubric.setdefault("rubrics", {
        key: {"title": label}
        for key, label in RETENTION_ELEMENT_SPECS
    })
    rubric.setdefault("question_short_labels", {key: label for key, label in RETENTION_ELEMENT_SPECS})
    return rubric


def load_rubric_json(path: Path = RUBRIC_JSON_PATH) -> dict[str, Any]:
    if not path.exists():
        raise FileNotFoundError(f"Missing rubric JSON source: {path}")
    return normalise_rubric_json(json.loads(path.read_text(encoding="utf-8")))


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
                answer = clean(row.get(retention_column_name(slot_index, question_key)))
                for q_element in RETENTION_PROMPT_TO_ELEMENTS.get(question_key, []):
                    rows.append({
                        "participant_id": participant_id,
                        "moment": moment,
                        "creature_id": creature_id,
                        "creature": CREATURE_NAME_BY_ID.get(creature_id, creature_id),
                        "q_element": q_element,
                        "question_key": question_key,
                        "question_label": RETENTION_ELEMENT_LABEL_BY_KEY.get(q_element, q_element),
                        "answer": answer,
                        "answer_std": standardise_answer(answer),
                    })

    rows.sort(key=lambda item: (
        clean(item.get("participant_id")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("q_element")), 999),
        clean(item.get("moment")),
    ))
    return rows


def build_retention_answer_rows(prompt_rows: list[dict[str, str]]) -> list[dict[str, str]]:
    rows = [
        {
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "q_element": row["q_element"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
        }
        for row in prompt_rows
    ]
    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("q_element")), 999),
        clean(item.get("answer_std")),
    ))
    return rows


def genai_group_key(row: dict[str, str], multi_creature_keys: set[tuple[str, str]]) -> tuple[str, str, str]:
    question = clean(row.get("q_element"))
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
        key = (clean(row.get("q_element")), clean(row.get("creature")), clean(row.get("answer_std")))
        if all(key):
            lookup[key] = row
    return lookup


def build_unique_genai_rows(prompt_rows: list[dict[str, str]], *, existing_path: Path | None = None) -> list[dict[str, str]]:
    nonblank = [row for row in prompt_rows if clean(row.get("answer_std"))]
    creatures_by_question_answer: dict[tuple[str, str], set[str]] = defaultdict(set)
    for row in nonblank:
        creatures_by_question_answer[(row["q_element"], row["answer_std"])].add(row["creature"])
    multi_creature_keys = {key for key, creatures in creatures_by_question_answer.items() if len(creatures) > 1}

    grouped: dict[tuple[str, str, str], dict[str, str]] = {}
    for row in nonblank:
        key = genai_group_key(row, multi_creature_keys)
        grouped.setdefault(key, {
            "q_element": key[0],
            "creature": key[1],
            "answer_std": key[2],
            "score (0-2)": "",
            "confidence (0-100%)": "",
            "note (optional)": "",
        })

    existing = existing_genai_lookup(existing_path)
    rows: list[dict[str, str]] = []
    for key, row in grouped.items():
        previous = existing.get(key, {})
        merged = dict(row)
        for field in ("score (0-2)", "confidence (0-100%)", "note (optional)"):
            if clean(previous.get(field)):
                merged[field] = clean(previous.get(field))
        rows.append(merged)

    rows.sort(key=lambda item: (
        QUESTION_SORT_INDEX.get(clean(item.get("q_element")), 999),
        clean(item.get("creature")).lower(),
        clean(item.get("answer_std")),
    ))
    return rows


def html_text(value: object) -> str:
    return html.escape(clean(value)).replace("\n", "<br>")


def _normalise_rubric_token_lines(text: str) -> str:
    """Attach standalone [SRC]/[FAN] fragments to the intended example lines.

    The compact JSON intentionally stores examples as one human/AI-readable
    answer per line. During generation some component markers may temporarily
    appear on their own line. This normaliser keeps line breaks between answer
    examples, while turning fragments such as ``abyss deer\n[SRC]\n[FAN]\nabyss
    deer`` into ``abyss deer [SRC]\n[FAN] abyss deer``.
    """
    token_line = re.compile(r"^\[(?:SRC|FAN)\]$")
    lines = [re.sub(r"\s+", " ", line.strip()) for line in text.split("\n")]
    lines = [line for line in lines if line]
    if not any(token_line.fullmatch(line) for line in lines):
        return "\n".join(lines)

    out: list[str] = []
    index = 0
    while index < len(lines):
        line = lines[index]
        if not token_line.fullmatch(line):
            out.append(line)
            index += 1
            continue

        tokens: list[str] = []
        while index < len(lines) and token_line.fullmatch(lines[index]):
            tokens.append(lines[index])
            index += 1

        has_previous = bool(out)
        has_next = index < len(lines)
        if has_previous and has_next:
            out[-1] = f"{out[-1]} {tokens[0]}".strip()
            if len(tokens) > 1:
                lines[index] = f"{' '.join(tokens[1:])} {lines[index]}".strip()
        elif has_previous:
            out[-1] = f"{out[-1]} {' '.join(tokens)}".strip()
        elif has_next:
            lines[index] = f"{' '.join(tokens)} {lines[index]}".strip()
        else:
            out.extend(tokens)

    return "\n".join(re.sub(r"[ \t]{2,}", " ", line).strip() for line in out if line.strip())


def _normalise_rubric_token_spacing(
    value: object,
    *,
    collapse: bool = False,
    join_token_fragments: bool = False,
) -> str:
    text = clean(value).replace("\u00a0", " ")
    if not text:
        return ""

    text = text.replace("\r\n", "\n").replace("\r", "\n")
    if collapse:
        text = re.sub(r"\s+", " ", text).strip()
    else:
        text = re.sub(r"[ \t]*\n[ \t]*", "\n", text).strip()
        if join_token_fragments:
            text = _normalise_rubric_token_lines(text)

    # Slash-separated components are inline labels, not separate examples.
    text = re.sub(
        r"\s*/\s*(\[(?:SRC|FAN)\])\s*/\s*(\[(?:SRC|FAN)\])\s*",
        r" / \1 / \2 ",
        text,
    )
    text = re.sub(r"\(\s+", "(", text)
    text = re.sub(r"\s+\)", ")", text)
    text = re.sub(r"\s+([,.;:])", r"\1", text)

    if collapse:
        text = re.sub(r"\s+", " ", text)
    else:
        text = "\n".join(re.sub(r"[ \t]{2,}", " ", line).strip() for line in text.split("\n"))
    return text.strip()


def _rubric_label_has_inline_tokens(label: object) -> bool:
    text = clean(label)
    return bool(re.search(r"\[(?:SRC|FAN)\]", text))


def html_text_with_tokens(
    value: object,
    *,
    collapse: bool = False,
    join_token_fragments: bool = False,
) -> str:
    text = _normalise_rubric_token_spacing(
        value,
        collapse=collapse,
        join_token_fragments=join_token_fragments,
    )
    escaped = html.escape(text).replace("\n", "<br>")
    return re.sub(
        r"\[(SRC|FAN)\]",
        r'<span class="rubric-token-cobalt">[\1]</span>',
        escaped,
    )


def _appendix_html_css() -> str:
    """CSS counterpart of the ReportLab rubric PDF layout.

    Keep this in sync with _retention_pdf_styles(). The selectors are deliberately
    specific and use !important where needed because the scoring app also has
    generic .score-number, .score-table, and .generated-rubric-inner-table rules
    that otherwise overwrite the appendix/rubric appearance.
    """
    return """
    :root {
      --thesis-title: #28393B;
      --thesis-h2: #35506B;
      --thesis-h3: #567087;
      --thesis-blue: #3C78D8;
      --rule: #000000;
      --table-header: #DCEBEC;
      --score-2: #93C47D;
      --score-2-content: #D9EAD3;
      --score-2-label: #B6D7A8;
      --score-1: #F6B26B;
      --score-1-content: #FCE5CD;
      --score-1-label: #F9CB9C;
      --score-0: #E06666;
      --score-0-content: #F4CCCC;
      --score-0-label: #EA9999;
      --example-bg: #F3F3F3;
    }
    .retention-rubric-appendix {
      box-sizing: border-box !important;
      max-width: 980px;
      margin: 0 auto;
      padding: 28px;
      color: #000000;
      font-family: Georgia, 'Times New Roman', serif !important;
      font-size: 14px;
      line-height: 1.18;
      background: #ffffff;
    }
    .retention-rubric-appendix * { box-sizing: border-box; }
    .retention-rubric-appendix h1 {
      margin: 0 0 8px !important;
      color: var(--thesis-title) !important;
      font-family: Georgia, 'Times New Roman', serif !important;
      font-size: 28px !important;
      line-height: 1.12 !important;
      font-weight: 700 !important;
    }
    .retention-rubric-appendix h2 {
      margin: 22px 0 8px !important;
      color: var(--thesis-h2) !important;
      font-family: Georgia, 'Times New Roman', serif !important;
      font-size: 23px !important;
      line-height: 1.12 !important;
      font-weight: 700 !important;
    }
    .retention-rubric-appendix h3 {
      margin: 14px 0 6px !important;
      color: var(--thesis-h3) !important;
      font-family: Georgia, 'Times New Roman', serif !important;
      font-size: 17px !important;
      line-height: 1.15 !important;
      font-weight: 700 !important;
    }
    .retention-rubric-appendix p { margin: 0 0 10px !important; }
    .retention-rubric-appendix ul {
      margin: 4px 0 12px 24px !important;
      padding: 0 !important;
    }
    .retention-rubric-appendix li { margin: 2px 0 !important; padding-left: 4px !important; }
    .retention-rubric-appendix .appendix-title-rule {
      border: 0 !important;
      border-top: 1px solid var(--rule) !important;
      margin: 8px 0 18px !important;
    }
    .retention-rubric-appendix .appendix-rubric-block {
      margin: 0 0 18px !important;
      break-inside: avoid;
      page-break-inside: avoid;
    }
    .retention-rubric-appendix .rubric-note-line {
      margin: 0 0 6px !important;
      font-size: 13px !important;
    }
    .retention-rubric-appendix .rubric-token-cobalt {
      color: var(--thesis-blue) !important;
      font-weight: 700 !important;
    }
    .retention-rubric-appendix .appendix-rubric-table,
    .retention-rubric-appendix table.appendix-rubric-table.score-table {
      width: 100% !important;
      border-collapse: collapse !important;
      table-layout: fixed !important;
      margin: 4px 0 14px !important;
      border: 1px solid var(--rule) !important;
      font-family: Georgia, 'Times New Roman', serif !important;
    }
    .retention-rubric-appendix .appendix-rubric-table th,
    .retention-rubric-appendix .appendix-rubric-table td {
      border: 1px solid var(--rule) !important;
      vertical-align: top !important;
      padding: 7px !important;
      text-align: left !important;
      text-transform: none !important;
      letter-spacing: 0 !important;
      font-family: Georgia, 'Times New Roman', serif !important;
    }
    .retention-rubric-appendix .appendix-rubric-table th {
      background: var(--table-header) !important;
      color: #000000 !important;
      font-weight: 700 !important;
      font-size: 13px !important;
    }
    .retention-rubric-appendix .appendix-score-col { width: 62px !important; }
    .retention-rubric-appendix .appendix-score-cell { text-align: left !important; }
    .retention-rubric-appendix .score-number {
      display: inline !important;
      width: auto !important;
      height: auto !important;
      border-radius: 0 !important;
      background: transparent !important;
      color: #ffffff !important;
      font-size: 26px !important;
      font-weight: 700 !important;
      line-height: 1 !important;
    }
    .retention-rubric-appendix .score-bg-2 { background: var(--score-2) !important; }
    .retention-rubric-appendix .score-bg-1 { background: var(--score-1) !important; }
    .retention-rubric-appendix .score-bg-0 { background: var(--score-0) !important; }
    .retention-rubric-appendix .content-bg-2 { background: var(--score-2-content) !important; }
    .retention-rubric-appendix .content-bg-1 { background: var(--score-1-content) !important; }
    .retention-rubric-appendix .content-bg-0 { background: var(--score-0-content) !important; }
    .retention-rubric-appendix .rubric-inner-table,
    .retention-rubric-appendix table.generated-rubric-inner-table {
      width: 100% !important;
      border-collapse: collapse !important;
      table-layout: fixed !important;
      margin: 0 !important;
    }
    .retention-rubric-appendix .rubric-inner-table td {
      border: 1px solid var(--rule) !important;
      vertical-align: top !important;
      padding: 6px !important;
      font-size: 12px !important;
      line-height: 1.15 !important;
      color: #000000 !important;
    }
    .retention-rubric-appendix .rubric-inner-table td:first-child {
      width: 66% !important;
      font-weight: 400 !important;
    }
    .retention-rubric-appendix .rubric-inner-table .inner-label-2 { background: var(--score-2-label) !important; }
    .retention-rubric-appendix .rubric-inner-table .inner-label-1 { background: var(--score-1-label) !important; }
    .retention-rubric-appendix .rubric-inner-table .inner-label-0 { background: var(--score-0-label) !important; }
    .retention-rubric-appendix .rubric-inner-table .inner-example { background: var(--example-bg) !important; }
    .retention-rubric-appendix .rubric-content-list { margin: 0 0 0 18px !important; }
    .retention-rubric-appendix .rubric-content-text { margin: 0 !important; }
    .retention-rubric-appendix .small { font-size: 13px !important; }
    """


def _instruction_sections_html(rubric: dict[str, Any]) -> str:
    sections = _instruction_sections_from_html(rubric.get("instructions_html"))
    if not sections:
        fallback = clean(rubric.get("instructions_html")) or "<p>No general scoring instructions are configured.</p>"
        return fallback
    parts: list[str] = ["<section class=\"appendix-instructions-block\">", "<h2>Instructions</h2>"]
    for section in sections:
        parts.append(f"<h3>{html_text(section.get('title'))}</h3>")
        current_list: list[str] = []
        def flush_list() -> None:
            nonlocal current_list
            if current_list:
                parts.append("<ul>" + "".join(current_list) + "</ul>")
                current_list = []
        for item in section.get("items", []):
            item_html = html_text_with_tokens(item.get("text"))
            if item.get("type") == "bullet":
                current_list.append(f"<li>{item_html}</li>")
            else:
                flush_list()
                parts.append(f"<p>{item_html}</p>")
        flush_list()
    parts.append("</section>")
    return "\n".join(parts)


def _content_rows_for_html(content: Any) -> list[tuple[str, str]]:
    if isinstance(content, dict):
        rows = [(clean(label), clean(examples)) for label, examples in content.items()]
        return rows or [("", "—")]
    if isinstance(content, list):
        rows: list[tuple[str, str]] = []
        for item in content:
            if isinstance(item, dict):
                rows.extend((clean(key), clean(value)) for key, value in item.items())
            elif clean(item):
                rows.append(("", clean(item)))
        return rows or [("", "—")]
    return [("", clean(content) or "—")]


def render_rubric_content_html(content: Any, score: object = "") -> str:
    """Render a rubric score's possible answers in the same mini-table style as the PDF."""
    score_key = clean(score)
    rows = _content_rows_for_html(content)
    if len(rows) == 1 and not rows[0][0] and rows[0][1] == "—":
        return '<p class="rubric-content-text">—</p>'
    return (
        '<table class="rubric-inner-table generated-rubric-inner-table"><tbody>'
        + "".join(
            "<tr>"
            f'<td class="inner-label-{html.escape(score_key)}">'
            f'{html_text_with_tokens(label or "—", collapse=True)}</td>'
            f'<td class="inner-example">'
            f'{html_text_with_tokens(examples or "—", join_token_fragments=_rubric_label_has_inline_tokens(label))}</td>'
            "</tr>"
            for label, examples in rows
        )
        + "</tbody></table>"
    )


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
            "note": clean(table.get("note")) or clean(table.get("rubric_note")),
        })

    for entry in table.get("rows") or []:
        if isinstance(entry, dict) and isinstance(entry.get("scores"), dict):
            append_score_rows(entry["scores"], {
                "creature_id": clean(entry.get("creature_id")),
                "creature": clean(entry.get("creature") or ""),
                "note": clean(entry.get("note")) or clean(entry.get("rubric_note")),
            })

    return rows


def expanded_rubric_rows_grouped_for_html(table: dict[str, Any], score_scale: list[Any]) -> list[dict[str, Any]]:
    groups: list[dict[str, Any]] = []
    source_rows_by_key: dict[str, dict[str, Any]] = {}
    for source_row in table.get("rows") or []:
        key = clean(source_row.get("creature_id")) or clean(source_row.get("creature"))
        if key:
            source_rows_by_key[key] = source_row
    for row in expanded_rubric_rows(table, score_scale):
        creature_key = clean(row.get("creature_id")) or clean(row.get("creature"))
        if not groups or groups[-1]["key"] != creature_key:
            source_row = source_rows_by_key.get(creature_key, {})
            groups.append({
                "key": creature_key,
                "creature": clean(row.get("creature")),
                "note": clean(source_row.get("note")) or clean(source_row.get("rubric_note")) or "—",
                "rows": [],
            })
        groups[-1]["rows"].append(row)
    return groups


def render_rubric_table_html(rows: list[dict[str, Any]], *, selectable: bool = False) -> str:
    body: list[str] = []
    for row in rows:
        score = clean(row.get("score"))
        selectable_attrs = ""
        selectable_class = ""
        if selectable:
            selectable_class = " rubric-score-row"
            selectable_attrs = f' data-score="{html.escape(score)}" tabindex="0" aria-disabled="false"'
        body.append(
            f'<tr class="appendix-rubric-score-row score-row-{html.escape(score)}{selectable_class}"{selectable_attrs}>'
            f'<td class="appendix-score-cell score-bg-{html.escape(score)}"><span class="score-number">{html_text(score or "—")}</span></td>'
            f'<td class="appendix-content-cell content-bg-{html.escape(score)}">{render_rubric_content_html(row.get("content"), score)}</td>'
            "</tr>"
        )
    return (
        '<table class="appendix-rubric-table">'
        '<colgroup><col class="appendix-score-col"><col></colgroup>'
        '<thead><tr><th>Score</th><th>Possible answers</th></tr></thead>'
        '<tbody>' + "".join(body) + '</tbody></table>'
    )


def render_single_creature_rubric_block_html(group: dict[str, Any]) -> str:
    note = clean(group.get("note")) or "—"
    return (
        '<section class="appendix-rubric-block">'
        f'<h3>{html_text(clean(group.get("creature")) or "Creature")}</h3>'
        f'<p class="rubric-note-line">Note: {html_text_with_tokens(note)}</p>'
        f'{render_rubric_table_html(group.get("rows") or [])}'
        '</section>'
    )


def render_question_rubric_section_html(question_key: str, table: dict[str, Any], score_scale: list[Any]) -> str:
    q_title = clean(table.get("title")) or clean(table.get("short_title")) or question_key
    groups = expanded_rubric_rows_grouped_for_html(table, list(reversed(score_scale)))
    parts: list[str] = [f'<section class="appendix-question-section" data-question="{html.escape(question_key)}">', f'<h2>{html_text(q_title)}</h2>']
    if clean(table.get("intro")):
        parts.append(f'<p>{html_text_with_tokens(table.get("intro"))}</p>')
    if not groups:
        parts.append('<p class="small">No creature-specific rubric rows are configured for this question element.</p>')
    else:
        parts.extend(render_single_creature_rubric_block_html(group) for group in groups)
    parts.append('</section>')
    return "\n".join(parts)


def render_rubric_question_tabs_html(rubric: dict[str, Any], *, first_question: str | None = None) -> str:
    question_tables = rubric.get("question_rubric_tables") or {}
    score_scale = rubric.get("score_scale", [0, 1, 2])
    question_labels = rubric.get("question_short_labels") or {}
    question_order = [key for key, _label in RETENTION_ELEMENT_SPECS if key in question_tables]
    first = first_question if first_question in question_order else (question_order[0] if question_order else "")
    tabs = "\n".join(
        f'''<button class="rubric-subtab-button {'active' if question_key == first else ''}" type="button" role="tab" data-rubric-question="{html.escape(question_key)}" aria-selected="{'true' if question_key == first else 'false'}">
              {html.escape(clean(question_labels.get(question_key)) or QUESTION_BY_KEY.get(question_key, question_key))}
            </button>'''
        for question_key in question_order
    )
    panels = "\n".join(
        f'''<section class="rubric-subtab-panel {'active' if question_key == first else ''}" role="tabpanel" data-rubric-question-panel="{html.escape(question_key)}">
              {render_question_rubric_section_html(question_key, question_tables.get(question_key) or {}, score_scale)}
            </section>'''
        for question_key in question_order
    )
    return f'''
      <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
        {tabs}
      </div>
      <div class="rubric-subtab-panels">
        {panels}
      </div>'''


def render_scoring_rubrics_html(rubric: dict[str, Any]) -> str:
    """Write the GenAI/reference HTML using the same rubric layout as scoring_rubrics.pdf."""
    generated_at = datetime.now().strftime("%d %B %Y at %H:%M")
    instructions = _instruction_sections_html(rubric)
    question_tabs = render_rubric_question_tabs_html(rubric)

    return f'''<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Materials: Retention Scoring Rubrics</title>
  <style>
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0;
      background: #ffffff;
      color: #172026;
      font-family: Arial, Helvetica, sans-serif;
      font-size: 13px;
      line-height: 1.45;
    }}
    #topbar {{
      position: sticky;
      top: 0;
      z-index: 100;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 20px;
      padding: 12px 18px;
      background: rgba(246, 247, 248, .98);
      border-bottom: 1px solid #d9e0e4;
    }}
    #app-title {{ margin: 0; font-size: 22px; }}
    #topbar .small {{ margin: 4px 0 0; color: #5f6c73; font-size: 12px; }}
    #tabs {{ display: flex; flex-wrap: wrap; gap: 6px; justify-content: flex-end; }}
    .tab-button, .rubric-subtab-button {{
      border-radius: 999px;
      padding: 9px 12px;
      font-weight: 800;
      cursor: pointer;
      border: 1px solid #111827;
      background: #ffffff;
      color: #111827;
      font: inherit;
    }}
    .tab-button.active, .rubric-subtab-button.active {{ background: #111827; color: #ffffff; }}
    main {{ padding: 18px; }}
    .tab-panel {{ display: none; }}
    .tab-panel.active {{ display: block; }}
    .rubric-subtabs {{ position: sticky; top: 73px; z-index: 10; background: #ffffff; padding: 0 0 8px; border-bottom: 1px solid #f0f0f0; }}
    .rubric-subtab-panel {{ display: none; }}
    .rubric-subtab-panel.active {{ display: block; }}
    {_appendix_html_css()}
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
      <article class="retention-rubric-appendix">
        <h1>Materials: Retention Scoring Rubrics</h1>
        <hr class="appendix-title-rule">
        {instructions}
      </article>
    </section>

    <section class="tab-panel" data-panel="all-rubrics">
      <article class="retention-rubric-appendix">
        <h1>Materials: Retention Scoring Rubrics</h1>
        <hr class="appendix-title-rule">
        {question_tabs}
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
    if GENAI_PROMPT_RESOURCE_PATH.exists():
        return GENAI_PROMPT_RESOURCE_PATH.read_text(encoding="utf-8")
    return (
        "In the attachment, you should find exactly these TWO files:\n"
        "- retention_scores_genai*.tsv (containing the answers to grade)\n"
        "- scoring_rubrics.html (containing the rubrics, instructions to follow, and information to know)\n\n"
        "Fill in score (0-2), confidence (0-100%), and note (optional). Use q_element, creature, and answer_std only. "
        "Preserve the TSV header and row order exactly. Return only the completed TSV.\n"
    )


def _resource_creature_image_path(image_value: object) -> Path | None:
    image_name = Path(clean(image_value)).name
    if not image_name:
        return None
    candidates = [
        RESOURCES_DIR / "static" / "creatures" / image_name,
        RESOURCES_DIR / "interactive_app" / "static" / "creatures" / image_name,
        Path(clean(image_value)),
    ]
    for candidate in candidates:
        if candidate.exists():
            return candidate
    return None



def _register_thesis_pdf_fonts() -> tuple[str, str, str]:
    """Return regular/bold/italic font names approximating style.sty.

    The manuscript uses Georgia when available, with a Times-like fallback.
    This helper keeps the generated appendix PDFs portable across Windows,
    macOS, Linux, and CI environments.
    """
    from reportlab.pdfbase import pdfmetrics
    from reportlab.pdfbase.ttfonts import TTFont

    candidates = [
        ("Georgia", "C:/Windows/Fonts/georgia.ttf", "C:/Windows/Fonts/georgiab.ttf", "C:/Windows/Fonts/georgiai.ttf"),
        ("Georgia", "/Library/Fonts/Georgia.ttf", "/Library/Fonts/Georgia Bold.ttf", "/Library/Fonts/Georgia Italic.ttf"),
        ("TeXGyreTermes", "/usr/share/fonts/opentype/tex-gyre/texgyretermes-regular.otf", "/usr/share/fonts/opentype/tex-gyre/texgyretermes-bold.otf", "/usr/share/fonts/opentype/tex-gyre/texgyretermes-italic.otf"),
        ("DejaVuSerif", "/usr/share/fonts/truetype/dejavu/DejaVuSerif.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSerif-Italic.ttf"),
    ]
    for family, regular, bold, italic in candidates:
        try:
            regular_path = Path(regular)
            if not regular_path.exists():
                continue
            pdfmetrics.registerFont(TTFont(family, regular))
            bold_name = family
            italic_name = family
            if Path(bold).exists():
                bold_name = f"{family}-Bold"
                pdfmetrics.registerFont(TTFont(bold_name, bold))
            if Path(italic).exists():
                italic_name = f"{family}-Italic"
                pdfmetrics.registerFont(TTFont(italic_name, italic))
            return family, bold_name, italic_name
        except Exception:
            continue
    return "Times-Roman", "Times-Bold", "Times-Italic"


def _pdf_markup_text(
    value: object,
    *,
    preserve_breaks: bool = True,
    collapse: bool = False,
    join_token_fragments: bool = False,
) -> str:
    """Escape rubric text while formatting tokens exactly as the HTML base does."""
    text = _normalise_rubric_token_spacing(
        value,
        collapse=collapse,
        join_token_fragments=join_token_fragments,
    )
    if not text:
        return "—"
    escaped = html.escape(text)
    escaped = re.sub(r"\[(SRC|FAN)\]", r'<font color="#3C78D8"><b>[\1]</b></font>', escaped)
    if preserve_breaks:
        escaped = escaped.replace("\n", "<br/>")
    return escaped or "—"


def _plain_text_from_html_fragment(fragment: object, *, collapse: bool = True) -> str:
    text = clean(fragment)
    if not text:
        return ""
    text = re.sub(r"<\s*br\s*/?\s*>", "\n", text, flags=re.IGNORECASE)
    text = re.sub(r"<\s*/\s*(p|h[1-6]|li)\s*>", "\n", text, flags=re.IGNORECASE)
    text = re.sub(r"<[^>]+>", "", text)
    text = html.unescape(text).replace("\u00a0", " ")
    if collapse:
        text = re.sub(r"\s+", " ", text).strip()
    else:
        text = re.sub(r"[ \t]+", " ", text)
        text = re.sub(r"\n{3,}", "\n\n", text).strip()
    return text


def _instruction_sections_from_html(instructions_html: object) -> list[dict[str, Any]]:
    """Extract h2 sections with paragraphs and bullets from the uploaded rubric HTML."""
    source = clean(instructions_html)
    if not source:
        return []
    h2_matches = list(re.finditer(r"<h2\b[^>]*>(.*?)</h2>", source, flags=re.IGNORECASE | re.DOTALL))
    sections: list[dict[str, Any]] = []
    for index, match in enumerate(h2_matches):
        title = _plain_text_from_html_fragment(match.group(1), collapse=True)
        # Manuscript appendix wording: keep the HTML source content, but remove
        # this purely parenthetical reminder from the printed section heading.
        title = re.sub(r"\s*\(\+ examples\)\s*$", "", title, flags=re.IGNORECASE).strip()
        body_start = match.end()
        body_end = h2_matches[index + 1].start() if index + 1 < len(h2_matches) else len(source)
        body = source[body_start:body_end]
        items: list[dict[str, str]] = []
        for part_match in re.finditer(r"<(p|li)\b[^>]*>(.*?)</\1>", body, flags=re.IGNORECASE | re.DOTALL):
            tag = part_match.group(1).lower()
            # Collapse HTML/newline artefacts from Google Docs spans. This keeps
            # normal paragraph wrapping to ReportLab and avoids mid-sentence line
            # breaks such as before/after inline emphasis.
            text = _plain_text_from_html_fragment(part_match.group(2), collapse=True)
            if not text:
                continue
            items.append({"type": "bullet" if tag == "li" else "paragraph", "text": text})
        sections.append({"title": title, "items": items})
    return sections


def _retention_pdf_styles() -> dict[str, Any]:
    """Shared ReportLab styles for manuscript appendix PDFs."""
    from reportlab.lib import colors
    from reportlab.lib.enums import TA_LEFT
    from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet

    font_regular, font_bold, font_italic = _register_thesis_pdf_fonts()
    base = getSampleStyleSheet()
    return {
        "font_regular": font_regular,
        "font_bold": font_bold,
        "font_italic": font_italic,
        "title_color": colors.HexColor("#28393B"),
        "h1_color": colors.HexColor("#1F2A44"),
        "h2_color": colors.HexColor("#35506B"),
        "h3_color": colors.HexColor("#567087"),
        "table_header": colors.HexColor("#DCEBEC"),
        "table_stripe": colors.HexColor("#F1F8F8"),
        "rule": colors.HexColor("#000000"),
        "score_bg": {"2": colors.HexColor("#93C47D"), "1": colors.HexColor("#F6B26B"), "0": colors.HexColor("#E06666")},
        "content_bg": {"2": colors.HexColor("#D9EAD3"), "1": colors.HexColor("#FCE5CD"), "0": colors.HexColor("#F4CCCC")},
        "inner_label_bg": {"2": colors.HexColor("#B6D7A8"), "1": colors.HexColor("#F9CB9C"), "0": colors.HexColor("#EA9999")},
        "inner_example_bg": colors.HexColor("#F3F3F3"),
        "title": ParagraphStyle("RetentionAppendixTitle", parent=base["Title"], fontName=font_bold, fontSize=20, leading=24, textColor=colors.HexColor("#28393B"), alignment=TA_LEFT, spaceAfter=8),
        "h2": ParagraphStyle("RetentionAppendixH2", parent=base["Heading2"], fontName=font_bold, fontSize=16, leading=19, textColor=colors.HexColor("#35506B"), spaceBefore=0, spaceAfter=5),
        "h3": ParagraphStyle("RetentionAppendixH3", parent=base["Heading3"], fontName=font_bold, fontSize=12, leading=14, textColor=colors.HexColor("#567087"), spaceBefore=0, spaceAfter=4),
        "body": ParagraphStyle("RetentionAppendixBody", parent=base["BodyText"], fontName=font_regular, fontSize=8.7, leading=10.7, alignment=TA_LEFT, spaceAfter=4),
        "bullet": ParagraphStyle("RetentionAppendixBullet", parent=base["BodyText"], fontName=font_regular, fontSize=8.7, leading=10.7, alignment=TA_LEFT, leftIndent=14, firstLineIndent=-8, bulletIndent=4, spaceAfter=2),
        "small": ParagraphStyle("RetentionAppendixSmall", parent=base["BodyText"], fontName=font_regular, fontSize=7.8, leading=9.2, textColor=colors.HexColor("#444444"), spaceAfter=4),
        "table": ParagraphStyle("RetentionAppendixTable", parent=base["BodyText"], fontName=font_regular, fontSize=6.6, leading=7.7, alignment=TA_LEFT, spaceAfter=0),
        "table_label": ParagraphStyle("RetentionAppendixTableLabel", parent=base["BodyText"], fontName=font_regular, fontSize=6.5, leading=7.6, alignment=TA_LEFT, spaceAfter=0),
        "table_head": ParagraphStyle("RetentionAppendixTableHead", parent=base["BodyText"], fontName=font_bold, fontSize=7.4, leading=8.8, alignment=TA_LEFT, spaceAfter=0),
        "score": ParagraphStyle("RetentionAppendixScore", parent=base["BodyText"], fontName=font_bold, fontSize=16, leading=18, textColor=colors.white, alignment=TA_LEFT, spaceAfter=0),
    }


def _appendix_page_number(canvas, doc) -> None:  # type: ignore[no-untyped-def]
    from reportlab.lib.pagesizes import A4
    from reportlab.lib.units import cm

    styles = getattr(doc, "_retention_styles", None) or {}
    font_regular = styles.get("font_regular", "Times-Roman")
    canvas.saveState()
    canvas.setFont(font_regular, 9)
    canvas.drawRightString(A4[0] - doc.rightMargin, A4[1] - 1.25 * cm, str(doc.page))
    canvas.restoreState()


def _horizontal_rule(width: float) -> Any:
    from reportlab.platypus import HRFlowable
    return HRFlowable(width=width, thickness=0.8, color="#000000", spaceBefore=1, spaceAfter=10)


def _safe_paragraph(value: object, style: Any, *, preserve_breaks: bool = True, collapse: bool = False) -> Any:
    from reportlab.platypus import Paragraph
    return Paragraph(_pdf_markup_text(value, preserve_breaks=preserve_breaks, collapse=collapse), style)


def write_creature_info_pdf(rubric: dict[str, Any], path: Path = CREATURE_INFO_PDF_PATH) -> None:
    """Write the creature-information manuscript appendix as a block-based PDF."""
    try:
        from PIL import Image as PILImage
        from reportlab.lib.pagesizes import A4
        from reportlab.lib.units import cm
        from reportlab.platypus import Image, KeepTogether, Paragraph, SimpleDocTemplate, Spacer
    except Exception as exc:  # pragma: no cover - environment fallback
        raise RuntimeError("Creating creature_info.pdf requires reportlab and pillow to be installed.") from exc

    path.parent.mkdir(parents=True, exist_ok=True)
    legacy_tmp = path.parent / "_creature_pdf_images"
    if legacy_tmp.exists():
        shutil.rmtree(legacy_tmp, ignore_errors=True)

    styles = _retention_pdf_styles()
    width, _height = A4
    margin = 2.54 * cm
    content_w = width - 2 * margin
    doc = SimpleDocTemplate(
        str(path), pagesize=A4,
        leftMargin=margin, rightMargin=margin,
        topMargin=margin, bottomMargin=margin,
        title="Materials: Creature Information",
    )
    doc._retention_styles = styles  # type: ignore[attr-defined]
    story: list[Any] = [
        Paragraph("Materials: Creature Information", styles["title"]),
        _horizontal_rule(content_w),
    ]
    image_buffers: list[Any] = []

    creatures = rubric.get("creatures") or {}
    ordered = sorted(creatures.items(), key=lambda item: clean((item[1] or {}).get("name")).lower())
    for creature_id, creature in ordered:
        name = clean(creature.get("name")) or creature_id
        block: list[Any] = [
            Paragraph(name, styles["h2"]),
            _safe_paragraph(f"Creature id: {creature_id}", styles["small"], collapse=True),
        ]
        image_path = _resource_creature_image_path(creature.get("image"))
        if image_path:
            try:
                with PILImage.open(image_path) as source_image:
                    image = source_image.convert("RGB")
                    image.thumbnail((1800, 900), PILImage.LANCZOS)
                    img_w, img_h = image.size
                    buffer = io.BytesIO()
                    image.save(buffer, format="JPEG", quality=88, optimize=True)
                    buffer.seek(0)
                image_buffers.append(buffer)
                max_w, max_h = content_w, 8.5 * cm
                ratio = min(max_w / max(1, img_w), max_h / max(1, img_h))
                block.append(Image(buffer, width=img_w * ratio, height=img_h * ratio, hAlign="CENTER"))
                block.append(Spacer(1, 6))
            except Exception:
                block.append(_safe_paragraph(f"Image unavailable: {image_path.name}", styles["small"], collapse=True))
        else:
            block.append(_safe_paragraph("Image unavailable", styles["small"], collapse=True))

        chapter = clean(creature.get("chapter")) or "—"
        environment = clean(creature.get("environment")) or "—"
        if chapter != "—" and environment != "—":
            place = f"{chapter} ({environment})"
        elif chapter != "—":
            place = chapter
        else:
            place = environment
        appearance = clean(creature.get("appearance")) or "—"
        facts = [clean(fact) for fact in (creature.get("facts") or []) if clean(fact)]
        block.extend([
            Paragraph(f"<b>Place:</b> {_pdf_markup_text(place, preserve_breaks=True)}", styles["body"]),
            Paragraph(f"<b>Appearance:</b> {_pdf_markup_text(appearance, preserve_breaks=True)}", styles["body"]),
        ])
        if facts:
            fact_markup = "<br/>".join(f"• {html.escape(fact)}" for fact in facts)
            block.append(Paragraph(f"<b>Facts:</b><br/>{fact_markup}", styles["body"]))
        else:
            block.append(Paragraph("<b>Facts:</b> —", styles["body"]))
        block.append(Spacer(1, 10))
        story.append(KeepTogether(block))

    doc.build(story, onFirstPage=_appendix_page_number, onLaterPages=_appendix_page_number)


def _content_rows_for_pdf(content: Any) -> list[tuple[str, str]]:
    """Return mini-table rows using the canonical HTML content-row logic."""
    return _content_rows_for_html(content)


def _mini_table_for_rubric_content(content: Any, score: str, styles: dict[str, Any], width: float) -> Any:
    from reportlab.platypus import Paragraph, Table, TableStyle

    rows = _content_rows_for_pdf(content)
    table_data: list[list[Any]] = []
    for label, examples in rows:
        label_para = Paragraph(_pdf_markup_text(label or "—", collapse=True), styles["table_label"])
        examples_para = Paragraph(
            _pdf_markup_text(
                examples or "—",
                preserve_breaks=True,
                join_token_fragments=_rubric_label_has_inline_tokens(label),
            ),
            styles["table"],
        )
        table_data.append([label_para, examples_para])
    inner = Table(table_data, colWidths=[width * 0.66, width * 0.34], hAlign="LEFT", splitByRow=1)
    row_styles: list[tuple[Any, ...]] = [
        ("GRID", (0, 0), (-1, -1), 0.5, styles["rule"]),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]
    for row_index in range(len(table_data)):
        row_styles.append(("BACKGROUND", (0, row_index), (0, row_index), styles["inner_label_bg"].get(score, styles["table_stripe"])))
        row_styles.append(("BACKGROUND", (1, row_index), (1, row_index), styles["inner_example_bg"]))
    inner.setStyle(TableStyle(row_styles))
    return inner


def _rubric_table_for_creature(rows: list[dict[str, Any]], styles: dict[str, Any], content_w: float) -> Any:
    from reportlab.lib.units import cm
    from reportlab.platypus import Paragraph, Table, TableStyle

    score_col_w = 1.45 * cm
    content_col_w = content_w - score_col_w
    table_data: list[list[Any]] = [[Paragraph("Score", styles["table_head"]), Paragraph("Possible answers", styles["table_head"])] ]
    row_styles: list[tuple[Any, ...]] = [
        ("GRID", (0, 0), (-1, -1), 0.6, styles["rule"]),
        ("BACKGROUND", (0, 0), (-1, 0), styles["table_header"]),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 5),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]
    for row in rows:
        score = clean(row.get("score"))
        row_index = len(table_data)
        table_data.append([
            Paragraph(html.escape(score or "—"), styles["score"]),
            _mini_table_for_rubric_content(row.get("content"), score, styles, content_col_w - 10),
        ])
        row_styles.extend([
            ("BACKGROUND", (0, row_index), (0, row_index), styles["score_bg"].get(score, styles["table_stripe"])),
            ("BACKGROUND", (1, row_index), (1, row_index), styles["content_bg"].get(score, styles["table_stripe"])),
        ])
    table = Table(table_data, colWidths=[score_col_w, content_col_w], repeatRows=1, hAlign="LEFT", splitByRow=1)
    table.setStyle(TableStyle(row_styles))
    return table


def expanded_rubric_rows_grouped_for_pdf(table: dict[str, Any], score_scale: list[Any]) -> list[dict[str, Any]]:
    """Group expanded rubric rows per creature for block-based PDF rendering."""
    groups: list[dict[str, Any]] = []
    source_rows_by_key: dict[str, dict[str, Any]] = {}
    for source_row in table.get("rows") or []:
        key = clean(source_row.get("creature_id")) or clean(source_row.get("creature"))
        if key:
            source_rows_by_key[key] = source_row
    for row in expanded_rubric_rows(table, score_scale):
        creature_key = clean(row.get("creature_id")) or clean(row.get("creature"))
        if not groups or groups[-1]["key"] != creature_key:
            source_row = source_rows_by_key.get(creature_key, {})
            groups.append({
                "key": creature_key,
                "creature": clean(row.get("creature")),
                "note": clean(source_row.get("note")) or clean(source_row.get("rubric_note")) or "—",
                "rows": [],
            })
        groups[-1]["rows"].append(row)
    return groups


def write_scoring_rubrics_pdf(rubric: dict[str, Any], path: Path = SCORING_RUBRICS_PDF_PATH) -> None:
    """Write the manuscript scoring-rubric appendix as a styled PDF."""
    try:
        from reportlab.lib.pagesizes import A4
        from reportlab.lib.units import cm
        from reportlab.platypus import KeepTogether, Paragraph, SimpleDocTemplate, Spacer
    except Exception as exc:  # pragma: no cover - environment fallback
        raise RuntimeError("Creating scoring_rubrics.pdf requires reportlab to be installed.") from exc

    path.parent.mkdir(parents=True, exist_ok=True)
    styles = _retention_pdf_styles()
    width, _height = A4
    margin = 2.54 * cm
    content_w = width - 2 * margin
    doc = SimpleDocTemplate(
        str(path), pagesize=A4,
        leftMargin=margin, rightMargin=margin,
        topMargin=margin, bottomMargin=margin,
        title="Materials: Retention Scoring Rubrics",
    )
    doc._retention_styles = styles  # type: ignore[attr-defined]
    story: list[Any] = [
        Paragraph("Materials: Retention Scoring Rubrics", styles["title"]),
        _horizontal_rule(content_w),
    ]

    instruction_sections = _instruction_sections_from_html(rubric.get("instructions_html"))
    if instruction_sections:
        story.append(Paragraph("Instructions", styles["h2"]))
        for section in instruction_sections:
            section_block: list[Any] = [Paragraph(_pdf_markup_text(section.get("title"), collapse=True), styles["h3"])]
            for item in section.get("items", []):
                item_text = _pdf_markup_text(item.get("text"), preserve_breaks=False, collapse=True)
                if item.get("type") == "bullet":
                    section_block.append(Paragraph(item_text, styles["bullet"], bulletText="•"))
                else:
                    section_block.append(Paragraph(item_text, styles["body"]))
            section_block.append(Spacer(1, 3))
            story.append(KeepTogether(section_block))
        story.append(Spacer(1, 10))

    question_tables = rubric.get("question_rubric_tables") or {}
    score_scale = rubric.get("score_scale", [0, 1, 2])
    question_order = [key for key, _label in RETENTION_ELEMENT_SPECS if key in question_tables]

    for q_element in question_order:
        table_source = question_tables.get(q_element) or {}
        q_title = clean(table_source.get("title")) or q_element
        groups = expanded_rubric_rows_grouped_for_pdf(table_source, list(reversed(score_scale)))
        if not groups:
            story.append(KeepTogether([
                Paragraph(q_title, styles["h2"]),
                _safe_paragraph("No creature-specific rubric rows are present in the uploaded base HTML for this question element.", styles["small"], collapse=True),
                Spacer(1, 10),
            ]))
            continue
        first = True
        for group in groups:
            block: list[Any] = []
            if first:
                block.append(Paragraph(q_title, styles["h2"]))
                if clean(table_source.get("intro")):
                    block.append(_safe_paragraph(table_source.get("intro"), styles["body"], preserve_breaks=True))
                first = False
            block.extend([
                Paragraph(clean(group.get("creature")) or "Creature", styles["h3"]),
                Paragraph(f"Note: {_pdf_markup_text(clean(group.get('note')) or '—', preserve_breaks=True)}", styles["small"]),
                _rubric_table_for_creature(group.get("rows") or [], styles, content_w),
                Spacer(1, 10),
            ])
            story.append(KeepTogether(block))

    doc.build(story, onFirstPage=_appendix_page_number, onLaterPages=_appendix_page_number)

def write_prompt_support_files(rubric_path: Path = RUBRIC_JSON_PATH) -> None:
    rubric = load_rubric_json(rubric_path)
    DATA_CONFIG_DIR.mkdir(parents=True, exist_ok=True)
    GENAI_PROMPT_PATH.write_text(genai_prompt_text(), encoding="utf-8")
    SCORING_RUBRICS_HTML_PATH.write_text(render_scoring_rubrics_html(rubric), encoding="utf-8")
    write_scoring_rubrics_pdf(rubric, SCORING_RUBRICS_PDF_PATH)
    write_creature_info_pdf(rubric, CREATURE_INFO_PDF_PATH)


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
    return number is not None and float(number).is_integer() and 0 <= int(number) <= 2


def score_text(value: object) -> str:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return ""
    score = int(number)
    return str(score) if 0 <= score <= 2 else ""


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
        key = (clean(row.get("q_element")), clean(row.get("creature")), clean(row.get("answer_std")))
        if not all(key):
            problems.append(f"{path.name} row {index}: q_element, creature, and answer_std must all be filled.")
            continue
        if key in lookup:
            problems.append(f"{path.name} row {index}: duplicate GenAI score key: {key[0]} / {key[1]} / {key[2]}")
        if not score_is_valid(row.get("score (0-2)")):
            problems.append(f"{path.name} row {index}: score (0-2) must be an integer from 0 to 2.")
        if confidence_value(row.get("confidence (0-100%)")) is None:
            problems.append(f"{path.name} row {index}: confidence (0-100%) must be a number from 0 to 100.")
        lookup[key] = row
    return lookup, problems


def load_genai_score_sources() -> tuple[dict[str, dict[tuple[str, str, str], dict[str, str]]], list[str]]:
    """Load exactly the configured GenAI source files.

    Do not discover a partial set and proceed. AMOUNT_GENAI is the contract.
    Extra files are ignored and reported so old/accidental TSVs cannot affect
    the merged scoring file.
    """
    expected_paths = configured_genai_score_paths()
    labelled_paths = labelled_source_paths(expected_paths, kind="genai")

    discovered_paths = discover_genai_score_paths()
    expected_names = {path.name for path in expected_paths}
    unexpected_names = sorted(path.name for path in discovered_paths if path.name not in expected_names)

    sources: dict[str, dict[tuple[str, str, str], dict[str, str]]] = {}
    problems: list[str] = []

    if unexpected_names:
        problems.append(
            f"Unexpected GenAI score file(s) ignored because AMOUNT_GENAI={AMOUNT_GENAI} expects exactly "
            f"{', '.join(path.name for path in expected_paths)}: {', '.join(unexpected_names)}"
        )

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
    """Load exactly the configured human-grader files.

    Human files are expected only for the frozen review manifest, not for all
    q_element answers. AMOUNT_HUMAN is the contract.
    """
    expected_paths = configured_grader_score_paths()
    labelled_paths = labelled_source_paths(expected_paths, kind="grader")

    discovered_paths = discover_grader_score_paths()
    expected_names = {path.name for path in expected_paths}
    unexpected_names = sorted(path.name for path in discovered_paths if path.name not in expected_names)

    sources: dict[str, dict[str, dict[str, str]]] = {}
    problems: list[str] = []

    if unexpected_names:
        problems.append(
            f"Unexpected human grader file(s) ignored because AMOUNT_HUMAN={AMOUNT_HUMAN} expects exactly "
            f"{', '.join(path.name for path in expected_paths)}: {', '.join(unexpected_names)}"
        )

    for label, path in labelled_paths:
        if not path.exists():
            problems.append(
                f"Missing expected human grader file {path.name}. Run python main.py score_ret grader=1 "
                f"after GenAI scoring to create all AMOUNT_HUMAN={AMOUNT_HUMAN} base files, then complete each grader file."
            )
            sources[label] = {}
            continue

        rows = read_tsv(path)
        if not rows:
            problems.append(f"{path.name} is empty.")
            sources[label] = {}
            continue

        missing_columns = [column for column in GRADER_SCORE_FIELDNAMES if column not in rows[0]]
        if missing_columns:
            problems.append(f"{path.name} is missing column(s): {', '.join(missing_columns)}")

        for index, row in enumerate(rows, start=2):
            status = clean(row.get("status"))
            if status == "graded" and not score_is_valid(row.get("score (0-2)")):
                problems.append(f"{path.name} row {index}: score (0-2) must be an integer from 0 to 2 when status is graded.")
            if status and status not in {"graded", "skipped", "flagged"}:
                problems.append(f"{path.name} row {index}: status must be graded, skipped, or flagged.")

        sources[label] = load_grader_scores(path)

    return sources, problems


def task_id_for_unique(question: str, creature: str, answer_std: str) -> str:
    return stable_id(question, creature, answer_std)


def unique_task_lookup(prompt_rows: list[dict[str, str]]) -> dict[str, dict[str, Any]]:
    genai_rows = build_unique_genai_rows(prompt_rows)
    occurrence_counts = Counter(
        (row["q_element"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    raw_examples: dict[tuple[str, str, str], list[str]] = defaultdict(list)
    creature_id_by_key: dict[tuple[str, str, str], str] = {}
    question_key_by_question: dict[str, str] = {}
    for row in prompt_rows:
        if not clean(row.get("answer_std")):
            continue
        key = (row["q_element"], row["creature"], row["answer_std"])
        if len(raw_examples[key]) < 5 and clean(row.get("answer")) not in raw_examples[key]:
            raw_examples[key].append(clean(row.get("answer")))
        creature_id_by_key[key] = row["creature_id"]
        question_key_by_question[row["q_element"]] = row["question_key"]

    tasks: dict[str, dict[str, Any]] = {}
    for row in genai_rows:
        key = (row["q_element"], row["creature"], row["answer_std"])
        task_id = task_id_for_unique(*key)
        question_key = question_key_by_question.get(row["q_element"], QUESTION_KEY_BY_QUESTION.get(row["q_element"], ""))
        creature_id = creature_id_by_key.get(key, "")
        tasks[task_id] = {
            "task_id": task_id,
            "q_element": row["q_element"],
            "question_key": question_key,
            "question_label": QUESTION_LABEL_BY_QUESTION.get(row["q_element"], question_key),
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
        task["genai_score"] = score_text(genai_row.get("score (0-2)"))
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
        groups[(clean(task.get("q_element")), clean(task.get("confidence_bucket")))].append(task)

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
        QUESTION_SORT_INDEX.get(clean(task.get("q_element")), 999),
        clean(task.get("creature")).lower(),
        clean(task.get("answer_std")),
    ))
    return selected



def review_manifest_row(task: dict[str, Any]) -> dict[str, Any]:
    return {field: clean(task.get(field)) for field in REVIEW_TASK_FIELDNAMES}


def read_review_manifest(path: Path = REVIEW_TASKS_PATH) -> list[dict[str, Any]]:
    rows = read_tsv(path)
    if not rows:
        return []
    if any(field not in rows[0] for field in REVIEW_TASK_FIELDNAMES):
        return []
    return [dict(row) for row in rows if clean(row.get("task_id"))]


def write_review_manifest(tasks: list[dict[str, Any]], path: Path = REVIEW_TASKS_PATH) -> None:
    rows = [review_manifest_row(task) for task in tasks]
    write_tsv(path, REVIEW_TASK_FIELDNAMES, rows)


def review_task_id_set(tasks: list[dict[str, Any]]) -> set[str]:
    return {clean(task.get("task_id")) for task in tasks if clean(task.get("task_id"))}


def load_or_build_review_manifest(
    prompt_rows: list[dict[str, str]],
    genai_lookup: dict[tuple[str, str, str], dict[str, str]],
    *,
    force: bool = False,
) -> list[dict[str, Any]]:
    """Return the frozen human review queue, creating it when needed.

    Once retention_review_tasks.tsv exists, the scoring app reuses it so both
    human coders receive exactly the same task IDs even on different devices.
    Delete the manifest deliberately if the GenAI files or source data must be
    regenerated.
    """
    current_tasks = build_review_tasks(prompt_rows, genai_lookup)
    if not force and REVIEW_TASKS_PATH.exists():
        existing = read_review_manifest(REVIEW_TASKS_PATH)
        if existing:
            return existing
    write_review_manifest(current_tasks, REVIEW_TASKS_PATH)
    return current_tasks


def grader_base_rows_from_tasks(tasks: list[dict[str, Any]], existing_rows: dict[str, dict[str, str]] | None = None) -> dict[str, dict[str, Any]]:
    existing_rows = existing_rows or {}
    rows: dict[str, dict[str, Any]] = {}
    for task in tasks:
        task_id = clean(task.get("task_id"))
        if not task_id:
            continue
        previous = existing_rows.get(task_id, {})
        rows[task_id] = {
            "task_id": task_id,
            "q_element": clean(task.get("q_element")),
            "question_key": clean(task.get("question_key")),
            "creature": clean(task.get("creature")),
            "creature_id": clean(task.get("creature_id")),
            "answer_std": clean(task.get("answer_std")),
            "score (0-2)": clean(previous.get("score (0-2)")),
            "status": clean(previous.get("status")),
            "note (optional)": clean(previous.get("note (optional)")),
            "updated_at": clean(previous.get("updated_at")),
        }
    return rows


def ensure_human_score_files(tasks: list[dict[str, Any]]) -> None:
    """Create all configured human base files from the same manifest.

    Existing human files are preserved row-for-row where task IDs still exist,
    so rerunning score_ret does not wipe completed grading.
    """
    expected_ids = review_task_id_set(tasks)
    for path in configured_grader_score_paths():
        existing = load_grader_scores(path) if path.exists() else {}
        rows = grader_base_rows_from_tasks(tasks, existing)
        if set(existing) != expected_ids or not path.exists():
            write_grader_scores(path, rows)


def prepare_human_review_files(
    prompt_rows: list[dict[str, str]],
    genai_lookup: dict[tuple[str, str, str], dict[str, str]],
) -> list[dict[str, Any]]:
    tasks = load_or_build_review_manifest(prompt_rows, genai_lookup)
    ensure_human_score_files(tasks)
    return tasks

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
        QUESTION_SORT_INDEX.get(clean(row.get("q_element")), 999),
        clean(row.get("creature")).lower(),
        clean(row.get("answer_std")),
    ))
    write_tsv(path, GRADER_SCORE_FIELDNAMES, rows)


def validate_genai_completeness(prompt_rows: list[dict[str, str]], genai_lookup: dict[tuple[str, str, str], dict[str, str]]) -> list[str]:
    expected = {
        (row["q_element"], row["creature"], row["answer_std"])
        for row in build_unique_genai_rows(prompt_rows)
    }
    missing = sorted(expected - set(genai_lookup), key=lambda key: (QUESTION_SORT_INDEX.get(key[0], 999), key[1].lower(), key[2]))
    if not missing:
        return []
    preview = [f"{q} / {creature} / {answer[:80]}" for q, creature, answer in missing[:10]]
    suffix = f"; plus {len(missing) - 10} more" if len(missing) > 10 else ""
    return ["Missing GenAI score rows for: " + " | ".join(preview) + suffix]


def preview_score_keys(keys: set[tuple[str, str, str]] | list[tuple[str, str, str]], *, limit: int = 10) -> str:
    ordered = sorted(
        keys,
        key=lambda key: (QUESTION_SORT_INDEX.get(key[0], 999), key[1].lower(), key[2]),
    )
    preview = [f"{q} / {creature} / {answer[:80]}" for q, creature, answer in ordered[:limit]]
    suffix = f"; plus {len(ordered) - limit} more" if len(ordered) > limit else ""
    return " | ".join(preview) + suffix


def preview_task_ids(task_ids: set[str] | list[str], *, limit: int = 10) -> str:
    ordered = sorted(clean(task_id) for task_id in task_ids if clean(task_id))
    preview = ordered[:limit]
    suffix = f"; plus {len(ordered) - limit} more" if len(ordered) > limit else ""
    return ", ".join(preview) + suffix


def retention_source_readiness_blockers(survey_rows: list[dict[str, str]]) -> list[str]:
    """Return blockers that should prevent initial creation of retention_scores_merged.tsv.

    This checks source-file readiness only. Score disagreements are not blockers
    here, because disagreements are exactly what the merged adjudication file is
    for. The blockers are missing files, malformed files, incomplete GenAI scores,
    missing frozen review manifest, or incomplete human-review scores.
    """
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    blockers: list[str] = []

    expected_genai_keys = {
        (row["q_element"], row["creature"], row["answer_std"])
        for row in build_unique_genai_rows(prompt_rows)
        if clean(row.get("answer_std"))
    }

    genai_sources, genai_problems = load_genai_score_sources()
    blockers.extend(genai_problems)

    for label, lookup in genai_sources.items():
        actual_keys = set(lookup)
        missing_keys = expected_genai_keys - actual_keys
        extra_keys = actual_keys - expected_genai_keys

        if missing_keys:
            blockers.append(
                f"{label} is incomplete: missing GenAI score row(s): {preview_score_keys(missing_keys)}"
            )
        if extra_keys:
            blockers.append(
                f"{label} contains unexpected GenAI score row(s), probably from stale data: {preview_score_keys(extra_keys)}"
            )

    genai_lookup = merge_genai_sources_for_lookup(genai_sources)

    review_tasks = read_review_manifest()
    if not review_tasks:
        blockers.append(
            "retention_review_tasks.tsv is missing; run python main.py score_ret grader=1 after GenAI scoring "
            "to freeze the human review queue."
        )
        return blockers

    current_review_ids = {task["task_id"] for task in build_review_tasks(prompt_rows, genai_lookup)}
    manifest_ids = review_task_id_set(review_tasks)

    missing_from_current = manifest_ids - current_review_ids
    if missing_from_current:
        blockers.append(
            "Frozen human review manifest contains task_id(s) no longer present in the current data/GenAI rows: "
            + preview_task_ids(missing_from_current)
        )

    grader_sources, grader_problems = load_grader_score_sources()
    blockers.extend(grader_problems)

    for label, source in grader_sources.items():
        actual_ids = set(source)
        missing_ids = manifest_ids - actual_ids
        extra_ids = actual_ids - manifest_ids

        if missing_ids:
            blockers.append(
                f"{label} is incomplete: missing human review task_id(s): {preview_task_ids(missing_ids)}"
            )
        if extra_ids:
            blockers.append(
                f"{label} contains unexpected human review task_id(s), probably from a stale manifest: {preview_task_ids(extra_ids)}"
            )

        incomplete_ids: list[str] = []
        for task_id in sorted(manifest_ids):
            source_row = source.get(task_id, {})
            if clean(source_row.get("status")) != "graded" or not score_is_valid(source_row.get("score (0-2)")):
                incomplete_ids.append(task_id)

        if incomplete_ids:
            blockers.append(
                f"{label} is not fully graded: every frozen human-review task must have status='graded' "
                f"and a valid score 0-2. Incomplete task_id(s): {preview_task_ids(incomplete_ids)}"
            )

    return list(dict.fromkeys(blockers))


def append_problem(problems: list[str], message: str, *, limit: int = 75) -> None:
    if len(problems) < limit:
        problems.append(message)


def score_source_values(source_rows: dict[str, dict[str, str]], *, score_field: str = "score (0-2)") -> dict[str, str]:
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
        if grader_scores:
            return "four-way agreement"
        return "GenAI agreement"

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
        if grader_scores:
            return agreed_score, "four_way_agreement", "four-way agreement", FINAL_NOTE_MANUAL_NOT_NEEDED
        return agreed_score, "genai_agreement", "GenAI agreement", FINAL_NOTE_MANUAL_NOT_NEEDED

    return FINAL_SCORE_PLACEHOLDER, "needs_adjudication", auto_final_note_for_scores(genai_scores, grader_scores), ""


def merged_row_exact_key(row: dict[str, Any]) -> tuple[str, str, str, str, str, str]:
    return (
        clean(row.get("MCID")),
        clean(row.get("moment")),
        clean(row.get("creature_id")),
        clean(row.get("question_key")),
        clean(row.get("q_element")),
        clean(row.get("answer_std")),
    )


def merged_row_identity_key(row: dict[str, Any]) -> tuple[str, str, str, str, str]:
    return (
        clean(row.get("MCID")),
        clean(row.get("moment")),
        clean(row.get("creature_id")),
        clean(row.get("question_key")),
        clean(row.get("q_element")),
    )


MANUAL_FINAL_STATUS_VALUES = {
    "manual",
    "manual_adjudicated",
    "adjudicated",
    "resolved",
    "researcher_resolved",
}

AUTO_RESOLVED_FINAL_NOTES = {
    "blank answer",
    "GenAI agreement",
    "genai agreement",
    "four-way agreement",
    "full agreement",
}


def load_existing_merged_rows(path: Path = RETENTION_MERGED_PATH) -> tuple[list[dict[str, str]], list[str], bool]:
    if not path.exists():
        return [], [], False
    rows = read_tsv(path)
    header = list(rows[0]) if rows else []
    final_columns = [field for field in header if field.startswith("final_")]
    preserve_final_values = "final_note_manual" in header
    return rows, final_columns, preserve_final_values


def existing_row_has_manual_final_value(row: dict[str, str]) -> bool:
    """Return True only for likely researcher adjudication, not auto-generated finals."""
    final_score = score_text(row.get("final_score"))
    if not final_score:
        return False

    manual_note = clean(row.get("final_note_manual"))
    if manual_note and manual_note != FINAL_NOTE_MANUAL_NOT_NEEDED:
        return True

    status = clean(row.get("final_status")).lower()
    if status in MANUAL_FINAL_STATUS_VALUES:
        return True

    # Safety net: if a previously unresolved row now has a numeric final_score
    # but no manual note yet, treat it as manual work rather than deleting it
    # during an explicit forced rebuild.
    auto_note = clean(row.get("final_note_auto"))
    if auto_note and auto_note not in AUTO_RESOLVED_FINAL_NOTES:
        return True

    return False


def apply_existing_final_values(
    rows: list[dict[str, Any]],
    existing_rows: list[dict[str, str]],
    final_columns: list[str],
    *,
    preserve_final_values: bool,
) -> None:
    """Preserve manual adjudication only.

    This function is used only for an explicit forced rebuild. Normal sum_merged
    runs do not rewrite retention_scores_merged.tsv at all.
    """
    if not preserve_final_values or not existing_rows or not final_columns:
        return

    existing_by_key = {merged_row_exact_key(row): row for row in existing_rows}

    for row in rows:
        existing = existing_by_key.get(merged_row_exact_key(row))
        if not existing or not existing_row_has_manual_final_value(existing):
            continue

        for column in final_columns:
            if column == "final_note_auto":
                continue

            value = clean(existing.get(column))
            if not value:
                continue

            if column == "final_status":
                status = value.lower()
                row[column] = value if status in MANUAL_FINAL_STATUS_VALUES else "manual_adjudicated"
            else:
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

    existing_by_identity: dict[tuple[str, str, str, str, str], dict[str, str]] = {}
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
                f"q_element={clean(row.get('q_element')) or 'NA'}",
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

    review_tasks = read_review_manifest()
    if review_tasks:
        current_review_ids = {task["task_id"] for task in build_review_tasks(prompt_rows, genai_lookup)}
        manifest_ids = review_task_id_set(review_tasks)
        if current_review_ids and not manifest_ids.issubset(current_review_ids):
            missing_preview = ", ".join(sorted(manifest_ids - current_review_ids)[:10])
            append_problem(problems, f"Frozen human review manifest contains task_id(s) no longer present in the current data/GenAI rows: {missing_preview}")
    else:
        review_tasks = build_review_tasks(prompt_rows, genai_lookup)
        if require_complete_review:
            append_problem(problems, "retention_review_tasks.tsv is missing; run python main.py score_ret grader=1 after GenAI scoring to freeze the human review queue.")
    required_review_ids = {task["task_id"] for task in review_tasks}
    occurrence_counts = Counter(
        (row["q_element"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    grader_sources, grader_problems = load_grader_score_sources()
    problems.extend(grader_problems)
    if required_review_ids and not grader_sources:
        problems.append(
            "No retention_scores_grader*.tsv files found. Run python main.py score_ret grader=1 "
            "to create the frozen human-validation manifest and all configured human base files."
        )

    genai_labels = sorted(genai_sources, key=natural_source_key)
    grader_labels = sorted(grader_sources, key=natural_source_key)

    rows: list[dict[str, Any]] = []
    for row in prompt_rows:
        key = (row["q_element"], row["creature"], row["answer_std"])
        task_id = "" if not row["answer_std"] else task_id_for_unique(*key)

        genai_rows_for_key = {
            label: source_lookup.get(key, {})
            for label, source_lookup in genai_sources.items()
            if row["answer_std"]
        }
        genai_scores = score_source_values(genai_rows_for_key)
        genai_agreement_score = consensus_score(genai_scores)
        genai_missing_labels = [
            label
            for label in genai_labels
            if row["answer_std"] and not score_text(genai_rows_for_key.get(label, {}).get("score (0-2)"))
        ]

        grader_rows_for_task = {
            label: source_lookup.get(task_id, {})
            for label, source_lookup in grader_sources.items()
            if task_id
        }
        graded_human_scores = {
            label: score_text(source_row.get("score (0-2)"))
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
                or not score_text(grader_rows_for_task.get(label, {}).get("score (0-2)"))
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
                    append_problem(problems, f"Human review incomplete for {row['q_element']} / {row['creature']} / {row['answer_std'][:80]} (missing: {missing_text})")
            elif not human_agreement_score:
                old_status = "needs_adjudication"
                if require_complete_review:
                    score_texts = ", ".join(f"{label}={score or 'missing'}" for label, score in graded_human_scores.items())
                    append_problem(problems, f"Human disagreement unresolved for {row['q_element']} / {row['creature']} / {row['answer_std'][:80]}: {score_texts}")
            else:
                old_status = "human_agreement"
        else:
            if not genai_labels or genai_missing_labels:
                old_status = "needs_genai_scores"
                if require_complete_review:
                    missing_text = ", ".join(genai_missing_labels) if genai_missing_labels else "all GenAI files"
                    append_problem(problems, f"GenAI scoring incomplete for {row['q_element']} / {row['creature']} / {row['answer_std'][:80]} (missing: {missing_text})")
            elif not genai_agreement_score:
                old_status = "needs_genai_adjudication"
                if require_complete_review:
                    score_texts = ", ".join(f"{label}={score or 'missing'}" for label, score in genai_scores.items())
                    append_problem(problems, f"GenAI source disagreement for {row['q_element']} / {row['creature']} / {row['answer_std'][:80]}: {score_texts}")
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
                f"Retention score conflict for {row['q_element']} / {row['creature']} / {row['answer_std'][:80]}: {final_note_auto}",
            )

        merged_row: dict[str, Any] = {
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "q_element": row["q_element"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
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
            merged_row[f"{label}_score"] = score_text(source_row.get("score (0-2)"))
            merged_row[f"{label}_confidence"] = clean(source_row.get("confidence (0-100%)"))
            merged_row[f"{label}_note"] = clean(source_row.get("note (optional)"))

        for label in grader_labels:
            source_row = grader_rows_for_task.get(label, {})
            merged_row[f"{label}_score"] = score_text(source_row.get("score (0-2)")) if clean(source_row.get("status")) == "graded" else ""
            merged_row[f"{label}_status"] = clean(source_row.get("status"))
            merged_row[f"{label}_note"] = clean(source_row.get("note (optional)"))

        rows.append(merged_row)

    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("moment")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("q_element")), 999),
    ))
    return rows, problems


def write_prompt_score_file(
    survey_rows: list[dict[str, str]],
    *,
    require_complete_review: bool = False,
    rebuild: bool = False,
) -> tuple[list[dict[str, Any]], list[str]]:
    """Create retention_scores_merged.tsv only when safe.

    Default behaviour is non-destructive:
    - if retention_scores_merged.tsv already exists, read and return it;
    - do not rewrite source columns;
    - do not recompute final_score;
    - do not touch manual notes.

    Initial creation is blocked until the configured GenAI files and configured
    human-review files are complete. Disagreements are not blockers: they become
    manual-adjudication rows in the merged file.
    """
    existing_rows, existing_final_columns, preserve_final_values = load_existing_merged_rows(RETENTION_MERGED_PATH)

    if existing_rows and not rebuild:
        print(
            "[retention_scores_merged] Existing merged file found; using it as the manual adjudication workspace. "
            "sum_merged will not rewrite it. Use an explicit rebuild=True debug call only when you deliberately "
            "want to regenerate it.",
            flush=True,
        )
        return existing_rows, []

    blockers = retention_source_readiness_blockers(survey_rows) if require_complete_review else []
    rows, problems = build_prompt_score_rows(survey_rows, require_complete_review=require_complete_review)

    if require_complete_review and blockers:
        print(
            "[retention_scores_merged] Not creating retention_scores_merged.tsv because the configured source TSVs "
            "are not complete yet.",
            flush=True,
        )
        return rows, list(dict.fromkeys(blockers + problems))

    if rows:
        if existing_rows and rebuild:
            # Protect the current adjudication file before any deliberate forced rewrite.
            backup_retention_tsv(RETENTION_MERGED_PATH)
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


def build_retention_scoring_checks(
    survey_rows: list[dict[str, str]],
    scoring_rows: list[dict[str, Any]] | None = None,
    problems: list[str] | None = None,
) -> list[dict[str, Any]]:
    """Return ordered retention-scoring workflow steps for the HTML report.

    The rows are deliberately phrased as a step-by-step checklist. Later steps
    are marked as waiting when an earlier prerequisite still needs action, so a
    user can fix the first blocking step instead of chasing downstream errors.
    """
    scoring_rows = scoring_rows or []
    problems = problems or []
    prompt_rows = build_prompt_rows_from_survey(survey_rows) if survey_rows else []

    def symbol(status: str) -> str:
        return {
            "pass": "✅",
            "action": "⭕",
            "wait": "⏸",
            "fail": "❌",
        }.get(status, status)

    def step(number: int, check: str, state: str, detail: str, action: str) -> dict[str, Any]:
        status_symbol = symbol(state)
        return {
            "step": f"Step {number}/5",
            "check": check,
            "status": status_symbol,
            "detail": detail or "—",
            # Passing steps are deliberately quiet: the next action is only shown
            # for the first blocking or cautionary step.
            "action": "—" if status_symbol == "✅" else (action or "—"),
        }

    def rows_with_invalid_genai(path: Path) -> tuple[int, list[str], list[str], list[str], list[str]]:
        rows = read_tsv(path)
        missing_score: list[str] = []
        missing_confidence: list[str] = []
        bad_score: list[str] = []
        bad_confidence: list[str] = []
        for idx, row in enumerate(rows, start=2):
            score_raw = clean(row.get("score (0-2)"))
            conf_raw = clean(row.get("confidence (0-100%)"))
            if not score_raw:
                missing_score.append(str(idx))
            elif not score_is_valid(score_raw):
                bad_score.append(str(idx))
            if not conf_raw:
                missing_confidence.append(str(idx))
            elif confidence_value(conf_raw) is None:
                bad_confidence.append(str(idx))
        return len(rows), missing_score, missing_confidence, bad_score, bad_confidence

    def rows_with_invalid_human(path: Path) -> tuple[list[str], list[str], list[str]]:
        rows = read_tsv(path)
        missing_score: list[str] = []
        bad_score: list[str] = []
        not_graded: list[str] = []
        for idx, row in enumerate(rows, start=2):
            status = clean(row.get("status"))
            score_raw = clean(row.get("score (0-2)"))
            if status != "graded":
                not_graded.append(str(idx))
            if not score_raw:
                missing_score.append(str(idx))
            elif not score_is_valid(score_raw):
                bad_score.append(str(idx))
        return missing_score, bad_score, not_graded

    def preview(items: list[str], limit: int = 12) -> str:
        if not items:
            return ""
        suffix = f" (+{len(items) - limit} more)" if len(items) > limit else ""
        return ", ".join(items[:limit]) + suffix

    rows: list[dict[str, Any]] = []

    expected_genai = configured_genai_score_paths()
    present_genai = [path for path in expected_genai if path.exists()]
    missing_genai = [path.name for path in expected_genai if not path.exists()]
    genai_files_present = len(present_genai) == len(expected_genai)
    rows.append(step(
        1,
        f"[{AMOUNT_GENAI}] GenAI base files present",
        "pass" if genai_files_present else "action",
        f"Found: {', '.join(path.name for path in present_genai) or 'none'}." + (f" Missing: {', '.join(missing_genai)}." if missing_genai else ""),
        "Run `python main.py sum_merged` with `PUBLIC_ROUTE=False`. Then use `./data/config/genai_prompt.txt`, `./data/config/scoring_rubrics.html`, and the generated `./data/retention_scores_genai*.tsv` files.",
    ))

    genai_details: list[str] = []
    genai_missing_details: list[str] = []
    genai_invalid_details: list[str] = []
    genai_too_much_missing = False
    genai_has_any_missing = False
    genai_has_invalid_values = False
    genai_has_structural_problem = False
    genai_filled = genai_files_present
    if genai_files_present:
        expected_cols = set(GENAI_SCORE_FIELDNAMES)
        for path in expected_genai:
            file_rows = read_tsv(path)
            missing_cols = sorted(expected_cols - set(file_rows[0].keys() if file_rows else []))
            if missing_cols:
                genai_filled = False
                genai_has_structural_problem = True
                genai_details.append(f"{path.name}: missing column(s) {', '.join(missing_cols)}")
                continue
            n_rows, missing_score, missing_confidence, bad_score, bad_confidence = rows_with_invalid_genai(path)
            denom = max(1, n_rows)
            file_missing_parts: list[str] = []
            if missing_score:
                genai_has_any_missing = True
                score_pct = 100 * len(missing_score) / denom
                if len(missing_score) / denom > 0.10:
                    genai_too_much_missing = True
                file_missing_parts.append(f"score (0-2): rows {preview(missing_score)} ({score_pct:.1f}%)")
            if missing_confidence:
                genai_has_any_missing = True
                conf_pct = 100 * len(missing_confidence) / denom
                if len(missing_confidence) / denom > 0.10:
                    genai_too_much_missing = True
                file_missing_parts.append(f"confidence (0-100%): rows {preview(missing_confidence)} ({conf_pct:.1f}%)")
            if file_missing_parts:
                genai_filled = False
                genai_missing_details.append(f"{path.name}\n- " + "\n- ".join(file_missing_parts))
            file_invalid_parts: list[str] = []
            if bad_score:
                genai_has_invalid_values = True
                file_invalid_parts.append(f"score outside 0-2 rows {preview(bad_score)}")
            if bad_confidence:
                genai_has_invalid_values = True
                file_invalid_parts.append(f"confidence outside 0-100 rows {preview(bad_confidence)}")
            if file_invalid_parts:
                genai_filled = False
                genai_invalid_details.append(f"{path.name}: " + "; ".join(file_invalid_parts))

    if genai_filled:
        genai_state = "pass"
        genai_detail = f"Found: {', '.join(path.name for path in expected_genai)}."
        genai_action = "—"
    elif not genai_files_present:
        genai_state = "wait"
        genai_detail = "Fix step 1 first."
        genai_action = "Fix step 1 first."
    elif genai_has_structural_problem:
        genai_state = "fail"
        genai_detail = "Not correctly filled in; the file structure is not valid. " + " | ".join(genai_details[:5])
        genai_action = "Regenerate the GenAI base files with `python main.py sum_merged` before rerunning the full prompt with the external GenAI tools."
    elif genai_has_invalid_values:
        genai_state = "fail"
        genai_detail = "Not correctly filled in; at least one score or confidence value is outside the allowed range. " + " | ".join(genai_invalid_details[:5])
        genai_action = "(Re)run the full prompt with the external GenAI tool for the affected file(s), using `./data/config/genai_prompt.txt`, `./data/config/scoring_rubrics.html`, and the relevant `./data/retention_scores_genai*.tsv`. Researchers can document and make a different decision, but the default pipeline treats these files as not usable for merging."
    elif genai_too_much_missing:
        genai_state = "fail"
        genai_detail = "Not correctly filled in; missing too much data. " + "\n".join(genai_missing_details[:5])
        genai_action = "(Re)run the full prompt with the external GenAI tool for the affected file(s), using `./data/config/genai_prompt.txt`, `./data/config/scoring_rubrics.html`, and the relevant `./data/retention_scores_genai*.tsv`. Researchers can document and make a different decision, but the default pipeline does not continue from a GenAI file with more than 10% missing required scoring data."
    elif genai_has_any_missing:
        genai_state = "action"
        genai_detail = "Passed, but missing the following data:\n" + "\n".join(genai_missing_details[:5])
        genai_action = "Preferably rerun the full prompt with the external GenAI tool for the affected file(s), using `./data/config/genai_prompt.txt`, `./data/config/scoring_rubrics.html`, and the relevant `./data/retention_scores_genai*.tsv`. Researchers can document and make a different decision, but the default checklist keeps this step open until the missing scores/confidence values are resolved."
    else:
        genai_state = "fail"
        genai_detail = "Not correctly filled in; the GenAI files could not be validated."
        genai_action = "Rerun the full prompt with the external GenAI tool, then rerun `python main.py sum_merged`."

    rows.append(step(
        2,
        f"[{AMOUNT_GENAI}] GenAI files correctly filled in",
        genai_state,
        genai_detail,
        genai_action,
    ))

    expected_human = configured_grader_score_paths()
    present_human = [path for path in expected_human if path.exists()]
    missing_human = [path.name for path in expected_human if not path.exists()]
    manifest_present = REVIEW_TASKS_PATH.exists()
    human_files_present = manifest_present and len(present_human) == len(expected_human)
    rows.append(step(
        3,
        f"[{AMOUNT_HUMAN}] Human base files present",
        "pass" if human_files_present else ("wait" if not genai_filled else "action"),
        f"Manifest: {'present' if manifest_present else 'missing'}. Found human files: {', '.join(path.name for path in present_human) or 'none'}." + (f" Missing: {', '.join(missing_human)}." if missing_human else ""),
        "—" if human_files_present else ("Fix step 2 first." if not genai_filled else "Run `python main.py score_ret prepare` once. This creates `./data/retention_review_tasks.tsv` and all configured `./data/retention_scores_grader{n}.tsv` base files from the frozen review manifest."),
    ))

    human_details: list[str] = []
    human_filled = human_files_present
    if human_files_present:
        manifest = read_review_manifest()
        manifest_ids = review_task_id_set(manifest)
        for path in expected_human:
            source = load_grader_scores(path)
            ids = set(source)
            missing_ids = sorted(manifest_ids - ids)
            extra_ids = sorted(ids - manifest_ids)
            if missing_ids or extra_ids:
                human_filled = False
                human_details.append(f"{path.name}: task_id mismatch (missing {len(missing_ids)}, extra {len(extra_ids)})")
                continue
            missing_score, bad_score, not_graded = rows_with_invalid_human(path)
            if missing_score or bad_score or not_graded:
                human_filled = False
                parts = []
                if not_graded:
                    parts.append(f"not graded rows {preview(not_graded)}")
                if missing_score:
                    parts.append(f"missing score rows {preview(missing_score)}")
                if bad_score:
                    parts.append(f"score outside 0-2 rows {preview(bad_score)}")
                human_details.append(f"{path.name}: " + "; ".join(parts))
    rows.append(step(
        4,
        f"[{AMOUNT_HUMAN}] Human files correctly filled in",
        "pass" if human_filled else ("wait" if not human_files_present else "action"),
        "All human review rows are graded with integer scores in [0, 2] and match the frozen manifest." if human_filled else ("Fix step 3 first." if not human_files_present else " | ".join(human_details[:5])),
        "—" if human_filled else ("Fix step 3 first." if not human_files_present else "Run `python main.py score_ret grader=1` and `python main.py score_ret grader=2` to complete the generated files. If grader 2 works on another device, copy the whole `./data/` folder first so both graders score the exact same manifest."),
    ))

    final_scores = [clean(row.get("final_score")) for row in scoring_rows if clean(row.get("answer_std"))]
    invalid_final = [str(index + 2) for index, value in enumerate(final_scores) if not score_text(value)]
    allowed_q_elements = set(Q_ELEMENT_ORDER)
    unexpected_q_elements = sorted({clean(row.get("q_element")) for row in scoring_rows if clean(row.get("q_element")) and clean(row.get("q_element")) not in allowed_q_elements})
    final_ready = bool(scoring_rows) and not invalid_final and not unexpected_q_elements and not problems
    final_detail_parts: list[str] = []
    if not scoring_rows:
        final_detail_parts.append("No merged scoring rows were available yet.")
    if invalid_final:
        final_detail_parts.append(f"Unresolved/non-numeric final_score rows {preview(invalid_final)} in `retention_scores_merged.tsv`.")
    if unexpected_q_elements:
        final_detail_parts.append(f"Unexpected q_element value(s): {', '.join(unexpected_q_elements)}.")
    if problems:
        final_detail_parts.append("Merge problem(s): " + " | ".join(problems[:5]))
    rows.append(step(
        5,
        "Merged final scores and statistics ready",
        "pass" if final_ready else ("wait" if not human_filled else "action"),
        "Final scores are complete, valid, and ready for retention statistics." if final_ready else ("Fix step 4 first." if not human_filled else " ".join(final_detail_parts)),
        "—" if final_ready else ("Fix step 4 first." if not human_filled else "Open `./data/retention_scores_merged.tsv` and manually resolve rows with disagreement or `[resolve conflict]`; fill `final_score` plus a manual note where needed, then rerun `python main.py sum_merged` with `PUBLIC_ROUTE=True`."),
    ))
    return rows
