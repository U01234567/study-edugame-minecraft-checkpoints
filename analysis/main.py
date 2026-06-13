from __future__ import annotations

from pathlib import Path
import sys

from apps.score_retention import main as score_retention_main
from apps.summarise_last_session import main as summarise_last_session_main
from apps.summarise_merged import main as merged_summary_main
from apps.summarise_survey import main as summarise_survey_main

# ---------------------------------------------------------------------------
# Data-route toggle
# ---------------------------------------------------------------------------
# PUBLIC_ROUTE=True is the colleague/researcher/reviewer route. In this mode,
# every app reads directly from publishable /data/ files. The private /raw/
# folder is not needed and should not be published.
#
# PUBLIC_ROUTE=False is the internal research-team route. In this mode, apps
# read private /raw/ files where appropriate. sum_merged first decrypts/extracts
# all raw logs, builds the inclusion/exclusion checklist from /raw/, regenerates
# /data/ for included MCIDs, and then performs the full report calculations from
# /data/ only.
PUBLIC_ROUTE = False

ANALYSIS_DIR = Path(__file__).resolve().parent
RAW_DIR = ANALYSIS_DIR / "raw"
DATA_DIR = ANALYSIS_DIR / "data"
OUTPUT_DIR = ANALYSIS_DIR / "output"
RESOURCES_DIR = ANALYSIS_DIR / "resources"

RAW_LOG_DIR = RAW_DIR / "logs"
RAW_SURVEY_DIR = RAW_DIR / "survey"
RAW_TRANSCRIPTS_DIR = RAW_DIR / "transcripts"
RAW_CONFIG_DIR = RAW_DIR / "config"

DATA_LOG_DIR = DATA_DIR / "logs"
DATA_SURVEY_PATH = DATA_DIR / "survey_export.tsv"
DATA_TRANSCRIPTS_DIR = DATA_DIR / "transcripts"
DATA_CONFIG_DIR = DATA_DIR / "config"
DATA_INTERVIEW_MANIFEST_PATH = DATA_CONFIG_DIR / "interview_manifest.json"
DATA_COLLECTION_LOCATIONS_PATH = DATA_CONFIG_DIR / "collection_locations.json"
DATA_RETENTION_SCORES_PATH = DATA_DIR / "retention_scores_merged.tsv"

MERGED_OUTPUT_PATH = OUTPUT_DIR / "merged_summary.html"
INCLUDED_MCIDS_OUTPUT_PATH = OUTPUT_DIR / "included_MCIDs.csv"

APP_PATHS = {
    "analysis_dir": ANALYSIS_DIR,
    "raw_dir": RAW_DIR,
    "data_dir": DATA_DIR,
    "output_dir": OUTPUT_DIR,
    "resources_dir": RESOURCES_DIR,
    "raw_log_dir": RAW_LOG_DIR,
    "raw_survey_dir": RAW_SURVEY_DIR,
    "raw_transcripts_dir": RAW_TRANSCRIPTS_DIR,
    "raw_config_dir": RAW_CONFIG_DIR,
    "data_log_dir": DATA_LOG_DIR,
    "data_survey_path": DATA_SURVEY_PATH,
    "data_transcripts_dir": DATA_TRANSCRIPTS_DIR,
    "data_config_dir": DATA_CONFIG_DIR,
    "data_interview_manifest_path": DATA_INTERVIEW_MANIFEST_PATH,
    "data_collection_locations_path": DATA_COLLECTION_LOCATIONS_PATH,
    "data_retention_scores_path": DATA_RETENTION_SCORES_PATH,
    "merged_output_path": MERGED_OUTPUT_PATH,
    "included_mcids_output_path": INCLUDED_MCIDS_OUTPUT_PATH,
}


def print_usage() -> None:
    """Print a very small command overview."""
    print("Usage:")
    print("  python main.py sum_last")
    print("  python main.py sum_survey")
    print("  python main.py sum_merged")
    print("  python main.py score_ret prepare")
    print("  python main.py score_ret grader=1")
    print("  python main.py score_ret grader=2")
    print("  python main.py score_ret grader=x port=8766")
    print()
    print(f"Current route: PUBLIC_ROUTE={PUBLIC_ROUTE}")


def main(argv: list[str] | None = None) -> int:
    """
    Simple command dispatcher for local analysis tools.

    Current commands:
    - sum_last: summarise the most recent study session from /data/logs or /raw/logs
    - sum_survey: summarise /data/survey_export.tsv or the newest /raw/survey/*.tsv
    - sum_merged: build the merged report; in private mode, /raw/ is used only
      to regenerate /data/, and all downstream calculations use /data/
    - score_ret: open blind retention scoring interface
    """
    args = argv if argv is not None else sys.argv[1:]

    if not args:
        print_usage()
        return 1

    command = args[0].strip().lower()

    if command == "sum_last":
        return summarise_last_session_main(public_route=PUBLIC_ROUTE, paths=APP_PATHS)

    if command == "sum_survey":
        return summarise_survey_main(public_route=PUBLIC_ROUTE, paths=APP_PATHS)

    if command == "sum_merged":
        return merged_summary_main(public_route=PUBLIC_ROUTE, paths=APP_PATHS)

    if command == "score_ret":
        return score_retention_main(args[1:])

    print(f"Unknown command: {command}")
    print_usage()
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
