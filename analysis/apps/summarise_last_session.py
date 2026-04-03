from __future__ import annotations

import collections
import dataclasses
import datetime as dt
import html
import re
from pathlib import Path
from typing import Any

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

REPO_ROOT = Path(__file__).resolve().parents[2]
LOG_PATH = REPO_ROOT / "mods" / "custom" / "run" / "logs" / "study-checkpoints.log"
CREATURE_CARDS_PATH = (
    REPO_ROOT
    / "mods"
    / "custom"
    / "src"
    / "main"
    / "java"
    / "io"
    / "github"
    / "u01234567"
    / "studycheckpoints"
    / "StudyCreatureCards.java"
)
OUTPUT_DIR = REPO_ROOT / "analysis" / "output"
OUTPUT_PATH = OUTPUT_DIR / "last_session_summary.html"

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

CHAPTER_TITLES: dict[int, str] = {
    0: "Chapter 0 (Get started)",
    1: "Chapter 1 (The Museum)",
    2: "Chapter 2 (The Farm)",
    3: "Chapter 3 (The Forest)",
}
CHAPTER_CONSTANT_TO_NUMBER = {f"CHAPTER_{number}": number for number in CHAPTER_TITLES}
CHAPTER_TITLE_TO_NUMBER = {title: number for number, title in CHAPTER_TITLES.items()}

EVENT_LINE_RE = re.compile(
    r"^\[(?P<date>\d{4}-\d{2}-\d{2})\] "
    r"\[(?P<time>\d{2}:\d{2}:\d{2}:\d{3})\] \| "
    r"(?P<event>[^|]+?) \| "
    r"(?P<rest>.+)$"
)

QUOTED_STRING_RE = re.compile(r'"([^"]*)"')
CLICK_LIKE_BLOCKED_ACTIONS = {
    "attack_entity_blocked",
    "attack_block_blocked",
    "use_block_blocked",
}

COLOUR_DARK = "#28393B"
COLOUR_GREEN = "#107C41"
COLOUR_LIGHT_GREEN = "#A9D3BF"
COLOUR_RED = "#B42318"
COLOUR_LIGHT_RED = "#FEE4E2"

# ---------------------------------------------------------------------------
# Data classes
# ---------------------------------------------------------------------------


@dataclasses.dataclass(slots=True)
class ParsedEvent:
    """One parsed event line from the study log."""

    timestamp: dt.datetime
    event_type: str
    session_id: str
    fields: dict[str, str]
    raw_line: str


@dataclasses.dataclass(slots=True)
class CreatureSpawnDefinition:
    """One configured spawn within a creature card."""

    unique_name: str
    x: int
    y: int
    z: int
    facing: str

    def block_pos_text(self) -> str:
        """Return the configured block position as text."""
        return f"{self.x},{self.y},{self.z}"


@dataclasses.dataclass(slots=True)
class CreatureDefinition:
    """One species definition from StudyCreatureCards.java."""

    species_key: str
    species_identifier: str
    display_name: str
    chapter_number: int
    movement_mode: str
    facts: list[str]
    spawns: list[CreatureSpawnDefinition]


@dataclasses.dataclass(slots=True)
class ClickRecord:
    """One creature-card click during the session."""

    click_number: int
    timestamp: dt.datetime
    chapter_number: int
    species_key: str
    creature_name: str
    creature_label: str
    entity_uuid: str
    entity_block_pos: str
    interacted_before: bool
    wrong_chapter: bool


@dataclasses.dataclass(slots=True)
class SpawnRecord:
    """One study_creature_spawned event."""

    timestamp: dt.datetime
    species_key: str
    creature_name: str
    creature_label: str
    entity_uuid: str
    chapter_number: int
    entity_block_pos: str
    facing: str
    movement_mode: str
    success: bool


@dataclasses.dataclass(slots=True)
class SpeciesSpawnCheck:
    """Global test: did all configured creature names for one species spawn?"""

    species_key: str
    expected_names: list[str]
    successful_names: list[str]

    def missing_names(self) -> list[str]:
        """Return expected creature names that never spawned successfully."""
        successful = set(self.successful_names)
        return [name for name in self.expected_names if name not in successful]

    def passed(self) -> bool:
        """Return whether every configured creature name spawned successfully."""
        return not self.missing_names()


@dataclasses.dataclass(slots=True)
class NameUuidCheck:
    """Global test: was one creature name assigned to multiple UUIDs?"""

    creature_name: str
    uuids: list[str]

    def passed(self) -> bool:
        """Return whether the creature name maps to zero or one UUID."""
        return len(self.uuids) <= 1


