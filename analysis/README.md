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

1. Place the private raw files in `./raw/` and set `PUBLIC_ROUTE = False` in `main.py`.

2. Generate the stripped `/data/` files, GenAI base files, GenAI prompt, rubric HTML, and appendix PDFs:

   ```bash
   python main.py sum_merged
   ```

3. Give each external GenAI tool exactly these support files from `./data/config/` plus its matching configured TSV from `./data/`:

   ```text
   ./data/config/genai_prompt.txt
   ./data/config/scoring_rubrics.html
   ./data/retention_scores_genai1.tsv
   ./data/retention_scores_genai2.tsv
   ```

   With the current `AMOUNT_GENAI = 2`, the configured GenAI files are `retention_scores_genai1.tsv` and `retention_scores_genai2.tsv`. If `AMOUNT_GENAI = 1`, the configured file is the backward-compatible unsuffixed `retention_scores_genai.tsv`. Fill only `score (0-2)`, `confidence (0-100%)`, and `note (optional)` in the GenAI TSVs. Preserve the header and row order exactly.

4. After the completed GenAI TSVs are back in `./data/`, freeze the human review manifest and create all configured human base files:

   ```bash
   python main.py score_ret prepare
   ```

   This writes `./data/retention_review_tasks.tsv` plus `./data/retention_scores_grader1.tsv`, `./data/retention_scores_grader2.tsv`, etc. All human files are generated from the same manifest, so do not delete or regenerate the manifest unless the GenAI/source data have deliberately changed.

5. Run the human-validation app for each grader:

   ```bash
   python main.py score_ret grader=1
   python main.py score_ret grader=2
   ```

   If a grader works on another device, copy the whole `./data/` folder first. This keeps both human graders on the exact same frozen task IDs.

6. Copy all completed `retention_scores_grader{int}.tsv` files into the same `./data/` folder, set `PUBLIC_ROUTE = True`, and rebuild the final report:

   ```bash
   python main.py sum_merged
   ```

7. Use the Retention tab’s **Retention scoring checks** table as the procedural guide. Fix the first row that is not ✅. Later rows may be waiting only because an earlier prerequisite is incomplete. When disagreements remain, run the final adjudication app:

   ```bash
   python main.py resolve_disagreements
   ```

   The app edits only the `final_*` columns in `./data/retention_scores_merged.tsv`, auto-fills the safe conflict cases, and lets you manually finalise the remaining rows with a required note. Then rerun `python main.py sum_merged`.

The final/public route creates `./data/retention_scores_merged.tsv` from `survey_export.tsv` and every configured `retention_scores_genai*.tsv` / `retention_scores_grader*.tsv` source file once the configured source files are complete. If `retention_scores_merged.tsv` already exists, `sum_merged` treats it as a non-destructive manual adjudication workspace and does not rewrite it. The merged HTML is still rendered when scoring is incomplete, with the Retention-tab checklist showing the next action.

### I want to use or check the analysis scripts

Use `PUBLIC_ROUTE = True`.

This route does not require private `./raw/` files. It reads only the publishable files already present in `./data/` and renders the final merged summary. This is the route to use for checking the analysis scripts without access to raw private data.

In this route, final retention scoring uses the configured source files determined by `AMOUNT_GENAI` and `AMOUNT_HUMAN`:

```text
./data/retention_scores_genai*.tsv
./data/retention_scores_grader*.tsv
```

Unexpected matching GenAI or grader files are ignored and reported so stale files cannot silently affect the merge. If source files are missing, incomplete, or contain unresolved scoring disagreements, `sum_merged` still renders the report. The Retention tab shows a red notification listing what is missing and how to fix it.

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

### Prepare human retention scoring files

Run this after completed GenAI TSVs are back in `./data/`:

```bash
python main.py score_ret prepare
```

This creates the frozen `./data/retention_review_tasks.tsv` manifest and all configured human base files (`AMOUNT_HUMAN`, default 2). It does not open the browser.

### Score retention as a human grader

Run this after `score_ret prepare`:

```bash
python main.py score_ret grader=1
python main.py score_ret grader=2
```

Each grader file uses the same frozen manifest. If a second device is used, copy the full `./data/` folder before running the second grader.

### Resolve final retention disagreements

Run this after `retention_scores_merged.tsv` has been created and the Retention tab still shows unresolved final-score conflicts:

```bash
python main.py resolve_disagreements
```

Optional arguments:

```bash
python main.py resolve_disagreements port=8767
python main.py resolve_disagreements input=./data/retention_scores_merged.tsv port=8767
```

