from __future__ import annotations

import sys

from apps.summarise_last_session import main as summarise_last_session_main
from apps.summarise_survey import main as summarise_survey_main


def print_usage() -> None:
    """Print a very small command overview."""
    print("Usage:")
    print("  python main.py sum_last")
    print("  python main.py sum_survey")


def main(argv: list[str] | None = None) -> int:
    """
    Simple command dispatcher for local analysis tools.

    Current commands:
    - sum_last: summarise the last recorded study session as HTML
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

    print(f"Unknown command: {command}")
    print_usage()
    return 1


if __name__ == "__main__":
    raise SystemExit(main())