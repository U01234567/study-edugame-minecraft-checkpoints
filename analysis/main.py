from __future__ import annotations

import sys

from apps.decrypt_logs import main as decrypt_logs_main
from apps.followup_email_app import main as followup_email_main
from apps.score_retention import main as score_retention_main
from apps.summarise_last_session import main as summarise_last_session_main
from apps.summarise_merged import main as merged_summary_main
from apps.summarise_survey import main as summarise_survey_main


def print_usage() -> None:
    """Print a very small command overview."""
    print("Usage:")
    print("  python main.py sum_last")
    print("  python main.py sum_survey")
    print("  python main.py sum_merged")
    print("  python main.py score_ret grader=1")
    print("  python main.py score_ret grader=2")
    print("  python main.py followup_email")
    print("  python main.py decrypt_logs")


def main(argv: list[str] | None = None) -> int:
    """
    Simple command dispatcher for local analysis tools.

    Current commands:
    - sum_last: summarise the last recorded study session as HTML
    - sum_survey: summarise the newest survey TSV
    - sum_merged: summarise merged survey data by condition as HTML
    - score_ret: open blind retention scoring interface
    - followup_email: open the follow-up email helper app
    - decrypt_logs: decrypt the newest encrypted Minecraft Study log upload
    """
    args = argv if argv is not None else sys.argv[1:]

    if not args:
        print_usage()
        return 1

    command = args[0].strip().lower()

    if command == "sum_last":
        return summarise_last_session_main()

    if command == "sum_survey":
        return summarise_survey_main()

    if command == "sum_merged":
        return merged_summary_main()

    if command == "score_ret":
        return score_retention_main(args[1:])

    if command == "followup_email":
        return followup_email_main(args[1:])

    if command == "decrypt_logs":
        return decrypt_logs_main(args[1:])

    print(f"Unknown command: {command}")
    print_usage()
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
