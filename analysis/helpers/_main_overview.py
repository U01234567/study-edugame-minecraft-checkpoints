from __future__ import annotations

from collections import Counter, defaultdict
import datetime as dt
import json
from typing import Any

from ._cl_main import cl_participant_scores
from ._ctrl_main import ctrl_participant_scores
from ._eng_main import eng_participant_scores
from ._ret_main import retention_wave_summary
from ._shared import (
    AGE_BIN_ORDER,
    COLLECTION_LOCATIONS_PATH,
    CONDITION_ORDER,
    CREATURE_TOTAL,
    EXCLUSION_CRITERIA,
    GENDER_ORDER,
    SCALE_VALUE_COLUMNS,
    age_bin,
    clean,
    date_is_before_official_start,
    display_datetime,
    duration_between,
    duration_seconds_between,
    first_present,
    format_seconds,
    mean_sd_text,
    mcid_from_row,
    normalise_gender,
    parse_age,
    parse_datetime,
    parse_numeric,
    progress_is_complete,
    scale_value,
    summarise,
)

STUDY_QUESTIONS = [
    {
        "id": "RQ1",
        "text": "How does Checkpoint Design (3; required continue vs required pauses vs optional pauses) at identical natural boundaries in an educational game affect learners’ post-game reported engagement and cognitive-load dimensions while playing, and immediate and delayed retention after playing?",
    },
    {
        "id": "RQ2",
        "text": "To what extent do learners’ post-game reported engagement and cognitive-load dimensions while playing mediate the effects of Checkpoint Design on immediate and delayed retention after playing?",
    },
    {
        "id": "RQ3",
        "text": "Within the optional-pauses condition, how do learners use the opportunity to pause, and how are their realised pause choices, decision time, perceived control, qualitative reports, and behavioural indicators associated with post-game reported engagement and cognitive-load dimensions while playing, and retention after playing?",
    },
    {
        "id": "H1",
        "text": "Checkpoint Design will affect immediate retention and, secondarily, delayed retention one week later; specifically, required pauses are expected to produce higher retention than required continue, and the optional-pauses condition is expected to differ from the average of the two system-controlled checkpoint designs without a directional prediction.",
    },
    {
        "id": "H2",
        "text": "Checkpoint Design will have an indirect effect on immediate and delayed retention via learners’ post-game reported cognitive-load dimensions while playing.",
    },
    {
        "id": "H2a",
        "text": "Checkpoint Design will affect learners’ post-game reported cognitive-load dimensions while playing; specifically, required pauses are expected to reduce extraneous cognitive load and increase germane cognitive load relative to required continue.",
    },
    {
        "id": "H2b",
        "text": "Higher post-game reported extraneous cognitive load while playing will be negatively associated with immediate and delayed retention, whereas higher germane cognitive load will be positively associated with immediate and delayed retention; intrinsic cognitive load will be associated with immediate and delayed retention without a directional prediction.",
    },
    {
        "id": "H3",
        "text": "Checkpoint Design will have an indirect effect on immediate and delayed retention via learners’ post-game reported engagement while playing.",
    },
    {
        "id": "H3a",
        "text": "Checkpoint Design will affect learners’ post-game reported engagement while playing; specifically, optional pauses are expected to produce higher engagement than the average of the two system-controlled checkpoint designs, whereas required pauses are expected to produce lower engagement than required continue.",
    },
    {
        "id": "H3b",
        "text": "Higher post-game reported engagement while playing will be positively associated with immediate and delayed retention.",
    },
    {
        "id": "H4",
        "text": "Higher post-game reported engagement while playing will be negatively associated with extraneous cognitive load and positively associated with germane cognitive load; its association with intrinsic cognitive load will be examined without a directional prediction.",
    },
    {
        "id": "EQ1",
        "text": "To what extent does the effect of Checkpoint Design on immediate and delayed retention operate serially via learners’ post-game reported engagement and then their cognitive-load dimensions while playing?",
    },
    {
        "id": "EQ2",
        "text": "Within the optional-pauses condition, how are learners’ realised pause choices (no pause, one pause, two pauses), decision time, and perceived control associated with post-game reported engagement and cognitive-load dimensions while playing, and retention after playing?",
    },
    {
        "id": "EQ3",
        "text": "How do chapter-level descriptive visualisations, qualitative reports, and behavioural indicators help explain or qualify the quantitative patterns observed across checkpoint conditions, particularly in the optional-pauses condition?",
    },
]


