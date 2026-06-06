from __future__ import annotations

import csv
import json
import sys
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._cl_main import cl_condition_summary, cl_overall_tables, cl_per_chapter_tables, cl_quality_flags
from helpers._ctrl_main import ctrl_condition_summary, ctrl_overall_tables, ctrl_quality_flags
from helpers._eng_main import eng_condition_summary, eng_overall_tables, eng_per_chapter_tables, eng_quality_flags
from helpers._delayed_response_filter import DELAYED_INCLUDED_COLUMN, build_delayed_response_checklist_from_annotations, delayed_included_column_present, delayed_included_values_missing
from helpers._interviews_main import load_interview_overview
from helpers._logs_main import build_game_log_report, load_log_index
from helpers._main_overview import (
    build_merged_dataset,
    build_raw_inclusion_checklist,
    condition_summary,
    controlling_variable_tables,
    demographic_distributions,
)
from helpers._raw_data_pipeline import decrypt_all_log_archives, publish_data_for_included_mcids
from helpers._ret_main import (
    attach_retention_scores,
    build_retention_question_rows,
    load_retention_scores,
    ret_condition_summary,
    retention_reliability_summary,
)
from helpers._retention_coding import (
    CREATURE_INFO_HTML_PATH,
    GENAI_PROMPT_PATH,
    GENAI_SCORES_PATH,
    GRADER1_SCORES_PATH,
    GRADER2_SCORES_PATH,
    SCORING_RUBRICS_HTML_PATH,
    prepare_retention_answer_files,
    write_prompt_score_file,
)
from helpers._shared import (
    COLLECTION_LOCATIONS_PATH,
    DATA_CONFIG_DIR,
    DATA_DIR,
    INTERVIEW_MANIFEST_PATH,
    INTERVIEW_TRANSCRIPTS_DIR,
    RETENTION_SCORES_PATH,
    RETENTION_QUESTION_SPECS,
    CONDITION_ORDER,
    CONDITION_COLOURS,
    INCLUDED_MCIDS_OUTPUT_PATH,
    LOG_DIR,
    MERGED_OUTPUT_PATH,
    OUTPUT_DIR,
    RAW_CONFIG_DIR,
    RAW_DIR,
    RAW_LOG_DIR,
    RAW_SURVEY_DIR,
    RAW_TRANSCRIPTS_DIR,
    RESOURCES_DIR,
    STATIC_DIR,
    SURVEY_EXPORT_PATH,
    TEMPLATES_DIR,
)
from helpers._stats_main import build_inferential_statistics
from helpers._survey_io import load_survey_export

PUBLIC_ROUTE = False


def log_step(message: str) -> None:
    print(f"[sum_merged] {message}", flush=True)


def newest_file(directory: Path, pattern: str) -> Path:
    if not directory.exists():
        raise FileNotFoundError(f"Expected directory at {directory}")
    candidates = sorted(
        (path for path in directory.glob(pattern) if path.is_file()),
        key=lambda item: (item.stat().st_mtime, item.name),
    )
    if not candidates:
        raise FileNotFoundError(f"No {pattern} files found in {directory}")
    return candidates[-1]


def route_paths(paths: dict[str, Path] | None = None) -> dict[str, Path]:
    defaults: dict[str, Path] = {
        "analysis_dir": REPO_ROOT,
        "raw_dir": RAW_DIR,
        "data_dir": DATA_DIR,
        "output_dir": OUTPUT_DIR,
        "resources_dir": RESOURCES_DIR,
        "raw_log_dir": RAW_LOG_DIR,
        "raw_survey_dir": RAW_SURVEY_DIR,
        "raw_transcripts_dir": RAW_TRANSCRIPTS_DIR,
        "raw_config_dir": RAW_CONFIG_DIR,
        "data_log_dir": LOG_DIR,
        "data_survey_path": SURVEY_EXPORT_PATH,
        "data_config_dir": DATA_CONFIG_DIR,
        "data_collection_locations_path": COLLECTION_LOCATIONS_PATH,
        "data_transcripts_dir": INTERVIEW_TRANSCRIPTS_DIR,
        "data_interview_manifest_path": INTERVIEW_MANIFEST_PATH,
        "data_retention_scores_path": RETENTION_SCORES_PATH,
        "merged_output_path": MERGED_OUTPUT_PATH,
        "included_mcids_output_path": INCLUDED_MCIDS_OUTPUT_PATH,
    }
    if paths:
        defaults.update({key: Path(value) for key, value in paths.items()})
    return defaults


