from __future__ import annotations

import base64
import json
import sys
from collections import Counter
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._cl_main import cl_condition_summary, cl_overall_tables, cl_per_chapter_tables, cl_quality_flags
from helpers._ctrl_main import ctrl_condition_summary, ctrl_overall_tables, ctrl_quality_flags
from helpers._eng_main import eng_condition_summary, eng_overall_tables, eng_per_chapter_tables, eng_quality_flags
from helpers._interviews_main import load_interview_overview
from helpers._logs_main import load_log_index
from helpers._main_overview import STUDY_QUESTIONS, build_merged_dataset, condition_summary, exclusion_summary
from helpers._ret_main import attach_retention_scores, build_retention_question_rows, load_retention_scores, ret_condition_summary, retention_reliability_summary
from helpers._shared import (
    CONDITION_COLOURS,
    CONDITION_ORDER,
    DISPLAY_SOURCE_PATHS,
    INTERVIEW_MANIFEST_PATH,
    INTERVIEW_TRANSCRIPTS_DIR,
    LOG_DIR,
    MERGED_OUTPUT_PATH,
    OUTPUT_DIR,
    STATIC_DIR,
    TEMPLATES_DIR,
    RETENTION_QUESTION_SPECS,
    RETENTION_SCORES_PATH,
    SURVEY_EXPORT_PATH,
    format_seconds,
    summarise,
)
from helpers._stats_main import build_inferential_statistics
from helpers._survey_io import load_survey_export

CONCEPTUAL_MODEL_PATH = STATIC_DIR / "conceptual-model-v00.06.png"

# Global blinding switch for retention coding. Keep this False while either grader
# is still scoring. Flip to True only after grading is complete and you are ready
# to inspect retention scores, notes, and interrater agreement.
SHOW_RETENTION_GRADES = False


def image_as_data_uri(path: Path) -> str:
    """Embed an image as a data URI so the HTML file remains shareable on its own."""
    if not path.exists():
        return ""
    encoded = base64.b64encode(path.read_bytes()).decode("ascii")
    return f"data:image/png;base64,{encoded}"


