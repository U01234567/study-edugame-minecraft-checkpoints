from __future__ import annotations

import csv
import re
from collections import Counter
from pathlib import Path
from typing import Any
from urllib.parse import parse_qs, urlparse

from ._shared import (
    CHAPTER_TITLE_TO_NUMBER,
    CREATURE_TOTAL,
    LOG_DIR,
    canonical_condition,
    clean,
    date_is_before_official_start,
    display_datetime,
    duration_between,
    duration_seconds_between,
    format_seconds,
    parse_bool,
    parse_datetime,
    parse_int,
    parse_numeric,
    rounded,
    summarise,
)

LOG_LINE_RE = re.compile(
    r"^\[(?P<date>\d{4}-\d{2}-\d{2})\] \[(?P<time>\d{2}:\d{2}:\d{2}:\d{3})\] \| (?P<body>.+)$"
)

REQUIRED_COMPLETED_CHAPTERS = {0, 1, 2, 3}
REQUIRED_INTERACTION_CHAPTERS = {1, 2, 3}
MANIPULATED_CHECKPOINTS = {(1, 2): "Ch1 > Ch2", (2, 3): "Ch2 > Ch3"}
MOVEMENT_SAMPLE_SECONDS = 1.0
MOVEMENT_DISTANCE_EPSILON = 0.05


def parse_log_line(line: str) -> dict[str, Any] | None:
    """Parse one Java StudyEventLog line into timestamp, event, and fields."""
    match = LOG_LINE_RE.match(line)
    if not match:
        return None

    parts = [part.strip() for part in match.group("body").split("|")]
    if not parts:
        return None

    fields: dict[str, str] = {}
    for part in parts[1:]:
        if "=" not in part:
            continue
        key, value = part.split("=", 1)
        fields[clean(key)] = clean(value)

    timestamp = parse_datetime(f"{match.group('date')} {match.group('time')}")

    return {
        "timestamp_dt": timestamp,
        "timestamp": timestamp.strftime("%Y-%m-%d %H:%M:%S") if timestamp else "",
        "event": parts[0],
        "fields": fields,
    }


def read_publishable_log_lines(path: Path) -> list[str]:
    """Read data/logs/{MCID}.csv and reconstruct parseable event lines.

    The publishable CSV intentionally omits per-line session_id and player fields;
    the MCID is recovered from the file name by parse_log_file().
    """
    lines: list[str] = []
    with path.open("r", encoding="utf-8-sig", newline="") as handle:
        reader = csv.DictReader(handle)
        for row in reader:
            date = clean(row.get("Date"))
            time = clean(row.get("Time"))
            activity = clean(row.get("Activity"))
            if not date or not time or not activity:
                continue
            lines.append(f"[{date}] [{time}] | {activity}")
    return lines


def log_candidate_paths(log_dir: Path) -> list[Path]:
    """Return raw study logs and publishable CSV logs from a directory."""
    if not log_dir.exists():
        return []

    paths = [
        *log_dir.glob("study-*.log"),
        *log_dir.glob("*.csv"),
    ]
    return sorted({path for path in paths if path.is_file()}, key=lambda item: item.name)


def chapter_from_title(title: object) -> int | None:
    """Map the exact Java chapter titles to chapter numbers."""
    return CHAPTER_TITLE_TO_NUMBER.get(clean(title))


def event_chapter(event: dict[str, Any], active_chapter: int | None = None) -> int | None:
    """Return the chapter number implied by a log event."""
    fields = event.get("fields", {})
    return (
        parse_int(fields.get("chapter"))
        or chapter_from_title(fields.get("active_chapter_title"))
        or chapter_from_title(fields.get("chapter_title"))
        or active_chapter
    )


def questionnaire_payload_from_url(url: str) -> dict[str, Any]:
    """Extract MCID and CREATURES_SEEN from the Qualtrics URL, when logged.

    This is kept for audit only. Creature scores are calculated from log events,
    not from the survey URL.
    """
    parsed = urlparse(clean(url))
    query = parse_qs(parsed.query)

    return {
        "mcid": clean(query.get("MCID", [""])[0]),
        "creatures_seen": clean(query.get("CREATURES_SEEN", [""])[0]),
    }


