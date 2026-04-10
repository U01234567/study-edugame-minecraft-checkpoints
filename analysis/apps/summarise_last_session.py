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
class CheckpointRecord:
    completed_chapter: int
    next_chapter: int
    displayed_at: dt.datetime | None = None
    choice_at: dt.datetime | None = None
    prompt_displayed: bool = False
    condition: str | None = None
    choice: str | None = None
    pause_started_at: dt.datetime | None = None
    pause_finished_at: dt.datetime | None = None
    slide_keys: list[str] = dataclasses.field(default_factory=list)

    def response_seconds(self) -> float | None:
        if self.displayed_at is None or self.choice_at is None:
            return None
        return (self.choice_at - self.displayed_at).total_seconds()

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
    blocked_actions: collections.Counter[str] = dataclasses.field(default_factory=collections.Counter)
    movement_samples: int = 0
    latest_total_distance: float = 0.0
    latest_total_sprint_distance: float = 0.0
    summed_sample_distance: float = 0.0
    jumps: int = 0
    chapter_zero_conditions: list[str] = dataclasses.field(default_factory=list)

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

    def blocked_click_like_actions(self) -> int:
        return sum(self.blocked_actions.get(name, 0) for name in CLICK_LIKE_BLOCKED_ACTIONS)


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

    return [parse_creature_spawn(part) for part in split_top_level_commas(inner)]


def parse_creature_spawn(text: str) -> CreatureSpawnDefinition:
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
    stripped = text.strip()
    if stripped.startswith('"') and stripped.endswith('"'):
        return stripped[1:-1]
    return stripped


