from __future__ import annotations

import collections
import dataclasses
import datetime as dt
import html
import re
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
ANALYSIS_LOG_DIR = REPO_ROOT / "analysis" / "logs"
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

CHAPTER_TITLES: dict[int, str] = {
    0: "Chapter 0 (Get Started)",
    1: "Chapter 1 (The Museum)",
    2: "Chapter 2 (The Farm)",
    3: "Chapter 3 (The Jungle)",
}
CHAPTER_CONSTANT_TO_NUMBER = {f"CHAPTER_{number}": number for number in CHAPTER_TITLES}
CHAPTER_TITLE_TO_NUMBER = {title: number for number, title in CHAPTER_TITLES.items()}

EVENT_LINE_RE = re.compile(
    r"^\[(?P<date>\d{4}-\d{2}-\d{2})\] "
    r"\[(?P<time>\d{2}:\d{2}:\d{2}:\d{3})\] \| "
    r"(?P<event>[^|]+?) \| "
    r"(?P<rest>.+)$"
)
QUOTED_STRING_RE = re.compile(r'"([^"\\]*(?:\\.[^"\\]*)*)"')

CLICK_LIKE_BLOCKED_ACTIONS = {
    "attack_entity_blocked",
    "attack_block_blocked",
    "use_block_blocked",
}

MOVEMENT_SAMPLE_SECONDS = 1.0
MOVEMENT_DISTANCE_EPSILON = 0.05
GAME_CHAPTER_NUMBERS = (1, 2, 3)


@dataclasses.dataclass(slots=True)
class ParsedEvent:
    timestamp: dt.datetime
    event_type: str
    session_id: str
    fields: dict[str, str]
    raw_line: str


@dataclasses.dataclass(slots=True)
class CreatureSpawnDefinition:
    unique_name: str
    x: int
    y: int
    z: int
    facing: str

    def block_pos_text(self) -> str:
        return f"{self.x},{self.y},{self.z}"


@dataclasses.dataclass(slots=True)
class CreatureDefinition:
    species_key: str
    species_identifier: str
    display_name: str
    chapter_number: int
    movement_mode: str
    facts: list[str]
    spawns: list[CreatureSpawnDefinition]


@dataclasses.dataclass(slots=True)
class ClickRecord:
    timestamp: dt.datetime
    active_chapter: int
    species_key: str
    display_name: str
    creature_name: str
    entity_uuid: str
    entity_block_pos: str
    interacted_before: bool
    configured_chapter: int | None
    wrong_chapter: bool


@dataclasses.dataclass(slots=True)
class CreatureReadRecord:
    timestamp: dt.datetime
    active_chapter: int
    species_key: str
    display_name: str
    configured_chapter: int | None
    read_duration_seconds: float


@dataclasses.dataclass(slots=True)
class InstructionRecord:
    screen_key: str
    step_key: str
    displayed_at: dt.datetime | None = None
    button_pressed_at: dt.datetime | None = None
    button_key: str | None = None
    button_label: str | None = None
    time_on_screen_ms: int | None = None
    disabled_click_attempts: int = 0

    def reading_seconds(self) -> float | None:
        if self.time_on_screen_ms is None:
            return None
        return self.time_on_screen_ms / 1000.0


@dataclasses.dataclass(slots=True)
class ChapterZeroValidationRecord:
    timestamp: dt.datetime
    trigger: str
    reached_depth: bool
    interacted_with_creature: bool
    missing_depth_requirement: bool
    missing_creature_interaction: bool


@dataclasses.dataclass(slots=True)
class CheckpointRecord:
    completed_chapter: int
    next_chapter: int
    displayed_at: dt.datetime | None = None
    choice_at: dt.datetime | None = None
    prompt_displayed: bool = False
    prompt_dismissed: bool = False
    prompt_visible_at_choice: bool | None = None
    prompt_dismissed_before_choice: bool | None = None
    choice_time_on_screen_ms: int | None = None
    condition: str | None = None
    choice: str | None = None
    pause_started_at: dt.datetime | None = None
    pause_finished_at: dt.datetime | None = None
    slide_keys: list[str] = dataclasses.field(default_factory=list)

    def response_seconds(self) -> float | None:
        if self.displayed_at is None or self.choice_at is None:
            return None
        return (self.choice_at - self.displayed_at).total_seconds()

    def choice_time_on_screen_seconds(self) -> float | None:
        if self.choice_time_on_screen_ms is None:
            return None
        return self.choice_time_on_screen_ms / 1000.0

    def label(self) -> str:
        if self.completed_chapter < 0:
            return "Introduction → Chapter 0"
        return f"Chapter {self.completed_chapter} → Chapter {self.next_chapter}"


@dataclasses.dataclass(slots=True)
class ChapterSummary:
    chapter_number: int
    title: str
    started_at: dt.datetime | None = None
    completed_at: dt.datetime | None = None
    clicks: list[ClickRecord] = dataclasses.field(default_factory=list)
    creature_reads: list[CreatureReadRecord] = dataclasses.field(default_factory=list)
    blocked_actions: collections.Counter[str] = dataclasses.field(default_factory=collections.Counter)
    empty_clicks: collections.Counter[str] = dataclasses.field(default_factory=collections.Counter)
    movement_samples: int = 0
    latest_total_distance: float = 0.0
    latest_total_sprint_distance: float = 0.0
    summed_sample_distance: float = 0.0
    walking_seconds_estimate: float = 0.0
    sprinting_seconds_estimate: float = 0.0
    jumps: int = 0
    chapter_zero_conditions: list[str] = dataclasses.field(default_factory=list)
    chapter_zero_validation_failures: list[ChapterZeroValidationRecord] = dataclasses.field(default_factory=list)

    def duration_seconds(self) -> float | None:
        if self.started_at is None or self.completed_at is None:
            return None
        return (self.completed_at - self.started_at).total_seconds()

    def unique_species_clicked(self) -> int:
        return len({click.species_key for click in self.clicks})

    def allowed_clicks(self) -> int:
        return sum(1 for click in self.clicks if not click.wrong_chapter)

    def repeat_clicks(self) -> int:
        return sum(1 for click in self.clicks if click.interacted_before)

    def total_blocked_actions(self) -> int:
        return sum(self.blocked_actions.values())

    def total_empty_clicks(self) -> int:
        return sum(self.empty_clicks.values())

    def blocked_click_like_actions(self) -> int:
        return sum(self.blocked_actions.get(name, 0) for name in CLICK_LIKE_BLOCKED_ACTIONS)

    def creature_read_seconds(self) -> float:
        return sum(record.read_duration_seconds for record in self.creature_reads)

    def activity_breakdown_seconds(self) -> dict[str, float]:
        duration = self.duration_seconds() or 0.0
        walking = min(self.walking_seconds_estimate, duration)
        sprinting = min(self.sprinting_seconds_estimate, max(0.0, duration - walking))
        interacting = min(self.creature_read_seconds(), max(0.0, duration - walking - sprinting))
        other = max(0.0, duration - walking - sprinting - interacting)
        return {
            "walking": walking,
            "sprinting": sprinting,
            "interacting": interacting,
            "other": other,
        }


@dataclasses.dataclass(slots=True)
class Metadata:
    values: dict[str, str]

    def get(self, key: str, default: str = "") -> str:
        return self.values.get(key, default)