def fallback_species_key(entity_type: str | None, creature_name: str | None) -> str:
    """Return the best available species key from log fields only.

    A spawned creature name such as abyss_deer_a is collapsed to abyss_deer, and
    entity_type is used only when the spawn name is missing.
    """
    if creature_name:
        base = re.sub(r"_[a-z]$", "", clean(creature_name).lower())
        if base:
            return base

    if entity_type:
        return clean(entity_type).split(".")[-1].lower()

    return ""


def normalise_label(value: object) -> str:
    """Return a stable label key for display-name fallbacks."""
    return re.sub(r"[^a-z0-9]+", "_", clean(value).lower()).strip("_")


def creature_species_from_event(event: dict[str, Any]) -> str:
    """Return a creature species key from a creature-card log event."""
    fields = event.get("fields", {})
    return fallback_species_key(
        fields.get("entity_type"),
        fields.get("creature_name") or normalise_label(fields.get("creature_label")),
    )


def creature_instance_from_event(event: dict[str, Any]) -> str:
    """Return the best available unique creature-instance key from a card event."""
    fields = event.get("fields", {})
    return clean(
        fields.get("entity_uuid")
        or fields.get("entity_block_pos")
        or fields.get("creature_name")
        or fields.get("creature_label")
        or fields.get("entity_type")
    )


def milliseconds_to_label(value: float | int | None) -> str:
    """Format milliseconds as a compact duration label."""
    if value is None:
        return ""
    return format_seconds(float(value) / 1000.0)


def choice_time_from_context(
    completed_chapter: int | None,
    next_chapter: int | None,
    checkpoint_choice_context: list[dict[str, Any]],
    checkpoint_displayed_at: dict[tuple[int, int], str],
    choice_timestamp: str,
) -> int | None:
    """Return checkpoint choice/thinking time in milliseconds when available."""
    if completed_chapter is None or next_chapter is None:
        return None

    for context in checkpoint_choice_context:
        if context.get("completed_chapter") == completed_chapter and context.get("next_chapter") == next_chapter:
            time_on_screen_ms = context.get("time_on_screen_ms")
            if time_on_screen_ms is not None:
                return int(time_on_screen_ms)

    seconds = duration_seconds_between(checkpoint_displayed_at.get((completed_chapter, next_chapter)), choice_timestamp)
    if seconds is None:
        return None
    return int(round(seconds * 1000))


