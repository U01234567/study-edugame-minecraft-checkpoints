from __future__ import annotations

import copy
import math
import random
from collections import Counter
from pathlib import Path
from typing import Any

import numpy as np

from ._cl_main import valid_score as cl_valid_score
from ._ctrl_main import valid_score as _ctrl_valid_score
from ._eng_main import valid_score as eng_valid_score
from ._ret_main import attach_retention_scores, load_retention_scores
from ._shared import CONDITION_ORDER, RETENTION_SCORES_PATH, mean_sd_text, parse_numeric, summarise

try:  # SciPy gives exact t-distribution p-values. The fallback keeps the app usable.
    from scipy import stats as scipy_stats
except Exception:  # pragma: no cover - only used when SciPy is absent locally.
    scipy_stats = None

BOOTSTRAP_SAMPLES = 5000
BOOTSTRAP_SEED = 20260508
ALPHA = 0.05

CONTRASTS = {
    "Required continue": {"required_pause_contrast": -1.0, "optional_pause_contrast": -0.5},
    "Required pauses": {"required_pause_contrast": 1.0, "optional_pause_contrast": -0.5},
    "Optional pauses": {"required_pause_contrast": 0.0, "optional_pause_contrast": 1.0},
}

CONTRAST_MULTIPLIERS = {
    "required_pause_contrast": 2.0,   # required pauses - required continue
    "optional_pause_contrast": 1.5,   # optional pauses - average(required continue, required pauses)
}

DISPLAY_NAMES = {
    "required_pause_contrast": "Required-pause contrast",
    "optional_pause_contrast": "Optional-pauses contrast",
    "retention_form_order": "Immediate retention-form order",
    "retention_immediate_form_order": "Immediate retention-form order",
    "retention_delayed_form_order": "Delayed retention-form order",
    "eng_main": "Engagement",
    "cl_intrinsic": "Intrinsic cognitive load",
    "cl_extraneous": "Extraneous cognitive load",
    "cl_germane": "Germane cognitive load",
    "ret_immediate_score": "Immediate retention",
    "ret_delayed_score": "Delayed retention",
    "ctrl_perceived": "Perceived control",
    "age": "Age",
    "gender": "Gender",
    "room_type": "Room type",
    "same_room_n": "Shared-slot n",
}


def _num(value: object) -> float | None:
    parsed = parse_numeric(value)
    if parsed is None or not math.isfinite(parsed):
        return None
    return float(parsed)


def _fmt(value: object, digits: int = 3) -> str:
    number = _num(value)
    if number is None:
        return ""
    if abs(number) < 0.001 and number != 0:
        return f"{number:.2e}"
    return f"{number:.{digits}f}"


def _fmt_p(value: float | None) -> str:
    if value is None:
        return ""
    if value < 0.001:
        return "< .001"
    return f"{value:.3f}".replace("0.", ".")


def _safe_mean(values: list[float | None]) -> float | None:
    valid = [value for value in values if value is not None]
    if not valid:
        return None
    return float(sum(valid) / len(valid))


def _p_two_tailed(t_value: float, df: int) -> float:
    if scipy_stats is not None and df > 0:
        return float(2 * scipy_stats.t.sf(abs(t_value), df))
    return float(math.erfc(abs(t_value) / math.sqrt(2)))


def _t_critical(df: int) -> float:
    if scipy_stats is not None and df > 0:
        return float(scipy_stats.t.ppf(0.975, df))
    return 1.96


def _holm_adjust(rows: list[dict[str, Any]], family: str, *, p_key: str = "p") -> None:
    indexed = [
        (index, row[p_key])
        for index, row in enumerate(rows)
        if row.get("p_family") == family and isinstance(row.get(p_key), (int, float))
    ]
    indexed.sort(key=lambda item: item[1])
    m = len(indexed)
    running = 0.0
    for rank, (index, p_value) in enumerate(indexed, start=1):
        adjusted = min(1.0, p_value * (m - rank + 1))
        running = max(running, adjusted)
        rows[index]["p_holm"] = running
        rows[index]["p_holm_display"] = _fmt_p(running)


def _condition_counts(rows: list[dict[str, Any]]) -> dict[str, int]:
    counts = Counter(row.get("condition") for row in rows)
    return {condition: counts.get(condition, 0) for condition in CONDITION_ORDER}


def _attach_scores(participants: list[dict[str, Any]], retention_scores_path: Path = RETENTION_SCORES_PATH) -> tuple[list[dict[str, Any]], list[str]]:
    enriched = [copy.deepcopy(participant) for participant in participants]
    scores, warnings = load_retention_scores(retention_scores_path)
    if scores:
        attach_retention_scores(enriched, scores)
    return enriched, warnings


