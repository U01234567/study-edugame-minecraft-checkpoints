from __future__ import annotations

# -----------------------------------------------------------------------------
# statistics_subscales.py
# -----------------------------------------------------------------------------
# Appendix report for rerunning the manuscript inferential statistics with the
# Cognitive Load and Engagement subscale/intermediate scores.
#
# Intent of this file:
#   - Keep the inferential scoring policy aligned with statistics_manuscript.py:
#     every displayed scale score used in a model requires all raw items that
#     define that scale/component to be present and inside the valid response
#     range.
#   - Recalculate the original merged-construct results as anchors.
#   - Recalculate the same families of inferential models with subscale scores.
#   - Show simple original-vs-subscale comparisons immediately after each family.
#   - Include the same style of assumption checks, HC3 robust OLS paths,
#     bootstrapped mediation intervals, Holm corrections, and covariate-adjusted
#     robustness models as the manuscript report.
#   - Omit scoring/descriptives already present in statistics_manuscript.html,
#     except for a small alpha/completeness audit requested for the appendix.
# -----------------------------------------------------------------------------

import datetime as dt
import math
import statistics
import sys
from pathlib import Path
from typing import Any

import numpy as np

THIS_FILE = Path(__file__).resolve()
if THIS_FILE.parent.name == "apps":
    ANALYSIS_DIR = THIS_FILE.parents[1]
    APPS_DIR = THIS_FILE.parent
else:
    ANALYSIS_DIR = THIS_FILE.parent
    APPS_DIR = ANALYSIS_DIR / "apps"

for path in (ANALYSIS_DIR, APPS_DIR):
    if str(path) not in sys.path:
        sys.path.insert(0, str(path))

import statistics_manuscript as sm  # noqa: E402

OUTPUT_PATH = ANALYSIS_DIR / "output" / "statistics_separated_scores.html"
ALPHA = sm.ALPHA
BOOTSTRAP_ITERATIONS = 1000
BOOTSTRAP_SEED = 20260622

ScaleItemSpec = tuple[str, float, float, bool]


# -----------------------------------------------------------------------------
# Logging / small display helpers
# -----------------------------------------------------------------------------


def log_step(message: str) -> None:
    print(f"[statistics_subscales] {message}", flush=True)


def h(value: object) -> str:
    return sm.h(value)


def clean(value: object) -> str:
    return sm.clean(value)


def fmt(value: float | None, digits: int = 3) -> str:
    return sm.fmt(value, digits=digits)


def p_text(value: float | None) -> str:
    return sm.p_text(value)


def ci_text(low: float | None, high: float | None, digits: int = 3) -> str:
    return sm.ci_text(low, high, digits=digits)


def sig_from_p(p_value: float | None) -> bool | None:
    if p_value is None or not math.isfinite(p_value):
        return None
    return p_value < ALPHA


def sig_from_ci(low: float | None, high: float | None) -> bool | None:
    if low is None or high is None or not math.isfinite(low) or not math.isfinite(high):
        return None
    return low > 0 or high < 0


def direction(value: float | None) -> str:
    if value is None or not math.isfinite(value):
        return "—"
    if value > 0:
        return "+"
    if value < 0:
        return "−"
    return "0"


def sig_badge(is_sig: bool | None) -> str:
    if is_sig is None:
        return '<span class="badge badge-neutral">not available</span>'
    if is_sig:
        return '<span class="badge badge-good">supported</span>'
    return '<span class="badge badge-neutral">not supported</span>'


def compact_sig_label(is_sig: bool | None) -> str:
    if is_sig is None:
        return "—"
    return "yes" if is_sig else "no"


def compact_effect_text(result: dict[str, Any] | None, *, include_p: bool = True) -> str:
    if not result:
        return "—"
    estimate = result.get("estimate")
    if estimate is None:
        return "—"
    parts = [f"b/r = {fmt(estimate)}"]
    if include_p and "p" in result:
        parts.append(f"p = {p_text(result.get('p'))}")
    if "p_holm" in result:
        parts.append(f"pHolm = {p_text(result.get('p_holm'))}")
    if "low" in result or "high" in result:
        parts.append(f"95% CI {ci_text(result.get('low'), result.get('high'))}")
    return ", ".join(parts)


def html_section(title: str, subtitle: str, body: str, *, badge: str = "Appendix") -> str:
    return (
        f'<section id="{h(sm.slugify(title))}" class="card body-output">'
        f'<h2>{h(title)} <span>{h(badge)}</span></h2>'
        f'<p>{h(subtitle)}</p>'
        f'{body}'
        '</section>'
    )


def details_block(title: str, body: str, *, open_: bool = True) -> str:
    opened = " open" if open_ else ""
    return f'<details class="compact-details"{opened}><summary>{h(title)}</summary>{body}</details>'


def table_html(headers: list[str], rows: list[list[str]], *, class_name: str = "model-table") -> str:
    if not rows:
        rows = [[f'<td colspan="{len(headers)}">No rows available.</td>']]
        header_html = "".join(f"<th>{h(header)}</th>" for header in headers)
        return f'<div class="table-wrap"><table class="{h(class_name)}"><thead><tr>{header_html}</tr></thead><tbody><tr>{rows[0][0]}</tr></tbody></table></div>'
    header_html = "".join(f"<th>{h(header)}</th>" for header in headers)
    body_html = "".join("<tr>" + "".join(cell if cell.startswith("<t") else f"<td>{cell}</td>" for cell in row) + "</tr>" for row in rows)
    return f'<div class="table-wrap"><table class="{h(class_name)}"><thead><tr>{header_html}</tr></thead><tbody>{body_html}</tbody></table></div>'


# -----------------------------------------------------------------------------
# Strict scale construction matching statistics_manuscript.py inferential logic
# -----------------------------------------------------------------------------


def cl(column: str, reverse: bool = False) -> ScaleItemSpec:
    return (column, 0.0, 10.0, reverse)


def eng(column: str, reverse: bool = False) -> ScaleItemSpec:
    return (column, 1.0, 7.0, reverse)


INTRINSIC_CHAPTER_SPECS: dict[str, list[ScaleItemSpec]] = {
    f"cl_intrinsic_ch{chapter}": [cl(f"cl_ch{chapter}_scores_{index}") for index in (1, 2, 3)]
    for chapter in (1, 2, 3)
}
EXTRANEOUS_ENV_CHAPTER_SPECS: dict[str, list[ScaleItemSpec]] = {
    f"cl_extraneous_environment_ch{chapter}": [cl(f"cl_ch{chapter}_scores_{index}") for index in (4, 5, 6, 7)]
    for chapter in (1, 2, 3)
}
EXTRANEOUS_INSTRUCTION_SPECS = [cl(f"cl_overall_scores_{index}", reverse=True) for index in (1, 2, 3)]
EXTRANEOUS_INTERACTION_SPECS = [cl(f"cl_overall_scores_{index}", reverse=True) for index in (4, 5, 6, 7)]
GERMANE_SPECS = [cl(f"cl_overall_scores_{index}") for index in (8, 9, 10, 11)]
ENGAGEMENT_CHAPTER_SPECS: dict[str, list[ScaleItemSpec]] = {
    f"engagement_ch{chapter}": [eng(f"eng_ch{chapter}_scores_{index}") for index in (1, 2, 3, 4, 5)]
    for chapter in (1, 2, 3)
}
ENGAGEMENT_OVERALL_SPECS = [
    eng("eng_overall_scores_1", reverse=True),
    eng("eng_overall_scores_2", reverse=True),
    eng("eng_overall_scores_3"),
    eng("eng_overall_scores_4"),
]


SCORE_LABELS = {
    "cl_intrinsic": "Original intrinsic load",
    "cl_extraneous": "Original extraneous load",
    "cl_germane": "Original germane load",
    "engagement": "Original engagement",
    "cl_intrinsic_chapter_merged": "Intrinsic load: merged chapter score",
    "cl_intrinsic_ch1": "Intrinsic load: chapter 1",
    "cl_intrinsic_ch2": "Intrinsic load: chapter 2",
    "cl_intrinsic_ch3": "Intrinsic load: chapter 3",
    "cl_extraneous_environment_chapter_merged": "Extraneous load: environment, merged chapters",
    "cl_extraneous_environment_ch1": "Extraneous load: environment chapter 1",
    "cl_extraneous_environment_ch2": "Extraneous load: environment chapter 2",
    "cl_extraneous_environment_ch3": "Extraneous load: environment chapter 3",
    "cl_extraneous_instruction_overall": "Extraneous load: instruction overall",
    "cl_extraneous_interaction_overall": "Extraneous load: interaction overall",
    "cl_extraneous_game_overall": "Extraneous load: instruction + interaction overall",
    "engagement_chapter_merged": "Engagement: merged chapter score",
    "engagement_ch1": "Engagement: chapter 1",
    "engagement_ch2": "Engagement: chapter 2",
    "engagement_ch3": "Engagement: chapter 3",
    "engagement_overall_game": "Engagement: game overall",
}


def score_item(row: dict[str, str], spec: ScaleItemSpec) -> float | None:
    column, minimum, maximum, reverse = spec
    value = sm.parse_float(row.get(column))
    if value is None or value < minimum or value > maximum:
        return None
    if reverse:
        return minimum + maximum - value
    return value


def strict_mean(row: dict[str, str], specs: list[ScaleItemSpec]) -> float | None:
    values: list[float] = []
    for spec in specs:
        value = score_item(row, spec)
        if value is None:
            return None
        values.append(value)
    return statistics.fmean(values) if values else None


