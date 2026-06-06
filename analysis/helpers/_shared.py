from __future__ import annotations

import datetime as dt
import math
from pathlib import Path
from typing import Any, Iterable, Sequence

REPO_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = REPO_ROOT / "data"
RAW_DIR = REPO_ROOT / "raw"
OUTPUT_DIR = REPO_ROOT / "output"
RESOURCES_DIR = REPO_ROOT / "resources"
STATIC_DIR = RESOURCES_DIR / "static"
TEMPLATES_DIR = RESOURCES_DIR / "templates"

DATA_CONFIG_DIR = DATA_DIR / "config"
RAW_CONFIG_DIR = RAW_DIR / "config"
LOG_DIR = DATA_DIR / "logs"
RAW_LOG_DIR = RAW_DIR / "logs"
RAW_SURVEY_DIR = RAW_DIR / "survey"
RAW_TRANSCRIPTS_DIR = RAW_DIR / "transcripts"
INTERVIEW_TRANSCRIPTS_DIR = DATA_DIR / "transcripts"
INTERVIEW_MANIFEST_PATH = DATA_CONFIG_DIR / "interview_manifest.json"
COLLECTION_LOCATIONS_PATH = DATA_CONFIG_DIR / "collection_locations.json"
RETENTION_RUBRICS_PATH = DATA_CONFIG_DIR / "retention_rubrics.json"
CONCEPTUAL_MODEL_PATH = STATIC_DIR / "conceptual-model-v00.06.png"
SURVEY_EXPORT_PATH = DATA_DIR / "survey_export.tsv"
RETENTION_SCORES_PATH = DATA_DIR / "retention_scores_merged.tsv"
RETENTION_ANSWERS_PATH = DATA_DIR / "retention_answers.tsv"
GENAI_SCORES_PATH = DATA_DIR / "retention_scores_genai.tsv"
GRADER1_SCORES_PATH = DATA_DIR / "retention_scores_grader1.tsv"
GRADER2_SCORES_PATH = DATA_DIR / "retention_scores_grader2.tsv"
MERGED_OUTPUT_PATH = OUTPUT_DIR / "merged_summary.html"
INCLUDED_MCIDS_OUTPUT_PATH = OUTPUT_DIR / "included_MCIDs.csv"
INTERVIEW_TRANSCRIPTS_PATH = INTERVIEW_TRANSCRIPTS_DIR

DISPLAY_SOURCE_PATHS = {
    "Survey export": r".\data\survey_export.tsv",
    "Retention answers": r".\data\retention_answers.tsv",
    "Retention scores GenAI": r".\data\retention_scores_genai.tsv",
    "Retention scores grader 1": r".\data\retention_scores_grader1.tsv",
    "Retention scores grader 2": r".\data\retention_scores_grader2.tsv",
    "Retention scores merged": r".\data\retention_scores_merged.tsv",
    "Interview transcript CSVs": r".\data\transcripts\*.csv",
    "Interview manifest": r".\data\config\interview_manifest.json",
    "Collection locations": r".\data\config\collection_locations.json",
    "Study logs": r".\data\logs\*.csv",
    "Conceptual model": r".\resources\static\conceptual-model-v00.06.png",
}

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
IGNORED_SEEN_EXTRAS = {"cow", "chicken", "pig"}
MAX_RETENTION_SLOTS = 18

RETENTION_QUESTION_SPECS = [
    ("img1", "IMAGE + What is the name of this creature?"),
    ("img2", "IMAGE + What are unique facts about this creature?"),
    ("name1", "NAME + What does this creature look like?"),
    ("name2", "NAME + Where in the game did you find this creature? Name the chapter and specific place if you can."),
]

EXCLUSION_CRITERIA = [
    "Survey or log start date outside 8 May 2026 to 5 June 2026.",
    "Survey Progress column is not 100.",
    "No matching study log for the survey MCID.",
    "The log does not contain consent_choice=agree_and_continue.",
    "The log does not show chapter_completed for Chapters 0, 1, 2, and 3.",
    "The log does not show at least one creature_card_closed event in each learning chapter: Chapter 1, Chapter 2, and Chapter 3.",
]