def split_survey_waves(rows: list[dict[str, str]]) -> dict[str, Any]:
    """Split the survey export into immediate and delayed waves using MCID and DELAYED."""
    immediate: dict[str, list[dict[str, str]]] = defaultdict(list)
    delayed: dict[str, list[dict[str, str]]] = defaultdict(list)
    missing_mcid_rows: list[str] = []

    for row_number, row in enumerate(rows, start=1):
        participant_id = mcid_from_row(row)
        if not participant_id:
            missing_mcid_rows.append(f"survey-row-{row_number}")
            continue

        if clean(row.get("DELAYED")) == "1":
            delayed[participant_id].append(row)
        else:
            immediate[participant_id].append(row)

    return {
        "immediate": dict(immediate),
        "delayed": dict(delayed),
        "missing_mcid_rows": missing_mcid_rows,
        "all_survey_ids": sorted(set(immediate) | set(delayed)),
        "immediate_ids": sorted(immediate),
        "delayed_ids": sorted(delayed),
        "duplicate_immediate_ids": sorted(participant_id for participant_id, grouped in immediate.items() if len(grouped) > 1),
        "duplicate_delayed_ids": sorted(participant_id for participant_id, grouped in delayed.items() if len(grouped) > 1),
        "delayed_without_immediate_ids": sorted(set(delayed) - set(immediate)),
    }