def mean_if_all(values: list[float | None]) -> float | None:
    if not values or any(value is None or not math.isfinite(float(value)) for value in values):
        return None
    return statistics.fmean([float(value) for value in values])


def immediate_survey_rows_by_mcid() -> dict[str, dict[str, str]]:
    survey_rows = sm.read_tsv(sm.SURVEY_EXPORT_PATH)
    immediate_by_mcid: dict[str, dict[str, str]] = {}
    for row in survey_rows:
        participant_id = sm.mcid_from_row(row)
        if participant_id and not sm.delayed_flag(row):
            immediate_by_mcid.setdefault(participant_id, row)
    return immediate_by_mcid


def calculate_subscale_scores_for_row(row: dict[str, str]) -> dict[str, float | None]:
    scores: dict[str, float | None] = {}

    for key, specs in INTRINSIC_CHAPTER_SPECS.items():
        scores[key] = strict_mean(row, specs)
    scores["cl_intrinsic_chapter_merged"] = mean_if_all([scores[f"cl_intrinsic_ch{chapter}"] for chapter in (1, 2, 3)])

    for key, specs in EXTRANEOUS_ENV_CHAPTER_SPECS.items():
        scores[key] = strict_mean(row, specs)
    scores["cl_extraneous_environment_chapter_merged"] = mean_if_all([scores[f"cl_extraneous_environment_ch{chapter}"] for chapter in (1, 2, 3)])
    scores["cl_extraneous_instruction_overall"] = strict_mean(row, EXTRANEOUS_INSTRUCTION_SPECS)
    scores["cl_extraneous_interaction_overall"] = strict_mean(row, EXTRANEOUS_INTERACTION_SPECS)
    scores["cl_extraneous_game_overall"] = mean_if_all([
        scores["cl_extraneous_instruction_overall"],
        scores["cl_extraneous_interaction_overall"],
    ])

    # Same four overall germane-load items as the original construct; included to
    # keep the subscale models readable and explicitly complete-case audited.
    scores["cl_germane_overall"] = strict_mean(row, GERMANE_SPECS)

    for key, specs in ENGAGEMENT_CHAPTER_SPECS.items():
        scores[key] = strict_mean(row, specs)
    scores["engagement_chapter_merged"] = mean_if_all([scores[f"engagement_ch{chapter}"] for chapter in (1, 2, 3)])
    scores["engagement_overall_game"] = strict_mean(row, ENGAGEMENT_OVERALL_SPECS)

    return scores


def build_rows_for_subscale_models() -> tuple[list[dict[str, Any]], list[str]]:
    rows, warnings = sm.build_rows_for_inferential_models()
    immediate_by_mcid = immediate_survey_rows_by_mcid()
    mismatch_count = 0

    for row in rows:
        participant_id = clean(row.get("MCID"))
        survey_row = immediate_by_mcid.get(participant_id)
        if not survey_row:
            continue
        scores = calculate_subscale_scores_for_row(survey_row)
        row.update(scores)
        # Aliases used by model families where GCL is conceptually overall-game.
        row["cl_germane_overall"] = scores.get("cl_germane_overall")

        # The following checks are deliberately soft warnings: the original
        # columns are retained from statistics_manuscript.py, so this appendix
        # cannot accidentally drift from the manuscript scoring.
        comparison_pairs = [
            ("cl_intrinsic", "cl_intrinsic_chapter_merged"),
            ("cl_germane", "cl_germane_overall"),
        ]
        for original, derived in comparison_pairs:
            original_value = row.get(original)
            derived_value = row.get(derived)
            if original_value is None and derived_value is None:
                continue
            if original_value is None or derived_value is None:
                mismatch_count += 1
                continue
            if abs(float(original_value) - float(derived_value)) > 1e-9:
                mismatch_count += 1

    if mismatch_count:
        warnings.append(
            f"Strictly recalculated subscale aliases differed from manuscript original columns in {mismatch_count} participant/scale check(s). "
            "The manuscript original columns are still used as the original-result anchor."
        )
    return rows, warnings


# -----------------------------------------------------------------------------
# Alpha / completeness audit
# -----------------------------------------------------------------------------


def cronbach_alpha(vectors: list[list[float]]) -> float | None:
    return sm._cronbach_alpha_from_vectors(vectors)


def vector_from_specs(row: dict[str, str], specs: list[ScaleItemSpec]) -> list[float] | None:
    values: list[float] = []
    for spec in specs:
        value = score_item(row, spec)
        if value is None:
            return None
        values.append(value)
    return values


def component_vector_from_specs(row: dict[str, str], components: list[list[ScaleItemSpec]]) -> list[float] | None:
    values: list[float] = []
    for specs in components:
        vector = vector_from_specs(row, specs)
        if vector is None:
            return None
        values.append(statistics.fmean(vector))
    return values


def alpha_rows() -> list[dict[str, Any]]:
    immediate_by_mcid = immediate_survey_rows_by_mcid()
    valid_rows = []
    for _participant_id, row in sorted(immediate_by_mcid.items()):
        condition = sm.canonical_condition(sm.first_present(row, ["condition", "Condition", "CONDITION", "experiment_condition", "condition_raw"]))
        if condition in sm.CONDITION_ORDER:
            valid_rows.append(row)
    eligible_n = len(valid_rows)

    rows: list[dict[str, Any]] = []

    def add_item_alpha(construct: str, scope: str, specs: list[ScaleItemSpec], basis: str) -> None:
        vectors = [vector for row in valid_rows if (vector := vector_from_specs(row, specs)) is not None]
        rows.append({
            "Construct": construct,
            "Scope": scope,
            "complete_n": len(vectors),
            "input_units": len(specs),
            "alpha": cronbach_alpha(vectors),
            "excluded_n": max(0, eligible_n - len(vectors)),
            "Basis": basis,
        })

    def add_component_alpha(construct: str, scope: str, components: list[list[ScaleItemSpec]], basis: str) -> None:
        vectors = [vector for row in valid_rows if (vector := component_vector_from_specs(row, components)) is not None]
        rows.append({
            "Construct": construct,
            "Scope": scope,
            "complete_n": len(vectors),
            "input_units": len(components),
            "alpha": cronbach_alpha(vectors),
            "excluded_n": max(0, eligible_n - len(vectors)),
            "Basis": basis,
        })

    intrinsic_components = [INTRINSIC_CHAPTER_SPECS[f"cl_intrinsic_ch{chapter}"] for chapter in (1, 2, 3)]
    for chapter, specs in zip((1, 2, 3), intrinsic_components, strict=True):
        add_item_alpha("Intrinsic cognitive load", f"Chapter {chapter}", specs, "Three chapter-specific intrinsic-load items.")
    add_item_alpha("Intrinsic cognitive load", "Merged chapter raw items", [spec for comp in intrinsic_components for spec in comp], "All nine chapter-level intrinsic-load items; same value as merged chapter score because chapters have equal item counts.")
    add_component_alpha("Intrinsic cognitive load", "Original/full construct", intrinsic_components, "Three chapter means, matching the manuscript full-construct aggregation.")

    env_components = [EXTRANEOUS_ENV_CHAPTER_SPECS[f"cl_extraneous_environment_ch{chapter}"] for chapter in (1, 2, 3)]
    for chapter, specs in zip((1, 2, 3), env_components, strict=True):
        add_item_alpha("Extraneous cognitive load", f"Environment chapter {chapter}", specs, "Four environment-related chapter-level extraneous-load items.")
    add_item_alpha("Extraneous cognitive load", "Environment merged chapters", [spec for comp in env_components for spec in comp], "All twelve environment-related chapter-level items.")
    add_item_alpha("Extraneous cognitive load", "Instruction overall", EXTRANEOUS_INSTRUCTION_SPECS, "Three reverse-coded overall instruction-related ECL items.")
    add_item_alpha("Extraneous cognitive load", "Interaction overall", EXTRANEOUS_INTERACTION_SPECS, "Four reverse-coded overall interaction-related ECL items.")
    add_item_alpha("Extraneous cognitive load", "Instruction + interaction overall", EXTRANEOUS_INSTRUCTION_SPECS + EXTRANEOUS_INTERACTION_SPECS, "Seven reverse-coded game-overall ECL items.")
    add_component_alpha("Extraneous cognitive load", "Original/full construct", [[spec for comp in env_components for spec in comp], EXTRANEOUS_INSTRUCTION_SPECS, EXTRANEOUS_INTERACTION_SPECS], "Environment, instruction, and interaction component means, matching the manuscript full-construct aggregation.")

    add_item_alpha("Germane cognitive load", "Game overall", GERMANE_SPECS, "Four overall germane-load items.")
    add_item_alpha("Germane cognitive load", "Original/full construct", GERMANE_SPECS, "Same four overall germane-load items; no chapter version exists.")

    engagement_components = [ENGAGEMENT_CHAPTER_SPECS[f"engagement_ch{chapter}"] for chapter in (1, 2, 3)]
    for chapter, specs in zip((1, 2, 3), engagement_components, strict=True):
        add_item_alpha("Engagement", f"Chapter {chapter}", specs, "Five chapter-specific engagement items.")
    add_item_alpha("Engagement", "Merged chapter raw items", [spec for comp in engagement_components for spec in comp], "All fifteen chapter-level engagement items.")
    add_item_alpha("Engagement", "Game overall", ENGAGEMENT_OVERALL_SPECS, "Four game-overall engagement items after reverse-coding frustration and confusion.")
    add_component_alpha("Engagement", "Original/full construct", [[spec for comp in engagement_components for spec in comp], ENGAGEMENT_OVERALL_SPECS], "Chapter-level engagement mean and game-overall engagement mean, matching the manuscript full-construct aggregation.")

    return rows