def parse_log_file(path: Path) -> dict[str, Any] | None:
    """Parse one raw study log or one publishable data/logs/{MCID}.csv file."""
    is_publishable_csv = path.suffix.lower() == ".csv"
    try:
        if is_publishable_csv:
            lines = read_publishable_log_lines(path)
        else:
            lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    except OSError:
        return None

    events = [event for line in lines if (event := parse_log_line(line)) is not None]
    if not events:
        return None

    session_ids = [clean(event["fields"].get("session_id")) for event in events if clean(event["fields"].get("session_id"))]
    participant_id = session_ids[0] if session_ids else (path.stem if is_publishable_csv else "")
    if not participant_id:
        return None

    started_event = next((event for event in events if event["event"] == "study_session_started"), events[0])
    ended_event = events[-1]

    condition_events = [event for event in events if event["event"] == "experiment_condition_assigned"]
    raw_condition = clean(condition_events[0]["fields"].get("condition")) if condition_events else ""
    condition = canonical_condition(raw_condition)

    consent_events = [event for event in events if event["event"] == "consent_choice"]
    agreed_events = [
        event
        for event in consent_events
        if clean(event["fields"].get("choice")) == "agree_and_continue"
    ]
    # Publishable CSV logs intentionally start after the consent line. Treat the
    # first retained event as the in-game start point while preserving the fact
    # that the file was generated only from consenting, included participants.
    consent_agreed_at = agreed_events[0]["timestamp"] if agreed_events else (events[0]["timestamp"] if is_publishable_csv else "")

    questionnaire_events = [event for event in events if event["event"] == "questionnaire_button_pressed"]
    survey_opened_at = questionnaire_events[0]["timestamp"] if questionnaire_events else ""
    questionnaire_payload = questionnaire_payload_from_url(questionnaire_events[-1]["fields"].get("url", "")) if questionnaire_events else {}

    game_duration_seconds = duration_seconds_between(consent_agreed_at, survey_opened_at)
    if game_duration_seconds is None:
        game_duration_seconds = duration_seconds_between(consent_agreed_at or started_event["timestamp"], ended_event["timestamp"])

    completed_chapters = sorted(
        chapter
        for event in events
        if event["event"] == "chapter_completed"
        for chapter in [parse_int(event["fields"].get("chapter"))]
        if chapter is not None
    )

    card_open_events = [event for event in events if event["event"] == "creature_card_opened"]
    card_close_events = [event for event in events if event["event"] == "creature_card_closed"]

    interacted_chapters = sorted(
        chapter
        for event in card_close_events
        for chapter in [event_chapter(event)]
        if chapter is not None
    )

    current_active_chapter: int | None = None
    opened_cards_by_learning_chapter: list[dict[str, Any]] = []
    closed_cards_by_learning_chapter: list[dict[str, Any]] = []
    movement_samples_in_game: list[dict[str, Any]] = []
    chapter_started_at: dict[int, str] = {}
    chapter_completed_at: dict[int, str] = {}
    time_to_sixth_creature_by_chapter: dict[str, float | None] = {"1": None, "2": None, "3": None}
    ch0_seconds: float | None = None
    species_seen_by_chapter: dict[int, set[str]] = {1: set(), 2: set(), 3: set()}

    for event in events:
        event_type = event["event"]

        if event_type == "chapter_started":
            chapter_number = parse_int(event["fields"].get("chapter"))
            if chapter_number is not None:
                current_active_chapter = chapter_number
                chapter_started_at[chapter_number] = event["timestamp"]
            continue

        if event_type == "chapter_completed":
            chapter_number = parse_int(event["fields"].get("chapter"))
            if chapter_number is not None:
                chapter_completed_at[chapter_number] = event["timestamp"]
            if current_active_chapter == chapter_number:
                current_active_chapter = None
            continue

        if event_type == "creature_card_opened":
            chapter_number = event_chapter(event, current_active_chapter)
            if chapter_number in REQUIRED_INTERACTION_CHAPTERS:
                opened_cards_by_learning_chapter.append(event)
                species_key = creature_species_from_event(event)
                if species_key:
                    species_seen_by_chapter[chapter_number].add(species_key)
                    chapter_key = str(chapter_number)
                    if len(species_seen_by_chapter[chapter_number]) >= 6 and time_to_sixth_creature_by_chapter[chapter_key] is None:
                        time_to_sixth_creature_by_chapter[chapter_key] = duration_seconds_between(
                            chapter_started_at.get(chapter_number),
                            event["timestamp"],
                        )
            continue

        if event_type == "creature_card_closed":
            chapter_number = event_chapter(event, current_active_chapter)
            if chapter_number in REQUIRED_INTERACTION_CHAPTERS:
                closed_cards_by_learning_chapter.append(event)
            continue

        if event_type == "movement_sample":
            event_timestamp = event.get("timestamp")
            if consent_agreed_at and survey_opened_at:
                after_consent = duration_seconds_between(consent_agreed_at, event_timestamp)
                before_survey = duration_seconds_between(event_timestamp, survey_opened_at)
                if after_consent is None or before_survey is None:
                    continue
            movement_samples_in_game.append(event)

    creature_score_source_events = opened_cards_by_learning_chapter or closed_cards_by_learning_chapter
    interacted_species = sorted(
        species
        for species in {creature_species_from_event(event) for event in creature_score_source_events}
        if species
    )
    interacted_creature_instances = sorted(
        instance
        for instance in {creature_instance_from_event(event) for event in creature_score_source_events}
        if instance
    )
    species_visit_counts = Counter(creature_species_from_event(event) for event in creature_score_source_events)
    instance_visit_counts = Counter(creature_instance_from_event(event) for event in creature_score_source_events)
    species_visit_counts.pop("", None)
    instance_visit_counts.pop("", None)
    interacted_species_count = min(len(interacted_species), CREATURE_TOTAL)

    raw_checkpoint_choice_context = [
        {
            "completed_chapter": parse_int(event["fields"].get("completed_chapter")),
            "next_chapter": parse_int(event["fields"].get("next_chapter")),
            "choice": clean(event["fields"].get("choice")),
            "prompt_visible_at_choice": parse_bool(event["fields"].get("prompt_visible_at_choice")),
            "prompt_dismissed_before_choice": parse_bool(event["fields"].get("prompt_dismissed_before_choice")),
            "time_on_screen_ms": parse_int(event["fields"].get("time_on_screen_ms")),
            "timestamp": event["timestamp"],
        }
        for event in events
        if event["event"] == "checkpoint_choice_context"
    ]

    checkpoint_displayed_at: dict[tuple[int, int], str] = {}
    for event in events:
        if event["event"] != "checkpoint_displayed":
            continue
        completed = parse_int(event["fields"].get("completed_chapter"))
        next_chapter = parse_int(event["fields"].get("next_chapter"))
        if completed is not None and next_chapter is not None:
            checkpoint_displayed_at[(completed, next_chapter)] = event["timestamp"]

    checkpoint_choices: list[dict[str, Any]] = []
    manipulated_checkpoint_choices: list[dict[str, Any]] = []
    for event in events:
        if event["event"] != "checkpoint_choice_made":
            continue

        completed = parse_int(event["fields"].get("completed_chapter"))
        next_chapter = parse_int(event["fields"].get("next_chapter"))
        choice_time_ms = choice_time_from_context(
            completed,
            next_chapter,
            raw_checkpoint_choice_context,
            checkpoint_displayed_at,
            event["timestamp"],
        )
        moment = MANIPULATED_CHECKPOINTS.get((completed, next_chapter), "")
        row = {
            "completed_chapter": completed,
            "next_chapter": next_chapter,
            "moment": moment,
            "condition": canonical_condition(event["fields"].get("condition")),
            "condition_raw": clean(event["fields"].get("condition")),
            "choice": clean(event["fields"].get("choice")),
            "choice_time_ms": choice_time_ms,
            "choice_time_label": milliseconds_to_label(choice_time_ms),
            "timestamp": event["timestamp"],
        }
        checkpoint_choices.append(row)
        if moment:
            manipulated_checkpoint_choices.append(row)

    card_read_durations = [
        parse_numeric(event["fields"].get("read_duration_ms"))
        for event in closed_cards_by_learning_chapter or card_close_events
        if parse_numeric(event["fields"].get("read_duration_ms")) is not None
    ]
    card_reading_seconds = sum(card_read_durations) / 1000 if card_read_durations else 0.0

    total_distance = 0.0
    sprint_distance = 0.0
    walking_seconds_estimate = 0.0
    sprinting_seconds_estimate = 0.0
    for event in movement_samples_in_game:
        fields = event["fields"]
        sample_distance = parse_numeric(fields.get("sample_distance")) or 0.0
        total_distance += sample_distance
        if parse_bool(fields.get("sprinting")):
            sprinting_seconds_estimate += MOVEMENT_SAMPLE_SECONDS
            sprint_distance += sample_distance
        elif sample_distance > MOVEMENT_DISTANCE_EPSILON:
            walking_seconds_estimate += MOVEMENT_SAMPLE_SECONDS

    walking_sprinting_seconds_estimate = walking_seconds_estimate + sprinting_seconds_estimate
    ch0_seconds = duration_seconds_between(chapter_started_at.get(0), chapter_completed_at.get(0))
    other_seconds_estimate = max(
        0.0,
        (game_duration_seconds or 0.0) - card_reading_seconds - walking_sprinting_seconds_estimate,
    )

    ended_events = [event for event in events if event["event"] == "game_ended"]
    end_reason = clean(ended_events[-1]["fields"].get("reason")) if ended_events else ""

    completed_chapter_set = set(completed_chapters)
    interacted_chapter_set = set(interacted_chapters)
    break_choice_count = sum(1 for item in manipulated_checkpoint_choices if item.get("choice") == "break")
    continue_choice_count = sum(1 for item in manipulated_checkpoint_choices if item.get("choice") == "continue")

    log_start_for_duration = consent_agreed_at or started_event["timestamp"]
    event_counts = Counter(event["event"] for event in events)

    return {
        "participant_id": participant_id,
        "source_log": path.name,
        "log_file_present": True,
        "started_at": display_datetime(started_event["timestamp"]),
        "consent_agreed_at": display_datetime(consent_agreed_at),
        "survey_opened_at": display_datetime(survey_opened_at),
        "ended_at": display_datetime(ended_event["timestamp"]),
        "log_duration": duration_between(log_start_for_duration, ended_event["timestamp"]),
        "log_duration_seconds": duration_seconds_between(log_start_for_duration, ended_event["timestamp"]),
        "game_duration": format_seconds(game_duration_seconds),
        "game_duration_seconds": rounded(game_duration_seconds),
        "ch0_duration": format_seconds(ch0_seconds),
        "ch0_duration_seconds": rounded(ch0_seconds),
        "start_before_official_date": date_is_before_official_start(started_event["timestamp"]),
        "condition": condition,
        "condition_raw": raw_condition,
        "consent_choices": [clean(event["fields"].get("choice")) for event in consent_events],
        "agreed_to_participate": bool(agreed_events) or is_publishable_csv,
        "completed_chapters": sorted(completed_chapter_set),
        "completed_all_chapters": REQUIRED_COMPLETED_CHAPTERS.issubset(completed_chapter_set),
        "completed_learning_chapters": REQUIRED_INTERACTION_CHAPTERS.issubset(completed_chapter_set),
        "interacted_chapters": sorted(interacted_chapter_set),
        "interacted_in_each_learning_chapter": REQUIRED_INTERACTION_CHAPTERS.issubset(interacted_chapter_set),
        "interacted_species": interacted_species,
        "interacted_species_count": interacted_species_count,
        "interacted_creature_instances": interacted_creature_instances,
        "interacted_creature_instance_count": len(interacted_creature_instances),
        "species_revisited_count": sum(1 for count in species_visit_counts.values() if count > 1),
        "creatures_revisited_count": sum(1 for count in instance_visit_counts.values() if count > 1),
        "creature_score_of_18": interacted_species_count,
        "creature_score_label": f"{interacted_species_count}/{CREATURE_TOTAL}",
        "time_to_sixth_creature_by_chapter": time_to_sixth_creature_by_chapter,
        "checkpoint_choices": checkpoint_choices,
        "manipulated_checkpoint_choices": manipulated_checkpoint_choices,
        "checkpoint_choice_context": raw_checkpoint_choice_context,
        "checkpoint_choice_count": len(manipulated_checkpoint_choices),
        "break_choice_count": break_choice_count,
        "continue_choice_count": continue_choice_count,
        "checkpoint_decisions": f"break: {break_choice_count}; continue: {continue_choice_count}",
        "card_open_count": len(card_open_events),
        "card_close_count": len(card_close_events),
        "card_open_count_learning": len(opened_cards_by_learning_chapter),
        "card_close_count_learning": len(closed_cards_by_learning_chapter),
        "card_read_time_total_ms": rounded(sum(card_read_durations)) if card_read_durations else None,
        "card_read_time_mean_ms": rounded(sum(card_read_durations) / len(card_read_durations)) if card_read_durations else None,
        "card_reading_seconds": rounded(card_reading_seconds),
        "walking_seconds_estimate": rounded(walking_seconds_estimate),
        "sprinting_seconds_estimate": rounded(sprinting_seconds_estimate),
        "walking_sprinting_seconds_estimate": rounded(walking_sprinting_seconds_estimate),
        "other_seconds_estimate": rounded(other_seconds_estimate),
        "movement_sample_count": len(movement_samples_in_game),
        "movement_total_distance": rounded(total_distance),
        "movement_total_sprint_distance": rounded(sprint_distance),
        "questionnaire_mcid": questionnaire_payload.get("mcid", ""),
        "questionnaire_creatures_seen": questionnaire_payload.get("creatures_seen", ""),
        "game_end_reason": end_reason,
        "event_count": len(events),
        "event_counts": dict(event_counts),
    }


