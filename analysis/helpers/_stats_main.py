from __future__ import annotations

from typing import Any


def stats_placeholder() -> dict[str, Any]:
    """Placeholder for the future, reviewable inferential-statistics helper.

    This file is deliberately separate from apps/summarise_merged.py so the final
    modelling code can be reviewed without reading the HTML-building code. The
    planned implementation should build one analysis table and then run the
    preregistered direct-effect, mediation, exploratory, and robustness models.
    """
    return {
        "status": "TBD"
    }