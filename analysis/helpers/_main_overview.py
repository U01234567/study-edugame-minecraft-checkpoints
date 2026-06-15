from __future__ import annotations

from collections import Counter, defaultdict
import datetime as dt
import json
from pathlib import Path
from typing import Any

from ._cl_main import cl_participant_scores
from ._ctrl_main import ctrl_participant_scores
from ._eng_main import eng_participant_scores
from ._logs_main import load_log_index, log_candidate_paths, parse_log_file
from ._ret_main import retention_wave_summary
from ._shared import (
    AGE_BIN_ORDER,
    COLLECTION_LOCATIONS_PATH,
    CONDITION_ORDER,
    GENDER_ORDER,
    SCALE_VALUE_COLUMNS,
    age_bin,
    canonical_condition,
    clean,
    date_is_outside_official_window,
    delayed_flag,
    delayed_included_flag,
    duration_seconds_between,
    first_present,
    format_seconds,
    mcid_from_row,
    normalise_gender,
    parse_age,
    parse_datetime,
    progress_is_complete,
    scale_value,
    survey_end,
    survey_progress,
    survey_start,
    summarise,
)

RAW_CHECKLIST_REASONS = [
    "Starting date outside [8 May – 5 June]",
    "Log data are missing linked survey data",
    "Survey data are missing linked log data",
    "Survey data are incomplete",
    "Log data do not show at least one creature interacted with in each chapter",
    "Participant does not meet any of the prior exclusion criteria, and is therefore included in the final analysis",
]


def split_survey_waves(rows: list[dict[str, str]]) -> dict[str, Any]:
    """Split the survey export into immediate and delayed waves using the original export columns."""
    immediate: dict[str, list[dict[str, str]]] = defaultdict(list)
    delayed: dict[str, list[dict[str, str]]] = defaultdict(list)
    missing_mcid_rows: list[str] = []

    for row_number, row in enumerate(rows, start=1):
        participant_id = mcid_from_row(row)
        if not participant_id:
            missing_mcid_rows.append(f"survey-row-{row_number}")
            continue
        if delayed_flag(row):
            if delayed_included_flag(row):
                delayed[participant_id].append(row)
        else:
            immediate[participant_id].append(row)

    all_ids = sorted(set(immediate) | set(delayed))
    all_row_counts = Counter(mcid_from_row(row) for row in rows if mcid_from_row(row))

    return {
        "immediate": dict(immediate),
        "delayed": dict(delayed),
        "all_survey_ids": all_ids,
        "missing_mcid_rows": missing_mcid_rows,
        "duplicate_immediate_ids": sorted(participant_id for participant_id, grouped in immediate.items() if len(grouped) > 1),
        "duplicate_delayed_ids": sorted(participant_id for participant_id, grouped in delayed.items() if len(grouped) > 1),
        "more_than_two_rows_ids": sorted(participant_id for participant_id, count in all_row_counts.items() if count > 2),
        "row_counts_by_id": dict(all_row_counts),
    }


def condition_from_survey_row(row: dict[str, str] | None) -> str:
    """Return the publishable survey condition, when present."""
    if row is None:
        return ""
    for column in ("condition", "Condition", "CONDITION", "experiment_condition", "condition_raw"):
        raw_condition = clean(row.get(column))
        if raw_condition:
            return canonical_condition(raw_condition) or raw_condition
    return ""


def participant_condition(row: dict[str, str] | None, log: dict[str, Any] | None) -> str:
    """Use survey_export.tsv as condition source, falling back to log metadata."""
    survey_condition = condition_from_survey_row(row)
    if survey_condition:
        return survey_condition
    if log and log.get("condition"):
        return log.get("condition")
    return "Missing / invalid"


def best_start_for_participant(participant_id: str, waves: dict[str, Any], log: dict[str, Any] | None) -> str:
    immediate_row = (waves["immediate"].get(participant_id) or [None])[0]
    delayed_row = (waves["delayed"].get(participant_id) or [None])[0]
    return survey_start(immediate_row) or survey_start(delayed_row) or (log.get("started_at") if log else "")