This opens the final adjudication app. On startup, it automatically resolves rows with a 3/4 majority and rows where the two human graders agree while the two GenAI sources disagree with each other. Remaining rows must be manually finalised with a score and note, or flagged for later review. Every save writes a timestamped safety backup before rewriting the active TSV.

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

With `PUBLIC_ROUTE = True`, this reads only publishable `./data/` files. It creates the initial merged retention-scoring file only when needed and the configured source TSVs are complete; if `./data/retention_scores_merged.tsv` already exists, it uses that file without rewriting it before calculating final statistics.

The merged summary is a standalone interactive HTML file. At generation time, it embeds the current report data, CSS, JavaScript, and conceptual-model image into the HTML output.

## Expected local input files

For the internal research-team route, private source files live in `./raw/`. `sum_merged` strips those files into publishable `./data/` files.

For the public/checking route, only `./data/` and committed resource files are needed.

| Path | Purpose |
| --- | --- |
| `./raw/` | Private local source files for the research-team route only; not required for `PUBLIC_ROUTE=True`. |
| `./data/survey_export.tsv` | Publishable Qualtrics export with immediate and delayed rows. |
| `./data/retention_answers.tsv` | Generated q_element-level answer-extraction file. First columns: `MCID`, `creature`, `q_element`, `answer`, `answer_std`. Q2 answer rows are duplicated across three fact elements; Q4 answer rows are duplicated across chapter/environment elements. No scores live here. |
| `./data/retention_scores_genai.tsv` or `./data/retention_scores_genai{int}.tsv` | Unique standardised non-empty q_element answers for external GenAI scoring. `AMOUNT_GENAI = 1` writes the unsuffixed file; higher values write numbered files. First columns: `q_element`, `creature`, `answer_std`. |
| `./data/retention_scores_grader{int}.tsv` | Human-validation score files generated from one frozen `retention_review_tasks.tsv` manifest. First columns: `q_element`, `creature`, `answer_std`; `task_id` is kept at the end for stable lookup. |
| `./data/retention_scores_merged.tsv` | q_element-level scoring audit created by `sum_merged` from survey, GenAI, and human score files. First columns: `MCID`, `creature`, `q_element`, `answer`, `answer_std`; source columns use configured labels such as `genai1_score`, `genai2_score`, `grader1_score`, and `grader2_score`; final manual-audit columns are at the end. |
| `./data/config/genai_prompt.txt` | Generated prompt for the external GenAI tool. |
| `./data/config/scoring_rubrics.html` | Generated rubric support file, derived directly from `resources/retention_rubrics.json`. |
| `./data/config/creature_info.pdf` | Generated creature-information appendix PDF, derived directly from `resources/retention_rubrics.json`. Not attached to GenAI; creature information is embedded in the rubric HTML. |
| `./data/config/scoring_rubrics.pdf` | Generated PDF version of the scoring rubrics for the manuscript appendix. |
| `./score_backups/*.tsv` | Timestamped copies written whenever a retention TSV is written. These are safety backups only; the code never restores from them automatically. |
| `./data/transcripts/*.csv` | Interview transcripts; each CSV should have `Speaker` and `Transcript` columns. |
| `./resources/interview_manifest.json` | Filename-level interview metadata; do not enter MCIDs here. |
| `./resources/static/conceptual-model-v00.06.png` | Conceptual model image embedded into the standalone merged HTML. |
| `./resources/retention_rubrics.json` | Canonical retention-rubric source. It stores the uploaded base HTML exactly in `source_html` and parsed q_element tables for the scoring app/PDF appendix. Do not hand-edit rubric content unless you are deliberately replacing the base rubric HTML. |

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

The Retention tab shows immediate and delayed retention answers by creature and question. When final scoring files are complete, it also displays the original answer, standardised answer, configured GenAI scores such as `genai1_score` and `genai2_score`, human scores, final status, final score, notes, and reliability summaries.

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

Retention scoring is GenAI-assisted and human-validated. The editable scoring sources are the GenAI/grader TSVs plus the `final_*` columns in `retention_scores_merged.tsv`. Do not manually edit any non-`final_*` column in `retention_scores_merged.tsv`; those columns are regenerated from `survey_export.tsv`, `retention_scores_genai*.tsv`, `retention_review_tasks.tsv`, and `retention_scores_grader*.tsv`.

### Scoring unit and score scale

The scoring unit is now the rubric-level `q_element`, not the original survey prompt. The generated elements are:

