from __future__ import annotations

# -----------------------------------------------------------------------------
# stats_explore.py
# -----------------------------------------------------------------------------
# Exploratory construct-scoring sensitivity report for the manuscript statistics.
#
# Goal:
#   Show ONLY the values that appear in the conceptual/results figure for H1-H4,
#   rerun under three construct-scoring routes:
#     1. preregistered: full = per-chapter + game-overall where both exist;
#     2. per-chapter combined only;
#     3. game-overall only.
#
# Retention scores are NOT varied here. The script reuses the base retention
# scores and model helpers from statistics_manuscript.py.
# -----------------------------------------------------------------------------

import datetime as dt
import math
import statistics
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import numpy as np

THIS_FILE = Path(__file__).resolve()
if THIS_FILE.parent.name == "apps":
    ANALYSIS_DIR = THIS_FILE.parents[1]
else:
    ANALYSIS_DIR = THIS_FILE.parent

if str(ANALYSIS_DIR) not in sys.path:
    sys.path.insert(0, str(ANALYSIS_DIR))

from apps import statistics_manuscript as sm  # noqa: E402

OUTPUT_PATH = sm.OUTPUT_DIR / "stats_explore.html"
BOOTSTRAP_ITERATIONS = 1000
BOOTSTRAP_SEED = 20260622

CONTRAST_ORDER = ["required_pause_contrast", "optional_pause_contrast"]
CONTRAST_TO_LABEL = {
    "required_pause_contrast": "Required pauses − required continue",
    "optional_pause_contrast": "Optional pauses − average system-controlled",
}
CONTRAST_TO_MODEL_LABEL_IMMEDIATE = {
    "required_pause_contrast": "[1]",
    "optional_pause_contrast": "[2]",
}
CONTRAST_TO_MODEL_LABEL_DELAYED = {
    "required_pause_contrast": "[3]",
    "optional_pause_contrast": "[4]",
}
CONTRAST_TO_A_LABEL = {
    "required_pause_contrast": "[1] / [3]",
    "optional_pause_contrast": "[2] / [4]",
}

MEDIATOR_LABELS = {
    "cl_intrinsic": "ICL",
    "cl_extraneous": "ECL",
    "cl_germane": "GCL",
    "engagement": "Engagement",
}

CL_MEDIATOR_ORDER = ["cl_intrinsic", "cl_extraneous", "cl_germane"]


@dataclass(frozen=True)
class ConstructVariant:
    key: str
    title: str
    short_title: str
    note: str


VARIANTS = [
    ConstructVariant(
        key="preregistered",
        title="Preregistered scoring route",
        short_title="Preregistered",
        note="Full construct scores are used: per-chapter and game-overall parts are combined where both were administered.",
    ),
    ConstructVariant(
        key="chapter_only",
        title="Per-chapter-only scoring route",
        short_title="Per-chapter only",
        note="Only chapter-specific items are used. Constructs without chapter-specific items are unavailable in this route.",
    ),
    ConstructVariant(
        key="overall_only",
        title="Game-overall-only scoring route",
        short_title="Game overall only",
        note="Only game-overall items are used. Constructs without game-overall items are unavailable in this route.",
    ),
]


@dataclass
class FigureLine:
    text: str
    significant: bool = False
    unavailable: bool = False


@dataclass
class Effect:
    estimate: float | None
    p: float | None
    low: float | None = None
    high: float | None = None
    se: float | None = None
    stat: float | None = None
    n: int | None = None

    @property
    def significant_by_p(self) -> bool:
        return self.p is not None and math.isfinite(self.p) and self.p < sm.ALPHA

    @property
    def significant_by_ci(self) -> bool:
        return (
            self.low is not None
            and self.high is not None
            and math.isfinite(self.low)
            and math.isfinite(self.high)
            and (self.low > 0 or self.high < 0)
        )


# -----------------------------------------------------------------------------
# Formatting helpers: compact figure-like text.
# -----------------------------------------------------------------------------


def fmt_effect(value: float | None) -> str:
    if value is None or not math.isfinite(value):
        return "—"
    if abs(value) < 0.005:
        value = 0.0
    return f"{value:.2f}"


def fmt_r(value: float | None) -> str:
    return fmt_effect(value)


def fmt_ci_bound(value: float | None) -> str:
    if value is None or not math.isfinite(value):
        return "—"
    if 0 < abs(value) < 0.01:
        return f"{value:.3f}"
    if abs(value) < 0.005:
        value = 0.0
    return f"{value:.2f}"


def fmt_ci(low: float | None, high: float | None) -> str:
    if low is None or high is None:
        return "[—, —]"
    return f"[{fmt_ci_bound(low)}, {fmt_ci_bound(high)}]"


def fmt_p(value: float | None) -> str:
    return sm.p_text(value)


def fmt_p_holm(value: float | None) -> str:
    if value is not None and math.isfinite(value) and value >= 0.9995:
        return "> .999"
    return sm.p_text(value)


