from __future__ import annotations

import csv
import json
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any

from ._shared import INTERVIEW_MANIFEST_PATH, INTERVIEW_TRANSCRIPTS_DIR, clean
from ._survey_io import detect_text_encoding

DEFAULT_INTERVIEW_CATEGORIES = {
    "low_prior_game_experience": "Low prior game experience; preferably from the required continue condition.",
    "high_prior_game_experience": "High prior game experience; preferably from the optional pauses condition.",
    "required_pause_chapter_change": "Required pause participant with a clear chapter-to-chapter change after the pause.",
    "optional_pause_chose_pause": "Optional pause participant who chose at least one 2-minute pause.",
    "many_disallowed_empty_disabled_clicks": "Participant with many disallowed, empty, or disabled clicks during instructions or gameplay.",
    "unusual_task_performance": "Participant with unusually high or low task performance.",
    "high_total_movement_distance": "Participant with high total movement distance.",
    "age_gender_coverage": "Participant selected to improve age and gender coverage across the interview sample.",
}


def read_manifest(path: Path = INTERVIEW_MANIFEST_PATH) -> dict[str, Any]:
    if not path.exists():
        return {"category_definitions": DEFAULT_INTERVIEW_CATEGORIES, "interviews": []}
    data = json.loads(path.read_text(encoding="utf-8"))
    data.setdefault("category_definitions", DEFAULT_INTERVIEW_CATEGORIES)
    data.setdefault("interviews", [])
    return data


def read_transcript_csv(path: Path) -> list[dict[str, str]]:
    encoding = detect_text_encoding(path)
    text = path.read_text(encoding=encoding)
    sample = text[:2048]
    try:
        dialect = csv.Sniffer().sniff(sample, delimiters=",;\t")
    except csv.Error:
        dialect = csv.excel

    rows = list(csv.DictReader(text.splitlines(), dialect=dialect))
    turns: list[dict[str, str]] = []
    for row in rows:
        speaker = clean(row.get("Speaker") or row.get("speaker"))
        transcript = clean(row.get("Transcript") or row.get("transcript"))
        if speaker or transcript:
            turns.append({"speaker": speaker, "transcript": transcript})
    return turns


def participant_ids_from_turns(turns: list[dict[str, str]]) -> list[str]:
    ids: list[str] = []
    for turn in turns:
        speaker = clean(turn.get("speaker"))
        if speaker and speaker.lower() != "researcher" and speaker not in ids:
            ids.append(speaker)
    return ids


def age_group(age: object) -> str:
    try:
        value = int(float(clean(age)))
    except ValueError:
        return "Unknown age"
    if value < 18:
        return "<18"
    if value <= 24:
        return "18-24"
    if value <= 34:
        return "25-34"
    if value <= 44:
        return "35-44"
    return "45+"


def compact_participant_type(participant_id: str, lookup: dict[str, dict[str, Any]]) -> dict[str, str]:
    participant = lookup.get(participant_id, {})
    return {
        "participant_id": participant_id,
        "condition": clean(participant.get("condition")) or "Unknown condition",
        "gender": clean(participant.get("gender")) or "Unknown gender",
        "age_group": age_group(participant.get("age")),
    }