def ids_with_outside_start(participant_ids: list[str], waves: dict[str, Any], logs: dict[str, dict[str, Any]]) -> list[str]:
    outside: list[str] = []
    for participant_id in participant_ids:
        if date_is_outside_official_window(best_start_for_participant(participant_id, waves, logs.get(participant_id))):
            outside.append(participant_id)
    return sorted(outside)


def build_raw_log_diagnostics(raw_log_dir: Path) -> dict[str, Any]:
    """Parse all raw log files and separate non-consent logs before checklisting."""
    all_logs_by_id: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for path in log_candidate_paths(raw_log_dir):
        parsed = parse_log_file(path)
        if parsed is None:
            continue
        all_logs_by_id[parsed["participant_id"]].append(parsed)

    duplicate_log_ids = sorted(participant_id for participant_id, logs in all_logs_by_id.items() if len(logs) > 1)
    non_consent_log_ids = sorted(
        participant_id
        for participant_id, logs in all_logs_by_id.items()
        if logs and not any(log.get("agreed_to_participate") is True for log in logs)
    )

    consent_logs_by_id: dict[str, dict[str, Any]] = {}
    for participant_id, logs in all_logs_by_id.items():
        consent_logs = [log for log in logs if log.get("agreed_to_participate") is True]
        if not consent_logs:
            continue
        consent_logs_by_id[participant_id] = sorted(consent_logs, key=lambda item: (item.get("started_at", ""), item.get("source_log", "")))[-1]

    return {
        "all_logs_by_id": dict(all_logs_by_id),
        "consent_logs_by_id": consent_logs_by_id,
        "duplicate_log_ids": duplicate_log_ids,
        "non_consent_log_ids": non_consent_log_ids,
    }


def add_checklist_row(rows: list[dict[str, Any]], reason: str, ids: list[str], *, hide_mcids: bool = False) -> None:
    rows.append({
        "reason": reason,
        "n": len(ids),
        "mcids": "—" if hide_mcids else ", ".join(sorted(ids)) if ids else "—",
    })


def build_raw_inclusion_checklist(survey_rows: list[dict[str, str]], raw_log_dir: Path) -> dict[str, Any]:
    """Build the hierarchical raw inclusion/exclusion checklist used by Main."""
    waves = split_survey_waves(survey_rows)
    log_diagnostics = build_raw_log_diagnostics(raw_log_dir)
    logs = log_diagnostics["consent_logs_by_id"]
    survey_ids = set(waves["all_survey_ids"])
    log_ids = set(logs)
    remaining = set(survey_ids | log_ids)
    rows: list[dict[str, Any]] = []

    outside_ids = ids_with_outside_start(sorted(remaining), waves, logs)
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[0], outside_ids, hide_mcids=True)
    remaining -= set(outside_ids)

    logs_missing_survey = sorted(participant_id for participant_id in remaining if participant_id in log_ids and participant_id not in survey_ids)
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[1], logs_missing_survey)
    remaining -= set(logs_missing_survey)

    survey_missing_logs = sorted(participant_id for participant_id in remaining if participant_id in survey_ids and participant_id not in log_ids)
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[2], survey_missing_logs)
    remaining -= set(survey_missing_logs)

    incomplete_survey = []
    for participant_id in sorted(remaining):
        immediate_rows = waves["immediate"].get(participant_id, [])
        if not immediate_rows or not progress_is_complete(survey_progress(immediate_rows[0])):
            incomplete_survey.append(participant_id)
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[3], incomplete_survey)
    remaining -= set(incomplete_survey)

    incomplete_logs = sorted(
        participant_id
        for participant_id in remaining
        if logs.get(participant_id) and not logs[participant_id].get("interacted_in_each_learning_chapter")
    )
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[4], incomplete_logs)
    remaining -= set(incomplete_logs)

    included_ids = sorted(remaining)
    add_checklist_row(rows, RAW_CHECKLIST_REASONS[5], included_ids, hide_mcids=True)

    hidden_non_consent_in_survey = sorted(set(log_diagnostics["non_consent_log_ids"]) & survey_ids)
    return {
        "rows": rows,
        "included_ids": included_ids,
        "hidden_non_consent_log_ids": log_diagnostics["non_consent_log_ids"],
        "hidden_non_consent_in_survey": hidden_non_consent_in_survey,
        "duplicate_log_ids": log_diagnostics["duplicate_log_ids"],
        "survey_more_than_two_rows_ids": waves["more_than_two_rows_ids"],
        "survey_missing_mcid_rows": waves["missing_mcid_rows"],
        "survey_unique_ids": len(survey_ids),
        "consenting_log_unique_ids": len(log_ids),
    }


