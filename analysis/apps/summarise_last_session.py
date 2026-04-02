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

EVENT_LINE_RE = re.compile(
    r"^\[(?P<date>\d{4}-\d{2}-\d{2})\] "
    r"\[(?P<time>\d{2}:\d{2}:\d{2}:\d{3})\] \| "
    r"(?P<event>[^|]+?) \| "
    r"(?P<rest>.+)$"
)

CREATURE_ENTRY_RE = re.compile(
    r"Map\.entry\s*\(\s*EntityType\.(?P<entity_type>[A-Z0-9_]+)\s*,\s*"
    r"new\s+CreatureCard\s*\(\s*"
    r'"(?P<display_name>[^"]+)"\s*,\s*'
    r"List\.of\s*\((?P<facts>.*?)\)\s*"
    r"\)\s*\)",
    flags=re.DOTALL,
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
class CreatureDefinition:
    """One creature defined in StudyCreatureCards.java."""

    entity_type: str
    display_name: str
    facts: list[str]


@dataclasses.dataclass(slots=True)
class ChapterSummary:
    """Accumulated summary for one chapter."""

    chapter_number: int
    title: str
    started_at: dt.datetime | None = None
    completed_at: dt.datetime | None = None

    creature_counts: collections.Counter[str] = dataclasses.field(
        default_factory=collections.Counter
    )
    blocked_actions: collections.Counter[str] = dataclasses.field(
        default_factory=collections.Counter
    )

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

    allowed_clicks: int = 0
    blocked_clicks: int = 0

    def unique_creatures_found(self) -> int:
        """Return the number of unique creature labels interacted with."""
        return sum(1 for count in self.creature_counts.values() if count > 0)

    def checkpoint_button_mode(self) -> str:
        """
        Describe whether the checkpoint offered a real choice.

        Chapter 0 always has one continue button in the current experiment flow.
        """
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

    def total_duration(self) -> dt.timedelta:
        """Return the session duration."""
        return self.ended_at - self.started_at

    def total_allowed_clicks(self) -> int:
        """Return total allowed clicks across chapters."""
        return sum(ch.allowed_clicks for ch in self.chapters.values())

    def total_blocked_clicks(self) -> int:
        """Return total blocked click-like actions across chapters."""
        return sum(ch.blocked_clicks for ch in self.chapters.values())

    def total_blocked_actions(self) -> int:
        """Return all blocked actions across chapters, not just click-like ones."""
        return sum(sum(ch.blocked_actions.values()) for ch in self.chapters.values())

    def total_unique_creatures_found(self) -> int:
        """Return unique creatures found across the whole session."""
        seen: set[str] = set()
        for chapter in self.chapters.values():
            for label, count in chapter.creature_counts.items():
                if count > 0:
                    seen.add(label)
        return len(seen)


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
    """
    Parse StudyCreatureCards.java.

    The parser is intentionally simple and expects entries in the form:
        Map.entry(EntityType.COW, new CreatureCard("Cow", List.of(...)))
    """
    text = path.read_text(encoding="utf-8")

    creatures: list[CreatureDefinition] = []

    for match in CREATURE_ENTRY_RE.finditer(text):
        entity_type = match.group("entity_type").strip()
        display_name = match.group("display_name").strip()
        facts_text = match.group("facts")
        facts = [fact.strip() for fact in QUOTED_STRING_RE.findall(facts_text) if fact.strip()]

        creatures.append(
            CreatureDefinition(
                entity_type=entity_type,
                display_name=display_name,
                facts=facts,
            )
        )

    return creatures


def parse_log_file(path: Path) -> list[ParsedEvent]:
    """
    Parse the main study log into structured events.

    Non-event metadata lines are ignored.
    """
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
    """
    Parse the trailing key=value fields from one log line.

    The current log format uses:
        field=value | field=value | ...
    """
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
    session_start_events = [event for event in events if event.event_type == "study_session_started"]

    if session_start_events:
        last_session_id = session_start_events[-1].session_id
    else:
        last_session_id = events[-1].session_id if events else ""

    if not last_session_id:
        return None

    session_events = [event for event in events if event.session_id == last_session_id]
    if not session_events:
        return None

    started_at = session_events[0].timestamp
    ended_at = session_events[-1].timestamp

    chapters: dict[int, ChapterSummary] = {
        chapter_number: ChapterSummary(
            chapter_number=chapter_number,
            title=title,
        )
        for chapter_number, title in CHAPTER_TITLES.items()
    }

    condition: str | None = None
    condition_source: str | None = None
    player_name: str | None = None
    questionnaire_clicks = 0

    current_active_chapter: int | None = None
    title_to_chapter = {title: number for number, title in CHAPTER_TITLES.items()}

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

        elif event.event_type == "creature_card_opened":
            if current_active_chapter is not None:
                label = event.fields.get("creature_label", "Unknown")
                chapters[current_active_chapter].creature_counts[label] += 1
                chapters[current_active_chapter].allowed_clicks += 1

        elif event.event_type == "cow_clicked":
            # Backwards-compatible support for older logs.
            if current_active_chapter is not None:
                click_type = event.fields.get("click_type", "").lower()
                if click_type == "use":
                    chapters[current_active_chapter].creature_counts["Cow"] += 1
                    chapters[current_active_chapter].allowed_clicks += 1
                elif click_type == "attack":
                    chapters[current_active_chapter].blocked_actions["legacy_attack_cow"] += 1
                    chapters[current_active_chapter].blocked_clicks += 1

        elif event.event_type == "blocked_action":
            if current_active_chapter is not None:
                action = event.fields.get("action", "unknown")
                chapters[current_active_chapter].blocked_actions[action] += 1
                if action in CLICK_LIKE_BLOCKED_ACTIONS:
                    chapters[current_active_chapter].blocked_clicks += 1

        elif event.event_type == "movement_sample":
            chapter_title = event.fields.get("chapter_title")
            chapter_number = title_to_chapter.get(chapter_title or "")
            if chapter_number is not None:
                chapter = chapters[chapter_number]
                chapter.movement_samples += 1
                chapter.latest_total_distance = (
                    safe_float(event.fields.get("total_distance")) or chapter.latest_total_distance
                )
                chapter.latest_total_sprint_distance = (
                    safe_float(event.fields.get("total_sprint_distance"))
                    or chapter.latest_total_sprint_distance
                )
                chapter.summed_sample_distance += safe_float(event.fields.get("sample_distance")) or 0.0

        elif event.event_type == "jump_started":
            chapter_title = event.fields.get("chapter_title")
            chapter_number = title_to_chapter.get(chapter_title or "")
            if chapter_number is not None:
                chapters[chapter_number].jumps += 1

        elif event.event_type == "checkpoint_displayed":
            chapter_number = safe_int(event.fields.get("completed_chapter"))
            if chapter_number is not None and chapter_number in chapters:
                chapters[chapter_number].checkpoint_displayed_at = event.timestamp
                chapters[chapter_number].checkpoint_condition = event.fields.get("condition")

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
                    chapters[chapter_number].checkpoint_condition = event.fields.get("condition")

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

    return SessionSummary(
        session_id=last_session_id,
        started_at=started_at,
        ended_at=ended_at,
        condition=condition,
        condition_source=condition_source,
        player_name=player_name,
        questionnaire_clicks=questionnaire_clicks,
        events=session_events,
        chapters=chapters,
        all_creatures=creatures,
    )


# ---------------------------------------------------------------------------
# HTML rendering
# ---------------------------------------------------------------------------


def render_session_html(session: SessionSummary) -> str:
    """Render the session summary as a small self-contained HTML page."""
    overall_creature_names = [creature.display_name for creature in session.all_creatures]

    toc_items = [
        '<li><a href="#overview">Session overview</a></li>',
        *[
            f'<li><a href="#chapter-{chapter_number}">{escape(session.chapters[chapter_number].title)}</a></li>'
            for chapter_number in sorted(session.chapters)
        ],
    ]

    chapter_sections = "\n".join(
        render_chapter_section(session, session.chapters[chapter_number], overall_creature_names)
        for chapter_number in sorted(session.chapters)
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
            --colour-white: #ffffff;
            --colour-bg: #f5f7f7;
            --colour-border: #d7e2de;
            --colour-text: #1b2627;
            --colour-muted: #5b6b6c;
            --shadow-soft: 0 10px 28px rgba(40, 57, 59, 0.08);
            --radius: 12px;
            --sidebar-width: 280px;
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
            max-width: 1600px;
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
            width: 38%;
        }}

        .two-col-table col.col-value {{
            width: 62%;
        }}

        .count-table col.col-name {{
            width: 70%;
        }}

        .count-table col.col-count {{
            width: 30%;
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
                <p>A compact local report generated from the latest study log and creature-card file.</p>
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
                    <tr><th>Total configured creatures</th><td>{len(session.all_creatures)}</td></tr>
                    <tr><th>Configured creatures</th><td>{escape(", ".join(overall_creature_names))}</td></tr>
                    <tr><th>Total unique creatures found</th><td>{session.total_unique_creatures_found()}</td></tr>
                    <tr><th>Total allowed clicks</th><td>{session.total_allowed_clicks()}</td></tr>
                    <tr><th>Total blocked click-like actions</th><td>{session.total_blocked_clicks()}</td></tr>
                    <tr><th>Total blocked actions</th><td>{session.total_blocked_actions()}</td></tr>
                    <tr><th>Questionnaire button clicks</th><td>{session.questionnaire_clicks}</td></tr>
                    <tr><th>Total parsed events</th><td>{len(session.events)}</td></tr>
                </table>

                <p class="muted">
                    Current note: the configured creature list is global, so this summary
                    shows the same possible creatures for each chapter until chapter-specific
                    creature mappings are added to the game code.
                </p>
            </section>

            {chapter_sections}
        </main>
    </div>
</body>
</html>
"""


def render_chapter_section(
    session: SessionSummary,
    chapter: ChapterSummary,
    overall_creature_names: list[str],
) -> str:
    """Render one chapter block."""
    creature_rows = "\n".join(
        f"<tr><td>{escape(name)}</td><td>{chapter.creature_counts.get(name, 0)}</td></tr>"
        for name in overall_creature_names
    )

    blocked_rows = ""
    if chapter.blocked_actions:
        blocked_rows = "\n".join(
            f"<tr><td>{escape(action)}</td><td>{count}</td></tr>"
            for action, count in chapter.blocked_actions.most_common()
        )
    else:
        blocked_rows = "<tr><td colspan='2'>No blocked actions recorded.</td></tr>"

    checkpoint_response = chapter.checkpoint_response_seconds()
    checkpoint_response_text = (
        f"{checkpoint_response:.1f} seconds" if checkpoint_response is not None else "Unknown"
    )

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
            <tr><th>Unique creatures found</th><td>{chapter.unique_creatures_found()}</td></tr>
            <tr><th>Allowed clicks</th><td>{chapter.allowed_clicks}</td></tr>
            <tr><th>Blocked click-like actions</th><td>{chapter.blocked_clicks}</td></tr>
            <tr><th>Total blocked actions</th><td>{sum(chapter.blocked_actions.values())}</td></tr>
            <tr><th>Movement samples</th><td>{chapter.movement_samples}</td></tr>
            <tr><th>Distance travelled (approx. blocks)</th><td>{chapter.latest_total_distance:.2f}</td></tr>
            <tr><th>Sprint distance (approx. blocks)</th><td>{chapter.latest_total_sprint_distance:.2f}</td></tr>
            <tr><th>Summed sample distance</th><td>{chapter.summed_sample_distance:.2f}</td></tr>
            <tr><th>Jumps</th><td>{chapter.jumps}</td></tr>
            <tr><th>Checkpoint condition</th><td>{escape(chapter.checkpoint_condition or "Not applicable / unknown")}</td></tr>
            <tr><th>Checkpoint button mode</th><td>{escape(chapter.checkpoint_button_mode())}</td></tr>
            <tr><th>Checkpoint choice</th><td>{escape(chapter.checkpoint_choice or "No choice logged")}</td></tr>
            <tr><th>Checkpoint response time</th><td>{escape(checkpoint_response_text)}</td></tr>
            <tr><th>Reminder shown</th><td>{"Yes" if chapter.checkpoint_prompt_displayed else "No"}</td></tr>
        </table>

        <h3>Configured creatures and interactions</h3>
        <table class="count-table">
            <colgroup>
                <col class="col-name">
                <col class="col-count">
            </colgroup>
            <tr><th>Creature</th><th>Interactions in this chapter</th></tr>
            {creature_rows}
        </table>

        <h3>Blocked actions</h3>
        <table class="count-table">
            <colgroup>
                <col class="col-name">
                <col class="col-count">
            </colgroup>
            <tr><th>Action</th><th>Count</th></tr>
            {blocked_rows}
        </table>
    </section>
    """


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