```text
Q1_name
Q2_fact1
Q2_fact2
Q2_fact3
Q3_looks
Q4_chapter
Q4_env
```

Q2 creates three rows with the same raw answer and different fact elements. Q4 creates two rows with the same raw answer and different location elements. Every score is an integer `0`, `1`, or `2`.

Participant retention scores are calculated by first reducing the elements to four components: `Q1_name`, the mean of `Q2_fact1`–`Q2_fact3`, `Q3_looks`, and the mean of `Q4_chapter`–`Q4_env`. The final immediate/delayed retention score is the mean of available administered components, normalised to 0–1. This prevents the three fact rubrics and two location rubrics from overweighting the final retention score.

### File roles

| File | Created/updated by | Role |
| --- | --- | --- |
| `./data/retention_answers.tsv` | `sum_merged` with `PUBLIC_ROUTE=False` | q_element-level answer extraction only. It contains `MCID`, `creature`, `q_element`, `answer`, and `answer_std`. |
| `./data/retention_scores_genai*.tsv` | `sum_merged` with `PUBLIC_ROUTE=False`, then filled externally | Unique non-empty q_element answers for GenAI scoring. With `AMOUNT_GENAI = 2`, the files are `retention_scores_genai1.tsv` and `retention_scores_genai2.tsv`. |
| `./data/retention_review_tasks.tsv` | `score_ret prepare` after GenAI scoring | Frozen human review manifest. Both human grader files are generated from this manifest so both humans grade exactly the same answers. |
| `./data/retention_scores_grader1.tsv`, `./data/retention_scores_grader2.tsv` | `score_ret grader=1` / `score_ret grader=2` | Human-validation scores. With `AMOUNT_HUMAN = 2`, both files are expected before the final merge. |
| `./data/retention_scores_merged.tsv` | `sum_merged` with `PUBLIC_ROUTE=True`, then optionally `resolve_disagreements` | q_element-level scoring audit used for retention statistics and the merged report. Source-score columns are generated from configured source labels such as `genai1`, `genai2`, `grader1`, and `grader2`; only columns whose names start with `final_` are intended for manual/adjudication edits. |
| `./data/config/genai_prompt.txt` | generated from `./resources/retention_genai_prompt.txt` | Prompt text to paste into the external GenAI tool. |
| `./data/config/scoring_rubrics.html` | generated from `./resources/retention_rubrics.json` | The HTML rubric file attached to GenAI and used by the human scoring app. |
| `./data/config/scoring_rubrics.pdf` | generated from the same rubric HTML | Manuscript appendix version of the rubrics. |
| `./data/config/creature_info.pdf` | generated from `./resources/retention_rubrics.json` | Manuscript appendix version of the creature information; not part of the GenAI package. |

Every time one of the retention TSV files above is written, the existing file is first copied to `./score_backups/` when it already exists. These backups are only for manual recovery. The code always reads the active file in `./data/` and never creates a missing `./data/` file from backups.

### Step 1: prepare GenAI base files

With `PUBLIC_ROUTE = False`, run:

```bash
python main.py sum_merged
```

This creates or refreshes:

```text
./data/retention_answers.tsv
./data/retention_scores_genai1.tsv
./data/retention_scores_genai2.tsv
./data/config/genai_prompt.txt
./data/config/scoring_rubrics.html
./data/config/scoring_rubrics.pdf
./data/config/creature_info.pdf
```

With the current `AMOUNT_GENAI = 2`, the generated GenAI TSVs are `retention_scores_genai1.tsv` and `retention_scores_genai2.tsv`. If `AMOUNT_GENAI = 1`, the generated TSV is `retention_scores_genai.tsv`; if higher, files are numbered through `retention_scores_genai{n}.tsv`. The GenAI prompt lives in `./resources/retention_genai_prompt.txt` and is copied to `./data/config/genai_prompt.txt` when the support files are generated. The prompt intentionally asks GenAI to use exactly two files: the relevant `retention_scores_genai*.tsv` file and `scoring_rubrics.html`.

### Step 2: fill GenAI scores

Use `./data/config/genai_prompt.txt` with each GenAI model, attaching:

```text
./data/retention_scores_genai*.tsv
./data/config/scoring_rubrics.html
```

The GenAI tool should fill only:

```text
score (0-2)
confidence (0-100%)
note (optional)
```

Notes should be left empty unless they are genuinely useful for ambiguity, uncertainty, borderline scoring, missing/unclear source information, suspected rubric tension, or a reason a human should inspect the row.

### Step 3: generate and complete human base files

