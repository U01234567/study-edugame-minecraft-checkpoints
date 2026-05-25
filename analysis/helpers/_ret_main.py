from __future__ import annotations

import csv
from collections import defaultdict
from pathlib import Path
from typing import Any

from ._shared import (
    CREATURE_NAME_BY_ID,
    IGNORED_SEEN_EXTRAS,
    MAX_RETENTION_SLOTS,
    RETENTION_QUESTION_SPECS,
    RETENTION_SCORES_PATH,
    clean,
    first_present,
    mean_sd_text,
    parse_numeric,
)
from ._survey_io import detect_text_encoding

RETENTION_SCORE_COLUMNS = {
    "ret_immediate_score": "ret_immediate_score",
    "ret_delayed_score": "ret_delayed_score",
}

PROMPT_SCORE_COLUMNS = {
    "task_id",
    "participant_id",
    "moment",
    "creature_id",
    "question_key",
    "grader1_score",
    "grader1_status",
    "grader1_note",
    "grader2_score",
    "grader2_status",
    "grader2_note",
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


def prompt_score_key(moment: object, creature_id: object, question_key: object) -> str:
    return "|".join([clean(moment), clean(creature_id), clean(question_key)])


def _prompt_score_for_participant(participant: dict[str, Any], moment: str, creature_id: str, question_key: str) -> dict[str, Any]:
    lookup = participant.get("_retention_prompt_scores") or {}

    # Current serialisable schema.
    string_key = prompt_score_key(moment, creature_id, question_key)
    if string_key in lookup:
        return lookup[string_key]

    # Backward compatibility if this function is called before the in-memory
    # tuple-key schema has fully disappeared.
    return lookup.get((moment, creature_id, question_key), {})


def _format_grader_note(score_info: dict[str, Any]) -> str:
    notes: list[str] = []
    grader1_note = clean(score_info.get("grader1_note"))
    grader2_note = clean(score_info.get("grader2_note"))
    if grader1_note:
        notes.append(f"<strong>Grader 1:</strong> {grader1_note}")
    if grader2_note:
        notes.append(f"<strong>Grader 2:</strong> {grader2_note}")
    return "<br>".join(notes)


def build_retention_question_rows(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []

    for participant in participants:
        participant_id = participant["participant_id"]
        condition = participant["condition"]

        for wave_key, moment in (("immediate", "Immediate"), ("delayed", "Delayed")):
            wave_data = participant.get(f"ret_{wave_key}_answers", {}) or {}
            for creature_id, answers in wave_data.items():
                for suffix, label in RETENTION_QUESTION_SPECS:
                    answer = clean(answers.get(suffix))
                    if not answer:
                        continue
                    score_info = _prompt_score_for_participant(participant, moment, creature_id, suffix)
                    rows.append({
                        "participant_id": participant_id,
                        "condition": condition,
                        "moment": moment,
                        "creature_id": creature_id,
                        "creature_name": CREATURE_NAME_BY_ID.get(creature_id, creature_id),
                        "question": suffix,
                        "question_label": label,
                        "answer": answer,
                        "grader1_score": score_info.get("grader1_score"),
                        "grader2_score": score_info.get("grader2_score"),
                        "grader_notes_html": _format_grader_note(score_info),
                    })

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
    if score is None or score < 0 or score > 4:
        return None
    return score


def _load_prompt_level_scores(rows: list[dict[str, str]]) -> dict[str, Any]:
    participant_scores: dict[str, dict[str, Any]] = defaultdict(dict)
    prompt_scores_by_participant: dict[str, dict[str, dict[str, Any]]] = defaultdict(dict)
    values_by_participant_wave: dict[tuple[str, str], list[float]] = defaultdict(list)

    for row in rows:
        participant_id = clean(row.get("participant_id"))
        moment = _normalise_moment(row.get("moment"))
        creature_id = clean(row.get("creature_id"))
        question_key = clean(row.get("question_key"))
        if not participant_id or not moment or not creature_id or not question_key:
            continue

        g1 = _score_from_row(row, "grader1")
        g2 = _score_from_row(row, "grader2")
        score_info = {
            "grader1_score": g1,
            "grader1_status": clean(row.get("grader1_status")),
            "grader1_note": clean(row.get("grader1_note")),
            "grader2_score": g2,
            "grader2_status": clean(row.get("grader2_status")),
            "grader2_note": clean(row.get("grader2_note")),
        }
        prompt_scores_by_participant[participant_id][prompt_score_key(moment, creature_id, question_key)] = score_info
        
        if g1 is not None:
            values_by_participant_wave[(participant_id, moment)].append(g1)

    for (participant_id, moment), values in values_by_participant_wave.items():
        proportion = sum(values) / (len(values) * 4)
        if moment == "Immediate":
            participant_scores[participant_id]["ret_immediate_score"] = proportion
            participant_scores[participant_id]["ret_immediate_scored_prompt_count"] = len(values)
        elif moment == "Delayed":
            participant_scores[participant_id]["ret_delayed_score"] = proportion
            participant_scores[participant_id]["ret_delayed_scored_prompt_count"] = len(values)

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
    if not path.exists():
        return {}, [f"Retention scoring file not found: {path}"]

    rows = _load_plain_delimited(path)
    if not rows:
        return {}, [f"Retention scoring file is empty: {path}"]

    header = set(rows[0])
    if PROMPT_SCORE_COLUMNS.issubset(header):
        return _load_prompt_level_scores(rows), []

    required = {"MCID", *RETENTION_SCORE_COLUMNS.values()}
    missing = sorted(required - header)
    if missing:
        return {}, [f"Retention scoring file is missing required column(s): {', '.join(missing)}"]

    return _load_legacy_participant_scores(rows), []


def attach_retention_scores(participants: list[dict[str, Any]], scores_by_id: dict[str, dict[str, Any]]) -> None:
    for participant in participants:
        scores = scores_by_id.get(participant["participant_id"], {})
        participant["ret_immediate_score"] = scores.get("ret_immediate_score")
        participant["ret_delayed_score"] = scores.get("ret_delayed_score")
        participant["ret_immediate_scored_prompt_count"] = scores.get("ret_immediate_scored_prompt_count")
        participant["ret_delayed_scored_prompt_count"] = scores.get("ret_delayed_scored_prompt_count")
        participant["_retention_prompt_scores"] = scores.get("_retention_prompt_scores", {})


def _valid_int_score(value: object) -> int | None:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return None
    score = int(number)
    if score < 0 or score > 4:
        return None
    return score


def quadratic_weighted_kappa(pairs: list[tuple[int, int]], categories: list[int] | None = None) -> float | None:
    if not pairs:
        return None

    cats = categories or [0, 1, 2, 3, 4]
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


def retention_reliability_summary(path: Path = RETENTION_SCORES_PATH) -> dict[str, Any]:
    if not path.exists():
        return {"available": False, "method": "No retention_scoring.csv file found.", "rows": []}

    rows = _load_plain_delimited(path)
    if not rows or not PROMPT_SCORE_COLUMNS.issubset(set(rows[0])):
        return {
            "available": False,
            "method": "Agreement requires the prompt-level retention_scoring.csv schema produced by score_retention.py.",
            "rows": [],
        }

    pairs_by_group: dict[str, list[tuple[int, int]]] = defaultdict(list)
    all_pairs: list[tuple[int, int]] = []

    for row in rows:
        if clean(row.get("grader1_status")) != "graded" or clean(row.get("grader2_status")) != "graded":
            continue
        g1 = _valid_int_score(row.get("grader1_score"))
        g2 = _valid_int_score(row.get("grader2_score"))
        if g1 is None or g2 is None:
            continue
        pair = (g1, g2)
        all_pairs.append(pair)
        pairs_by_group[clean(row.get("question_key")) or "Unknown"].append(pair)

    summary_rows = [_agreement_row("Overall", all_pairs)]
    for question_key, _label in RETENTION_QUESTION_SPECS:
        summary_rows.append(_agreement_row(question_key, pairs_by_group.get(question_key, [])))

    return {
        "available": bool(all_pairs),
        "method": "Agreement is calculated on prompt-level rows where both graders have status=graded. Percent exact agreement is exact matching divided by double-scored prompts. Quadratic weighted Cohen's kappa uses ordinal categories 0, 1, 2, 3, 4 and squared-distance weights w_ij=(i-j)^2/(4^2).",
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
            "ret_delayed_wave_count": sum(1 for p in scoped if p.get("completed_delayed_retention_test")),
        })

    return rows