@dataclasses.dataclass(slots=True)
class SessionSummary:
    log_path: Path
    metadata: Metadata
    events: list[ParsedEvent]
    creatures: list[CreatureDefinition]
    chapters: dict[int, ChapterSummary]
    checkpoints: list[CheckpointRecord]
    instructions: list[InstructionRecord]
    condition: str | None
    condition_source: str | None
    player_name: str | None
    questionnaire_clicks: int
    game_end_reasons: list[str]
    raw_event_counts: collections.Counter[str]

    @property
    def session_id(self) -> str:
        return self.events[0].session_id if self.events else ""

    @property
    def started_at(self) -> dt.datetime:
        return self.events[0].timestamp

    @property
    def ended_at(self) -> dt.datetime:
        return self.events[-1].timestamp

    def duration(self) -> dt.timedelta:
        return self.ended_at - self.started_at

    def total_clicks(self) -> int:
        return sum(len(chapter.clicks) for chapter in self.chapters.values())

    def total_repeat_clicks(self) -> int:
        return sum(chapter.repeat_clicks() for chapter in self.chapters.values())

    def total_blocked_actions(self) -> int:
        return sum(chapter.total_blocked_actions() for chapter in self.chapters.values())

    def total_instruction_read_seconds(self) -> float:
        return sum((record.time_on_screen_ms or 0) / 1000.0 for record in self.instructions)

    def total_instruction_disabled_clicks(self) -> int:
        return sum(record.disabled_click_attempts for record in self.instructions)

    def final_score_interacted_species(self) -> int:
        return len(
            {
                click.species_key
                for chapter in self.chapters.values()
                for click in chapter.clicks
                if click.configured_chapter != 0
            }
        )

    def final_score_total_species(self) -> int:
        return len({creature.species_key for creature in self.creatures if creature.chapter_number != 0})

    def coverage_percent(self) -> float:
        total = self.final_score_total_species()
        return 0.0 if total == 0 else 100.0 * self.final_score_interacted_species() / total


def main() -> int:
    log_path = latest_session_log_path(ANALYSIS_LOG_DIR)
    if log_path is None:
        print(f"No study session log found in: {ANALYSIS_LOG_DIR}")
        return 1

    if not CREATURE_CARDS_PATH.exists():
        print(f"Creature card file not found: {CREATURE_CARDS_PATH}")
        return 1

    creatures = parse_creature_cards(CREATURE_CARDS_PATH)
    if not creatures:
        print("Could not parse any creature definitions from StudyCreatureCards.java")
        return 1

    metadata, events = parse_session_log(log_path)
    if not events:
        print(f"No parseable events found in: {log_path}")
        return 1

    summary = build_session_summary(log_path, metadata, events, creatures)
    html_text = render_session_html(summary)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(html_text, encoding="utf-8")

    print(f"Summary source log: {log_path.resolve()}")
    print(f"Summary written to: {OUTPUT_PATH.resolve()}")
    print(f"Open in browser: {OUTPUT_PATH.resolve().as_uri()}")
    return 0


def parse_creature_cards(path: Path) -> list[CreatureDefinition]:
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
            species_key = species_identifier.lower()
        else:
            species_identifier = strip_java_string(key_text)
            species_key = species_identifier.lower()

        display_name = strip_java_string(args[0])
        chapter_constant = args[1].strip().split(".")[-1]
        movement_mode = args[2].strip().split(".")[-1]
        facts = [clean_java_string(match) for match in QUOTED_STRING_RE.findall(args[3]) if match.strip()]
        spawns = parse_spawn_list(args[4])

        chapter_number = CHAPTER_CONSTANT_TO_NUMBER.get(chapter_constant)
        if chapter_number is None:
            continue

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
    list_expression = list_expression.strip()
    list_open = list_expression.find("(")
    list_close = list_expression.rfind(")")
    if list_open < 0 or list_close < 0 or list_close <= list_open:
        return []

    inner = list_expression[list_open + 1:list_close].strip()
    if not inner:
        return []

    blocks: list[str] = []
    depth = 0
    start = 0
    i = 0
    while i < len(inner):
        char = inner[i]
        if char == "(":
            depth += 1
        elif char == ")":
            depth = max(0, depth - 1)
        elif char == "," and depth == 0:
            blocks.append(inner[start:i].strip())
            start = i + 1
        i += 1
    final = inner[start:].strip()
    if final:
        blocks.append(final)

    spawns: list[CreatureSpawnDefinition] = []
    for block in blocks:
        if "new CreatureSpawn" not in block:
            continue
        args_open = block.find("(")
        args_close = block.rfind(")")
        if args_open < 0 or args_close <= args_open:
            continue
        args = split_top_level_commas(block[args_open + 1:args_close])
        if len(args) != 5:
            continue
        unique_name = strip_java_string(args[0])
        x = safe_int(strip_numeric_text(args[1]))
        y = safe_int(strip_numeric_text(args[2]))
        z = safe_int(strip_numeric_text(args[3]))
        facing = args[4].strip().split(".")[-1]
        if None in (x, y, z):
            continue
        spawns.append(
            CreatureSpawnDefinition(
                unique_name=unique_name,
                x=x,
                y=y,
                z=z,
                facing=facing,
            )
        )
    return spawns


def extract_map_entry_blocks(text: str) -> list[str]:
    marker = "Map.entry("
    blocks: list[str] = []
    cursor = 0

    while True:
        start = text.find(marker, cursor)
        if start == -1:
            break

        i = start
        depth = 0
        in_string = False
        escape_next = False

        while i < len(text):
            char = text[i]
            if in_string:
                if escape_next:
                    escape_next = False
                elif char == "\\":
                    escape_next = True
                elif char == '"':
                    in_string = False
            else:
                if char == '"':
                    in_string = True
                elif char == "(":
                    depth += 1
                elif char == ")":
                    depth -= 1
                    if depth == 0:
                        blocks.append(text[start:i + 1].strip())
                        cursor = i + 1
                        break
            i += 1
        else:
            break

    return blocks


def split_top_level_commas(text: str) -> list[str]:
    parts: list[str] = []
    depth = 0
    in_string = False
    escape_next = False
    start = 0

    for i, char in enumerate(text):
        if in_string:
            if escape_next:
                escape_next = False
            elif char == "\\":
                escape_next = True
            elif char == '"':
                in_string = False
            continue

        if char == '"':
            in_string = True
        elif char in "([{":
            depth += 1
        elif char in ")]}":
            depth = max(0, depth - 1)
        elif char == "," and depth == 0:
            parts.append(text[start:i].strip())
            start = i + 1

    final = text[start:].strip()
    if final:
        parts.append(final)
    return parts


def strip_java_string(text: str) -> str:
    text = text.strip()
    if text.startswith('"') and text.endswith('"'):
        text = text[1:-1]
    return clean_java_string(text)


def clean_java_string(text: str) -> str:
    return bytes(text, "utf-8").decode("unicode_escape").strip()


def strip_numeric_text(text: str) -> str:
    return re.sub(r"[dDfFlL_]", "", text.strip())


def latest_session_log_path(log_dir: Path) -> Path | None:
    if not log_dir.exists():
        return None
    candidates = sorted(log_dir.glob("study-*.log"))
    return candidates[-1] if candidates else None