def delayed_completed(delayed_row: dict[str, str] | None) -> bool:
    return delayed_row is not None and progress_is_complete(survey_progress(delayed_row))


def add_optional_seconds(*values: float | int | None) -> float | None:
    valid = [float(value) for value in values if value is not None]
    if not valid:
        return None
    return sum(valid)


def _format_number(value: float | int | None) -> str:
    if value is None:
        return "—"
    value = float(value)
    if value.is_integer():
        return str(int(value))
    return f"{value:.2f}"


def _format_stat_value(value: float | int | None, *, duration: bool) -> str:
    if value is None:
        return "—"
    if duration:
        return format_seconds(value) or "—"
    return _format_number(value)


def stat_micro_table(values: list[float | int | None], *, duration: bool = False) -> str:
    """Return a compact in-cell mean/SD/min/max table."""
    summary = summarise(values)
    if not summary["n"]:
        return "—"
    rows = [
        ("Mean", _format_stat_value(summary["mean"], duration=duration)),
        ("<em>SD</em>", _format_stat_value(summary["sd"], duration=duration)),
        ("Min", _format_stat_value(summary["min"], duration=duration)),
        ("Max", _format_stat_value(summary["max"], duration=duration)),
    ]
    return "<table class=\"micro-stat-table\"><tbody>" + "".join(
        f"<tr><th>{label}</th><td>{value}</td></tr>" for label, value in rows
    ) + "</tbody></table>"


def scale_values_from_row(row: dict[str, str]) -> dict[str, str]:
    """Keep only questionnaire scale columns needed for merged-report calculations."""
    return {column: scale_value(row, column) for column in SCALE_VALUE_COLUMNS}


SLOT_LABELS = [
    "09:40 - 10:40",
    "10:40 - 11:40",
    "11:40 - 12:40",
    "12:40 - 13:40",
    "13:40 - 14:40",
    "14:40 - 15:40",
    "15:40 - 16:40",
    "Outside 09:40 - 16:40",
]
ALLOWED_COLLECTION_LOCATIONS = {"Creative Space", "Living Room"}


def participant_is_remote(row: dict[str, str]) -> bool:
    return clean(first_present(row, ["REMOTE", "remote", "Remote"])).lower() in {"1", "true", "yes"}


def slot_for_start(start: dt.datetime | None) -> tuple[str, int]:
    """Return the lab slot based on the parsed start of the /data/logs/ file."""
    if start is None:
        return "Unknown start time", 999

    time_of_day = start.time().replace(second=0, microsecond=0)
    anchor = dt.datetime.combine(dt.date(2000, 1, 1), dt.time(9, 40))
    for index in range(7):
        begin = (anchor + dt.timedelta(hours=index)).time()
        end = (anchor + dt.timedelta(hours=index + 1)).time()
        if begin <= time_of_day < end:
            return SLOT_LABELS[index], index

    return SLOT_LABELS[-1], 7