def exclusion_summary(excluded_participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Return exclusion counts and affected IDs by reason."""
    by_reason: dict[str, list[str]] = defaultdict(list)

    for participant in excluded_participants:
        participant_id = participant["participant_id"]
        for reason in participant.get("exclusion_reasons", []):
            by_reason[reason].append(participant_id)

    return [
        {"reason": reason, "n": len(sorted_ids), "ids": sorted_ids}
        for reason, ids in sorted(by_reason.items())
        for sorted_ids in [sorted(set(ids))]
    ]


def survey_start(row: dict[str, str] | None) -> str:
    """Return the survey start timestamp from a survey row."""
    if row is None:
        return ""
    return first_present(row, ["startDate", "StartDate", "Start Date"])


def survey_end(row: dict[str, str] | None) -> str:
    """Return the survey end timestamp from a survey row."""
    if row is None:
        return ""
    return first_present(row, ["endDate", "EndDate", "End Date"])


def survey_progress(row: dict[str, str] | None) -> str:
    """Return the survey progress value from a survey row."""
    if row is None:
        return ""
    return first_present(row, ["progress", "Progress"])


def survey_duration(row: dict[str, str] | None) -> str:
    """Return the duration between survey start and end."""
    if row is None:
        return ""
    return duration_between(survey_start(row), survey_end(row))


def scale_values_from_row(row: dict[str, str]) -> dict[str, str]:
    """Keep only questionnaire scale columns needed for summary calculations."""
    return {column: scale_value(row, column) for column in SCALE_VALUE_COLUMNS}


SLOT_LABELS = [
    "09:40–10:40", "10:40–11:40", "11:40–12:40", "12:40–13:40",
    "13:40–14:40", "14:40–15:40", "15:40–16:40", "Outside 09:40–16:40",
]
ALLOWED_COLLECTION_LOCATIONS = {"Creative Space", "Living Room"}
RETENTION_START_VALUES = {"image_first", "name_first"}


def delayed_completed(delayed_row: dict[str, str] | None) -> bool:
    """Return True when the delayed retention row exists and Progress is 100."""
    return delayed_row is not None and progress_is_complete(survey_progress(delayed_row))


def slot_for_start(start: dt.datetime | None) -> tuple[str, int]:
    """Return the planned lab slot used by the follow-up email app."""
    if start is None:
        return "Unknown start time", 999
    t = start.time().replace(second=0, microsecond=0)
    anchor = dt.datetime.combine(dt.date(2000, 1, 1), dt.time(9, 40))
    for index in range(7):
        begin = (anchor + dt.timedelta(hours=index)).time()
        end = (anchor + dt.timedelta(hours=index + 1)).time()
        if begin <= t < end:
            return SLOT_LABELS[index], index
    return SLOT_LABELS[-1], 7


def normalise_retention_start(value: object) -> str:
    """Return image_first/name_first when a retention-form value is present."""
    text = clean(value).lower()
    return text if text in RETENTION_START_VALUES else ""


def retention_start_from_row(row: dict[str, str] | None, *, prefer_init: bool = False) -> str:
    """Read the counterbalanced retention start value from a survey row."""
    if row is None:
        return ""
    columns = ["INIT_START", "START"] if prefer_init else ["START", "INIT_START"]
    for column in columns:
        value = normalise_retention_start(row.get(column))
        if value:
            return value
    return ""


def opposite_retention_start(value: str) -> str:
    if value == "image_first":
        return "name_first"
    if value == "name_first":
        return "image_first"
    return ""


def retention_counterbalance_status(immediate_row: dict[str, str], delayed_row: dict[str, str] | None) -> dict[str, str]:
    """Check whether immediate and delayed retention forms are counterbalanced within MCID."""
    init_start = normalise_retention_start(immediate_row.get("INIT_START"))
    immediate_start = retention_start_from_row(immediate_row, prefer_init=False) or init_start
    delayed_start = retention_start_from_row(delayed_row, prefer_init=False)

    if delayed_row is None:
        return {
            "status": "not checked; no delayed row",
            "warning": "",
            "immediate_start": immediate_start,
            "delayed_start": "",
            "init_start": init_start,
        }
    if not immediate_start or not delayed_start:
        return {
            "status": "missing START/INIT_START value",
            "warning": "Retention counterbalance could not be checked because START or INIT_START is missing.",
            "immediate_start": immediate_start,
            "delayed_start": delayed_start,
            "init_start": init_start,
        }
    if delayed_start != opposite_retention_start(immediate_start):
        return {
            "status": "mismatch",
            "warning": f"Retention counterbalance mismatch: immediate START={immediate_start}, delayed START={delayed_start}.",
            "immediate_start": immediate_start,
            "delayed_start": delayed_start,
            "init_start": init_start,
        }
    if init_start and init_start != immediate_start:
        return {
            "status": "mismatch",
            "warning": f"INIT_START={init_start} does not match the immediate START={immediate_start}.",
            "immediate_start": immediate_start,
            "delayed_start": delayed_start,
            "init_start": init_start,
        }
    return {
        "status": "OK",
        "warning": "",
        "immediate_start": immediate_start,
        "delayed_start": delayed_start,
        "init_start": init_start,
    }


def participant_is_remote(row: dict[str, str]) -> bool:
    return clean(first_present(row, ["REMOTE", "remote", "Remote"])).lower() in {"1", "true", "yes"}


def load_collection_locations() -> tuple[dict[str, str], list[str]]:
    """Load the editable date -> lab-location map."""
    if not COLLECTION_LOCATIONS_PATH.exists():
        return {}, [f"Collection-location template not found at {COLLECTION_LOCATIONS_PATH}."]
    try:
        payload = json.loads(COLLECTION_LOCATIONS_PATH.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        return {}, [f"Collection-location template could not be read: {exc}"]

    if not isinstance(payload, dict):
        return {}, ["Collection-location template must be a JSON object."]
    raw_locations = payload.get("locations_by_date", payload)
    locations: dict[str, str] = {}
    warnings: list[str] = []
    if not isinstance(raw_locations, dict):
        return {}, ["Collection-location template must contain a locations_by_date object."]

    for date_key, value in raw_locations.items():
        location = clean(value)
        if location:
            locations[clean(date_key)] = location
        else:
            locations[clean(date_key)] = ""
    return locations, warnings


def attach_collection_context(participants: list[dict[str, Any]]) -> list[str]:
    """Attach room type and same-slot participant counts to included participants."""
    locations_by_date, warnings = load_collection_locations()
    warning_set = set(warnings)

    for participant in participants:
        participant["collection_context_warning"] = ""
        participant["same_room_n"] = None
        if participant.get("remote"):
            participant["room_type"] = "At home"
            continue

        date_key = clean(participant.get("collection_date"))
        location = locations_by_date.get(date_key)
        if not date_key:
            participant["room_type"] = ""
            participant["collection_context_warning"] = "Collection date could not be parsed from survey start date."
        elif location not in ALLOWED_COLLECTION_LOCATIONS:
            participant["room_type"] = ""
            missing_or_invalid = "missing" if location in {None, ""} else f"invalid value '{location}'"
            participant["collection_context_warning"] = f"Collection location is {missing_or_invalid} for {date_key}."
            warning_set.add(f"Collection location is {missing_or_invalid} for {date_key}. Fill resources/collection_locations.json with Creative Space or Living Room, or remove the date only if no data collection happened that day.")
        else:
            participant["room_type"] = location

    grouped: dict[tuple[str, str, str], list[dict[str, Any]]] = defaultdict(list)
    for participant in participants:
        if participant.get("remote") or not clean(participant.get("room_type")):
            continue
        grouped[(participant.get("collection_date", ""), participant.get("collection_slot_label", ""), participant.get("room_type", ""))].append(participant)

    for group in grouped.values():
        shared_count = max(0, len(group) - 1)
        for participant in group:
            participant["same_room_n"] = shared_count

    return sorted(warning_set)


def participant_from_survey_and_log(
    participant_id: str,
    immediate_row: dict[str, str],
    delayed_row: dict[str, str] | None,
    log: dict[str, Any] | None,
) -> dict[str, Any]:
    """Build the participant-level row used by the merged app."""
    age = parse_age(immediate_row.get("age"))
    gender = normalise_gender(immediate_row.get("gender"))
    immediate_retention = retention_wave_summary(immediate_row)
    delayed_retention = retention_wave_summary(delayed_row)
    completed_delayed_retention_test = delayed_completed(delayed_row)

    started = log.get("consent_agreed_at") if log else ""
    finished = survey_end(immediate_row)
    experiment_duration_seconds = duration_seconds_between(started, finished)
    delayed_duration_seconds = duration_seconds_between(survey_start(delayed_row), survey_end(delayed_row))
    creature_score = log.get("creature_score_of_18") if log else None

    start_dt = parse_datetime(survey_start(immediate_row))
    slot_label, slot_order = slot_for_start(start_dt)
    remote = participant_is_remote(immediate_row)
    retention_check = retention_counterbalance_status(immediate_row, delayed_row)
    retention_immediate_form_order = retention_check["immediate_start"]
    retention_delayed_form_order = retention_check["delayed_start"]

    participant: dict[str, Any] = {
        "participant_id": participant_id,
        "included": "True",
        "condition": log.get("condition") if log and log.get("condition") else "Missing / invalid",
        "condition_raw": log.get("condition_raw") if log else "",
        "source_log": log.get("source_log") if log else "",
        "remote": remote,
        "remote_raw": clean(first_present(immediate_row, ["REMOTE", "remote", "Remote"])),
        "collection_date": start_dt.date().isoformat() if start_dt else "",
        "collection_slot_label": slot_label,
        "collection_slot_order": slot_order,
        "retention_form_order": retention_immediate_form_order,
        "retention_immediate_form_order": retention_immediate_form_order,
        "retention_delayed_form_order": retention_delayed_form_order,
        "retention_init_start": retention_check["init_start"],
        "retention_counterbalance_status": retention_check["status"],
        "retention_counterbalance_warning": retention_check["warning"],
        "room_type": "At home" if remote else "",
        "same_room_n": None,
        "survey_start_date": display_datetime(survey_start(immediate_row)),
        "survey_end_date": display_datetime(survey_end(immediate_row)),
        "survey_duration": survey_duration(immediate_row),
        "progress": survey_progress(immediate_row),
        "age": age,
        "age_raw": clean(immediate_row.get("age")),
        "age_bin": age_bin(age),
        "gender": gender,
        "gender_raw": clean(immediate_row.get("gender")),
        "has_delayed_wave": delayed_row is not None,
        "completed_delayed_retention_test": completed_delayed_retention_test,
        "completed_delayed_retention_tick": "✓" if completed_delayed_retention_test else "",
        "delayed_start_date": display_datetime(survey_start(delayed_row)),
        "delayed_end_date": display_datetime(survey_end(delayed_row)),
        "delayed_progress": survey_progress(delayed_row),
        "delayed_duration": survey_duration(delayed_row),
        "delayed_duration_seconds": delayed_duration_seconds,
        "started": display_datetime(started),
        "finished": display_datetime(finished),
        "experiment_duration": duration_between(started, finished),
        "experiment_duration_seconds": experiment_duration_seconds,
        "ret_immediate_seen_count": immediate_retention["seen_creature_count"],
        "ret_immediate_answer_count": immediate_retention["answer_count"],
        "ret_immediate_answers": immediate_retention["answers"],
        "ret_delayed_seen_count": delayed_retention["seen_creature_count"],
        "ret_delayed_answer_count": delayed_retention["answer_count"],
        "ret_delayed_answers": delayed_retention["answers"],
        "log_file_present": bool(log),
        "log_started_at": log.get("started_at") if log else "",
        "log_consent_agreed_at": log.get("consent_agreed_at") if log else "",
        "log_ended_at": log.get("ended_at") if log else "",
        "log_duration": log.get("log_duration") if log else "",
        "log_duration_seconds": log.get("log_duration_seconds") if log else None,
        "log_agreed_to_participate": log.get("agreed_to_participate") if log else None,
        "log_completed_chapters": log.get("completed_chapters") if log else [],
        "log_interacted_chapters": log.get("interacted_chapters") if log else [],
        "logs_checkpoint_decisions": log.get("checkpoint_decisions") if log else "",
        "logs_checkpoint_choice_count": log.get("checkpoint_choice_count") if log else None,
        "logs_break_choice_count": log.get("break_choice_count") if log else None,
        "logs_continue_choice_count": log.get("continue_choice_count") if log else None,
        "logs_card_open_count": log.get("card_open_count") if log else None,
        "logs_card_close_count": log.get("card_close_count") if log else None,
        "logs_unique_interacted_creatures": log.get("interacted_species_count") if log else None,
        "logs_unique_interacted_creature_instances": log.get("interacted_creature_instance_count") if log else None,
        "logs_species_revisited_count": log.get("species_revisited_count") if log else None,
        "logs_creatures_revisited_count": log.get("creatures_revisited_count") if log else None,
        "logs_creature_score_of_18": creature_score,
        "logs_creature_score_label": f"{creature_score}/{CREATURE_TOTAL}" if creature_score is not None else "",
        "logs_card_read_time_mean_ms": log.get("card_read_time_mean_ms") if log else None,
        "logs_card_read_time_total_ms": log.get("card_read_time_total_ms") if log else None,
        "logs_checkpoint_thinking_time_mean_ms": log.get("checkpoint_thinking_time_mean_ms") if log else None,
        "logs_walking_seconds_estimate": log.get("walking_seconds_estimate") if log else None,
        "logs_sprinting_seconds_estimate": log.get("sprinting_seconds_estimate") if log else None,
        "logs_reading_card_seconds": log.get("reading_card_seconds") if log else None,
        "logs_other_seconds_estimate": log.get("other_seconds_estimate") if log else None,
        "logs_movement_total_distance": log.get("movement_total_distance") if log else None,
        "logs_movement_total_sprint_distance": log.get("movement_total_sprint_distance") if log else None,
        "logs_game_end_reason": log.get("game_end_reason") if log else "",
        "scale_values": scale_values_from_row(immediate_row),
    }

    participant.update(cl_participant_scores(immediate_row))
    participant.update(eng_participant_scores(immediate_row))
    participant.update(ctrl_participant_scores(immediate_row))

    return participant


def participant_exclusion_reasons(
    participant_id: str,
    immediate_rows: list[dict[str, str]],
    delayed_rows: list[dict[str, str]],
    log: dict[str, Any] | None,
) -> list[str]:
    """Evaluate the current participant-level exclusion criteria."""
    reasons: list[str] = []

    if not immediate_rows:
        reasons.append("Survey has no non-delayed row for this MCID.")
        return reasons

    if len(immediate_rows) > 1:
        reasons.append("Survey has duplicate non-delayed rows for this MCID.")

    immediate_row = immediate_rows[0]

    if date_is_before_official_start(survey_start(immediate_row)):
        reasons.append("Survey start date is before 8 May 2026.")

    if not progress_is_complete(survey_progress(immediate_row)):
        reasons.append("Survey Progress is not 100.")

    if log is None:
        reasons.append("No matching study log was found for this MCID.")
        return reasons

    if log.get("start_before_official_date"):
        reasons.append("Log start date is before 8 May 2026.")

    if log.get("agreed_to_participate") is not True:
        reasons.append("Log does not contain consent_choice=agree_and_continue.")

    if not log.get("completed_all_chapters"):
        reasons.append("Log does not show chapter_completed for Chapters 0, 1, 2, and 3.")

    if not log.get("interacted_in_each_learning_chapter"):
        reasons.append("Log does not show at least one creature_card_closed event in Chapters 1, 2, and 3.")

    return reasons


def build_audit_rows(
    participant_ids: list[str],
    waves: dict[str, Any],
    log_index: dict[str, dict[str, Any]],
    included_ids: set[str],
    exclusion_reasons_by_id: dict[str, list[str]],
) -> list[dict[str, Any]]:
    """Build a per-MCID survey/log audit table for the Main tab."""
    rows: list[dict[str, Any]] = []

    for participant_id in participant_ids:
        immediate_row = (waves["immediate"].get(participant_id) or [None])[0]
        delayed_row = (waves["delayed"].get(participant_id) or [None])[0]
        log = log_index.get(participant_id)

        rows.append({
            "participant_id": participant_id,
            "included": "True" if participant_id in included_ids else "False",
            "survey_present": "True" if immediate_row else "False",
            "survey_start": display_datetime(survey_start(immediate_row)),
            "survey_duration": survey_duration(immediate_row),
            "survey_progress": survey_progress(immediate_row),
            "age": parse_age((immediate_row or {}).get("age")),
            "gender": normalise_gender((immediate_row or {}).get("gender")),
            "delayed_present": "True" if delayed_row else "False",
            "delayed_completed": "True" if delayed_completed(delayed_row) else "False",
            "delayed_duration": survey_duration(delayed_row),
            "log_present": "True" if log else "False",
            "log_start": log.get("started_at") if log else "",
            "log_consent_time": log.get("consent_agreed_at") if log else "",
            "log_duration": log.get("log_duration") if log else "",
            "condition": log.get("condition") if log else "",
            "checkpoint_decisions": log.get("checkpoint_decisions") if log else "",
            "creature_score": log.get("creature_score_label") if log else "",
            "exclusion_reasons": "; ".join(exclusion_reasons_by_id.get(participant_id, [])),
        })

    return rows


def build_merged_dataset(survey_rows: list[dict[str, str]], log_index: dict[str, dict[str, Any]]) -> dict[str, Any]:
    """Merge survey waves with exact MCID-matched log summaries."""
    waves = split_survey_waves(survey_rows)
    participant_ids = sorted(set(waves["immediate_ids"]) | set(waves["delayed_ids"]) | set(log_index))

    included: list[dict[str, Any]] = []
    excluded: list[dict[str, Any]] = []
    exclusion_reasons_by_id: dict[str, list[str]] = {}

    for participant_id in participant_ids:
        immediate_rows = waves["immediate"].get(participant_id, [])
        delayed_rows = waves["delayed"].get(participant_id, [])
        log = log_index.get(participant_id)
        reasons = participant_exclusion_reasons(participant_id, immediate_rows, delayed_rows, log)
        exclusion_reasons_by_id[participant_id] = reasons

        if immediate_rows:
            participant = participant_from_survey_and_log(
                participant_id,
                immediate_rows[0],
                delayed_rows[0] if delayed_rows else None,
                log,
            )
        else:
            participant = {
                "participant_id": participant_id,
                "included": "False",
                "condition": log.get("condition") if log else "Missing / invalid",
                "source_log": log.get("source_log") if log else "",
                "has_delayed_wave": bool(delayed_rows),
                "completed_delayed_retention_test": delayed_completed(delayed_rows[0] if delayed_rows else None),
                "completed_delayed_retention_tick": "✓" if delayed_completed(delayed_rows[0] if delayed_rows else None) else "",
                "scale_values": {},
            }

        participant["exclusion_reasons"] = reasons
        if reasons:
            participant["included"] = "False"
            excluded.append(participant)
        else:
            included.append(participant)

    collection_context_warnings = attach_collection_context(included)

    log_ids = sorted(log_index)
    survey_ids = waves["all_survey_ids"]
    included_ids = {participant["participant_id"] for participant in included}

    return {
        "participants": sorted(included, key=lambda item: item["participant_id"]),
        "excluded_participants": sorted(excluded, key=lambda item: item["participant_id"]),
        "audit_rows": build_audit_rows(participant_ids, waves, log_index, included_ids, exclusion_reasons_by_id),
        "audit": {
            "survey_rows": len(survey_rows),
            "survey_unique_ids": len(survey_ids),
            "survey_immediate_unique_ids_raw": len(waves["immediate_ids"]),
            "survey_delayed_unique_ids_raw": len(waves["delayed_ids"]),
            "survey_missing_mcid_rows": waves["missing_mcid_rows"],
            "survey_duplicate_immediate_ids": waves["duplicate_immediate_ids"],
            "survey_duplicate_delayed_ids": waves["duplicate_delayed_ids"],
            "delayed_without_immediate_ids": waves["delayed_without_immediate_ids"],
            "log_unique_ids": len(log_ids),
            "ids_in_survey_not_logs": sorted(set(survey_ids) - set(log_ids)),
            "ids_in_logs_not_survey": sorted(set(log_ids) - set(survey_ids)),
            "included_count": len(included),
            "excluded_count": len(excluded),
            "included_immediate_response_count": len(included),
            "included_delayed_response_count": sum(1 for participant in included if participant.get("completed_delayed_retention_test")),
            "collection_context_warnings": collection_context_warnings,
            "collection_locations_path": str(COLLECTION_LOCATIONS_PATH),
            "exclusion_criteria": EXCLUSION_CRITERIA,
        },
    }


def condition_summary(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Create a minimal demographic and log-score summary by condition and overall."""
    rows: list[dict[str, Any]] = []

    for condition in CONDITION_ORDER + ["Overall"]:
        scoped = participants if condition == "Overall" else [p for p in participants if p.get("condition") == condition]
        duration_summary = summarise([p.get("experiment_duration_seconds") for p in scoped])
        score_summary = summarise([p.get("logs_creature_score_of_18") for p in scoped])

        duration_mean_sd = ""
        if duration_summary["n"]:
            duration_mean_sd = f"{format_seconds(duration_summary['mean'])} ({format_seconds(duration_summary['sd'] or 0)})"

        row: dict[str, Any] = {
            "condition": condition,
            "n": len(scoped),
            "completed_delayed_retention_count": sum(1 for p in scoped if p.get("completed_delayed_retention_test")),
            "age_mean_sd": mean_sd_text([p.get("age") for p in scoped]),
            "experiment_duration_mean_sd": duration_mean_sd,
            "experiment_duration_min": format_seconds(duration_summary["min"]),
            "experiment_duration_max": format_seconds(duration_summary["max"]),
            "creature_score_mean_sd": mean_sd_text([p.get("logs_creature_score_of_18") for p in scoped]),
            "creature_score_min": score_summary["min"],
            "creature_score_max": score_summary["max"],
        }

        gender_counts = Counter(p.get("gender") or "Unknown / missing" for p in scoped)
        age_bin_counts = Counter(p.get("age_bin") or "Unknown / missing" for p in scoped)

        for gender in GENDER_ORDER:
            row[f"gender_{gender}"] = gender_counts.get(gender, 0)
        for current_age_bin in AGE_BIN_ORDER:
            row[f"age_bin_{current_age_bin}"] = age_bin_counts.get(current_age_bin, 0)

        rows.append(row)

    return rows