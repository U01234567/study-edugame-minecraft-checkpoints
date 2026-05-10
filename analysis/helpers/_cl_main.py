from __future__ import annotations

from typing import Any

from ._shared import mean, mean_sd_text, parse_numeric, rounded, summarise, two_decimals

CHAPTER_NAMES = {
    1: "Chapter 1 (The Museum)",
    2: "Chapter 2 (The Farm)",
    3: "Chapter 3 (The Jungle)",
}

CL_CHAPTER_ITEMS = [
    {"index": 1, "scale": "Intrinsic cognitive load", "statement": "The appearances of the creatures covered in this chapter were complex."},
    {"index": 2, "scale": "Intrinsic cognitive load", "statement": "The facts about the creatures covered in this chapter were complex."},
    {"index": 3, "scale": "Intrinsic cognitive load", "statement": "The creatures covered in this chapter were difficult to tell apart."},
    {"index": 4, "scale": "Environment-related extraneous cognitive load", "statement": "The elements in this chapter's game world made the learning unclear."},
    {"index": 5, "scale": "Environment-related extraneous cognitive load", "statement": "This chapter's game world was, in terms of learning, ineffective."},
    {"index": 6, "scale": "Environment-related extraneous cognitive load", "statement": "This chapter's game world was full of irrelevant content."},
    {"index": 7, "scale": "Environment-related extraneous cognitive load", "statement": "It was difficult to find the relevant learning information in this chapter's game world."},
]

CL_OVERALL_SCALES = [
    {
        "scale": "Instruction-related extraneous cognitive load",
        "items": [
            {"index": 1, "statement": "The instructions about what I had to do in the game were clear.", "reverse": True},
            {"index": 2, "statement": "The explanation of how the chapters and checkpoints worked was clear.", "reverse": True},
            {"index": 3, "statement": "The instructions about finding and learning about creatures were, in terms of learning, effective.", "reverse": True},
        ],
    },
    {
        "scale": "Interaction-related extraneous cognitive load",
        "items": [
            {"index": 4, "statement": "The way I had to interact with the game was clear.", "reverse": True},
            {"index": 5, "statement": "The way I had to interact with the game was, in terms of learning about the creatures, effective.", "reverse": True},
            {"index": 6, "statement": "The way I had to interact with the game made it easier to learn about the creatures.", "reverse": True},
            {"index": 7, "statement": "The way I had to interact with the game was easy to master.", "reverse": True},
        ],
    },
    {
        "scale": "Germane cognitive load",
        "items": [
            {"index": 8, "statement": "The game really enhanced my understanding of the creatures covered.", "reverse": False},
            {"index": 9, "statement": "The game helped me connect the creatures' appearances with their facts.", "reverse": False},
            {"index": 10, "statement": "The game helped me organise the creature information in my mind.", "reverse": False},
            {"index": 11, "statement": "The game helped me keep the different creatures apart.", "reverse": False},
        ],
    },
]

CL_SCALE_MIN = 0
CL_SCALE_MAX = 10


def valid_score(value: object, *, reverse: bool = False) -> float | None:
    """Return a valid cognitive-load score, reverse-coded when required."""
    parsed = parse_numeric(value)
    if parsed is None or parsed < CL_SCALE_MIN or parsed > CL_SCALE_MAX:
        return None

    if reverse:
        return CL_SCALE_MIN + CL_SCALE_MAX - parsed

    return parsed


def participant_scale_values(participant: dict[str, Any]) -> dict[str, str]:
    """Return the scale-value dictionary stored on a participant row."""
    return participant.get("scale_values", {}) or {}


def _chapter_value(participant: dict[str, Any], chapter: int, item_index: int) -> float | None:
    return valid_score(participant_scale_values(participant).get(f"cl_ch{chapter}_scores_{item_index}"))


def _overall_value(participant: dict[str, Any], item_index: int, *, reverse: bool = False) -> float | None:
    return valid_score(participant_scale_values(participant).get(f"cl_overall_scores_{item_index}"), reverse=reverse)


def cl_participant_scores(row: dict[str, Any]) -> dict[str, float | None]:
    """Compute preregistered participant-level cognitive-load scores from the immediate survey wave."""
    intrinsic = mean(
        valid_score(row.get(f"cl_ch{chapter}_scores_{index}"))
        for chapter in (1, 2, 3)
        for index in (1, 2, 3)
    )

    environment_extraneous = mean(
        valid_score(row.get(f"cl_ch{chapter}_scores_{index}"))
        for chapter in (1, 2, 3)
        for index in (4, 5, 6, 7)
    )

    instruction_extraneous = mean(
        valid_score(row.get(f"cl_overall_scores_{index}"), reverse=True)
        for index in (1, 2, 3)
    )

    interaction_extraneous = mean(
        valid_score(row.get(f"cl_overall_scores_{index}"), reverse=True)
        for index in (4, 5, 6, 7)
    )

    extraneous = mean([environment_extraneous, instruction_extraneous, interaction_extraneous])

    germane = mean(
        valid_score(row.get(f"cl_overall_scores_{index}"))
        for index in (8, 9, 10, 11)
    )

    return {
        "cl_intrinsic": rounded(intrinsic),
        "cl_environment_extraneous": rounded(environment_extraneous),
        "cl_instruction_extraneous": rounded(instruction_extraneous),
        "cl_interaction_extraneous": rounded(interaction_extraneous),
        "cl_extraneous": rounded(extraneous),
        "cl_germane": rounded(germane),
    }