def alpha_audit_html(rows: list[dict[str, Any]]) -> str:
    table_rows: list[list[str]] = []
    for row in rows:
        table_rows.append([
            f'<th>{h(row["Construct"])}</th>',
            f'<td>{h(row["Scope"])}</td>',
            f'<td>{h(row["complete_n"])}</td>',
            f'<td>{h(row["input_units"])}</td>',
            f'<td>{h(sm._format_cronbach_alpha(row.get("alpha")))}</td>',
            f'<td>{h(row["excluded_n"])}</td>',
            f'<td>{h(row["Basis"])}</td>',
        ])
    return html_section(
        "Internal consistency audit for appendix subscales",
        "Cronbach's alpha is recalculated before the inferential appendix results. Strict complete cases are used for alpha vectors, and reverse-coded items are scored before alpha is estimated.",
        '<p class="notice">The full scoring/descriptives section remains in <code>statistics_manuscript.html</code>. This appendix only repeats internal consistency for the Cognitive Load and Engagement constructs/subscales so the scale split can be checked locally.</p>'
        + table_html(["Construct", "Scope", "complete n", "input units k", "Cronbach's α", "excluded n", "Input used"], table_rows),
    )


def strict_score_completeness_html(rows: list[dict[str, Any]]) -> str:
    variables = [
        "cl_intrinsic", "cl_intrinsic_chapter_merged", "cl_extraneous", "cl_extraneous_environment_chapter_merged",
        "cl_extraneous_instruction_overall", "cl_extraneous_interaction_overall", "cl_extraneous_game_overall",
        "cl_germane", "engagement", "engagement_chapter_merged", "engagement_overall_game",
    ]
    table_rows: list[list[str]] = []
    for variable in variables:
        complete = [row for row in rows if sm.parse_float(row.get(variable)) is not None]
        table_rows.append([
            f'<td>{h(SCORE_LABELS.get(variable, variable))}</td>',
            f'<td><code>{h(variable)}</code></td>',
            f'<td>{len(complete)}</td>',
            f'<td>{max(0, len(rows) - len(complete))}</td>',
        ])
    return details_block(
        "Strict score completeness audit used by inferential models",
        '<p class="small">These counts are participant-level availability before outcome-specific model complete-case filtering. The inferential models may have smaller n because retention outcomes and covariates are also required.</p>'
        + table_html(["Score", "Column", "available n", "unavailable n"], table_rows),
        open_=False,
    )


# -----------------------------------------------------------------------------
# OLS and mediation result extraction
# -----------------------------------------------------------------------------


def contrast_effects_from_model(model: dict[str, Any]) -> dict[str, dict[str, Any]]:
    effects: dict[str, dict[str, Any]] = {}
    for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
        if not model.get("ok"):
            effects[contrast] = {"estimate": None, "se": None, "p": None, "low": None, "high": None, "n": model.get("n"), "ok": False}
            continue
        effect = sm.scaled_effect_from_ols(model, contrast, sm.CONTRAST_MULTIPLIERS[contrast])
        if effect is None:
            effects[contrast] = {"estimate": None, "se": None, "p": None, "low": None, "high": None, "n": model.get("n"), "ok": False}
            continue
        effects[contrast] = {**effect, "n": model.get("n"), "ok": True}
    return effects


def term_effect_from_model(model: dict[str, Any], term: str) -> dict[str, Any]:
    if not model.get("ok") or term not in model.get("names", []):
        return {"estimate": None, "se": None, "p": None, "low": None, "high": None, "n": model.get("n"), "ok": False}
    idx = model["names"].index(term)
    row = model["coeff_rows"][idx]
    return {
        "estimate": row.get("b"),
        "se": row.get("SE_HC3"),
        "p": row.get("p"),
        "low": row.get("CI_low"),
        "high": row.get("CI_high"),
        "n": model.get("n"),
        "ok": True,
    }


def run_mediation(
    *,
    label: str,
    rows: list[dict[str, Any]],
    mediators: list[str],
    outcome: str,
    include_covariates: bool,
    iterations: int = BOOTSTRAP_ITERATIONS,
    seed: int = BOOTSTRAP_SEED,
) -> dict[str, Any]:
    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    needed = ["required_pause_contrast", "optional_pause_contrast", *mediators, outcome, *cov_num, *cov_cat]
    complete, excluded = sm.make_complete_cases(rows, needed, f"{label} mediation")
    result: dict[str, Any] = {
        "label": label,
        "mediators": mediators,
        "outcome": outcome,
        "include_covariates": include_covariates,
        "n": len(complete),
        "excluded": excluded,
        "ok": False,
        "error": "",
        "a": {},
        "a_holm": {},
        "b": {},
        "total": {},
        "direct": {},
        "indirect": {},
    }
    if len(complete) < 10:
        result["error"] = "Too few complete cases for mediation."
        return result

    a_models = {mediator: sm.fit_ols_arrays(complete, mediator, ["required_pause_contrast", "optional_pause_contrast", *cov_num], cov_cat) for mediator in mediators}
    y_model = sm.fit_ols_arrays(complete, outcome, ["required_pause_contrast", "optional_pause_contrast", *mediators, *cov_num], cov_cat)
    total_model = sm.fit_ols_arrays(complete, outcome, ["required_pause_contrast", "optional_pause_contrast", *cov_num], cov_cat)
    if not y_model.get("ok") or not total_model.get("ok") or any(not model.get("ok") for model in a_models.values()):
        result["error"] = "One or more component regressions could not be fitted."
        result["component_errors"] = {
            "y_model": y_model.get("error"),
            "total_model": total_model.get("error"),
            **{f"a_model_{m}": model.get("error") for m, model in a_models.items() if not model.get("ok")},
        }
        return result

    result["ok"] = True
    result["a_models"] = a_models
    result["y_model"] = y_model
    result["total_model"] = total_model
    result["total"] = contrast_effects_from_model(total_model)
    result["direct"] = contrast_effects_from_model(y_model)

    a_p_values: list[float | None] = []
    a_keys: list[tuple[str, str]] = []
    for mediator in mediators:
        result["b"][mediator] = term_effect_from_model(y_model, mediator)
        for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
            a_effect = contrast_effects_from_model(a_models[mediator])[contrast]
            result["a"][(contrast, mediator)] = a_effect
            a_p_values.append(a_effect.get("p"))
            a_keys.append((contrast, mediator))
    for key, p_holm in zip(a_keys, sm.holm_adjust(a_p_values)):
        result["a_holm"][key] = p_holm
        result["a"][key]["p_holm"] = p_holm

    point_indirect: dict[tuple[str, str], float] = {}
    for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
        for mediator in mediators:
            a = a_models[mediator]["beta"][a_models[mediator]["names"].index(contrast)]
            b = y_model["beta"][y_model["names"].index(mediator)]
            point_indirect[(contrast, mediator)] = float(sm.CONTRAST_MULTIPLIERS[contrast] * a * b)

    boot_values: dict[tuple[str, str], list[float]] = {key: [] for key in point_indirect}
    index_sets = sm.bootstrap_indices(len(complete), iterations, seed)
    for indices in index_sets:
        sample = [complete[int(i)] for i in indices]
        try:
            sample_a = {mediator: sm.fit_ols_arrays(sample, mediator, ["required_pause_contrast", "optional_pause_contrast", *cov_num], cov_cat) for mediator in mediators}
            sample_y = sm.fit_ols_arrays(sample, outcome, ["required_pause_contrast", "optional_pause_contrast", *mediators, *cov_num], cov_cat)
            if not sample_y.get("ok") or any(not model.get("ok") for model in sample_a.values()):
                continue
            for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
                for mediator in mediators:
                    a = sample_a[mediator]["beta"][sample_a[mediator]["names"].index(contrast)]
                    b = sample_y["beta"][sample_y["names"].index(mediator)]
                    boot_values[(contrast, mediator)].append(float(sm.CONTRAST_MULTIPLIERS[contrast] * a * b))
        except Exception:
            continue

    for key, point in point_indirect.items():
        vals = boot_values[key]
        low = high = None
        if len(vals) >= 50:
            low, high = np.percentile(vals, [2.5, 97.5]).tolist()
        result["indirect"][key] = {
            "estimate": point,
            "low": low,
            "high": high,
            "p": None,
            "boot_success": len(vals),
            "n": len(complete),
            "ok": low is not None and high is not None,
        }
    return result


