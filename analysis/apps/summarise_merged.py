from __future__ import annotations

import csv
import json
import re
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
    build_final_retention_descriptives,
    build_retention_question_rows,
    load_retention_scores,
    ret_condition_summary,
    retention_reliability_summary,
)
from helpers._retention_coding import (
    AMOUNT_GENAI,
    AMOUNT_HUMAN,
    CREATURE_INFO_PDF_PATH,
    GENAI_LOW_CONFIDENCE_THRESHOLD,
    GENAI_PROMPT_PATH,
    SCORING_RUBRICS_HTML_PATH,
    SCORING_RUBRICS_PDF_PATH,
    build_retention_scoring_checks,
    configured_grader_score_paths,
    load_grader_scores,
    prepare_retention_answer_files,
    read_review_manifest,
    write_prompt_score_file,
    write_retention_scores_final_if_complete,
)
from helpers._shared import (
    COLLECTION_LOCATIONS_PATH,
    DATA_CONFIG_DIR,
    DATA_DIR,
    INTERVIEW_MANIFEST_PATH,
    INTERVIEW_TRANSCRIPTS_DIR,
    RETENTION_SCORES_PATH,
    RETENTION_FINAL_SCORES_PATH,
    RETENTION_COMPONENT_SPECS,
    RETENTION_ELEMENT_SPECS,
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
    clean,
    parse_numeric,
)
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
        "data_retention_scores_final_path": RETENTION_FINAL_SCORES_PATH,
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
    """Return a concise terminal/UI message for common retention-scoring setup states."""
    if not problems:
        return ""

    score_problem = "score (0-2) must be an integer from 0 to 2"
    confidence_problem = "confidence (0-100%) must be a number from 0 to 100"
    unfinished_score_confidence_rows = sum(
        1
        for problem in problems
        if score_problem in problem or confidence_problem in problem
    )

    genai_path = _relative_data_label(paths["data_dir"] / "retention_scores_genai*.tsv", paths)
    grader_path = _relative_data_label(paths["data_dir"] / "retention_scores_grader*.tsv", paths)
    prompt_path = _relative_data_label(GENAI_PROMPT_PATH, paths)
    rubrics_path = _relative_data_label(SCORING_RUBRICS_HTML_PATH, paths)
    creature_path = _relative_data_label(CREATURE_INFO_PDF_PATH, paths)
    rubrics_pdf_path = _relative_data_label(SCORING_RUBRICS_PDF_PATH, paths)

    rebuild_hint = (
        "Fix: make sure the source files are complete, then rerun sum_merged with PUBLIC_ROUTE=True. "
        "If the GenAI files do not exist yet, first run sum_merged with PUBLIC_ROUTE=False. "
        f"Fill {genai_path} using {prompt_path}; attach {genai_path} and {rubrics_path} "
        f"when asking GenAI to score. Appendix PDFs are also generated at {creature_path} and {rubrics_pdf_path}. "
        "If human review is required, create/complete the configured grader files with "
        f"python main.py score_ret grader=1, which writes the frozen review manifest and {grader_path}."
    )

    if unfinished_score_confidence_rows >= 4:
        prefix = (
            "Cannot calculate final retention statistics yet: "
            if final_stats
            else "Retention scores merged file was not rebuilt: "
        )
        return (
            prefix
            + "GenAI scoring appears incomplete because multiple score/confidence cells are empty or invalid. "
            + rebuild_hint
        )

    preview = " | ".join(problems[:5])
    suffix = f" ({len(problems) - 5} more not shown)" if len(problems) > 5 else ""

    if final_stats:
        return (
            "Cannot calculate final retention statistics yet. "
            "Retention scoring source files need attention. "
            "Problems: "
            + preview
            + suffix
            + ". "
            + rebuild_hint
        )

    return (
        "Retention scores merged file was not rebuilt. This is expected while GenAI/human scoring is still incomplete. "
        "Problems: "
        + preview
        + suffix
        + ". "
        + rebuild_hint
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


RETENTION_SCORE_HISTOGRAM_CATEGORIES = [
    ("score_0", "0"),
    ("score_1", "1"),
    ("score_2", "2"),
    ("unknown", "Unknown"),
]

RETENTION_SCORE_HISTOGRAM_SEGMENTS = [
    ("confident", "Confident"),
    ("unsure", "Unsure"),
]


def _natural_source_key(label: str) -> list[Any]:
    return [int(part) if part.isdigit() else part for part in re.split(r"(\d+)", label)]


def _retention_score_source_labels(scoring_rows: list[dict[str, Any]]) -> list[str]:
    labels: set[str] = set()
    for row in scoring_rows:
        for key in row:
            match = re.match(r"^((?:genai|grader)\d+(?:_\d+)?)_score$", key)
            if match:
                labels.add(match.group(1))
    return sorted(labels, key=_natural_source_key)


def _score_category_and_segment_for_source(row: dict[str, Any], source_label: str) -> tuple[str, str]:
    score = parse_numeric(row.get(f"{source_label}_score"))
    if score is None or not float(score).is_integer() or int(score) not in (0, 1, 2):
        return "unknown", "unsure"

    note = clean(row.get(f"{source_label}_note"))
    confidence = parse_numeric(clean(row.get(f"{source_label}_confidence")).replace("%", ""))
    low_confidence = confidence is not None and confidence < GENAI_LOW_CONFIDENCE_THRESHOLD
    segment = "unsure" if note or low_confidence else "confident"
    return f"score_{int(score)}", segment


def _valid_source_score(row: dict[str, Any], source_label: str) -> float | None:
    score = parse_numeric(row.get(f"{source_label}_score"))
    if score is None or score < 0 or score > 2:
        return None
    return score


def build_retention_full_score_distributions(scoring_rows: list[dict[str, Any]]) -> dict[str, Any]:
    """Build manuscript-style full retention scores per participant, source file, and test occasion.

    Scores stay on the original 0-2 rubric scale. Within each participant/source/moment,
    rubric rows are first averaged into component scores (Q2 facts and Q4 location are
    averaged within component), then component scores are averaged across administered
    components. Missing or invalid scores are omitted rather than treated as 0.
    """
    source_labels = _retention_score_source_labels(scoring_rows)
    if not scoring_rows or not source_labels:
        return {"source_labels": [], "moments": [], "values": {}}

    known_elements = {key for key, _label in RETENTION_ELEMENT_SPECS}
    creature_element_scores: dict[tuple[str, str, str, str], dict[str, float]] = {}

    for row in scoring_rows:
        participant_id = clean(row.get("MCID"))
        moment = clean(row.get("moment"))
        creature_id = clean(row.get("creature_id"))
        q_element = clean(row.get("q_element"))
        if (
            not participant_id
            or not moment
            or not creature_id
            or q_element not in known_elements
            or not clean(row.get("answer_std"))
        ):
            continue

        for source_label in source_labels:
            score = _valid_source_score(row, source_label)
            if score is None:
                continue
            key = (source_label, moment, participant_id, creature_id)
            creature_element_scores.setdefault(key, {})[q_element] = score

    component_values_by_participant: dict[tuple[str, str, str], list[float]] = {}
    for (source_label, moment, participant_id, _creature_id), element_scores in creature_element_scores.items():
        for _component_key, _component_label, q_elements in RETENTION_COMPONENT_SPECS:
            component_scores = [element_scores[q_element] for q_element in q_elements if q_element in element_scores]
            if component_scores:
                component_values_by_participant.setdefault((source_label, moment, participant_id), []).append(
                    sum(component_scores) / len(component_scores)
                )

    values: dict[str, dict[str, list[float]]] = {source_label: {} for source_label in source_labels}
    for (source_label, moment, _participant_id), component_values in component_values_by_participant.items():
        if component_values:
            values[source_label].setdefault(moment, []).append(sum(component_values) / len(component_values))

    moment_order = ["Immediate", "Delayed"]
    extra_moments = sorted({moment for source_values in values.values() for moment in source_values} - set(moment_order))
    moments = [moment for moment in moment_order if any(moment in source_values for source_values in values.values())] + extra_moments

    for source_label in source_labels:
        for moment in moments:
            values[source_label].setdefault(moment, [])

    return {
        "source_labels": source_labels,
        "moments": moments,
        "values": values,
    }


def _configured_retention_source_labels(kind: str, amount: int) -> list[str]:
    return [f"{kind}{index}" for index in range(1, max(0, int(amount)) + 1)]


def _display_retention_source_label(label: str) -> str:
    text = clean(label)
    match = re.match(r"^(genai|grader)(\d+)(?:_(\d+))?$", text, flags=re.IGNORECASE)
    if not match:
        return text or "Unknown source"

    source_type, number, duplicate = match.groups()
    base = "GenAI" if source_type.lower() == "genai" else "Grader"
    suffix = f" / {duplicate}" if duplicate else ""
    return f"{base} #{number}{suffix}"


def _valid_retention_integer_score(value: object) -> int | None:
    score = parse_numeric(value)
    if score is None or not float(score).is_integer():
        return None
    score_int = int(score)
    return score_int if score_int in (0, 1, 2) else None


def _format_percent(numerator: int, denominator: int) -> str:
    if denominator <= 0:
        return "—"
    return f"{100 * numerator / denominator:.1f}%"


def build_retention_human_genai_comparison(scoring_rows: list[dict[str, Any]]) -> dict[str, Any]:
    """Compare each configured human grader's completed exact-answer tasks with configured GenAI sources.

    The denominator for human progress is the frozen human-review queue/grader TSV,
    not the participant-occurrence rows in retention_scores_merged.tsv. Comparisons
    are made by task_id, which is derived from q_element + creature + answer_std.
    """
    genai_labels = _configured_retention_source_labels("genai", AMOUNT_GENAI)
    grader_paths = configured_grader_score_paths(AMOUNT_HUMAN)
    grader_labels = _configured_retention_source_labels("grader", AMOUNT_HUMAN)

    scoring_by_task_id: dict[str, dict[str, Any]] = {}
    for row in scoring_rows:
        task_id = clean(row.get("task_id"))
        if task_id and clean(row.get("answer_std")) and task_id not in scoring_by_task_id:
            scoring_by_task_id[task_id] = row

    manifest_task_ids = [
        task_id
        for task_id in (clean(row.get("task_id")) for row in read_review_manifest())
        if task_id
    ]

    summary_rows: list[dict[str, Any]] = []
    matrix_sections: list[dict[str, Any]] = []

    for grader_label, grader_path in zip(grader_labels, grader_paths):
        grader_display = _display_retention_source_label(grader_label)
        grader_source = load_grader_scores(grader_path) if grader_path.exists() else {}
        task_ids = list(dict.fromkeys(manifest_task_ids or sorted(grader_source)))
        total_tasks = len(task_ids)

        status_counts = {"graded": 0, "todo": 0, "skipped": 0, "flagged": 0, "invalid": 0}
        graded_tasks: list[tuple[str, int]] = []

        for task_id in task_ids:
            source_row = grader_source.get(task_id, {})
            status = clean(source_row.get("status"))
            score = _valid_retention_integer_score(source_row.get("score (0-2)"))

            if status == "graded" and score is not None:
                status_counts["graded"] += 1
                graded_tasks.append((task_id, score))
            elif status == "graded":
                status_counts["invalid"] += 1
            elif status in {"skipped", "flagged"}:
                status_counts[status] += 1
            else:
                status_counts["todo"] += 1

        graded_count = len(graded_tasks)

        summary_rows.append({
            "grader_label": grader_label,
            "grader": grader_display,
            "comparison": "Progress",
            "value": f"{grader_display} scored {graded_count} out of {total_tasks}",
            "detail": (
                f"graded={status_counts['graded']}; todo={status_counts['todo']}; "
                f"skipped={status_counts['skipped']}; flagged={status_counts['flagged']}; "
                f"invalid graded scores={status_counts['invalid']}"
            ),
        })

        for genai_label in genai_labels:
            genai_display = _display_retention_source_label(genai_label)

            matrix: dict[int, dict[int | str, int]] = {
                grader_score: {0: 0, 1: 0, 2: 0, "missing": 0}
                for grader_score in (0, 1, 2)
            }

            identical = 0
            comparable = 0
            grader_higher = 0
            genai_higher = 0
            missing_genai = 0

            for task_id, grader_score in graded_tasks:
                scoring_row = scoring_by_task_id.get(task_id, {})

                genai_score = _valid_retention_integer_score(scoring_row.get(f"{genai_label}_score"))
                if genai_score is None and genai_label == "genai1":
                    genai_score = _valid_retention_integer_score(scoring_row.get("genai_score"))

                if genai_score is None:
                    matrix[grader_score]["missing"] += 1
                    missing_genai += 1
                    continue

                matrix[grader_score][genai_score] += 1
                comparable += 1

                if grader_score == genai_score:
                    identical += 1
                elif grader_score > genai_score:
                    grader_higher += 1
                else:
                    genai_higher += 1

            summary_rows.append({
                "grader_label": grader_label,
                "grader": grader_display,
                "comparison": f"Exact match with {genai_display}",
                "value": f"From {graded_count}, {_format_percent(identical, graded_count)} is identical to the scoring of {genai_display}",
                "detail": (
                    f"{identical}/{graded_count} identical; compared={comparable}; "
                    f"{grader_display} higher={grader_higher}; {genai_display} higher={genai_higher}; "
                    f"missing/invalid {genai_display} score={missing_genai}"
                ),
            })

            if graded_count:
                column_totals = {
                    score: sum(matrix[grader_score][score] for grader_score in (0, 1, 2))
                    for score in (0, 1, 2)
                }
                missing_total = sum(matrix[grader_score]["missing"] for grader_score in (0, 1, 2))

                matrix_rows = []
                for grader_score in (0, 1, 2):
                    row_total = (
                        sum(matrix[grader_score][source_score] for source_score in (0, 1, 2))
                        + matrix[grader_score]["missing"]
                    )
                    matrix_rows.append({
                        "grader_score": f"{grader_display} score {grader_score}",
                        "source_score_0": matrix[grader_score][0],
                        "source_score_1": matrix[grader_score][1],
                        "source_score_2": matrix[grader_score][2],
                        "source_missing": matrix[grader_score]["missing"],
                        "total": row_total,
                    })

                matrix_rows.append({
                    "grader_score": f"Column total ({genai_display})",
                    "source_score_0": column_totals[0],
                    "source_score_1": column_totals[1],
                    "source_score_2": column_totals[2],
                    "source_missing": missing_total,
                    "total": graded_count,
                })

                matrix_sections.append({
                    "grader_label": grader_label,
                    "grader": grader_display,
                    "source_label": genai_label,
                    "source": genai_display,
                    "title": f"{grader_display} compared with {genai_display}",
                    "summary": (
                        f"Identical: {identical}/{graded_count} ({_format_percent(identical, graded_count)}); "
                        f"{grader_display} higher: {grader_higher}; {genai_display} higher: {genai_higher}; "
                        f"missing/invalid {genai_display}: {missing_genai}."
                    ),
                    "rows": matrix_rows,
                })

    return {
        "configured_genai_count": AMOUNT_GENAI,
        "configured_human_count": AMOUNT_HUMAN,
        "summary_rows": summary_rows,
        "matrix_sections": matrix_sections,
    }


def build_retention_score_histograms(scoring_rows: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Build source-file score/confidence histograms for the Retention tab."""
    source_labels = _retention_score_source_labels(scoring_rows)
    if not scoring_rows or not source_labels:
        return []

    component_elements = {key: elements for key, _label, elements in RETENTION_COMPONENT_SPECS}
    chart_specs: list[dict[str, Any]] = [
        {"key": "Q1_name", "label": "Q1 name", "q_elements": ["Q1_name"]},
        {"key": "Q2_merged", "label": "Q2 merged", "q_elements": component_elements.get("Q2_facts", ["Q2_fact1", "Q2_fact2", "Q2_fact3"])},
        {"key": "Q2_fact1", "label": "Q2 fact #1", "q_elements": ["Q2_fact1"]},
        {"key": "Q2_fact2", "label": "Q2 fact #2", "q_elements": ["Q2_fact2"]},
        {"key": "Q2_fact3", "label": "Q2 fact #3", "q_elements": ["Q2_fact3"]},
        {"key": "Q3_looks", "label": "Q3 looks", "q_elements": ["Q3_looks"]},
        {"key": "Q4_merged", "label": "Q4 merged", "q_elements": component_elements.get("Q4_location", ["Q4_chapter", "Q4_env"])},
        {"key": "Q4_chapter", "label": "Q4 chapter", "q_elements": ["Q4_chapter"]},
        {"key": "Q4_env", "label": "Q4 environment", "q_elements": ["Q4_env"]},
    ]

    known_elements = {key for key, _label in RETENTION_ELEMENT_SPECS}
    charts: list[dict[str, Any]] = []
    for spec in chart_specs:
        q_elements = [element for element in spec["q_elements"] if element in known_elements]
        scoped_rows = [
            row for row in scoring_rows
            if clean(row.get("answer_std")) and clean(row.get("q_element")) in q_elements
        ]
        counts_by_source: dict[str, dict[str, dict[str, int]]] = {
            label: {
                category_key: {segment_key: 0 for segment_key, _segment_label in RETENTION_SCORE_HISTOGRAM_SEGMENTS}
                for category_key, _category_label in RETENTION_SCORE_HISTOGRAM_CATEGORIES
            }
            for label in source_labels
        }
        for row in scoped_rows:
            for source_label in source_labels:
                category_key, segment_key = _score_category_and_segment_for_source(row, source_label)
                counts_by_source[source_label][category_key][segment_key] += 1
        charts.append({
            "key": spec["key"],
            "label": spec["label"],
            "q_elements": q_elements,
            "n_answer_rows": len(scoped_rows),
            "source_labels": source_labels,
            "categories": [
                {"key": key, "label": label}
                for key, label in RETENTION_SCORE_HISTOGRAM_CATEGORIES
            ],
            "counts": counts_by_source,
            "segments": [
                {"key": key, "label": label}
                for key, label in RETENTION_SCORE_HISTOGRAM_SEGMENTS
            ],
        })

    return charts


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

    scoring_problems: list[str] = []
    scoring_rows: list[dict[str, Any]] = []
    if public_route:
        retention_score_path = paths.get("data_retention_scores_path", RETENTION_SCORES_PATH)
        if retention_score_path.exists():
            log_step(
                "PUBLIC_ROUTE=True: using existing retention_scores_merged.tsv as a non-destructive "
                "manual adjudication workspace. It will not be rewritten."
            )
        else:
            log_step(
                "PUBLIC_ROUTE=True: creating initial retention_scores_merged.tsv only if all configured "
                "GenAI and human-review TSVs are complete."
            )

        scoring_rows, scoring_problems = write_prompt_score_file(survey_rows, require_complete_review=True)

        if scoring_problems:
            log_step(_retention_scoring_problem_message(scoring_problems, paths, final_stats=True))

        if retention_score_path.exists():
            log_step(f"Retention scores merged file available: {len(scoring_rows):,} q_element row(s).")
        else:
            log_step(
                f"Retention scores merged file not written yet. Preview rows available in memory: "
                f"{len(scoring_rows):,} q_element row(s)."
            )

        if not scoring_problems:
            final_path = paths.get("data_retention_scores_final_path", RETENTION_FINAL_SCORES_PATH)
            final_written, final_count, final_messages = write_retention_scores_final_if_complete(scoring_rows, final_path)
            if final_written:
                log_step(f"Wrote retention_scores_final.tsv with {final_count:,} participant row(s).")
            else:
                for message in final_messages:
                    log_step(f"Retention final scores not written: {message}")
            if final_written:
                for message in final_messages:
                    log_step(f"Retention final scores warning: {message}")
    else:
        log_step("PUBLIC_ROUTE=False: preparing retention_answers.tsv, configured GenAI score file(s), and GenAI prompt support files.")
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
    unresolved_retention_conflicts = sum(
        1
        for row in retention_question_rows
        if clean(row.get("answer_std"))
        and not (
            parse_numeric(row.get("final_score")) is not None
            and float(parse_numeric(row.get("final_score"))).is_integer()
            and 0 <= int(parse_numeric(row.get("final_score"))) <= 2
        )
    )
    scoring_warning_summary = (
        [_retention_scoring_problem_message(scoring_problems, paths, final_stats=True)]
        if scoring_problems
        else []
    )
    retention_checks = build_retention_scoring_checks(
        survey_rows,
        scoring_rows if public_route else [],
        scoring_problems + retention_warnings,
    )
    retention = {
        "warnings": retention_warnings + scoring_warning_summary + scoring_problems,
        "condition_summary": ret_condition_summary(participants, CONDITION_ORDER),
        "final_descriptives": build_final_retention_descriptives(
            participants,
            retention_score_path,
            CONDITION_ORDER,
        ),
        "reliability": retention_reliability_summary(retention_score_path),
        "answer_rows": retention_question_rows,
        "questions": [
            {"key": key, "label": label}
            for key, label in RETENTION_ELEMENT_SPECS
        ],
        "show_grades": bool(retention_scores),
        "unresolved_conflict_count": unresolved_retention_conflicts,
        "checks": retention_checks,
        "full_score_distributions": build_retention_full_score_distributions(scoring_rows),
        "score_histograms": build_retention_score_histograms(scoring_rows),
        "human_genai_comparison": build_retention_human_genai_comparison(scoring_rows),
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
