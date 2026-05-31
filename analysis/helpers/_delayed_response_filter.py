
from __future__ import annotations

import datetime as dt
from collections import defaultdict
from typing import Any

from ._shared import (
    clean,
    delayed_flag,
    delayed_included_flag,
    first_present,
    mcid_from_row,
    parse_datetime,
    progress_is_complete,
    survey_end,
    survey_progress,
)

DELAYED_INCLUDED_COLUMN = "delayed_included"
DELAYED_INVITATION_DELAY_DAYS = 7
DELAYED_INVITATION_HOUR = 11
DELAYED_RESPONSE_WINDOW_HOURS = 24

DELAYED_RESPONSE_REASONS = [
    "Participant's DELAYED response was missing",
    "Participant's DELAYED response could not be verified because required timing or completion data were missing or invalid",
    "Participant's DELAYED response was completed before the scheduled delayed-questionnaire invitation",
    "Participant's DELAYED response was completed more than 24 hours after the scheduled delayed-questionnaire invitation",
    "Participant's DELAYED response does not meet any of the prior exclusion criteria, and is therefore included in the final analysis",
]

DELAYED_RESPONSE_DESCRIPTION = (
    "DELAYED rows are checked using EndDate and Progress in /data/survey_export.tsv. The scheduled delayed-questionnaire "
    "invitation is treated as 11:00 AM, seven calendar days after the participant's immediate EndDate. Delayed "
    "responses are included only when their timing and completion can be verified and they were completed within "
    "the following 24-hour window. Missing, unverifiable, early, or late delayed rows remain in the data, but are "
    "marked delayed_included=false and ignored for delayed analyses."
)


def _format_offset(delta: dt.timedelta) -> str:
    total_minutes = int(round(delta.total_seconds() / 60))
    sign = "+" if total_minutes >= 0 else "-"
    total_minutes = abs(total_minutes)
    hours, minutes = divmod(total_minutes, 60)
    return f"{sign}{hours}h {minutes:02d}m"


def _item(participant_id: str, offset: str) -> dict[str, str]:
    return {"mcid": participant_id, "offset": offset}


def delayed_included_column_present(header: list[str]) -> bool:
    return any(clean(column).lower() == DELAYED_INCLUDED_COLUMN.lower() for column in header)


def delayed_included_value(row: dict[str, str]) -> str:
    return first_present(row, [DELAYED_INCLUDED_COLUMN, "DELAYED_INCLUDED", "DelayedIncluded"])


def delayed_included_values_missing(survey_rows: list[dict[str, str]]) -> list[str]:
    missing: list[str] = []
    for row in survey_rows:
        if delayed_flag(row) and not delayed_included_value(row):
            missing.append(mcid_from_row(row) or "unknown MCID")
    return sorted(set(missing))


def _add_row(
    rows: list[dict[str, Any]],
    reason: str,
    ids: list[str],
    *,
    hide_mcids: bool = False,
    mcid_items: list[dict[str, str]] | None = None,
) -> None:
    sorted_items = sorted(mcid_items or [], key=lambda item: item.get("mcid", ""))
    rows.append({
        "reason": reason,
        "n": len(ids),
        "mcids": "—" if hide_mcids else ", ".join(sorted(ids)) if ids else "—",
        "mcid_items": [] if hide_mcids else sorted_items,
    })


def _scheduled_delayed_invitation(immediate_row: dict[str, str]) -> dt.datetime | None:
    immediate_end = parse_datetime(survey_end(immediate_row))
    if immediate_end is None:
        return None
    invitation_date = immediate_end.date() + dt.timedelta(days=DELAYED_INVITATION_DELAY_DAYS)
    return dt.datetime.combine(invitation_date, dt.time(hour=DELAYED_INVITATION_HOUR))


def _delayed_unverifiable_details(immediate_row: dict[str, str], delayed_row: dict[str, str]) -> list[str]:
    details: list[str] = []
    if _scheduled_delayed_invitation(immediate_row) is None:
        details.append("immediate EndDate missing/invalid")
    if parse_datetime(survey_end(delayed_row)) is None:
        details.append("delayed EndDate missing/invalid")
    if not progress_is_complete(survey_progress(delayed_row)):
        details.append("delayed Progress missing/incomplete")
    return details


