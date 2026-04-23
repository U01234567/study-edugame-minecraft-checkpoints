from __future__ import annotations

import csv
import html
import json
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = REPO_ROOT / "data"
OUTPUT_DIR = REPO_ROOT / "output"
OUTPUT_PATH = OUTPUT_DIR / "survey_summary.html"

CREATURES: list[tuple[str, str]] = [
    ("abyss_deer", "Abyss deer"),
    ("amethyst_scarab", "Amethyst scarab"),
    ("axolotl_dragon", "Axolotl dragon"),
    ("cave_dweller", "Cave dweller"),
    ("ender_ape", "Ender ape"),
    ("flying_bunny", "Flying bunny"),
    ("glare", "Glare"),
    ("grand_grassling_father", "Grand grassling father"),
    ("ice_golem", "Ice golem"),
    ("killer_crab", "Killer crab"),
    ("lizard_knight", "Lizard knight"),
    ("mushroom_bup", "Mushroom bup"),
    ("orc", "Orc"),
    ("prototype_warden", "Prototype warden"),
    ("retro_tv_robot", "Retro TV robot"),
    ("scrambler_king", "Scrambler king"),
    ("walking_robot_guy", "Walking robot guy"),
    ("wardigo", "Wardigo"),
]
CREATURE_NAME_BY_ID = {creature_id: name for creature_id, name in CREATURES}
IGNORED_SEEN_EXTRAS = {"cow", "chicken", "pig"}
MAX_RETENTION_SLOTS = 18

RETENTION_QUESTION_SPECS = [
    ("img1", "IMAGE + What is the name of this creature?"),
    ("img2", "IMAGE + What are unique facts about this creature?"),
    ("name1", "NAME + What does this creature look like?"),
    ("name2", "NAME + In which environment does this creature live, and how would you describe its surroundings?"),
]

