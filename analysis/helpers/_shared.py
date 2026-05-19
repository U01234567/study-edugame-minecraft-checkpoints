from __future__ import annotations

import datetime as dt
import math
from pathlib import Path
from typing import Any, Iterable, Sequence

REPO_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = REPO_ROOT / "data"
LOG_DIR = REPO_ROOT / "logs"
OUTPUT_DIR = REPO_ROOT / "output"

RESOURCES_DIR = REPO_ROOT / "resources"
STATIC_DIR = RESOURCES_DIR / "static"
TEMPLATES_DIR = RESOURCES_DIR / "templates"

INTERVIEW_TRANSCRIPTS_DIR = DATA_DIR / "transcripts"
INTERVIEW_MANIFEST_PATH = RESOURCES_DIR / "interview_manifest.json"
COLLECTION_LOCATIONS_PATH = RESOURCES_DIR / "collection_locations.json"
CONCEPTUAL_MODEL_PATH = STATIC_DIR / "conceptual-model-v00.06.png"

SURVEY_EXPORT_PATH = DATA_DIR / "survey_export.tsv"
RETENTION_SCORES_PATH = DATA_DIR / "retention_scores.tsv"
INTERVIEW_TRANSCRIPTS_PATH = INTERVIEW_TRANSCRIPTS_DIR
MERGED_OUTPUT_PATH = OUTPUT_DIR / "merged_summary.html"

DISPLAY_SOURCE_PATHS = {
    "Survey export": r".\data\survey_export.tsv",
    "Retention scores": r".\data\retention_scores.tsv",
    "Interview transcript CSVs": r".\data\transcripts\*.csv",
    "Interview manifest": r".\resources\interview_manifest.json",
    "Collection locations": r".\resources\collection_locations.json",
    "Study logs": r".\logs\study-*.log",
    "Conceptual model": r".\resources\static\conceptual-model-v00.06.png",
}

OFFICIAL_DATA_COLLECTION_START = dt.date(2026, 5, 8)

CONDITION_LABELS = {
    "cond_continue": "Required continue",
    "continue": "Required continue",
    "cond_pause": "Required pauses",
    "pause": "Required pauses",
    "cond_choice": "Optional pauses",
    "choice": "Optional pauses",
}

CONDITION_ORDER = [
    "Required continue",
    "Required pauses",
    "Optional pauses",
]

CONDITION_COLOURS = {
    "Required continue": "#2563eb",
    "Required pauses": "#f97316",
    "Optional pauses": "#16a34a",
    "Overall": "#111827",
    "Missing / invalid": "#b42318",
}

GENDER_ORDER = [
    "Male",
    "Female",
    "Other",
    "Unknown / missing",
]

AGE_BIN_ORDER = [
    "<18",
    "18-24",
    "25-34",
    "35-44",
    "45-54",
    "55+",
    "Unknown / missing",
]

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
    ("retro_tv_robot", "Retro tv robot"),
    ("scrambler_king", "Scrambler king"),
    ("walking_robot_guy", "Walking robot guy"),
    ("wardigo", "Wardigo"),
]
CREATURE_NAME_BY_ID = dict(CREATURES)
CREATURE_TOTAL = len(CREATURES)
IGNORED_SEEN_EXTRAS = {"cow", "chicken", "pig"}
MAX_RETENTION_SLOTS = 18

RETENTION_QUESTION_SPECS = [
    ("img1", "IMAGE + What is the name of this creature?"),
    ("img2", "IMAGE + What are unique facts about this creature?"),
    ("name1", "NAME + What does this creature look like?"),
    ("name2", "NAME + Where in the game did you find this creature? Name the chapter and specific place if you can."),
]

EXCLUSION_CRITERIA = [
    "Survey or log start date before 8 May 2026.",
    "Survey Progress column is not 100.",
    "No matching study log for the survey MCID.",
    "The log does not contain consent_choice=agree_and_continue.",
    "The log does not show chapter_completed for Chapters 0, 1, 2, and 3.",
    "The log does not show at least one creature_card_closed event in each learning chapter: Chapter 1, Chapter 2, and Chapter 3.",
]