def load_log_index(log_dir: Path = LOG_DIR) -> dict[str, dict[str, Any]]:
    """Parse raw study logs or publishable CSV logs indexed by MCID/session_id."""
    parsed_logs: dict[str, dict[str, Any]] = {}
    for path in log_candidate_paths(log_dir):
        parsed = parse_log_file(path)
        if parsed is None:
            continue
        parsed_logs[parsed["participant_id"]] = parsed

    return parsed_logs


# ---------------------------------------------------------------------------
# Dense merged-report summaries for the Game logs tab
# ---------------------------------------------------------------------------

LOG_REPORT_CONDITIONS = ["Required continue", "Required pauses", "Optional pauses", "Overall"]

LOG_THEME_SPECS: list[dict[str, Any]] = [
    {
        "title": "Time and Progress",
        "description": "Durations are based on /data/logs/, where the game window starts after consent and ends at the first questionnaire-button press when available.",
        "metrics": [
            {"key": "game_duration_seconds", "label": "Game duration", "kind": "duration"},
            {"key": "ch0_duration_seconds", "label": "Chapter 0 duration", "kind": "duration"},
        ],
    },
    {
        "title": "Creature Interaction",
        "description": "Creature-card and creature-coverage metrics calculated from retained card-open/card-close events.",
        "metrics": [
            {"key": "logs_creature_score_of_18", "label": "Unique creature species", "kind": "number"},
            {"key": "interacted_creature_instance_count", "label": "Creature instances interacted with", "kind": "number"},
            {"key": "species_revisited_count", "label": "Species revisited", "kind": "number"},
            {"key": "creatures_revisited_count", "label": "Creature instances revisited", "kind": "number"},
            {"key": "card_open_count_learning", "label": "Learning-chapter card opens", "kind": "count"},
            {"key": "card_close_count_learning", "label": "Learning-chapter card closes", "kind": "count"},
            {"key": "card_reading_seconds", "label": "Total card reading time", "kind": "duration"},
            {"key": "card_read_time_mean_ms", "label": "Mean card read time", "kind": "duration_ms"},
        ],
    },
    {
        "title": "Movement and Time Allocation",
        "description": "Movement estimates are based on retained movement samples from the in-game interval.",
        "metrics": [
            {"key": "walking_seconds_estimate", "label": "Walking time", "kind": "duration"},
            {"key": "sprinting_seconds_estimate", "label": "Sprinting time", "kind": "duration"},
            {"key": "walking_sprinting_seconds_estimate", "label": "Walking + sprinting time", "kind": "duration"},
            {"key": "other_seconds_estimate", "label": "Still / other time", "kind": "duration"},
            {"key": "movement_sample_count", "label": "Movement samples", "kind": "count"},
            {"key": "movement_total_distance", "label": "Total movement distance", "kind": "number"},
            {"key": "movement_total_sprint_distance", "label": "Sprint distance", "kind": "number"},
        ],
    },
]