SCALES = [
    {
        "id": "cl_ch1",
        "title": "Cognitive load | Chapter 1 (The Museum)",
        "section_title": "Experience per chapter (1/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 0 (= not at all the case) to 10 (= completely the case).",
        "interpretation": "Higher scores indicate less optimal cognitive load.",
        "min_value": 0,
        "max_value": 10,
        "items": [
            {"column": "cl_ch1_scores_1", "statement": "The topics covered in this chapter were very complex.", "reverse": False},
            {"column": "cl_ch1_scores_2", "statement": "This chapter covered creature characteristics that seemed very complex.", "reverse": False},
            {"column": "cl_ch1_scores_3", "statement": "This chapter covered differences between the creatures that seemed very complex.", "reverse": False},
            {"column": "cl_ch1_scores_4", "statement": "The elements in this chapter's game world made the learning very unclear.", "reverse": False},
            {"column": "cl_ch1_scores_5", "statement": "This chapter's game world was, in terms of learning, very ineffective.", "reverse": False},
            {"column": "cl_ch1_scores_6", "statement": "This chapter's game world was full of irrelevant content.", "reverse": False},
            {"column": "cl_ch1_scores_7", "statement": "It was difficult to find the relevant learning information in this chapter's game world.", "reverse": False},
        ],
    },
    {
        "id": "cl_ch2",
        "title": "Cognitive load | Chapter 2 (The Farm)",
        "section_title": "Experience per chapter (1/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 0 (= not at all the case) to 10 (= completely the case).",
        "interpretation": "Higher scores indicate less optimal cognitive load.",
        "min_value": 0,
        "max_value": 10,
        "items": [
            {"column": "cl_ch2_scores_1", "statement": "The topics covered in this chapter were very complex.", "reverse": False},
            {"column": "cl_ch2_scores_2", "statement": "This chapter covered creature characteristics that seemed very complex.", "reverse": False},
            {"column": "cl_ch2_scores_3", "statement": "This chapter covered differences between the creatures that seemed very complex.", "reverse": False},
            {"column": "cl_ch2_scores_4", "statement": "The elements in this chapter's game world made the learning very unclear.", "reverse": False},
            {"column": "cl_ch2_scores_5", "statement": "This chapter's game world was, in terms of learning, very ineffective.", "reverse": False},
            {"column": "cl_ch2_scores_6", "statement": "This chapter's game world was full of irrelevant content.", "reverse": False},
            {"column": "cl_ch2_scores_7", "statement": "It was difficult to find the relevant learning information in this chapter's game world.", "reverse": False},
        ],
    },
    {
        "id": "cl_ch3",
        "title": "Cognitive load | Chapter 3 (The Jungle)",
        "section_title": "Experience per chapter (1/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 0 (= not at all the case) to 10 (= completely the case).",
        "interpretation": "Higher scores indicate less optimal cognitive load.",
        "min_value": 0,
        "max_value": 10,
        "items": [
            {"column": "cl_ch3_scores_1", "statement": "The topics covered in this chapter were very complex.", "reverse": False},
            {"column": "cl_ch3_scores_2", "statement": "This chapter covered creature characteristics that seemed very complex.", "reverse": False},
            {"column": "cl_ch3_scores_3", "statement": "This chapter covered differences between the creatures that seemed very complex.", "reverse": False},
            {"column": "cl_ch3_scores_4", "statement": "The elements in this chapter's game world made the learning very unclear.", "reverse": False},
            {"column": "cl_ch3_scores_5", "statement": "This chapter's game world was, in terms of learning, very ineffective.", "reverse": False},
            {"column": "cl_ch3_scores_6", "statement": "This chapter's game world was full of irrelevant content.", "reverse": False},
            {"column": "cl_ch3_scores_7", "statement": "It was difficult to find the relevant learning information in this chapter's game world.", "reverse": False},
        ],
    },
    {
        "id": "eng_ch1",
        "title": "Engagement | Chapter 1 (The Museum)",
        "section_title": "Experience per chapter (2/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 1 (= strongly disagree) to 7 (= strongly agree).",
        "interpretation": "Higher scores indicate higher engagement.",
        "min_value": 1,
        "max_value": 7,
        "items": [
            {"column": "eng_ch1_scores_1", "statement": "I lost myself in this chapter.", "reverse": False},
            {"column": "eng_ch1_scores_2", "statement": "The time I spent in this chapter just slipped away.", "reverse": False},
            {"column": "eng_ch1_scores_3", "statement": "I was fully focused on this chapter.", "reverse": False},
            {"column": "eng_ch1_scores_4", "statement": "This chapter looked good.", "reverse": False},
            {"column": "eng_ch1_scores_5", "statement": "I felt interested in this chapter.", "reverse": False},
        ],
    },
    {
        "id": "eng_ch2",
        "title": "Engagement | Chapter 2 (The Farm)",
        "section_title": "Experience per chapter (2/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 1 (= strongly disagree) to 7 (= strongly agree).",
        "interpretation": "Higher scores indicate higher engagement.",
        "min_value": 1,
        "max_value": 7,
        "items": [
            {"column": "eng_ch2_scores_1", "statement": "I lost myself in this chapter.", "reverse": False},
            {"column": "eng_ch2_scores_2", "statement": "The time I spent in this chapter just slipped away.", "reverse": False},
            {"column": "eng_ch2_scores_3", "statement": "I was fully focused on this chapter.", "reverse": False},
            {"column": "eng_ch2_scores_4", "statement": "This chapter looked good.", "reverse": False},
            {"column": "eng_ch2_scores_5", "statement": "I felt interested in this chapter.", "reverse": False},
        ],
    },
    {
        "id": "eng_ch3",
        "title": "Engagement | Chapter 3 (The Jungle)",
        "section_title": "Experience per chapter (2/2)",
        "description": "Please answer the following questions for each chapter separately on a scale from 1 (= strongly disagree) to 7 (= strongly agree).",
        "interpretation": "Higher scores indicate higher engagement.",
        "min_value": 1,
        "max_value": 7,
        "items": [
            {"column": "eng_ch3_scores_1", "statement": "I lost myself in this chapter.", "reverse": False},
            {"column": "eng_ch3_scores_2", "statement": "The time I spent in this chapter just slipped away.", "reverse": False},
            {"column": "eng_ch3_scores_3", "statement": "I was fully focused on this chapter.", "reverse": False},
            {"column": "eng_ch3_scores_4", "statement": "This chapter looked good.", "reverse": False},
            {"column": "eng_ch3_scores_5", "statement": "I felt interested in this chapter.", "reverse": False},
        ],
    },
    {
        "id": "cl_overall",
        "title": "Cognitive load | Overall",
        "section_title": "Overall experience (1/2)",
        "description": "Please answer the following questions about the game overall on a scale from 0 (= not at all the case) to 10 (= completely the case).",
        "interpretation": "Higher scores indicate less optimal cognitive load.",
        "min_value": 0,
        "max_value": 10,
        "items": [
            {"column": "cl_overall_scores_1", "statement": "The instructions and/or explanations used in the game were very unclear.", "reverse": False},
            {"column": "cl_overall_scores_2", "statement": "The instructions and/or explanations used in the game were, in terms of learning, very ineffective.", "reverse": False},
            {"column": "cl_overall_scores_3", "statement": "The instructions and/or explanations used in the game were full of unclear content.", "reverse": False},
            {"column": "cl_overall_scores_4", "statement": "The way of interacting with the game was very unclear.", "reverse": False},
            {"column": "cl_overall_scores_5", "statement": "The way of interacting with the game was, in terms of learning, very ineffective.", "reverse": False},
            {"column": "cl_overall_scores_6", "statement": "The way of interacting with the game made it harder to learn.", "reverse": False},
            {"column": "cl_overall_scores_7", "statement": "The way of interacting with the game was difficult to master.", "reverse": False},
        ],
    },
    {
        "id": "eng_overall",
        "title": "Engagement | Overall",
        "section_title": "Overall experience (2/2)",
        "description": "Please answer the following questions about the game overall on a scale from 1 (= strongly disagree) to 7 (= strongly agree).",
        "interpretation": "Higher scores indicate higher engagement.",
        "min_value": 1,
        "max_value": 7,
        "items": [
            {"column": "eng_overall_scores_1", "statement": "I felt frustrated while playing this game.", "reverse": True},
            {"column": "eng_overall_scores_2", "statement": "I found this game confusing to play.", "reverse": True},
            {"column": "eng_overall_scores_3", "statement": "Playing this game was worth my time.", "reverse": False},
            {"column": "eng_overall_scores_4", "statement": "My experience was rewarding.", "reverse": False},
        ],
    },
    {
        "id": "perceived_control",
        "title": "Perceived control at checkpoints",
        "section_title": "Experience at checkpoints",
        "description": "Please answer the following questions on a scale from 1 (= strongly disagree) to 7 (= strongly agree).",
        "interpretation": "Higher scores indicate higher perceived control.",
        "min_value": 1,
        "max_value": 7,
        "items": [
            {"column": "ctrl_scores_1", "statement": "At the checkpoints, I was free to decide how I wanted to proceed.", "reverse": False},
            {"column": "ctrl_scores_2", "statement": "The choices I made at the checkpoints influenced what happened next.", "reverse": False},
        ],
    },
]


def detect_text_encoding(path: Path) -> str:
    with path.open("rb") as handle:
        start = handle.read(4)

    if start.startswith((b"\xff\xfe", b"\xfe\xff")):
        return "utf-16"
    if start.startswith(b"\xef\xbb\xbf"):
        return "utf-8-sig"
    return "utf-8"


def newest_tsv_file(data_dir: Path) -> Path | None:
    if not data_dir.exists() or not data_dir.is_dir():
        return None

    tsv_files = [path for path in data_dir.iterdir() if path.is_file() and path.suffix.lower() == ".tsv"]
    if not tsv_files:
        return None

    return max(tsv_files, key=lambda path: path.stat().st_mtime)