CHAPTER_TITLE_TO_NUMBER = {
    "Chapter 0 (Get Started)": 0,
    "Chapter 1 (The Museum)": 1,
    "Chapter 2 (The Farm)": 2,
    "Chapter 3 (The Jungle)": 3,
}

SCALE_VALUE_COLUMNS = [
    *[f"cl_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 8)],
    *[f"cl_overall_scores_{index}" for index in range(1, 12)],
    *[f"eng_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 6)],
    *[f"eng_overall_scores_{index}" for index in range(1, 5)],
    "ctrl_scores_1",
    "ctrl_scores_2",
]


def clean(value: object) -> str:
    """Return a stripped string, with None represented as an empty string."""
    if value is None:
        return ""
    return str(value).strip()


def parse_numeric(value: object) -> float | None:
    """Parse a numeric value from TSV/log text. Invalid and empty values become None."""
    text = clean(value).replace(",", ".")
    if not text:
        return None

    try:
        number = float(text)
    except ValueError:
        return None

    if math.isnan(number):
        return None

    return number


def parse_int(value: object) -> int | None:
    """Parse an integer when possible, including integer-looking floats."""
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return None
    return int(number)


def parse_age(value: object) -> int | None:
    """Parse age in years from a survey cell."""
    age = parse_int(value)
    if age is None or age < 0 or age > 120:
        return None
    return age


def parse_bool(value: object) -> bool | None:
    """Parse Java-style boolean strings while preserving unknowns as None."""
    text = clean(value).lower()
    if text == "true":
        return True
    if text == "false":
        return False
    return None


def parse_datetime(value: object) -> dt.datetime | None:
    """Parse date-time formats used in Qualtrics exports and Java study logs."""
    text = clean(value)
    if not text:
        return None

    normalised = text.replace("T", " ").replace("Z", "")
    for fmt in (
        "%Y-%m-%d %H:%M:%S:%f",
        "%Y-%m-%d %H:%M:%S.%f",
        "%Y-%m-%d %H:%M:%S",
        "%Y-%m-%d %H:%M",
        "%Y-%m-%d",
        "%d/%m/%Y %H:%M:%S",
        "%d/%m/%Y %H:%M",
        "%d/%m/%Y",
        "%m/%d/%Y %H:%M:%S",
        "%m/%d/%Y %H:%M",
        "%m/%d/%Y",
    ):
        try:
            return dt.datetime.strptime(normalised, fmt)
        except ValueError:
            pass

    try:
        return dt.datetime.fromisoformat(normalised)
    except ValueError:
        return None


def display_datetime(value: object) -> str:
    """Return a compact display timestamp."""
    parsed = parse_datetime(value)
    if parsed is None:
        return clean(value)
    return parsed.strftime("%Y-%m-%d %H:%M:%S")


def date_is_before_official_start(value: object) -> bool:
    """Return True when a parsed date lies before the official start date."""
    parsed = parse_datetime(value)
    return parsed is not None and parsed.date() < OFFICIAL_DATA_COLLECTION_START


def duration_seconds_between(start: object, end: object) -> float | None:
    """Return the duration between two parseable date-times in seconds."""
    parsed_start = parse_datetime(start)
    parsed_end = parse_datetime(end)
    if parsed_start is None or parsed_end is None:
        return None

    seconds = (parsed_end - parsed_start).total_seconds()
    if seconds < 0:
        return None
    return seconds


def format_seconds(total_seconds: float | None) -> str:
    """Format a duration in seconds as h m s."""
    if total_seconds is None or total_seconds < 0:
        return ""

    seconds = int(round(total_seconds))
    hours, remainder = divmod(seconds, 3600)
    minutes, seconds = divmod(remainder, 60)

    if hours:
        return f"{hours}h {minutes}m {seconds}s"
    if minutes:
        return f"{minutes}m {seconds}s"
    return f"{seconds}s"


def duration_between(start: object, end: object) -> str:
    """Return a display duration between two parseable date-times."""
    return format_seconds(duration_seconds_between(start, end))


def mean(values: Iterable[float | None]) -> float | None:
    """Return the arithmetic mean of non-missing values."""
    valid = [float(value) for value in values if value is not None and not math.isnan(float(value))]
    if not valid:
        return None
    return sum(valid) / len(valid)


def rounded(value: float | None, digits: int = 2) -> float | None:
    """Round a value for display while preserving None."""
    if value is None:
        return None
    return round(float(value), digits)


def two_decimals(value: float | None) -> str:
    """Format a value with exactly two decimals."""
    if value is None:
        return ""
    return f"{float(value):.2f}"


def summarise(values: Iterable[float | None]) -> dict[str, float | int | None]:
    """Return n, mean, median, sd, min, and max for a numeric sequence."""
    valid = sorted(float(value) for value in values if value is not None and not math.isnan(float(value)))
    if not valid:
        return {"n": 0, "mean": None, "median": None, "sd": None, "min": None, "max": None}

    n = len(valid)
    middle = n // 2
    median = valid[middle] if n % 2 else (valid[middle - 1] + valid[middle]) / 2

    sd = None
    if n > 1:
        average = sum(valid) / n
        sd = math.sqrt(sum((value - average) ** 2 for value in valid) / (n - 1))

    return {
        "n": n,
        "mean": rounded(sum(valid) / n),
        "median": rounded(median),
        "sd": rounded(sd),
        "min": rounded(valid[0]),
        "max": rounded(valid[-1]),
    }


def mean_sd_text(values: Iterable[float | None]) -> str:
    """Return Mean (SD), forced to two decimals."""
    summary = summarise(values)
    if summary["n"] == 0:
        return ""
    sd = summary["sd"] if summary["sd"] is not None else 0.0
    return f"{float(summary['mean']):.2f} ({float(sd):.2f})"


def normalise_gender(value: object) -> str:
    """Normalise the Qualtrics gender codes used in this project."""
    text = clean(value)

    if text == "1":
        return "Male"
    if text == "2":
        return "Female"
    if text in {"3", "4", "5"}:
        return "Other"

    lowered = text.lower()
    if not lowered:
        return "Unknown / missing"
    if lowered == "male":
        return "Male"
    if lowered == "female":
        return "Female"
    if lowered in {"other", "prefer not to say", "nonbinary", "non-binary", "transgender"}:
        return "Other"

    return "Unknown / missing"


def age_bin(age: int | None) -> str:
    """Return a display bin for an age value."""
    if age is None:
        return "Unknown / missing"
    if age < 18:
        return "<18"
    if age <= 24:
        return "18-24"
    if age <= 34:
        return "25-34"
    if age <= 44:
        return "35-44"
    if age <= 54:
        return "45-54"
    return "55+"


def canonical_condition(raw_condition: object) -> str | None:
    """Map the exact Java condition identifiers to stable display labels."""
    key = clean(raw_condition).lower()
    return CONDITION_LABELS.get(key)


def first_present(row: dict[str, Any], names: Sequence[str]) -> str:
    """Return the first non-empty value for a list of possible column names."""
    lower_lookup = {key.lower(): key for key in row}

    for name in names:
        if name in row and clean(row[name]):
            return clean(row[name])

        actual = lower_lookup.get(name.lower())
        if actual is not None and clean(row[actual]):
            return clean(row[actual])

    return ""


def mcid_from_row(row: dict[str, Any]) -> str:
    """Return the MCID only; do not fall back to Qualtrics response identifiers."""
    return first_present(row, ["MCID"])


def progress_is_complete(value: object) -> bool:
    """Return True when Qualtrics Progress is exactly 100, allowing numeric formatting."""
    number = parse_numeric(value)
    return number == 100


def scale_value(row: dict[str, Any], column: str) -> str:
    """Return a scale value as exported, preserving blanks for auditability."""
    return clean(row.get(column))