def mediation_result_tables(result: dict[str, Any]) -> str:
    if not result.get("ok"):
        return sm.model_status_note(result.get("error", "Model could not be fitted."), "red") + sm.combined_exclusion_details("Show mediation exclusions", result.get("excluded", []))

    a_rows: list[list[str]] = []
    for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
        for mediator in result["mediators"]:
            effect = result["a"].get((contrast, mediator), {})
            is_sig = sig_from_p(effect.get("p_holm"))
            cls1 = '<td class="significant-cell">' if is_sig else '<td>'
            a_rows.append([
                f'<td>{h(sm.CONTRAST_DISPLAY[contrast])}</td>',
                f'<td>{h(SCORE_LABELS.get(mediator, mediator))}<br><code>{h(mediator)}</code></td>',
                f'<td>{h(effect.get("n"))}</td>',
                f'<td>{fmt(effect.get("estimate"))}</td>',
                f'<td>{fmt(effect.get("se"))}</td>',
                f'<td>{p_text(effect.get("p"))}</td>',
                f'{cls1}{p_text(effect.get("p_holm"))}</td>',
                f'<td>{ci_text(effect.get("low"), effect.get("high"))}</td>',
            ])

    indirect_rows: list[list[str]] = []
    for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
        for mediator in result["mediators"]:
            effect = result["indirect"].get((contrast, mediator), {})
            is_sig = sig_from_ci(effect.get("low"), effect.get("high"))
            cls = '<td class="significant-cell">' if is_sig else '<td>'
            indirect_rows.append([
                f'<td>{h(sm.CONTRAST_DISPLAY[contrast])}</td>',
                f'<td>{h(SCORE_LABELS.get(mediator, mediator))}<br><code>{h(mediator)}</code></td>',
                f'<td>{h(effect.get("n"))}</td>',
                f'{cls}{fmt(effect.get("estimate"))}</td>',
                f'<td>{ci_text(effect.get("low"), effect.get("high"))}</td>',
                f'<td>{h(effect.get("boot_success"))}</td>',
            ])

    b_rows: list[list[str]] = []
    for mediator in result["mediators"]:
        effect = result["b"].get(mediator, {})
        is_sig = sig_from_p(effect.get("p"))
        cls = '<td class="significant-cell">' if is_sig else '<td>'
        b_rows.append([
            f'<td>{h(SCORE_LABELS.get(mediator, mediator))}<br><code>{h(mediator)}</code></td>',
            f'<td>{h(effect.get("n"))}</td>',
            f'{cls}{fmt(effect.get("estimate"))}</td>',
            f'<td>{fmt(effect.get("se"))}</td>',
            f'<td>{p_text(effect.get("p"))}</td>',
            f'<td>{ci_text(effect.get("low"), effect.get("high"))}</td>',
        ])

    direct_rows: list[list[str]] = []
    for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
        total = result["total"].get(contrast, {})
        direct = result["direct"].get(contrast, {})
        direct_rows.append([
            f'<td>{h(sm.CONTRAST_DISPLAY[contrast])}</td>',
            f'<td>{h(total.get("n"))}</td>',
            f'<td>{fmt(total.get("estimate"))}</td>',
            f'<td>{p_text(total.get("p"))}</td>',
            f'<td>{ci_text(total.get("low"), total.get("high"))}</td>',
            f'<td>{fmt(direct.get("estimate"))}</td>',
            f'<td>{p_text(direct.get("p"))}</td>',
            f'<td>{ci_text(direct.get("low"), direct.get("high"))}</td>',
        ])

    model_type = "covariate-adjusted" if result.get("include_covariates") else "base"
    return (
        f'<p><strong>Model:</strong> {h(result["label"])} ({h(model_type)}), outcome = <code>{h(result["outcome"])}</code>, n = {h(result.get("n"))}; bootstrap iterations requested = {BOOTSTRAP_ITERATIONS}.</p>'
        '<h4>a-paths: Checkpoint Design → mediator(s)</h4>'
        + table_html(["Contrast", "Mediator", "n", "b", "SE", "raw p", "Holm p", "95% robust CI"], a_rows)
        + '<h4>Indirect effects: Checkpoint Design → mediator → retention</h4>'
        + table_html(["Contrast", "Mediator", "n", "Indirect b", "95% bootstrap CI", "Successful bootstraps"], indirect_rows)
        + '<h4>b-paths: mediator(s) → retention</h4>'
        + table_html(["Mediator", "n", "b", "SE", "p", "95% robust CI"], b_rows)
        + '<h4>Total and direct planned contrast effects</h4>'
        + table_html(["Contrast", "n", "Total b", "Total p", "Total 95% robust CI", "Direct b", "Direct p", "Direct 95% robust CI"], direct_rows)
        + sm.combined_exclusion_details("Show mediation complete-case exclusions", result.get("excluded", []))
    )


def mediation_comparison_html(original: dict[str, Any], alternatives: list[dict[str, Any]], *, title: str) -> str:
    rows: list[list[str]] = []
    for alt in alternatives:
        if not alt.get("ok"):
            rows.append([
                f'<td>{h(alt.get("label"))}</td>', '<td>—</td>', '<td>—</td>', '<td>—</td>', '<td>—</td>',
                f'<td>{h(alt.get("error", "not fitted"))}</td>',
            ])
            continue
        for contrast in ["required_pause_contrast", "optional_pause_contrast"]:
            original_direct = original.get("direct", {}).get(contrast, {}) if original.get("ok") else {}
            alt_direct = alt.get("direct", {}).get(contrast, {})
            original_sig_any = False
            alt_sig_any = False
            original_dir_set: set[str] = set()
            alt_dir_set: set[str] = set()
            for key, effect in original.get("indirect", {}).items():
                c, _m = key
                if c == contrast:
                    original_sig_any = original_sig_any or bool(sig_from_ci(effect.get("low"), effect.get("high")))
                    original_dir_set.add(direction(effect.get("estimate")))
            for key, effect in alt.get("indirect", {}).items():
                c, _m = key
                if c == contrast:
                    alt_sig_any = alt_sig_any or bool(sig_from_ci(effect.get("low"), effect.get("high")))
                    alt_dir_set.add(direction(effect.get("estimate")))
            note_parts: list[str] = []
            if compact_sig_label(sig_from_p(original_direct.get("p"))) != compact_sig_label(sig_from_p(alt_direct.get("p"))):
                note_parts.append("direct-effect support changed")
            if original_sig_any != alt_sig_any:
                note_parts.append("indirect-effect support changed")
            if original_dir_set and alt_dir_set and original_dir_set != alt_dir_set:
                note_parts.append("one or more indirect-effect directions changed")
            rows.append([
                f'<td>{h(alt.get("label"))}</td>',
                f'<td>{h(sm.CONTRAST_DISPLAY[contrast])}</td>',
                f'<td>{compact_effect_text(original_direct)}</td>',
                f'<td>{compact_effect_text(alt_direct)}</td>',
                f'<td>original any indirect supported: {h("yes" if original_sig_any else "no")}<br>subscale any indirect supported: {h("yes" if alt_sig_any else "no")}</td>',
                f'<td>{h("; ".join(note_parts) if note_parts else "same support pattern")}</td>',
            ])
    return details_block(
        title,
        '<p class="small">This deliberately simple overview compares support/direction patterns. It does not replace the full coefficient, indirect-effect, and assumption-check tables above.</p>'
        + table_html(["Subscale model", "Contrast", "Original direct result", "Subscale direct result", "Indirect-effect support", "Plain-language comparison"], rows),
        open_=True,
    )


# -----------------------------------------------------------------------------
# H1: original retention benchmark
# -----------------------------------------------------------------------------


def h1_diagnostics_html(rows: list[dict[str, Any]]) -> str:
    immediate_form = sm.categorical_if_available(rows, "retention_form_order", "ret_immediate_score")
    delayed_form = sm.categorical_if_available(rows, "retention_form_order", "ret_delayed_score")
    return (
        sm.diagnostics_block("H1 base diagnostic: immediate retention", sm.fit_diagnostic_residuals(rows, "ret_immediate_score", ["required_pause_contrast", "optional_pause_contrast"], immediate_form))
        + sm.diagnostics_block("H1 base diagnostic: delayed retention", sm.fit_diagnostic_residuals(rows, "ret_delayed_score", ["required_pause_contrast", "optional_pause_contrast"], delayed_form))
        + sm.diagnostics_block("H1 covariate diagnostic: immediate retention", sm.fit_diagnostic_residuals(rows, "ret_immediate_score", ["required_pause_contrast", "optional_pause_contrast", "co_present_participants", "age"], [*immediate_form, "location", "gender"]))
        + sm.diagnostics_block("H1 covariate diagnostic: delayed retention", sm.fit_diagnostic_residuals(rows, "ret_delayed_score", ["required_pause_contrast", "optional_pause_contrast", "co_present_participants", "age"], [*delayed_form, "location", "gender"]))
    )


def h1_section(rows: list[dict[str, Any]], warnings: list[str]) -> str:
    log_step("Rendering H1 original benchmark models.")
    base_complete, base_excluded = sm.make_complete_cases(rows, ["ret_immediate_score", "required_pause_contrast", "optional_pause_contrast"], "H1 immediate base pool")
    data_html = sm.condition_count_table(base_complete, "Immediate-retention base complete cases", ["ret_immediate_score"]) + sm.covariate_feasibility_table(rows)
    final_html = sm.h1_confirmatory_models_html(
        rows,
        heading="Original H1 benchmark recalculated in the subscale appendix",
        note="H1 does not use Cognitive Load or Engagement; it is recalculated here so later subscale results can be compared against the same retention-effect benchmark.",
        model_label_prefix="Appendix H1 original benchmark",
    )
    return sm.assumption_section_shell(
        "H1 original retention benchmark",
        "H1 is not altered by the subscale split. It is recalculated as an anchor for the appendix.",
        [
            "Outcomes: immediate and delayed retention, continuous/bounded 0–2.",
            "Predictor: Checkpoint Design represented by C1 and C2 planned contrasts.",
            "Base models include retention form/order when available; covariate models add retention form/order, location, co-present participants, age, and gender.",
        ],
        [
            {"Assumption": "Independent observations", "How to test": "Each MCID contributes one immediate and one delayed retention score.", "How to read": "No repeated rows should enter a single OLS model."},
            {"Assumption": "Linearity/additivity", "How to test": "Inspect residual-vs-fitted plots.", "How to read": "A random residual cloud supports the linear-additive model."},
            {"Assumption": "Residual normality", "How to test": "Inspect Q-Q plots and Shapiro-type diagnostics where available.", "How to read": "Strong deviations mainly affect small-sample p-values/CIs."},
            {"Assumption": "Homoscedasticity/influence", "How to test": "Inspect residual diagnostics and leverage/outlier counts.", "How to read": "HC3 robust SEs reduce sensitivity to heteroscedasticity."},
        ],
        data_html,
        h1_diagnostics_html(rows),
        final_html,
        sm.status_messages([], warnings, "H1 original benchmark models were generated."),
        sm.combined_exclusion_details("Show H1 base complete-case exclusions", base_excluded),
    )