def h(value: object) -> str:
    return sm.h(value)


# -----------------------------------------------------------------------------
# Construct-score recalculation routes.
# -----------------------------------------------------------------------------


def _valid_scaled_value(row: dict[str, str], column: str, minimum: float, maximum: float, *, reverse: bool = False) -> float | None:
    value = sm.parse_float(row.get(column))
    if value is None or value < minimum or value > maximum:
        return None
    if reverse:
        return minimum + maximum - value
    return value


def _complete_values(row: dict[str, str], specs: list[tuple[str, float, float, bool]]) -> list[float] | None:
    values: list[float] = []
    for column, minimum, maximum, reverse in specs:
        value = _valid_scaled_value(row, column, minimum, maximum, reverse=reverse)
        if value is None:
            return None
        values.append(value)
    return values


def _mean_or_none(values: list[float] | None) -> float | None:
    if not values:
        return None
    return statistics.fmean(values)


def _mean_components(components: list[float | None]) -> float | None:
    if any(value is None for value in components):
        return None
    return statistics.fmean([float(value) for value in components if value is not None])


def _cl_chapter_intrinsic_mean(row: dict[str, str]) -> float | None:
    chapter_means: list[float | None] = []
    for chapter in (1, 2, 3):
        specs = [(f"cl_ch{chapter}_scores_{index}", 0.0, 10.0, False) for index in (1, 2, 3)]
        chapter_means.append(_mean_or_none(_complete_values(row, specs)))
    return _mean_components(chapter_means)


def _cl_chapter_environment_extraneous_mean(row: dict[str, str]) -> float | None:
    chapter_means: list[float | None] = []
    for chapter in (1, 2, 3):
        specs = [(f"cl_ch{chapter}_scores_{index}", 0.0, 10.0, False) for index in (4, 5, 6, 7)]
        chapter_means.append(_mean_or_none(_complete_values(row, specs)))
    return _mean_components(chapter_means)


def _cl_overall_instruction_extraneous_mean(row: dict[str, str]) -> float | None:
    specs = [(f"cl_overall_scores_{index}", 0.0, 10.0, True) for index in (1, 2, 3)]
    return _mean_or_none(_complete_values(row, specs))


def _cl_overall_interaction_extraneous_mean(row: dict[str, str]) -> float | None:
    specs = [(f"cl_overall_scores_{index}", 0.0, 10.0, True) for index in (4, 5, 6, 7)]
    return _mean_or_none(_complete_values(row, specs))


def _cl_overall_germane_mean(row: dict[str, str]) -> float | None:
    specs = [(f"cl_overall_scores_{index}", 0.0, 10.0, False) for index in (8, 9, 10, 11)]
    return _mean_or_none(_complete_values(row, specs))


def _engagement_chapter_mean(row: dict[str, str]) -> float | None:
    chapter_means: list[float | None] = []
    for chapter in (1, 2, 3):
        specs = [(f"eng_ch{chapter}_scores_{index}", 1.0, 7.0, False) for index in (1, 2, 3, 4, 5)]
        chapter_means.append(_mean_or_none(_complete_values(row, specs)))
    return _mean_components(chapter_means)


def _engagement_overall_mean(row: dict[str, str]) -> float | None:
    specs = [
        ("eng_overall_scores_1", 1.0, 7.0, True),
        ("eng_overall_scores_2", 1.0, 7.0, True),
        ("eng_overall_scores_3", 1.0, 7.0, False),
        ("eng_overall_scores_4", 1.0, 7.0, False),
    ]
    return _mean_or_none(_complete_values(row, specs))