@dataclasses.dataclass(slots=True)
class ChapterSummary:
    """Accumulated summary for one chapter."""

    chapter_number: int
    title: str
    started_at: dt.datetime | None = None
    completed_at: dt.datetime | None = None

    movement_samples: int = 0
    latest_total_distance: float = 0.0
    latest_total_sprint_distance: float = 0.0
    summed_sample_distance: float = 0.0
    jumps: int = 0

    checkpoint_displayed_at: dt.datetime | None = None
    checkpoint_prompt_displayed: bool = False
    checkpoint_condition: str | None = None
    checkpoint_choice: str | None = None
    checkpoint_choice_at: dt.datetime | None = None
    checkpoint_pause_started_at: dt.datetime | None = None
    checkpoint_pause_finished_at: dt.datetime | None = None

    blocked_actions: collections.Counter[str] = dataclasses.field(
        default_factory=collections.Counter
    )
    click_records: list[ClickRecord] = dataclasses.field(default_factory=list)

    def allowed_clicks(self) -> int:
        """Return click count on creatures that belong to this chapter."""
        return sum(1 for record in self.click_records if not record.wrong_chapter)

    def wrong_chapter_clicks(self) -> int:
        """Return click count on creatures that do not belong to this chapter."""
        return sum(1 for record in self.click_records if record.wrong_chapter)

    def blocked_clicks(self) -> int:
        """Return blocked click-like actions in this chapter."""
        return sum(
            self.blocked_actions.get(action, 0)
            for action in CLICK_LIKE_BLOCKED_ACTIONS
        )

    def total_blocked_actions(self) -> int:
        """Return all blocked actions in this chapter."""
        return sum(self.blocked_actions.values())

    def unique_creatures_found(self) -> int:
        """Return number of uniquely named creatures clicked in this chapter."""
        return len({record.creature_name for record in self.click_records})

    def checkpoint_button_mode(self) -> str:
        """Describe the checkpoint button mode."""
        if self.chapter_number == 0:
            return "Only one button"
        if self.checkpoint_condition == "cond_choice":
            return "Choice between pause and continue"
        if self.checkpoint_condition in {"cond_continue", "cond_pause"}:
            return "Only one button"
        return "Unknown"

    def checkpoint_response_seconds(self) -> float | None:
        """Return checkpoint response time when both timestamps exist."""
        if self.checkpoint_displayed_at is None or self.checkpoint_choice_at is None:
            return None
        return (self.checkpoint_choice_at - self.checkpoint_displayed_at).total_seconds()


@dataclasses.dataclass(slots=True)
class SessionSummary:
    """Summary of the last recorded session."""

    session_id: str
    started_at: dt.datetime
    ended_at: dt.datetime
    condition: str | None
    condition_source: str | None
    player_name: str | None
    questionnaire_clicks: int
    events: list[ParsedEvent]
    chapters: dict[int, ChapterSummary]
    all_creatures: list[CreatureDefinition]
    spawn_records: list[SpawnRecord]
    spawn_checks: list[SpeciesSpawnCheck]
    name_uuid_checks: list[NameUuidCheck]

    def total_duration(self) -> dt.timedelta:
        """Return the session duration."""
        return self.ended_at - self.started_at

    def total_allowed_clicks(self) -> int:
        """Return total allowed clicks across chapters."""
        return sum(chapter.allowed_clicks() for chapter in self.chapters.values())

    def total_wrong_chapter_clicks(self) -> int:
        """Return total wrong-chapter clicks across chapters."""
        return sum(
            chapter.wrong_chapter_clicks() for chapter in self.chapters.values()
        )

    def total_blocked_clicks(self) -> int:
        """Return total blocked click-like actions across chapters."""
        return sum(chapter.blocked_clicks() for chapter in self.chapters.values())

    def total_blocked_actions(self) -> int:
        """Return total blocked actions across chapters."""
        return sum(
            chapter.total_blocked_actions() for chapter in self.chapters.values()
        )

    def total_unique_creatures_found(self) -> int:
        """Return unique creature names clicked across the whole session."""
        return len(
            {
                record.creature_name
                for chapter in self.chapters.values()
                for record in chapter.click_records
            }
        )

    def passed_spawn_check_count(self) -> int:
        """Return how many species passed the spawn completeness test."""
        return sum(1 for check in self.spawn_checks if check.passed())

    def failed_spawn_check_count(self) -> int:
        """Return how many species failed the spawn completeness test."""
        return sum(1 for check in self.spawn_checks if not check.passed())

    def duplicate_name_uuid_count(self) -> int:
        """Return how many creature names map to multiple UUIDs."""
        return sum(1 for check in self.name_uuid_checks if not check.passed())


# ---------------------------------------------------------------------------
# Public entry point
# ---------------------------------------------------------------------------


def main() -> int:
    """
    Build a summary for the last session and export it as HTML.

    Expected command:
        python main.py sum_last
    """
    if not LOG_PATH.exists():
        print(f"No study log found yet: {LOG_PATH}")
        print("Run the experiment locally first so the log file is created.")
        return 1

    if not CREATURE_CARDS_PATH.exists():
        print(f"Creature card file not found: {CREATURE_CARDS_PATH}")
        return 1

    creatures = parse_creature_cards(CREATURE_CARDS_PATH)
    if not creatures:
        print(
            "Could not parse any creature cards from StudyCreatureCards.java. "
            "Check the file format first."
        )
        return 1

    events = parse_log_file(LOG_PATH)
    if not events:
        print("No parseable session events were found in the study log.")
        return 1

    session = build_last_session_summary(events, creatures)
    if session is None:
        print("Could not identify a last session in the study log.")
        return 1

    html_text = render_session_html(session)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(html_text, encoding="utf-8")

    print(f"Summary written to: {OUTPUT_PATH}")
    return 0


# ---------------------------------------------------------------------------
# Parsing helpers
# ---------------------------------------------------------------------------