After the generated `retention_scores_genai*.tsv` file(s) have been filled, run:

```bash
python main.py score_ret prepare
```

This freezes `./data/retention_review_tasks.tsv` and creates all configured human base files (`AMOUNT_HUMAN = 2` by default) without opening the browser. Both `retention_scores_grader1.tsv` and `retention_scores_grader2.tsv` are generated from exactly the same task manifest. You can then run:

```bash
python main.py score_ret grader=1
python main.py score_ret grader=2
```

If a second device is used, copy the full `./data/` folder before running the second grader.

Each grader receives the same blinded review queue. GenAI score, confidence, and note are hidden until the grader has submitted their own score for that item. Human graders enter only a score and optional note; no human confidence column is used.

The review queue is the union of:

1. a deterministic stratified 25% validation sample of unique non-empty GenAI rows;
2. every extra GenAI row below `GENAI_LOW_CONFIDENCE_THRESHOLD = 80`;
3. every extra GenAI row with a GenAI note.

GenAI disagreements are not added to the human queue. They remain visible in the merged adjudication workflow and are resolved through `python main.py resolve_disagreements` after the initial merged file is created.

### Step 4: final rebuild, checks, and statistics

With `PUBLIC_ROUTE = True`, run:

```bash
python main.py sum_merged
```

This creates or uses:

```text
./data/retention_scores_merged.tsv
./output/merged_summary.html
```

If `./data/retention_scores_merged.tsv` does not exist, `sum_merged` creates it only after the configured GenAI and human-review TSVs are complete. If it already exists, `sum_merged` uses it as the manual adjudication workspace and does not rewrite it. The command still attempts to write the HTML report when retention scoring is incomplete. The Retention tab includes a “Retention scoring checks” block showing whether expected GenAI files are present, expected human files are present, the frozen manifest exists, all source scores are valid `0`–`2`, all q_element values are expected, final scores are complete, and any critical merge problems remain.

Final statistics use only rows whose `final_score` is a valid integer `0`–`2`. If conflicts or missing scores remain, unresolved rows are highlighted and the report explains what still needs to be fixed. Use `python main.py resolve_disagreements` to adjudicate final-score conflicts, then rerun `python main.py sum_merged`.

### Manual final-score audit columns

`retention_scores_merged.tsv` ends with the manual-audit columns:

| Column | Meaning | Edit by hand? |
| --- | --- | --- |
| `final_status` | Machine-readable final adjudication state, for example `auto_blank`, `four_way_agreement`, `genai_agreement`, `needs_adjudication`, `auto_majority_3_of_4`, `auto_human_agreement`, `manual_adjudicated`, or `flagged_for_review`. | Usually no. The adjudication app writes this when resolving or flagging rows. |
| `final_score` | The score used for retention statistics when it is a valid integer `0`–`2`. If scores are missing or disagree, this is `[resolve conflict]` until manually resolved. | Yes, but preferably through `python main.py resolve_disagreements` for unresolved rows. Replace `[resolve conflict]` with `0`, `1`, or `2`. |
| `final_note_auto` | Short automatic explanation of why the row was resolved or why it needs manual adjudication, for example `four-way agreement`, `GenAI source disagreement`, or `missing scores: genai2`. | Usually no. This is generated by `sum_merged`. |
| `final_note_manual` | Your manual rationale for unresolved rows. Automatically resolved rows are prefilled with an em dash (`—`). | Yes, for rows where `final_score` had to be resolved manually. The adjudication app requires a note before finalising a manual score. |

Initial automatic `final_score` rules are deliberately simple: blank administered answers are scored `0`; if all required available scores agree, `final_score` is filled with the shared score; otherwise `final_score` remains `[resolve conflict]` for adjudication. The `resolve_disagreements` app then auto-fills two safe conflict classes before showing the remaining queue: rows with a 3/4 majority and rows where both human graders agree while the two GenAI sources disagree with each other.

## Development checks

Run syntax checks after changing Python files:

```bash
python -m py_compile main.py apps/resolve_disagreements.py apps/score_retention.py apps/summarise_merged.py helpers/_logs_main.py helpers/_interviews_main.py helpers/_ret_main.py helpers/_retention_coding.py helpers/_shared.py
```

Check JavaScript syntax if Node is available:

```bash
node --check resources/static/merged_app.js
node --check resources/static/scoring_app.js
node --check resources/static/resolve_disagreements_app.js
```

Regenerate the merged summary after changing report code or static assets:

```bash
python main.py sum_merged
```