LOG_VISUAL_METRICS = [
    {"key": "logs_creature_score_of_18", "label": "Creature species", "kind": "number", "min": 3, "max": 18},
    {"key": "movement_total_distance", "label": "Movement distance", "kind": "number"},
]


def _metric_number(value: object) -> float | None:
    parsed = parse_numeric(value)
    if parsed is None:
        return None
    return float(parsed)


def _format_metric_value(value: float | None, kind: str) -> str:
    if value is None:
        return "—"
    if kind == "duration":
        return format_seconds(value) or "—"
    if kind == "duration_ms":
        return format_seconds(value / 1000.0) or "—"
    if kind == "percent":
        return f"{value:.1f}%"
    if kind == "count":
        return str(int(round(value))) if float(value).is_integer() else f"{value:.2f}"
    if float(value).is_integer():
        return str(int(value))
    return f"{value:.2f}"


def _metric_summary(values: list[object], kind: str) -> dict[str, Any]:
    numeric = [_metric_number(value) for value in values]
    summary = summarise(numeric)
    if not summary["n"]:
        return {"n": 0, "mean_sd": "—", "min": "—", "max": "—"}
    return {
        "n": summary["n"],
        "mean_sd": f"{_format_metric_value(summary['mean'], kind)} ({_format_metric_value(summary['sd'], kind)})",
        "min": _format_metric_value(summary["min"], kind),
        "max": _format_metric_value(summary["max"], kind),
    }