def parse_session_log(path: Path) -> tuple[Metadata, list[ParsedEvent]]:
    metadata: dict[str, str] = {}
    events: list[ParsedEvent] = []

    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line == "================================================================":
            continue

        match = EVENT_LINE_RE.match(line)
        if match:
            timestamp = dt.datetime.strptime(
                f"{match.group('date')} {match.group('time')}",
                "%Y-%m-%d %H:%M:%S:%f",
            )
            fields = parse_key_value_fields(match.group("rest").strip())
            session_id = fields.get("session_id", "")
            if not session_id:
                continue
            events.append(
                ParsedEvent(
                    timestamp=timestamp,
                    event_type=match.group("event").strip(),
                    session_id=session_id,
                    fields=fields,
                    raw_line=raw_line,
                )
            )
            continue

        if "=" in line and " | " not in line:
            key, value = line.split("=", 1)
            metadata[key.strip()] = value.strip()

    return Metadata(metadata), events


def parse_key_value_fields(rest: str) -> dict[str, str]:
    result: dict[str, str] = {}
    for part in rest.split(" | "):
        if "=" not in part:
            continue
        key, value = part.split("=", 1)
        result[key.strip()] = value.strip()
    return result


def build_session_summary(
    log_path: Path,
    metadata: Metadata,
    events: list[ParsedEvent],
    creatures: list[CreatureDefinition],
) -> SessionSummary:
    chapters = {
        chapter_number: ChapterSummary(chapter_number=chapter_number, title=title)
        for chapter_number, title in CHAPTER_TITLES.items()
    }

    checkpoint_by_key: dict[tuple[int, int], CheckpointRecord] = {}
    instruction_by_key: dict[tuple[str, str], InstructionRecord] = {}
    raw_event_counts: collections.Counter[str] = collections.Counter()
    player_name: str | None = None
    condition: str | None = None
    condition_source: str | None = None
    questionnaire_clicks = 0
    game_end_reasons: list[str] = []
    current_active_chapter: int | None = None

    creature_by_spawn_name: dict[str, CreatureDefinition] = {}
    creature_by_display_name: dict[str, CreatureDefinition] = {}
    for creature in creatures:
        creature_by_display_name[normalise_label(creature.display_name)] = creature
        for spawn in creature.spawns:
            creature_by_spawn_name[spawn.unique_name] = creature

    for event in events:
        raw_event_counts[event.event_type] += 1
        if player_name is None:
            player_name = event.fields.get("player") or metadata.get("session_player") or None

        if event.event_type == "experiment_condition_assigned":
            condition = event.fields.get("condition")
            condition_source = event.fields.get("source")

        elif event.event_type == "chapter_started":
            chapter_number = safe_int(event.fields.get("chapter"))
            if chapter_number in chapters:
                current_active_chapter = chapter_number
                chapters[chapter_number].started_at = event.timestamp

        elif event.event_type == "chapter_completed":
            chapter_number = safe_int(event.fields.get("chapter"))
            if chapter_number in chapters:
                chapters[chapter_number].completed_at = event.timestamp
                if current_active_chapter == chapter_number:
                    current_active_chapter = None

        elif event.event_type == "movement_sample":
            chapter_number = chapter_number_from_title(event.fields.get("chapter_title"))
            if chapter_number in chapters:
                chapter = chapters[chapter_number]
                chapter.movement_samples += 1
                chapter.latest_total_distance = safe_float(event.fields.get("total_distance")) or chapter.latest_total_distance
                chapter.latest_total_sprint_distance = safe_float(event.fields.get("total_sprint_distance")) or chapter.latest_total_sprint_distance
                sample_distance = safe_float(event.fields.get("sample_distance")) or 0.0
                chapter.summed_sample_distance += sample_distance
                if safe_bool(event.fields.get("sprinting")):
                    chapter.sprinting_seconds_estimate += MOVEMENT_SAMPLE_SECONDS
                elif sample_distance > MOVEMENT_DISTANCE_EPSILON:
                    chapter.walking_seconds_estimate += MOVEMENT_SAMPLE_SECONDS

        elif event.event_type == "jump_started":
            chapter_number = chapter_number_from_title(event.fields.get("chapter_title"))
            if chapter_number in chapters:
                chapters[chapter_number].jumps += 1

        elif event.event_type == "creature_card_opened":
            if current_active_chapter is None:
                continue

            creature_name = event.fields.get("creature_name", "unknown")
            configured = creature_by_spawn_name.get(creature_name)
            species_key = (
                configured.species_key
                if configured is not None
                else fallback_species_key(event.fields.get("entity_type"), creature_name)
            )
            display_name = (
                configured.display_name
                if configured is not None
                else event.fields.get("creature_label", humanize_species_key(species_key))
            )
            configured_chapter = configured.chapter_number if configured is not None else None
            wrong_chapter = configured_chapter is not None and configured_chapter != current_active_chapter

            chapters[current_active_chapter].clicks.append(
                ClickRecord(
                    timestamp=event.timestamp,
                    active_chapter=current_active_chapter,
                    species_key=species_key,
                    display_name=display_name,
                    creature_name=creature_name,
                    entity_uuid=event.fields.get("entity_uuid", "unknown"),
                    entity_block_pos=normalise_block_pos(event.fields.get("entity_block_pos", "unknown")),
                    interacted_before=event.fields.get("interacted_before", "").lower() == "true",
                    configured_chapter=configured_chapter,
                    wrong_chapter=wrong_chapter,
                )
            )

        elif event.event_type == "creature_card_closed":
            active_chapter = chapter_number_from_title(event.fields.get("active_chapter_title"))
            if active_chapter not in chapters:
                continue
            creature_label = event.fields.get("creature_label", "unknown")
            configured = creature_by_display_name.get(normalise_label(creature_label))
            configured_chapter = (
                configured.chapter_number
                if configured is not None
                else chapter_number_from_title(event.fields.get("chapter_title"))
            )
            species_key = configured.species_key if configured is not None else fallback_species_key(None, creature_label)
            display_name = configured.display_name if configured is not None else creature_label
            chapters[active_chapter].creature_reads.append(
                CreatureReadRecord(
                    timestamp=event.timestamp,
                    active_chapter=active_chapter,
                    species_key=species_key,
                    display_name=display_name,
                    configured_chapter=configured_chapter,
                    read_duration_seconds=(safe_float(event.fields.get("read_duration_ms")) or 0.0) / 1000.0,
                )
            )

        elif event.event_type == "empty_click":
            chapter_number = chapter_number_from_title(event.fields.get("chapter_title"))
            if chapter_number in chapters:
                chapters[chapter_number].empty_clicks[event.fields.get("click_type", "unknown")] += 1

        elif event.event_type == "blocked_action":
            if current_active_chapter in chapters:
                chapters[current_active_chapter].blocked_actions[event.fields.get("action", "unknown")] += 1

        elif event.event_type == "chapter_zero_condition_satisfied":
            chapters[0].chapter_zero_conditions.append(event.fields.get("condition", "unknown"))

        elif event.event_type == "chapter_zero_validation_failed":
            chapters[0].chapter_zero_validation_failures.append(
                ChapterZeroValidationRecord(
                    timestamp=event.timestamp,
                    trigger=event.fields.get("trigger", "unknown"),
                    reached_depth=safe_bool(event.fields.get("reached_depth")),
                    interacted_with_creature=safe_bool(event.fields.get("interacted_with_creature")),
                    missing_depth_requirement=safe_bool(event.fields.get("missing_depth_requirement")),
                    missing_creature_interaction=safe_bool(event.fields.get("missing_creature_interaction")),
                )
            )

        elif event.event_type in {
            "checkpoint_displayed",
            "checkpoint_choice_made",
            "checkpoint_prompt_displayed",
            "checkpoint_prompt_dismissed",
            "checkpoint_choice_context",
            "checkpoint_pause_started",
            "checkpoint_pause_finished",
            "checkpoint_slide_displayed",
        }:
            completed_chapter = safe_int(event.fields.get("completed_chapter"))
            next_chapter = safe_int(event.fields.get("next_chapter"))
            if completed_chapter is None or next_chapter is None:
                continue

            key = (completed_chapter, next_chapter)
            record = checkpoint_by_key.setdefault(
                key,
                CheckpointRecord(completed_chapter=completed_chapter, next_chapter=next_chapter),
            )

            if event.event_type == "checkpoint_displayed":
                record.displayed_at = event.timestamp
                record.condition = event.fields.get("condition")
            elif event.event_type == "checkpoint_choice_made":
                record.choice_at = event.timestamp
                record.choice = event.fields.get("choice")
                record.condition = record.condition or event.fields.get("condition")
            elif event.event_type == "checkpoint_prompt_displayed":
                record.prompt_displayed = True
                record.condition = record.condition or event.fields.get("condition")
            elif event.event_type == "checkpoint_prompt_dismissed":
                record.prompt_dismissed = True
                record.condition = record.condition or event.fields.get("condition")
            elif event.event_type == "checkpoint_choice_context":
                record.condition = record.condition or event.fields.get("condition")
                record.choice = record.choice or event.fields.get("choice")
                prompt_visible_at_choice = event.fields.get("prompt_visible_at_choice")
                if prompt_visible_at_choice is not None:
                    record.prompt_visible_at_choice = safe_bool(prompt_visible_at_choice)
                prompt_dismissed_before_choice = event.fields.get("prompt_dismissed_before_choice")
                if prompt_dismissed_before_choice is not None:
                    record.prompt_dismissed_before_choice = safe_bool(prompt_dismissed_before_choice)
                record.choice_time_on_screen_ms = safe_int(event.fields.get("time_on_screen_ms"))
            elif event.event_type == "checkpoint_pause_started":
                record.pause_started_at = event.timestamp
            elif event.event_type == "checkpoint_pause_finished":
                record.pause_finished_at = event.timestamp
            elif event.event_type == "checkpoint_slide_displayed":
                slide_key = event.fields.get("slide_key")
                if slide_key:
                    record.slide_keys.append(slide_key)

        elif event.event_type in {
            "instruction_screen_displayed",
            "instruction_button_pressed",
            "instruction_button_clicked_while_disabled",
        }:
            screen_key = event.fields.get("screen_key", "unknown")
            step_key = event.fields.get("step_key", "unknown")
            record = instruction_by_key.setdefault(
                (screen_key, step_key),
                InstructionRecord(screen_key=screen_key, step_key=step_key),
            )
            if event.event_type == "instruction_screen_displayed":
                record.displayed_at = record.displayed_at or event.timestamp
            elif event.event_type == "instruction_button_pressed":
                record.button_pressed_at = event.timestamp
                record.button_key = event.fields.get("button_key")
                record.button_label = event.fields.get("button_label")
                record.time_on_screen_ms = safe_int(event.fields.get("time_on_screen_ms"))
            elif event.event_type == "instruction_button_clicked_while_disabled":
                record.disabled_click_attempts += 1

        elif event.event_type == "questionnaire_button_pressed":
            questionnaire_clicks += 1

        elif event.event_type == "game_ended":
            reason = event.fields.get("reason")
            if reason:
                game_end_reasons.append(reason)

    checkpoints = sorted(
        checkpoint_by_key.values(),
        key=lambda record: (record.completed_chapter, record.next_chapter),
    )
    instructions = sorted(
        instruction_by_key.values(),
        key=lambda record: (
            record.displayed_at or dt.datetime.min,
            record.screen_key,
            record.step_key,
        ),
    )

    return SessionSummary(
        log_path=log_path,
        metadata=metadata,
        events=events,
        creatures=creatures,
        chapters=chapters,
        checkpoints=checkpoints,
        instructions=instructions,
        condition=condition,
        condition_source=condition_source,
        player_name=player_name or metadata.get("session_player") or None,
        questionnaire_clicks=questionnaire_clicks,
        game_end_reasons=game_end_reasons,
        raw_event_counts=raw_event_counts,
    )


