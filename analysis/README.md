# analysis

This folder contains the code used to inspect and analyse study results.

Generated HTML files may contain participant-level information. Treat generated summaries as data files, not as harmless visualisations.

## Important

These scripts only work after you have run the experiment locally and exported the required local data files.

Do not commit local participant data, study logs, survey exports, retention scores, interview transcripts, or generated participant-level HTML outputs unless the repository is explicitly intended to store those files under the approved data-management plan.

## Data route toggle

Set the global route toggle near the top of `main.py`:

```python
PUBLIC_ROUTE = True
```

### I am part of the research team

Use `PUBLIC_ROUTE = False`.

This route starts from private local source files in `./raw/`. It rebuilds the stripped publishable `./data/` folder, prepares retention-answer files for GenAI-assisted scoring, and then renders the merged summary from `./data/`.

Typical retention workflow:

1. Place the private raw files in `./raw/`.

2. Run:

   ```bash
   python main.py sum_merged
   ```

3. Use the generated prompt/support files in `./data/config/` to fill `./data/retention_scores_genai.tsv` with the external GenAI tool.

4. Run the human-validation app for both graders:

   ```bash
   python main.py score_ret grader=1
   python main.py score_ret grader=2
   ```

5. If graders worked on different devices, copy both `retention_scores_grader1.tsv` and `retention_scores_grader2.tsv` into the same `./data/` folder.

6. Switch `PUBLIC_ROUTE = True`.

7. Run:

   ```bash
   python main.py sum_merged
   ```

The final/public route will rebuild `./data/retention_scores_merged.tsv` from `survey_export.tsv`, `retention_scores_genai.tsv`, and both grader files before calculating final retention statistics.

### I want to use or check the analysis scripts

Use `PUBLIC_ROUTE = True`.

This route does not require private `./raw/` files. It reads only the publishable files already present in `./data/` and renders the final merged summary. This is the route to use for checking the analysis scripts without access to raw private data.

In this route, final retention statistics require the completed scoring files:

```text
./data/retention_scores_genai.tsv
./data/retention_scores_grader1.tsv
./data/retention_scores_grader2.tsv
```

If these files are missing, incomplete, or contain unresolved human-scoring disagreements, `sum_merged` stops before calculating final retention statistics.

## Current commands


### Decrypt encrypted Minecraft Study logs

Run this from the `analysis` folder:

```bash
python main.py decrypt_logs
```

This reads the newest `.age` file from:

```text
./logs/
```

It decrypts it with your local age private key, writes the decrypted zip into `./logs/`, extracts files into `./logs/`, and does not overwrite existing files.

The default private key path is:

```text
~/.minecraft-study/minecraft-study-logs-age-key.txt
```

You can override it:

```bash
python main.py decrypt_logs identity=C:/Users/YOU/.minecraft-study/minecraft-study-logs-age-key.txt
```

### Summarise the last session

Run this from the `analysis` folder:

```bash
python main.py sum_last
```

This writes:

```text
./output/last_session_summary.html
```

### Summarise the survey export

Run this from the `analysis` folder:

```bash
python main.py sum_survey
```

This reads:

```text
./data/survey_export.tsv
```

and writes:

```text
./output/survey_summary.html
```

### Build the standalone merged summary

Run this from the `analysis` folder:

```bash
python main.py sum_merged
```

This writes:

```text
./output/merged_summary.html
```

With `PUBLIC_ROUTE = False`, this first rebuilds publishable `./data/` files from private `./raw/` files and prepares the GenAI retention-scoring files.

With `PUBLIC_ROUTE = True`, this reads only publishable `./data/` files and rebuilds final retention scoring before calculating final statistics.

The merged summary is a standalone interactive HTML file. At generation time, it embeds the current report data, CSS, JavaScript, and conceptual-model image into the HTML output.

## Expected local input files

For the internal research-team route, private source files live in `./raw/`. `sum_merged` strips those files into publishable `./data/` files.

For the public/checking route, only `./data/` and committed resource files are needed.