def _analysis_rows(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    for participant in participants:
        condition = participant.get("condition")
        if condition not in CONTRASTS:
            continue
        row = {
            "participant_id": participant.get("participant_id", ""),
            "condition": condition,
            **CONTRASTS[condition],
            "retention_form_order": participant.get("retention_immediate_form_order") or participant.get("retention_form_order") or "",
            "retention_immediate_form_order": participant.get("retention_immediate_form_order") or participant.get("retention_form_order") or "",
            "retention_delayed_form_order": participant.get("retention_delayed_form_order") or "",
            "retention_counterbalance_status": participant.get("retention_counterbalance_status") or "",
            "retention_counterbalance_warning": participant.get("retention_counterbalance_warning") or "",
            "collection_context_warning": participant.get("collection_context_warning") or "",
            "remote": bool(participant.get("remote")),
            "collection_date": participant.get("collection_date") or "",
            "collection_slot_label": participant.get("collection_slot_label") or "",
            "room_type": participant.get("room_type") or "",
            "same_room_n": _num(participant.get("same_room_n")),
            "age": _num(participant.get("age")),
            "gender": participant.get("gender") or "",
            "ret_immediate_score": _num(participant.get("ret_immediate_score")),
            "ret_delayed_score": _num(participant.get("ret_delayed_score")),
            "ret_immediate_scored_prompt_count": _num(participant.get("ret_immediate_scored_prompt_count")),
            "ret_delayed_scored_prompt_count": _num(participant.get("ret_delayed_scored_prompt_count")),
            "cl_intrinsic": _num(participant.get("cl_intrinsic")),
            "cl_extraneous": _num(participant.get("cl_extraneous")),
            "cl_germane": _num(participant.get("cl_germane")),
            "eng_main": _num(participant.get("eng_main")),
            "ctrl_perceived": _num(participant.get("ctrl_perceived")),
        }
        rows.append(row)
    return rows


def _levels(rows: list[dict[str, Any]], key: str) -> list[str]:
    return sorted({str(row.get(key) or "").strip() for row in rows if str(row.get(key) or "").strip()})


def _numeric_has_variation(rows: list[dict[str, Any]], key: str) -> bool:
    values = {_fmt(value, 8) for row in rows for value in [row.get(key)] if _num(value) is not None}
    return len(values) > 1


def _retention_order_key(outcome: str) -> str:
    if outcome == "ret_delayed_score":
        return "retention_delayed_form_order"
    return "retention_immediate_form_order"


def _retention_covariates(rows: list[dict[str, Any]], outcome: str) -> list[str]:
    key = _retention_order_key(outcome)
    return [key] if len(_levels(rows, key)) > 1 else []


def _location_summary(rows: list[dict[str, Any]]) -> list[dict[str, Any]]:
    locations = sorted({str(row.get("room_type") or "Missing / not set").strip() for row in rows})
    preferred = ["Creative Space", "Living Room", "At home", "Missing / not set"]
    ordered = [location for location in preferred if location in locations]
    ordered.extend([location for location in locations if location not in ordered])

    summary_rows: list[dict[str, Any]] = []
    for location in ordered:
        scoped = [row for row in rows if (str(row.get("room_type") or "Missing / not set").strip()) == location]
        is_lab_location = location in {"Creative Space", "Living Room"}

        # same_room_n stores the number of OTHER participants in the same lab slot.
        # For the descriptive table, slot n is clearer because it includes the participant themself.
        slot_counts = []
        if is_lab_location:
            for row in scoped:
                shared_n = _num(row.get("same_room_n"))
                if shared_n is not None:
                    slot_counts.append(shared_n + 1)

        slot_summary = summarise(slot_counts)
        summary_rows.append({
            "location": location,
            "n": len(scoped),
            "slot_n_mean": _fmt(slot_summary.get("mean"), 2) if is_lab_location else "NA",
            "slot_n_min": _fmt(slot_summary.get("min"), 0) if is_lab_location else "NA",
            "slot_n_max": _fmt(slot_summary.get("max"), 0) if is_lab_location else "NA",
        })
    return summary_rows


def _lab_slot_summary(rows: list[dict[str, Any]]) -> list[dict[str, Any]]:
    grouped: dict[tuple[str, str, str], int] = {}
    slot_orders: dict[tuple[str, str, str], int] = {}

    for row in rows:
        location = str(row.get("room_type") or "").strip()
        if location not in {"Creative Space", "Living Room"}:
            continue

        date = str(row.get("collection_date") or "").strip()
        slot_label = str(row.get("collection_slot_label") or "").strip()
        if not date or not slot_label:
            continue

        key = (date, slot_label, location)
        grouped[key] = grouped.get(key, 0) + 1

        slot_order = _num(row.get("collection_slot_order"))
        if key not in slot_orders:
            slot_orders[key] = int(slot_order) if slot_order is not None else 999

    return [
        {
            "date": date,
            "time": slot_label.replace("–", " - "),
            "lab": location,
            "n_participants": n,
        }
        for (date, slot_label, location), n in sorted(
            grouped.items(),
            key=lambda item: (
                item[0][0],
                slot_orders.get(item[0], 999),
                item[0][2],
                item[0][1],
            ),
        )
    ]


def _reference_level(key: str, levels: list[str]) -> str:
    if key == "gender" and "Male" in levels:
        return "Male"
    if key in {"retention_form_order", "retention_immediate_form_order", "retention_delayed_form_order"} and "image_first" in levels:
        return "image_first"
    return levels[0] if levels else ""


def _fit_lm(
    rows: list[dict[str, Any]],
    *,
    label: str,
    outcome: str,
    numeric_predictors: list[str],
    categorical_predictors: list[str] | None = None,
) -> dict[str, Any]:
    categorical_predictors = categorical_predictors or []
    needed_numeric = [outcome, *numeric_predictors]
    scoped = []
    for row in rows:
        if any(_num(row.get(key)) is None for key in needed_numeric):
            continue
        if any(not str(row.get(key) or "").strip() for key in categorical_predictors):
            continue
        scoped.append(row)

    if not scoped:
        return {"label": label, "outcome": DISPLAY_NAMES.get(outcome, outcome), "n": 0, "status": "No complete cases for this model.", "rows": []}

    cat_specs: list[tuple[str, str, list[str]]] = []
    omitted_covariates: list[str] = []
    for key in categorical_predictors:
        levels = _levels(scoped, key)
        if len(levels) < 2:
            omitted_covariates.append(f"{DISPLAY_NAMES.get(key, key)} omitted: fewer than two observed levels in complete cases.")
            continue
        ref = _reference_level(key, levels)
        cat_specs.append((key, ref, [level for level in levels if level != ref]))

    names = ["Intercept", *numeric_predictors]
    for key, ref, levels in cat_specs:
        names.extend([f"{key}={level}" for level in levels])

    x_rows: list[list[float]] = []
    y_values: list[float] = []
    for row in scoped:
        x = [1.0]
        x.extend(float(row[key]) for key in numeric_predictors)
        for key, ref, levels in cat_specs:
            value = str(row.get(key) or "").strip()
            x.extend([1.0 if value == level else 0.0 for level in levels])
        x_rows.append(x)
        y_values.append(float(row[outcome]))

    x_matrix = np.array(x_rows, dtype=float)
    y = np.array(y_values, dtype=float)
    n, p = x_matrix.shape
    if n <= p:
        return {
            "label": label,
            "outcome": DISPLAY_NAMES.get(outcome, outcome),
            "n": n,
            "status": f"Too few complete cases for {p} model parameter(s).",
            "rows": [],
            "omitted_covariates": omitted_covariates,
        }

    xtx_inv = np.linalg.pinv(x_matrix.T @ x_matrix)
    beta = xtx_inv @ x_matrix.T @ y
    fitted = x_matrix @ beta
    residuals = y - fitted
    hat = np.sum((x_matrix @ xtx_inv) * x_matrix, axis=1)
    hc3_weights = (residuals / np.clip(1.0 - hat, 1e-8, None)) ** 2
    meat = x_matrix.T @ (x_matrix * hc3_weights[:, None])
    cov = xtx_inv @ meat @ xtx_inv
    se = np.sqrt(np.maximum(np.diag(cov), 0.0))
    df = n - p
    crit = _t_critical(df)
    y_sd = float(np.std(y, ddof=1)) if n > 1 else 0.0
    total_ss = float(np.sum((y - np.mean(y)) ** 2))
    r2 = None if total_ss == 0 else float(1.0 - np.sum(residuals ** 2) / total_ss)

    result_rows: list[dict[str, Any]] = []
    for index, name in enumerate(names):
        estimate = float(beta[index])
        se_value = float(se[index]) if math.isfinite(se[index]) else None
        if not se_value or se_value == 0:
            t_value = None
            p_value = None
            ci_low = None
            ci_high = None
        else:
            t_value = estimate / se_value
            p_value = _p_two_tailed(t_value, df)
            ci_low = estimate - crit * se_value
            ci_high = estimate + crit * se_value

        predictor_sd = float(np.std(x_matrix[:, index], ddof=1)) if index > 0 and n > 1 else None
        std_beta = None
        if predictor_sd is not None and y_sd > 0:
            std_beta = estimate * predictor_sd / y_sd
        partial_r2 = None
        if t_value is not None:
            partial_r2 = (t_value ** 2) / (t_value ** 2 + df)

        contrast_estimate = None
        contrast_ci = ""
        if name in CONTRAST_MULTIPLIERS:
            multiplier = CONTRAST_MULTIPLIERS[name]
            contrast_estimate = estimate * multiplier
            if ci_low is not None and ci_high is not None:
                contrast_ci = f"[{_fmt(ci_low * multiplier)}, {_fmt(ci_high * multiplier)}]"

        result_rows.append({
            "term": DISPLAY_NAMES.get(name, name),
            "term_key": name,
            "b": estimate,
            "b_display": _fmt(estimate),
            "se_hc3": se_value,
            "se_hc3_display": _fmt(se_value),
            "ci_95": "" if ci_low is None or ci_high is None else f"[{_fmt(ci_low)}, {_fmt(ci_high)}]",
            "t": t_value,
            "t_display": _fmt(t_value),
            "p": p_value,
            "p_display": _fmt_p(p_value),
            "std_beta_display": _fmt(std_beta),
            "partial_r2_display": _fmt(partial_r2),
            "planned_contrast_estimate_display": _fmt(contrast_estimate),
            "planned_contrast_ci_95": contrast_ci,
        })

    predictors_text = [DISPLAY_NAMES.get(key, key) for key in numeric_predictors]
    for key, ref, levels in cat_specs:
        predictors_text.append(f"{DISPLAY_NAMES.get(key, key)} categorical, reference = {ref}")

    return {
        "label": label,
        "outcome": DISPLAY_NAMES.get(outcome, outcome),
        "formula": f"{DISPLAY_NAMES.get(outcome, outcome)} ~ " + " + ".join(predictors_text),
        "n": n,
        "df_residual": df,
        "r2_display": _fmt(r2),
        "status": "OK",
        "rows": result_rows,
        "omitted_covariates": omitted_covariates,
    }


def _term(model: dict[str, Any], term_key: str) -> dict[str, Any] | None:
    for row in model.get("rows", []):
        if row.get("term_key") == term_key:
            return row
    return None


def _focal_row(model: dict[str, Any], term_key: str, *, hypothesis: str, family: str | None = None, note: str = "") -> dict[str, Any] | None:
    row = _term(model, term_key)
    if not row:
        return None
    out = {
        "hypothesis": hypothesis,
        "model": model.get("label", ""),
        "outcome": model.get("outcome", ""),
        "n": model.get("n", 0),
        "term": row.get("term", ""),
        "b": row.get("b"),
        "b_display": row.get("b_display", ""),
        "se_hc3_display": row.get("se_hc3_display", ""),
        "ci_95": row.get("ci_95", ""),
        "planned_contrast_estimate_display": row.get("planned_contrast_estimate_display", ""),
        "planned_contrast_ci_95": row.get("planned_contrast_ci_95", ""),
        "t_display": row.get("t_display", ""),
        "p": row.get("p"),
        "p_display": row.get("p_display", ""),
        "std_beta_display": row.get("std_beta_display", ""),
        "partial_r2_display": row.get("partial_r2_display", ""),
        "note": note,
    }
    if family:
        out["p_family"] = family
    return out



def _complete_rows(rows: list[dict[str, Any]], keys: list[str], categorical_keys: list[str] | None = None) -> list[dict[str, Any]]:
    categorical_keys = categorical_keys or []
    complete = []
    for row in rows:
        if any(_num(row.get(key)) is None for key in keys):
            continue
        if any(not str(row.get(key) or "").strip() for key in categorical_keys):
            continue
        complete.append(row)
    return complete


def _categorical_matrix(rows: list[dict[str, Any]], keys: list[str]) -> tuple[list[str], np.ndarray]:
    names: list[str] = []
    columns: list[list[float]] = []
    for key in keys:
        levels = _levels(rows, key)
        if len(levels) < 2:
            continue
        ref = _reference_level(key, levels)
        for level in levels:
            if level == ref:
                continue
            names.append(f"{key}={level}")
            columns.append([1.0 if str(row.get(key) or "").strip() == level else 0.0 for row in rows])
    if not columns:
        return [], np.zeros((len(rows), 0))
    return names, np.array(columns, dtype=float).T


def _fast_design(rows: list[dict[str, Any]], numeric_predictors: list[str], categorical_predictors: list[str] | None = None) -> tuple[list[str], np.ndarray]:
    categorical_predictors = categorical_predictors or []
    names = ["Intercept", *numeric_predictors]
    columns = [[1.0 for _ in rows]] + [[float(row[key]) for row in rows] for key in numeric_predictors]
    cat_names, cat_matrix = _categorical_matrix(rows, categorical_predictors)
    matrix = np.array(columns, dtype=float).T
    if cat_matrix.shape[1]:
        matrix = np.column_stack([matrix, cat_matrix])
        names.extend(cat_names)
    return names, matrix


def _coef_vector(x: np.ndarray, y: np.ndarray) -> np.ndarray:
    return np.linalg.lstsq(x, y, rcond=None)[0]


def _bootstrap_ci(values: list[float]) -> tuple[float | None, float | None, float | None]:
    valid = sorted(value for value in values if value is not None and math.isfinite(value))
    if not valid:
        return None, None, None
    low = float(np.percentile(valid, 2.5))
    high = float(np.percentile(valid, 97.5))
    se = float(np.std(valid, ddof=1)) if len(valid) > 1 else None
    return se, low, high


def _mediation_parallel(rows: list[dict[str, Any]], outcome: str) -> dict[str, Any]:
    mediators = ["cl_intrinsic", "cl_extraneous", "cl_germane"]
    covariates = _retention_covariates(rows, outcome)
    keys = [outcome, "required_pause_contrast", "optional_pause_contrast", *mediators]
    complete = _complete_rows(rows, keys, covariates)
    if len(complete) < 8:
        return {"n": len(complete), "status": "Too few complete cases for the parallel mediation model.", "direct_rows": [], "indirect_rows": []}

    a_names, x_a = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast"])
    y_names, x_y = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast", *mediators], covariates)
    m_matrix = np.array([[float(row[mediator]) for mediator in mediators] for row in complete], dtype=float)
    y = np.array([float(row[outcome]) for row in complete], dtype=float)
    a_index = {name: index for index, name in enumerate(a_names)}
    y_index = {name: index for index, name in enumerate(y_names)}

    a_betas = {mediator: _coef_vector(x_a, m_matrix[:, i]) for i, mediator in enumerate(mediators)}
    y_beta = _coef_vector(x_y, y)

    indirect_specs = []
    for x_key in ("required_pause_contrast", "optional_pause_contrast"):
        total = 0.0
        for mediator in mediators:
            estimate = float(a_betas[mediator][a_index[x_key]] * y_beta[y_index[mediator]])
            total += estimate
            indirect_specs.append({"contrast_key": x_key, "mediator_key": mediator, "estimate": estimate})
        indirect_specs.append({"contrast_key": x_key, "mediator_key": "Total indirect", "estimate": total})

    rng = np.random.default_rng(BOOTSTRAP_SEED + (1 if outcome == "ret_immediate_score" else 2))
    boot: dict[tuple[str, str], list[float]] = {(spec["contrast_key"], spec["mediator_key"]): [] for spec in indirect_specs}
    n = len(complete)
    for _ in range(BOOTSTRAP_SAMPLES):
        idx = rng.integers(0, n, n)
        try:
            sample_a = {mediator: _coef_vector(x_a[idx], m_matrix[idx, i]) for i, mediator in enumerate(mediators)}
            sample_y = _coef_vector(x_y[idx], y[idx])
            for x_key in ("required_pause_contrast", "optional_pause_contrast"):
                total = 0.0
                for mediator in mediators:
                    value = float(sample_a[mediator][a_index[x_key]] * sample_y[y_index[mediator]])
                    boot[(x_key, mediator)].append(value)
                    total += value
                boot[(x_key, "Total indirect")].append(total)
        except Exception:
            continue

    indirect_rows = []
    for spec in indirect_specs:
        x_key = spec["contrast_key"]
        mediator = spec["mediator_key"]
        se, low, high = _bootstrap_ci(boot[(x_key, mediator)])
        indirect_rows.append({
            "contrast": DISPLAY_NAMES.get(x_key, x_key),
            "mediator": DISPLAY_NAMES.get(mediator, mediator),
            "effect": _fmt(spec["estimate"]),
            "boot_se": _fmt(se),
            "boot_ci_95": "" if low is None or high is None else f"[{_fmt(low)}, {_fmt(high)}]",
            "bootstrap_samples": len(boot[(x_key, mediator)]),
            "focal": "yes" if x_key == "required_pause_contrast" and mediator in {"cl_extraneous", "cl_germane"} else "",
        })

    direct_rows = [{"contrast": DISPLAY_NAMES.get(x_key, x_key), "direct_b": _fmt(float(y_beta[y_index[x_key]]))} for x_key in ("required_pause_contrast", "optional_pause_contrast")]
    return {"n": len(complete), "status": "OK", "direct_rows": direct_rows, "indirect_rows": indirect_rows, "covariates": covariates}


def _mediation_simple(rows: list[dict[str, Any]], outcome: str, mediator: str = "eng_main") -> dict[str, Any]:
    covariates = _retention_covariates(rows, outcome)
    keys = [outcome, "required_pause_contrast", "optional_pause_contrast", mediator]
    complete = _complete_rows(rows, keys, covariates)
    if len(complete) < 6:
        return {"n": len(complete), "status": "Too few complete cases for the simple mediation model.", "direct_rows": [], "indirect_rows": []}

    a_names, x_a = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast"])
    y_names, x_y = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast", mediator], covariates)
    m = np.array([float(row[mediator]) for row in complete], dtype=float)
    y = np.array([float(row[outcome]) for row in complete], dtype=float)
    a_index = {name: index for index, name in enumerate(a_names)}
    y_index = {name: index for index, name in enumerate(y_names)}
    a_beta = _coef_vector(x_a, m)
    y_beta = _coef_vector(x_y, y)
    indirect = {x_key: float(a_beta[a_index[x_key]] * y_beta[y_index[mediator]]) for x_key in ("required_pause_contrast", "optional_pause_contrast")}

    rng = np.random.default_rng(BOOTSTRAP_SEED + (11 if outcome == "ret_immediate_score" else 12))
    boot = {x_key: [] for x_key in indirect}
    n = len(complete)
    for _ in range(BOOTSTRAP_SAMPLES):
        idx = rng.integers(0, n, n)
        try:
            sample_a = _coef_vector(x_a[idx], m[idx])
            sample_y = _coef_vector(x_y[idx], y[idx])
            for x_key in indirect:
                boot[x_key].append(float(sample_a[a_index[x_key]] * sample_y[y_index[mediator]]))
        except Exception:
            continue

    indirect_rows = []
    for x_key, estimate in indirect.items():
        se, low, high = _bootstrap_ci(boot[x_key])
        indirect_rows.append({
            "contrast": DISPLAY_NAMES.get(x_key, x_key),
            "mediator": DISPLAY_NAMES.get(mediator, mediator),
            "effect": _fmt(estimate),
            "boot_se": _fmt(se),
            "boot_ci_95": "" if low is None or high is None else f"[{_fmt(low)}, {_fmt(high)}]",
            "bootstrap_samples": len(boot[x_key]),
            "focal": "yes" if x_key == "optional_pause_contrast" else "",
        })

    direct_rows = [{"contrast": DISPLAY_NAMES.get(x_key, x_key), "direct_b": _fmt(float(y_beta[y_index[x_key]]))} for x_key in indirect]
    return {"n": len(complete), "status": "OK", "direct_rows": direct_rows, "indirect_rows": indirect_rows, "covariates": covariates}


def _serial_mediation(rows: list[dict[str, Any]], outcome: str, load_key: str) -> dict[str, Any]:
    covariates = _retention_covariates(rows, outcome)
    keys = [outcome, "required_pause_contrast", "optional_pause_contrast", "eng_main", load_key]
    complete = _complete_rows(rows, keys, covariates)
    if len(complete) < 8:
        return {"n": len(complete), "status": "Too few complete cases for the serial mediation model.", "rows": []}

    a_names, x_a = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast"])
    d_names, x_d = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast", "eng_main"])
    b_names, x_b = _fast_design(complete, ["required_pause_contrast", "optional_pause_contrast", "eng_main", load_key], covariates)
    eng = np.array([float(row["eng_main"]) for row in complete], dtype=float)
    load = np.array([float(row[load_key]) for row in complete], dtype=float)
    y = np.array([float(row[outcome]) for row in complete], dtype=float)
    a_index = {name: index for index, name in enumerate(a_names)}
    d_index = {name: index for index, name in enumerate(d_names)}
    b_index = {name: index for index, name in enumerate(b_names)}
    a_beta = _coef_vector(x_a, eng)
    d_beta = _coef_vector(x_d, load)
    b_beta = _coef_vector(x_b, y)
    serial = {x_key: float(a_beta[a_index[x_key]] * d_beta[d_index["eng_main"]] * b_beta[b_index[load_key]]) for x_key in ("required_pause_contrast", "optional_pause_contrast")}

    rng = np.random.default_rng(BOOTSTRAP_SEED + {"cl_intrinsic": 31, "cl_extraneous": 32, "cl_germane": 33}.get(load_key, 30) + (0 if outcome == "ret_immediate_score" else 100))
    boot = {x_key: [] for x_key in serial}
    n = len(complete)
    for _ in range(BOOTSTRAP_SAMPLES):
        idx = rng.integers(0, n, n)
        try:
            sample_a = _coef_vector(x_a[idx], eng[idx])
            sample_d = _coef_vector(x_d[idx], load[idx])
            sample_b = _coef_vector(x_b[idx], y[idx])
            for x_key in serial:
                boot[x_key].append(float(sample_a[a_index[x_key]] * sample_d[d_index["eng_main"]] * sample_b[b_index[load_key]]))
        except Exception:
            continue

    serial_rows = []
    for x_key, estimate in serial.items():
        se, low, high = _bootstrap_ci(boot[x_key])
        serial_rows.append({
            "contrast": DISPLAY_NAMES.get(x_key, x_key),
            "path": f"Checkpoint Design -> Engagement -> {DISPLAY_NAMES.get(load_key, load_key)} -> {DISPLAY_NAMES.get(outcome, outcome)}",
            "effect": _fmt(estimate),
            "boot_se": _fmt(se),
            "boot_ci_95": "" if low is None or high is None else f"[{_fmt(low)}, {_fmt(high)}]",
            "bootstrap_samples": len(boot[x_key]),
        })
    return {"n": len(complete), "status": "OK", "rows": serial_rows, "covariates": covariates}

def _score_control(value: object, *, reverse: bool = False) -> float | None:
    score = _ctrl_valid_score(value)
    if score is None:
        return None
    return 8 - score if reverse else score

def _item_matrix(participants: list[dict[str, Any]], item_specs: list[dict[str, Any]]) -> tuple[np.ndarray, list[str]]:
    matrix = []
    labels = [spec["label"] for spec in item_specs]
    for participant in participants:
        values = participant.get("scale_values", {}) or {}
        row = []
        for spec in item_specs:
            raw = values.get(spec["column"])
            score = spec["scorer"](raw, reverse=spec.get("reverse", False))
            row.append(score)
        if all(value is not None for value in row):
            matrix.append([float(value) for value in row])
    return np.array(matrix, dtype=float), labels


def _alpha(matrix: np.ndarray) -> float | None:
    if matrix.ndim != 2 or matrix.shape[1] < 2 or matrix.shape[0] < 2:
        return None
    item_vars = np.var(matrix, axis=0, ddof=1)
    total_var = np.var(np.sum(matrix, axis=1), ddof=1)
    if total_var == 0:
        return None
    k = matrix.shape[1]
    return float((k / (k - 1)) * (1 - np.sum(item_vars) / total_var))


def _factor_summary(title: str, participants: list[dict[str, Any]], item_specs: list[dict[str, Any]]) -> dict[str, Any]:
    matrix, labels = _item_matrix(participants, item_specs)
    if matrix.shape[0] < 3 or matrix.shape[1] < 2:
        return {
            "title": title,
            "n_complete": int(matrix.shape[0]) if matrix.ndim == 2 else 0,
            "items": len(item_specs),
            "alpha": "",
            "first_eigenvalue": "",
            "first_factor_variance_percent": "",
            "loading_range": "",
            "status": "Too few complete cases/items for a one-factor check.",
            "loadings": [],
        }

    corr = np.corrcoef(matrix, rowvar=False)
    corr = np.nan_to_num(corr, nan=0.0)
    eigenvalues, eigenvectors = np.linalg.eigh(corr)
    order = np.argsort(eigenvalues)[::-1]
    eigenvalues = eigenvalues[order]
    eigenvectors = eigenvectors[:, order]
    loadings = eigenvectors[:, 0] * math.sqrt(max(float(eigenvalues[0]), 0.0))
    if np.sum(loadings) < 0:
        loadings = -loadings
    variance_percent = 100 * float(eigenvalues[0]) / len(item_specs)
    loading_values = [float(value) for value in loadings]

    return {
        "title": title,
        "n_complete": int(matrix.shape[0]),
        "items": len(item_specs),
        "alpha": _fmt(_alpha(matrix)),
        "first_eigenvalue": _fmt(float(eigenvalues[0])),
        "first_factor_variance_percent": _fmt(variance_percent, 1),
        "loading_range": f"{_fmt(min(loading_values))} to {_fmt(max(loading_values))}",
        "status": "One-factor PCA check on complete item rows.",
        "loadings": [{"item": label, "loading": _fmt(float(value))} for label, value in zip(labels, loadings)],
    }


def _factor_analyses(participants: list[dict[str, Any]]) -> list[dict[str, Any]]:
    cl_intrinsic = [
        {"column": f"cl_ch{chapter}_scores_{item}", "label": f"Ch{chapter} IL{item}", "scorer": cl_valid_score}
        for chapter in (1, 2, 3) for item in (1, 2, 3)
    ]
    cl_environment = [
        {"column": f"cl_ch{chapter}_scores_{item}", "label": f"Ch{chapter} environment EL{item}", "scorer": cl_valid_score}
        for chapter in (1, 2, 3) for item in (4, 5, 6, 7)
    ]
    cl_instruction = [
        {"column": f"cl_overall_scores_{item}", "label": f"Instruction EL{item}", "scorer": cl_valid_score, "reverse": True}
        for item in (1, 2, 3)
    ]
    cl_interaction = [
        {"column": f"cl_overall_scores_{item}", "label": f"Interaction EL{item}", "scorer": cl_valid_score, "reverse": True}
        for item in (4, 5, 6, 7)
    ]
    cl_germane = [
        {"column": f"cl_overall_scores_{item}", "label": f"Germane GL{item}", "scorer": cl_valid_score}
        for item in (8, 9, 10, 11)
    ]
    eng_chapter = [
        {"column": f"eng_ch{chapter}_scores_{item}", "label": f"Ch{chapter} ENG{item}", "scorer": eng_valid_score}
        for chapter in (1, 2, 3) for item in (1, 2, 3, 4, 5)
    ]
    eng_overall = [
        {"column": "eng_overall_scores_1", "label": "Frustrated reversed", "scorer": eng_valid_score, "reverse": True},
        {"column": "eng_overall_scores_2", "label": "Confusing reversed", "scorer": eng_valid_score, "reverse": True},
        {"column": "eng_overall_scores_3", "label": "Worth my time", "scorer": eng_valid_score},
        {"column": "eng_overall_scores_4", "label": "Rewarding", "scorer": eng_valid_score},
    ]
    control = [
        {"column": "ctrl_scores_1", "label": "Freedom to decide", "scorer": _score_control},
        {"column": "ctrl_scores_2", "label": "Influence over checkpoint", "scorer": _score_control},
    ]
    return [
        _factor_summary("Cognitive load - intrinsic", participants, cl_intrinsic),
        _factor_summary("Cognitive load - environment-related extraneous", participants, cl_environment),
        _factor_summary("Cognitive load - instruction-related extraneous", participants, cl_instruction),
        _factor_summary("Cognitive load - interaction-related extraneous", participants, cl_interaction),
        _factor_summary("Cognitive load - germane", participants, cl_germane),
        _factor_summary("Engagement - chapter-specific", participants, eng_chapter),
        _factor_summary("Engagement - overall", participants, eng_overall),
        _factor_summary("Perceived control", participants, control),
    ]


def _power_summary(rows: list[dict[str, Any]]) -> dict[str, Any]:
    delayed_rows = [row for row in rows if row.get("ret_delayed_score") is not None]
    return {
        "planned_total": 258,
        "planned_per_condition": 86,
        "planning_effect": "d = 0.43",
        "planning_test": "required continue vs required pauses on immediate retention, alpha = .05, power = .80",
        "current_total": len(rows),
        "current_by_condition": _condition_counts(rows),
        "current_delayed_total": len(delayed_rows),
        "current_delayed_by_condition": _condition_counts(delayed_rows),
        "note": "The preregistered planning target is anchored to the focal direct effect on immediate retention. Delayed-retention and mediation analyses have lower effective power, especially when follow-up or scored-prompt data are missing.",
    }


def _model_rows(models: list[dict[str, Any]]) -> list[dict[str, Any]]:
    rows = []
    for model in models:
        rows.append({
            "model": model.get("label", ""),
            "formula": model.get("formula", ""),
            "n": model.get("n", 0),
            "df_residual": model.get("df_residual", ""),
            "r2": model.get("r2_display", ""),
            "status": model.get("status", ""),
            "omitted_covariates": "; ".join(model.get("omitted_covariates", [])),
        })
    return rows


def build_inferential_statistics(
    participants: list[dict[str, Any]],
    retention_scores_path: Path = RETENTION_SCORES_PATH,
) -> dict[str, Any]:
    enriched, retention_warnings = _attach_scores(participants, retention_scores_path)
    rows = _analysis_rows(enriched)

    direct_immediate = _fit_lm(rows, label="H1 immediate retention", outcome="ret_immediate_score", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"], categorical_predictors=_retention_covariates(rows, "ret_immediate_score"))
    direct_delayed = _fit_lm(rows, label="H1 delayed retention", outcome="ret_delayed_score", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"], categorical_predictors=_retention_covariates(rows, "ret_delayed_score"))
    cl_intrinsic = _fit_lm(rows, label="H2a intrinsic load", outcome="cl_intrinsic", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"])
    cl_extraneous = _fit_lm(rows, label="H2a extraneous load", outcome="cl_extraneous", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"])
    cl_germane = _fit_lm(rows, label="H2a germane load", outcome="cl_germane", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"])
    engagement = _fit_lm(rows, label="H3a engagement", outcome="eng_main", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"])
    control = _fit_lm(rows, label="Manipulation check perceived control", outcome="ctrl_perceived", numeric_predictors=["required_pause_contrast", "optional_pause_contrast"])
    retention_paths_immediate = _fit_lm(rows, label="H2b/H3b immediate retention paths", outcome="ret_immediate_score", numeric_predictors=["eng_main", "cl_intrinsic", "cl_extraneous", "cl_germane"], categorical_predictors=_retention_covariates(rows, "ret_immediate_score"))
    retention_paths_delayed = _fit_lm(rows, label="H2b/H3b delayed retention paths", outcome="ret_delayed_score", numeric_predictors=["eng_main", "cl_intrinsic", "cl_extraneous", "cl_germane"], categorical_predictors=_retention_covariates(rows, "ret_delayed_score"))
    h4_intrinsic = _fit_lm(rows, label="H4 engagement -> intrinsic load", outcome="cl_intrinsic", numeric_predictors=["eng_main"])
    h4_extraneous = _fit_lm(rows, label="H4 engagement -> extraneous load", outcome="cl_extraneous", numeric_predictors=["eng_main"])
    h4_germane = _fit_lm(rows, label="H4 engagement -> germane load", outcome="cl_germane", numeric_predictors=["eng_main"])

    focal_rows: list[dict[str, Any]] = []
    for model, term_key, hypothesis, family, note in [
        (direct_immediate, "required_pause_contrast", "H1", None, "Primary endpoint; required pauses are expected to be higher than required continue."),
        (direct_delayed, "required_pause_contrast", "H1", None, "Secondary endpoint because follow-up completion can reduce power."),
        (direct_immediate, "optional_pause_contrast", "H1", None, "Planned non-directional comparison: optional pauses versus the two system-controlled designs."),
        (direct_delayed, "optional_pause_contrast", "H1", None, "Secondary non-directional comparison."),
        (cl_intrinsic, "required_pause_contrast", "H2a", "H2a cognitive-load models", "No directional prediction for intrinsic load."),
        (cl_extraneous, "required_pause_contrast", "H2a", "H2a cognitive-load models", "Required pauses are expected to reduce extraneous load."),
        (cl_germane, "required_pause_contrast", "H2a", "H2a cognitive-load models", "Required pauses are expected to increase germane load."),
        (engagement, "optional_pause_contrast", "H3a", None, "Optional pauses are expected to increase engagement relative to the system-controlled designs."),
        (engagement, "required_pause_contrast", "H3a", None, "Required pauses are expected to lower engagement relative to required continue."),
        (control, "optional_pause_contrast", "Manipulation check", None, "Optional pauses should show higher perceived control."),
        (retention_paths_immediate, "cl_extraneous", "H2b", None, "Expected negative association with immediate retention."),
        (retention_paths_immediate, "cl_germane", "H2b", None, "Expected positive association with immediate retention."),
        (retention_paths_immediate, "cl_intrinsic", "H2b", None, "No directional prediction."),
        (retention_paths_immediate, "eng_main", "H3b", None, "Expected positive association with immediate retention."),
        (h4_intrinsic, "eng_main", "H4", "H4 engagement-load models", "No directional prediction for intrinsic load."),
        (h4_extraneous, "eng_main", "H4", "H4 engagement-load models", "Expected negative association with extraneous load."),
        (h4_germane, "eng_main", "H4", "H4 engagement-load models", "Expected positive association with germane load."),
    ]:
        row = _focal_row(model, term_key, hypothesis=hypothesis, family=family, note=note)
        if row:
            focal_rows.append(row)

    _holm_adjust(focal_rows, "H2a cognitive-load models")
    _holm_adjust(focal_rows, "H4 engagement-load models")

    age_gender_covariates = ["age"] + (["gender"] if len(_levels(rows, "gender")) > 1 else [])
    robustness_age_gender = [
        _fit_lm(rows, label="Robustness: H1 immediate + age/gender", outcome="ret_immediate_score", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *[key for key in age_gender_covariates if key == "age"]], categorical_predictors=[key for key in [*_retention_covariates(rows, "ret_immediate_score"), "gender"] if len(_levels(rows, key)) > 1]),
        _fit_lm(rows, label="Robustness: extraneous load + age/gender", outcome="cl_extraneous", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *[key for key in age_gender_covariates if key == "age"]], categorical_predictors=["gender"] if len(_levels(rows, "gender")) > 1 else []),
        _fit_lm(rows, label="Robustness: engagement + age/gender", outcome="eng_main", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *[key for key in age_gender_covariates if key == "age"]], categorical_predictors=["gender"] if len(_levels(rows, "gender")) > 1 else []),
    ]
    context_cats = ["room_type"] if len(_levels(rows, "room_type")) > 1 else []
    context_nums = ["same_room_n"] if _numeric_has_variation(rows, "same_room_n") else []
    robustness_context = [
        _fit_lm(rows, label="Context check: H1 immediate + room", outcome="ret_immediate_score", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *context_nums], categorical_predictors=[key for key in [*_retention_covariates(rows, "ret_immediate_score"), *context_cats] if len(_levels(rows, key)) > 1]),
        _fit_lm(rows, label="Context check: extraneous load + room", outcome="cl_extraneous", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *context_nums], categorical_predictors=context_cats),
        _fit_lm(rows, label="Context check: engagement + room", outcome="eng_main", numeric_predictors=["required_pause_contrast", "optional_pause_contrast", *context_nums], categorical_predictors=context_cats),
    ] if context_cats or context_nums else []

    h2_parallel_immediate = _mediation_parallel(rows, "ret_immediate_score")
    h2_parallel_delayed = _mediation_parallel(rows, "ret_delayed_score")
    h3_simple_immediate = _mediation_simple(rows, "ret_immediate_score")
    h3_simple_delayed = _mediation_simple(rows, "ret_delayed_score")
    serial_immediate = [_serial_mediation(rows, "ret_immediate_score", load_key) for load_key in ["cl_intrinsic", "cl_extraneous", "cl_germane"]]
    serial_delayed = [_serial_mediation(rows, "ret_delayed_score", load_key) for load_key in ["cl_intrinsic", "cl_extraneous", "cl_germane"]]

    warnings = list(retention_warnings)
    warnings.extend(sorted({row.get("collection_context_warning") for row in rows if row.get("collection_context_warning")}))
    warnings.extend(sorted({row.get("retention_counterbalance_warning") for row in rows if row.get("retention_counterbalance_warning")}))
    if len(_levels(rows, "retention_immediate_form_order")) < 2:
        warnings.append("Immediate retention-form order was not available with at least two levels, so immediate retention models could not include this preregistered design covariate.")
    delayed_rows_with_scores = [row for row in rows if row.get("ret_delayed_score") is not None]
    if delayed_rows_with_scores and len(_levels(delayed_rows_with_scores, "retention_delayed_form_order")) < 2:
        warnings.append("Delayed retention-form order was not available with at least two levels among delayed complete cases, so delayed retention models could not include this preregistered design covariate.")
    if not context_cats and not context_nums:
        warnings.append("Room-context data are available only descriptively, or have no usable variation, so contextual robustness models are not shown.")
    warnings = list(dict.fromkeys(warnings))

    direct_models = [direct_immediate, direct_delayed, cl_intrinsic, cl_extraneous, cl_germane, engagement, control, retention_paths_immediate, retention_paths_delayed, h4_intrinsic, h4_extraneous, h4_germane]

    return {
        "status": "ready" if rows else "No included participants available yet.",
        "intro": "Use this tab as an inferential-statistics audit trail. The decision rule is preregistered alpha = .05 for direct/association models and bootstrap 95% CIs for indirect effects, but interpretation should not stop at the p-value. Read the sign, effect estimate, 95% CI, partial r² or standardized beta, sample size, and whether robustness checks tell the same story.",
        "warnings": warnings,
        "power": _power_summary(rows),
        "location_summary": _location_summary(rows),
        "lab_slot_summary": _lab_slot_summary(rows),
        "calculation_notes": [
            {"item": "Checkpoint Design coding", "calculation": "required-pause contrast: required continue = -1, required pauses = 1, optional pauses = 0; optional-pauses contrast: required continue = -0.5, required pauses = -0.5, optional pauses = 1."},
            {"item": "Direct models", "calculation": "Linear models use HC3 heteroskedasticity-consistent standard errors. Immediate and delayed retention models include the matching retention-form order when START/INIT_START is available with more than one observed level."},
            {"item": "Planned-contrast estimates", "calculation": "For readability, the table also reports the contrast on the group-difference scale: required pauses - required continue = 2*b; optional pauses - average system-controlled = 1.5*b."},
            {"item": "Mediation models", "calculation": f"Indirect effects use {BOOTSTRAP_SAMPLES:,} percentile bootstrap samples with a fixed seed. A total/direct effect is not required before inspecting preregistered indirect effects."},
            {"item": "Location context", "calculation": "REMOTE = 1 is coded as At home. For lab sessions, resources/collection_locations.json maps each collection date to Creative Space or Living Room. Shared-slot n is the number of other included participants in the same date, time slot, and lab location."},
            {"item": "Factor analyses", "calculation": "Scale checks report Cronbach's alpha and a one-factor PCA check on complete item rows. These are measurement checks; they do not replace preregistered construct scoring."},
        ],
        "focal_rows": focal_rows,
        "model_rows": _model_rows(direct_models),
        "models": direct_models,
        "robustness_age_gender": _model_rows(robustness_age_gender),
        "robustness_context": _model_rows(robustness_context),
        "mediation": {
            "h2_parallel_immediate": h2_parallel_immediate,
            "h2_parallel_delayed": h2_parallel_delayed,
            "h3_simple_immediate": h3_simple_immediate,
            "h3_simple_delayed": h3_simple_delayed,
            "serial_immediate": serial_immediate,
            "serial_delayed": serial_delayed,
        },
        "factor_analyses": _factor_analyses(enriched),
    }