def write_included_mcids(included_ids: list[str], output_path: Path) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["MCID"], lineterminator="\n")
        writer.writeheader()
        for participant_id in sorted(included_ids):
            writer.writerow({"MCID": participant_id})


def load_text(path: Path) -> str:
    if not path.exists():
        raise FileNotFoundError(f"Expected template/static file at {path}")
    return path.read_text(encoding="utf-8")


def render_html(payload: dict[str, Any], paths: dict[str, Path]) -> str:
    template = load_text(TEMPLATES_DIR / "merged_app.html")
    css = load_text(STATIC_DIR / "merged_app.css")
    js = load_text(STATIC_DIR / "merged_app.js")
    report_payload = json.dumps(payload, ensure_ascii=False).replace("</", "<\\/")
    return (
        template
        .replace("__MERGED_APP_CSS__", css)
        .replace("__REPORT_PAYLOAD__", report_payload)
        .replace("__MERGED_APP_JS__", js)
    )


def public_route_raw_block() -> dict[str, Any]:
    return {
        "mode": "public",
        "title": "Exclusion / inclusion based on /raw/",
        "description": "This checklist is not evaluated because PUBLIC_ROUTE in main.py is set to True. This route uses the already prepared publishable /data/ files; private /raw/ files are not required and should not be published.",
        "rows": [
            {
                "reason": "Raw inclusion/exclusion checklist not evaluated on the colleague/researcher/reviewer route",
                "n": "—",
                "mcids": "—",
            }
        ],
        "included_ids": [],
        "diagnostics": {},
    }


def _relative_data_label(path: Path, paths: dict[str, Path]) -> str:
    try:
        return "/" + str(path.resolve().relative_to(paths["analysis_dir"].resolve())).replace("\\", "/")
    except ValueError:
        return str(path)


def _retention_scoring_problem_message(problems: list[str], paths: dict[str, Path], *, final_stats: bool) -> str:
    """Return a concise terminal message for common retention-scoring setup states."""
    if not problems:
        return ""

    score_problem = "score (0-4) must be an integer from 0 to 4"
    confidence_problem = "confidence (0-100%) must be a number from 0 to 100"
    unfinished_score_confidence_rows = sum(
        1
        for problem in problems
        if score_problem in problem or confidence_problem in problem
    )

    genai_path = _relative_data_label(GENAI_SCORES_PATH, paths)
    prompt_path = _relative_data_label(GENAI_PROMPT_PATH, paths)
    rubrics_path = _relative_data_label(SCORING_RUBRICS_HTML_PATH, paths)
    creature_path = _relative_data_label(CREATURE_INFO_HTML_PATH, paths)

    if unfinished_score_confidence_rows >= 4:
        prefix = (
            "Cannot calculate final retention statistics yet: "
            if final_stats
            else "Retention scores merged file not rebuilt yet: "
        )
        return (
            prefix
            + f"GenAI scoring is not ready. Fill {genai_path} first. "
            + f"Use {prompt_path} and attach {genai_path}, {rubrics_path}, and {creature_path}. "
            + "The score/confidence columns appear to be empty or unfinished."
        )

    preview = " | ".join(problems[:5])
    suffix = f" ({len(problems) - 5} more not shown)" if len(problems) > 5 else ""

    if final_stats:
        return (
            "Cannot calculate final retention statistics yet. "
            "Resolve retention scoring first. Problems: "
            + preview
            + suffix
        )

    return (
        "Retention scores merged file not rebuilt yet. This is okay before GenAI/human scoring is complete. "
        "Problems: "
        + preview
        + suffix
    )