def load_interview_overview(
    transcripts_dir: Path = INTERVIEW_TRANSCRIPTS_DIR,
    participants: list[dict[str, Any]] | None = None,
    manifest_path: Path = INTERVIEW_MANIFEST_PATH,
) -> dict[str, Any]:
    manifest = read_manifest(manifest_path)
    categories = manifest.get("category_definitions", DEFAULT_INTERVIEW_CATEGORIES)
    manifest_lookup = {
        clean(entry.get("filename")).lower(): entry
        for entry in manifest.get("interviews", [])
        if clean(entry.get("filename"))
    }
    participant_lookup = {
        clean(row.get("participant_id")): row
        for row in (participants or [])
        if clean(row.get("participant_id"))
    }

    if not transcripts_dir.exists():
        return {
            "available": False,
            "n_files": 0,
            "n_turns": 0,
            "unique_participant_ids": [],
            "category_rows": [],
            "type_rows": [],
            "transcripts": [],
            "notes": ["Interview transcript directory not found."],
        }

    transcript_paths = sorted(transcripts_dir.glob("*.csv"))
    transcripts: list[dict[str, Any]] = []
    category_to_ids: dict[str, set[str]] = defaultdict(set)
    category_to_files: dict[str, set[str]] = defaultdict(set)
    category_to_transcript_ids: dict[str, list[str]] = defaultdict(list)
    type_counter: Counter[tuple[str, str, str, str]] = Counter()
    all_participant_ids: set[str] = set()
    total_turns = 0
    notes: list[str] = []

    for index, path in enumerate(transcript_paths, start=1):
        turns = read_transcript_csv(path)
        speaker_ids = participant_ids_from_turns(turns)
        all_participant_ids.update(speaker_ids)
        total_turns += len(turns)

        manifest_entry = manifest_lookup.get(path.name.lower(), {})
        selection_categories = [
            clean(item)
            for item in manifest_entry.get("selection_categories", [])
            if clean(item)
        ]

        unknown_categories = [item for item in selection_categories if item not in categories]
        if unknown_categories:
            notes.append(f"{path.name}: unknown manifest categories: {', '.join(unknown_categories)}")

        participant_types = [compact_participant_type(pid, participant_lookup) for pid in speaker_ids]

        transcript_id = f"interview-{index:02d}"

        for category in selection_categories:
            category_to_files[category].add(path.name)
            if transcript_id not in category_to_transcript_ids[category]:
                category_to_transcript_ids[category].append(transcript_id)

        for ptype in participant_types:
            for category in selection_categories:
                category_to_ids[category].add(ptype["participant_id"])

            type_counter[
                (
                    ptype["condition"],
                    ptype["gender"],
                    ptype["age_group"],
                    ", ".join(selection_categories) or "No manifest category",
                )
            ] += 1

        transcripts.append({
            "transcript_id": transcript_id,
            "filename": path.name,
            "interview_no": manifest_entry.get("interview_no") or index,
            "title": " + ".join(speaker_ids) if speaker_ids else path.stem,
            "speaker_ids": speaker_ids,
            "speaker_count": len(speaker_ids),
            "n_turns": len(turns),
            "selection_categories": selection_categories,
            "category_labels": [categories.get(category, category) for category in selection_categories],
            "notes": clean(manifest_entry.get("notes")),
            "participant_types": participant_types,
            "turns": turns,
        })

    transcripts_by_id = {transcript["transcript_id"]: transcript for transcript in transcripts}

    def slot_payload(transcript_id: str | None) -> dict[str, Any]:
        if not transcript_id or transcript_id not in transcripts_by_id:
            return {
                "label": "",
                "transcript_id": "",
                "filename": "",
                "speaker_ids": [],
                "n_turns": 0,
            }

        transcript = transcripts_by_id[transcript_id]
        speaker_ids = transcript.get("speaker_ids") or []
        return {
            "label": " + ".join(speaker_ids) if speaker_ids else transcript.get("filename", ""),
            "transcript_id": transcript["transcript_id"],
            "filename": transcript.get("filename", ""),
            "speaker_ids": speaker_ids,
            "n_turns": transcript.get("n_turns", 0),
        }

    category_rows = []
    for category, definition in categories.items():
        ids = sorted(category_to_ids.get(category, set()))
        files = sorted(category_to_files.get(category, set()))
        transcript_ids = category_to_transcript_ids.get(category, [])
        slots = [slot_payload(transcript_ids[index] if index < len(transcript_ids) else None) for index in range(3)]

        category_rows.append({
            "category": category,
            "definition": definition,
            "slot_1": slots[0],
            "slot_2": slots[1],
            "slot_3": slots[2],
            "overflow_count": max(0, len(transcript_ids) - 3),
            "n_participants": len(ids),
            "n_interviews": len(files),
            "participant_ids": ", ".join(ids),
            "files": ", ".join(files),
        })

    type_rows = [
        {
            "condition": condition,
            "gender": gender,
            "age_group": group,
            "selection_categories": selection_categories,
            "n_participants": count,
        }
        for (condition, gender, group, selection_categories), count in sorted(type_counter.items())
    ]

    return {
        "available": bool(transcript_paths),
        "n_files": len(transcript_paths),
        "n_turns": total_turns,
        "unique_participant_ids": sorted(all_participant_ids),
        "category_rows": category_rows,
        "type_rows": type_rows,
        "transcripts": transcripts,
        "notes": notes,
    }