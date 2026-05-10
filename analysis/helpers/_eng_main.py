from __future__ import annotations

from typing import Any

from ._shared import mean, mean_sd_text, parse_numeric, rounded, summarise, two_decimals

ENG_CHAPTER_ITEMS = [
    {"index": 1, "statement": "I lost myself in this chapter."},
    {"index": 2, "statement": "The time I spent in this chapter just slipped away."},
    {"index": 3, "statement": "I was absorbed in this chapter."},
    {"index": 4, "statement": "This chapter was pleasurable to look at."},
    {"index": 5, "statement": "I felt interested in this chapter."},
]

ENG_OVERALL_ITEMS = [
    {"index": 1, "statement": "I felt frustrated while playing this game.", "reverse": True},
    {"index": 2, "statement": "I found this game confusing to play.", "reverse": True},
    {"index": 3, "statement": "Playing this game was worth my time.", "reverse": False},
    {"index": 4, "statement": "My experience was rewarding.", "reverse": False},
]

ENG_SCALE_MIN = 1
ENG_SCALE_MAX = 7


def valid_score(value: object, *, reverse: bool = False) -> float | None:
    """Return a valid engagement score, reverse-coded when required."""
    parsed = parse_numeric(value)
    if parsed is None or parsed < ENG_SCALE_MIN or parsed > ENG_SCALE_MAX:
        return None

    if reverse:
        return ENG_SCALE_MIN + ENG_SCALE_MAX - parsed

    return parsed


def participant_scale_values(participant: dict[str, Any]) -> dict[str, str]:
    """Return the scale-value dictionary stored on a participant row."""
    return participant.get("scale_values", {}) or {}


def _chapter_value(participant: dict[str, Any], chapter: int, item_index: int) -> float | None:
    return valid_score(participant_scale_values(participant).get(f"eng_ch{chapter}_scores_{item_index}"))


def _overall_value(participant: dict[str, Any], item_index: int, *, reverse: bool = False) -> float | None:
    return valid_score(participant_scale_values(participant).get(f"eng_overall_scores_{item_index}"), reverse=reverse)


def eng_participant_scores(row: dict[str, Any]) -> dict[str, float | None]:
    """Compute the preregistered participant-level engagement score from the immediate survey wave."""
    chapter_score = mean(
        valid_score(row.get(f"eng_ch{chapter}_scores_{index}"))
        for chapter in (1, 2, 3)
        for index in range(1, 6)
    )

    overall_score = mean(
        valid_score(row.get(f"eng_overall_scores_{item['index']}"), reverse=item["reverse"])
        for item in ENG_OVERALL_ITEMS
    )

    return {
        "eng_chapter": rounded(chapter_score),
        "eng_overall": rounded(overall_score),
        "eng_main": rounded(mean([chapter_score, overall_score])),
    }


def _item_summary(values: list[float | None]) -> dict[str, Any]:
    summary = summarise(values)
    return {
        "n": summary["n"],
        "mean_sd": mean_sd_text(values),
        "min": two_decimals(summary["min"]),
        "max": two_decimals(summary["max"]),
    }