def clean_java_string(text: str) -> str:
    return text.encode("utf-8").decode("unicode_escape")


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
    raw_event_counts: collections.Counter[str] = collections.Counter()
    player_name: str | None = None
    condition: str | None = None
    condition_source: str | None = None
    questionnaire_clicks = 0
    game_end_reasons: list[str] = []
    current_active_chapter: int | None = None

    creature_by_spawn_name: dict[str, CreatureDefinition] = {}
    for creature in creatures:
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
                chapter.summed_sample_distance += safe_float(event.fields.get("sample_distance")) or 0.0

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

        elif event.event_type == "blocked_action":
            if current_active_chapter in chapters:
                chapters[current_active_chapter].blocked_actions[event.fields.get("action", "unknown")] += 1

        elif event.event_type == "chapter_zero_condition_satisfied":
            chapters[0].chapter_zero_conditions.append(event.fields.get("condition", "unknown"))

        elif event.event_type in {
            "checkpoint_displayed",
            "checkpoint_choice_made",
            "checkpoint_prompt_displayed",
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
            elif event.event_type == "checkpoint_pause_started":
                record.pause_started_at = event.timestamp
            elif event.event_type == "checkpoint_pause_finished":
                record.pause_finished_at = event.timestamp
            elif event.event_type == "checkpoint_slide_displayed":
                slide_key = event.fields.get("slide_key")
                if slide_key:
                    record.slide_keys.append(slide_key)

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

    return SessionSummary(
        log_path=log_path,
        metadata=metadata,
        events=events,
        creatures=creatures,
        chapters=chapters,
        checkpoints=checkpoints,
        condition=condition,
        condition_source=condition_source,
        player_name=player_name or metadata.get("session_player") or None,
        questionnaire_clicks=questionnaire_clicks,
        game_end_reasons=game_end_reasons,
        raw_event_counts=raw_event_counts,
    )


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
        metric_card("Questionnaire", str(session.questionnaire_clicks), "button press(es)"),
        metric_card("End reason", ", ".join(session.game_end_reasons) or "unknown"),
    ]

    chapter_overview_rows = "\n".join(
        render_chapter_overview_row(session.chapters[n], session.creatures) for n in sorted(session.chapters)
    )
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
main {{ max-width: 1700px; margin: 0 auto; padding: 18px; }}
h1, h2, h3 {{ margin: 0 0 10px 0; }}
p {{ margin: 0; }}
.small {{ font-size: 12px; color: var(--muted); }}
.num {{ text-align: right; white-space: nowrap; }}
.wrap {{ white-space: normal; }}
.card {{ background: var(--card); border: 1px solid var(--line); border-radius: 12px; padding: 14px; margin-bottom: 14px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }}
.grid {{ display: grid; gap: 12px; }}
.metric-grid {{ grid-template-columns: repeat(4, minmax(0, 1fr)); }}
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
.two-col {{ display: grid; grid-template-columns: 1.2fr 1fr; gap: 14px; }}
.three-col {{ display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14px; }}
@media (max-width: 1200px) {{
  .metric-grid {{ grid-template-columns: repeat(2, minmax(0, 1fr)); }}
  .two-col, .three-col {{ grid-template-columns: 1fr; }}
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
            <th class="num">Repeat</th>
            <th class="num">Steps*</th>
            <th class="num">Distance</th>
            <th class="num">Sprint</th>
            <th class="num">Jumps</th>
          </tr>
          {chapter_overview_rows}
        </table>
      </div>
      <div class="card">
        <h2>Checkpoint / choice timeline</h2>
        <table>
          <tr>
            <th>Transition</th>
            <th>Condition</th>
            <th>Choice</th>
            <th class="num">Response</th>
            <th>Prompt</th>
            <th>Slides</th>
          </tr>
          {checkpoint_rows}
        </table>
      </div>
    </div>

    <div class="card">
      <h2>Clicks per creature per chapter</h2>
      <table>
        <tr>
          <th>Creature</th>
          <th>Configured chapter</th>
          <th class="num">Ch0</th>
          <th class="num">Ch1</th>
          <th class="num">Ch2</th>
          <th class="num">Ch3</th>
          <th class="num">Total</th>
          <th class="num">Unique entities</th>
          <th class="num">Repeat clicks</th>
          <th>Notes</th>
        </tr>
        {creature_rows}
      </table>
    </div>

    <div class="card">
      <h2>Chapter details</h2>
      <p class="small">* Steps are a rounded interview shorthand based on logged movement distance.</p>
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
    return (
        "<tr>"
        f"<td>{escape(chapter.title)}</td>"
        f"<td class='num'>{escape(format_seconds(chapter.duration_seconds()))}</td>"
        f"<td class='num'>{escape(score_text)}</td>"
        f"<td class='num'>{len(chapter.clicks)}</td>"
        f"<td class='num'>{chapter.repeat_clicks()}</td>"
        f"<td class='num'>{round(chapter.latest_total_distance)}</td>"
        f"<td class='num'>{chapter.latest_total_distance:.1f}</td>"
        f"<td class='num'>{chapter.latest_total_sprint_distance:.1f}</td>"
        f"<td class='num'>{chapter.jumps}</td>"
        "</tr>"
    )


def render_checkpoint_row(record: CheckpointRecord) -> str:
    response = record.response_seconds()
    slide_text = ", ".join(record.slide_keys) if record.slide_keys else "-"
    prompt = "Yes" if record.prompt_displayed else "No"
    return (
        "<tr>"
        f"<td>{escape(record.label())}</td>"
        f"<td>{escape(record.condition or '-')}</td>"
        f"<td>{escape(record.choice or '-')}</td>"
        f"<td class='num'>{escape(format_seconds(response))}</td>"
        f"<td>{prompt}</td>"
        f"<td>{escape(slide_text)}</td>"
        "</tr>"
    )


def render_creature_matrix_row(session: SessionSummary, creature: CreatureDefinition) -> str:
    chapter_clicks = {0: 0, 1: 0, 2: 0, 3: 0}
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

    total = sum(chapter_clicks.values())
    notes = "; ".join(sorted(set(wrong_chapter_targets))) if wrong_chapter_targets else ""
    return (
        "<tr>"
        f"<td>{escape(creature.display_name)}</td>"
        f"<td>{escape(CHAPTER_TITLES.get(creature.chapter_number, f'Chapter {creature.chapter_number}'))}</td>"
        f"<td class='num'>{chapter_clicks[0]}</td>"
        f"<td class='num'>{chapter_clicks[1]}</td>"
        f"<td class='num'>{chapter_clicks[2]}</td>"
        f"<td class='num'>{chapter_clicks[3]}</td>"
        f"<td class='num'>{total}</td>"
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

    return f"""
    <div class="card">
      <h3>{escape(chapter.title)}</h3>
      <table>
        <tr><th>Started</th><td>{escape(format_datetime(chapter.started_at))}</td><th>Completed</th><td>{escape(format_datetime(chapter.completed_at))}</td></tr>
        <tr><th>Duration</th><td>{escape(format_seconds(chapter.duration_seconds()))}</td><th>Unique species clicked</th><td>{chapter.unique_species_clicked()}</td></tr>
        <tr><th>Total clicks</th><td>{len(chapter.clicks)}</td><th>Repeat clicks</th><td>{chapter.repeat_clicks()}</td></tr>
        <tr><th>Allowed clicks</th><td>{chapter.allowed_clicks()}</td><th>Blocked click-like actions</th><td>{chapter.blocked_click_like_actions()}</td></tr>
        <tr><th>Approx steps</th><td>{round(chapter.latest_total_distance)}</td><th>Distance</th><td>{chapter.latest_total_distance:.1f}</td></tr>
        <tr><th>Sprint distance</th><td>{chapter.latest_total_sprint_distance:.1f}</td><th>Movement samples</th><td>{chapter.movement_samples}</td></tr>
        <tr><th>Jumps</th><td>{chapter.jumps}</td><th>Chapter 0 conditions</th><td>{escape(chapter_zero_notes)}</td></tr>
        <tr><th>Top clicked creatures</th><td colspan="3">{escape(top_clicks)}</td></tr>
        <tr><th>Blocked actions</th><td colspan="3">{escape(blocked_summary)}</td></tr>
      </table>
    </div>
    """


def render_blocked_row(chapter: ChapterSummary) -> str:
    if not chapter.blocked_actions:
        return f"<tr><td>{escape(chapter.title)}</td><td>-</td><td class='num'>0</td></tr>"
    lines = ", ".join(f"{name} ({count})" for name, count in chapter.blocked_actions.most_common())
    return f"<tr><td>{escape(chapter.title)}</td><td>{escape(lines)}</td><td class='num'>{chapter.total_blocked_actions()}</td></tr>"


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


def format_condition(condition: str | None, source: str | None) -> str:
    if not condition:
        return "unknown"
    if source:
        return f"{condition} ({source})"
    return condition


if __name__ == "__main__":
    raise SystemExit(main())