def render_in_short_block(session: SessionSummary) -> str:
    game_chapters = [session.chapters[number] for number in GAME_CHAPTER_NUMBERS]
    chapter_duration = sum(chapter.duration_seconds() or 0.0 for chapter in game_chapters)
    chapter_zero_duration = session.chapters[0].duration_seconds() or 0.0
    instruction_seconds = session.total_instruction_read_seconds()

    activity_totals = {"interacting": 0.0, "walking": 0.0, "sprinting": 0.0, "other": 0.0}
    for chapter in game_chapters:
        activity = chapter.activity_breakdown_seconds()
        for key in activity_totals:
            activity_totals[key] += activity[key]

    distance = sum(chapter.latest_total_distance for chapter in game_chapters)
    overlay_seconds = sum(checkpoint_overlay_seconds(record) for record in session.checkpoints if record.completed_chapter >= 0)

    creature_clicks = [click for chapter in game_chapters for click in chapter.clicks]
    empty_clicks = sum(chapter.total_empty_clicks() for chapter in game_chapters)
    blocked_clicks = sum(chapter.blocked_click_like_actions() for chapter in game_chapters)
    allowed_clicks = sum(chapter.allowed_clicks() for chapter in game_chapters)
    disallowed_creature_clicks = sum(1 for click in creature_clicks if click.wrong_chapter)
    all_ingame_clicks = len(creature_clicks) + empty_clicks + blocked_clicks
    disallowed_or_empty = disallowed_creature_clicks + empty_clicks + blocked_clicks
    jumps = sum(chapter.jumps for chapter in game_chapters)

    species_counts = collections.Counter(click.species_key for click in creature_clicks)
    creature_counts = collections.Counter(click_identity(click) for click in creature_clicks)

    choice_records = [
        record
        for record in session.checkpoints
        if record.completed_chapter in (1, 2) and record.choice
    ]
    if not choice_records:
        choice_records = [record for record in session.checkpoints if record.choice][:2]
    choices_html = render_choice_summary(choice_records[:2])

    ingame_progress = (
        f"overlays {format_seconds(overlay_seconds)}; "
        f"reading cards {activity_with_percent(activity_totals['interacting'], chapter_duration)}; "
        f"walking {activity_with_percent(activity_totals['walking'], chapter_duration)}; "
        f"sprinting {activity_with_percent(activity_totals['sprinting'], chapter_duration)}; "
        f"other {activity_with_percent(activity_totals['other'], chapter_duration)}; "
        f"distance {distance:.1f}"
    )

    return f"""
  <div class="card in-short">
    <h2>In short</h2>
    <div class="short-grid">
      {short_stat("Session ID", session.session_id, "Session identifier read from the newest parsed log file.")}
      {short_stat("Start", format_datetime(session.started_at), "Timestamp of the first parseable event in this session log.")}
      <div class="short-stat short-wide" title="Experimental condition plus the two main checkpoint choices after chapters 1 and 2. The parenthesised time is the logged time the choice was on screen where available; otherwise the display-to-choice response time.">
        <div class="short-label">Condition + choices</div>
        <div class="short-value">{escape(format_condition(session.condition, session.condition_source))}</div>
        {choices_html}
      </div>
      {short_stat("Progress overall", format_timedelta(session.duration()), "Full duration from the first to the last parseable event in the log.")}
      {short_stat("Progress before chapters", f"instructions {format_seconds(instruction_seconds)}; ch0 {format_seconds(chapter_zero_duration)}", "Instruction-screen reading time plus Chapter 0 active-play duration.")}
      {short_stat("Progress ingame", ingame_progress, "Checkpoint overlays include logged checkpoint response time plus pause timers. Chapter totals merge Chapters 1–3; percentages use merged active chapter time as the denominator.")}
      {short_stat("Actions", f"all {all_ingame_clicks}; allowed {allowed_clicks}; disallowed/empty {disallowed_or_empty}; disabled btn {session.total_instruction_disabled_clicks()}; jumps {jumps}", "All ingame clicks = creature-card clicks plus empty-air clicks plus blocked click-like actions in Chapters 1–3. Allowed clicks are creature-card opens in the configured chapter. Disallowed/empty combines wrong-chapter creature clicks, empty-air clicks, and blocked click-like actions.")}
      {short_stat("Interactions", f"species {len(species_counts)}; creatures {len(creature_counts)}; species revisited {sum(1 for count in species_counts.values() if count > 1)}; creatures revisited {sum(1 for count in creature_counts.values() if count > 1)}", "Unique species and unique creature entities clicked during Chapters 1–3. Revisited means clicked more than once.")}
    </div>
  </div>
    """