# -----------------------------------------------------------------------------
# H2/H3 mediation sections
# -----------------------------------------------------------------------------


def mediation_diagnostics_for_set(rows: list[dict[str, Any]], mediators: list[str], prefix: str, *, include_covariates: bool) -> str:
    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    cov_suffix = " + covariates" if include_covariates else ""
    html_parts: list[str] = []
    for mediator in mediators:
        html_parts.append(sm.diagnostics_block(
            f"{prefix} a-path diagnostic: {SCORE_LABELS.get(mediator, mediator)}{cov_suffix}",
            sm.fit_diagnostic_residuals(rows, mediator, ["required_pause_contrast", "optional_pause_contrast", *cov_num], cov_cat),
        ))
    for outcome in ["ret_immediate_score", "ret_delayed_score"]:
        html_parts.append(sm.diagnostics_block(
            f"{prefix} b/direct diagnostic: {outcome}{cov_suffix}",
            sm.fit_diagnostic_residuals(rows, outcome, ["required_pause_contrast", "optional_pause_contrast", *mediators, *cov_num], cov_cat),
        ))
    return "".join(html_parts)


def run_mediation_set(rows: list[dict[str, Any]], label: str, mediators: list[str], *, family: str) -> list[dict[str, Any]]:
    outputs: list[dict[str, Any]] = []
    for include_covariates in (False, True):
        for outcome in ("ret_immediate_score", "ret_delayed_score"):
            log_step(f"Running {family} mediation: {label}; outcome={outcome}; covariates={include_covariates}.")
            outputs.append(run_mediation(
                label=label,
                rows=rows,
                mediators=mediators,
                outcome=outcome,
                include_covariates=include_covariates,
            ))
    return outputs


def select_result(results: list[dict[str, Any]], *, label: str, outcome: str, include_covariates: bool) -> dict[str, Any]:
    for result in results:
        if result.get("label") == label and result.get("outcome") == outcome and result.get("include_covariates") == include_covariates:
            return result
    return {"ok": False, "label": label, "outcome": outcome, "include_covariates": include_covariates, "error": "Result not found."}


def mediation_set_details(results: list[dict[str, Any]], *, heading: str) -> str:
    blocks: list[str] = []
    for result in results:
        model_type = "covariate-adjusted" if result.get("include_covariates") else "base"
        outcome_label = "Immediate retention" if result.get("outcome") == "ret_immediate_score" else "Delayed retention"
        blocks.append(details_block(f"{heading}: {result.get('label')} · {outcome_label} · {model_type}", mediation_result_tables(result), open_=False))
    return "".join(blocks)


def h2_section(rows: list[dict[str, Any]], warnings: list[str]) -> str:
    log_step("Preparing H2 cognitive-load subscale model families.")
    original_label = "Original merged cognitive-load constructs"
    decomposed_label = "Subscale model: ICL chapter + ECL environment/instruction/interaction + GCL"
    together_label = "Subscale sensitivity: ICL chapter + ECL environment/game-overall together + GCL"
    source_label = "Source-aligned subscale checks: chapter/environment and game-overall routes"

    original_mediators = ["cl_intrinsic", "cl_extraneous", "cl_germane"]
    decomposed_mediators = [
        "cl_intrinsic_chapter_merged",
        "cl_extraneous_environment_chapter_merged",
        "cl_extraneous_instruction_overall",
        "cl_extraneous_interaction_overall",
        "cl_germane",
    ]
    together_mediators = [
        "cl_intrinsic_chapter_merged",
        "cl_extraneous_environment_chapter_merged",
        "cl_extraneous_game_overall",
        "cl_germane",
    ]
    source_models = {
        "Subscale source route: chapter ICL + chapter environment ECL": ["cl_intrinsic_chapter_merged", "cl_extraneous_environment_chapter_merged"],
        "Subscale source route: overall instruction/interaction ECL + GCL": ["cl_extraneous_instruction_overall", "cl_extraneous_interaction_overall", "cl_germane"],
        "Subscale source route: overall ECL together + GCL": ["cl_extraneous_game_overall", "cl_germane"],
    }

    original_results = run_mediation_set(rows, original_label, original_mediators, family="H2")
    decomposed_results = run_mediation_set(rows, decomposed_label, decomposed_mediators, family="H2")
    together_results = run_mediation_set(rows, together_label, together_mediators, family="H2")
    source_results: list[dict[str, Any]] = []
    for label, mediators in source_models.items():
        source_results.extend(run_mediation_set(rows, label, mediators, family="H2"))

    base_complete, base_excluded = sm.make_complete_cases(rows, ["required_pause_contrast", "optional_pause_contrast", *original_mediators], "H2 original mediator pool")
    sub_complete, _ = sm.make_complete_cases(rows, ["required_pause_contrast", "optional_pause_contrast", *decomposed_mediators], "H2 decomposed subscale mediator pool")
    data_html = (
        sm.condition_count_table(base_complete, "Original merged cognitive-load complete-case pool", original_mediators)
        + sm.condition_count_table(sub_complete, "Primary decomposed subscale complete-case pool", decomposed_mediators)
        + strict_score_completeness_html(rows)
        + sm.covariate_feasibility_table(rows)
    )
    diagnostics = (
        '<h4>Original merged mediator diagnostics</h4>'
        + mediation_diagnostics_for_set(rows, original_mediators, "H2 original", include_covariates=False)
        + '<h4>Primary subscale mediator diagnostics</h4>'
        + mediation_diagnostics_for_set(rows, decomposed_mediators, "H2 primary subscale", include_covariates=False)
        + details_block("Covariate-adjusted diagnostics for original and primary subscale H2 models", mediation_diagnostics_for_set(rows, original_mediators, "H2 original", include_covariates=True) + mediation_diagnostics_for_set(rows, decomposed_mediators, "H2 primary subscale", include_covariates=True), open_=False)
    )

    comparison_blocks: list[str] = []
    all_alt = decomposed_results + together_results + source_results
    for include_covariates in (False, True):
        for outcome in ("ret_immediate_score", "ret_delayed_score"):
            original = select_result(original_results, label=original_label, outcome=outcome, include_covariates=include_covariates)
            alts = [result for result in all_alt if result.get("outcome") == outcome and result.get("include_covariates") == include_covariates]
            outcome_label = "immediate retention" if outcome == "ret_immediate_score" else "delayed retention"
            model_label = "covariate-adjusted" if include_covariates else "base"
            comparison_blocks.append(mediation_comparison_html(original, alts, title=f"Simple H2 comparison: original vs subscales · {outcome_label} · {model_label}"))

    final_models_html = (
        '<p class="notice"><strong>Reading guide:</strong> the first details recalculate the original merged-construct model. The following details replace the merged constructs with subscale/intermediate scores. Instruction-related and interaction-related game-overall ECL are shown separately, and the combined game-overall ECL score is shown as an additional sensitivity.</p>'
        + mediation_set_details(original_results, heading="H2 original")
        + mediation_set_details(decomposed_results, heading="H2 primary subscale")
        + mediation_set_details(together_results, heading="H2 combined-overall-ECL sensitivity")
        + mediation_set_details(source_results, heading="H2 source-aligned subscale checks")
        + ''.join(comparison_blocks)
    )
    return sm.assumption_section_shell(
        "H2 cognitive-load mediation: original vs subscales",
        "The original H2 parallel-mediation model is recalculated, then rerun using Cognitive Load subscale/intermediate scores instead of the merged extraneous-load construct.",
        [
            "Predictor: Checkpoint Design represented by C1 and C2 planned contrasts.",
            "Original mediators: intrinsic, extraneous, and germane cognitive load.",
            "Subscale mediators: merged chapter ICL, merged chapter environment ECL, game-overall instruction ECL, game-overall interaction ECL, combined game-overall ECL, and GCL.",
            "All scale scores use strict complete raw-item scoring, matching statistics_manuscript.py inferential scoring.",
            "Final mediation uses HC3 robust OLS paths and percentile bootstrap CIs for indirect effects; robustness models add location, co-present participants, age, and gender.",
        ],
        [
            {"Assumption": "Correct causal ordering", "How to test": "Confirm that checkpoint design preceded load reports and retention outcomes.", "How to read": "Mediation is interpreted cautiously as theoretically ordered indirect-effect evidence."},
            {"Assumption": "Regression assumptions for each path", "How to test": "Inspect a-path and b/direct-path residual diagnostics.", "How to read": "Use residual-vs-fitted, Q-Q, outlier, and VIF diagnostics before interpreting paths."},
            {"Assumption": "No severe multicollinearity", "How to test": "Inspect VIF in outcome equations containing parallel mediators.", "How to read": "High VIF means unique b-paths may be unstable."},
            {"Assumption": "Indirect-effect uncertainty", "How to test": "Use bootstrap CIs.", "How to read": "An indirect effect is supported when its bootstrap CI excludes zero."},
        ],
        data_html,
        diagnostics,
        final_models_html,
        sm.status_messages([], warnings, "H2 original and subscale mediation models were generated."),
        sm.combined_exclusion_details("Show H2 original mediator-pool exclusions", base_excluded),
    )