def load_collection_locations(path: Path = COLLECTION_LOCATIONS_PATH) -> tuple[dict[str, str], list[str]]:
    """Load the editable date -> lab-location map."""
    if not path.exists():
        return {}, [f"Collection-location template not found at {path}."]
    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        return {}, [f"Collection-location template could not be read from {path}: {exc}"]
    if not isinstance(payload, dict):
        return {}, ["Collection-location template must be a JSON object."]
    raw_locations = payload.get("locations_by_date", payload)
    if not isinstance(raw_locations, dict):
        return {}, ["Collection-location template must contain a locations_by_date object."]
    return {clean(date_key): clean(value) for date_key, value in raw_locations.items()}, []


def lab_slot_key(participant: dict[str, Any]) -> tuple[str, str, str]:
    return (
        clean(participant.get("collection_date")),
        clean(participant.get("collection_slot_label")),
        clean(participant.get("room_type")),
    )


def occupied_lab_slot_groups(participants: list[dict[str, Any]]) -> dict[tuple[str, str, str], list[dict[str, Any]]]:
    """Group only occupied lab slots; unused slots are never represented."""
    grouped: dict[tuple[str, str, str], list[dict[str, Any]]] = defaultdict(list)
    for participant in participants:
        location = clean(participant.get("room_type"))
        if location not in ALLOWED_COLLECTION_LOCATIONS:
            continue
        key = lab_slot_key(participant)
        if not all(key):
            continue
        grouped[key].append(participant)
    return grouped


def attach_collection_context(participants: list[dict[str, Any]], collection_locations_path: Path = COLLECTION_LOCATIONS_PATH) -> list[str]:
    """Attach room type and occupied-room-slot sizes to included participants."""
    locations_by_date, warnings = load_collection_locations(collection_locations_path)
    warning_set = set(warnings)

    for participant in participants:
        participant["collection_context_warning"] = ""
        participant["same_room_participants_n"] = None

        if participant.get("remote"):
            participant["room_type"] = "At home"
            continue

        date_key = clean(participant.get("collection_date"))
        location = locations_by_date.get(date_key)
        if not date_key:
            participant["room_type"] = ""
            participant["collection_context_warning"] = "Collection date could not be parsed from the log start."
        elif location not in ALLOWED_COLLECTION_LOCATIONS:
            participant["room_type"] = ""
            missing_or_invalid = "missing" if location in {None, ""} else f"invalid value '{location}'"
            participant["collection_context_warning"] = f"Collection location is {missing_or_invalid} for {date_key}."
            warning_set.add(
                f"Collection location is {missing_or_invalid} for {date_key}. "
                "Fill data/config/collection_locations.json with Creative Space or Living Room, "
                "or remove the date only if no data collection happened that day."
            )
        else:
            participant["room_type"] = location

    for group in occupied_lab_slot_groups(participants).values():
        slot_size = len(group)
        same_room_n = max(0, slot_size - 1)
        for participant in group:
            participant["same_room_participants_n"] = slot_size
            participant["same_room_n"] = same_room_n

    return sorted(warning_set)




def _choice_time_ms(log: dict[str, Any] | None, moment: str) -> float | None:
    if not log:
        return None
    values = [
        item.get("choice_time_ms")
        for item in log.get("manipulated_checkpoint_choices", [])
        if clean(item.get("moment")) == moment and item.get("choice_time_ms") is not None
    ]
    return values[0] if values else None


