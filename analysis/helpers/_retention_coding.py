from __future__ import annotations

import csv
import hashlib
import html
import json
import math
import re
import unicodedata
from collections import Counter, defaultdict
from datetime import datetime
from pathlib import Path
from typing import Any

from ._delayed_response_filter import DELAYED_INCLUDED_COLUMN
from ._ret_main import parse_seen_details, retention_column_name
from ._shared import (
    CREATURE_NAME_BY_ID,
    DATA_CONFIG_DIR,
    DATA_DIR,
    MAX_RETENTION_SLOTS,
    RESOURCES_DIR,
    RETENTION_QUESTION_SPECS,
    SURVEY_EXPORT_PATH,
    clean,
    delayed_flag,
    delayed_included_flag,
    first_present,
    mcid_from_row,
    parse_numeric,
)
from ._survey_io import detect_text_encoding

RETENTION_ANSWERS_PATH = DATA_DIR / "retention_answers.tsv"
GENAI_SCORES_PATH = DATA_DIR / "retention_scores_genai.tsv"
GRADER1_SCORES_PATH = DATA_DIR / "retention_scores_grader1.tsv"
GRADER2_SCORES_PATH = DATA_DIR / "retention_scores_grader2.tsv"
RETENTION_MERGED_PATH = DATA_DIR / "retention_scores_merged.tsv"
GENAI_PROMPT_PATH = DATA_CONFIG_DIR / "genai_prompt.txt"
SCORING_RUBRICS_HTML_PATH = DATA_CONFIG_DIR / "scoring_rubrics.html"
CREATURE_INFO_HTML_PATH = DATA_CONFIG_DIR / "creature_info.html"
RUBRIC_JSON_PATH = RESOURCES_DIR / "retention_rubrics.json"
SCORE_BACKUPS_DIR = DATA_DIR.parent / "score_backups"
BACKED_UP_RETENTION_FILENAMES = {
    "retention_answers.tsv",
    "retention_scores_genai.tsv",
    "retention_scores_grader1.tsv",
    "retention_scores_grader2.tsv",
    "retention_scores_merged.tsv",
}

LOW_CONFIDENCE_THRESHOLD = 80.0
VALIDATION_SAMPLE_FRACTION = 0.25

RETENTION_ANSWER_FIELDNAMES = [
    "MCID",
    "creature",
    "question",
    "answer",
    "answer_std",
]

GENAI_SCORE_FIELDNAMES = [
    "question",
    "creature",
    "answer_std",
    "score (0-4)",
    "confidence (0-100%)",
    "note (optional)",
]

GRADER_SCORE_FIELDNAMES = [
    "question",
    "creature",
    "answer_std",
    "score (0-4)",
    "status",
    "note (optional)",
    "updated_at",
    "task_id",
]

MERGED_SCORE_FIELDNAMES = [
    "MCID",
    "creature",
    "question",
    "answer",
    "answer_std",
    "genai_score",
    "genai_confidence",
    "genai_note",
    "grader1_score",
    "grader1_status",
    "grader1_note",
    "grader2_score",
    "grader2_status",
    "grader2_note",
    "final_score",
    "final_status",
    "moment",
    "creature_id",
    "question_key",
    "question_label",
    "task_id",
    "occurrence_weight",
]

QUESTION_BY_KEY = {
    key: f"Q{index}"
    for index, (key, _label) in enumerate(RETENTION_QUESTION_SPECS, start=1)
}
QUESTION_KEY_BY_QUESTION = {value: key for key, value in QUESTION_BY_KEY.items()}
QUESTION_LABEL_BY_QUESTION = {
    QUESTION_BY_KEY[key]: label
    for key, label in RETENTION_QUESTION_SPECS
}
QUESTION_ORDER = list(QUESTION_KEY_BY_QUESTION)
QUESTION_SORT_INDEX = {question: index for index, question in enumerate(QUESTION_ORDER)}
FORM_ORDER_COLUMNS = [
    "retention_form_order",
    "retention_immediate_form_order",
    "retention_delayed_form_order",
    "form_order",
    "FORM_ORDER",
    "START",
    "INIT_START",
    "start",
    "init_start",
]


def stable_id(*parts: object, length: int = 24) -> str:
    raw = "|".join(clean(part) for part in parts)
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()[:length]


def standardise_answer(value: object) -> str:
    text = clean(value)
    if not text:
        return ""
    text = unicodedata.normalize("NFKC", text)
    text = text.lower()
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def read_tsv(path: Path) -> list[dict[str, str]]:
    if not path.exists():
        return []
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        reader = csv.DictReader(handle, delimiter="\t")
        return [dict(row) for row in reader]


def backup_retention_tsv(path: Path) -> None:
    """Copy newly written retention TSVs to score_backups without ever restoring from backups."""
    if path.name not in BACKED_UP_RETENTION_FILENAMES or not path.exists():
        return
    SCORE_BACKUPS_DIR.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S-%f")
    backup_path = SCORE_BACKUPS_DIR / f"{path.stem}-{timestamp}{path.suffix}"
    backup_path.write_bytes(path.read_bytes())