def _item_summary(values: list[float | None]) -> dict[str, Any]:
    summary = summarise(values)
    return {
        "n": summary["n"],
        "mean_sd": mean_sd_text(values),
        "min": two_decimals(summary["min"]),
        "max": two_decimals(summary["max"]),
    }


def cl_per_chapter_tables(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return per-chapter CL item summaries grouped by scale."""
    tables: list[dict[str, Any]] = []

    for scale_name in ("Intrinsic cognitive load", "Environment-related extraneous cognitive load"):
        rows: list[dict[str, Any]] = []
        for item in [item for item in CL_CHAPTER_ITEMS if item["scale"] == scale_name]:
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

        tables.append({
            "title": scale_name,
            "description": "Per-chapter items, 0 = not at all the case, 10 = completely the case.",
            "rows": rows,
        })

    return tables


def cl_overall_tables(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return overall-game CL item summaries grouped by scale."""
    tables: list[dict[str, Any]] = []

    for block in CL_OVERALL_SCALES:
        rows: list[dict[str, Any]] = []
        for item in block["items"]:
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

        tables.append({
            "title": block["scale"],
            "description": "Overall-game items, 0 = not at all the case, 10 = completely the case.",
            "rows": rows,
        })

    return tables


def cl_condition_summary(participants: list[dict[str, Any]], condition_order: list[str]) -> list[dict[str, Any]]:
    """Summarise preregistered merged CL constructs by condition and overall."""
    metrics = [
        ("cl_intrinsic", "Intrinsic cognitive load"),
        ("cl_extraneous", "Extraneous cognitive load"),
        ("cl_germane", "Germane cognitive load"),
    ]
    rows: list[dict[str, Any]] = []

    for condition in condition_order + ["Overall"]:
        scoped = participants if condition == "Overall" else [p for p in participants if p["condition"] == condition]
        row = {"condition": condition, "n": len(scoped)}

        for metric, _label in metrics:
            values = [p.get(metric) for p in scoped]
            summary = summarise(values)
            row[f"{metric}_mean_sd"] = mean_sd_text(values)
            row[f"{metric}_min"] = summary["min"]
            row[f"{metric}_max"] = summary["max"]

        rows.append(row)

    return rows


def cl_quality_flags(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Flag cognitive-load responses outside the valid range, unused options, or unequal chapter n."""
    columns = [
        *[f"cl_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 8)],
        *[f"cl_overall_scores_{index}" for index in range(1, 12)],
    ]

    flags = _quality_flags(
        participants=participants,
        title="Cognitive load",
        columns=columns,
        minimum=CL_SCALE_MIN,
        maximum=CL_SCALE_MAX,
    )

    n_mismatch_flags = cl_per_chapter_n_flags(participants)
    if flags and flags[0]["flag"] == "OK" and n_mismatch_flags:
        flags = []
    flags.extend(n_mismatch_flags)

    if not flags:
        flags.append({
            "scale": "Cognitive load",
            "flag": "OK",
            "details": "All observed responses were within the valid scale range, every response option was observed at least once, and chapter-level n values matched.",
        })

    return flags


def cl_per_chapter_n_flags(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Flag per-chapter CL items whose valid n differs across chapters."""
    flags: list[dict[str, Any]] = []

    for item in CL_CHAPTER_ITEMS:
        chapter_ns = [
            summarise([_chapter_value(participant, chapter, item["index"]) for participant in participants])["n"]
            for chapter in (1, 2, 3)
        ]
        if len(set(chapter_ns)) > 1:
            flags.append({
                "scale": item["scale"],
                "flag": "Per-chapter n mismatch",
                "details": f"{item['statement']} | Ch1={chapter_ns[0]}, Ch2={chapter_ns[1]}, Ch3={chapter_ns[2]}",
            })

    return flags


def _quality_flags(
    *,
    participants: list[dict[str, Any]],
    title: str,
    columns: list[str],
    minimum: int,
    maximum: int,
) -> list[dict[str, Any]]:
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

            if parsed < minimum or parsed > maximum:
                out_of_range.append(f"{participant_id}: {column}={raw}")
                continue

            if float(parsed).is_integer():
                observed_valid_scores.add(int(parsed))

    flags: list[dict[str, Any]] = []

    if out_of_range:
        flags.append({
            "scale": title,
            "flag": "Out-of-range value",
            "details": "; ".join(out_of_range),
        })

    missing_valid_scores = [str(value) for value in range(minimum, maximum + 1) if value not in observed_valid_scores]
    if missing_valid_scores:
        flags.append({
            "scale": title,
            "flag": "Valid response option not observed",
            "details": ", ".join(missing_valid_scores),
        })

    if not flags:
        flags.append({
            "scale": title,
            "flag": "OK",
            "details": "All observed responses were within the valid scale range and every response option was observed at least once.",
        })

    return flags