def _scope_for_condition(participants: list[dict[str, Any]], condition: str) -> list[dict[str, Any]]:
    if condition == "Overall":
        return participants
    return [participant for participant in participants if participant.get("condition") == condition]


def _metric_row(participants: list[dict[str, Any]], metric: dict[str, str]) -> dict[str, Any]:
    row: dict[str, Any] = {"metric": metric["label"]}
    for condition in LOG_REPORT_CONDITIONS:
        scoped = _scope_for_condition(participants, condition)
        row[condition] = _metric_summary([participant.get(metric["key"]) for participant in scoped], metric.get("kind", "number"))
    return row


def _theme_rows(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    blocks = []
    for theme in LOG_THEME_SPECS:
        blocks.append({
            "title": theme["title"],
            "description": theme["description"],
            "rows": [_metric_row(participants, metric) for metric in theme["metrics"]],
        })
    return blocks


def seconds_mean_sd(values: list[float | int | None]) -> str:
    summary = summarise(values)
    if not summary["n"]:
        return ""
    return f"{format_seconds(summary['mean'])} ({format_seconds(summary['sd'] or 0)})"


def time_to_sixth_creature_summary(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    for condition in LOG_REPORT_CONDITIONS:
        scoped = _scope_for_condition(participants, condition)
        for chapter in (1, 2, 3):
            key = f"time_to_sixth_creature_ch{chapter}_seconds"
            values = [participant.get(key) for participant in scoped]
            summary = summarise(values)
            rows.append({
                "condition": condition,
                "chapter": f"Ch{chapter}",
                "n": summary["n"],
                "total": len(scoped),
                "mean_sd": seconds_mean_sd(values),
                "min": format_seconds(summary["min"]),
                "max": format_seconds(summary["max"]),
            })
    return rows


def optional_pause_choice_patterns(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    patterns: Counter[str] = Counter()
    optional_participants = [participant for participant in participants if participant.get("condition") == "Optional pauses"]
    for participant in optional_participants:
        choices = [
            clean(choice.get("choice"))
            for choice in sorted(participant.get("manipulated_checkpoint_choices", []), key=lambda item: item.get("moment", ""))
            if clean(choice.get("choice"))
        ]
        patterns[" → ".join(choices) if choices else "No manipulated choice logged"] += 1
    return [{"pattern": pattern, "n": count} for pattern, count in patterns.most_common()]


def optional_pause_checkpoint_choices(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    for participant in participants:
        if participant.get("condition") != "Optional pauses":
            continue
        for choice in participant.get("manipulated_checkpoint_choices", []):
            rows.append({
                "participant_id": participant.get("participant_id", ""),
                "moment": choice.get("moment", ""),
                "choice": choice.get("choice", ""),
                "choice_time_label": choice.get("choice_time_label", ""),
                "choice_time_ms": choice.get("choice_time_ms"),
            })
    return rows


def _yes_no_summary(participants: list[dict[str, Any]], key: str, label: str) -> dict[str, Any]:
    row: dict[str, Any] = {"check": label}
    for condition in LOG_REPORT_CONDITIONS:
        scoped = _scope_for_condition(participants, condition)
        denominator = len(scoped)
        numerator = sum(1 for participant in scoped if bool(participant.get(key)))
        percentage = (100.0 * numerator / denominator) if denominator else None
        row[condition] = "—" if percentage is None else f"{numerator}/{denominator} ({percentage:.1f}%)"
    return row


def _categorical_count_rows(participants: list[dict[str, Any]], key: str, label_key: str) -> list[dict[str, Any]]:
    categories = sorted({clean(participant.get(key)) or "Missing / not set" for participant in participants})
    rows = []
    for category in categories:
        row: dict[str, Any] = {label_key: category}
        for condition in LOG_REPORT_CONDITIONS:
            scoped = _scope_for_condition(participants, condition)
            row[condition] = sum(1 for participant in scoped if (clean(participant.get(key)) or "Missing / not set") == category)
        rows.append(row)
    return rows


def build_game_log_report(participants: list[dict[str, Any]]) -> dict[str, Any]:
    """Build dense per-condition summaries for the Game logs tab."""
    log_rows = sorted((dict(participant) for participant in participants), key=lambda item: str(item.get("participant_id", "")))
    return {
        "theme_blocks": _theme_rows(participants),
        "visual_metrics": LOG_VISUAL_METRICS,
        "time_to_sixth_creature": time_to_sixth_creature_summary(participants),
        "optional_pause_choice_patterns": optional_pause_choice_patterns(participants),
        "checkpoint_choices": optional_pause_checkpoint_choices(participants),
        "logs": log_rows,
    }