def parse_creature_cards(path: Path) -> list[CreatureDefinition]:
    """Parse the current StudyCreatureCards.java structure."""
    text = path.read_text(encoding="utf-8")
    creatures: list[CreatureDefinition] = []

    for block in extract_map_entry_blocks(text):
        block_match = re.match(
            r'Map\.entry\s*\(\s*(?P<key>EntityType\.[A-Z0-9_]+|"[^"]+")\s*,\s*'
            r'new\s+CreatureCard\s*\((?P<args>.*)\)\s*\)\s*$',
            block,
            flags=re.DOTALL,
        )
        if not block_match:
            continue

        key_text = block_match.group("key").strip()
        args = split_top_level_commas(block_match.group("args"))
        if len(args) != 5:
            continue

        if key_text.startswith("EntityType."):
            species_identifier = key_text.removeprefix("EntityType.")
            species_key = species_identifier
        else:
            species_identifier = strip_java_string(key_text)
            species_key = species_identifier.upper()

        display_name = strip_java_string(args[0])
        chapter_constant = args[1].strip().split(".")[-1]
        movement_mode = args[2].strip().split(".")[-1]
        facts = [
            fact.strip()
            for fact in QUOTED_STRING_RE.findall(args[3])
            if fact.strip()
        ]
        spawns = parse_spawn_list(args[4])

        chapter_number = CHAPTER_CONSTANT_TO_NUMBER.get(chapter_constant)
        if chapter_number is None:
            raise ValueError(f"Unknown chapter constant: {chapter_constant}")

        creatures.append(
            CreatureDefinition(
                species_key=species_key,
                species_identifier=species_identifier,
                display_name=display_name,
                chapter_number=chapter_number,
                movement_mode=movement_mode,
                facts=facts,
                spawns=spawns,
            )
        )

    return creatures


def parse_spawn_list(list_expression: str) -> list[CreatureSpawnDefinition]:
    """Parse the List.of(...) expression holding CreatureSpawn entries."""
    list_expression = list_expression.strip()
    list_open = list_expression.find("(")
    list_close = list_expression.rfind(")")
    if list_open < 0 or list_close < 0 or list_close <= list_open:
        raise ValueError(f"Could not parse spawn list: {list_expression}")

    inner = list_expression[list_open + 1:list_close].strip()
    if not inner:
        return []

    return [parse_creature_spawn(part) for part in split_top_level_commas(inner)]


def parse_creature_spawn(text: str) -> CreatureSpawnDefinition:
    """Parse one new CreatureSpawn(...) expression."""
    text = text.strip()
    prefix = "new CreatureSpawn("
    if not text.startswith(prefix) or not text.endswith(")"):
        raise ValueError(f"Could not parse CreatureSpawn: {text}")

    inner = text[len(prefix):-1]
    args = split_top_level_commas(inner)
    if len(args) != 5:
        raise ValueError(f"Expected 5 CreatureSpawn args, found {len(args)}: {text}")

    return CreatureSpawnDefinition(
        unique_name=strip_java_string(args[0]),
        x=int(args[1].strip()),
        y=int(args[2].strip()),
        z=int(args[3].strip()),
        facing=args[4].strip().split(".")[-1],
    )


def extract_map_entry_blocks(text: str) -> list[str]:
    """Return full Map.entry(...) blocks from the Java source."""
    blocks: list[str] = []
    search_start = 0
    needle = "Map.entry("

    while True:
        start = text.find(needle, search_start)
        if start < 0:
            break

        open_index = text.find("(", start)
        end = find_matching_parenthesis(text, open_index)
        blocks.append(text[start:end + 1])
        search_start = end + 1

    return blocks


def find_matching_parenthesis(text: str, open_index: int) -> int:
    """Return the index of the matching closing parenthesis."""
    depth = 0
    in_string = False
    escape_next = False

    for index in range(open_index, len(text)):
        character = text[index]

        if in_string:
            if escape_next:
                escape_next = False
            elif character == "\\":
                escape_next = True
            elif character == '"':
                in_string = False
            continue

        if character == '"':
            in_string = True
            continue

        if character == "(":
            depth += 1
        elif character == ")":
            depth -= 1
            if depth == 0:
                return index

    raise ValueError("Unbalanced parentheses while parsing StudyCreatureCards.java")


def split_top_level_commas(text: str) -> list[str]:
    """Split a comma-separated expression without splitting nested calls."""
    parts: list[str] = []
    start = 0
    depth = 0
    in_string = False
    escape_next = False

    for index, character in enumerate(text):
        if in_string:
            if escape_next:
                escape_next = False
            elif character == "\\":
                escape_next = True
            elif character == '"':
                in_string = False
            continue

        if character == '"':
            in_string = True
            continue

        if character in "([{":
            depth += 1
            continue

        if character in ")]}":
            depth -= 1
            continue

        if character == "," and depth == 0:
            parts.append(text[start:index].strip())
            start = index + 1

    trailing = text[start:].strip()
    if trailing:
        parts.append(trailing)

    return parts


def strip_java_string(text: str) -> str:
    """Remove surrounding double quotes when present."""
    stripped = text.strip()
    if stripped.startswith('"') and stripped.endswith('"'):
        return stripped[1:-1]
    return stripped


def parse_log_file(path: Path) -> list[ParsedEvent]:
    """Parse the main study log into structured events."""
    events: list[ParsedEvent] = []

    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line:
            continue

        match = EVENT_LINE_RE.match(line)
        if not match:
            continue

        timestamp = dt.datetime.strptime(
            f"{match.group('date')} {match.group('time')}",
            "%Y-%m-%d %H:%M:%S:%f",
        )
        event_type = match.group("event").strip()
        rest = match.group("rest").strip()

        fields = parse_key_value_fields(rest)
        session_id = fields.get("session_id", "")

        if not session_id:
            continue

        events.append(
            ParsedEvent(
                timestamp=timestamp,
                event_type=event_type,
                session_id=session_id,
                fields=fields,
                raw_line=raw_line,
            )
        )

    return events


