from __future__ import annotations

import csv
import re
from collections import defaultdict
from itertools import combinations
from pathlib import Path
from typing import Any

from ._shared import (
    CREATURE_NAME_BY_ID,
    IGNORED_SEEN_EXTRAS,
    MAX_RETENTION_SLOTS,
    RETENTION_QUESTION_SPECS,
    RETENTION_ELEMENT_LABEL_BY_KEY,
    RETENTION_ELEMENT_SPECS,
    RETENTION_PROMPT_TO_ELEMENTS,
    RETENTION_COMPONENT_SPECS,
    RETENTION_SCORES_PATH,
    canonical_condition,
    clean,
    delayed_flag,
    delayed_included_flag,
    first_present,
    mcid_from_row,
    mean_sd_text,
    parse_numeric,
    summarise,
)
from ._survey_io import detect_text_encoding

RETENTION_SCORE_COLUMNS = {
    "ret_immediate_score": "ret_immediate_score",
    "ret_delayed_score": "ret_delayed_score",
}


SURVEY_CONDITION_COLUMNS = ("condition", "Condition", "CONDITION", "experiment_condition", "condition_raw")


def condition_from_retention_survey_row(row: dict[str, str] | None) -> str:
    """Return the retention condition carried by survey_export.tsv."""
    if row is None:
        return "Missing / invalid"

    for column in SURVEY_CONDITION_COLUMNS:
        raw_condition = clean(row.get(column))
        if raw_condition:
            return canonical_condition(raw_condition) or raw_condition

    return "Missing / invalid"


def split_retention_survey_waves(rows: list[dict[str, str]]) -> dict[str, dict[str, list[dict[str, str]]]]:
    """Split survey rows into immediate and delayed retention waves by MCID."""
    waves: dict[str, dict[str, list[dict[str, str]]]] = {
        "immediate": defaultdict(list),
        "delayed": defaultdict(list),
    }

    for row in rows:
        participant_id = mcid_from_row(row)
        if not participant_id:
            continue
        if delayed_flag(row):
            if delayed_included_flag(row):
                waves["delayed"][participant_id].append(row)
        else:
            waves["immediate"][participant_id].append(row)

    return {
        "immediate": dict(waves["immediate"]),
        "delayed": dict(waves["delayed"]),
    }


def build_retention_participants_from_survey(survey_rows: list[dict[str, str]]) -> list[dict[str, Any]]:
    """Build the minimal participant records needed by the retention scoring app.

    The scoring app now uses survey_export.tsv as the source of truth for both
    retention answers and experimental condition. It deliberately does not read
    or merge game-log metadata.
    """
    waves = split_retention_survey_waves(survey_rows)
    participants: list[dict[str, Any]] = []

    for participant_id in sorted(waves["immediate"]):
        immediate_row = waves["immediate"][participant_id][0]
        delayed_row = (waves["delayed"].get(participant_id) or [None])[0]
        immediate_retention = retention_wave_summary(immediate_row)
        delayed_retention = retention_wave_summary(delayed_row)

        participants.append({
            "participant_id": participant_id,
            "condition": condition_from_retention_survey_row(immediate_row),
            "ret_immediate_seen_count": immediate_retention["seen_creature_count"],
            "ret_immediate_answer_count": immediate_retention["answer_count"],
            "ret_immediate_answers": immediate_retention["answers"],
            "ret_immediate_seen_invalid": immediate_retention["seen_invalid"],
            "ret_delayed_available": delayed_retention["available"],
            "ret_delayed_seen_count": delayed_retention["seen_creature_count"],
            "ret_delayed_answer_count": delayed_retention["answer_count"],
            "ret_delayed_answers": delayed_retention["answers"],
            "ret_delayed_seen_invalid": delayed_retention["seen_invalid"],
        })

    return participants

PROMPT_SCORE_COLUMNS = {
    "MCID",
    "creature",
    "q_element",
    "answer",
    "answer_std",
    "moment",
    "creature_id",
    "question_key",
    "final_score",
    "final_status",
}


def retention_column_name(slot_index: int, suffix: str) -> str:
    return f"ret_slot{slot_index:02d}_{suffix}"


def parse_seen_details(value: object) -> tuple[list[str], list[str]]:
    raw_items = [clean(item) for item in clean(value).split(",") if clean(item)]
    valid: list[str] = []
    invalid: list[str] = []

    for item in raw_items:
        if item in IGNORED_SEEN_EXTRAS:
            continue
        if item in CREATURE_NAME_BY_ID:
            valid.append(item)
        else:
            invalid.append(item)

    return valid, invalid


def extract_retention_answers(row: dict[str, Any]) -> dict[str, dict[str, str]]:
    seen_creatures, _invalid = parse_seen_details(row.get("SEEN"))
    answers: dict[str, dict[str, str]] = {}

    for slot_index, creature_id in enumerate(seen_creatures[:MAX_RETENTION_SLOTS], start=1):
        answers_for_creature: dict[str, str] = {}
        for suffix, _label in RETENTION_QUESTION_SPECS:
            answers_for_creature[suffix] = clean(row.get(retention_column_name(slot_index, suffix)))
        answers[creature_id] = answers_for_creature

    return answers