| Path | Purpose |
| --- | --- |
| `./raw/` | Private local source files for the research-team route only; not required for `PUBLIC_ROUTE=True`. |
| `./data/survey_export.tsv` | Publishable Qualtrics export with immediate and delayed rows. |
| `./data/retention_answers.tsv` | Generated prompt-level answer-extraction file. First columns: `MCID`, `creature`, `question`, `answer`, `answer_std`. No scores live here. |
| `./data/retention_scores_genai.tsv` | Unique standardised non-empty answers for external GenAI scoring. First columns: `question`, `creature`, `answer_std`. |
| `./data/retention_scores_grader1.tsv` | Grader 1 human-validation score file. First columns: `question`, `creature`, `answer_std`; `task_id` is kept at the end for stable lookup. |
| `./data/retention_scores_grader2.tsv` | Grader 2 human-validation score file. First columns: `question`, `creature`, `answer_std`; `task_id` is kept at the end for stable lookup. |
| `./data/retention_scores_merged.tsv` | Generated final prompt-level scoring file rebuilt by `sum_merged` from survey, GenAI, and human score files. First columns: `MCID`, `creature`, `question`, `answer`, `answer_std`. |
| `./data/config/genai_prompt.txt` | Generated prompt for the external GenAI tool. |
| `./data/config/scoring_rubrics.html` | Generated rubric support file, derived directly from `resources/retention_rubrics.json`. |
| `./data/config/creature_info.html` | Generated creature-information support file, derived directly from `resources/retention_rubrics.json`. |
| `./score_backups/*.tsv` | Timestamped copies written whenever a retention TSV is written. These are safety backups only; the code never restores from them automatically. |
| `./data/transcripts/*.csv` | Interview transcripts; each CSV should have `Speaker` and `Transcript` columns. |
| `./resources/interview_manifest.json` | Filename-level interview metadata; do not enter MCIDs here. |
| `./resources/static/conceptual-model-v00.06.png` | Conceptual model image embedded into the standalone merged HTML. |
| `./resources/retention_rubrics.json` | Editable retention rubric and creature facts for the scoring app. |

The generated merged HTML displays relative paths only, such as `./data/survey_export.tsv`, so that private local filesystem paths are not exposed.

## Merged summary structure

The merged summary contains these tabs:

* Main
* Retention
* Cognitive load
* Engagement
* Perceived control
* Game logs
* Interviews
* Inferential statistics

The Main tab includes the conceptual model, research questions and hypotheses, condition summaries, participant-level checks, exclusion summaries, and merge-audit tables.

The Retention tab shows immediate and delayed retention answers by creature and question. When final scoring files are complete, it also displays the original answer, standardised answer, GenAI score, human scores, final score, final source, notes, and reliability summaries.

The Cognitive load, Engagement, and Perceived control tabs contain item-level and construct-level summaries. Figures are rendered as standalone SVGs with embedded legends and numeric labels so they can be copied into a manuscript more easily.

The Game logs tab includes time-use summaries, time to sixth creature, optional-pause choices, and a detailed log table. The detailed log table has sticky headers and summary rows at the end.

The Interviews tab reads CSV transcripts from `./data/transcripts/`. The `Speaker` column should contain either `Researcher` or an MCID. A single interview CSV can contain one or more MCID speakers. The app infers MCIDs from the CSV and uses `./resources/interview_manifest.json` only to map filenames to selection categories and notes.

The Inferential statistics tab is generated from:

```text
./helpers/_stats_main.py
```

It places the calculation audit trail before the results, then reports the preregistered planned-contrast models, mediation checks, factor-analysis/scale checks, collection-location context, and covariate robustness checks. Review this helper file when checking the inferential calculations. We filled `./resources/collection_locations.json` with `Creative Space` or `Living Room` for each data-collection date; `REMOTE = 1` participants are coded as `At home` automatically.

## Current inclusion checks

The merged summary excludes a participant when one or more of the following applies:

* survey or log start date is before 8 May 2026
* survey `Progress` is not `100`
* no matching study log was found for the survey `MCID`
* the log does not contain `consent_choice=agree_and_continue`
* the log does not show `chapter_completed` for Chapters 0, 1, 2, and 3
* the log does not show at least one `creature_card_closed` event in each learning chapter: Chapter 1, Chapter 2, and Chapter 3

The Main tab reports excluded MCIDs and the reason for exclusion.

## Blind retention scoring

Retention scoring is GenAI-assisted and human-validated. The editable scoring sources are separate from the final merged scoring output. Do not manually edit `retention_scores_merged.tsv`; fix one of the source files and rerun `sum_merged`.

### File roles

| File | Created/updated by | Role |
| --- | --- | --- |
| `./data/retention_answers.tsv` | `sum_merged` with `PUBLIC_ROUTE=False` | Prompt-level answer extraction only. It contains `MCID`, `creature`, `question`, `answer`, and `answer_std`. |
| `./data/retention_scores_genai.tsv` | `sum_merged` with `PUBLIC_ROUTE=False`, then filled externally | Unique non-empty answers that the external GenAI tool scores. |
| `./data/retention_scores_grader1.tsv` | `score_ret grader=1` | Human-validation scores from grader 1. |
| `./data/retention_scores_grader2.tsv` | `score_ret grader=2` | Human-validation scores from grader 2. |
| `./data/retention_scores_merged.tsv` | `sum_merged` with `PUBLIC_ROUTE=True` | Final prompt-level scoring audit used for retention statistics and the merged report. |