def write_tsv(path: Path, fieldnames: list[str], rows: list[dict[str, Any]], *, backup: bool = True) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames, delimiter="\t", extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({field: clean(row.get(field)) for field in fieldnames})
    if backup:
        backup_retention_tsv(path)


def load_rubric_json(path: Path = RUBRIC_JSON_PATH) -> dict[str, Any]:
    if not path.exists():
        raise FileNotFoundError(f"Missing rubric JSON source: {path}")
    return json.loads(path.read_text(encoding="utf-8"))


def normalise_form_order(value: object) -> str:
    text = clean(value).lower().replace("-", "_").replace(" ", "_")
    if not text:
        return ""
    image_tokens = {"image_first", "images_first", "img_first", "img", "image", "1"}
    name_tokens = {"name_first", "names_first", "name", "2"}
    if text in image_tokens or ("image" in text and "first" in text) or ("img" in text and "first" in text):
        return "image_first"
    if text in name_tokens or ("name" in text and "first" in text):
        return "name_first"
    return ""


def form_order_from_row(row: dict[str, Any], *, delayed: bool) -> str:
    if delayed:
        delayed_specific = first_present(row, ["retention_delayed_form_order", "delayed_form_order"])
        normalised = normalise_form_order(delayed_specific)
        if normalised:
            return normalised

    immediate_specific = first_present(row, ["retention_immediate_form_order", "retention_form_order", "form_order", "START", "INIT_START"])
    immediate_order = normalise_form_order(immediate_specific)
    if not immediate_order:
        for column in FORM_ORDER_COLUMNS:
            immediate_order = normalise_form_order(row.get(column))
            if immediate_order:
                break

    if not immediate_order:
        return ""
    if not delayed:
        return immediate_order
    return "name_first" if immediate_order == "image_first" else "image_first"


def question_pair_for_slot(row: dict[str, Any], slot_index: int, seen_count: int, *, delayed: bool) -> list[str]:
    image_keys = ["img1", "img2"]
    name_keys = ["name1", "name2"]
    image_has_answer = any(clean(row.get(retention_column_name(slot_index, key))) for key in image_keys)
    name_has_answer = any(clean(row.get(retention_column_name(slot_index, key))) for key in name_keys)

    if image_has_answer and not name_has_answer:
        return image_keys
    if name_has_answer and not image_has_answer:
        return name_keys
    if image_has_answer and name_has_answer:
        # Structurally unusual, but the safest audit behaviour is to retain all non-empty pairs.
        return [*image_keys, *name_keys]

    order = form_order_from_row(row, delayed=delayed)
    if not order:
        return []

    # For odd counts, the first half is rounded up. This only affects fully blank
    # administered pairs; non-empty columns above override the inferred half.
    first_half_end = math.ceil(max(0, seen_count) / 2)
    first_half = slot_index <= first_half_end
    image_for_this_slot = first_half if order == "image_first" else not first_half
    return image_keys if image_for_this_slot else name_keys


def build_prompt_rows_from_survey(survey_rows: list[dict[str, str]]) -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []

    for row in survey_rows:
        participant_id = mcid_from_row(row)
        if not participant_id:
            continue
        is_delayed = delayed_flag(row)
        if is_delayed and not delayed_included_flag(row):
            continue
        moment = "Delayed" if is_delayed else "Immediate"
        seen_creatures, _invalid = parse_seen_details(row.get("SEEN"))
        seen_creatures = seen_creatures[:MAX_RETENTION_SLOTS]

        for slot_index, creature_id in enumerate(seen_creatures, start=1):
            administered_keys = question_pair_for_slot(row, slot_index, len(seen_creatures), delayed=is_delayed)
            for question_key in administered_keys:
                question = QUESTION_BY_KEY[question_key]
                answer = clean(row.get(retention_column_name(slot_index, question_key)))
                rows.append({
                    "participant_id": participant_id,
                    "moment": moment,
                    "creature_id": creature_id,
                    "creature": CREATURE_NAME_BY_ID.get(creature_id, creature_id),
                    "question": question,
                    "question_key": question_key,
                    "question_label": QUESTION_LABEL_BY_QUESTION.get(question, question_key),
                    "answer": answer,
                    "answer_std": standardise_answer(answer),
                })

    rows.sort(key=lambda item: (
        clean(item.get("participant_id")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("moment")),
    ))
    return rows


def build_retention_answer_rows(prompt_rows: list[dict[str, str]]) -> list[dict[str, str]]:
    rows = [
        {
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "question": row["question"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
        }
        for row in prompt_rows
    ]
    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("answer_std")),
    ))
    return rows


def genai_group_key(row: dict[str, str], multi_creature_keys: set[tuple[str, str]]) -> tuple[str, str, str]:
    question = clean(row.get("question"))
    answer_std = clean(row.get("answer_std"))
    creature = clean(row.get("creature"))
    # Hybrid duplicate rule: normally question + answer_std; if the same
    # standardised answer for that question appears for multiple creatures, split by creature.
    if (question, answer_std) in multi_creature_keys:
        return question, creature, answer_std
    return question, creature, answer_std


