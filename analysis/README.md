# analysis

This folder contains the code used to statistically analyse study results.

The repository does **not** include the resulting datasets.

## Important

These scripts only work **after you have run the experiment locally**.

They read files that are generated on your machine, especially:

- `./mods/custom/run/logs/study-checkpoints.log`
- `./mods/custom/src/main/java/io/github/u01234567/studycheckpoints/StudyCreatureCards.java`

The log file is created locally during play and is **not committed to GitHub**.

## Current script

### Summarise the last session

Run this from the `analysis` folder:

```bash
python main.py sum_last
```

This will:

1. read the main study log
2. read the configured creature cards
3. find the most recent session in the log
4. build a simple HTML summary
5. write the result to:

```text
./output/last_session_summary.html
```