def log_report_values(log: dict[str, Any] | None) -> dict[str, Any]:
    """Copy selected parsed-log metrics onto the participant row for reporting."""
    if not log:
        return {}
    time_to_sixth = log.get("time_to_sixth_creature_by_chapter", {}) or {}
    checkpoint_count = log.get("checkpoint_choice_count") or 0
    break_count = log.get("break_choice_count") or 0
    continue_count = log.get("continue_choice_count") or 0
    return {
        "log_duration_seconds": log.get("log_duration_seconds"),
        "ch0_duration_seconds": log.get("ch0_duration_seconds"),
        "completed_all_chapters": bool(log.get("completed_all_chapters")),
        "completed_learning_chapters": bool(log.get("completed_learning_chapters")),
        "interacted_in_each_learning_chapter": bool(log.get("interacted_in_each_learning_chapter")),
        "interacted_creature_instance_count": log.get("interacted_creature_instance_count"),
        "species_revisited_count": log.get("species_revisited_count"),
        "creatures_revisited_count": log.get("creatures_revisited_count"),
        "time_to_sixth_creature_ch1_seconds": time_to_sixth.get("1"),
        "time_to_sixth_creature_ch2_seconds": time_to_sixth.get("2"),
        "time_to_sixth_creature_ch3_seconds": time_to_sixth.get("3"),
        "checkpoint_choice_count": checkpoint_count,
        "break_choice_count": break_count,
        "continue_choice_count": continue_count,
        "break_choice_percent": (100.0 * break_count / checkpoint_count) if checkpoint_count else None,
        "continue_choice_percent": (100.0 * continue_count / checkpoint_count) if checkpoint_count else None,
        "checkpoint_choices": log.get("checkpoint_choices", []),
        "manipulated_checkpoint_choices": log.get("manipulated_checkpoint_choices", []),
        "choice_time_ch1_ch2_ms": _choice_time_ms(log, "Ch1 > Ch2"),
        "choice_time_ch2_ch3_ms": _choice_time_ms(log, "Ch2 > Ch3"),
        "card_open_count": log.get("card_open_count"),
        "card_close_count": log.get("card_close_count"),
        "card_open_count_learning": log.get("card_open_count_learning"),
        "card_close_count_learning": log.get("card_close_count_learning"),
        "card_read_time_total_ms": log.get("card_read_time_total_ms"),
        "card_read_time_mean_ms": log.get("card_read_time_mean_ms"),
        "card_reading_seconds": log.get("card_reading_seconds"),
        "walking_seconds_estimate": log.get("walking_seconds_estimate"),
        "sprinting_seconds_estimate": log.get("sprinting_seconds_estimate"),
        "walking_sprinting_seconds_estimate": log.get("walking_sprinting_seconds_estimate"),
        "other_seconds_estimate": log.get("other_seconds_estimate"),
        "movement_sample_count": log.get("movement_sample_count"),
        "movement_total_distance": log.get("movement_total_distance"),
        "movement_total_sprint_distance": log.get("movement_total_sprint_distance"),
        "game_end_reason": clean(log.get("game_end_reason")) or "Missing / not set",
        "event_count": log.get("event_count"),
    }