def existing_genai_lookup(path: Path = GENAI_SCORES_PATH) -> dict[tuple[str, str, str], dict[str, str]]:
    lookup: dict[tuple[str, str, str], dict[str, str]] = {}
    for row in read_tsv(path):
        key = (clean(row.get("question")), clean(row.get("creature")), clean(row.get("answer_std")))
        if all(key):
            lookup[key] = row
    return lookup


def build_unique_genai_rows(prompt_rows: list[dict[str, str]], *, existing_path: Path = GENAI_SCORES_PATH) -> list[dict[str, str]]:
    nonblank = [row for row in prompt_rows if clean(row.get("answer_std"))]
    creatures_by_question_answer: dict[tuple[str, str], set[str]] = defaultdict(set)
    for row in nonblank:
        creatures_by_question_answer[(row["question"], row["answer_std"])].add(row["creature"])
    multi_creature_keys = {key for key, creatures in creatures_by_question_answer.items() if len(creatures) > 1}

    grouped: dict[tuple[str, str, str], dict[str, str]] = {}
    for row in nonblank:
        key = genai_group_key(row, multi_creature_keys)
        grouped.setdefault(key, {
            "question": key[0],
            "creature": key[1],
            "answer_std": key[2],
            "score (0-4)": "",
            "confidence (0-100%)": "",
            "note (optional)": "",
        })

    existing = existing_genai_lookup(existing_path)
    rows: list[dict[str, str]] = []
    for key, row in grouped.items():
        previous = existing.get(key, {})
        merged = dict(row)
        for field in ("score (0-4)", "confidence (0-100%)", "note (optional)"):
            if clean(previous.get(field)):
                merged[field] = clean(previous.get(field))
        rows.append(merged)

    rows.sort(key=lambda item: (
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
        clean(item.get("creature")).lower(),
        clean(item.get("answer_std")),
    ))
    return rows


def html_text(value: object) -> str:
    return html.escape(clean(value)).replace("\n", "<br>")


def html_text_with_tokens(value: object) -> str:
    escaped = html_text(value)
    return re.sub(
        r"\[(SRC|FAN)\]",
        r'<span class="rubric-token-cobalt">[\1]</span>',
        escaped,
    )


def render_rubric_content_html(content: Any) -> str:
    if isinstance(content, dict):
        rows = []
        for left, right in content.items():
            rows.append(
                "<tr>"
                f"<td>{html_text(left)}</td>"
                f"<td>{html_text_with_tokens(right)}</td>"
                "</tr>"
            )
        if not rows:
            return ""
        return (
            '<table class="rubric-inner-table generated-rubric-inner-table"><tbody>'
            + "".join(rows)
            + "</tbody></table>"
        )

    if isinstance(content, list):
        items: list[str] = []
        for item in content:
            if isinstance(item, dict):
                text = "\n".join(f"{key}: {value}" for key, value in item.items())
            else:
                text = clean(item)
            if clean(text):
                items.append(f"<li>{html_text_with_tokens(text)}</li>")
        return f'<ul class="rubric-content-list">{"".join(items)}</ul>' if items else ""

    if clean(content):
        return f'<p class="rubric-content-text">{html_text_with_tokens(content)}</p>'
    return ""