def clean(value: object) -> str:
    if value is None:
        return ""
    return str(value).strip()


def escape(value: object) -> str:
    return html.escape("" if value is None else str(value), quote=True)


def parse_numeric(value: str) -> float | int | None:
    text = clean(value)
    if not text:
        return None

    try:
        number = float(text)
    except ValueError:
        return None

    if number.is_integer():
        return int(number)
    return number


def parse_seen_details(raw_seen: str) -> tuple[list[str], list[str]]:
    seen_ids: list[str] = []
    invalid_ids: list[str] = []
    seen_id_set: set[str] = set()

    raw_value = clean(raw_seen)
    if not raw_value or raw_value.upper() == "ERROR":
        return seen_ids, invalid_ids

    for part in raw_value.replace("|", ",").split(","):
        item = clean(part)
        if not item:
            continue
        if item in CREATURE_NAME_BY_ID:
            if item not in seen_id_set:
                seen_id_set.add(item)
                seen_ids.append(item)
        else:
            invalid_ids.append(item)

    return seen_ids, invalid_ids


def is_truthy_seen(value: str) -> bool:
    text = clean(value).lower()
    if not text:
        return False
    return text in {"1", "true", "yes", "y", "x"}


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


def normalize_headers(raw_rows: list[list[str]]) -> tuple[list[str], list[str]]:
    if len(raw_rows) < 2:
        raise ValueError("Expected at least two rows for header parsing.")

    raw_header = raw_rows[0]
    labels = raw_rows[1]
    import_row = raw_rows[2] if len(raw_rows) > 2 else []

    normalized: list[str] = []

    for index, raw_name in enumerate(raw_header):
        name = clean(raw_name)
        label = clean(labels[index]) if index < len(labels) else ""
        import_id = extract_import_id(import_row[index]) if index < len(import_row) else None

        if import_id:
            if name and not name.startswith("QID"):
                if import_id.startswith("QID"):
                    normalized.append(name)
                else:
                    normalized.append(import_id)
                continue

        if name.startswith("QID") and " - " in label:
            custom_prefix = clean(label.split(" - ", 1)[0])
            if custom_prefix:
                normalized.append(custom_prefix)
                continue

        normalized.append(name)

    return normalized, labels