SCALE_VALUE_COLUMNS = [
    *[f"cl_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 8)],
    *[f"cl_overall_scores_{index}" for index in range(1, 12)],
    *[f"eng_ch{chapter}_scores_{index}" for chapter in (1, 2, 3) for index in range(1, 6)],
    *[f"eng_overall_scores_{index}" for index in range(1, 5)],
    "ctrl_scores_1",
    "ctrl_scores_2",
]

OFFICIAL_DATA_COLLECTION_START = dt.date(2026, 5, 8)
OFFICIAL_DATA_COLLECTION_END = dt.date(2026, 6, 5)

CONDITION_LABELS = {
    "cond_continue": "Required continue",
    "continue": "Required continue",
    "required_continue": "Required continue",
    "required continue": "Required continue",
    "cond_pause": "Required pauses",
    "pause": "Required pauses",
    "required_pause": "Required pauses",
    "required_pauses": "Required pauses",
    "required pauses": "Required pauses",
    "cond_choice": "Optional pauses",
    "choice": "Optional pauses",
    "optional_pause": "Optional pauses",
    "optional_pauses": "Optional pauses",
    "optional pauses": "Optional pauses",
}
CONDITION_ORDER = ["Required continue", "Required pauses", "Optional pauses"]
CONDITION_COLOURS = {
    "Required continue": "#2563eb",
    "Required pauses": "#f97316",
    "Optional pauses": "#16a34a",
    "Overall": "#111827",
    "Missing / invalid": "#b42318",
}
GENDER_ORDER = ["Male", "Female", "Other", "Unknown / missing"]
AGE_BIN_ORDER = ["<18", "18–24", "25–34", "35–44", "45–54", "55+", "Unknown / missing"]
CREATURE_TOTAL = 18
CHAPTER_TITLE_TO_NUMBER = {
    "Chapter 0 (Get Started)": 0,
    "Chapter 1 (The Museum)": 1,
    "Chapter 2 (The Farm)": 2,
    "Chapter 3 (The Jungle)": 3,
}


def clean(value: object) -> str:
    """Return a stripped string, with None represented as an empty string."""
    if value is None:
        return ""
    return str(value).strip()


def parse_numeric(value: object) -> float | None:
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
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return None
    return int(number)


def parse_bool(value: object) -> bool | None:
    text = clean(value).lower()
    if text == "true":
        return True
    if text == "false":
        return False
    return None


def parse_age(value: object) -> int | None:
    age = parse_int(value)
    if age is None or age < 0 or age > 120:
        return None
    return age


def parse_datetime(value: object) -> dt.datetime | None:
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
    parsed = parse_datetime(value)
    if parsed is None:
        return clean(value)
    return parsed.strftime("%Y-%m-%d %H:%M:%S")


def date_is_before_official_start(value: object) -> bool:
    parsed = parse_datetime(value)
    return parsed is not None and parsed.date() < OFFICIAL_DATA_COLLECTION_START


def date_is_outside_official_window(value: object) -> bool:
    parsed = parse_datetime(value)
    if parsed is None:
        return False
    return parsed.date() < OFFICIAL_DATA_COLLECTION_START or parsed.date() > OFFICIAL_DATA_COLLECTION_END


def duration_seconds_between(start: object, end: object) -> float | None:
    parsed_start = parse_datetime(start)
    parsed_end = parse_datetime(end)
    if parsed_start is None or parsed_end is None:
        return None
    seconds = (parsed_end - parsed_start).total_seconds()
    if seconds < 0:
        return None
    return seconds


def format_seconds(total_seconds: float | None) -> str:
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
    return format_seconds(duration_seconds_between(start, end))


def mean(values: Iterable[float | int | None]) -> float | None:
    valid = [float(value) for value in values if value is not None and not math.isnan(float(value))]
    if not valid:
        return None
    return sum(valid) / len(valid)


def rounded(value: float | None, digits: int = 2) -> float | None:
    if value is None:
        return None
    return round(float(value), digits)


def two_decimals(value: float | int | None) -> str:
    if value is None:
        return ""
    return f"{float(value):.2f}"


def summarise(values: Iterable[float | int | None]) -> dict[str, float | int | None]:
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


def mean_sd_text(values: Iterable[float | int | None]) -> str:
    summary = summarise(values)
    if summary["n"] == 0:
        return "—"
    if summary["sd"] is None:
        return f"{summary['mean']:.2f} (—)"
    return f"{summary['mean']:.2f} ({summary['sd']:.2f})"


def canonical_condition(value: object) -> str:
    text = clean(value)
    if not text:
        return ""
    key = text.strip().lower().replace("-", "_").replace(" ", "_")
    return CONDITION_LABELS.get(key, CONDITION_LABELS.get(text.lower(), text))


def first_present(row: dict[str, Any] | None, names: list[str]) -> str:
    if row is None:
        return ""
    lower_lookup = {key.lower(): key for key in row.keys()}
    for name in names:
        if name in row and clean(row.get(name)):
            return clean(row.get(name))
        actual = lower_lookup.get(name.lower())
        if actual is not None and clean(row.get(actual)):
            return clean(row.get(actual))
    return ""


def mcid_from_row(row: dict[str, Any] | None) -> str:
    value = first_present(row, ["MCID", "mcid", "participant_id", "Participant ID", "session_id"])
    return value.upper()


def survey_start(row: dict[str, Any] | None) -> str:
    return first_present(row, ["startDate", "StartDate", "Start Date", "Start date"])


def survey_end(row: dict[str, Any] | None) -> str:
    return first_present(row, ["endDate", "EndDate", "End Date", "End date"])


def survey_progress(row: dict[str, Any] | None) -> str:
    return first_present(row, ["progress", "Progress"])


def progress_is_complete(value: object) -> bool:
    number = parse_numeric(value)
    return number is not None and number >= 100


def delayed_flag(row: dict[str, Any] | None) -> bool:
    return clean(first_present(row, ["DELAYED", "Delayed", "delayed"])).lower() in {"1", "true", "yes", "delayed"}


def delayed_included_flag(row: dict[str, Any] | None) -> bool:
    value = clean(first_present(row, ["delayed_included", "DELAYED_INCLUDED", "DelayedIncluded"]))
    if not value:
        return True
    return value.lower() in {"1", "true", "yes", "included"}


def normalise_gender(value: object) -> str:
    text = clean(value).lower()
    if not text:
        return "Unknown / missing"
    if text in {"1", "male", "man", "m"}:
        return "Male"
    if text in {"2", "female", "woman", "f"}:
        return "Female"
    if text in {"3", "other", "non-binary", "nonbinary", "prefer to self-describe"}:
        return "Other"
    return clean(value) or "Unknown / missing"


def age_bin(age: int | None) -> str:
    if age is None:
        return "Unknown / missing"
    if age < 18:
        return "<18"
    if age <= 24:
        return "18–24"
    if age <= 34:
        return "25–34"
    if age <= 44:
        return "35–44"
    if age <= 54:
        return "45–54"
    return "55+"


def scale_value(row: dict[str, Any] | None, column: str) -> str:
    """Return a scale value as exported, preserving blanks for auditability."""
    if row is None:
        return ""
    return clean(row.get(column))