def participant_from_survey_and_log(participant_id: str, immediate_row: dict[str, str], delayed_row: dict[str, str] | None, log: dict[str, Any] | None) -> dict[str, Any]:
    """Build the participant-level row using the same survey columns as the original merged app."""
    age = parse_age(immediate_row.get("age"))
    gender = normalise_gender(immediate_row.get("gender"))
    game_duration_seconds = log.get("game_duration_seconds") if log else None
    questionnaire_duration_seconds = duration_seconds_between(survey_start(immediate_row), survey_end(immediate_row))
    delayed_duration_seconds = duration_seconds_between(survey_start(delayed_row), survey_end(delayed_row))
    total_duration_seconds = add_optional_seconds(game_duration_seconds, questionnaire_duration_seconds, delayed_duration_seconds)
    creature_score = log.get("creature_score_of_18") if log else None
    immediate_retention = retention_wave_summary(immediate_row)
    delayed_retention = retention_wave_summary(delayed_row)
    log_start_dt = parse_datetime(log.get("started_at")) if log else None
    survey_start_dt = parse_datetime(survey_start(immediate_row))
    collection_start_dt = log_start_dt or survey_start_dt
    slot_label, slot_order = slot_for_start(collection_start_dt)
    remote = participant_is_remote(immediate_row)

    participant = {
        "participant_id": participant_id,
        "condition": participant_condition(immediate_row, log),
        "remote": remote,
        "remote_raw": clean(first_present(immediate_row, ["REMOTE", "remote", "Remote"])),
        "collection_start_source": "log" if log_start_dt else "survey",
        "collection_date": collection_start_dt.date().isoformat() if collection_start_dt else "",
        "collection_slot_label": slot_label,
        "collection_slot_order": slot_order,
        "room_type": "At home" if remote else "",
        "same_room_participants_n": None,
        "same_room_n": None,
        "collection_context_warning": "",
        "age": age,
        "age_raw": clean(immediate_row.get("age")),
        "age_bin": age_bin(age),
        "gender": gender,
        "gender_raw": clean(immediate_row.get("gender")),
        "completed_delayed_retention_test": delayed_completed(delayed_row),
        "game_duration_seconds": game_duration_seconds,
        "game_duration": format_seconds(game_duration_seconds),
        "questionnaire_duration_seconds": questionnaire_duration_seconds,
        "questionnaire_duration": format_seconds(questionnaire_duration_seconds),
        "delayed_duration_seconds": delayed_duration_seconds,
        "delayed_duration": format_seconds(delayed_duration_seconds),
        "total_duration_seconds": total_duration_seconds,
        "total_duration": format_seconds(total_duration_seconds),
        "experiment_duration_seconds": total_duration_seconds,
        "experiment_duration": format_seconds(total_duration_seconds),
        "logs_creature_score_of_18": creature_score,
        "logs_creature_score_label": f"{creature_score}/18" if creature_score is not None else "—",
        "ret_immediate_seen_count": immediate_retention["seen_creature_count"],
        "ret_immediate_seen_creature_count": immediate_retention["seen_creature_count"],
        "ret_immediate_answer_count": immediate_retention["answer_count"],
        "ret_immediate_answers": immediate_retention["answers"],
        "ret_immediate_seen_invalid": immediate_retention["seen_invalid"],
        "ret_delayed_available": delayed_retention["available"],
        "ret_delayed_seen_count": delayed_retention["seen_creature_count"],
        "ret_delayed_seen_creature_count": delayed_retention["seen_creature_count"],
        "ret_delayed_answer_count": delayed_retention["answer_count"],
        "ret_delayed_answers": delayed_retention["answers"],
        "ret_delayed_seen_invalid": delayed_retention["seen_invalid"],
        "scale_values": scale_values_from_row(immediate_row),
    }

    participant.update(log_report_values(log))
    participant.update(cl_participant_scores(immediate_row))
    participant.update(eng_participant_scores(immediate_row))
    participant.update(ctrl_participant_scores(immediate_row))
    return participant


def build_merged_dataset(
    survey_rows: list[dict[str, str]],
    log_index: dict[str, dict[str, Any]],
    collection_locations_path: Path = COLLECTION_LOCATIONS_PATH,
) -> dict[str, Any]:
    waves = split_survey_waves(survey_rows)
    participants: list[dict[str, Any]] = []

    for participant_id in sorted(waves["immediate"]):
        immediate_row = waves["immediate"][participant_id][0]
        delayed_row = (waves["delayed"].get(participant_id) or [None])[0]
        participants.append(participant_from_survey_and_log(participant_id, immediate_row, delayed_row, log_index.get(participant_id)))

    collection_context_warnings = attach_collection_context(participants, collection_locations_path)

    return {
        "participants": participants,
        "audit": {
            "survey_rows": len(survey_rows),
            "survey_unique_ids": len(waves["all_survey_ids"]),
            "log_unique_ids": len(log_index),
            "included_count": len(participants),
            "ids_in_survey_not_logs": sorted(set(waves["all_survey_ids"]) - set(log_index)),
            "ids_in_logs_not_survey": sorted(set(log_index) - set(waves["all_survey_ids"])),
            "collection_context_warnings": collection_context_warnings,
            "collection_locations_path": str(collection_locations_path),
        },
    }