def load_rows(tsv_path: Path) -> tuple[list[dict[str, str]], list[str], list[str]]:
    encoding = detect_text_encoding(tsv_path)

    with tsv_path.open("r", encoding=encoding, newline="") as handle:
        reader = csv.reader(handle, delimiter="\t")
        raw_rows = list(reader)

    if len(raw_rows) < 2:
        raise ValueError(
            f"Expected at least a header row and label row in {tsv_path}, but found only {len(raw_rows)} row(s)."
        )

    header, labels = normalize_headers(raw_rows)
    rows: list[dict[str, str]] = []

    start_index = 2
    if len(raw_rows) > 2:
        import_id_cells = sum(1 for cell in raw_rows[2] if '"ImportId"' in clean(cell))
        if import_id_cells >= max(3, len(header) // 3):
            start_index = 3

    for raw_row in raw_rows[start_index:]:
        if not any(clean(cell) for cell in raw_row):
            continue

        padded = raw_row + [""] * (len(header) - len(raw_row))
        row = {header[index]: padded[index] for index in range(len(header))}
        rows.append(row)

    return rows, header, labels


def retention_column_name(slot_index: int, suffix: str) -> str:
    return f"ret_slot{slot_index:02d}_{suffix}"


def build_expected_retention_columns() -> list[str]:
    expected: list[str] = []
    for slot in range(1, MAX_RETENTION_SLOTS + 1):
        for suffix, _label in RETENTION_QUESTION_SPECS:
            expected.append(retention_column_name(slot, suffix))
    return expected


def build_retention_check(header: list[str]) -> dict[str, object]:
    expected = build_expected_retention_columns()
    present = [column for column in expected if column in header]
    missing = [column for column in expected if column not in header]
    unexpected = sorted(
        [column for column in header if column.startswith("ret_slot") and column not in expected]
    )

    return {
        "expected_count": len(expected),
        "present_count": len(present),
        "missing_count": len(missing),
        "unexpected_count": len(unexpected),
        "missing": missing,
        "unexpected": unexpected,
        "ok": not missing and not unexpected,
    }


def build_participants(
    rows: list[dict[str, str]],
) -> tuple[list[dict[str, object]], int, dict[str, dict[str, list[dict[str, str]]]]]:
    participants: list[dict[str, object]] = []
    delayed_count = 0
    qualitative_by_creature: dict[str, dict[str, list[dict[str, str]]]] = {
        creature_id: {suffix: [] for suffix, _label in RETENTION_QUESTION_SPECS}
        for creature_id, _name in CREATURES
    }

    for row in rows:
        delayed_value = clean(row.get("DELAYED"))
        is_delayed = delayed_value == "1"
        if is_delayed:
            delayed_count += 1

        mcid = clean(row.get("MCID")) or clean(row.get("_recordId")) or f"row-{len(participants) + 1}"
        response_id = clean(row.get("ResponseId")) or clean(row.get("_recordId")) or f"row-{len(participants) + 1}"
        start_date = clean(row.get("startDate"))

        seen_from_columns: dict[str, bool] = {}
        seen_columns_ids: list[str] = []
        for creature_id, _ in CREATURES:
            seen_value = is_truthy_seen(row.get(f"seen_{creature_id}", ""))
            seen_from_columns[creature_id] = seen_value
            if seen_value:
                seen_columns_ids.append(creature_id)

        seen_embedded_ids_raw, seen_embedded_invalid_raw = parse_seen_details(row.get("SEEN", ""))
        seen_embedded_ids = [item for item in seen_embedded_ids_raw if item not in IGNORED_SEEN_EXTRAS]
        seen_embedded_invalid = [item for item in seen_embedded_invalid_raw if item not in IGNORED_SEEN_EXTRAS]

        seen_matches_embedded = (
            set(seen_columns_ids) == set(seen_embedded_ids) and not seen_embedded_invalid
        )

        scale_values: dict[str, dict[str, float | int | None]] = {}
        for scale in SCALES:
            current: dict[str, float | int | None] = {}
            for item in scale["items"]:
                current[item["column"]] = parse_numeric(row.get(item["column"], ""))
            scale_values[scale["id"]] = current

        retention_answers: dict[str, dict[str, str]] = {}
        seen_slots = seen_embedded_ids[:MAX_RETENTION_SLOTS]
        for slot_index, creature_id in enumerate(seen_slots, start=1):
            answers_for_creature: dict[str, str] = {}
            for suffix, _label in RETENTION_QUESTION_SPECS:
                column_name = retention_column_name(slot_index, suffix)
                answer = clean(row.get(column_name, ""))
                answers_for_creature[suffix] = answer
                if answer:
                    qualitative_by_creature[creature_id][suffix].append(
                        {
                            "response_id": response_id,
                            "mcid": mcid,
                            "answer": answer,
                            "startDate": start_date,
                            "delayed": is_delayed,
                        }
                    )
            retention_answers[creature_id] = answers_for_creature

        participants.append(
            {
                "response_id": response_id,
                "mcid": mcid,
                "startDate": start_date,
                "endDate": clean(row.get("endDate")),
                "progress": clean(row.get("progress")),
                "saw_debriefing": clean(row.get("SAW_DEBRIEFING")),
                "delayed_link": clean(row.get("DELAYED_LINK")),
                "delayed": is_delayed,
                "seen": seen_from_columns,
                "seen_columns_ids": seen_columns_ids,
                "seen_embedded_ids": seen_embedded_ids,
                "seen_embedded_invalid": seen_embedded_invalid,
                "seen_matches_embedded": seen_matches_embedded,
                "scales": scale_values,
                "retention_answers": retention_answers,
            }
        )

    for _creature_id, question_map in qualitative_by_creature.items():
        for _suffix, rows_for_question in question_map.items():
            rows_for_question.sort(key=lambda item: item["startDate"], reverse=True)

    return participants, delayed_count, qualitative_by_creature


def metric_card(label: str, value: str, sub: str = "") -> str:
    sub_html = f'<div class="sub">{escape(sub)}</div>' if sub else ""
    return (
        '<div class="metric">'
        f'<div class="label">{escape(label)}</div>'
        f'<div class="value">{escape(value)}</div>'
        f"{sub_html}"
        "</div>"
    )


def render_participant_row(participant: dict[str, object]) -> str:
    mcid = str(participant["mcid"])
    response_id = str(participant["response_id"])
    saw_debriefing = "true" if clean(participant.get("saw_debriefing")) == "1" else ""
    delayed_display = "true" if participant.get("delayed") else ""
    delayed_link = clean(participant.get("delayed_link"))
    delayed_link_html = (
        f'<a href="{escape(delayed_link)}" target="_blank">Link</a>'
        if delayed_link and not participant.get("delayed")
        else ""
    )

    return (
        "<tr>"
        f"<td><input type='checkbox' class='participant-checkbox' data-response-id='{escape(response_id)}' checked></td>"
        f"<td>{escape(mcid)}</td>"
        f"<td>{escape(participant['startDate'])}</td>"
        f"<td>{escape(participant['endDate'])}</td>"
        f"<td class='num'>{escape(participant['progress'])}</td>"
        f"<td>{escape(saw_debriefing)}</td>"
        f"<td>{delayed_link_html}</td>"
        f"<td>{escape(delayed_display)}</td>"
        "</tr>"
    )


def render_html(
    *,
    source_path: Path,
    participants: list[dict[str, object]],
    delayed_count: int,
    available_rows: int,
    retention_check: dict[str, object],
    qualitative_by_creature: dict[str, dict[str, list[dict[str, str]]]],
) -> str:
    participants_sorted = sorted(
        participants,
        key=lambda participant: clean(participant.get("startDate")),
        reverse=True,
    )
    participant_rows = "\n".join(render_participant_row(participant) for participant in participants_sorted)
    non_delayed_count = sum(1 for participant in participants_sorted if not participant.get("delayed"))

    report_data = {
        "participants": participants_sorted,
        "creatures": [{"id": creature_id, "name": name} for creature_id, name in CREATURES],
        "scales": SCALES,
        "retentionQuestions": [{"suffix": suffix, "label": label} for suffix, label in RETENTION_QUESTION_SPECS],
        "retentionCheck": retention_check,
        "qualitativeByCreature": qualitative_by_creature,
    }
    report_json = json.dumps(report_data, ensure_ascii=False).replace("</", "<\\/")

    metrics = [
        metric_card("Rows after Qualtrics header cleanup", str(available_rows), "all non-empty response rows"),
        metric_card("Participant rows shown", str(len(participants_sorted)), "includes delayed and non-delayed rows"),
        metric_card("Rows used in scales / X table", str(non_delayed_count), 'rows where DELAYED is not "1"'),
        metric_card("Rows marked delayed", str(delayed_count), 'rows where DELAYED == "1"'),
        metric_card("Creatures tracked", str(len(CREATURES)), "from the embedded-data / delayed-link script"),
        metric_card("Scales", str(len(SCALES)), "chapter-specific and overall cognitive load / engagement, plus perceived control",),
    ]

    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Survey summary</title>
<style>
:root {{
  --bg: #f5f7f8;
  --card: #ffffff;
  --line: #d7dee3;
  --text: #142126;
  --muted: #5d6c74;
  --accent: #0f766e;
  --ok-bg: #ecfdf5;
  --ok-line: #10b981;
  --ok-text: #065f46;
  --bad-bg: #fef2f2;
  --bad-line: #ef4444;
  --bad-text: #991b1b;
}}
* {{ box-sizing: border-box; }}
body {{ margin: 0; background: var(--bg); color: var(--text); font-family: Arial, sans-serif; }}
main {{ max-width: 1800px; margin: 0 auto; padding: 18px; }}
h1, h2, h3 {{ margin: 0 0 10px 0; }}
h4 {{ margin: 14px 0 8px 0; }}
p {{ margin: 0; }}
.small {{ font-size: 12px; color: var(--muted); }}
.num {{ text-align: right; white-space: nowrap; }}
.wrap {{ white-space: normal; }}
.participant-table th,
.participant-table td,
.participant-table .num {{ text-align: left; }}
.answer-cell {{ white-space: pre-wrap; word-break: break-word; }}
.card {{ background: var(--card); border: 1px solid var(--line); border-radius: 12px; padding: 14px; margin-bottom: 14px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }}
.grid {{ display: grid; gap: 12px; }}
.metric-grid {{ grid-template-columns: repeat(6, minmax(0, 1fr)); }}
.metric {{ border: 1px solid var(--line); border-radius: 10px; padding: 12px; background: #fbfcfd; }}
.metric .label {{ font-size: 12px; color: var(--muted); text-transform: uppercase; letter-spacing: .04em; }}
.metric .value {{ font-size: 21px; font-weight: 700; margin-top: 4px; }}
.metric .sub {{ font-size: 12px; color: var(--muted); margin-top: 4px; }}
.tabs {{ display: flex; gap: 8px; margin: 10px 0 14px 0; flex-wrap: wrap; }}
.tab-btn, .subtab-btn {{ border: 1px solid var(--line); background: var(--card); border-radius: 999px; padding: 10px 14px; cursor: pointer; font-weight: 700; }}
.tab-btn.active, .subtab-btn.active {{ background: var(--accent); color: white; border-color: var(--accent); }}
.tab-panel {{ display: none; }}
.tab-panel.active {{ display: block; }}
table {{ width: 100%; border-collapse: collapse; font-size: 13px; table-layout: fixed; }}
th, td {{ border: 1px solid var(--line); padding: 7px 8px; vertical-align: top; overflow-wrap: anywhere; word-break: break-word; }}
th {{ background: #f2f5f7; text-align: left; }}
.table-wrap {{ width: 100%; overflow-x: auto; }}
.col-check {{ width: 56px; }}
.col-mcid {{ width: 140px; }}
.col-date {{ width: 180px; }}
.col-progress {{ width: 90px; }}
.col-flag {{ width: 130px; }}
.col-link {{ width: 100px; }}
.col-creature-mcid {{ width: 140px; }}
.col-creature-item {{ width: 88px; }}
.col-answer-mcid {{ width: 140px; }}
.col-answer-delayed {{ width: 110px; }}
.col-answer-text {{ width: auto; }}
.col-scale-statement {{ width: auto; }}
.col-scale-score {{ width: 110px; }}
.col-scale-n {{ width: 80px; }}
.col-scale-reverse {{ width: 120px; }}
.controls {{ display: flex; gap: 8px; margin-bottom: 12px; flex-wrap: wrap; }}
.inline-btn {{ border: 1px solid var(--line); background: var(--card); border-radius: 999px; padding: 8px 12px; cursor: pointer; font-weight: 700; }}
.section-stack > .card {{ margin-bottom: 14px; }}
.empty-state {{ padding: 16px; border: 1px dashed var(--line); border-radius: 10px; color: var(--muted); background: #fbfcfd; }}
.status-banner {{
  border-radius: 10px;
  padding: 12px 14px;
  margin-bottom: 12px;
  border: 1px solid var(--line);
}}
.status-banner.success {{
  background: var(--ok-bg);
  border-color: var(--ok-line);
  color: var(--ok-text);
}}
.status-banner.error {{
  background: var(--bad-bg);
  border-color: var(--bad-line);
  color: var(--bad-text);
}}
.status-banner .title {{
  font-weight: 700;
  margin-bottom: 4px;
}}
.status-banner .detail {{
  font-size: 12px;
  line-height: 1.45;
  margin-top: 4px;
  white-space: pre-wrap;
}}
.subtabs {{ display: flex; gap: 8px; margin: 22px 0 12px 0; flex-wrap: wrap; }}
.creature-detail-sections > section {{ margin-bottom: 18px; }}
.creature-detail-sections h3 {{ margin-top: 2px; }}
@media (max-width: 1200px) {{
  .metric-grid {{ grid-template-columns: repeat(2, minmax(0, 1fr)); }}
}}
</style>
</head>
<body>
<main>
  <div class="card">
    <h1>Survey summary</h1>
    <p class="small">Newest TSV in ./data/ rendered as an interactive HTML report.</p>
    <p class="small">Source TSV: {escape(source_path.resolve())}</p>
    <p class="small">HTML: {escape(OUTPUT_PATH.resolve())}</p>
  </div>

  <section class="grid metric-grid">
    {''.join(metrics)}
  </section>

  <div class="tabs">
    <button class="tab-btn active" data-tab="participants">Per participant</button>
    <button class="tab-btn" data-tab="creatures">Per creature</button>
    <button class="tab-btn" data-tab="scales">Per scale</button>
  </div>

  <section id="participants" class="tab-panel active">
    <div class="card">
      <h2>Per participant</h2>
      <p class="small">All rows are selected by default. The selected rows drive the other tabs.</p>
      <p class="small">Rows are ordered by startDate, newest first.</p>
      <div class="controls">
        <button type="button" class="inline-btn" id="select-all-btn">Select all</button>
        <button type="button" class="inline-btn" id="clear-all-btn">Clear all</button>
        <span class="small" id="selected-count-label"></span>
      </div>
      <div class="table-wrap">
        <table class="participant-table">
          <colgroup>
            <col class="col-check">
            <col class="col-mcid">
            <col class="col-date">
            <col class="col-date">
            <col class="col-progress">
            <col class="col-flag">
            <col class="col-link">
            <col class="col-flag">
          </colgroup>
          <tr>
            <th>Use</th>
            <th>MCID</th>
            <th>startDate</th>
            <th>endDate</th>
            <th class="num">progress</th>
            <th class="num">SAW_DEBRIEFING</th>
            <th>DELAYED_LINK</th>
            <th>DELAYED</th>
          </tr>
          {participant_rows or '<tr><td colspan="8">No participant rows found.</td></tr>'}
        </table>
      </div>
    </div>
  </section>

  <section id="creatures" class="tab-panel">
    <div class="card">
      <h2>Per creature</h2>
      <p class="small">The X-overview table uses only non-delayed rows. The qualitative answer tables include both non-delayed and delayed rows and mark delayed answers in a separate column.</p>
      <div id="creature-status-container"></div>
      <div id="retention-column-check-container"></div>
      <div id="creature-table-container"></div>
      <div id="creature-detail-tabs" class="subtabs"></div>
      <div id="creature-detail-container"></div>
    </div>
  </section>

  <section id="scales" class="tab-panel">
    <div id="scale-table-container" class="section-stack"></div>
  </section>
</main>
<script>
const REPORT_DATA = {report_json};
let activeCreatureId = REPORT_DATA.creatures.length ? REPORT_DATA.creatures[0].id : null;

function escapeHtml(value) {{
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;');
}}

function formatNumber(value) {{
  if (value === null || value === undefined || Number.isNaN(value)) return '';
  return Number(value).toFixed(2).replace(/\\.00$/, '');
}}

function adjustedValue(scale, item, rawValue) {{
  if (rawValue === null || rawValue === undefined || Number.isNaN(rawValue)) return null;
  if (!item.reverse) return Number(rawValue);
  return Number(scale.min_value) + Number(scale.max_value) - Number(rawValue);
}}

function selectedResponseIds() {{
  const selected = new Set();
  document.querySelectorAll('.participant-checkbox').forEach((checkbox) => {{
    if (checkbox.checked) selected.add(checkbox.dataset.responseId);
  }});
  return selected;
}}

function selectedParticipants() {{
  const keep = selectedResponseIds();
  return REPORT_DATA.participants.filter((participant) => keep.has(participant.response_id));
}}

function selectedNonDelayedParticipants() {{
  return selectedParticipants().filter((participant) => !participant.delayed);
}}

function updateSelectedCount() {{
  const count = selectedParticipants().length;
  document.getElementById('selected-count-label').textContent = `${{count}} participant row(s) selected`;
}}

function renderRetentionColumnCheck() {{
  const check = REPORT_DATA.retentionCheck;
  const container = document.getElementById('retention-column-check-container');

  if (check.ok) {{
    container.innerHTML = `
      <div class="status-banner success">
        <div class="title">Retention answer columns look correct.</div>
        <div class="detail">All ${{check.expected_count}} expected ret_slot&lt;nn&gt;_* columns are present in the TSV.</div>
      </div>
    `;
    return;
  }}

  const details = [];
  details.push(`Present expected columns: ${{check.present_count}} / ${{check.expected_count}}`);
  if (check.missing_count) details.push(`Missing columns (${{check.missing_count}}): ${{check.missing.join(', ')}}`);
  if (check.unexpected_count) details.push(`Unexpected ret_slot columns (${{check.unexpected_count}}): ${{check.unexpected.join(', ')}}`);

  container.innerHTML = `
    <div class="status-banner error">
      <div class="title">Retention answer column check failed.</div>
      <div class="detail">${{escapeHtml(details.join('\\n\\n'))}}</div>
    </div>
  `;
}}

function renderCreatureOverviewTable(participants) {{
  const tableContainer = document.getElementById('creature-table-container');

  if (!participants.length) {{
    tableContainer.innerHTML = '<div class="empty-state">No non-delayed participants selected for the creature overview table.</div>';
    return;
  }}

  const totalCounts = Object.fromEntries(REPORT_DATA.creatures.map((creature) => [creature.id, 0]));
  const headerCells = ['<th>MCID</th>']
    .concat(REPORT_DATA.creatures.map((creature) => `<th>${{escapeHtml(creature.name)}}</th>`))
    .join('');

  const rows = participants.map((participant) => {{
    const cells = [`<td>${{escapeHtml(participant.mcid)}}</td>`];
    for (const creature of REPORT_DATA.creatures) {{
      const seen = Boolean(participant.seen?.[creature.id]);
      if (seen) totalCounts[creature.id] += 1;
      cells.push(`<td class="num">${{seen ? 'X' : ''}}</td>`);
    }}
    return `<tr>${{cells.join('')}}</tr>`;
  }});

  const totalRow = ['<td><strong>Total</strong></td>'];
  for (const creature of REPORT_DATA.creatures) {{
    totalRow.push(`<td class="num"><strong>${{totalCounts[creature.id]}}</strong></td>`);
  }}

  const creatureColgroup = `
    <colgroup>
      <col class="col-creature-mcid">
      ${{REPORT_DATA.creatures.map(() => '<col class="col-creature-item">').join('')}}
    </colgroup>
  `;

  tableContainer.innerHTML = `
    <div class="table-wrap">
      <table>
        ${{creatureColgroup}}
        <tr>${{headerCells}}</tr>
        ${{rows.join('')}}
        <tr>${{totalRow.join('')}}</tr>
      </table>
    </div>
  `;
}}

function renderCreatureDetailTabs() {{
  const container = document.getElementById('creature-detail-tabs');

  if (!REPORT_DATA.creatures.length) {{
    container.innerHTML = '';
    return;
  }}

  if (!activeCreatureId) activeCreatureId = REPORT_DATA.creatures[0].id;

  container.innerHTML = REPORT_DATA.creatures.map((creature) => `
    <button class="subtab-btn ${{creature.id === activeCreatureId ? 'active' : ''}}" data-creature-id="${{escapeHtml(creature.id)}}">
      ${{escapeHtml(creature.name)}}
    </button>
  `).join('');

  container.querySelectorAll('.subtab-btn').forEach((button) => {{
    button.addEventListener('click', () => {{
      activeCreatureId = button.dataset.creatureId;
      renderCreatureSection();
    }});
  }});
}}

function renderCreatureDetails(allSelectedParticipants) {{
  const container = document.getElementById('creature-detail-container');

  if (!allSelectedParticipants.length) {{
    container.innerHTML = '';
    return;
  }}

  if (!activeCreatureId) activeCreatureId = REPORT_DATA.creatures[0]?.id || null;
  const creature = REPORT_DATA.creatures.find((item) => item.id === activeCreatureId) || REPORT_DATA.creatures[0];

  if (!creature) {{
    container.innerHTML = '';
    return;
  }}

  activeCreatureId = creature.id;
  const keep = selectedResponseIds();
  const qualitative = REPORT_DATA.qualitativeByCreature?.[creature.id] || {{}};

  const sections = REPORT_DATA.retentionQuestions.map((question) => {{
    const rows = (qualitative[question.suffix] || []).filter((row) => keep.has(row.response_id));

    const tableHtml = rows.length
      ? `
        <div class="table-wrap">
          <table>
            <colgroup>
              <col class="col-answer-mcid">
              <col class="col-answer-delayed">
              <col class="col-answer-text">
            </colgroup>
            <tr>
              <th>MCID</th>
              <th>DELAYED</th>
              <th>Answers</th>
            </tr>
            ${{rows.map((row) => `
              <tr>
                <td>${{escapeHtml(row.mcid)}}</td>
                <td>${{row.delayed ? 'true' : ''}}</td>
                <td class="answer-cell">${{escapeHtml(row.answer)}}</td>
              </tr>
            `).join('')}}
          </table>
        </div>
      `
      : '<div class="empty-state">No answers for the selected rows.</div>';

    return `
      <section>
        <h3>${{escapeHtml(question.label)}}</h3>
        ${{tableHtml}}
      </section>
    `;
  }}).join('');

  container.innerHTML = `
    <div class="card">
      <h2>${{escapeHtml(creature.name)}}</h2>
      <div class="creature-detail-sections">${{sections}}</div>
    </div>
  `;
}}

function renderCreatureSection() {{
  const allSelectedParticipants = selectedParticipants();
  const nonDelayedParticipants = selectedNonDelayedParticipants();
  const statusContainer = document.getElementById('creature-status-container');

  if (!allSelectedParticipants.length) {{
    statusContainer.innerHTML = '<div class="empty-state">No participant rows selected.</div>';
    document.getElementById('creature-table-container').innerHTML = '';
    document.getElementById('creature-detail-tabs').innerHTML = '';
    document.getElementById('creature-detail-container').innerHTML = '';
    renderRetentionColumnCheck();
    return;
  }}

  if (!nonDelayedParticipants.length) {{
    statusContainer.innerHTML = `
      <div class="status-banner error">
        <div class="title">No non-delayed participants selected for the SEEN overview.</div>
        <div class="detail">The X-overview table and SEEN consistency check use only rows where DELAYED is not "1". Qualitative answer tables below still include the selected delayed rows.</div>
      </div>
    `;
  }} else {{
    const mismatches = [];

    for (const participant of nonDelayedParticipants) {{
      if (!participant.seen_matches_embedded) {{
        mismatches.push({{
          mcid: participant.mcid,
          seenColumns: participant.seen_columns_ids || [],
          seenEmbedded: participant.seen_embedded_ids || [],
          invalidEmbedded: participant.seen_embedded_invalid || [],
        }});
      }}
    }}

    if (!mismatches.length) {{
      statusContainer.innerHTML = `
        <div class="status-banner success">
          <div class="title">SEEN matches seen_* for all selected non-delayed participants.</div>
          <div class="detail">${{nonDelayedParticipants.length}} non-delayed participant row(s) checked.</div>
        </div>
      `;
    }} else {{
      const details = mismatches.map((item) => {{
        const invalidPart = item.invalidEmbedded.length
          ? `; invalid SEEN ids: [${{item.invalidEmbedded.join(', ')}}]`
          : '';
        return `
          <div class="detail">
            <strong>${{escapeHtml(item.mcid)}}</strong>:
            seen_* = [${{escapeHtml(item.seenColumns.join(', '))}}];
            SEEN = [${{escapeHtml(item.seenEmbedded.join(', '))}}]${{escapeHtml(invalidPart)}}
          </div>
        `;
      }}).join('');

      statusContainer.innerHTML = `
        <div class="status-banner error">
          <div class="title">SEEN does not match seen_* for all selected non-delayed participants.</div>
          <div class="detail">${{mismatches.length}} of ${{nonDelayedParticipants.length}} non-delayed participant row(s) differ.</div>
          ${{details}}
        </div>
      `;
    }}
  }}

  renderRetentionColumnCheck();
  renderCreatureOverviewTable(nonDelayedParticipants);
  renderCreatureDetailTabs();
  renderCreatureDetails(allSelectedParticipants);
}}

function summarise(values) {{
  if (!values.length) return {{ min: null, avg: null, max: null, n: 0 }};
  const sorted = [...values].sort((a, b) => a - b);
  const avg = sorted.reduce((sum, value) => sum + value, 0) / sorted.length;
  return {{ min: sorted[0], avg, max: sorted[sorted.length - 1], n: sorted.length }};
}}

function renderSingleScaleBlock(scale, participants) {{
  const rows = [];
  const pooledAdjustedScores = [];

  for (const item of scale.items) {{
    const values = participants
      .map((participant) => adjustedValue(scale, item, participant.scales?.[scale.id]?.[item.column]))
      .filter((value) => value !== null && !Number.isNaN(value));

    pooledAdjustedScores.push(...values);
    const stats = summarise(values);

    rows.push(`
      <tr>
        <td>${{escapeHtml(item.statement)}}</td>
        <td class="num">${{formatNumber(stats.min)}}</td>
        <td class="num">${{formatNumber(stats.avg)}}</td>
        <td class="num">${{formatNumber(stats.max)}}</td>
        <td class="num">${{stats.n}}</td>
        <td>${{item.reverse ? 'true' : ''}}</td>
      </tr>
    `);
  }}

  const overviewStats = summarise(pooledAdjustedScores);

  const scaleParticipantCount = participants.filter((participant) => {{
    return scale.items.every((item) => {{
      const value = adjustedValue(scale, item, participant.scales?.[scale.id]?.[item.column]);
      return value !== null && !Number.isNaN(value);
    }});
  }}).length;

  const totalPossibleMin = scale.items.length * Number(scale.min_value);
  const totalPossibleMax = scale.items.length * Number(scale.max_value);

  return `
    <section style="margin-top: 16px;">
      <h3>${{escapeHtml(scale.title)}}</h3>
      <div class="table-wrap">
        <table>
          <colgroup>
            <col class="col-scale-statement">
            <col class="col-scale-score">
            <col class="col-scale-score">
            <col class="col-scale-score">
            <col class="col-scale-n">
            <col class="col-scale-reverse">
          </colgroup>
          <tr>
            <th>Statement</th>
            <th class="num">Min score</th>
            <th class="num">Avg score</th>
            <th class="num">Max score</th>
            <th class="num">n</th>
            <th>Reverse-coded</th>
          </tr>
          ${{rows.join('')}}
          <tr>
            <td><strong>Scale overview</strong></td>
            <td class="num"><strong>${{formatNumber(overviewStats.min)}}</strong></td>
            <td class="num"><strong>${{formatNumber(overviewStats.avg)}}</strong></td>
            <td class="num"><strong>${{formatNumber(overviewStats.max)}}</strong></td>
            <td class="num"><strong>${{scaleParticipantCount}}</strong></td>
            <td></td>
          </tr>
        </table>
      </div>
      <p class="small">Reverse-coded items are displayed after reversing, so all item summaries point in the same conceptual direction as the scale note below.</p>
      <p class="small">Interpretation: ${{escapeHtml(scale.interpretation)}}</p>
      <p class="small">Item response range: ${{scale.min_value}}–${{scale.max_value}}. If you sum complete participant totals, the theoretical total range is ${{totalPossibleMin}}–${{totalPossibleMax}}.</p>
    </section>
  `;
}}

function renderScaleTables() {{
  const participants = selectedNonDelayedParticipants();
  const container = document.getElementById('scale-table-container');

  if (!participants.length) {{
    container.innerHTML = '<div class="card"><div class="empty-state">No non-delayed participant rows selected.</div></div>';
    return;
  }}

  const sectionOrder = [
    "Experience per chapter (1/2)",
    "Experience per chapter (2/2)",
    "Overall experience (1/2)",
    "Overall experience (2/2)",
    "Experience at checkpoints",
  ];

  const grouped = new Map();

  for (const scale of REPORT_DATA.scales) {{
    if (!grouped.has(scale.section_title)) {{
      grouped.set(scale.section_title, []);
    }}
    grouped.get(scale.section_title).push(scale);
  }}

  const sections = sectionOrder
    .filter((sectionTitle) => grouped.has(sectionTitle))
    .map((sectionTitle) => {{
      const scalesInSection = grouped.get(sectionTitle);
      const description = scalesInSection[0]?.description || "";

      return `
        <div class="card">
          <h2>${{escapeHtml(sectionTitle)}}</h2>
          <p class="small">${{escapeHtml(description)}}</p>
          ${{scalesInSection.map((scale) => renderSingleScaleBlock(scale, participants)).join('')}}
        </div>
      `;
    }});

  container.innerHTML = sections.join('');
}}

function refreshDerivedTabs() {{
  updateSelectedCount();
  renderCreatureSection();
  renderScaleTables();
}}

document.querySelectorAll('.tab-btn').forEach((button) => {{
  button.addEventListener('click', () => {{
    document.querySelectorAll('.tab-btn').forEach((other) => other.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach((panel) => panel.classList.remove('active'));
    button.classList.add('active');
    document.getElementById(button.dataset.tab).classList.add('active');
  }});
}});

document.getElementById('select-all-btn').addEventListener('click', () => {{
  document.querySelectorAll('.participant-checkbox').forEach((checkbox) => {{
    checkbox.checked = true;
  }});
  refreshDerivedTabs();
}});

document.getElementById('clear-all-btn').addEventListener('click', () => {{
  document.querySelectorAll('.participant-checkbox').forEach((checkbox) => {{
    checkbox.checked = false;
  }});
  refreshDerivedTabs();
}});

document.querySelectorAll('.participant-checkbox').forEach((checkbox) => {{
  checkbox.addEventListener('change', refreshDerivedTabs);
}});

refreshDerivedTabs();
</script>
</body>
</html>"""


def main() -> int:
    if not DATA_DIR.exists():
        print(f"Expected a ./data/ directory at {DATA_DIR.resolve()}, but it was not found.")
        return 1

    tsv_path = newest_tsv_file(DATA_DIR)
    if tsv_path is None:
        print(f"Expected at least one .tsv export inside {DATA_DIR.resolve()}, but none were found.")
        return 1

    rows, header, _labels = load_rows(tsv_path)
    retention_check = build_retention_check(header)
    participants, delayed_count, qualitative_by_creature = build_participants(rows)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(
        render_html(
            source_path=tsv_path,
            participants=participants,
            delayed_count=delayed_count,
            available_rows=len(rows),
            retention_check=retention_check,
            qualitative_by_creature=qualitative_by_creature,
        ),
        encoding="utf-8",
    )

    print(f"Survey summary written to {OUTPUT_PATH.resolve()}")
    print(f"Source TSV: {tsv_path.resolve()}")
    print(f"Participant rows shown: {len(participants)}")
    print(f"Rows marked as delayed: {delayed_count}")
    print(f"Retention columns present: {retention_check['present_count']} / {retention_check['expected_count']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())