def optional_pause_choice_patterns(logs: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Summarise manipulated checkpoint-choice patterns for included optional-pauses logs."""
    patterns: Counter[str] = Counter()
    for log in logs:
        choices = [
            choice.get("choice", "")
            for choice in sorted(log.get("manipulated_checkpoint_choices", []), key=lambda item: item.get("moment", ""))
            if choice.get("choice")
        ]
        patterns[" → ".join(choices) if choices else "No manipulated choice logged"] += 1
    return [{"pattern": pattern, "n": count} for pattern, count in patterns.most_common()]


def seconds_mean_sd(values: list[float | int | None]) -> str:
    """Return duration Mean (SD), formatted in seconds/minutes."""
    summary = summarise(values)
    if summary["n"] == 0:
        return ""
    return f"{format_seconds(summary['mean'])} ({format_seconds(summary['sd'] or 0)})"


def time_to_sixth_creature_summary(logs: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Summarise time from chapter start to the sixth unique species by condition and chapter."""
    rows: list[dict[str, Any]] = []
    for condition in CONDITION_ORDER + ["Overall"]:
        scoped = logs if condition == "Overall" else [log for log in logs if log.get("condition") == condition]
        for chapter in (1, 2, 3):
            values = [(log.get("time_to_sixth_creature_by_chapter") or {}).get(str(chapter)) for log in scoped]
            summary = summarise(values)
            rows.append({
                "condition": condition,
                "chapter": f"Ch{chapter}",
                "n": summary["n"],
                "mean_sd": seconds_mean_sd(values),
                "min": format_seconds(summary["min"]),
                "max": format_seconds(summary["max"]),
            })
    return rows


def build_log_overview(log_index: dict[str, dict[str, Any]], participants: list[dict[str, Any]]) -> dict[str, Any]:
    """Create compact log information for included participants only."""
    included_ids = {participant["participant_id"] for participant in participants}
    logs = sorted(
        [log for participant_id, log in log_index.items() if participant_id in included_ids],
        key=lambda item: str(item.get("participant_id", "")),
    )
    optional_logs = [log for log in logs if log.get("condition") == "Optional pauses"]
    return {
        "n_logs": len(logs),
        "by_condition": {condition: sum(1 for log in logs if log.get("condition") == condition) for condition in CONDITION_ORDER},
        "optional_pause_choice_patterns": optional_pause_choice_patterns(optional_logs),
        "checkpoint_choices": [
            {"participant_id": log.get("participant_id", ""), **choice}
            for log in optional_logs
            for choice in log.get("manipulated_checkpoint_choices", [])
        ],
        "time_to_sixth_creature": time_to_sixth_creature_summary(logs),
        "logs": logs,
    }


def add_interview_comparison_summaries(interview_data: dict[str, Any], participants: list[dict[str, Any]]) -> dict[str, Any]:
    """Attach full-sample and interview-subsample condition summaries to the interview payload."""
    interview_ids = set(interview_data.get("unique_participant_ids") or [])
    interview_participants = [
        participant
        for participant in participants
        if participant.get("participant_id") in interview_ids
    ]
    enriched = dict(interview_data)
    enriched["condition_summary_full"] = condition_summary(participants)
    enriched["condition_summary_interview"] = condition_summary(interview_participants)
    enriched["n_interview_participants_in_merged_data"] = len(interview_participants)
    enriched["interview_participant_ids_not_in_merged_data"] = sorted(
        interview_ids - {participant.get("participant_id") for participant in interview_participants}
    )
    return enriched


def build_report_data() -> dict[str, Any]:
    """Load all current inputs and build the embedded app payload."""
    survey_rows, survey_header = load_survey_export(SURVEY_EXPORT_PATH)
    log_index = load_log_index(LOG_DIR)
    dataset = build_merged_dataset(survey_rows, log_index)
    participants = dataset["participants"]
    if SHOW_RETENTION_GRADES:
        retention_scores, retention_warnings = load_retention_scores(RETENTION_SCORES_PATH)
        attach_retention_scores(participants, retention_scores)
        retention_reliability = retention_reliability_summary(RETENTION_SCORES_PATH)
    else:
        retention_warnings = ["Retention grades are currently hidden because SHOW_RETENTION_GRADES is False in apps/summarise_merged.py."]
        retention_reliability = {
            "available": False,
            "method": "Retention scores and agreement statistics are hidden until SHOW_RETENTION_GRADES is set to True.",
            "rows": [],
        }

    interview_data = load_interview_overview(INTERVIEW_TRANSCRIPTS_DIR, participants, INTERVIEW_MANIFEST_PATH)

    return {
        "show_retention_grades": SHOW_RETENTION_GRADES,
        "retention_reliability": retention_reliability,
        "sources": DISPLAY_SOURCE_PATHS,
        "survey_header_count": len(survey_header),
        "condition_order": CONDITION_ORDER,
        "condition_colours": CONDITION_COLOURS,
        "study_questions": STUDY_QUESTIONS,
        "conceptual_model_data_uri": image_as_data_uri(CONCEPTUAL_MODEL_PATH),
        "audit": dataset["audit"],
        "audit_rows": dataset["audit_rows"],
        "participants": participants,
        "excluded_participants": dataset["excluded_participants"],
        "exclusion_summary": exclusion_summary(dataset["excluded_participants"]),
        "summaries": {
            "condition": condition_summary(participants),
            "retention": ret_condition_summary(participants, CONDITION_ORDER),
            "cognitive_load": cl_condition_summary(participants, CONDITION_ORDER),
            "engagement": eng_condition_summary(participants, CONDITION_ORDER),
            "control": ctrl_condition_summary(participants, CONDITION_ORDER),
        },
        "scale_tables": {
            "cognitive_load": {
                "per_chapter": cl_per_chapter_tables(participants),
                "overall": cl_overall_tables(participants),
                "merged": cl_condition_summary(participants, CONDITION_ORDER),
                "flags": cl_quality_flags(participants),
            },
            "engagement": {
                "per_chapter": eng_per_chapter_tables(participants),
                "overall": eng_overall_tables(participants),
                "merged": eng_condition_summary(participants, CONDITION_ORDER),
                "flags": eng_quality_flags(participants),
            },
            "control": {
                "overall": ctrl_overall_tables(participants),
                "merged": ctrl_condition_summary(participants, CONDITION_ORDER),
                "flags": ctrl_quality_flags(participants),
            },
        },
        "retention_questions": [{"key": key, "label": label} for key, label in RETENTION_QUESTION_SPECS],
        "retention_answer_rows": build_retention_question_rows(participants),
        "logs": build_log_overview(log_index, participants),
        "interviews": add_interview_comparison_summaries(interview_data, participants),
        "statistics": build_inferential_statistics(participants),
        "warnings": retention_warnings,
    }


def render_html(report_data: dict[str, Any]) -> str:
    """Render a standalone HTML app from external template, CSS, and JavaScript files."""
    payload = json.dumps(report_data, ensure_ascii=False).replace("</", "<\\/")
    template = (TEMPLATES_DIR / "merged_app.html").read_text(encoding="utf-8")
    css = (STATIC_DIR / "merged_app.css").read_text(encoding="utf-8")
    script = (STATIC_DIR / "merged_app.js").read_text(encoding="utf-8")
    return (
        template
        .replace("__REPORT_PAYLOAD__", payload)
        .replace("__MERGED_APP_CSS__", css)
        .replace("__MERGED_APP_JS__", script)
    )


def main() -> int:
    """Generate output/merged_summary.html as a standalone interactive HTML file."""
    report_data = build_report_data()
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    MERGED_OUTPUT_PATH.write_text(render_html(report_data), encoding="utf-8")
    print(f"Merged standalone summary written to {MERGED_OUTPUT_PATH.resolve()}")
    print(f"Included participants: {report_data['audit']['included_count']}")
    print(f"Excluded participants: {report_data['audit']['excluded_count']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())