def build_construct_variant_rows(variant_key: str) -> tuple[list[dict[str, Any]], list[str]]:
    """Build participant rows with the requested construct-scoring route.

    Retention scores, condition coding, and covariates come from the existing
    manuscript-statistics row builder. Only CL/Engagement construct scores are
    overwritten here.
    """
    base_rows, warnings = sm.build_rows_for_inferential_models()
    base_by_mcid = {sm.clean(row.get("MCID")): dict(row) for row in base_rows if sm.clean(row.get("MCID"))}

    survey_rows = sm.read_tsv(sm.SURVEY_EXPORT_PATH)
    immediate_by_mcid: dict[str, dict[str, str]] = {}
    for row in survey_rows:
        participant_id = sm.mcid_from_row(row)
        if participant_id and not sm.delayed_flag(row):
            immediate_by_mcid.setdefault(participant_id, row)

    rows: list[dict[str, Any]] = []
    for participant_id, base_row in sorted(base_by_mcid.items()):
        survey_row = immediate_by_mcid.get(participant_id)
        if survey_row is None:
            continue

        intrinsic_chapter = _cl_chapter_intrinsic_mean(survey_row)
        extraneous_chapter = _cl_chapter_environment_extraneous_mean(survey_row)
        extraneous_instruction = _cl_overall_instruction_extraneous_mean(survey_row)
        extraneous_interaction = _cl_overall_interaction_extraneous_mean(survey_row)
        germane_overall = _cl_overall_germane_mean(survey_row)
        engagement_chapter = _engagement_chapter_mean(survey_row)
        engagement_overall = _engagement_overall_mean(survey_row)

        row = dict(base_row)
        row["cl_intrinsic"] = None
        row["cl_extraneous"] = None
        row["cl_germane"] = None
        row["engagement"] = None

        if variant_key == "preregistered":
            row["cl_intrinsic"] = intrinsic_chapter
            row["cl_extraneous"] = _mean_components([extraneous_chapter, extraneous_instruction, extraneous_interaction])
            row["cl_germane"] = germane_overall
            row["engagement"] = _mean_components([engagement_chapter, engagement_overall])
        elif variant_key == "chapter_only":
            row["cl_intrinsic"] = intrinsic_chapter
            row["cl_extraneous"] = extraneous_chapter
            row["cl_germane"] = None
            row["engagement"] = engagement_chapter
        elif variant_key == "overall_only":
            row["cl_intrinsic"] = None
            row["cl_extraneous"] = _mean_components([extraneous_instruction, extraneous_interaction])
            row["cl_germane"] = germane_overall
            row["engagement"] = engagement_overall
        else:
            raise ValueError(f"Unknown construct variant: {variant_key}")

        rows.append(row)

    return rows, warnings


def available_cl_mediators(rows: list[dict[str, Any]]) -> list[str]:
    available: list[str] = []
    for mediator in CL_MEDIATOR_ORDER:
        if any(sm.parse_float(row.get(mediator)) is not None for row in rows):
            available.append(mediator)
    return available


# -----------------------------------------------------------------------------
# Statistical helpers returning only figure-level values.
# -----------------------------------------------------------------------------


def _effect_from_model(model: dict[str, Any], contrast: str) -> Effect:
    if not model.get("ok"):
        return Effect(None, None)
    raw = sm.scaled_effect_from_ols(model, contrast, sm.CONTRAST_MULTIPLIERS[contrast])
    if raw is None:
        return Effect(None, None)
    return Effect(
        estimate=raw.get("estimate"),
        p=raw.get("p"),
        low=raw.get("low"),
        high=raw.get("high"),
        se=raw.get("se"),
        stat=raw.get("stat"),
        n=model.get("n"),
    )


def h1_total_effect_lines(rows: list[dict[str, Any]], *, include_covariates: bool) -> list[FigureLine]:
    lines: list[FigureLine] = []
    for outcome, label_map in [
        ("ret_immediate_score", CONTRAST_TO_MODEL_LABEL_IMMEDIATE),
        ("ret_delayed_score", CONTRAST_TO_MODEL_LABEL_DELAYED),
    ]:
        retention_form = sm.categorical_if_available(rows, "retention_form_order", outcome)
        cov_num = ["co_present_participants", "age"] if include_covariates else []
        cov_cat = ["location", "gender"] if include_covariates else []
        model = sm.fit_ols_arrays(rows, outcome, [*CONTRAST_ORDER, *cov_num], [*retention_form, *cov_cat])
        for contrast in CONTRAST_ORDER:
            effect = _effect_from_model(model, contrast)
            model_label = label_map[contrast]
            lines.append(FigureLine(
                f"{model_label} b = {fmt_effect(effect.estimate)}, 95% CI {fmt_ci(effect.low, effect.high)}, p = {fmt_p(effect.p)}",
                significant=effect.significant_by_p,
            ))
    return lines


def h2a_a_path_lines(rows: list[dict[str, Any]], mediators: list[str], *, include_covariates: bool) -> list[FigureLine]:
    if not mediators:
        return [FigureLine("No cognitive-load mediators are available for this scoring route.", unavailable=True)]

    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    raw_rows: list[dict[str, Any]] = []
    for mediator in mediators:
        model = sm.fit_ols_arrays(rows, mediator, [*CONTRAST_ORDER, *cov_num], cov_cat)
        for contrast in CONTRAST_ORDER:
            effect = _effect_from_model(model, contrast)
            raw_rows.append({"mediator": mediator, "contrast": contrast, "effect": effect})

    adjusted = sm.holm_adjust([row["effect"].p for row in raw_rows])
    lines: list[FigureLine] = []
    for row, p_holm in zip(raw_rows, adjusted):
        mediator_label = MEDIATOR_LABELS[row["mediator"]]
        contrast_label = CONTRAST_TO_A_LABEL[row["contrast"]]
        effect: Effect = row["effect"]
        lines.append(FigureLine(
            f"{mediator_label} — {contrast_label} b = {fmt_effect(effect.estimate)}, pHolm = {fmt_p_holm(p_holm)}",
            significant=p_holm is not None and math.isfinite(p_holm) and p_holm < sm.ALPHA,
        ))
    return lines