def _delayed_timing_classification(immediate_row: dict[str, str], delayed_row: dict[str, str]) -> tuple[str, str]:
    unverifiable_details = _delayed_unverifiable_details(immediate_row, delayed_row)
    if unverifiable_details:
        return "unverifiable", "; ".join(unverifiable_details)

    invitation = _scheduled_delayed_invitation(immediate_row)
    delayed_end = parse_datetime(survey_end(delayed_row))
    if invitation is None or delayed_end is None:
        return "unverifiable", "timing data missing/invalid"

    deadline = invitation + dt.timedelta(hours=DELAYED_RESPONSE_WINDOW_HOURS)
    if delayed_end < invitation:
        return "early", _format_offset(delayed_end - invitation)
    if delayed_end > deadline:
        return "late", _format_offset(delayed_end - deadline)
    return "included", _format_offset(delayed_end - invitation)


def _row_groups(survey_rows: list[dict[str, str]]) -> dict[str, dict[str, list[tuple[int, dict[str, str]]]]]:
    grouped: dict[str, defaultdict[str, list[tuple[int, dict[str, str]]]]] = {
        "immediate": defaultdict(list),
        "delayed": defaultdict(list),
    }

    for row_index, row in enumerate(survey_rows):
        participant_id = mcid_from_row(row)
        if not participant_id:
            continue
        wave = "delayed" if delayed_flag(row) else "immediate"
        grouped[wave][participant_id].append((row_index, row))

    return {
        "immediate": dict(grouped["immediate"]),
        "delayed": dict(grouped["delayed"]),
    }


def build_delayed_response_checklist(survey_rows: list[dict[str, str]]) -> dict[str, Any]:
    """Build the delayed-response inclusion/exclusion checklist from /data/survey_export.tsv.

    This annotates only DELAYED rows. It does not exclude participants from the immediate-session analyses.
    """
    grouped = _row_groups(survey_rows)
    immediate = grouped["immediate"]
    delayed = grouped["delayed"]

    missing_ids: list[str] = []
    unverifiable_ids: list[str] = []
    early_ids: list[str] = []
    late_ids: list[str] = []
    included_ids: list[str] = []
    unverifiable_items: list[dict[str, str]] = []
    early_items: list[dict[str, str]] = []
    late_items: list[dict[str, str]] = []
    included_delayed_row_indices: list[int] = []

    for participant_id in sorted(immediate):
        immediate_row = immediate[participant_id][0][1]
        delayed_rows = delayed.get(participant_id, [])
        delayed_item = delayed_rows[0] if delayed_rows else None

        if delayed_item is None:
            missing_ids.append(participant_id)
            continue

        delayed_row_index, delayed_row = delayed_item
        classification, detail = _delayed_timing_classification(immediate_row, delayed_row)

        if classification == "unverifiable":
            unverifiable_ids.append(participant_id)
            unverifiable_items.append(_item(participant_id, detail))
        elif classification == "early":
            early_ids.append(participant_id)
            early_items.append(_item(participant_id, detail))
        elif classification == "late":
            late_ids.append(participant_id)
            late_items.append(_item(participant_id, detail))
        else:
            included_ids.append(participant_id)
            included_delayed_row_indices.append(delayed_row_index)

    rows: list[dict[str, Any]] = []
    _add_row(rows, DELAYED_RESPONSE_REASONS[0], missing_ids, hide_mcids=True)
    _add_row(rows, DELAYED_RESPONSE_REASONS[1], unverifiable_ids, mcid_items=unverifiable_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[2], early_ids, mcid_items=early_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[3], late_ids, mcid_items=late_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[4], included_ids, hide_mcids=True)

    return {
        "mode": "data",
        "title": "Exclusion / inclusion based on delayed response",
        "description": DELAYED_RESPONSE_DESCRIPTION,
        "rows": rows,
        "included_ids": sorted(included_ids),
        "included_delayed_row_indices": sorted(included_delayed_row_indices),
        "delayed_included_column": DELAYED_INCLUDED_COLUMN,
        "diagnostics": {
            "missing_ids": sorted(missing_ids),
            "unverifiable_ids": sorted(unverifiable_ids),
            "early_ids": sorted(early_ids),
            "late_ids": sorted(late_ids),
            "included_ids": sorted(included_ids),
        },
    }