Every time one of the retention TSV files above is written, a timestamped copy is also written to `./score_backups/`. These backups are only for manual recovery. The code always reads the active file in `./data/` and never creates a missing `./data/` file from backups.

### Answer standardisation

`answer_std` is intentionally conservative. The code:

* strips leading/trailing whitespace
* normalises Unicode with NFKC
* lowercases the answer
* collapses repeated whitespace to a single space

It does not remove punctuation, correct spelling, remove stopwords, or perform fuzzy matching. The aim is to merge obvious duplicates without changing meaning.

### Step 1: prepare files

With `PUBLIC_ROUTE = False`, run:

```bash
python main.py sum_merged
```

This creates or refreshes:

```text
./data/retention_answers.tsv
./data/retention_scores_genai.tsv
./data/config/genai_prompt.txt
./data/config/scoring_rubrics.html
./data/config/creature_info.html
```

`retention_answers.tsv` contains one row for each retention prompt that was actually administered. If a shown prompt was left blank, the row is kept with an empty `answer` and empty `answer_std`. Blank administered answers are later scored 0 in the merged scoring file.

`retention_scores_genai.tsv` contains unique non-empty standardised answers. The duplicate rule is hybrid: rows are normally collapsed by `question + answer_std`, but if that same combination occurs for multiple creatures, it is split by creature to avoid assigning a score that is correct for one creature to another creature.

The support HTML files are generated directly from `resources/retention_rubrics.json`; do not maintain separate hand-written rubric copies.

### Step 2: fill GenAI scores

Use `./data/config/genai_prompt.txt` with the external GenAI tool, attaching:

```text
./data/retention_scores_genai.tsv
./data/config/scoring_rubrics.html
./data/config/creature_info.html
```

The GenAI tool should fill only:

```text
score (0-4)
confidence (0-100%)
note (optional)
```

Notes should be left empty unless they are genuinely useful for ambiguity, uncertainty, borderline scoring, missing/unclear source information, suspected rubric tension, or a reason a human should inspect the row.

### Step 3: human validation

After `retention_scores_genai.tsv` has been filled, run the scoring app for both graders:

```bash
python main.py score_ret grader=1
python main.py score_ret grader=2
```

Both graders receive the same blinded review queue. GenAI score, confidence, and note are hidden until the grader has submitted their own score for that item.

The number shown as review tasks in the app consists of the union of:

1. a deterministic stratified 25% validation sample of unique non-empty GenAI rows;
2. every extra GenAI row below the low-confidence threshold;
3. every extra GenAI row with a GenAI note.

If a row belongs to more than one group, it appears only once. The app does not tell the grader whether a specific item was selected because of the 25% sample, low confidence, a note, or multiple reasons.

The 25% sample is stratified by `question` and GenAI confidence bucket. Current buckets are `00-59`, `60-79`, `80-100`, and `missing`. Within each stratum, rows are ordered with a deterministic SHA-256 hash of the task id and a fixed seed, then the first rounded 25% is selected, with at least one row selected from each non-empty stratum.

Blank administered answers are automatically scored 0 and are not shown in the scoring app.

The scoring app writes only the grader-specific human-validation files:

```text
./data/retention_scores_grader1.tsv
./data/retention_scores_grader2.tsv
```

The scoring app does not create or update `./data/retention_scores_merged.tsv`. That file is generated by `sum_merged`.

### Step 4: final rebuild and statistics

With `PUBLIC_ROUTE = True`, run:

```bash
python main.py sum_merged
```

This rebuilds:

```text
./data/retention_scores_merged.tsv
```

from:

```text
./data/survey_export.tsv
./data/retention_scores_genai.tsv
./data/retention_scores_grader1.tsv
./data/retention_scores_grader2.tsv
```

Final retention statistics are blocked until required human scores are present and human disagreements are resolved.

## Development checks

Run syntax checks after changing Python files:

```bash
python -m py_compile main.py apps/score_retention.py apps/summarise_merged.py helpers/_logs_main.py helpers/_interviews_main.py helpers/_ret_main.py helpers/_retention_coding.py helpers/_shared.py
```

Check JavaScript syntax if Node is available:

```bash
node --check resources/static/merged_app.js
node --check resources/static/scoring_app.js
```

Regenerate the merged summary after changing report code or static assets:

```bash
python main.py sum_merged
```