def eng_per_chapter_tables(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return per-chapter engagement item summaries."""
    rows: list[dict[str, Any]] = []

    for item in ENG_CHAPTER_ITEMS:
        chapter_summaries = {
            chapter: _item_summary([_chapter_value(participant, chapter, item["index"]) for participant in participants])
            for chapter in (1, 2, 3)
        }
        chapter_ns = [chapter_summaries[chapter]["n"] for chapter in (1, 2, 3)]
        n_display = chapter_ns[0] if len(set(chapter_ns)) == 1 else f"varies: Ch1={chapter_ns[0]}, Ch2={chapter_ns[1]}, Ch3={chapter_ns[2]}"

        row: dict[str, Any] = {
            "item": item["statement"],
            "n": n_display,
            "chapter_n_values": chapter_ns,
        }

        for chapter in (1, 2, 3):
            prefix = f"ch{chapter}"
            row[f"{prefix}_mean_sd"] = chapter_summaries[chapter]["mean_sd"]
            row[f"{prefix}_min"] = chapter_summaries[chapter]["min"]
            row[f"{prefix}_max"] = chapter_summaries[chapter]["max"]

        rows.append(row)

    return [{
        "title": "Per-chapter engagement",
        "description": "Per-chapter items, 1 = strongly disagree, 7 = strongly agree.",
        "rows": rows,
    }]


def eng_overall_tables(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return overall-game engagement item summaries."""
    rows: list[dict[str, Any]] = []

    for item in ENG_OVERALL_ITEMS:
        values = [_overall_value(participant, item["index"], reverse=item["reverse"]) for participant in participants]
        summary = _item_summary(values)
        statement = item["statement"] + (" (reverse-coded)" if item["reverse"] else "")

        rows.append({
            "item": statement,
            "n": summary["n"],
            "mean_sd": summary["mean_sd"],
            "min": summary["min"],
            "max": summary["max"],
        })

    return [{
        "title": "Overall engagement",
        "description": "Overall-game items, 1 = strongly disagree, 7 = strongly agree.",
        "rows": rows,
    }]


def eng_condition_summary(participants: list[dict[str, Any]], condition_order: list[str]) -> list[dict[str, Any]]:
    """Summarise preregistered merged engagement by condition and overall."""
    rows: list[dict[str, Any]] = []

    for condition in condition_order + ["Overall"]:
        scoped = participants if condition == "Overall" else [p for p in participants if p["condition"] == condition]
        values = [p.get("eng_main") for p in scoped]
        summary = summarise(values)

        rows.append({
            "condition": condition,
            "n": len(scoped),
            "eng_main_mean_sd": mean_sd_text(values),
            "eng_main_min": summary["min"],
            "eng_main_max": summary["max"],
        })

    return rows


def eng_quality_flags(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Flag engagement responses outside the valid range, unused options, or unequal chapter n."""
    columns = [
        *[f"eng_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 6)],
        *[f"eng_overall_scores_{index}" for index in range(1, 5)],
    ]

    flags: list[dict[str, Any]] = []
    observed_valid_scores: set[int] = set()
    out_of_range: list[str] = []

    for participant in participants:
        participant_id = participant.get("participant_id", "")
        values = participant_scale_values(participant)

        for column in columns:
            raw = values.get(column)
            parsed = parse_numeric(raw)
            if parsed is None:
                continue

            if parsed < ENG_SCALE_MIN or parsed > ENG_SCALE_MAX:
                out_of_range.append(f"{participant_id}: {column}={raw}")
                continue

            if float(parsed).is_integer():
                observed_valid_scores.add(int(parsed))

    if out_of_range:
        flags.append({
            "scale": "Engagement",
            "flag": "Out-of-range value",
            "details": "; ".join(out_of_range),
        })

    missing_valid_scores = [str(value) for value in range(ENG_SCALE_MIN, ENG_SCALE_MAX + 1) if value not in observed_valid_scores]
    if missing_valid_scores:
        flags.append({
            "scale": "Engagement",
            "flag": "Valid response option not observed",
            "details": ", ".join(missing_valid_scores),
        })

    flags.extend(eng_per_chapter_n_flags(participants))

    if not flags:
        flags.append({
            "scale": "Engagement",
            "flag": "OK",
            "details": "All observed responses were within the valid scale range, every response option was observed at least once, and chapter-level n values matched.",
        })

    return flags


def eng_per_chapter_n_flags(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Flag per-chapter engagement items whose valid n differs across chapters."""
    flags: list[dict[str, Any]] = []

    for item in ENG_CHAPTER_ITEMS:
        chapter_ns = [
            summarise([_chapter_value(participant, chapter, item["index"]) for participant in participants])["n"]
            for chapter in (1, 2, 3)
        ]
        if len(set(chapter_ns)) > 1:
            flags.append({
                "scale": "Engagement",
                "flag": "Per-chapter n mismatch",
                "details": f"{item['statement']} | Ch1={chapter_ns[0]}, Ch2={chapter_ns[1]}, Ch3={chapter_ns[2]}",
            })

    return flags