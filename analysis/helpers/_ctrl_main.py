from __future__ import annotations

from typing import Any

from ._shared import mean, mean_sd_text, parse_numeric, rounded, summarise, two_decimals

CTRL_SCALE_MIN = 1
CTRL_SCALE_MAX = 7

CTRL_ITEMS = [
    {
        "column": "ctrl_scores_1",
        "statement": "At the checkpoints, I was free to decide how I wanted to proceed.",
    },
    {
        "column": "ctrl_scores_2",
        "statement": "The choices I made at the checkpoints influenced what happened next.",
    },
]


def valid_score(value: object) -> float | None:
    """Return a valid perceived-control score."""
    parsed = parse_numeric(value)
    if parsed is None or parsed < CTRL_SCALE_MIN or parsed > CTRL_SCALE_MAX:
        return None
    return parsed


def ctrl_participant_scores(row: dict[str, Any]) -> dict[str, float | None]:
    """Compute the perceived-control manipulation-check score from the immediate survey wave."""
    values = [valid_score(row.get(item["column"])) for item in CTRL_ITEMS]
    return {"ctrl_perceived": rounded(mean(values))}


def _item_summary(values: list[float | None]) -> dict[str, Any]:
    """Summarise one perceived-control item."""
    summary = summarise(values)
    return {
        "n": summary["n"],
        "mean_sd": mean_sd_text(values),
        "min": two_decimals(summary["min"]),
        "max": two_decimals(summary["max"]),
    }


def ctrl_overall_tables(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return perceived-control item summaries.

    The perceived-control manipulation check is asked once, about the checkpoint
    experience as a whole. Scores range from 1 to 7, where higher scores indicate
    higher perceived control.
    """
    rows: list[dict[str, Any]] = []

    for item in CTRL_ITEMS:
        values = [
            valid_score((participant.get("scale_values", {}) or {}).get(item["column"]))
            for participant in participants
        ]
        summary = _item_summary(values)

        rows.append({
            "item": item["statement"],
            "n": summary["n"],
            "mean_sd": summary["mean_sd"],
            "min": summary["min"],
            "max": summary["max"],
        })

    return [{
        "title": "Perceived control at checkpoints",
        "description": "Items, 1 = strongly disagree, 7 = strongly agree. Higher scores indicate higher perceived control.",
        "rows": rows,
    }]


def ctrl_condition_summary(participants: list[dict[str, Any]], condition_order: list[str]) -> list[dict[str, Any]]:
    """Summarise perceived control by condition and overall."""
    rows: list[dict[str, Any]] = []

    for condition in condition_order + ["Overall"]:
        scoped = participants if condition == "Overall" else [p for p in participants if p["condition"] == condition]
        values = [p.get("ctrl_perceived") for p in scoped]
        summary = summarise(values)

        rows.append({
            "condition": condition,
            "n": len(scoped),
            "ctrl_perceived_mean_sd": mean_sd_text(values),
            "ctrl_perceived_min": two_decimals(summary["min"]),
            "ctrl_perceived_max": two_decimals(summary["max"]),
        })

    return rows


def ctrl_quality_flags(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Flag perceived-control responses outside the valid range or unused valid response options."""
    observed_valid_scores: set[int] = set()
    out_of_range: list[str] = []

    for participant in participants:
        participant_id = participant.get("participant_id", "")
        values = participant.get("scale_values", {}) or {}

        for item in CTRL_ITEMS:
            column = item["column"]
            raw = values.get(column)
            parsed = parse_numeric(raw)
            if parsed is None:
                continue

            if parsed < CTRL_SCALE_MIN or parsed > CTRL_SCALE_MAX:
                out_of_range.append(f"{participant_id}: {column}={raw}")
                continue

            if float(parsed).is_integer():
                observed_valid_scores.add(int(parsed))

    flags: list[dict[str, Any]] = []

    if out_of_range:
        flags.append({
            "scale": "Perceived control",
            "flag": "Out-of-range value",
            "details": "; ".join(out_of_range),
        })

    missing_valid_scores = [
        str(value)
        for value in range(CTRL_SCALE_MIN, CTRL_SCALE_MAX + 1)
        if value not in observed_valid_scores
    ]
    if missing_valid_scores:
        flags.append({
            "scale": "Perceived control",
            "flag": "Valid response option not observed",
            "details": ", ".join(missing_valid_scores),
        })

    if not flags:
        flags.append({
            "scale": "Perceived control",
            "flag": "OK",
            "details": "All observed responses were within the valid scale range and every response option was observed at least once.",
        })

    return flags