def validate_public_data_files(paths: dict[str, Path]) -> None:
    missing: list[str] = []

    survey_path = paths["data_survey_path"]
    if not survey_path.exists():
        missing.append(_relative_data_label(survey_path, paths))

    log_dir = paths["data_log_dir"]
    if not log_dir.exists():
        missing.append(_relative_data_label(log_dir, paths) + "/")
    elif not any(log_dir.glob("*.csv")):
        missing.append(_relative_data_label(log_dir, paths) + "/*.csv")

    for key in ("data_collection_locations_path", "data_interview_manifest_path"):
        path = paths.get(key)
        if path is not None and not path.exists():
            missing.append(_relative_data_label(path, paths))

    for path in (GENAI_SCORES_PATH, GRADER1_SCORES_PATH, GRADER2_SCORES_PATH):
        if not path.exists():
            missing.append(_relative_data_label(path, paths))

    if missing:
        raise FileNotFoundError(
            "PUBLIC_ROUTE=True requires publishable /data/ files generated by the private route. "
            "Missing: " + ", ".join(missing) + ". "
            "Run sum_merged once with PUBLIC_ROUTE=False to regenerate /data/, then switch PUBLIC_ROUTE=True."
        )


def require_delayed_included_column(survey_rows: list[dict[str, str]], header: list[str], survey_path: Path) -> None:
    if not delayed_included_column_present(header):
        raise RuntimeError(
            f"{survey_path} is missing the required {DELAYED_INCLUDED_COLUMN!r} column. "
            "Run sum_merged once with PUBLIC_ROUTE=False so /data/survey_export.tsv is regenerated with "
            "persisted delayed-response inclusion flags."
        )
    missing_flags = delayed_included_values_missing(survey_rows)
    if missing_flags:
        preview = ", ".join(missing_flags[:20])
        suffix = "..." if len(missing_flags) > 20 else ""
        raise RuntimeError(
            f"{survey_path} has DELAYED row(s) without a true/false {DELAYED_INCLUDED_COLUMN!r} value: "
            f"{preview}{suffix}. Regenerate /data/ with PUBLIC_ROUTE=False."
        )


def add_interview_comparison_summaries(interview_data: dict[str, Any], participants: list[dict[str, Any]]) -> dict[str, Any]:
    """Attach full-sample and interview-subsample summaries for the interview viewer."""
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




def slim_interview_overview(overview: dict[str, Any]) -> dict[str, Any]:
    """Keep the interview payload focused; do not embed full transcript turns."""
    transcripts = []
    for transcript in overview.get("transcripts", []):
        transcripts.append({
            "transcript_id": transcript.get("transcript_id", ""),
            "filename": transcript.get("filename", ""),
            "title": transcript.get("title", ""),
            "speaker_ids": ", ".join(transcript.get("speaker_ids", [])),
            "speaker_count": transcript.get("speaker_count", 0),
            "n_turns": transcript.get("n_turns", 0),
            "category_labels": "; ".join(transcript.get("category_labels", [])),
            "notes": transcript.get("notes", ""),
        })
    return {
        "available": overview.get("available", False),
        "n_files": overview.get("n_files", 0),
        "n_turns": overview.get("n_turns", 0),
        "n_unique_participants": len(overview.get("unique_participant_ids", [])),
        "unique_participant_ids": ", ".join(overview.get("unique_participant_ids", [])),
        "category_rows": overview.get("category_rows", []),
        "type_rows": overview.get("type_rows", []),
        "transcripts": transcripts,
        "notes": overview.get("notes", []),
    }

def route_description(public_route: bool) -> str:
    if public_route:
        return (
            "The value of PUBLIC_ROUTE in main.py was set to True, which means this report uses "
            "the colleague/researcher/reviewer route: all calculations read directly from /data/, and /raw/ is not required. "
            "The delayed-response filter still runs from /data/survey_export.tsv."
        )
    return (
        "The value of PUBLIC_ROUTE in main.py was set to False, which means this report uses "
        "the internal research-team route: /raw/ is used only for the inclusion/exclusion block and for rebuilding /data/. "
        "All statistics shown below are calculated from /data/, after applying the delayed-response filter."
    )