def h3_section(rows: list[dict[str, Any]], warnings: list[str]) -> str:
    log_step("Preparing H3 engagement subscale model families.")
    original_label = "Original merged engagement construct"
    chapter_label = "Subscale model: merged chapter engagement"
    overall_label = "Subscale model: game-overall engagement"
    parallel_label = "Subscale sensitivity: chapter engagement + game-overall engagement in parallel"

    original_results = run_mediation_set(rows, original_label, ["engagement"], family="H3")
    chapter_results = run_mediation_set(rows, chapter_label, ["engagement_chapter_merged"], family="H3")
    overall_results = run_mediation_set(rows, overall_label, ["engagement_overall_game"], family="H3")
    parallel_results = run_mediation_set(rows, parallel_label, ["engagement_chapter_merged", "engagement_overall_game"], family="H3")

    base_complete, base_excluded = sm.make_complete_cases(rows, ["required_pause_contrast", "optional_pause_contrast", "engagement"], "H3 original mediator pool")
    sub_complete, _ = sm.make_complete_cases(rows, ["required_pause_contrast", "optional_pause_contrast", "engagement_chapter_merged", "engagement_overall_game"], "H3 subscale mediator pool")
    data_html = (
        sm.condition_count_table(base_complete, "Original merged engagement complete-case pool", ["engagement"])
        + sm.condition_count_table(sub_complete, "Engagement subscale complete-case pool", ["engagement_chapter_merged", "engagement_overall_game"])
        + strict_score_completeness_html(rows)
        + sm.covariate_feasibility_table(rows)
    )
    diagnostics = (
        '<h4>Original merged engagement diagnostics</h4>'
        + mediation_diagnostics_for_set(rows, ["engagement"], "H3 original", include_covariates=False)
        + '<h4>Engagement subscale diagnostics</h4>'
        + mediation_diagnostics_for_set(rows, ["engagement_chapter_merged", "engagement_overall_game"], "H3 subscale", include_covariates=False)
        + details_block("Covariate-adjusted diagnostics for original and engagement-subscale H3 models", mediation_diagnostics_for_set(rows, ["engagement"], "H3 original", include_covariates=True) + mediation_diagnostics_for_set(rows, ["engagement_chapter_merged", "engagement_overall_game"], "H3 subscale", include_covariates=True), open_=False)
    )

    comparison_blocks: list[str] = []
    all_alt = chapter_results + overall_results + parallel_results
    for include_covariates in (False, True):
        for outcome in ("ret_immediate_score", "ret_delayed_score"):
            original = select_result(original_results, label=original_label, outcome=outcome, include_covariates=include_covariates)
            alts = [result for result in all_alt if result.get("outcome") == outcome and result.get("include_covariates") == include_covariates]
            outcome_label = "immediate retention" if outcome == "ret_immediate_score" else "delayed retention"
            model_label = "covariate-adjusted" if include_covariates else "base"
            comparison_blocks.append(mediation_comparison_html(original, alts, title=f"Simple H3 comparison: original vs engagement subscales · {outcome_label} · {model_label}"))

    final_models_html = (
        '<p class="notice"><strong>Reading guide:</strong> the original merged Engagement mediation is recalculated first. Then the same mediation is run once with merged chapter engagement, once with game-overall engagement, and once with both engagement subscales in a parallel sensitivity model.</p>'
        + mediation_set_details(original_results, heading="H3 original")
        + mediation_set_details(chapter_results, heading="H3 chapter engagement subscale")
        + mediation_set_details(overall_results, heading="H3 game-overall engagement subscale")
        + mediation_set_details(parallel_results, heading="H3 parallel engagement-subscale sensitivity")
        + ''.join(comparison_blocks)
    )
    return sm.assumption_section_shell(
        "H3 engagement mediation: original vs subscales",
        "The original H3 Engagement mediation is recalculated and then rerun using the chapter and game-overall Engagement scores separately.",
        [
            "Predictor: Checkpoint Design represented by C1 and C2 planned contrasts.",
            "Original mediator: merged Engagement.",
            "Subscale mediators: merged chapter Engagement and game-overall Engagement.",
            "All Engagement scores use strict complete raw-item scoring and reverse-code frustration/confusion before averaging.",
            "Final mediation uses HC3 robust OLS paths and percentile bootstrap CIs; robustness models add location, co-present participants, age, and gender.",
        ],
        [
            {"Assumption": "Correct causal ordering", "How to test": "Confirm that checkpoint design preceded engagement reports and retention outcomes.", "How to read": "Mediation wording should remain cautious."},
            {"Assumption": "Regression assumptions for each path", "How to test": "Inspect a-path and b/direct-path residual diagnostics.", "How to read": "Use residual-vs-fitted, Q-Q, outlier, and VIF diagnostics before interpreting paths."},
            {"Assumption": "Outcome and mediator scale", "How to test": "Check retention remains 0–2 and engagement remains 1–7.", "How to read": "Out-of-range values are coding/data issues."},
            {"Assumption": "Indirect-effect uncertainty", "How to test": "Use bootstrap CIs.", "How to read": "An indirect effect is supported when its bootstrap CI excludes zero."},
        ],
        data_html,
        diagnostics,
        final_models_html,
        sm.status_messages([], warnings, "H3 original and engagement-subscale mediation models were generated."),
        sm.combined_exclusion_details("Show H3 original mediator-pool exclusions", base_excluded),
    )


# -----------------------------------------------------------------------------
# H4 correlations and covariate-adjusted partial correlations
# -----------------------------------------------------------------------------


def pearson_ci(r_value: float, n: int) -> tuple[float | None, float | None]:
    if n <= 3:
        return None, None
    z = math.atanh(max(-0.999999, min(0.999999, float(r_value))))
    se = 1 / math.sqrt(n - 3)
    return math.tanh(z - 1.96 * se), math.tanh(z + 1.96 * se)


def residualize_for_partial(complete: list[dict[str, Any]], variable: str, cov_num: list[str], cov_cat: list[str]) -> tuple[list[float] | None, int]:
    x_matrix, _names, _notes = sm.design_matrix_for_assumptions(complete, cov_num, cov_cat)
    if x_matrix is None:
        return None, 0
    y = np.array([float(sm.parse_float(row.get(variable))) for row in complete], dtype=float)
    beta = np.linalg.pinv(x_matrix.T @ x_matrix) @ x_matrix.T @ y
    residuals = y - x_matrix @ beta
    return [float(value) for value in residuals], x_matrix.shape[1] - 1


def run_correlation(rows: list[dict[str, Any]], x_var: str, y_var: str, *, include_covariates: bool) -> dict[str, Any]:
    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    needed = [x_var, y_var, *cov_num, *cov_cat]
    complete, excluded = sm.make_complete_cases(rows, needed, f"H4 correlation {x_var} with {y_var}")
    result: dict[str, Any] = {"x": x_var, "y": y_var, "n": len(complete), "excluded": excluded, "include_covariates": include_covariates, "ok": False}
    if len(complete) < 4 or sm.scipy_stats is None:
        result["error"] = "Not enough complete cases or SciPy unavailable."
        return result

    if include_covariates:
        x_values, cov_parameters = residualize_for_partial(complete, x_var, cov_num, cov_cat)
        y_values, _ = residualize_for_partial(complete, y_var, cov_num, cov_cat)
        if x_values is None or y_values is None:
            result["error"] = "Could not residualise variables on covariates."
            return result
        df = max(1, len(complete) - cov_parameters - 2)
    else:
        x_values = [float(sm.parse_float(row.get(x_var))) for row in complete]
        y_values = [float(sm.parse_float(row.get(y_var))) for row in complete]
        cov_parameters = 0
        df = max(1, len(complete) - 2)

    r_value, _raw_p = sm.scipy_stats.pearsonr(x_values, y_values)
    r_value = float(r_value)
    if abs(r_value) >= 1:
        p_value = 0.0
    else:
        t_value = r_value * math.sqrt(df / max(1e-12, 1 - r_value * r_value))
        p_value = float(2 * sm.scipy_stats.t.sf(abs(t_value), df))
    low, high = pearson_ci(r_value, len(complete))
    rs, ps = sm.scipy_stats.spearmanr(x_values, y_values)
    result.update({
        "ok": True,
        "estimate": r_value,
        "p": p_value,
        "low": low,
        "high": high,
        "r2": r_value * r_value,
        "spearman": float(rs),
        "spearman_p": float(ps),
        "df": df,
        "covariate_parameters": cov_parameters,
    })
    return result