def short_stat(label: str, value: str, tooltip: str) -> str:
    return (
        f'<div class="short-stat" title="{escape(tooltip)}">'
        f'<div class="short-label">{escape(label)}</div>'
        f'<div class="short-value">{escape(value)}</div>'
        '</div>'
    )


def render_choice_summary(records: list[CheckpointRecord]) -> str:
    if not records:
        return '<ol class="short-choices"><li>No checkpoint choices found</li></ol>'

    rows = []
    for record in records:
        seconds = record.choice_time_on_screen_seconds()
        if seconds is None:
            seconds = record.response_seconds()
        rows.append(
            f"<li>{escape(format_checkpoint_choice(record))} "
            f"<span class='short-muted'>(thought {escape(format_seconds(seconds))})</span></li>"
        )

    return f"<ol class='short-choices'>{''.join(rows)}</ol>"


def format_checkpoint_choice(record: CheckpointRecord) -> str:
    choice = (record.choice or "").strip().lower()
    if choice in {"break", "pause", "2-min pause", "two-minute pause"} or record.pause_started_at is not None:
        return "2-min pause"
    if choice == "continue":
        return "continue"
    return humanize_key(choice or "unknown").lower()


def checkpoint_overlay_seconds(record: CheckpointRecord) -> float:
    seconds = record.choice_time_on_screen_seconds()
    if seconds is None:
        seconds = record.response_seconds()
    total = seconds or 0.0

    if record.pause_started_at is not None and record.pause_finished_at is not None:
        total += max(0.0, (record.pause_finished_at - record.pause_started_at).total_seconds())

    return total


def click_identity(click: ClickRecord) -> str:
    if click.entity_uuid and click.entity_uuid != "unknown":
        return click.entity_uuid
    if click.creature_name and click.creature_name != "unknown":
        return click.creature_name
    return f"{click.species_key}@{click.entity_block_pos}"


def render_session_html(session: SessionSummary) -> str:
    overview_metrics = [
        metric_card("Start", format_datetime(session.started_at)),
        metric_card("End", format_datetime(session.ended_at)),
        metric_card("Duration", format_timedelta(session.duration())),
        metric_card("Condition", format_condition(session.condition, session.condition_source)),
        metric_card(
            "Final score",
            f"{session.final_score_interacted_species()} / {session.final_score_total_species()} species",
            f"{session.coverage_percent():.0f}% coverage",
        ),
        metric_card("Total clicks", str(session.total_clicks()), f"repeat clicks: {session.total_repeat_clicks()}"),
        metric_card("Instruction reading", format_seconds(session.total_instruction_read_seconds()), "consent + intro screens"),
        metric_card("Disabled intro clicks", str(session.total_instruction_disabled_clicks()), "attempts while button inactive"),
        metric_card("Questionnaire", str(session.questionnaire_clicks), "button press(es)"),
        metric_card("End reason", ", ".join(session.game_end_reasons) or "unknown"),
    ]

    chapter_overview_rows = "\n".join(
        render_chapter_overview_row(session.chapters[n], session.creatures) for n in sorted(session.chapters)
    )
    instruction_rows = "\n".join(render_instruction_row(record) for record in session.instructions)
    creature_rows = "\n".join(
        render_creature_matrix_row(session, creature)
        for creature in sorted(session.creatures, key=lambda item: (item.chapter_number, item.display_name.lower()))
    )
    checkpoint_rows = "\n".join(render_checkpoint_row(record) for record in session.checkpoints)
    detail_sections = "\n".join(render_chapter_detail(session.chapters[n]) for n in sorted(session.chapters))
    blocked_rows = "\n".join(render_blocked_row(session.chapters[n]) for n in sorted(session.chapters))
    raw_event_rows = "\n".join(
        f"<tr><td>{escape(name)}</td><td class='num'>{count}</td></tr>"
        for name, count in sorted(session.raw_event_counts.items())
    )
    spawn_rows = "\n".join(
        render_spawn_integrity_row(creature)
        for creature in sorted(session.creatures, key=lambda item: (item.chapter_number, item.display_name.lower()))
    )
    chapter_zero_failure_rows = "\n".join(render_chapter_zero_failure_row(record) for record in session.chapters[0].chapter_zero_validation_failures)
    in_short_block = render_in_short_block(session)

    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Last session summary</title>