def _fit_mediation_components(
    rows: list[dict[str, Any]],
    mediators: list[str],
    outcome: str,
    *,
    include_covariates: bool,
    iterations: int = BOOTSTRAP_ITERATIONS,
    seed: int = BOOTSTRAP_SEED,
) -> dict[str, Any]:
    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    needed = [*CONTRAST_ORDER, *mediators, outcome, *cov_num, *cov_cat]
    complete, excluded = sm.make_complete_cases(rows, needed, f"exploratory mediation {outcome}")
    if len(complete) < 10 or not mediators:
        return {"ok": False, "complete": complete, "excluded": excluded, "reason": "Too few complete cases or no mediators."}

    a_models = {mediator: sm.fit_ols_arrays(complete, mediator, [*CONTRAST_ORDER, *cov_num], cov_cat) for mediator in mediators}
    y_model = sm.fit_ols_arrays(complete, outcome, [*CONTRAST_ORDER, *mediators, *cov_num], cov_cat)
    total_model = sm.fit_ols_arrays(complete, outcome, [*CONTRAST_ORDER, *cov_num], cov_cat)
    if not y_model.get("ok") or not total_model.get("ok") or any(not model.get("ok") for model in a_models.values()):
        return {"ok": False, "complete": complete, "excluded": excluded, "reason": "One or more component regressions could not be fitted."}

    indirect_point: dict[tuple[str, str], float] = {}
    for contrast in CONTRAST_ORDER:
        for mediator in mediators:
            a = a_models[mediator]["beta"][a_models[mediator]["names"].index(contrast)]
            b = y_model["beta"][y_model["names"].index(mediator)]
            indirect_point[(contrast, mediator)] = float(sm.CONTRAST_MULTIPLIERS[contrast] * a * b)

    boot_values: dict[tuple[str, str], list[float]] = {key: [] for key in indirect_point}
    for indices in sm.bootstrap_indices(len(complete), iterations, seed):
        sample = [complete[int(index)] for index in indices]
        try:
            sample_a_models = {mediator: sm.fit_ols_arrays(sample, mediator, [*CONTRAST_ORDER, *cov_num], cov_cat) for mediator in mediators}
            sample_y_model = sm.fit_ols_arrays(sample, outcome, [*CONTRAST_ORDER, *mediators, *cov_num], cov_cat)
            if not sample_y_model.get("ok") or any(not model.get("ok") for model in sample_a_models.values()):
                continue
            for contrast in CONTRAST_ORDER:
                for mediator in mediators:
                    a = sample_a_models[mediator]["beta"][sample_a_models[mediator]["names"].index(contrast)]
                    b = sample_y_model["beta"][sample_y_model["names"].index(mediator)]
                    boot_values[(contrast, mediator)].append(float(sm.CONTRAST_MULTIPLIERS[contrast] * a * b))
        except Exception:
            continue

    indirect_effects: dict[tuple[str, str], Effect] = {}
    for key, point in indirect_point.items():
        values = boot_values.get(key, [])
        low = high = None
        if len(values) >= 50:
            low, high = np.percentile(values, [2.5, 97.5]).tolist()
        indirect_effects[key] = Effect(estimate=point, p=None, low=low, high=high, n=len(complete))

    direct_effects = {contrast: _effect_from_model(y_model, contrast) for contrast in CONTRAST_ORDER}
    total_effects = {contrast: _effect_from_model(total_model, contrast) for contrast in CONTRAST_ORDER}

    b_paths: dict[str, Effect] = {}
    for mediator in mediators:
        if mediator not in y_model.get("names", []):
            b_paths[mediator] = Effect(None, None)
            continue
        idx = y_model["names"].index(mediator)
        coeff = y_model["coeff_rows"][idx]
        b_paths[mediator] = Effect(
            estimate=coeff.get("b"),
            p=coeff.get("p"),
            low=coeff.get("CI_low"),
            high=coeff.get("CI_high"),
            se=coeff.get("SE_HC3"),
            stat=coeff.get("t"),
            n=len(complete),
        )

    return {
        "ok": True,
        "complete": complete,
        "excluded": excluded,
        "a_models": a_models,
        "y_model": y_model,
        "total_model": total_model,
        "direct_effects": direct_effects,
        "total_effects": total_effects,
        "indirect_effects": indirect_effects,
        "b_paths": b_paths,
    }