def count_non_empty_answers(answers_by_creature: dict[str, dict[str, str]]) -> int:
    return sum(
        1
        for answers_for_creature in answers_by_creature.values()
        for answer in answers_for_creature.values()
        if clean(answer)
    )


def retention_wave_summary(row: dict[str, Any] | None) -> dict[str, Any]:
    if row is None:
        return {
            "available": False,
            "seen_creature_count": 0,
            "answer_count": 0,
            "answers": {},
            "seen_invalid": [],
        }

    seen_creatures, invalid_seen = parse_seen_details(row.get("SEEN"))
    answers = extract_retention_answers(row)

    return {
        "available": True,
        "seen_creature_count": len(seen_creatures),
        "answer_count": count_non_empty_answers(answers),
        "answers": answers,
        "seen_invalid": invalid_seen,
    }


def prompt_score_key(moment: object, creature_id: object, q_element: object) -> str:
    return "|".join([clean(moment), clean(creature_id), clean(q_element)])


def _prompt_score_for_participant(participant: dict[str, Any], moment: str, creature_id: str, q_element: str) -> dict[str, Any]:
    lookup = participant.get("_retention_prompt_scores") or {}

    # Current serialisable schema: moment | creature_id | q_element.
    string_key = prompt_score_key(moment, creature_id, q_element)
    if string_key in lookup:
        return lookup[string_key]

    # Backward compatibility if this function is called before the in-memory
    # tuple-key schema has fully disappeared.
    return lookup.get((moment, creature_id, q_element), {})


def _natural_source_key(label: str) -> list[Any]:
    return [int(part) if part.isdigit() else part for part in re.split(r"(\d+)", label)]


def _source_labels(row: dict[str, str], prefix: str) -> list[str]:
    labels = set()
    for key in row:
        match = re.match(rf"^({prefix}\d+(?:_\d+)?)_score$", key)
        if match:
            labels.add(match.group(1))
    return sorted(labels, key=_natural_source_key)


def _format_grader_note(score_info: dict[str, Any]) -> str:
    notes: list[str] = []
    for label in sorted(score_info.get("grader_labels") or [], key=_natural_source_key):
        note = clean(score_info.get(f"{label}_note"))
        if note:
            display = label.replace("grader", "Grader ", 1).replace("_", " / ")
            notes.append(f"<strong>{display}:</strong> {note}")
    return "<br>".join(notes)