def condition_summary(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    for condition in CONDITION_ORDER + ["Overall"]:
        scoped = participants if condition == "Overall" else [participant for participant in participants if participant.get("condition") == condition]
        rows.append({
            "condition": condition,
            "n": len(scoped),
            "completed_delayed_retention_count": sum(1 for participant in scoped if participant.get("completed_delayed_retention_test")),
            "age": stat_micro_table([participant.get("age") for participant in scoped]),
            "creature_score": stat_micro_table([participant.get("logs_creature_score_of_18") for participant in scoped]),
            "game_duration": stat_micro_table([participant.get("game_duration_seconds") for participant in scoped], duration=True),
            "questionnaire_duration": stat_micro_table([participant.get("questionnaire_duration_seconds") for participant in scoped], duration=True),
            "delayed_duration": stat_micro_table([participant.get("delayed_duration_seconds") for participant in scoped], duration=True),
            "total_duration": stat_micro_table([participant.get("total_duration_seconds") for participant in scoped], duration=True),
        })
    return rows


def grouped_counts_by_condition(participants: list[dict[str, Any]], *, field: str, categories: list[str]) -> list[dict[str, Any]]:
    groups: list[dict[str, Any]] = []
    for condition in CONDITION_ORDER + ["Overall"]:
        scoped = participants if condition == "Overall" else [participant for participant in participants if participant.get("condition") == condition]
        counts = Counter(participant.get(field) or "Unknown / missing" for participant in scoped)
        groups.append({
            "group": condition,
            "rows": [{"label": category, "n": counts.get(category, 0)} for category in categories],
        })
    return groups


def demographic_distributions(participants: list[dict[str, Any]]) -> dict[str, list[dict[str, Any]]]:
    condition_counts = Counter(participant.get("condition") or "Missing / invalid" for participant in participants)
    return {
        "conditions": [
            {"label": condition, "n": condition_counts.get(condition, 0)}
            for condition in CONDITION_ORDER
        ] + [{"label": "Overall", "n": len(participants)}],
        "gender_by_condition": grouped_counts_by_condition(participants, field="gender", categories=GENDER_ORDER),
        "age_by_condition": grouped_counts_by_condition(participants, field="age_bin", categories=AGE_BIN_ORDER),
    }


def location_condition_summary(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Summarise collection location and same-room context by condition.

    Location counts are participant counts. Same-room participants are the number
    of other included participants in the same date, time slot, and lab location;
    this is defined only for Creative Space and Living Room participants.
    """
    locations = sorted({clean(participant.get("room_type")) or "Missing / not set" for participant in participants})
    preferred = ["Creative Space", "Living Room", "At home", "Missing / not set"]
    ordered_locations = [location for location in preferred if location in locations]
    ordered_locations.extend(location for location in locations if location not in ordered_locations)

    rows: list[dict[str, Any]] = []
    for condition in CONDITION_ORDER + ["Overall"]:
        scoped = participants if condition == "Overall" else [
            participant for participant in participants
            if participant.get("condition") == condition
        ]

        same_room_values = [
            participant.get("same_room_n")
            for participant in scoped
            if clean(participant.get("room_type")) in ALLOWED_COLLECTION_LOCATIONS
        ]

        row: dict[str, Any] = {
            "condition": condition,
            "n": len(scoped),
            "same_room_participants": stat_micro_table(same_room_values),
        }
        for location in ordered_locations:
            row[location] = sum(
                1 for participant in scoped
                if (clean(participant.get("room_type")) or "Missing / not set") == location
            )
        rows.append(row)
    return rows


def lab_slot_summary(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Summarise included participants per occupied lab date/time/location slot.

    Ordering is handled once in the frontend, based on the rendered date and
    time labels. Do not sort here and do not emit a second ordering field.
    """
    grouped = occupied_lab_slot_groups(participants)

    return [
        {
            "date": date,
            "time": slot_label,
            "location": location,
            "n": len(group),
        }
        for (date, slot_label, location), group in grouped.items()
    ]


def controlling_variable_tables(participants: list[dict[str, Any]]) -> dict[str, list[dict[str, Any]]]:
    return {
        "location_condition_summary": location_condition_summary(participants),
        "lab_slot_summary": lab_slot_summary(participants),
    }