def parse_key_value_fields(rest: str) -> dict[str, str]:
    """Parse the trailing key=value fields from one log line."""
    result: dict[str, str] = {}

    for part in rest.split(" | "):
        if "=" not in part:
            continue
        key, value = part.split("=", 1)
        result[key.strip()] = value.strip()

    return result


# ---------------------------------------------------------------------------
# Session summarisation
# ---------------------------------------------------------------------------


def build_last_session_summary(
    events: list[ParsedEvent],
    creatures: list[CreatureDefinition],
) -> SessionSummary | None:
    """Build a summary for the most recent session in the log."""
    session_start_events = [
        event for event in events if event.event_type == "study_session_started"
    ]

    if session_start_events:
        last_session_id = session_start_events[-1].session_id
    else:
        last_session_id = events[-1].session_id if events else ""

    if not last_session_id:
        return None

    session_events = [event for event in events if event.session_id == last_session_id]
    if not session_events:
        return None

    chapters: dict[int, ChapterSummary] = {
        chapter_number: ChapterSummary(chapter_number=chapter_number, title=title)
        for chapter_number, title in CHAPTER_TITLES.items()
    }

    creature_by_name: dict[str, CreatureDefinition] = {}
    for creature in creatures:
        for spawn in creature.spawns:
            creature_by_name[spawn.unique_name] = creature

    condition: str | None = None
    condition_source: str | None = None
    player_name: str | None = None
    questionnaire_clicks = 0

    current_active_chapter: int | None = None
    click_number = 0

    spawn_records: list[SpawnRecord] = []
    successful_names_by_species: dict[str, list[str]] = collections.defaultdict(list)
    successful_uuids_by_name: dict[str, set[str]] = collections.defaultdict(set)

    for event in session_events:
        if player_name is None and "player" in event.fields:
            player_name = event.fields.get("player")

        if event.event_type == "experiment_condition_assigned":
            condition = event.fields.get("condition")
            condition_source = event.fields.get("source")

        elif event.event_type == "chapter_started":
            chapter_number = safe_int(event.fields.get("chapter"))
            if chapter_number is not None and chapter_number in chapters:
                current_active_chapter = chapter_number
                chapters[chapter_number].started_at = event.timestamp

        elif event.event_type == "chapter_completed":
            chapter_number = safe_int(event.fields.get("chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].completed_at = event.timestamp
                current_active_chapter = None

        elif event.event_type == "study_creature_spawned":
            creature_name = event.fields.get("creature_name", "unknown")
            configured_creature = creature_by_name.get(creature_name)
            species_key = (
                configured_creature.species_key
                if configured_creature is not None
                else event.fields.get("entity_type", "UNKNOWN").upper()
            )
            chapter_number = CHAPTER_TITLE_TO_NUMBER.get(
                event.fields.get("chapter_title", "")
            )
            success = event.fields.get("success", "").lower() == "true"

            spawn_record = SpawnRecord(
                timestamp=event.timestamp,
                species_key=species_key,
                creature_name=creature_name,
                creature_label=event.fields.get("creature_label", "Unknown"),
                entity_uuid=event.fields.get("entity_uuid", "unknown"),
                chapter_number=chapter_number if chapter_number is not None else -1,
                entity_block_pos=event.fields.get("entity_block_pos", "unknown"),
                facing=event.fields.get("facing", "unknown"),
                movement_mode=event.fields.get("movement_mode", "unknown"),
                success=success,
            )
            spawn_records.append(spawn_record)

            if success:
                successful_names_by_species[species_key].append(creature_name)
                successful_uuids_by_name[creature_name].add(spawn_record.entity_uuid)

        elif event.event_type == "creature_card_opened":
            if current_active_chapter is None:
                continue

            click_number += 1
            creature_name = event.fields.get("creature_name", "unknown")
            configured_creature = creature_by_name.get(creature_name)
            species_key = (
                configured_creature.species_key
                if configured_creature is not None
                else event.fields.get("entity_type", "UNKNOWN").upper()
            )
            configured_chapter = (
                configured_creature.chapter_number
                if configured_creature is not None
                else None
            )
            wrong_chapter = (
                configured_chapter is not None
                and configured_chapter != current_active_chapter
            )

            chapters[current_active_chapter].click_records.append(
                ClickRecord(
                    click_number=click_number,
                    timestamp=event.timestamp,
                    chapter_number=current_active_chapter,
                    species_key=species_key,
                    creature_name=creature_name,
                    creature_label=event.fields.get("creature_label", "Unknown"),
                    entity_uuid=event.fields.get("entity_uuid", "unknown"),
                    entity_block_pos=event.fields.get("entity_block_pos", "unknown"),
                    interacted_before=event.fields.get("interacted_before", "").lower()
                    == "true",
                    wrong_chapter=wrong_chapter,
                )
            )

        elif event.event_type == "blocked_action":
            if current_active_chapter is not None:
                action = event.fields.get("action", "unknown")
                chapters[current_active_chapter].blocked_actions[action] += 1

        elif event.event_type == "movement_sample":
            chapter_number = CHAPTER_TITLE_TO_NUMBER.get(
                event.fields.get("chapter_title", "")
            )
            if chapter_number is not None:
                chapter = chapters[chapter_number]
                chapter.movement_samples += 1
                chapter.latest_total_distance = (
                    safe_float(event.fields.get("total_distance"))
                    or chapter.latest_total_distance
                )
                chapter.latest_total_sprint_distance = (
                    safe_float(event.fields.get("total_sprint_distance"))
                    or chapter.latest_total_sprint_distance
                )
                chapter.summed_sample_distance += (
                    safe_float(event.fields.get("sample_distance")) or 0.0
                )

        elif event.event_type == "jump_started":
            chapter_number = CHAPTER_TITLE_TO_NUMBER.get(
                event.fields.get("chapter_title", "")
            )
            if chapter_number is not None:
                chapters[chapter_number].jumps += 1

        elif event.event_type == "checkpoint_displayed":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_displayed_at = event.timestamp
                chapters[chapter_number].checkpoint_condition = event.fields.get(
                    "condition"
                )

        elif event.event_type == "checkpoint_prompt_displayed":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_prompt_displayed = True

        elif event.event_type == "checkpoint_choice_made":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_choice = event.fields.get("choice")
                chapters[chapter_number].checkpoint_choice_at = event.timestamp
                if chapters[chapter_number].checkpoint_condition is None:
                    chapters[chapter_number].checkpoint_condition = event.fields.get(
                        "condition"
                    )

        elif event.event_type == "checkpoint_pause_started":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_pause_started_at = event.timestamp

        elif event.event_type == "checkpoint_pause_finished":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_pause_finished_at = event.timestamp

        elif event.event_type == "questionnaire_button_pressed":
            questionnaire_clicks += 1

    spawn_checks = [
        SpeciesSpawnCheck(
            species_key=creature.species_key,
            expected_names=[spawn.unique_name for spawn in creature.spawns],
            successful_names=list(
                dict.fromkeys(successful_names_by_species.get(creature.species_key, []))
            ),
        )
        for creature in creatures
    ]

    name_uuid_checks = [
        NameUuidCheck(creature_name=name, uuids=sorted(uuids))
        for name, uuids in sorted(successful_uuids_by_name.items())
    ]
    seen_check_names = {check.creature_name for check in name_uuid_checks}
    for creature in creatures:
        for spawn in creature.spawns:
            if spawn.unique_name not in seen_check_names:
                name_uuid_checks.append(
                    NameUuidCheck(creature_name=spawn.unique_name, uuids=[])
                )
    name_uuid_checks.sort(key=lambda item: item.creature_name)

    return SessionSummary(
        session_id=last_session_id,
        started_at=session_events[0].timestamp,
        ended_at=session_events[-1].timestamp,
        condition=condition,
        condition_source=condition_source,
        player_name=player_name,
        questionnaire_clicks=questionnaire_clicks,
        events=session_events,
        chapters=chapters,
        all_creatures=creatures,
        spawn_records=spawn_records,
        spawn_checks=spawn_checks,
        name_uuid_checks=name_uuid_checks,
    )


# ---------------------------------------------------------------------------
# HTML rendering
# ---------------------------------------------------------------------------


def render_session_html(session: SessionSummary) -> str:
    """Render the session summary as a small self-contained HTML page."""
    toc_items = [
        '<li><a href="#overview">Session overview</a></li>',
        '<li><a href="#global-checks">Global creature checks</a></li>',
        *[
            f'<li><a href="#chapter-{chapter_number}">{escape(session.chapters[chapter_number].title)}</a></li>'
            for chapter_number in sorted(session.chapters)
        ],
    ]

    chapter_sections = "\n".join(
        render_chapter_section(session, session.chapters[chapter_number])
        for chapter_number in sorted(session.chapters)
    )

    spawn_check_rows = "\n".join(
        render_spawn_check_row(check)
        for check in session.spawn_checks
    )

    duplicate_rows = "\n".join(
        render_name_uuid_row(check)
        for check in session.name_uuid_checks
    )

    return f"""<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Last Study Session Summary</title>
    <style>
        :root {{
            --colour-dark: {COLOUR_DARK};
            --colour-green: {COLOUR_GREEN};
            --colour-light-green: {COLOUR_LIGHT_GREEN};
            --colour-red: {COLOUR_RED};
            --colour-light-red: {COLOUR_LIGHT_RED};
            --colour-white: #ffffff;
            --colour-bg: #f5f7f7;
            --colour-border: #d7e2de;
            --colour-text: #1b2627;
            --colour-muted: #5b6b6c;
            --shadow-soft: 0 10px 28px rgba(40, 57, 59, 0.08);
            --radius: 12px;
            --sidebar-width: 300px;
        }}

        * {{
            box-sizing: border-box;
        }}

        html {{
            scroll-behaviour: smooth;
        }}

        body {{
            margin: 0;
            font-family: Arial, sans-serif;
            line-height: 1.5;
            color: var(--colour-text);
            background: linear-gradient(180deg, #fbfcfc 0%, var(--colour-bg) 100%);
        }}

        a {{
            color: var(--colour-green);
            text-decoration: none;
        }}

        a:hover {{
            text-decoration: underline;
        }}

        .layout {{
            display: block;
            max-width: 1700px;
            margin: 0 auto;
        }}

        .sidebar {{
            background: var(--colour-dark);
            color: var(--colour-white);
            padding: 22px 18px;
        }}

        .sidebar h2 {{
            margin: 0 0 0.7rem 0;
            font-size: 1.05rem;
        }}

        .sidebar p {{
            margin: 0 0 1rem 0;
            color: rgba(255, 255, 255, 0.82);
            font-size: 0.95rem;
        }}

        .sidebar ul {{
            list-style: none;
            margin: 0;
            padding: 0;
        }}

        .sidebar li {{
            margin: 0 0 0.45rem 0;
        }}

        .sidebar a {{
            display: block;
            padding: 0.5rem 0.65rem;
            border-radius: 8px;
            color: var(--colour-white);
            background: rgba(255, 255, 255, 0.06);
        }}

        .sidebar a:hover {{
            text-decoration: none;
            background: rgba(169, 211, 191, 0.22);
        }}

        .content {{
            padding: 22px;
        }}

        .page-header {{
            margin-bottom: 20px;
        }}

        .page-header h1 {{
            margin: 0;
            color: var(--colour-dark);
            font-size: 1.9rem;
        }}

        .page-header p {{
            margin: 0.45rem 0 0 0;
            color: var(--colour-muted);
        }}

        .card {{
            background: var(--colour-white);
            border: 1px solid var(--colour-border);
            border-radius: var(--radius);
            padding: 18px;
            margin-bottom: 18px;
            box-shadow: var(--shadow-soft);
        }}

        .card h2 {{
            margin-top: 0;
            margin-bottom: 0.75rem;
            color: var(--colour-dark);
        }}

        .card h3 {{
            margin-top: 1.2rem;
            margin-bottom: 0.55rem;
            color: var(--colour-green);
        }}

        .muted {{
            color: var(--colour-muted);
        }}

        .highlight-bar {{
            height: 6px;
            border-radius: 999px;
            background: linear-gradient(90deg, var(--colour-green), var(--colour-light-green));
            margin-bottom: 14px;
        }}

        .meta-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 10px;
            margin-bottom: 16px;
        }}

        .meta-pill {{
            border: 1px solid var(--colour-border);
            background: #f8fcfa;
            border-radius: 10px;
            padding: 10px 12px;
        }}

        .meta-pill .label {{
            display: block;
            font-size: 0.82rem;
            color: var(--colour-muted);
            margin-bottom: 0.25rem;
        }}

        .meta-pill .value {{
            display: block;
            font-weight: 700;
            color: var(--colour-dark);
        }}

        table {{
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            margin-top: 10px;
            background: var(--colour-white);
        }}

        .two-col-table col.col-key {{
            width: 36%;
        }}

        .two-col-table col.col-value {{
            width: 64%;
        }}

        th, td {{
            border: 1px solid var(--colour-border);
            padding: 9px 10px;
            text-align: left;
            vertical-align: top;
            word-wrap: break-word;
            overflow-wrap: break-word;
        }}

        th {{
            background: #eef7f2;
            color: var(--colour-dark);
        }}

        tr:nth-child(even) td {{
            background: #fcfefd;
        }}

        .status-pass {{
            color: var(--colour-green);
            font-weight: 700;
        }}

        .status-fail {{
            color: var(--colour-red);
            font-weight: 700;
        }}

        .species-row td:first-child {{
            font-weight: 700;
            background: #f3faf6;
        }}

        .child-row td:first-child {{
            padding-left: 28px;
        }}

        .count-bad {{
            color: var(--colour-red);
            font-weight: 700;
        }}

        .badge {{
            display: inline-block;
            padding: 3px 7px;
            margin: 2px 4px 2px 0;
            border-radius: 999px;
            border: 1px solid var(--colour-border);
            background: #f5faf7;
            white-space: nowrap;
            font-size: 0.92rem;
        }}

        .badge.bad {{
            border-color: #f0b8b4;
            background: var(--colour-light-red);
            color: var(--colour-red);
            font-weight: 700;
        }}

        .badge.good {{
            border-color: #c9e7d7;
            background: #edf9f1;
            color: var(--colour-green);
        }}

        code {{
            background: #eef2f2;
            padding: 2px 6px;
            border-radius: 6px;
        }}

        .section-anchor {{
            scroll-margin-top: 20px;
        }}

        @media (min-width: 1100px) {{
            .layout {{
                display: grid;
                grid-template-columns: var(--sidebar-width) minmax(0, 1fr);
                gap: 0;
                align-items: start;
            }}

            .sidebar {{
                min-height: 100vh;
                position: sticky;
                top: 0;
            }}

            .content {{
                padding: 28px;
            }}
        }}

        @media (max-width: 1099px) {{
            .sidebar {{
                border-bottom: 4px solid var(--colour-light-green);
            }}
        }}
    </style>
</head>
<body>
    <div class="layout">
        <aside class="sidebar">
            <h2>Contents</h2>
            <p>Navigate through the last recorded study session.</p>
            <ul>
                {"".join(toc_items)}
            </ul>
        </aside>

        <main class="content">
            <header class="page-header">
                <h1>Last Study Session Summary</h1>
                <p>
                    A chapter-aware local report generated from the latest study log
                    and the configured creature roster.
                </p>
            </header>

            <section id="overview" class="card section-anchor">
                <div class="highlight-bar"></div>
                <h2>Session overview</h2>

                <div class="meta-grid">
                    <div class="meta-pill">
                        <span class="label">Session ID</span>
                        <span class="value"><code>{escape(session.session_id)}</code></span>
                    </div>
                    <div class="meta-pill">
                        <span class="label">Condition</span>
                        <span class="value">{escape(session.condition or "Unknown")}</span>
                    </div>
                    <div class="meta-pill">
                        <span class="label">Condition source</span>
                        <span class="value">{escape(session.condition_source or "Unknown")}</span>
                    </div>
                    <div class="meta-pill">
                        <span class="label">Player</span>
                        <span class="value">{escape(session.player_name or "Unknown")}</span>
                    </div>
                </div>

                <table class="two-col-table">
                    <colgroup>
                        <col class="col-key">
                        <col class="col-value">
                    </colgroup>
                    <tr><th>Started</th><td>{escape(format_datetime(session.started_at))}</td></tr>
                    <tr><th>Ended</th><td>{escape(format_datetime(session.ended_at))}</td></tr>
                    <tr><th>Total duration</th><td>{escape(format_timedelta(session.total_duration()))}</td></tr>
                    <tr><th>Total configured species</th><td>{len(session.all_creatures)}</td></tr>
                    <tr><th>Total configured named creatures</th><td>{sum(len(creature.spawns) for creature in session.all_creatures)}</td></tr>
                    <tr><th>Total unique named creatures clicked</th><td>{session.total_unique_creatures_found()}</td></tr>
                    <tr><th>Total allowed clicks</th><td>{session.total_allowed_clicks()}</td></tr>
                    <tr><th>Total wrong-chapter clicks</th><td>{session.total_wrong_chapter_clicks()}</td></tr>
                    <tr><th>Total blocked click-like actions</th><td>{session.total_blocked_clicks()}</td></tr>
                    <tr><th>Total blocked actions</th><td>{session.total_blocked_actions()}</td></tr>
                    <tr><th>Questionnaire button clicks</th><td>{session.questionnaire_clicks}</td></tr>
                    <tr><th>Total parsed events</th><td>{len(session.events)}</td></tr>
                </table>
            </section>

            <section id="global-checks" class="card section-anchor">
                <div class="highlight-bar"></div>
                <h2>Global creature checks</h2>

                <table class="two-col-table">
                    <colgroup>
                        <col class="col-key">
                        <col class="col-value">
                    </colgroup>
                    <tr>
                        <th>All configured named creatures spawned</th>
                        <td>
                            <span class="{'status-pass' if session.failed_spawn_check_count() == 0 else 'status-fail'}">
                                {'PASS' if session.failed_spawn_check_count() == 0 else 'FAIL'}
                            </span>
                            — {session.passed_spawn_check_count()} species passed, {session.failed_spawn_check_count()} species failed
                        </td>
                    </tr>
                    <tr>
                        <th>No creature name assigned to multiple UUIDs</th>
                        <td>
                            <span class="{'status-pass' if session.duplicate_name_uuid_count() == 0 else 'status-fail'}">
                                {'PASS' if session.duplicate_name_uuid_count() == 0 else 'FAIL'}
                            </span>
                            — {session.duplicate_name_uuid_count()} duplicate-name issue(s)
                        </td>
                    </tr>
                </table>

                <h3>Spawn completeness by species</h3>
                <table>
                    <tr>
                        <th>Species</th>
                        <th>Expected names</th>
                        <th>Successfully spawned names</th>
                        <th>Missing names</th>
                        <th>Result</th>
                    </tr>
                    {spawn_check_rows}
                </table>

                <h3>UUID uniqueness by creature name</h3>
                <table>
                    <tr>
                        <th>Creature name</th>
                        <th>UUIDs seen in successful spawn events</th>
                        <th>Result</th>
                    </tr>
                    {duplicate_rows}
                </table>
            </section>

            {chapter_sections}
        </main>
    </div>
</body>
</html>
"""


def render_spawn_check_row(check: SpeciesSpawnCheck) -> str:
    """Render one row of the global spawn completeness table."""
    missing = check.missing_names()
    return (
        f"<tr>"
        f"<td><code>{escape(check.species_key)}</code></td>"
        f"<td>{escape(', '.join(check.expected_names) or 'None')}</td>"
        f"<td>{escape(', '.join(check.successful_names) or 'None')}</td>"
        f"<td>{escape(', '.join(missing) or 'None')}</td>"
        f"<td><span class=\"{'status-pass' if check.passed() else 'status-fail'}\">"
        f"{'PASS' if check.passed() else 'FAIL'}</span></td>"
        f"</tr>"
    )


def render_name_uuid_row(check: NameUuidCheck) -> str:
    """Render one row of the name-to-UUID uniqueness table."""
    uuid_text = ", ".join(check.uuids) if check.uuids else "No successful spawn logged"
    return (
        f"<tr>"
        f"<td><code>{escape(check.creature_name)}</code></td>"
        f"<td>{escape(uuid_text)}</td>"
        f"<td><span class=\"{'status-pass' if check.passed() else 'status-fail'}\">"
        f"{'PASS' if check.passed() else 'FAIL'}</span></td>"
        f"</tr>"
    )


def render_chapter_section(
    session: SessionSummary,
    chapter: ChapterSummary,
) -> str:
    """Render one chapter block."""
    checkpoint_response = chapter.checkpoint_response_seconds()
    checkpoint_response_text = (
        f"{checkpoint_response:.1f} seconds"
        if checkpoint_response is not None
        else "Unknown"
    )

    creature_rows = render_chapter_creature_rows(session, chapter)

    blocked_rows = ""
    if chapter.blocked_actions:
        blocked_rows = "\n".join(
            f"<tr><td>{escape(action)}</td><td>{count}</td></tr>"
            for action, count in chapter.blocked_actions.most_common()
        )
    else:
        blocked_rows = "<tr><td colspan='2'>No blocked actions recorded.</td></tr>"

    return f"""
    <section id="chapter-{chapter.chapter_number}" class="card section-anchor">
        <div class="highlight-bar"></div>
        <h2>{escape(chapter.title)}</h2>

        <table class="two-col-table">
            <colgroup>
                <col class="col-key">
                <col class="col-value">
            </colgroup>
            <tr><th>Started</th><td>{escape(format_datetime(chapter.started_at))}</td></tr>
            <tr><th>Completed</th><td>{escape(format_datetime(chapter.completed_at))}</td></tr>
            <tr><th>Unique named creatures clicked</th><td>{chapter.unique_creatures_found()}</td></tr>
            <tr><th>Allowed clicks</th><td>{chapter.allowed_clicks()}</td></tr>
            <tr><th>Wrong-chapter clicks</th><td>{chapter.wrong_chapter_clicks()}</td></tr>
            <tr><th>Blocked click-like actions</th><td>{chapter.blocked_clicks()}</td></tr>
            <tr><th>Total blocked actions</th><td>{chapter.total_blocked_actions()}</td></tr>
            <tr><th>Movement samples</th><td>{chapter.movement_samples}</td></tr>
            <tr><th>Distance travelled (approx. blocks)</th><td>{chapter.latest_total_distance:.2f}</td></tr>
            <tr><th>Sprint distance (approx. blocks)</th><td>{chapter.latest_total_sprint_distance:.2f}</td></tr>
            <tr><th>Summed sample distance</th><td>{chapter.summed_sample_distance:.2f}</td></tr>
            <tr><th>Jumps</th><td>{chapter.jumps}</td></tr>
            <tr><th>Checkpoint condition</th><td>{escape(chapter.checkpoint_condition or 'Not applicable / unknown')}</td></tr>
            <tr><th>Checkpoint button mode</th><td>{escape(chapter.checkpoint_button_mode())}</td></tr>
            <tr><th>Checkpoint choice</th><td>{escape(chapter.checkpoint_choice or 'No choice logged')}</td></tr>
            <tr><th>Checkpoint response time</th><td>{escape(checkpoint_response_text)}</td></tr>
            <tr><th>Reminder shown</th><td>{"Yes" if chapter.checkpoint_prompt_displayed else "No"}</td></tr>
        </table>

        <h3>Creature clicks in this chapter</h3>
        <table>
            <tr>
                <th>Entry</th>
                <th>Configured chapter</th>
                <th>Configured position / mode</th>
                <th>Click count</th>
                <th>Clicks</th>
            </tr>
            {creature_rows}
        </table>

        <h3>Blocked actions</h3>
        <table>
            <tr><th>Action</th><th>Count</th></tr>
            {blocked_rows}
        </table>
    </section>
    """


def render_chapter_creature_rows(session: SessionSummary, chapter: ChapterSummary) -> str:
    """Render grouped species rows and named-creature rows for one chapter."""
    rows: list[str] = []

    for creature in session.all_creatures:
        species_clicks = [
            record
            for record in chapter.click_records
            if record.species_key == creature.species_key
        ]
        species_wrong = any(record.wrong_chapter for record in species_clicks)
        species_count_class = "count-bad" if species_wrong else ""
        rows.append(
            "<tr class=\"species-row\">"
            f"<td><code>{escape(creature.species_key)}</code> — {escape(creature.display_name)}</td>"
            f"<td>{escape(CHAPTER_TITLES.get(creature.chapter_number, 'Unknown'))}</td>"
            f"<td>{escape(creature.movement_mode)}</td>"
            f"<td class=\"{species_count_class}\">{len(species_clicks)}</td>"
            f"<td>{render_click_badges(species_clicks)}</td>"
            "</tr>"
        )

        for spawn in creature.spawns:
            named_clicks = [
                record
                for record in chapter.click_records
                if record.creature_name == spawn.unique_name
            ]
            named_wrong = any(record.wrong_chapter for record in named_clicks)
            named_count_class = "count-bad" if named_wrong else ""
            rows.append(
                "<tr class=\"child-row\">"
                f"<td><code>{escape(spawn.unique_name)}</code></td>"
                f"<td>{escape(CHAPTER_TITLES.get(creature.chapter_number, 'Unknown'))}</td>"
                f"<td>{escape(spawn.block_pos_text())} / {escape(spawn.facing)} / {escape(creature.movement_mode)}</td>"
                f"<td class=\"{named_count_class}\">{len(named_clicks)}</td>"
                f"<td>{render_click_badges(named_clicks)}</td>"
                "</tr>"
            )

    return "\n".join(rows)


def render_click_badges(clicks: list[ClickRecord]) -> str:
    """Render click badges, marking wrong-chapter clicks in red."""
    if not clicks:
        return "None"

    ordered_clicks = sorted(clicks, key=lambda record: record.click_number)
    return "".join(
        (
            f'<span class="badge {"bad" if click.wrong_chapter else "good"}">'
            f'Click #{click.click_number}'
            f'</span>'
        )
        for click in ordered_clicks
    )


# ---------------------------------------------------------------------------
# Small utility helpers
# ---------------------------------------------------------------------------


def safe_int(value: str | None) -> int | None:
    """Convert a string to int when possible."""
    if value is None:
        return None
    try:
        return int(value)
    except (TypeError, ValueError):
        return None


def safe_float(value: str | None) -> float | None:
    """Convert a string to float when possible."""
    if value is None:
        return None
    try:
        return float(value)
    except (TypeError, ValueError):
        return None


def format_datetime(value: dt.datetime | None) -> str:
    """Format datetimes for the HTML report."""
    if value is None:
        return "Unknown"
    return value.strftime("%Y-%m-%d %H:%M:%S")


def format_timedelta(value: dt.timedelta) -> str:
    """Format a duration in a compact readable way."""
    total_seconds = int(value.total_seconds())
    if total_seconds < 0:
        total_seconds = 0

    hours, remainder = divmod(total_seconds, 3600)
    minutes, seconds = divmod(remainder, 60)

    if hours > 0:
        return f"{hours}h {minutes}m {seconds}s"
    return f"{minutes}m {seconds}s"


def escape(value: Any) -> str:
    """HTML-escape any printable value."""
    return html.escape(str(value))