def expanded_rubric_rows(table: dict[str, Any], score_scale: list[Any]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    score_order = [str(score) for score in score_scale]

    def append_score_rows(scores_by_value: dict[str, Any], base_row: dict[str, str]) -> None:
        ordered_scores = [score for score in score_order if score in scores_by_value]
        ordered_scores.extend(score for score in scores_by_value if score not in ordered_scores)
        for score in ordered_scores:
            rows.append({
                **base_row,
                "score": score,
                "content": scores_by_value.get(score),
            })

    if isinstance(table.get("scores"), dict):
        append_score_rows(table["scores"], {
            "creature_id": clean(table.get("creature_id")),
            "creature": clean(table.get("creature") or "All creatures"),
        })

    for entry in table.get("rows") or []:
        if isinstance(entry, dict) and isinstance(entry.get("scores"), dict):
            append_score_rows(entry["scores"], {
                "creature_id": clean(entry.get("creature_id")),
                "creature": clean(entry.get("creature") or ""),
            })

    return rows


def grouped_rubric_body_html(rows: list[dict[str, Any]]) -> str:
    groups: list[dict[str, Any]] = []
    for row in rows:
        creature_key = clean(row.get("creature_id")) or clean(row.get("creature"))
        if not groups or groups[-1]["key"] != creature_key:
            groups.append({"key": creature_key, "creature": clean(row.get("creature")), "rows": []})
        groups[-1]["rows"].append(row)

    parts: list[str] = []
    for group in groups:
        group_rows = group["rows"]
        row_span = max(1, len(group_rows))
        for row_index, row in enumerate(group_rows):
            creature_cell = f'<td rowspan="{row_span}">{html_text(group["creature"])}</td>' if row_index == 0 else ""
            parts.append(
                "<tr>"
                + creature_cell
                + f'<td><span class="score-number">{html_text(row.get("score"))}</span></td>'
                + f'<td class="rubric-note">{render_rubric_content_html(row.get("content"))}</td>'
                + "</tr>"
            )
    return "\n".join(parts)


def render_scoring_rubrics_html(rubric: dict[str, Any]) -> str:
    question_tables = rubric.get("question_rubric_tables") or {}
    question_order = [key for key, _label in RETENTION_QUESTION_SPECS if key in question_tables]
    first_question = question_order[0] if question_order else ""
    score_scale = rubric.get("score_scale", [0, 1, 2, 3, 4])
    question_labels = rubric.get("question_short_labels") or {}
    css_path = "../../resources/static/scoring_app.css"
    generated_at = datetime.now().strftime("%d %B %Y at %H:%M")

    question_tabs = "\n".join(
        f'''<button class="rubric-subtab-button {'active' if question_key == first_question else ''}" type="button" role="tab" data-rubric-question="{html.escape(question_key)}" aria-selected="{'true' if question_key == first_question else 'false'}">
              {html.escape(clean(question_labels.get(question_key)) or QUESTION_BY_KEY.get(question_key, question_key))}
            </button>'''
        for question_key in question_order
    )

    question_panels: list[str] = []
    for question_key in question_order:
        table = question_tables.get(question_key) or {}
        rows = expanded_rubric_rows(table, score_scale)
        intro_html = f"<p>{html.escape(clean(table.get('intro')))}</p>" if clean(table.get("intro")) else ""
        question_panels.append(f'''
          <section class="rubric-subtab-panel {'active' if question_key == first_question else ''}" role="tabpanel" data-rubric-question-panel="{html.escape(question_key)}">
            <section class="full-rubric-section">
              <h3>{html.escape(clean(table.get('short_title')) or QUESTION_BY_KEY.get(question_key, question_key))}</h3>
              <p class="small">{html.escape(clean(table.get('title')))}</p>
              {intro_html}
              <table class="full-rubric-table">
                <colgroup>
                  <col class="full-rubric-creature-col">
                  <col class="full-rubric-score-col">
                  <col>
                </colgroup>
                <thead>
                  <tr><th>Creature</th><th>Score</th><th>Label / Content</th></tr>
                </thead>
                <tbody>
                  {grouped_rubric_body_html(rows)}
                </tbody>
              </table>
            </section>
          </section>''')

    instructions = clean(rubric.get("instructions_html")) or "<p>No general scoring instructions are configured.</p>"

    return f'''<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Retention scoring rubrics</title>
  <link rel="stylesheet" href="{css_path}">
  <style>
    body {{ padding: 0; }}
    #topbar {{ align-items: center; }}
    #topbar .small {{ margin: 4px 0 0; }}
    main {{ padding: 18px; }}
    .doc-card {{ max-width: 1280px; margin: 0 auto 18px; }}
    .tab-panel {{ display: none; }}
    .tab-panel.active {{ display: block; }}
    .rubric-subtabs {{ top: 0; }}
    .full-rubric-table {{ table-layout: fixed; }}
    .full-rubric-table td:first-child {{ font-weight: 800; }}
    .generated-rubric-inner-table td:first-child {{ width: 38%; font-weight: 700; }}
  </style>
</head>
<body>
  <header id="topbar">
    <div>
      <h1 id="app-title">Retention scoring rubrics</h1>
      <p class="small">Generated directly from <code>resources/retention_rubrics.json</code> on {html.escape(generated_at)}.</p>
    </div>
    <nav id="tabs" aria-label="Document tabs">
      <button class="tab-button active" type="button" data-tab="instructions">Instructions</button>
      <button class="tab-button" type="button" data-tab="all-rubrics">All rubrics</button>
    </nav>
  </header>

  <main>
    <section class="tab-panel active" data-panel="instructions">
      <article class="doc-card">{instructions}</article>
    </section>

    <section class="tab-panel" data-panel="all-rubrics">
      <article class="doc-card">
        <div class="rubric-subtabs" role="tablist" aria-label="Rubric question tabs">
          {question_tabs}
        </div>
        <div class="rubric-subtab-panels">
          {''.join(question_panels)}
        </div>
      </article>
    </section>
  </main>

  <script>
    const activateTopTab = (tabName) => {{
      document.querySelectorAll('.tab-button').forEach((button) => {{
        const active = button.dataset.tab === tabName;
        button.classList.toggle('active', active);
        button.setAttribute('aria-selected', active ? 'true' : 'false');
      }});
      document.querySelectorAll('.tab-panel').forEach((panel) => {{
        panel.classList.toggle('active', panel.dataset.panel === tabName);
      }});
    }};

    document.querySelectorAll('.tab-button').forEach((button) => {{
      button.addEventListener('click', () => activateTopTab(button.dataset.tab));
    }});

    const activateRubricTab = (questionKey) => {{
      document.querySelectorAll('.rubric-subtab-button').forEach((button) => {{
        const active = button.dataset.rubricQuestion === questionKey;
        button.classList.toggle('active', active);
        button.setAttribute('aria-selected', active ? 'true' : 'false');
      }});
      document.querySelectorAll('.rubric-subtab-panel').forEach((panel) => {{
        panel.classList.toggle('active', panel.dataset.rubricQuestionPanel === questionKey);
      }});
    }};

    document.querySelectorAll('.rubric-subtab-button').forEach((button) => {{
      button.addEventListener('click', () => activateRubricTab(button.dataset.rubricQuestion));
    }});
  </script>
</body>
</html>'''


def render_creature_info_html(rubric: dict[str, Any]) -> str:
    parts = [
        "<!doctype html><html><head><meta charset='utf-8'><title>Creature information</title>",
        "<style>body{font-family:system-ui,sans-serif;line-height:1.45;margin:2rem;max-width:1100px}.creature{border-top:1px solid #ccd;padding:1rem 0}img{max-width:220px;height:auto;border:1px solid #ccd;background:#f7f7f7}dt{font-weight:700}dd{margin:0 0 .5rem 0}</style>",
        "</head><body>",
        "<h1>Creature information</h1>",
        "<p>This file is generated directly from <code>resources/retention_rubrics.json</code>. Image paths are relative to this file in <code>data/config/</code>.</p>",
    ]
    creatures = rubric.get("creatures") or {}
    for creature_id, creature in sorted(creatures.items(), key=lambda item: clean((item[1] or {}).get("name")).lower()):
        image_name = Path(clean(creature.get("image"))).name
        image_path = f"../../resources/static/creatures/{image_name}" if image_name else ""
        facts = creature.get("facts") or []
        parts.append("<section class='creature'>")
        parts.append(f"<h2>{html.escape(clean(creature.get('name')) or creature_id)}</h2>")
        if image_path:
            parts.append(f"<img src='{html.escape(image_path)}' alt='{html.escape(clean(creature.get('name')))}'>")
        parts.append("<dl>")
        parts.append(f"<dt>Creature id</dt><dd>{html.escape(creature_id)}</dd>")
        parts.append(f"<dt>Chapter</dt><dd>{html.escape(clean(creature.get('chapter')))}</dd>")
        parts.append(f"<dt>Environment</dt><dd>{html.escape(clean(creature.get('environment')))}</dd>")
        parts.append(f"<dt>Appearance</dt><dd>{html.escape(clean(creature.get('appearance')))}</dd>")
        parts.append("<dt>Facts</dt><dd><ul>" + "".join(f"<li>{html.escape(clean(fact))}</li>" for fact in facts) + "</ul></dd>")
        parts.append("</dl></section>")
    parts.append("</body></html>")
    return "\n".join(parts)


def genai_prompt_text() -> str:
    return """In the attachment, you should find exactly these files:
- retention_scores_genai.tsv
- scoring_rubrics.html
- creature_info.html

If any file is missing, inaccessible, unreadable, or clearly incomplete, do not continue. Stop your response and state which file(s) are missing or unusable.

Strict source rule:
Use only the attached files. Do not browse the internet. Do not look up creature names, animal facts, game facts, images, or locations online. Do not use external knowledge, model memory, or assumptions about real animals, games, Minecraft, fantasy creatures, or naming conventions. These are study-specific fictional learning materials. If something is not supported by the attached rubric or creature information, treat it as unknown.

Task:
Fill in retention_scores_genai.tsv. Keep the row order exactly the same. Do not add, delete, reorder, rename, or reformat columns. For every row, evaluate the standardised retention answer using only the attached rubric and creature information.

Columns to fill:
- score (0-4): an integer from 0 to 4 only.
- confidence (0-100%): your confidence percentage as a number from 0 to 100 only. Do not include a percent sign.
- note (optional): leave this cell empty unless a note is genuinely needed. Add a note only for ambiguity, borderline scores, uncertainty, suspected rubric tension, missing/unclear source information, or a reason a human should inspect the row. Do not add routine notes for evident answers.

Important scoring rules:
- Score the value in answer_std for the listed question and creature.
- Use the four question rubrics exactly as supplied.
- The answer text has already been standardised; do not punish lowercase, stripped whitespace, or simple formatting loss.
- Do not reward an answer for being plausible in general. Reward it only when it matches the supplied study-specific creature information and rubric.
- Do not infer hidden intent when the answer is vague. If the answer could refer to multiple things and the rubric does not make it clearly correct, lower the score and add a short note.
- If the answer is blank, equivalent to "I do not know", nonsensical, or off-topic, score it 0.
- If an answer contains both correct and incorrect elements, apply the rubric rather than automatically giving full credit. Use the note only if the mixed answer needs human inspection.
- The same answer may occur for many participants. Score the row once as the score for all identical cases represented by that row.
- If the same answer seems correct for one creature but not another, score each row according to the listed creature only.
- If the rubric and creature information appear to conflict, follow the rubric where possible and add a note.

TSV output rules:
- Preserve valid TSV format.
- Preserve the original header exactly.
- Preserve the original row order exactly.
- Preserve all existing cell values except the three columns you are asked to fill.
- Do not add markdown, explanations, comments, code fences, bullet points, or surrounding text around the TSV.
- Return only the completed TSV content or a completed TSV attachment, depending on the interface you are using.
"""


def write_prompt_support_files(rubric_path: Path = RUBRIC_JSON_PATH) -> None:
    rubric = load_rubric_json(rubric_path)
    DATA_CONFIG_DIR.mkdir(parents=True, exist_ok=True)
    GENAI_PROMPT_PATH.write_text(genai_prompt_text(), encoding="utf-8")
    SCORING_RUBRICS_HTML_PATH.write_text(render_scoring_rubrics_html(rubric), encoding="utf-8")
    CREATURE_INFO_HTML_PATH.write_text(render_creature_info_html(rubric), encoding="utf-8")


def prepare_retention_answer_files(survey_rows: list[dict[str, str]]) -> dict[str, Any]:
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    retention_rows = build_retention_answer_rows(prompt_rows)
    genai_rows = build_unique_genai_rows(prompt_rows)
    write_tsv(RETENTION_ANSWERS_PATH, RETENTION_ANSWER_FIELDNAMES, retention_rows)
    write_tsv(GENAI_SCORES_PATH, GENAI_SCORE_FIELDNAMES, genai_rows)
    write_prompt_support_files()
    return {
        "prompt_rows": len(prompt_rows),
        "retention_answer_rows": len(retention_rows),
        "unique_genai_rows": len(genai_rows),
        "blank_prompt_rows": sum(1 for row in prompt_rows if not clean(row.get("answer_std"))),
        "nonblank_prompt_rows": sum(1 for row in prompt_rows if clean(row.get("answer_std"))),
    }


def score_is_valid(value: object) -> bool:
    number = parse_numeric(value)
    return number is not None and float(number).is_integer() and 0 <= int(number) <= 4


def score_text(value: object) -> str:
    number = parse_numeric(value)
    if number is None or not float(number).is_integer():
        return ""
    score = int(number)
    return str(score) if 0 <= score <= 4 else ""


def confidence_value(value: object) -> float | None:
    text = clean(value).replace("%", "")
    number = parse_numeric(text)
    if number is None or number < 0 or number > 100:
        return None
    return float(number)


def load_genai_scores(path: Path = GENAI_SCORES_PATH) -> tuple[dict[tuple[str, str, str], dict[str, str]], list[str]]:
    if not path.exists():
        return {}, [f"Missing {path}. Run sum_merged with PUBLIC_ROUTE=False to create the GenAI prompt files, then use data/config/genai_prompt.txt to fill data/retention_scores_genai.tsv."]
    rows = read_tsv(path)
    if not rows:
        return {}, [f"{path} is empty."]
    missing_columns = [column for column in GENAI_SCORE_FIELDNAMES if column not in rows[0]]
    if missing_columns:
        return {}, [f"{path} is missing column(s): {', '.join(missing_columns)}"]
    lookup: dict[tuple[str, str, str], dict[str, str]] = {}
    problems: list[str] = []
    for index, row in enumerate(rows, start=2):
        key = (clean(row.get("question")), clean(row.get("creature")), clean(row.get("answer_std")))
        if not all(key):
            problems.append(f"Row {index}: question, creature, and answer_std must all be filled.")
            continue
        if key in lookup:
            problems.append(f"Row {index}: duplicate GenAI score key: {key[0]} / {key[1]} / {key[2]}")
        if not score_is_valid(row.get("score (0-4)")):
            problems.append(f"Row {index}: score (0-4) must be an integer from 0 to 4.")
        if confidence_value(row.get("confidence (0-100%)")) is None:
            problems.append(f"Row {index}: confidence (0-100%) must be a number from 0 to 100.")
        lookup[key] = row
    return lookup, problems


def task_id_for_unique(question: str, creature: str, answer_std: str) -> str:
    return stable_id(question, creature, answer_std)


def unique_task_lookup(prompt_rows: list[dict[str, str]]) -> dict[str, dict[str, Any]]:
    genai_rows = build_unique_genai_rows(prompt_rows)
    occurrence_counts = Counter(
        (row["question"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    raw_examples: dict[tuple[str, str, str], list[str]] = defaultdict(list)
    creature_id_by_key: dict[tuple[str, str, str], str] = {}
    question_key_by_question: dict[str, str] = {}
    for row in prompt_rows:
        if not clean(row.get("answer_std")):
            continue
        key = (row["question"], row["creature"], row["answer_std"])
        if len(raw_examples[key]) < 5 and clean(row.get("answer")) not in raw_examples[key]:
            raw_examples[key].append(clean(row.get("answer")))
        creature_id_by_key[key] = row["creature_id"]
        question_key_by_question[row["question"]] = row["question_key"]

    tasks: dict[str, dict[str, Any]] = {}
    for row in genai_rows:
        key = (row["question"], row["creature"], row["answer_std"])
        task_id = task_id_for_unique(*key)
        question_key = question_key_by_question.get(row["question"], QUESTION_KEY_BY_QUESTION.get(row["question"], ""))
        creature_id = creature_id_by_key.get(key, "")
        tasks[task_id] = {
            "task_id": task_id,
            "question": row["question"],
            "question_key": question_key,
            "question_label": QUESTION_LABEL_BY_QUESTION.get(row["question"], question_key),
            "creature": row["creature"],
            "creature_name": row["creature"],
            "creature_id": creature_id,
            "answer_std": row["answer_std"],
            "answer": row["answer_std"],
            "occurrence_count": occurrence_counts.get(key, 0),
            "raw_examples": raw_examples.get(key, []),
        }
    return tasks


def confidence_bucket(value: object) -> str:
    confidence = confidence_value(value)
    if confidence is None:
        return "missing"
    if confidence < 60:
        return "00-59"
    if confidence < 80:
        return "60-79"
    return "80-100"


def deterministic_order(seed: str, task: dict[str, Any]) -> str:
    return hashlib.sha256((seed + "|" + clean(task.get("task_id"))).encode("utf-8")).hexdigest()


def build_review_tasks(prompt_rows: list[dict[str, str]], genai_lookup: dict[tuple[str, str, str], dict[str, str]], *, low_confidence_threshold: float = LOW_CONFIDENCE_THRESHOLD) -> list[dict[str, Any]]:
    tasks_by_id = unique_task_lookup(prompt_rows)
    tasks = list(tasks_by_id.values())
    genai_by_task_id: dict[str, dict[str, str]] = {}
    for key, row in genai_lookup.items():
        genai_by_task_id[task_id_for_unique(*key)] = row

    for task in tasks:
        genai_row = genai_by_task_id.get(task["task_id"], {})
        task["genai_score"] = score_text(genai_row.get("score (0-4)"))
        confidence = confidence_value(genai_row.get("confidence (0-100%)"))
        task["genai_confidence"] = "" if confidence is None else str(int(confidence) if confidence.is_integer() else confidence)
        task["genai_note"] = clean(genai_row.get("note (optional)"))
        task["confidence_bucket"] = confidence_bucket(genai_row.get("confidence (0-100%)"))
        task["low_confidence"] = confidence is not None and confidence < low_confidence_threshold

    validation_ids: set[str] = set()
    low_confidence_ids: set[str] = set()
    note_ids: set[str] = set()
    groups: dict[tuple[str, str], list[dict[str, Any]]] = defaultdict(list)
    for task in tasks:
        groups[(clean(task.get("question")), clean(task.get("confidence_bucket")))].append(task)

    for group_key, group in groups.items():
        ordered = sorted(group, key=lambda item: deterministic_order(f"validation-{group_key[0]}-{group_key[1]}-v1", item))
        n_to_take = max(1, round(len(ordered) * VALIDATION_SAMPLE_FRACTION))
        validation_ids.update(task["task_id"] for task in ordered[:n_to_take])

    for task in tasks:
        if task.get("low_confidence"):
            low_confidence_ids.add(task["task_id"])
        if clean(task.get("genai_note")):
            note_ids.add(task["task_id"])

    selected_ids = validation_ids | low_confidence_ids | note_ids
    selected = [task for task in tasks if task["task_id"] in selected_ids]
    for task in selected:
        reasons = []
        if task["task_id"] in validation_ids:
            # The UI does not reveal this per task during blind scoring, but the
            # payload keeps the reason for auditability.
            reasons.append("validation_sample")
        if task["task_id"] in low_confidence_ids:
            reasons.append("low_confidence")
        if task["task_id"] in note_ids:
            reasons.append("genai_note")
        task["review_reasons"] = ",".join(dict.fromkeys(reasons))

    selected.sort(key=lambda task: (
        QUESTION_SORT_INDEX.get(clean(task.get("question")), 999),
        clean(task.get("creature")).lower(),
        clean(task.get("answer_std")),
    ))
    return selected


def load_grader_scores(path: Path) -> dict[str, dict[str, str]]:
    rows = read_tsv(path)
    lookup: dict[str, dict[str, str]] = {}
    for row in rows:
        task_id = clean(row.get("task_id"))
        if task_id:
            lookup[task_id] = row
    return lookup


def write_grader_scores(path: Path, rows_by_task_id: dict[str, dict[str, Any]]) -> None:
    rows = sorted(rows_by_task_id.values(), key=lambda row: (
        QUESTION_SORT_INDEX.get(clean(row.get("question")), 999),
        clean(row.get("creature")).lower(),
        clean(row.get("answer_std")),
    ))
    write_tsv(path, GRADER_SCORE_FIELDNAMES, rows)


def validate_genai_completeness(prompt_rows: list[dict[str, str]], genai_lookup: dict[tuple[str, str, str], dict[str, str]]) -> list[str]:
    expected = {
        (row["question"], row["creature"], row["answer_std"])
        for row in build_unique_genai_rows(prompt_rows)
    }
    missing = sorted(expected - set(genai_lookup), key=lambda key: (QUESTION_SORT_INDEX.get(key[0], 999), key[1].lower(), key[2]))
    return [f"Missing GenAI score for {q} / {creature} / {answer[:80]}" for q, creature, answer in missing[:50]]


def build_prompt_score_rows(
    survey_rows: list[dict[str, str]],
    *,
    require_complete_review: bool = True,
) -> tuple[list[dict[str, Any]], list[str]]:
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    genai_lookup, genai_problems = load_genai_scores(GENAI_SCORES_PATH)
    problems = list(genai_problems)
    if genai_problems:
        return [], problems
    problems.extend(validate_genai_completeness(prompt_rows, genai_lookup))

    review_tasks = build_review_tasks(prompt_rows, genai_lookup)
    required_review_ids = {task["task_id"] for task in review_tasks}
    occurrence_counts = Counter(
        (row["question"], row["creature"], row["answer_std"])
        for row in prompt_rows
        if clean(row.get("answer_std"))
    )
    grader1 = load_grader_scores(GRADER1_SCORES_PATH)
    grader2 = load_grader_scores(GRADER2_SCORES_PATH)

    rows: list[dict[str, Any]] = []
    for row in prompt_rows:
        key = (row["question"], row["creature"], row["answer_std"])
        task_id = "" if not row["answer_std"] else task_id_for_unique(*key)
        genai = {} if not row["answer_std"] else genai_lookup.get(key, {})
        g1 = grader1.get(task_id, {}) if task_id else {}
        g2 = grader2.get(task_id, {}) if task_id else {}
        g1_score = score_text(g1.get("score (0-4)")) if clean(g1.get("status")) == "graded" else ""
        g2_score = score_text(g2.get("score (0-4)")) if clean(g2.get("status")) == "graded" else ""

        final_score = ""
        final_status = ""
        if not row["answer_std"]:
            final_score = "0"
            final_status = "auto_blank"
        elif task_id in required_review_ids:
            if not g1_score or not g2_score:
                final_status = "needs_human_scores"
                if require_complete_review:
                    problems.append(f"Human review incomplete for {row['question']} / {row['creature']} / {row['answer_std'][:80]}")
            elif g1_score != g2_score:
                final_status = "needs_adjudication"
                if require_complete_review:
                    problems.append(f"Human disagreement unresolved for {row['question']} / {row['creature']} / {row['answer_std'][:80]}: grader1={g1_score}, grader2={g2_score}")
            else:
                final_score = g1_score
                final_status = "human_agreement"
        else:
            final_score = score_text(genai.get("score (0-4)"))
            final_status = "genai"

        rows.append({
            "MCID": row["participant_id"],
            "creature": row["creature"],
            "question": row["question"],
            "answer": row["answer"],
            "answer_std": row["answer_std"],
            "genai_score": score_text(genai.get("score (0-4)")),
            "genai_confidence": clean(genai.get("confidence (0-100%)")),
            "genai_note": clean(genai.get("note (optional)")),
            "grader1_score": g1_score,
            "grader1_status": clean(g1.get("status")),
            "grader1_note": clean(g1.get("note (optional)")),
            "grader2_score": g2_score,
            "grader2_status": clean(g2.get("status")),
            "grader2_note": clean(g2.get("note (optional)")),
            "final_score": final_score,
            "final_status": final_status,
            "moment": row["moment"],
            "creature_id": row["creature_id"],
            "question_key": row["question_key"],
            "question_label": row["question_label"],
            "task_id": task_id,
            "occurrence_weight": occurrence_counts.get(key, 1) if row["answer_std"] else 1,
        })

    rows.sort(key=lambda item: (
        clean(item.get("MCID")),
        clean(item.get("moment")),
        clean(item.get("creature")).lower(),
        QUESTION_SORT_INDEX.get(clean(item.get("question")), 999),
    ))
    return rows, problems


def write_prompt_score_file(survey_rows: list[dict[str, str]], *, require_complete_review: bool = False) -> tuple[list[dict[str, Any]], list[str]]:
    rows, problems = build_prompt_score_rows(survey_rows, require_complete_review=require_complete_review)
    if rows and not problems:
        write_tsv(RETENTION_MERGED_PATH, MERGED_SCORE_FIELDNAMES, rows)
    return rows, problems

def refresh_retention_answers_from_genai(survey_rows: list[dict[str, str]]) -> tuple[int, list[str]]:
    """Regenerate retention_answers.tsv without adding scores.

    This helper name is kept for older callers, but retention_answers.tsv is now
    deliberately an answer-extraction file only. Scores live in the GenAI, grader,
    and merged score files.
    """
    prompt_rows = build_prompt_rows_from_survey(survey_rows)
    rows = build_retention_answer_rows(prompt_rows)
    write_tsv(RETENTION_ANSWERS_PATH, RETENTION_ANSWER_FIELDNAMES, rows)
    return len(rows), []