<style>
:root {{
  --bg: #f5f7f8;
  --card: #ffffff;
  --line: #d7dee3;
  --text: #142126;
  --muted: #5d6c74;
  --accent: #0f766e;
}}
* {{ box-sizing: border-box; }}
body {{ margin: 0; background: var(--bg); color: var(--text); font-family: Arial, sans-serif; }}
main {{ max-width: 1800px; margin: 0 auto; padding: 18px; }}
h1, h2, h3 {{ margin: 0 0 10px 0; }}
p {{ margin: 0; }}
.small {{ font-size: 12px; color: var(--muted); }}
.num {{ text-align: right; white-space: nowrap; }}
.wrap {{ white-space: normal; }}
.card {{ background: var(--card); border: 1px solid var(--line); border-radius: 12px; padding: 14px; margin-bottom: 14px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }}
.grid {{ display: grid; gap: 12px; }}
.metric-grid {{ grid-template-columns: repeat(5, minmax(0, 1fr)); }}
.metric {{ border: 1px solid var(--line); border-radius: 10px; padding: 12px; background: #fbfcfd; }}
.metric .label {{ font-size: 12px; color: var(--muted); text-transform: uppercase; letter-spacing: .04em; }}
.metric .value {{ font-size: 21px; font-weight: 700; margin-top: 4px; }}
.metric .sub {{ font-size: 12px; color: var(--muted); margin-top: 4px; }}
.tabs {{ display: flex; gap: 8px; margin: 10px 0 14px 0; }}
.tab-btn {{ border: 1px solid var(--line); background: var(--card); border-radius: 999px; padding: 10px 14px; cursor: pointer; font-weight: 700; }}
.tab-btn.active {{ background: var(--accent); color: white; border-color: var(--accent); }}
.tab-panel {{ display: none; }}
.tab-panel.active {{ display: block; }}
table {{ width: 100%; border-collapse: collapse; font-size: 13px; }}
th, td {{ border: 1px solid var(--line); padding: 7px 8px; vertical-align: top; }}
th {{ background: #f2f5f7; text-align: left; }}
.two-col {{ display: grid; grid-template-columns: 1.3fr 1fr; gap: 14px; }}
.three-col {{ display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14px; }}
.in-short {{ border: 2px solid #10b981; background: #f8fffb; }}
.short-grid {{ display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 10px; }}
.short-stat {{ border: 1px solid #b7e4c7; border-radius: 10px; padding: 10px; background: rgba(255,255,255,0.78); min-height: 72px; }}
.short-wide {{ grid-column: span 2; }}
.short-label {{ font-size: 11px; color: var(--muted); text-transform: uppercase; letter-spacing: .04em; margin-bottom: 5px; }}
.short-value {{ font-size: 15px; font-weight: 700; line-height: 1.35; }}
.short-choices {{ margin: 6px 0 0 20px; padding: 0; font-size: 13px; line-height: 1.35; }}
.short-muted {{ color: var(--muted); font-weight: 400; }}
@media (max-width: 1200px) {{
  .metric-grid {{ grid-template-columns: repeat(2, minmax(0, 1fr)); }}
  .two-col, .three-col, .short-grid {{ grid-template-columns: 1fr; }}
  .short-wide {{ grid-column: span 1; }}
}}
</style>
</head>
<body>
<main>
  <div class="card">
    <h1>Last session summary</h1>
    <p class="small">Dense interview aid for the most recent study session.</p>
    <p class="small">Log: {escape(str(session.log_path.resolve()))}</p>
    <p class="small">HTML: {escape(str(OUTPUT_PATH.resolve()))}</p>
  </div>

  {in_short_block}

  <section class="grid metric-grid">
    {''.join(overview_metrics)}
  </section>

  <div class="tabs">
    <button class="tab-btn active" data-tab="primary">Primary relevance</button>
    <button class="tab-btn" data-tab="secondary">Secondary relevance</button>
  </div>

  <section id="primary" class="tab-panel active">
    <div class="two-col">
      <div class="card">
        <h2>Per-chapter overview</h2>
        <table>
          <tr>
            <th>Chapter</th>
            <th class="num">Time</th>
            <th class="num">Score</th>
            <th class="num">Clicks</th>
            <th class="num">Read cards</th>
            <th class="num">Walking</th>
            <th class="num">Sprinting</th>
            <th class="num">Other</th>
            <th class="num">Distance</th>
            <th class="num">Jumps</th>
          </tr>
          {chapter_overview_rows}
        </table>
      </div>
      <div class="card">
        <h2>Instruction screens</h2>
        <table>
          <tr>
            <th>Screen</th>
            <th>Step</th>
            <th>Button</th>
            <th class="num">Read time</th>
            <th class="num">Disabled click attempts</th>
          </tr>
          {instruction_rows}
        </table>
      </div>
    </div>

    <div class="card">
      <h2>Checkpoint / choice timeline</h2>
      <table>
        <tr>
          <th>Transition</th>
          <th>Condition</th>
          <th>Choice</th>
          <th class="num">Response</th>
          <th>Prompt shown</th>
          <th>Prompt dismissed</th>
          <th>Prompt visible at choice</th>
          <th>Slides</th>
        </tr>
        {checkpoint_rows}
      </table>
    </div>

    <div class="card">
      <h2>Clicks and creature-card reading per species</h2>
      <table>
        <tr>
          <th>Creature</th>
          <th>Configured chapter</th>
          <th class="num">Ch0 clicks</th>
          <th class="num">Ch1 clicks</th>
          <th class="num">Ch2 clicks</th>
          <th class="num">Ch3 clicks</th>
          <th class="num">Total clicks</th>
          <th class="num">Ch0 read</th>
          <th class="num">Ch1 read</th>
          <th class="num">Ch2 read</th>
          <th class="num">Ch3 read</th>
          <th class="num">Total read</th>
          <th class="num">Unique entities</th>
          <th class="num">Repeat clicks</th>
          <th>Notes</th>
        </tr>
        {creature_rows}
      </table>
    </div>

    <div class="card">
      <h2>Chapter details</h2>
      <p class="small">Movement times are estimated from one-second movement samples. “Other” is the remainder of the chapter after walking, sprinting, and reading creature cards.</p>
      {detail_sections}
    </div>
  </section>

  <section id="secondary" class="tab-panel">
    <div class="three-col">
      <div class="card">
        <h2>Session metadata</h2>
        <table>
          <tr><th>Player</th><td>{escape(session.player_name or 'unknown')}</td></tr>
          <tr><th>Session ID</th><td>{escape(session.session_id)}</td></tr>
          <tr><th>Condition</th><td>{escape(format_condition(session.condition, session.condition_source))}</td></tr>
          <tr><th>Analysis log file</th><td class="wrap">{escape(session.metadata.get('analysis_log_file', ''))}</td></tr>
          <tr><th>Runtime mirror</th><td class="wrap">{escape(session.metadata.get('runtime_log_mirror', ''))}</td></tr>
          <tr><th>World source</th><td class="wrap">{escape(session.metadata.get('repo_world_source', ''))}</td></tr>
          <tr><th>Runtime world</th><td class="wrap">{escape(session.metadata.get('runtime_world_copy', ''))}</td></tr>
        </table>
      </div>
      <div class="card">
        <h2>Blocked / illegal actions</h2>
        <table>
          <tr><th>Chapter</th><th>Blocked actions</th><th class="num">Count</th></tr>
          {blocked_rows}
        </table>
      </div>
      <div class="card">
        <h2>Raw event counts</h2>
        <table>
          <tr><th>Event</th><th class="num">Count</th></tr>
          {raw_event_rows}
        </table>
      </div>
    </div>

    <div class="card">
      <h2>Chapter 0 validation failures</h2>
      <table>
        <tr>
          <th>Time</th>
          <th>Trigger</th>
          <th>Reached depth</th>
          <th>Interacted with creature</th>
          <th>Missing depth</th>
          <th>Missing creature interaction</th>
        </tr>
        {chapter_zero_failure_rows or '<tr><td colspan="6">No logged failures</td></tr>'}
      </table>
    </div>

    <div class="card">
      <h2>Configured species / spawn integrity</h2>
      <table>
        <tr>
          <th>Creature</th>
          <th>Chapter</th>
          <th class="num">Configured spawns</th>
          <th>Spawn names</th>
          <th>Movement</th>
          <th>Facts</th>
        </tr>
        {spawn_rows}
      </table>
    </div>
  </section>
</main>
<script>
for (const button of document.querySelectorAll('.tab-btn')) {{
  button.addEventListener('click', () => {{
    for (const other of document.querySelectorAll('.tab-btn')) other.classList.remove('active');
    for (const panel of document.querySelectorAll('.tab-panel')) panel.classList.remove('active');
    button.classList.add('active');
    document.getElementById(button.dataset.tab).classList.add('active');
  }});
}}
</script>
</body>
</html>"""


def metric_card(label: str, value: str, sub: str = "") -> str:
    sub_html = f'<div class="sub">{escape(sub)}</div>' if sub else ""
    return (
        '<div class="metric">'
        f'<div class="label">{escape(label)}</div>'
        f'<div class="value">{escape(value)}</div>'
        f"{sub_html}"
        "</div>"
    )


def render_chapter_overview_row(chapter: ChapterSummary, creatures: list[CreatureDefinition]) -> str:
    configured_total = sum(1 for creature in creatures if creature.chapter_number == chapter.chapter_number)
    score_text = f"{chapter.unique_species_clicked()} / {configured_total}" if configured_total else str(chapter.unique_species_clicked())
    activity = chapter.activity_breakdown_seconds()
    duration = chapter.duration_seconds() or 0.0
    return (
        "<tr>"
        f"<td>{escape(chapter.title)}</td>"
        f"<td class='num'>{escape(format_seconds(chapter.duration_seconds()))}</td>"
        f"<td class='num'>{escape(score_text)}</td>"
        f"<td class='num'>{len(chapter.clicks)}</td>"
        f"<td class='num'>{escape(activity_with_percent(activity['interacting'], duration))}</td>"
        f"<td class='num'>{escape(activity_with_percent(activity['walking'], duration))}</td>"
        f"<td class='num'>{escape(activity_with_percent(activity['sprinting'], duration))}</td>"
        f"<td class='num'>{escape(activity_with_percent(activity['other'], duration))}</td>"
        f"<td class='num'>{chapter.latest_total_distance:.1f}</td>"
        f"<td class='num'>{chapter.jumps}</td>"
        "</tr>"
    )


def render_instruction_row(record: InstructionRecord) -> str:
    return (
        "<tr>"
        f"<td>{escape(humanize_key(record.screen_key))}</td>"
        f"<td>{escape(humanize_key(record.step_key))}</td>"
        f"<td>{escape(record.button_label or record.button_key or '-')}</td>"
        f"<td class='num'>{escape(format_seconds(record.reading_seconds()))}</td>"
        f"<td class='num'>{record.disabled_click_attempts}</td>"
        "</tr>"
    )


def render_checkpoint_row(record: CheckpointRecord) -> str:
    response = record.response_seconds()
    slide_text = ", ".join(record.slide_keys) if record.slide_keys else "-"
    prompt = "Yes" if record.prompt_displayed else "No"
    prompt_dismissed = "Yes" if record.prompt_dismissed else "No"
    prompt_visible_at_choice = format_optional_bool(record.prompt_visible_at_choice)
    return (
        "<tr>"
        f"<td>{escape(record.label())}</td>"
        f"<td>{escape(record.condition or '-')}</td>"
        f"<td>{escape(record.choice or '-')}</td>"
        f"<td class='num'>{escape(format_seconds(response))}</td>"
        f"<td>{prompt}</td>"
        f"<td>{prompt_dismissed}</td>"
        f"<td>{prompt_visible_at_choice}</td>"
        f"<td>{escape(slide_text)}</td>"
        "</tr>"
    )


def render_creature_matrix_row(session: SessionSummary, creature: CreatureDefinition) -> str:
    chapter_clicks = {0: 0, 1: 0, 2: 0, 3: 0}
    chapter_read_seconds = {0: 0.0, 1: 0.0, 2: 0.0, 3: 0.0}
    repeat_clicks = 0
    uuids: set[str] = set()
    wrong_chapter_targets: list[str] = []

    for chapter in session.chapters.values():
        for click in chapter.clicks:
            if click.species_key != creature.species_key:
                continue
            chapter_clicks[click.active_chapter] += 1
            repeat_clicks += 1 if click.interacted_before else 0
            uuids.add(click.entity_uuid)
            if click.wrong_chapter:
                wrong_chapter_targets.append(f"clicked in Ch{click.active_chapter}")
        for read in chapter.creature_reads:
            if read.species_key != creature.species_key:
                continue
            chapter_read_seconds[read.active_chapter] += read.read_duration_seconds

    total_clicks = sum(chapter_clicks.values())
    total_read_seconds = sum(chapter_read_seconds.values())
    notes = "; ".join(sorted(set(wrong_chapter_targets))) if wrong_chapter_targets else ""
    return (
        "<tr>"
        f"<td>{escape(creature.display_name)}</td>"
        f"<td>{escape(CHAPTER_TITLES.get(creature.chapter_number, f'Chapter {creature.chapter_number}'))}</td>"
        f"<td class='num'>{escape(format_possible_count(chapter_clicks[0], creature.chapter_number == 0))}</td>"
        f"<td class='num'>{escape(format_possible_count(chapter_clicks[1], creature.chapter_number == 1))}</td>"
        f"<td class='num'>{escape(format_possible_count(chapter_clicks[2], creature.chapter_number == 2))}</td>"
        f"<td class='num'>{escape(format_possible_count(chapter_clicks[3], creature.chapter_number == 3))}</td>"
        f"<td class='num'>{total_clicks}</td>"
        f"<td class='num'>{escape(format_possible_seconds(chapter_read_seconds[0], creature.chapter_number == 0))}</td>"
        f"<td class='num'>{escape(format_possible_seconds(chapter_read_seconds[1], creature.chapter_number == 1))}</td>"
        f"<td class='num'>{escape(format_possible_seconds(chapter_read_seconds[2], creature.chapter_number == 2))}</td>"
        f"<td class='num'>{escape(format_possible_seconds(chapter_read_seconds[3], creature.chapter_number == 3))}</td>"
        f"<td class='num'>{escape(format_seconds(total_read_seconds))}</td>"
        f"<td class='num'>{len(uuids)}</td>"
        f"<td class='num'>{repeat_clicks}</td>"
        f"<td>{escape(notes or '-')}</td>"
        "</tr>"
    )


def render_chapter_detail(chapter: ChapterSummary) -> str:
    click_counter = collections.Counter(click.display_name for click in chapter.clicks)
    top_clicks = ", ".join(f"{name} ({count})" for name, count in click_counter.most_common(8)) or "No clicks"
    blocked_summary = ", ".join(
        f"{name} ({count})" for name, count in chapter.blocked_actions.most_common()
    ) or "None"
    chapter_zero_notes = ", ".join(chapter.chapter_zero_conditions) if chapter.chapter_zero_conditions else "-"
    activity = chapter.activity_breakdown_seconds()
    duration = chapter.duration_seconds() or 0.0
    read_rows = "\n".join(render_chapter_creature_read_row(read) for read in top_creature_reads(chapter))
    validation_notes = "<br>".join(render_validation_note_html(item) for item in chapter.chapter_zero_validation_failures) or "-"

    return f"""
    <div class="card">
      <h3>{escape(chapter.title)}</h3>
      <table>
        <tr><th>Started</th><td>{escape(format_datetime(chapter.started_at))}</td><th>Completed</th><td>{escape(format_datetime(chapter.completed_at))}</td></tr>
        <tr><th>Duration</th><td>{escape(format_seconds(chapter.duration_seconds()))}</td><th>Unique species clicked</th><td>{chapter.unique_species_clicked()}</td></tr>
        <tr><th>Total clicks</th><td>{len(chapter.clicks)}</td><th>Repeat clicks</th><td>{chapter.repeat_clicks()}</td></tr>
        <tr><th>Allowed clicks</th><td>{chapter.allowed_clicks()}</td><th>Blocked click-like actions</th><td>{chapter.blocked_click_like_actions()}</td></tr>
        <tr><th>Walking</th><td>{escape(activity_with_percent(activity['walking'], duration))}</td><th>Sprinting</th><td>{escape(activity_with_percent(activity['sprinting'], duration))}</td></tr>
        <tr><th>Interacting</th><td>{escape(activity_with_percent(activity['interacting'], duration))}</td><th>Other / stationary</th><td>{escape(activity_with_percent(activity['other'], duration))}</td></tr>
        <tr><th>Distance</th><td>{chapter.latest_total_distance:.1f}</td><th>Sprint distance</th><td>{chapter.latest_total_sprint_distance:.1f}</td></tr>
        <tr><th>Jumps</th><td>{chapter.jumps}</td><th>Movement samples</th><td>{chapter.movement_samples}</td></tr>
        <tr><th>Empty air clicks</th><td colspan="3">{escape(format_empty_click_summary(chapter.empty_clicks))}</td></tr>
        <tr><th>Chapter 0 conditions</th><td colspan="3">{escape(chapter_zero_notes)}</td></tr>
        <tr><th>Chapter 0 validation failures</th><td colspan="3">{validation_notes}</td></tr>
        <tr><th>Top clicked creatures</th><td colspan="3">{escape(top_clicks)}</td></tr>
        <tr><th>Blocked actions</th><td colspan="3">{escape(blocked_summary)}</td></tr>
      </table>
      <div style="margin-top:10px;">
        <table>
          <tr><th>Creature card reading in this chapter</th><th class="num">Seconds</th></tr>
          {read_rows or '<tr><td colspan="2">No creature-card reads</td></tr>'}
        </table>
      </div>
    </div>
    """


def render_chapter_creature_read_row(item: tuple[str, float]) -> str:
    display_name, seconds = item
    return f"<tr><td>{escape(display_name)}</td><td class='num'>{escape(format_seconds(seconds))}</td></tr>"


def render_blocked_row(chapter: ChapterSummary) -> str:
    if not chapter.blocked_actions:
        return f"<tr><td>{escape(chapter.title)}</td><td>-</td><td class='num'>0</td></tr>"
    lines = ", ".join(f"{name} ({count})" for name, count in chapter.blocked_actions.most_common())
    return f"<tr><td>{escape(chapter.title)}</td><td>{escape(lines)}</td><td class='num'>{chapter.total_blocked_actions()}</td></tr>"


def render_chapter_zero_failure_row(record: ChapterZeroValidationRecord) -> str:
    return (
        "<tr>"
        f"<td>{escape(format_datetime(record.timestamp))}</td>"
        f"<td>{escape(record.trigger)}</td>"
        f"<td>{format_optional_bool(record.reached_depth)}</td>"
        f"<td>{format_optional_bool(record.interacted_with_creature)}</td>"
        f"<td>{format_optional_bool(record.missing_depth_requirement)}</td>"
        f"<td>{format_optional_bool(record.missing_creature_interaction)}</td>"
        "</tr>"
    )


def render_spawn_integrity_row(creature: CreatureDefinition) -> str:
    facts = " | ".join(creature.facts)
    names = ", ".join(spawn.unique_name for spawn in creature.spawns)
    return (
        "<tr>"
        f"<td>{escape(creature.display_name)}</td>"
        f"<td>{escape(CHAPTER_TITLES.get(creature.chapter_number, str(creature.chapter_number)))}</td>"
        f"<td class='num'>{len(creature.spawns)}</td>"
        f"<td>{escape(names)}</td>"
        f"<td>{escape(creature.movement_mode)}</td>"
        f"<td>{escape(facts)}</td>"
        "</tr>"
    )


def top_creature_reads(chapter: ChapterSummary) -> list[tuple[str, float]]:
    counter: collections.Counter[str] = collections.Counter()
    for record in chapter.creature_reads:
        counter[record.display_name] += record.read_duration_seconds
    return counter.most_common()


def render_validation_note_html(record: ChapterZeroValidationRecord) -> str:
    parts: list[str] = []
    if record.missing_depth_requirement:
        parts.append("missing depth requirement")
    if record.missing_creature_interaction:
        parts.append("missing creature interaction")
    if not parts:
        parts.append("validation failed without a flagged missing requirement")
    return escape(f"{format_datetime(record.timestamp)} — {record.trigger} — " + ", ".join(parts))


def activity_with_percent(seconds: float, duration: float) -> str:
    if duration <= 0:
        return format_seconds(seconds)
    return f"{format_seconds(seconds)} ({(100.0 * seconds / duration):.0f}%)"


def format_optional_bool(value: bool | None) -> str:
    if value is None:
        return "-"
    return "Yes" if value else "No"


def humanize_key(value: str) -> str:
    return value.replace("_", " ").strip().title()


def chapter_number_from_title(title: str | None) -> int | None:
    if title is None:
        return None
    return CHAPTER_TITLE_TO_NUMBER.get(title.strip())


def normalise_block_pos(text: str) -> str:
    return re.sub(r"\s+", "", text or "")


def fallback_species_key(entity_type: str | None, creature_name: str | None) -> str:
    if creature_name:
        base = re.sub(r"_[a-z]$", "", creature_name.strip().lower())
        if base:
            return base
    if entity_type:
        return entity_type.split(".")[-1].lower()
    return "unknown"


def humanize_species_key(species_key: str) -> str:
    return species_key.replace("_", " ").title()


def normalise_label(value: str | None) -> str:
    return re.sub(r"\s+", " ", (value or "").strip().lower())


def safe_bool(value: str | None) -> bool:
    return str(value or "").strip().lower() == "true"


def safe_int(value: str | None) -> int | None:
    try:
        return int(value) if value is not None else None
    except (TypeError, ValueError):
        return None


def safe_float(value: str | None) -> float | None:
    try:
        return float(value) if value is not None else None
    except (TypeError, ValueError):
        return None


def escape(text: object) -> str:
    return html.escape(str(text), quote=True)


def format_datetime(value: dt.datetime | None) -> str:
    if value is None:
        return "-"
    return value.strftime("%Y-%m-%d %H:%M:%S")


def format_timedelta(value: dt.timedelta) -> str:
    total_seconds = int(value.total_seconds())
    minutes, seconds = divmod(total_seconds, 60)
    hours, minutes = divmod(minutes, 60)
    if hours:
        return f"{hours}h {minutes}m {seconds}s"
    return f"{minutes}m {seconds}s"


def format_seconds(value: float | None) -> str:
    if value is None:
        return "-"
    if value >= 60:
        return format_timedelta(dt.timedelta(seconds=value))
    return f"{value:.1f}s"


def format_possible_count(value: int, can_change: bool) -> str:
    if value == 0 and not can_change:
        return ""
    return str(value)


def format_possible_seconds(value: float, can_change: bool) -> str:
    if value == 0.0 and not can_change:
        return ""
    return format_seconds(value)


def format_empty_click_summary(counter: collections.Counter[str]) -> str:
    total = sum(counter.values())
    if total == 0:
        return "0"

    left = counter.get("left", 0)
    right = counter.get("right", 0)
    other = total - left - right

    parts: list[str] = []
    if left:
        parts.append(f"left {left}")
    if right:
        parts.append(f"right {right}")
    if other:
        parts.append(f"other {other}")

    if not parts:
        return str(total)
    return f"{total} ({', '.join(parts)})"


def format_condition(condition: str | None, source: str | None) -> str:
    if not condition:
        return "unknown"
    if source:
        return f"{condition} ({source})"
    return condition


if __name__ == "__main__":
    raise SystemExit(main())