def build_delayed_response_checklist_from_annotations(survey_rows: list[dict[str, str]]) -> dict[str, Any]:
    """Build the delayed-response checklist from persisted delayed_included flags in /data/.

    Inclusion follows the stored delayed_included value, while the displayed exclusion reason is reconstructed
    from the same timing/completion rules used when /data/survey_export.tsv was written.
    """
    grouped = _row_groups(survey_rows)
    immediate = grouped["immediate"]
    delayed = grouped["delayed"]

    missing_ids: list[str] = []
    unverifiable_ids: list[str] = []
    early_ids: list[str] = []
    late_ids: list[str] = []
    included_ids: list[str] = []
    unverifiable_items: list[dict[str, str]] = []
    early_items: list[dict[str, str]] = []
    late_items: list[dict[str, str]] = []
    included_delayed_row_indices: list[int] = []
    integrity_errors: list[str] = []

    for participant_id in sorted(immediate):
        immediate_row = immediate[participant_id][0][1]
        delayed_rows = delayed.get(participant_id, [])
        delayed_item = delayed_rows[0] if delayed_rows else None

        if delayed_item is None:
            missing_ids.append(participant_id)
            continue

        delayed_row_index, delayed_row = delayed_item
        stored_included = delayed_included_flag(delayed_row)
        classification, detail = _delayed_timing_classification(immediate_row, delayed_row)

        if stored_included:
            if classification != "included":
                integrity_errors.append(
                    f"{participant_id}: delayed_included=true but timing/completion classification is "
                    f"{classification!r} ({detail or 'no detail'})"
                )
            included_ids.append(participant_id)
            included_delayed_row_indices.append(delayed_row_index)
            continue

        if classification == "unverifiable":
            unverifiable_ids.append(participant_id)
            unverifiable_items.append(_item(participant_id, detail))
        elif classification == "early":
            early_ids.append(participant_id)
            early_items.append(_item(participant_id, detail))
        elif classification == "late":
            late_ids.append(participant_id)
            late_items.append(_item(participant_id, detail))
        else:
            integrity_errors.append(
                f"{participant_id}: delayed_included=false but timing/completion classification is "
                f"{classification!r} ({detail or 'no detail'})"
            )

    if integrity_errors:
        preview = "; ".join(integrity_errors[:20])
        suffix = "..." if len(integrity_errors) > 20 else ""
        raise RuntimeError(
            "Invalid delayed_included values in /data/survey_export.tsv. "
            "The persisted delayed_included value does not match the delayed-response timing/completion rules. "
            f"Problem row(s): {preview}{suffix}. "
            "Regenerate /data/ with PUBLIC_ROUTE=False, or inspect the immediate/delayed EndDate and Progress values."
        )

    rows: list[dict[str, Any]] = []
    _add_row(rows, DELAYED_RESPONSE_REASONS[0], missing_ids, hide_mcids=True)
    _add_row(rows, DELAYED_RESPONSE_REASONS[1], unverifiable_ids, mcid_items=unverifiable_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[2], early_ids, mcid_items=early_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[3], late_ids, mcid_items=late_items)
    _add_row(rows, DELAYED_RESPONSE_REASONS[4], included_ids, hide_mcids=True)

    return {
        "mode": "data",
        "title": "Exclusion / inclusion based on delayed response",
        "description": DELAYED_RESPONSE_DESCRIPTION,
        "rows": rows,
        "included_ids": sorted(included_ids),
        "included_delayed_row_indices": sorted(included_delayed_row_indices),
        "delayed_included_column": DELAYED_INCLUDED_COLUMN,
        "diagnostics": {
            "missing_ids": sorted(missing_ids),
            "unverifiable_ids": sorted(unverifiable_ids),
            "early_ids": sorted(early_ids),
            "late_ids": sorted(late_ids),
            "included_ids": sorted(included_ids),
        },
    }


def annotate_survey_rows_with_delayed_inclusion(
    survey_rows: list[dict[str, str]],
    delayed_checklist: dict[str, Any] | None = None,
) -> list[dict[str, str]]:
    """Return survey rows with a delayed_included column added.

    The original DELAYED rows are retained. Downstream delayed-wave logic should use only DELAYED rows whose
    delayed_included value is true; immediate rows are marked true so immediate-session analyses are unaffected.
    """
    checklist = delayed_checklist or build_delayed_response_checklist(survey_rows)
    included_delayed_row_indices = set(checklist.get("included_delayed_row_indices") or [])

    annotated_rows: list[dict[str, str]] = []
    for row_index, row in enumerate(survey_rows):
        annotated_row = dict(row)
        annotated_row[DELAYED_INCLUDED_COLUMN] = "false" if delayed_flag(row) and row_index not in included_delayed_row_indices else "true"
        annotated_rows.append(annotated_row)
    return annotated_rows


# Backwards-compatible alias for older call sites. The function no longer removes rows.
def filter_survey_rows_for_valid_delayed_responses(
    survey_rows: list[dict[str, str]],
    delayed_checklist: dict[str, Any] | None = None,
) -> list[dict[str, str]]:
    return annotate_survey_rows_with_delayed_inclusion(survey_rows, delayed_checklist)