def run_correlation_family(rows: list[dict[str, Any]], pairs: list[tuple[str, str, str]], *, include_covariates: bool) -> list[dict[str, Any]]:
    results = []
    for label, x_var, y_var in pairs:
        results.append({"label": label, **run_correlation(rows, x_var, y_var, include_covariates=include_covariates)})
    adjusted = sm.holm_adjust([result.get("p") for result in results])
    for result, p_holm in zip(results, adjusted):
        result["p_holm"] = p_holm
    return results


def correlation_table(results: list[dict[str, Any]], title: str) -> str:
    rows: list[list[str]] = []
    for result in results:
        if not result.get("ok"):
            rows.append([
                f'<td>{h(result.get("label"))}</td>',
                f'<td>{h(SCORE_LABELS.get(result.get("x"), result.get("x")))}</td>',
                f'<td>{h(SCORE_LABELS.get(result.get("y"), result.get("y")))}</td>',
                f'<td>{h(result.get("n"))}</td>',
                f'<td colspan="7">{h(result.get("error", "not available"))}</td>',
            ])
            continue
        is_sig = sig_from_p(result.get("p_holm"))
        cls = '<td class="significant-cell">' if is_sig else '<td>'
        rows.append([
            f'<td>{h(result.get("label"))}</td>',
            f'<td>{h(SCORE_LABELS.get(result.get("x"), result.get("x")))}<br><code>{h(result.get("x"))}</code></td>',
            f'<td>{h(SCORE_LABELS.get(result.get("y"), result.get("y")))}<br><code>{h(result.get("y"))}</code></td>',
            f'<td>{h(result.get("n"))}</td>',
            f'{cls}{fmt(result.get("estimate"))}</td>',
            f'<td>{p_text(result.get("p"))}</td>',
            f'<td>{p_text(result.get("p_holm"))}</td>',
            f'<td>{ci_text(result.get("low"), result.get("high"))}</td>',
            f'<td>{fmt(result.get("r2"))}</td>',
            f'<td>{fmt(result.get("spearman"))}</td>',
            f'<td>{p_text(result.get("spearman_p"))}</td>',
        ])
    return details_block(
        title,
        table_html(["Pair label", "X", "Y", "n", "Pearson/partial r", "raw p", "Holm p", "95% CI", "r²", "Spearman ρ sensitivity", "Spearman raw p"], rows),
        open_=False,
    )


def h4_comparison_html(original_results: list[dict[str, Any]], subscale_results: list[dict[str, Any]], *, title: str) -> str:
    rows: list[list[str]] = []
    original_sig = any(bool(sig_from_p(result.get("p_holm"))) for result in original_results if result.get("ok"))
    for result in subscale_results:
        if not result.get("ok"):
            rows.append([
                f'<td>{h(result.get("label"))}</td>', '<td>—</td>', '<td>—</td>', '<td>—</td>', f'<td>{h(result.get("error", "not fitted"))}</td>',
            ])
            continue
        sub_sig = bool(sig_from_p(result.get("p_holm")))
        note_parts: list[str] = []
        if original_sig != sub_sig:
            note_parts.append("support differs from at least one original H4 result")
        if direction(result.get("estimate")) == "+":
            note_parts.append("positive association")
        elif direction(result.get("estimate")) == "−":
            note_parts.append("negative association")
        rows.append([
            f'<td>{h(result.get("label"))}</td>',
            f'<td>{compact_sig_label(original_sig)}</td>',
            f'<td>{compact_effect_text(result)}</td>',
            f'<td>{compact_sig_label(sub_sig)}</td>',
            f'<td>{h("; ".join(note_parts) if note_parts else "same broad support pattern")}</td>',
        ])
    return details_block(
        title,
        '<p class="small">The comparison uses the family-level original H4 pattern as the anchor and then reports each source-aligned subscale pair.</p>'
        + table_html(["Subscale pair", "Any original H4 support?", "Subscale result", "Subscale supported?", "Plain-language comparison"], rows),
        open_=True,
    )


def h4_scatter_diagnostics_for_pairs(rows: list[dict[str, Any]], pairs: list[tuple[str, str, str]]) -> str:
    blocks: list[str] = []
    for label, x_var, y_var in pairs:
        complete, excluded = sm.make_complete_cases(rows, [x_var, y_var], f"H4 scatter {x_var} with {y_var}")
        x_values = [float(sm.parse_float(row.get(x_var))) for row in complete]
        y_values = [float(sm.parse_float(row.get(y_var))) for row in complete]
        outlier_count = 0
        if len(x_values) > 2 and len(y_values) > 2:
            x_sd = np.std(x_values, ddof=1) or 1
            y_sd = np.std(y_values, ddof=1) or 1
            zx = (np.array(x_values) - np.mean(x_values)) / x_sd
            zy = (np.array(y_values) - np.mean(y_values)) / y_sd
            outlier_count = int(np.sum((np.abs(zx) > 3) | (np.abs(zy) > 3)))
        tests = [
            {"Assumption": "Linearity", "How to read": "Scatter should look approximately straight rather than curved.", "Diagnostic": "Inspect scatterplot below.", "Flag": '<span class="badge badge-neutral">visual check</span>'},
            {"Assumption": "Outliers", "How to read": "Points with |z| > 3 on either variable should be inspected.", "Diagnostic": f"Potential univariate outlier pairs: {outlier_count}", "Flag": sm.interpretation_badge(outlier_count == 0, "no severe cases", "inspect cases")},
        ]
        blocks.append(details_block(
            f"H4 diagnostic pair: {label} · n={len(complete)}",
            sm.diagnostic_tests_table(tests)
            + sm.simple_scatter_svg("h4-subscale-" + sm.slugify(label), x_values, y_values, label, SCORE_LABELS.get(x_var, x_var), SCORE_LABELS.get(y_var, y_var))
            + sm.combined_exclusion_details("Show pairwise exclusions", excluded),
            open_=False,
        ))
    return "".join(blocks)


def h4_section(rows: list[dict[str, Any]], warnings: list[str]) -> str:
    log_step("Running H4 original and subscale correlations, including covariate-adjusted partial correlations.")
    original_pairs = [
        ("Original Engagement with original ICL", "engagement", "cl_intrinsic"),
        ("Original Engagement with original ECL", "engagement", "cl_extraneous"),
        ("Original Engagement with original GCL", "engagement", "cl_germane"),
    ]
    subscale_pairs = [
        ("Chapter Engagement with merged chapter ICL", "engagement_chapter_merged", "cl_intrinsic_chapter_merged"),
        ("Chapter Engagement with environment ECL", "engagement_chapter_merged", "cl_extraneous_environment_chapter_merged"),
        ("Game-overall Engagement with instruction ECL", "engagement_overall_game", "cl_extraneous_instruction_overall"),
        ("Game-overall Engagement with interaction ECL", "engagement_overall_game", "cl_extraneous_interaction_overall"),
        ("Game-overall Engagement with instruction+interaction ECL", "engagement_overall_game", "cl_extraneous_game_overall"),
        ("Game-overall Engagement with GCL", "engagement_overall_game", "cl_germane"),
    ]
    original_base = run_correlation_family(rows, original_pairs, include_covariates=False)
    original_cov = run_correlation_family(rows, original_pairs, include_covariates=True)
    subscale_base = run_correlation_family(rows, subscale_pairs, include_covariates=False)
    subscale_cov = run_correlation_family(rows, subscale_pairs, include_covariates=True)

    complete, base_excluded = sm.make_complete_cases(rows, ["engagement", "cl_intrinsic", "cl_extraneous", "cl_germane"], "H4 original complete-case pool")
    sub_complete, _ = sm.make_complete_cases(rows, ["engagement_chapter_merged", "engagement_overall_game", "cl_intrinsic_chapter_merged", "cl_extraneous_environment_chapter_merged", "cl_extraneous_instruction_overall", "cl_extraneous_interaction_overall", "cl_extraneous_game_overall", "cl_germane"], "H4 subscale complete-case pool")
    data_html = (
        sm.condition_count_table(complete, "Original H4 complete-case pool", ["engagement", "cl_intrinsic", "cl_extraneous", "cl_germane"])
        + sm.condition_count_table(sub_complete, "Subscale H4 complete-case pool", ["engagement_chapter_merged", "engagement_overall_game", "cl_extraneous_environment_chapter_merged", "cl_extraneous_game_overall"])
        + strict_score_completeness_html(rows)
        + sm.covariate_feasibility_table(rows)
    )
    diagnostics = (
        '<h4>Original H4 scatter diagnostics</h4>'
        + h4_scatter_diagnostics_for_pairs(rows, original_pairs)
        + '<h4>Source-aligned subscale scatter diagnostics</h4>'
        + h4_scatter_diagnostics_for_pairs(rows, subscale_pairs)
    )
    final_models_html = (
        '<p class="notice"><strong>Reading guide:</strong> base rows are ordinary Pearson correlations with Holm correction. Covariate-adjusted robustness rows are partial correlations calculated by residualising both variables on location, co-present participants, age, and gender before correlating the residuals.</p>'
        + correlation_table(original_base, "H4 original merged-construct correlations · base")
        + correlation_table(subscale_base, "H4 source-aligned subscale correlations · base")
        + h4_comparison_html(original_base, subscale_base, title="Simple H4 comparison: original vs subscale correlations · base")
        + correlation_table(original_cov, "H4 original merged-construct partial correlations · covariate-adjusted")
        + correlation_table(subscale_cov, "H4 source-aligned subscale partial correlations · covariate-adjusted")
        + h4_comparison_html(original_cov, subscale_cov, title="Simple H4 comparison: original vs subscale partial correlations · covariate-adjusted")
    )
    return sm.assumption_section_shell(
        "H4 engagement-load associations: original vs subscales",
        "The original H4 correlations are recalculated, then repeated with source-aligned Engagement and Cognitive Load subscales.",
        [
            "Original pairs: merged Engagement with original ICL, ECL, and GCL.",
            "Subscale pairs align measurement source: chapter Engagement with chapter ICL/environment ECL; game-overall Engagement with instruction ECL, interaction ECL, combined game-overall ECL, and GCL.",
            "Base results use Pearson correlations with Holm correction and Spearman sensitivity.",
            "Robustness results use covariate-adjusted partial correlations with the same covariates used elsewhere in the appendix.",
        ],
        [
            {"Assumption": "Independent paired observations", "How to test": "Each MCID contributes one pairwise score per correlation.", "How to read": "No participant should appear twice in the same pair."},
            {"Assumption": "Linearity", "How to test": "Inspect scatterplots.", "How to read": "Pearson/partial correlation is most interpretable when the cloud is approximately linear."},
            {"Assumption": "Outliers", "How to test": "Inspect scatterplots and z-score outlier counts.", "How to read": "Extreme points can dominate correlations."},
            {"Assumption": "Robustness to covariates", "How to test": "Compare base correlations to partial correlations.", "How to read": "A stable direction/support pattern is more robust."},
        ],
        data_html,
        diagnostics,
        final_models_html,
        sm.status_messages([], warnings, "H4 original and subscale correlation models were generated."),
        sm.combined_exclusion_details("Show H4 original complete-case exclusions", base_excluded),
    )