def h2_indirect_lines(rows: list[dict[str, Any]], mediators: list[str], *, include_covariates: bool) -> list[FigureLine]:
    if not mediators:
        return [FigureLine("No cognitive-load mediators are available for this scoring route.", unavailable=True)]
    immediate = _fit_mediation_components(rows, mediators, "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, mediators, "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H2 mediation could not be fitted for this scoring route.", unavailable=True)]

    lines: list[FigureLine] = []
    for mediator in mediators:
        for result, label_map in [(immediate, CONTRAST_TO_MODEL_LABEL_IMMEDIATE), (delayed, CONTRAST_TO_MODEL_LABEL_DELAYED)]:
            for contrast in CONTRAST_ORDER:
                effect: Effect = result["indirect_effects"][(contrast, mediator)]
                lines.append(FigureLine(
                    f"{MEDIATOR_LABELS[mediator]} — {label_map[contrast]} b = {fmt_effect(effect.estimate)}, 95% CI {fmt_ci(effect.low, effect.high)}",
                    significant=effect.significant_by_ci,
                ))
    return lines


def h2_b_path_lines(rows: list[dict[str, Any]], mediators: list[str], *, include_covariates: bool) -> list[FigureLine]:
    if not mediators:
        return [FigureLine("No cognitive-load mediators are available for this scoring route.", unavailable=True)]
    immediate = _fit_mediation_components(rows, mediators, "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, mediators, "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H2 b-path models could not be fitted for this scoring route.", unavailable=True)]

    lines: list[FigureLine] = []
    for mediator in mediators:
        imm: Effect = immediate["b_paths"][mediator]
        deleff: Effect = delayed["b_paths"][mediator]
        lines.append(FigureLine(
            f"{MEDIATOR_LABELS[mediator]} — [1] / [2] b = {fmt_effect(imm.estimate)}, p = {fmt_p(imm.p)}",
            significant=imm.significant_by_p,
        ))
        lines.append(FigureLine(
            f"{MEDIATOR_LABELS[mediator]} — [3] / [4] b = {fmt_effect(deleff.estimate)}, p = {fmt_p(deleff.p)}",
            significant=deleff.significant_by_p,
        ))
    return lines


def h2_direct_lines(rows: list[dict[str, Any]], mediators: list[str], *, include_covariates: bool) -> list[FigureLine]:
    if not mediators:
        return [FigureLine("No cognitive-load mediators are available for this scoring route.", unavailable=True)]
    immediate = _fit_mediation_components(rows, mediators, "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, mediators, "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H2 direct-effect models could not be fitted for this scoring route.", unavailable=True)]

    lines: list[FigureLine] = []
    for result, label_map in [(immediate, CONTRAST_TO_MODEL_LABEL_IMMEDIATE), (delayed, CONTRAST_TO_MODEL_LABEL_DELAYED)]:
        for contrast in CONTRAST_ORDER:
            effect: Effect = result["direct_effects"][contrast]
            lines.append(FigureLine(
                f"{label_map[contrast]} b = {fmt_effect(effect.estimate)}, p = {fmt_p(effect.p)}",
                significant=effect.significant_by_p,
            ))
    return lines


def h3a_a_path_lines(rows: list[dict[str, Any]], *, include_covariates: bool) -> list[FigureLine]:
    cov_num = ["co_present_participants", "age"] if include_covariates else []
    cov_cat = ["location", "gender"] if include_covariates else []
    model = sm.fit_ols_arrays(rows, "engagement", [*CONTRAST_ORDER, *cov_num], cov_cat)
    if not model.get("ok"):
        return [FigureLine("H3a could not be fitted for this scoring route.", unavailable=True)]
    lines: list[FigureLine] = []
    for contrast in CONTRAST_ORDER:
        effect = _effect_from_model(model, contrast)
        lines.append(FigureLine(
            f"{CONTRAST_TO_A_LABEL[contrast]} b = {fmt_effect(effect.estimate)}, p = {fmt_p(effect.p)}",
            significant=effect.significant_by_p,
        ))
    return lines


def h3_indirect_lines(rows: list[dict[str, Any]], *, include_covariates: bool) -> list[FigureLine]:
    immediate = _fit_mediation_components(rows, ["engagement"], "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, ["engagement"], "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H3 mediation could not be fitted for this scoring route.", unavailable=True)]
    lines: list[FigureLine] = []
    for result, label_map in [(immediate, CONTRAST_TO_MODEL_LABEL_IMMEDIATE), (delayed, CONTRAST_TO_MODEL_LABEL_DELAYED)]:
        for contrast in CONTRAST_ORDER:
            effect: Effect = result["indirect_effects"][(contrast, "engagement")]
            lines.append(FigureLine(
                f"{label_map[contrast]} b = {fmt_effect(effect.estimate)}, 95% CI {fmt_ci(effect.low, effect.high)}",
                significant=effect.significant_by_ci,
            ))
    return lines


def h3_b_path_lines(rows: list[dict[str, Any]], *, include_covariates: bool) -> list[FigureLine]:
    immediate = _fit_mediation_components(rows, ["engagement"], "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, ["engagement"], "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H3 b-path models could not be fitted for this scoring route.", unavailable=True)]
    imm: Effect = immediate["b_paths"]["engagement"]
    deleff: Effect = delayed["b_paths"]["engagement"]
    return [
        FigureLine(f"[1] / [2] b = {fmt_effect(imm.estimate)}, p = {fmt_p(imm.p)}", significant=imm.significant_by_p),
        FigureLine(f"[3] / [4] b = {fmt_effect(deleff.estimate)}, p = {fmt_p(deleff.p)}", significant=deleff.significant_by_p),
    ]


def h3_direct_lines(rows: list[dict[str, Any]], *, include_covariates: bool) -> list[FigureLine]:
    immediate = _fit_mediation_components(rows, ["engagement"], "ret_immediate_score", include_covariates=include_covariates)
    delayed = _fit_mediation_components(rows, ["engagement"], "ret_delayed_score", include_covariates=include_covariates)
    if not immediate.get("ok") or not delayed.get("ok"):
        return [FigureLine("H3 direct-effect models could not be fitted for this scoring route.", unavailable=True)]
    lines: list[FigureLine] = []
    for result, label_map in [(immediate, CONTRAST_TO_MODEL_LABEL_IMMEDIATE), (delayed, CONTRAST_TO_MODEL_LABEL_DELAYED)]:
        for contrast in CONTRAST_ORDER:
            effect: Effect = result["direct_effects"][contrast]
            lines.append(FigureLine(
                f"{label_map[contrast]} b = {fmt_effect(effect.estimate)}, p = {fmt_p(effect.p)}",
                significant=effect.significant_by_p,
            ))
    return lines


def _correlation_ci(r: float, n: int, covariate_parameter_count: int = 0) -> tuple[float | None, float | None]:
    denominator = n - covariate_parameter_count - 3
    if denominator <= 0 or abs(r) >= 1:
        return None, None
    z = math.atanh(max(-0.999999, min(0.999999, r)))
    se = 1 / math.sqrt(denominator)
    return math.tanh(z - 1.96 * se), math.tanh(z + 1.96 * se)


def _residualize(rows: list[dict[str, Any]], value_column: str, cov_num: list[str], cov_cat: list[str]) -> tuple[np.ndarray | None, int]:
    x_matrix, _names, _notes = sm.design_matrix_for_assumptions(rows, cov_num, cov_cat)
    if x_matrix is None:
        return None, 0
    y_values = np.array([float(sm.parse_float(row.get(value_column))) for row in rows], dtype=float)
    beta = np.linalg.pinv(x_matrix.T @ x_matrix) @ x_matrix.T @ y_values
    return y_values - x_matrix @ beta, max(0, x_matrix.shape[1] - 1)


def h4_correlation_lines(rows: list[dict[str, Any]], mediators: list[str], *, include_covariates: bool) -> list[FigureLine]:
    if not mediators:
        return [FigureLine("No cognitive-load variables are available for H4 in this scoring route.", unavailable=True)]
    raw_rows: list[dict[str, Any]] = []
    for mediator in mediators:
        if include_covariates:
            cov_num = ["co_present_participants", "age"]
            cov_cat = ["location", "gender"]
            complete, _excluded = sm.make_complete_cases(rows, ["engagement", mediator, *cov_num, *cov_cat], f"partial correlation engagement with {mediator}")
        else:
            cov_num = []
            cov_cat = []
            complete, _excluded = sm.make_complete_cases(rows, ["engagement", mediator], f"correlation engagement with {mediator}")

        if len(complete) < 4 or sm.scipy_stats is None:
            raw_rows.append({"mediator": mediator, "effect": Effect(None, None, n=len(complete)), "available": False})
            continue

        if include_covariates:
            x_resid, cov_count_x = _residualize(complete, "engagement", cov_num, cov_cat)
            y_resid, cov_count_y = _residualize(complete, mediator, cov_num, cov_cat)
            cov_count = max(cov_count_x, cov_count_y)
            if x_resid is None or y_resid is None:
                raw_rows.append({"mediator": mediator, "effect": Effect(None, None, n=len(complete)), "available": False})
                continue
            r = float(np.corrcoef(x_resid, y_resid)[0, 1])
            df = len(complete) - cov_count - 2
            if df <= 0 or abs(r) >= 1:
                p_value = None
            else:
                t_value = r * math.sqrt(df / max(1e-12, 1 - r * r))
                p_value = float(2 * sm.scipy_stats.t.sf(abs(t_value), df))
            low, high = _correlation_ci(r, len(complete), cov_count)
        else:
            x_values = [float(sm.parse_float(row.get("engagement"))) for row in complete]
            y_values = [float(sm.parse_float(row.get(mediator))) for row in complete]
            r, p_value = sm.scipy_stats.pearsonr(x_values, y_values)
            low, high = _correlation_ci(float(r), len(complete), 0)

        raw_rows.append({"mediator": mediator, "effect": Effect(float(r), float(p_value) if p_value is not None else None, low, high, n=len(complete)), "available": True})

    adjusted = sm.holm_adjust([row["effect"].p for row in raw_rows])
    lines: list[FigureLine] = []
    for row, p_holm in zip(raw_rows, adjusted):
        mediator_label = MEDIATOR_LABELS[row["mediator"]]
        effect: Effect = row["effect"]
        if not row.get("available"):
            lines.append(FigureLine(f"{mediator_label} — not available", unavailable=True))
            continue
        lines.append(FigureLine(
            f"{mediator_label} — r = {fmt_r(effect.estimate)}, 95% CI {fmt_ci(effect.low, effect.high)}, pHolm = {fmt_p_holm(p_holm)}",
            significant=p_holm is not None and math.isfinite(p_holm) and p_holm < sm.ALPHA,
        ))
    return lines


# -----------------------------------------------------------------------------
# HTML rendering.
# -----------------------------------------------------------------------------


def render_lines(title: str, lines: list[FigureLine]) -> str:
    rendered: list[str] = []
    for line in lines:
        classes = ["figure-line"]
        if line.significant:
            classes.append("sig")
        if line.unavailable:
            classes.append("na")
        badge = '<span class="badge-sig">significant</span>' if line.significant else '<span class="badge-muted">ns</span>'
        if line.unavailable:
            badge = '<span class="badge-na">n/a</span>'
        rendered.append(f'<div class="{" ".join(classes)}"><span class="line-text">{h(line.text)}</span>{badge}</div>')
    return f'<div class="line-block"><h4>{h(title)}</h4>' + "".join(rendered) + "</div>"


def render_model_mode(variant: ConstructVariant, rows: list[dict[str, Any]], *, include_covariates: bool) -> str:
    mode_class = "model-covariate" if include_covariates else "model-base"
    mode_title = "Covariate-adjusted models" if include_covariates else "Base models"
    mode_note = (
        "Covariate-adjusted sensitivity lines include location, co-present participants, age, and gender where applicable; H4 is shown as a covariate-adjusted partial correlation."
        if include_covariates
        else "Base lines are the no-covariate models and should reproduce the figure for the preregistered scoring route."
    )
    cl_mediators = available_cl_mediators(rows)

    blocks = [
        f'<div class="model-view {mode_class}">',
        f'<h3>{h(mode_title)}</h3>',
        f'<p class="small">{h(mode_note)}</p>',
        '<div class="hyp-grid">',
        '<section class="hyp-card"><h3>H1 &amp; H4</h3>',
        render_lines("total effect", h1_total_effect_lines(rows, include_covariates=include_covariates)),
        render_lines("correlations with Engagement", h4_correlation_lines(rows, cl_mediators, include_covariates=include_covariates)),
        '</section>',
        '<section class="hyp-card"><h3>H2</h3>',
        render_lines("a-paths", h2a_a_path_lines(rows, cl_mediators, include_covariates=include_covariates)),
        render_lines("indirect effect", h2_indirect_lines(rows, cl_mediators, include_covariates=include_covariates)),
        render_lines("b-paths", h2_b_path_lines(rows, cl_mediators, include_covariates=include_covariates)),
        render_lines("direct effect", h2_direct_lines(rows, cl_mediators, include_covariates=include_covariates)),
        '</section>',
        '<section class="hyp-card"><h3>H3</h3>',
        render_lines("a-path", h3a_a_path_lines(rows, include_covariates=include_covariates)),
        render_lines("indirect effect", h3_indirect_lines(rows, include_covariates=include_covariates)),
        render_lines("b-path", h3_b_path_lines(rows, include_covariates=include_covariates)),
        render_lines("direct effect", h3_direct_lines(rows, include_covariates=include_covariates)),
        '</section>',
        '</div>',
        '</div>',
    ]
    return "".join(blocks)


def render_variant_section(variant: ConstructVariant) -> str:
    rows, warnings = build_construct_variant_rows(variant.key)
    warnings_html = "".join(f'<li>{h(message)}</li>' for message in warnings)
    if warnings_html:
        warnings_html = f'<details class="compact"><summary>Build warnings</summary><ul>{warnings_html}</ul></details>'
    available = ", ".join(MEDIATOR_LABELS[name] for name in available_cl_mediators(rows)) or "none"
    n_rows = len(rows)
    return (
        f'<section class="route-card" id="route-{h(variant.key)}">'
        f'<h2>{h(variant.title)}</h2>'
        f'<p>{h(variant.note)}</p>'
        f'<p class="small">Participant rows built: {n_rows}. Available cognitive-load variables for H2/H4 in this route: {h(available)}.</p>'
        f'{warnings_html}'
        f'{render_model_mode(variant, rows, include_covariates=False)}'
        f'{render_model_mode(variant, rows, include_covariates=True)}'
        '</section>'
    )


def html_document() -> str:
    generated = dt.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    variant_nav = "".join(f'<li><a href="#route-{h(variant.key)}">{h(variant.short_title)}</a></li>' for variant in VARIANTS)
    sections = "".join(render_variant_section(variant) for variant in VARIANTS)
    css = """
    :root { --bg:#f6f7f8; --card:#fff; --text:#172026; --muted:#667085; --line:#d0d5dd; --blue:#1d4ed8; --green-bg:#ecfdf3; --green:#027a48; --orange-bg:#fff7ed; --orange:#b54708; --na-bg:#f2f4f7; --na:#475467; }
    * { box-sizing:border-box; }
    body { margin:0; background:var(--bg); color:var(--text); font-family:Arial, Helvetica, sans-serif; line-height:1.45; }
    .page { max-width:1420px; margin:0 auto; padding:18px; }
    .top-card, .route-card { background:var(--card); border:1px solid var(--line); border-radius:14px; padding:16px; margin-bottom:16px; }
    h1, h2, h3, h4, p { margin-top:0; }
    h1 { margin-bottom:6px; }
    h2 { border-bottom:1px solid var(--line); padding-bottom:8px; }
    h3 { margin-top:12px; }
    h4 { margin:10px 0 6px; color:#344054; }
    .small { color:var(--muted); font-size:12px; }
    .route-nav { display:flex; gap:14px; flex-wrap:wrap; margin:10px 0 0; padding-left:18px; }
    .toggle-box { position:sticky; top:0; z-index:2; background:#ffffffee; backdrop-filter:blur(4px); border:1px solid var(--line); border-radius:12px; padding:10px 12px; margin:14px 0; display:flex; gap:16px; align-items:center; flex-wrap:wrap; }
    .toggle-box label { cursor:pointer; font-weight:700; }
    .model-covariate { display:none; }
    body.show-covariate .model-base { display:none; }
    body.show-covariate .model-covariate { display:block; }
    .hyp-grid { display:grid; grid-template-columns:repeat(3, minmax(0, 1fr)); gap:12px; align-items:start; }
    .hyp-card { border:1px solid var(--line); border-radius:12px; padding:12px; background:#fff; }
    .line-block { border-top:1px solid var(--line); margin-top:10px; padding-top:8px; }
    .figure-line { display:grid; grid-template-columns:minmax(0, 1fr) auto; gap:10px; align-items:center; border:1px solid var(--line); border-radius:10px; padding:7px 9px; margin:5px 0; font-family:Georgia, 'Times New Roman', serif; font-size:14px; }
    .figure-line.sig { background:var(--green-bg); border-color:#abefc6; }
    .figure-line.na { background:var(--na-bg); color:var(--na); }
    .line-text { min-width:0; }
    .badge-sig, .badge-muted, .badge-na { display:inline-block; border-radius:999px; padding:2px 7px; font-family:Arial, Helvetica, sans-serif; font-size:11px; font-weight:700; white-space:nowrap; }
    .badge-sig { background:var(--green-bg); color:var(--green); border:1px solid #abefc6; }
    .badge-muted { background:#f8fafc; color:#667085; border:1px solid var(--line); }
    .badge-na { background:var(--na-bg); color:var(--na); border:1px solid var(--line); }
    .compact { border:1px solid var(--line); border-radius:10px; padding:8px 10px; margin:8px 0; }
    @media (max-width:1100px) { .hyp-grid { grid-template-columns:1fr; } }
    """
    js = """
    function setModelMode(mode) {
      if (mode === 'covariate') document.body.classList.add('show-covariate');
      else document.body.classList.remove('show-covariate');
    }
    window.addEventListener('DOMContentLoaded', () => {
      for (const input of document.querySelectorAll('input[name="model-mode"]')) {
        input.addEventListener('change', () => setModelMode(input.value));
      }
      setModelMode('base');
    });
    """
    return (
        '<!doctype html><html lang="en"><head><meta charset="utf-8">'
        '<meta name="viewport" content="width=device-width, initial-scale=1">'
        '<title>Exploratory construct scoring: H1-H4</title>'
        f'<style>{css}</style></head><body><div class="page">'
        '<section class="top-card">'
        '<h1>Exploratory construct-scoring checks for H1-H4</h1>'
        f'<p class="small">Generated {h(generated)} from {h(sm.ANALYSIS_DIR)}.</p>'
        '<p>This report shows only the figure-level values: H1 total effects, H2 cognitive-load mediation lines, H3 engagement mediation lines, and H4 engagement–cognitive-load correlations. Retention scoring is kept at the base/final retention-score route.</p>'
        '<p><strong>Default:</strong> base/no-covariate models. The preregistered route should match the values in the figure.</p>'
        f'<ul class="route-nav">{variant_nav}</ul>'
        '</section>'
        '<div class="toggle-box"><span>Displayed model set:</span>'
        '<label><input type="radio" name="model-mode" value="base" checked> Base models</label>'
        '<label><input type="radio" name="model-mode" value="covariate"> Covariate-adjusted sensitivity models</label>'
        '</div>'
        f'{sections}'
        '</div>'
        f'<script>{js}</script></body></html>'
    )


def main(argv: list[str] | None = None) -> int:
    _ = argv
    sm.OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(html_document(), encoding="utf-8")
    print(f"Wrote exploratory construct-scoring report to: {OUTPUT_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