def private_route_prepare_data(paths: dict[str, Path]) -> dict[str, Any]:
    log_step("Private route: checking /raw/logs/ for zipped or encrypted log archives.")
    extraction = decrypt_all_log_archives(paths["raw_log_dir"])
    if extraction.copied or extraction.decrypted_archives or extraction.processed_archives:
        log_step(
            "Archive processing complete: "
            f"{len(extraction.processed_archives)} archive(s) processed, "
            f"{len(extraction.decrypted_archives)} decrypted, "
            f"{len(extraction.copied)} log(s) copied."
        )
    else:
        log_step("No new archives needed extracting.")
    for error in extraction.errors:
        print(f"[sum_merged] WARNING: {error}", flush=True)

    raw_survey_path = newest_file(paths["raw_survey_dir"], "*.tsv")
    log_step(f"Building raw inclusion/exclusion checklist from {raw_survey_path.name} and /raw/logs/.")
    raw_survey_rows, _headers = load_survey_export(raw_survey_path)
    raw_checklist = build_raw_inclusion_checklist(raw_survey_rows, paths["raw_log_dir"])

    diagnostics = {
        "hidden_non_consent_log_ids": raw_checklist["hidden_non_consent_log_ids"],
        "hidden_non_consent_in_survey": raw_checklist["hidden_non_consent_in_survey"],
        "duplicate_log_ids": raw_checklist["duplicate_log_ids"],
        "survey_more_than_two_rows_ids": raw_checklist["survey_more_than_two_rows_ids"],
        "survey_missing_mcid_rows": raw_checklist["survey_missing_mcid_rows"],
    }
    raw_checklist["diagnostics"] = diagnostics
    raw_checklist["mode"] = "private"
    raw_checklist["title"] = "Exclusion / inclusion based on /raw/"
    raw_checklist["description"] = "This is the only part of the report that uses private raw files. Non-consent logs are filtered out before this checklist because they are usually researcher/test closures rather than participant sessions."

    if diagnostics["hidden_non_consent_in_survey"]:
        print(
            "[sum_merged] WARNING: Non-consent log MCID(s) also found in survey data: "
            + ", ".join(diagnostics["hidden_non_consent_in_survey"]),
            flush=True,
        )
    print(
        "[sum_merged] MCID(s) found in logs more than once: "
        + (", ".join(diagnostics["duplicate_log_ids"]) if diagnostics["duplicate_log_ids"] else "none"),
        flush=True,
    )
    print(
        "[sum_merged] MCID(s) with more than two survey rows: "
        + (", ".join(diagnostics["survey_more_than_two_rows_ids"]) if diagnostics["survey_more_than_two_rows_ids"] else "none"),
        flush=True,
    )

    included_ids = raw_checklist["included_ids"]
    write_included_mcids(included_ids, paths["included_mcids_output_path"])
    log_step(f"Publishing stripped /data/ files for {len(included_ids)} included MCID(s).")
    publish_summary = publish_data_for_included_mcids(
        included_ids,
        raw_dir=paths["raw_dir"],
        data_dir=paths["data_dir"],
        resources_dir=paths["resources_dir"],
    )
    for line in publish_summary.lines():
        print(f"[sum_merged] {line}", flush=True)
    raw_checklist["publish_summary"] = publish_summary.lines()
    return raw_checklist