def build_retention_question_rows(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []

    for participant in participants:
        participant_id = participant["participant_id"]
        condition = participant["condition"]

        for wave_key, moment in (("immediate", "Immediate"), ("delayed", "Delayed")):
            wave_data = participant.get(f"ret_{wave_key}_answers", {}) or {}
            for creature_id, answers in wave_data.items():
                for suffix, raw_label in RETENTION_QUESTION_SPECS:
                    answer = clean(answers.get(suffix))
                    for q_element in RETENTION_PROMPT_TO_ELEMENTS.get(suffix, []):
                        label = RETENTION_ELEMENT_LABEL_BY_KEY.get(q_element, q_element)
                        score_info = _prompt_score_for_participant(participant, moment, creature_id, q_element)
                        if not answer and not score_info:
                            continue
                        output = {
                            "participant_id": participant_id,
                            "condition": condition,
                            "moment": moment,
                            "creature_id": creature_id,
                            "creature_name": CREATURE_NAME_BY_ID.get(creature_id, creature_id),
                            "question": q_element,
                            "q_element": q_element,
                            "question_key": suffix,
                            "question_label": label,
                            "raw_question_label": raw_label,
                            "answer": answer,
                            "answer_std": score_info.get("answer_std"),
                            "genai_labels": score_info.get("genai_labels") or [],
                            "grader_labels": score_info.get("grader_labels") or [],
                            "genai_score": score_info.get("genai_score"),
                            "genai_confidence": score_info.get("genai_confidence"),
                            "final_score": score_info.get("final_score_display"),
                            "final_status": score_info.get("final_status"),
                            "final_note_auto": score_info.get("final_note_auto"),
                            "final_note_manual": score_info.get("final_note_manual"),
                            "grader_notes_html": _format_grader_note(score_info),
                        }
                        for source_label in score_info.get("genai_labels") or []:
                            output[f"{source_label}_score"] = score_info.get(f"{source_label}_score")
                            output[f"{source_label}_confidence"] = score_info.get(f"{source_label}_confidence")
                        for source_label in score_info.get("grader_labels") or []:
                            output[f"{source_label}_score"] = score_info.get(f"{source_label}_score")
                        # Legacy UI aliases when the first two grader files exist.
                        output["grader1_score"] = score_info.get("grader1_score")
                        output["grader2_score"] = score_info.get("grader2_score")
                        rows.append(output)

    return rows


def _detect_delimiter(path: Path) -> str:
    return "\t" if path.suffix.lower() == ".tsv" else ","


def _load_plain_delimited(path: Path) -> list[dict[str, str]]:
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        return [dict(row) for row in csv.DictReader(handle, delimiter=_detect_delimiter(path))]


def _normalise_moment(value: object) -> str:
    text = clean(value).lower()
    if text.startswith("imm"):
        return "Immediate"
    if text.startswith("del"):
        return "Delayed"
    return clean(value)


def _score_from_row(row: dict[str, str], grader_prefix: str) -> float | None:
    if clean(row.get(f"{grader_prefix}_status")) != "graded":
        return None
    score = parse_numeric(row.get(f"{grader_prefix}_score"))
    if score is None or score < 0 or score > 2:
        return None
    return score


def _final_score_from_row(row: dict[str, str]) -> float | None:
    score = parse_numeric(row.get("final_score"))
    if score is None or score < 0 or score > 2:
        return None
    return score


FINAL_RETENTION_PLACEHOLDER = "x should appear here when scoring is finished"

FINAL_RETENTION_REQUIRED_COLUMNS = {
    "MCID",
    "moment",
    "creature_id",
    "q_element",
    "final_score",
}

FINAL_RETENTION_MOMENTS = [
    ("Immediate", "immediate"),
    ("Delayed", "delayed"),
]

FINAL_RETENTION_QUESTION_TABLE_SPECS = [
    {
        "key": "q1_name",
        "title": "Retention question 1: Creature name",
        "note": "Retention scores range from 0 (= fully wrong) to 2 (= fully correct).",
        "items": [("Q1_name", "Creature name")],
    },
    {
        "key": "q2_facts",
        "title": "Retention question 2: Three creature facts",
        "note": "Retention scores range from 0 (= fully wrong) to 2 (= fully correct).",
        "items": [
            ("Q2_fact1", "Fact #1"),
            ("Q2_fact2", "Fact #2"),
            ("Q2_fact3", "Fact #3"),
        ],
    },
    {
        "key": "q3_looks",
        "title": "Retention question 3: Creature looks",
        "note": "Retention scores range from 0 (= fully wrong) to 2 (= fully correct).",
        "items": [("Q3_looks", "Creature looks")],
    },
    {
        "key": "q4_location",
        "title": "Retention question 4: Chapter and creature environment",
        "note": "Retention scores range from 0 (= fully wrong) to 2 (= fully correct).",
        "items": [
            ("Q4_chapter", "Chapter name"),
            ("Q4_env", "Creature environment"),
        ],
    },
]


def _final_number_text(value: object) -> str:
    if value is None:
        return "—"
    return f"{float(value):.2f}"


def _final_stat_cell(values: list[float], expected_n: int) -> dict[str, Any]:
    summary = summarise(values)
    valid_n = int(summary["n"] or 0)
    has_values = valid_n > 0

    return {
        "n_valid": valid_n,
        "n_expected": expected_n,
        "complete": expected_n > 0 and valid_n == expected_n,
        "mean": _final_number_text(summary["mean"]) if has_values else FINAL_RETENTION_PLACEHOLDER,
        "sd": _final_number_text(summary["sd"]) if summary["sd"] is not None else ("—" if has_values else FINAL_RETENTION_PLACEHOLDER),
        "min": _final_number_text(summary["min"]) if has_values else FINAL_RETENTION_PLACEHOLDER,
        "max": _final_number_text(summary["max"]) if has_values else FINAL_RETENTION_PLACEHOLDER,
    }


def _load_final_retention_rows(path: Path = RETENTION_SCORES_PATH) -> dict[str, Any]:
    if not path.exists():
        return {
            "available": False,
            "rows": [],
            "warnings": [f"Final retention tables should appear here when scoring is finished. Missing file: {path}."],
        }

    rows = _load_plain_delimited(path)
    if not rows:
        return {
            "available": False,
            "rows": [],
            "warnings": [f"Final retention tables should appear here when scoring is finished. Empty file: {path}."],
        }

    header = set(rows[0])
    missing = sorted(FINAL_RETENTION_REQUIRED_COLUMNS - header)
    if missing:
        return {
            "available": False,
            "rows": [],
            "warnings": [
                "Final retention tables should appear here when scoring is finished. "
                f"The final retention file is missing required column(s): {', '.join(missing)}."
            ],
        }

    duplicate_counts: dict[tuple[str, str, str, str], int] = defaultdict(int)
    for row in rows:
        key = (
            clean(row.get("MCID")),
            _normalise_moment(row.get("moment")),
            clean(row.get("creature_id")),
            clean(row.get("q_element")),
        )
        duplicate_counts[key] += 1

    known_elements = {key for key, _label in RETENTION_ELEMENT_SPECS}
    known_moments = {label for label, _key in FINAL_RETENTION_MOMENTS}

    normalised_rows: list[dict[str, Any]] = []
    duplicate_row_count = 0
    skipped_identity_count = 0
    skipped_unknown_count = 0

    for row in rows:
        participant_id = clean(row.get("MCID"))
        moment = _normalise_moment(row.get("moment"))
        creature_id = clean(row.get("creature_id"))
        q_element = clean(row.get("q_element"))
        identity = (participant_id, moment, creature_id, q_element)

        if not all(identity):
            skipped_identity_count += 1
            continue

        if duplicate_counts[identity] > 1:
            duplicate_row_count += 1
            continue

        if moment not in known_moments or q_element not in known_elements:
            skipped_unknown_count += 1
            continue

        output = dict(row)
        output["MCID"] = participant_id
        output["moment"] = moment
        output["creature_id"] = creature_id
        output["q_element"] = q_element
        output["_final_score_numeric"] = _final_score_from_row(row)
        normalised_rows.append(output)

    warnings: list[str] = []
    if duplicate_row_count:
        warnings.append(
            f"{duplicate_row_count} duplicate final-score row(s) were excluded from the final retention descriptives. "
            "Each MCID × moment × creature_id × q_element should appear only once."
        )
    if skipped_identity_count:
        warnings.append(
            f"{skipped_identity_count} final-score row(s) were excluded because MCID, moment, creature_id, or q_element was missing."
        )
    if skipped_unknown_count:
        warnings.append(
            f"{skipped_unknown_count} final-score row(s) were excluded because moment or q_element was not recognised."
        )
    if not normalised_rows:
        warnings.append("Final retention tables should appear here when scoring is finished; no usable final-score rows were available.")

    return {
        "available": bool(normalised_rows),
        "rows": normalised_rows,
        "warnings": warnings,
    }


def _build_complete_final_participant_scores(
    final_rows: list[dict[str, Any]],
    condition_by_id: dict[str, str],
) -> list[dict[str, Any]]:
    expected_rows_by_participant_moment: dict[tuple[str, str], list[dict[str, Any]]] = defaultdict(list)
    element_scores_by_creature: dict[tuple[str, str, str], dict[str, float]] = defaultdict(dict)

    for row in final_rows:
        participant_id = clean(row.get("MCID"))
        moment = clean(row.get("moment"))
        creature_id = clean(row.get("creature_id"))
        q_element = clean(row.get("q_element"))
        score = row.get("_final_score_numeric")

        expected_rows_by_participant_moment[(participant_id, moment)].append(row)
        if score is not None:
            element_scores_by_creature[(participant_id, moment, creature_id)][q_element] = float(score)

    final_scores: list[dict[str, Any]] = []
    for (participant_id, moment), expected_rows in expected_rows_by_participant_moment.items():
        if any(row.get("_final_score_numeric") is None for row in expected_rows):
            continue

        component_values: list[float] = []
        creature_ids = sorted({clean(row.get("creature_id")) for row in expected_rows})
        for creature_id in creature_ids:
            element_scores = element_scores_by_creature.get((participant_id, moment, creature_id), {})
            for _component_key, _component_label, q_elements in RETENTION_COMPONENT_SPECS:
                scores = [element_scores[q_element] for q_element in q_elements if q_element in element_scores]
                if scores:
                    component_values.append(sum(scores) / len(scores))

        if not component_values:
            continue

        final_scores.append({
            "participant_id": participant_id,
            "condition": condition_by_id.get(participant_id, "Missing / invalid"),
            "moment": moment,
            "score": sum(component_values) / len(component_values),
        })

    return final_scores


def build_final_retention_descriptives(
    participants: list[dict[str, Any]],
    path: Path = RETENTION_SCORES_PATH,
    condition_order: list[str] | None = None,
) -> dict[str, Any]:
    """Build Retention-tab descriptives from adjudicated final_score only.

    Participant-level retention is calculated on the 0-2 rubric scale. A
    participant/test-occasion score is included only when every expected
    q_element row for that participant and moment has a valid final_score.
    Question-element tables can preview partial scoring, with red n shown in
    the browser whenever valid_n < expected_n.
    """
    condition_order = condition_order or []
    loaded = _load_final_retention_rows(path)
    final_rows = loaded["rows"]

    condition_by_id = {
        participant.get("participant_id"): participant.get("condition", "Missing / invalid")
        for participant in participants
        if participant.get("participant_id")
    }

    complete_participant_scores = _build_complete_final_participant_scores(final_rows, condition_by_id)

    expected_ids_by_condition_moment: dict[tuple[str, str], set[str]] = defaultdict(set)
    for row in final_rows:
        participant_id = clean(row.get("MCID"))
        moment = clean(row.get("moment"))
        condition = condition_by_id.get(participant_id, "Missing / invalid")
        expected_ids_by_condition_moment[(condition, moment)].add(participant_id)
        expected_ids_by_condition_moment[("Overall", moment)].add(participant_id)

    scores_by_condition_moment: dict[tuple[str, str], list[float]] = defaultdict(list)
    for row in complete_participant_scores:
        condition = clean(row.get("condition")) or "Missing / invalid"
        moment = clean(row.get("moment"))
        score = float(row["score"])
        scores_by_condition_moment[(condition, moment)].append(score)
        scores_by_condition_moment[("Overall", moment)].append(score)

    condition_rows: list[dict[str, Any]] = []
    for condition in condition_order + ["Overall"]:
        output: dict[str, Any] = {"condition": condition}
        for moment_label, moment_key in FINAL_RETENTION_MOMENTS:
            values = scores_by_condition_moment.get((condition, moment_label), [])
            expected_n = len(expected_ids_by_condition_moment.get((condition, moment_label), set()))
            output[moment_key] = _final_stat_cell(values, expected_n)
        condition_rows.append(output)

    question_tables: list[dict[str, Any]] = []
    for table_spec in FINAL_RETENTION_QUESTION_TABLE_SPECS:
        table_rows: list[dict[str, Any]] = []
        for q_element, item_label in table_spec["items"]:
            output: dict[str, Any] = {"item": item_label}
            for moment_label, moment_key in FINAL_RETENTION_MOMENTS:
                scoped = [
                    row for row in final_rows
                    if row.get("q_element") == q_element and row.get("moment") == moment_label
                ]
                values = [
                    float(row["_final_score_numeric"])
                    for row in scoped
                    if row.get("_final_score_numeric") is not None
                ]
                output[moment_key] = _final_stat_cell(values, len(scoped))
            table_rows.append(output)

        question_tables.append({
            "key": table_spec["key"],
            "title": table_spec["title"],
            "note": table_spec["note"],
            "rows": table_rows,
        })

    return {
        "available": loaded["available"],
        "placeholder": FINAL_RETENTION_PLACEHOLDER,
        "warnings": loaded["warnings"],
        "condition_rows": condition_rows,
        "question_tables": question_tables,
        "boxplot_rows": complete_participant_scores,
    }


def _load_prompt_level_scores(rows: list[dict[str, str]]) -> dict[str, Any]:
    participant_scores: dict[str, dict[str, Any]] = defaultdict(dict)
    prompt_scores_by_participant: dict[str, dict[str, dict[str, Any]]] = defaultdict(dict)
    values_by_participant_wave: dict[tuple[str, str, str, str], list[float]] = defaultdict(list)

    for row in rows:
        participant_id = clean(row.get("MCID"))
        moment = _normalise_moment(row.get("moment"))
        creature_id = clean(row.get("creature_id"))
        question_key = clean(row.get("question_key"))
        q_element = clean(row.get("q_element"))
        if not participant_id or not moment or not creature_id or not q_element:
            continue

        genai_labels = _source_labels(row, "genai")
        grader_labels = _source_labels(row, "grader")
        first_genai_label = genai_labels[0] if genai_labels else ""
        final_score = _final_score_from_row(row)
        final_score_raw = clean(row.get("final_score"))
        score_info = {
            "answer_std": clean(row.get("answer_std")),
            "genai_labels": genai_labels,
            "grader_labels": grader_labels,
            "genai_score": parse_numeric(row.get("genai_score") or (row.get(f"{first_genai_label}_score") if first_genai_label else "")),
            "genai_confidence": parse_numeric(clean(row.get("genai_confidence") or (row.get(f"{first_genai_label}_confidence") if first_genai_label else "")).replace("%", "")),
            "genai_note": clean(row.get("genai_note") or (row.get(f"{first_genai_label}_note") if first_genai_label else "")),
            "final_score": final_score,
            "final_score_display": final_score_raw if final_score_raw else ("" if final_score is None else final_score),
            "final_status": clean(row.get("final_status")),
            "final_note_auto": clean(row.get("final_note_auto")),
            "final_note_manual": clean(row.get("final_note_manual")),
        }
        for label in genai_labels:
            score_info[f"{label}_score"] = parse_numeric(row.get(f"{label}_score"))
            score_info[f"{label}_confidence"] = parse_numeric(clean(row.get(f"{label}_confidence")).replace("%", ""))
            score_info[f"{label}_note"] = clean(row.get(f"{label}_note"))
        for label in grader_labels:
            score_info[f"{label}_score"] = _score_from_row(row, label)
            score_info[f"{label}_status"] = clean(row.get(f"{label}_status"))
            score_info[f"{label}_note"] = clean(row.get(f"{label}_note"))
        # Legacy UI aliases when the first two grader files exist.
        for label in ("grader1", "grader2"):
            score_info.setdefault(f"{label}_score", _score_from_row(row, label))
            score_info.setdefault(f"{label}_status", clean(row.get(f"{label}_status")))
            score_info.setdefault(f"{label}_note", clean(row.get(f"{label}_note")))

        prompt_scores_by_participant[participant_id][prompt_score_key(moment, creature_id, q_element)] = score_info
        if final_score is not None:
            values_by_participant_wave[(participant_id, moment, creature_id, q_element)].append(final_score)

    component_values_by_participant_wave: dict[tuple[str, str, str, str], list[float]] = defaultdict(list)
    element_counts_by_participant_wave: dict[tuple[str, str], int] = defaultdict(int)
    component_counts_by_participant_wave: dict[tuple[str, str], int] = defaultdict(int)

    grouped_by_creature_wave: dict[tuple[str, str, str], dict[str, float]] = defaultdict(dict)
    for (participant_id, moment, creature_id, q_element), values in values_by_participant_wave.items():
        if values:
            grouped_by_creature_wave[(participant_id, moment, creature_id)][q_element] = values[0]
            element_counts_by_participant_wave[(participant_id, moment)] += 1

    for (participant_id, moment, _creature_id), element_scores in grouped_by_creature_wave.items():
        for _component_key, _component_label, q_elements in RETENTION_COMPONENT_SPECS:
            scores = [element_scores[q_element] for q_element in q_elements if q_element in element_scores]
            if not scores:
                continue
            component_values_by_participant_wave[(participant_id, moment)].append(sum(scores) / (len(scores) * 2))
            component_counts_by_participant_wave[(participant_id, moment)] += 1

    for (participant_id, moment), values in component_values_by_participant_wave.items():
        proportion = sum(values) / len(values) if values else None
        if moment == "Immediate":
            participant_scores[participant_id]["ret_immediate_score"] = proportion
            participant_scores[participant_id]["ret_immediate_scored_prompt_count"] = element_counts_by_participant_wave[(participant_id, moment)]
            participant_scores[participant_id]["ret_immediate_scored_component_count"] = component_counts_by_participant_wave[(participant_id, moment)]
        elif moment == "Delayed":
            participant_scores[participant_id]["ret_delayed_score"] = proportion
            participant_scores[participant_id]["ret_delayed_scored_prompt_count"] = element_counts_by_participant_wave[(participant_id, moment)]
            participant_scores[participant_id]["ret_delayed_scored_component_count"] = component_counts_by_participant_wave[(participant_id, moment)]

    for participant_id, prompt_scores in prompt_scores_by_participant.items():
        participant_scores[participant_id]["_retention_prompt_scores"] = prompt_scores

    return dict(participant_scores)


def _load_legacy_participant_scores(rows: list[dict[str, str]]) -> dict[str, dict[str, Any]]:
    by_id: dict[str, dict[str, Any]] = {}
    for row in rows:
        participant_id = first_present(row, ["MCID"])
        if not participant_id:
            continue
        by_id[participant_id] = {
            score_name: parse_numeric(row.get(column_name))
            for score_name, column_name in RETENTION_SCORE_COLUMNS.items()
        }
    return by_id


def load_retention_scores(path: Path = RETENTION_SCORES_PATH) -> tuple[dict[str, dict[str, Any]], list[str]]:
    fix_hint = (
        "To fix this, rebuild the merged retention scores by running sum_merged with PUBLIC_ROUTE=True "
        "after the retention source files exist. If you have not prepared GenAI scoring yet, first run "
        "sum_merged with PUBLIC_ROUTE=False, fill data/retention_scores_genai*.tsv using "
        "data/config/genai_prompt.txt and the rubric files, then rerun sum_merged with PUBLIC_ROUTE=True. "
        "If human review is required, complete it with python main.py score_ret grader=1 "
        "(or another positive integer) before the final PUBLIC_ROUTE=True rebuild."
    )

    if not path.exists():
        return {}, [
            f"Retention scores merged file not found: {path}. {fix_hint}"
        ]
    
    rows = _load_plain_delimited(path)
    if not rows:
        return {}, [
            f"Retention scores merged file is empty: {path}. {fix_hint}"
        ]

    header = set(rows[0])
    if PROMPT_SCORE_COLUMNS.issubset(header):
        return _load_prompt_level_scores(rows), []

    required = {"MCID", *RETENTION_SCORE_COLUMNS.values()}
    missing = sorted(required - header)
    if missing:
        return {}, [
            "Retention scores merged file is missing required column(s): "
            f"{', '.join(missing)}. Do not edit this file manually; rebuild it from the "
            f"retention_scores_genai*.tsv and retention_scores_grader*.tsv source files. {fix_hint}"
        ]

    return _load_legacy_participant_scores(rows), []


def attach_retention_scores(participants: list[dict[str, Any]], scores_by_id: dict[str, dict[str, Any]]) -> None:
    for participant in participants:
        scores = scores_by_id.get(participant["participant_id"], {})
        participant["ret_immediate_score"] = scores.get("ret_immediate_score")
        participant["ret_delayed_score"] = scores.get("ret_delayed_score")
        participant["ret_immediate_scored_prompt_count"] = scores.get("ret_immediate_scored_prompt_count")
        participant["ret_delayed_scored_prompt_count"] = scores.get("ret_delayed_scored_prompt_count")
        participant["ret_immediate_scored_component_count"] = scores.get("ret_immediate_scored_component_count")
        participant["ret_delayed_scored_component_count"] = scores.get("ret_delayed_scored_component_count")
        participant["_retention_prompt_scores"] = scores.get("_retention_prompt_scores", {})


def _valid_int_score(value: object) -> int | None:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return None
    score = int(number)
    if score < 0 or score > 2:
        return None
    return score


def quadratic_weighted_kappa(pairs: list[tuple[int, int]], categories: list[int] | None = None) -> float | None:
    if not pairs:
        return None

    cats = categories or [0, 1, 2]
    index = {cat: i for i, cat in enumerate(cats)}
    k = len(cats)

    matrix = [[0.0 for _ in cats] for _ in cats]
    for left, right in pairs:
        if left in index and right in index:
            matrix[index[left]][index[right]] += 1.0

    n = sum(sum(row) for row in matrix)
    if n == 0:
        return None

    row_totals = [sum(row) for row in matrix]
    col_totals = [sum(matrix[i][j] for i in range(k)) for j in range(k)]

    observed = 0.0
    expected = 0.0
    for i in range(k):
        for j in range(k):
            weight = ((i - j) ** 2) / ((k - 1) ** 2)
            observed += weight * matrix[i][j]
            expected += weight * (row_totals[i] * col_totals[j] / n)

    if expected == 0:
        return 1.0 if observed == 0 else None
    return 1.0 - (observed / expected)


def ordinal_krippendorff_alpha(ratings_by_unit: list[list[int]], categories: list[int] | None = None) -> float | None:
    """Krippendorff's alpha for ordinal 0-2 ratings.

    The implementation follows the usual coincidence-matrix definition. Units
    with fewer than two valid ratings are ignored.
    """
    cats = categories or [0, 1, 2]
    index = {cat: i for i, cat in enumerate(cats)}
    k = len(cats)
    coincidence = [[0.0 for _ in cats] for _ in cats]
    valid_units = 0

    for raw_unit in ratings_by_unit:
        values = [score for score in raw_unit if score in index]
        m = len(values)
        if m < 2:
            continue
        valid_units += 1
        counts = [0 for _ in cats]
        for score in values:
            counts[index[score]] += 1
        for i in range(k):
            for j in range(k):
                if i == j:
                    coincidence[i][j] += counts[i] * (counts[j] - 1) / (m - 1)
                else:
                    coincidence[i][j] += counts[i] * counts[j] / (m - 1)

    n = sum(sum(row) for row in coincidence)
    if valid_units == 0 or n <= 1:
        return None

    marginals = [sum(row) for row in coincidence]
    observed = 0.0
    expected = 0.0
    for i in range(k):
        for j in range(k):
            delta = ((i - j) ** 2) / ((k - 1) ** 2)
            observed += coincidence[i][j] * delta
            expected += marginals[i] * marginals[j] * delta / (n - 1)

    if expected == 0:
        return 1.0 if observed == 0 else None
    return 1.0 - observed / expected


def _alpha_row(label: str, ratings_by_unit: list[list[int]]) -> dict[str, Any]:
    usable = [unit for unit in ratings_by_unit if len(unit) >= 2]
    alpha = ordinal_krippendorff_alpha(usable)
    return {
        "group": label,
        "n_units": len(usable),
        "ordinal_krippendorff_alpha": None if alpha is None else round(alpha, 3),
    }


def _agreement_row(label: str, pairs: list[tuple[int, int]]) -> dict[str, Any]:
    exact = sum(1 for left, right in pairs if left == right)
    n = len(pairs)
    kappa = quadratic_weighted_kappa(pairs)
    return {
        "group": label,
        "n_double_scored": n,
        "exact_agreement_percent": None if n == 0 else round(100 * exact / n, 1),
        "quadratic_weighted_kappa": None if kappa is None else round(kappa, 3),
    }


def _agreement_row_weighted(label: str, pairs: list[tuple[int, int]], weights: list[int] | None = None) -> dict[str, Any]:
    if weights is None:
        return _agreement_row(label, pairs)
    expanded: list[tuple[int, int]] = []
    for pair, weight in zip(pairs, weights):
        expanded.extend([pair] * max(1, int(weight)))
    row = _agreement_row(label, expanded)
    row["n_unique_double_scored"] = len(pairs)
    row["n_weighted_occurrences"] = len(expanded)
    return row


def _row_weight(row: dict[str, str]) -> int:
    number = parse_numeric(row.get("occurrence_weight"))
    if number is None or number < 1:
        return 1
    return int(number)


def _unique_rows_by_task(rows: list[dict[str, str]]) -> list[dict[str, str]]:
    seen: set[str] = set()
    unique: list[dict[str, str]] = []
    for row in rows:
        key = clean(row.get("task_id")) or "|".join([
            clean(row.get("q_element")) or clean(row.get("question")),
            clean(row.get("creature_id")),
            clean(row.get("answer_std")),
        ])
        if key in seen:
            continue
        seen.add(key)
        unique.append(row)
    return unique


def retention_reliability_summary(path: Path = RETENTION_SCORES_PATH) -> dict[str, Any]:
    if not path.exists():
        return {"available": False, "method": "No retention_scores_merged.tsv file found.", "rows": []}

    rows = _load_plain_delimited(path)
    if not rows or not PROMPT_SCORE_COLUMNS.issubset(set(rows[0])):
        return {
            "available": False,
            "method": "Agreement requires the q_element-level retention_scores_merged.tsv schema produced by sum_merged.",
            "rows": [],
        }

    unique_rows = _unique_rows_by_task([row for row in rows if clean(row.get("task_id"))])

    def human_pairs(row: dict[str, str]) -> list[tuple[int, int]]:
        graded: list[int] = []
        for label in _source_labels(row, "grader"):
            if clean(row.get(f"{label}_status")) != "graded":
                continue
            score = _valid_int_score(row.get(f"{label}_score"))
            if score is not None:
                graded.append(score)
        return list(combinations(graded, 2))

    def genai_human_pairs(row: dict[str, str]) -> list[tuple[int, int]]:
        human_scores: list[int] = []
        for label in _source_labels(row, "grader"):
            if clean(row.get(f"{label}_status")) != "graded":
                continue
            score = _valid_int_score(row.get(f"{label}_score"))
            if score is not None:
                human_scores.append(score)
        if not human_scores or not all(score == human_scores[0] for score in human_scores):
            return []

        human_consensus = human_scores[0]
        pairs: list[tuple[int, int]] = []
        genai_labels = _source_labels(row, "genai")
        if genai_labels:
            for label in genai_labels:
                genai = _valid_int_score(row.get(f"{label}_score"))
                if genai is not None:
                    pairs.append((genai, human_consensus))
        else:
            genai = _valid_int_score(row.get("genai_score"))
            if genai is not None:
                pairs.append((genai, human_consensus))
        return pairs

    summary_rows: list[dict[str, Any]] = []
    for label, pair_getter in (
        ("Human-human unique answers", human_pairs),
        ("Human-human occurrence-weighted", human_pairs),
        ("GenAI-human unique answers", genai_human_pairs),
        ("GenAI-human occurrence-weighted", genai_human_pairs),
    ):
        source_rows = unique_rows if "unique" in label else rows
        pairs: list[tuple[int, int]] = []
        weights: list[int] = []
        pairs_by_group: dict[str, list[tuple[int, int]]] = defaultdict(list)
        weights_by_group: dict[str, list[int]] = defaultdict(list)
        for row in source_rows:
            row_pairs = pair_getter(row)
            if not row_pairs:
                continue
            weight = _row_weight(row) if "weighted" in label else 1
            question_key = clean(row.get("q_element")) or clean(row.get("question")) or "Unknown"
            for pair in row_pairs:
                pairs.append(pair)
                weights.append(weight)
                pairs_by_group[question_key].append(pair)
                weights_by_group[question_key].append(weight)
        overall = _agreement_row_weighted(label + " · Overall", pairs, weights if "weighted" in label else None)
        summary_rows.append(overall)
        for question_key, _label in RETENTION_ELEMENT_SPECS:
            summary_rows.append(_agreement_row_weighted(
                label + f" · {question_key}",
                pairs_by_group.get(question_key, []),
                weights_by_group.get(question_key, []) if "weighted" in label else None,
            ))

    def all_available_ratings(row: dict[str, str]) -> list[int]:
        ratings: list[int] = []
        for label in _source_labels(row, "genai"):
            score = _valid_int_score(row.get(f"{label}_score"))
            if score is not None:
                ratings.append(score)
        if not _source_labels(row, "genai"):
            score = _valid_int_score(row.get("genai_score"))
            if score is not None:
                ratings.append(score)
        for label in _source_labels(row, "grader"):
            if clean(row.get(f"{label}_status")) != "graded":
                continue
            score = _valid_int_score(row.get(f"{label}_score"))
            if score is not None:
                ratings.append(score)
        return ratings

    unique_alpha_units = [all_available_ratings(row) for row in unique_rows]
    weighted_alpha_units: list[list[int]] = []
    for row in rows:
        ratings = all_available_ratings(row)
        if ratings:
            weighted_alpha_units.extend([ratings] * _row_weight(row))
    summary_rows.insert(0, _alpha_row("All available graders · Unique q_element answers", unique_alpha_units))
    summary_rows.insert(1, _alpha_row("All available graders · Occurrence-weighted", weighted_alpha_units))

    return {
        "available": any(row.get("n_double_scored", 0) or row.get("n_units", 0) for row in summary_rows),
        "method": "Reliability is reported from retention_scores_merged.tsv. Ordinal Krippendorff's alpha is the primary statistic across all available valid GenAI and human ratings for each q_element answer. Percent exact agreement and quadratic weighted Cohen's kappa are reported for human-human and GenAI-human pairs using ordinal categories 0, 1, 2. Unique-answer rows count each reviewed standardised answer once; occurrence-weighted rows expand each reviewed unique answer by its frequency in the q_element-level data.",
        "rows": summary_rows,
    }


def ret_condition_summary(participants: list[dict[str, Any]], condition_order: list[str]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []

    for condition in condition_order + ["Overall"]:
        scoped = participants if condition == "Overall" else [p for p in participants if p["condition"] == condition]
        rows.append({
            "condition": condition,
            "n": len(scoped),
            "ret_immediate_mean_sd": mean_sd_text([p.get("ret_immediate_score") for p in scoped]),
            "ret_delayed_mean_sd": mean_sd_text([p.get("ret_delayed_score") for p in scoped]),
            "ret_immediate_answer_count": sum(int(p.get("ret_immediate_answer_count") or 0) for p in scoped),
            "ret_delayed_answer_count": sum(int(p.get("ret_delayed_answer_count") or 0) for p in scoped),
            "ret_immediate_scored_prompt_count": sum(int(p.get("ret_immediate_scored_prompt_count") or 0) for p in scoped),
            "ret_delayed_scored_prompt_count": sum(int(p.get("ret_delayed_scored_prompt_count") or 0) for p in scoped),
            "ret_immediate_scored_component_count": sum(int(p.get("ret_immediate_scored_component_count") or 0) for p in scoped),
            "ret_delayed_scored_component_count": sum(int(p.get("ret_delayed_scored_component_count") or 0) for p in scoped),
            "ret_delayed_wave_count": sum(1 for p in scoped if p.get("completed_delayed_retention_test")),
        })

    return rows