# -----------------------------------------------------------------------------
# HTML assembly
# -----------------------------------------------------------------------------


def intro_section() -> str:
    body = (
        '<p>This standalone appendix report is generated by <code>statistics_subscales.py</code>. It is intended to sit next to <code>statistics_manuscript.html</code>.</p>'
        '<p><strong>Scoring and descriptives:</strong> the full scoring, descriptive statistics, retention scoring diagnostics, and main internal-consistency table are reported in <code>statistics_manuscript.html</code>. This appendix does not duplicate those sections. It only repeats the internal-consistency audit needed to check the subscale split and then reruns the inferential models.</p>'
        '<p><strong>Strict inferential scoring:</strong> scale means in this appendix follow the same strict raw-item completeness policy as <code>statistics_manuscript.py</code>. A participant receives a score only when all raw items for that score are present and valid. This differs from the more permissive <code>sum_merged</code> display logic, but keeps the manuscript and appendix inferential n-values directly comparable.</p>'
        '<p><strong>Reverse-coding:</strong> game-overall instruction and interaction ECL items are reverse-coded as <code>10 - raw</code>. Game-overall frustration and confusion engagement items are reverse-coded as <code>8 - raw</code>.</p>'
        '<p><strong>ECL split:</strong> game-overall instruction-related and interaction-related extraneous load are analysed separately and also together as a combined game-overall ECL sensitivity score.</p>'
    )
    return html_section("Subscale inferential-statistics appendix", "Original merged-construct results are recalculated and compared with subscale/intermediate-score results.", body, badge="Intro")


def html_document(sections: list[str]) -> str:
    generated = dt.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    css = """
    :root { --bg:#f5f7fb; --card:#ffffff; --text:#111827; --muted:#667085; --line:#d0d5dd; --blue:#1d4ed8; --brown:#92400e; --green:#067647; --green-bg:#ecfdf3; --orange:#b54708; --orange-bg:#fff7ed; --red:#b42318; --red-bg:#fef3f2; --neutral:#475467; --neutral-bg:#f2f4f7; }
    * { box-sizing:border-box; }
    body { margin:0; background:var(--bg); color:var(--text); font-family:Arial, Helvetica, sans-serif; line-height:1.42; }
    .page-layout { display:grid; grid-template-columns:280px minmax(0,1fr); gap:18px; max-width:1540px; margin:0 auto; padding:18px; }
    .side-toc { position:sticky; top:18px; align-self:start; max-height:calc(100vh - 36px); overflow:auto; background:#fff; border:1px solid var(--line); border-radius:14px; padding:14px; }
    .side-toc h2 { font-size:18px; margin:0 0 8px; }
    .side-toc h3 { font-size:13px; color:var(--muted); text-transform:uppercase; letter-spacing:.04em; margin:14px 0 6px; }
    .side-toc ol, .side-toc ul { margin:0 0 0 18px; padding:0; }
    .side-toc li { margin:6px 0; font-size:13px; }
    .side-toc a { color:#1d4ed8; text-decoration:none; }
    .side-toc a:hover { text-decoration:underline; }
    main { min-width:0; }
    h1, h2, h3, h4 { margin:0 0 10px 0; }
    p { margin:0 0 10px 0; }
    .card { background:var(--card); border:1px solid var(--line); border-radius:14px; padding:16px; margin-bottom:16px; scroll-margin-top:18px; }
    .body-output h2, .appendix-output h2 { color:#fff; margin:-16px -16px 14px -16px; padding:12px 16px; border-radius:14px 14px 0 0; display:flex; justify-content:space-between; gap:12px; align-items:center; background:var(--blue); }
    h2 span { font-size:12px; font-weight:700; opacity:.9; }
    .small { color:var(--muted); font-size:12px; }
    .notice { background:#f8fafc; border:1px solid var(--line); border-left:4px solid var(--blue); border-radius:10px; padding:10px 12px; }
    .table-wrap { overflow:auto; margin:12px 0; }
    table { border-collapse:collapse; width:100%; background:#fff; font-size:13px; }
    th, td { border:1px solid var(--line); padding:8px 9px; vertical-align:top; text-align:left; }
    th { background:#eef2f7; }
    code { background:#f2f4f7; padding:1px 4px; border-radius:4px; }
    .model-table { font-size:12px; }
    .chart-box { border:1px solid var(--line); border-radius:12px; padding:12px; background:white; margin:14px 0; }
    .standalone-figure { width:100%; max-width:960px; display:block; }
    .status { border-radius:10px; padding:9px 11px; margin:9px 0; border:1px solid transparent; }
    .status-green { background:var(--green-bg); color:var(--green); border-color:#abefc6; }
    .status-orange { background:var(--orange-bg); color:var(--orange); border-color:#fed7aa; }
    .status-red { background:var(--red-bg); color:var(--red); border-color:#fecdca; }
    .badge { display:inline-block; border-radius:999px; padding:2px 8px; font-size:12px; font-weight:700; }
    .badge-good { background:var(--green-bg); color:var(--green); border:1px solid #abefc6; }
    .badge-warning { background:var(--orange-bg); color:var(--orange); border:1px solid #fed7aa; }
    .badge-neutral { background:var(--neutral-bg); color:var(--neutral); border:1px solid #d0d5dd; }
    .significant-row > th, .significant-row > td, .significant-cell { background:var(--green-bg); }
    details { margin:10px 0; }
    summary { cursor:pointer; font-weight:700; }
    .compact-details { border:1px solid var(--line); border-radius:12px; background:#fff; padding:0; }
    .compact-details summary { padding:9px 12px; }
    .compact-details[open] summary { border-bottom:1px solid var(--line); }
    .compact-details > :not(summary) { margin-left:12px; margin-right:12px; }
    @media (max-width: 980px) { .page-layout { display:block; padding:12px; } .side-toc { position:relative; top:0; max-height:none; margin-bottom:14px; } }
    """
    toc_items = [
        ("Subscale inferential-statistics appendix", "Intro"),
        ("Internal consistency audit for appendix subscales", "Alpha audit"),
        ("H1 original retention benchmark", "H1"),
        ("H2 cognitive-load mediation: original vs subscales", "H2"),
        ("H3 engagement mediation: original vs subscales", "H3"),
        ("H4 engagement-load associations: original vs subscales", "H4"),
    ]
    toc = (
        '<aside class="side-toc" aria-label="Table of contents"><h2>Table of contents</h2>'
        f'<p class="small">Generated {h(generated)} from <code>{h(ANALYSIS_DIR)}</code>.</p>'
        '<ol>'
        + ''.join(f'<li><a href="#{h(sm.slugify(title))}">{h(label)}</a></li>' for title, label in toc_items)
        + '</ol></aside>'
    )
    return (
        '<!doctype html><html lang="en"><head><meta charset="utf-8">'
        '<meta name="viewport" content="width=device-width, initial-scale=1">'
        '<title>Statistics subscales appendix</title>'
        f'<style>{css}</style></head><body><div class="page-layout">'
        + toc + '<main>' + ''.join(sections) + '</main></div></body></html>'
    )


def main() -> int:
    log_step("Starting appendix generation.")
    sm.OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    log_step("Building original manuscript rows and strict subscale rows.")
    rows, warnings = build_rows_for_subscale_models()
    log_step(f"Built {len(rows)} participant-level inferential row(s).")

    log_step("Calculating Cronbach's alphas for original constructs and subscales.")
    alphas = alpha_rows()

    sections = [
        intro_section(),
        alpha_audit_html(alphas),
        h1_section(rows, warnings),
        h2_section(rows, warnings),
        h3_section(rows, warnings),
        h4_section(rows, warnings),
    ]

    log_step(f"Writing {OUTPUT_PATH}.")
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(html_document(sections), encoding="utf-8")
    log_step("Done.")
    print(f"Wrote {OUTPUT_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