def build_payload(*, public_route: bool, paths: dict[str, Path]) -> dict[str, Any]:
    if public_route:
        log_step("PUBLIC_ROUTE=True: validating existing publishable /data/ files.")
        validate_public_data_files(paths)
        raw_block = public_route_raw_block()
    else:
        raw_block = private_route_prepare_data(paths)

    log_step("Loading /data/ survey export and /data/logs/.")
    survey_rows, headers = load_survey_export(paths["data_survey_path"])
    require_delayed_included_column(survey_rows, headers, paths["data_survey_path"])

    if public_route:
        log_step("PUBLIC_ROUTE=True: rebuilding retention_scores_merged.tsv from survey_export, GenAI scores, and human grader files.")
        scoring_rows, scoring_problems = write_prompt_score_file(survey_rows, require_complete_review=True)
        if scoring_problems:
            raise RuntimeError(_retention_scoring_problem_message(scoring_problems, paths, final_stats=True))
        log_step(f"Retention scores merged file ready: {len(scoring_rows):,} prompt-level row(s).")
    else:
        log_step("PUBLIC_ROUTE=False: preparing retention_answers.tsv, retention_scores_genai.tsv, and GenAI prompt support files.")
        retention_prepare = prepare_retention_answer_files(survey_rows)
        for key, value in retention_prepare.items():
            log_step(f"Retention prep {key}: {value:,}" if isinstance(value, int) else f"Retention prep {key}: {value}")

    log_index = load_log_index(paths["data_log_dir"])

    log_step("Reading persisted delayed-response inclusion from /data/survey_export.tsv.")
    delayed_block = build_delayed_response_checklist_from_annotations(survey_rows)
    delayed_diagnostics = delayed_block.get("diagnostics", {})
    log_step(
        "Delayed-response filter complete: "
        f"{len(delayed_diagnostics.get('included_ids') or [])} included delayed response(s); "
        f"{len(delayed_diagnostics.get('unverifiable_ids') or [])} unverifiable; "
        f"{len(delayed_diagnostics.get('early_ids') or [])} early; "
        f"{len(delayed_diagnostics.get('late_ids') or [])} late."
    )

    log_step("Building participant-level merged dataset.")
    merged = build_merged_dataset(
        survey_rows,
        log_index,
        collection_locations_path=paths.get("data_collection_locations_path", COLLECTION_LOCATIONS_PATH),
    )
    participants = merged["participants"]

    log_step("Building retention summaries.")
    retention_score_path = paths.get("data_retention_scores_path", RETENTION_SCORES_PATH)
    retention_scores, retention_warnings = load_retention_scores(retention_score_path)
    attach_retention_scores(participants, retention_scores)
    retention_question_rows = build_retention_question_rows(participants)
    retention = {
        "warnings": retention_warnings,
        "condition_summary": ret_condition_summary(participants, CONDITION_ORDER),
        "reliability": retention_reliability_summary(retention_score_path),
        "answer_rows": retention_question_rows,
        "questions": [
            {"key": key, "label": label}
            for key, label in RETENTION_QUESTION_SPECS
        ],
        "show_grades": bool(retention_scores),
    }

    log_step("Building demographic and collection-context summaries.")
    summary_rows = condition_summary(participants)
    distributions = demographic_distributions(participants)
    controlling_variables = controlling_variable_tables(participants)

    log_step("Building cognitive load, engagement, and perceived-control summaries.")
    scale_tables = {
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
    }

    log_step("Building game-log summaries.")
    game_logs = build_game_log_report(participants)

    log_step("Building interview overview.")
    interviews = add_interview_comparison_summaries(
        load_interview_overview(
            paths.get("data_transcripts_dir", INTERVIEW_TRANSCRIPTS_DIR),
            participants=participants,
            manifest_path=paths.get("data_interview_manifest_path", INTERVIEW_MANIFEST_PATH),
        ),
        participants,
    )

    log_step("Building inferential-statistics overview.")
    inferential_statistics = build_inferential_statistics(participants, retention_score_path)

    return {
        "meta": {
            "title": "Merged study summary",
            "route": "PUBLIC_ROUTE=True" if public_route else "PUBLIC_ROUTE=False",
            "route_description": route_description(public_route),
            "data_note": "This statistics app uses /data/: publishable, stripped files used for the report calculations.",
            "delayed_filter_note": "Delayed retention uses the persisted delayed_included=true/false column in /data/survey_export.tsv; delayed analyses and score_ret use only delayed_included=true delayed rows.",
            "data_dir_label": "/data/",
            "raw_dir_label": "/raw/",
        },
        "raw_block": raw_block,
        "delayed_block": delayed_block,
        "data_audit": merged["audit"],
        "condition_summary": summary_rows,
        "demographics": distributions,
        "controlling_variables": controlling_variables,
        "condition_order": CONDITION_ORDER,
        "condition_colours": CONDITION_COLOURS,
        "scale_tables": scale_tables,
        "game_logs": game_logs,
        "interviews": interviews,
        "inferential_statistics": inferential_statistics,
        "statistics": inferential_statistics,
        "retention": retention,
        "participants": participants,
        "tabs": {
            "main": "Main",
            "retention": "Retention",
            "cognitive-load": "Cognitive load",
            "engagement": "Engagement",
            "control": "Perceived control",
            "logs": "Game logs",
            "interviews": "Interviews",
            "statistics": "Inferential statistics",
        },
    }


def main(public_route: bool = PUBLIC_ROUTE, paths: dict[str, Path] | None = None) -> int:
    resolved_paths = route_paths(paths)
    log_step("Starting simplified merged summary.")
    payload = build_payload(public_route=public_route, paths=resolved_paths)
    log_step("Rendering HTML report.")
    html = render_html(payload, resolved_paths)
    output_path = resolved_paths["merged_output_path"]
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(html, encoding="utf-8")
    log_step(f"Wrote {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
