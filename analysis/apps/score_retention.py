from __future__ import annotations

import csv
import datetime as dt
import hashlib
import json
import sys
import threading
import time
import webbrowser
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
import mimetypes
from urllib.parse import unquote, urlparse

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._logs_main import load_log_index
from helpers._main_overview import build_merged_dataset
from helpers._ret_main import build_retention_question_rows
from helpers._shared import DATA_DIR, LOG_DIR, RESOURCES_DIR, RETENTION_QUESTION_SPECS, RETENTION_RUBRICS_PATH, RETENTION_SCORES_PATH, SURVEY_EXPORT_PATH, STATIC_DIR, TEMPLATES_DIR, clean
from helpers._survey_io import detect_text_encoding, load_survey_export

RUBRIC_RESOURCE_PATH = RESOURCES_DIR / "retention_rubrics.json"
RUBRIC_PATH = RETENTION_RUBRICS_PATH
SCORE_BACKUP_DIR = REPO_ROOT / "score_backups"
GRADER_SCORE_TEMPLATE = "retention_scores_grader{grader}.tsv"
HOST = "127.0.0.1"
DEFAULT_PORT = 8765
TASK_LOG_BATCH_SIZE = 100

GRADER_SCORE_FIELDNAMES = [
    "task_id", "participant_id", "moment", "creature_id", "creature_name",
    "question_key", "question_label", "answer_hash", "grader", "score",
    "status", "note", "updated_at",
]

MERGED_SCORE_FIELDNAMES = [
    "task_id", "participant_id", "moment", "creature_id", "creature_name",
    "question_key", "question_label", "answer_hash",
    "grader1_score", "grader1_status", "grader1_note", "grader1_updated_at",
    "grader2_score", "grader2_status", "grader2_note", "grader2_updated_at",
    "final_score", "final_status",
]


def log_step(message: str) -> None:
    print(f"[score_ret] {message}", flush=True)


def utc_timestamp() -> str:
    return dt.datetime.now(dt.timezone.utc).astimezone().isoformat(timespec="seconds")


def backup_timestamp() -> str:
    return dt.datetime.now().strftime("%Y%m%d-%H%M%S")


def short_hash(value: object) -> str:
    return hashlib.sha256(clean(value).encode("utf-8")).hexdigest()[:16]


def stable_task_id(row: dict[str, Any]) -> str:
    raw = "|".join([
        clean(row.get("participant_id")),
        clean(row.get("moment")),
        clean(row.get("creature_id")),
        clean(row.get("question")),
    ])
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()[:24]


def load_json(path: Path) -> dict[str, Any]:
    if path.exists():
        return json.loads(path.read_text(encoding="utf-8"))
    raise FileNotFoundError(f"Missing JSON file: {path}")


def load_scoring_rubric() -> dict[str, Any]:
    """Load the bundled scoring-rubric resource used by the browser UI.

    Historically this app also looked in data/config/retention_rubrics.json. That
    made local stale config files silently override the bundled source-rubric HTML,
    which left the Instructions and All rubrics pages empty even when the updated
    app files were present. The scoring UI now uses the bundled resource as the
    source of truth so the copied Google-doc-style rubric tables always travel
    with the app code.
    """
    if RUBRIC_RESOURCE_PATH.exists():
        rubric = load_json(RUBRIC_RESOURCE_PATH)
        instructions_len = len(clean(rubric.get("instructions_html")))
        full_len = len(clean(rubric.get("full_rubric_html")))
        log_step(
            f"Loaded bundled rubric from {RUBRIC_RESOURCE_PATH} "
            f"({instructions_len:,} instruction chars; {full_len:,} full-rubric chars)."
        )
        return rubric

    # Development fallback only. In normal use resources/retention_rubrics.json
    # should exist and should not be overridden by data/config.
    log_step(f"Bundled rubric missing; falling back to {RUBRIC_PATH}.")
    return load_json(RUBRIC_PATH)


def read_tsv(path: Path) -> list[dict[str, str]]:
    if not path.exists():
        return []
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        return [dict(row) for row in csv.DictReader(handle, delimiter="\t")]


def write_delimited(path: Path, fieldnames: list[str], rows: list[dict[str, Any]], *, delimiter: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames, delimiter=delimiter, extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({field: clean(row.get(field)) for field in fieldnames})


def write_tsv(path: Path, fieldnames: list[str], rows: list[dict[str, Any]]) -> None:
    write_delimited(path, fieldnames, rows, delimiter="\t")


def write_csv(path: Path, fieldnames: list[str], rows: list[dict[str, Any]]) -> None:
    write_delimited(path, fieldnames, rows, delimiter=",")


def load_grader_scores(grader: int) -> dict[str, dict[str, str]]:
    rows = read_tsv(DATA_DIR / GRADER_SCORE_TEMPLATE.format(grader=grader))
    return {clean(row.get("task_id")): row for row in rows if clean(row.get("task_id"))}


def write_grader_scores(grader: int, rows_by_task_id: dict[str, dict[str, Any]]) -> None:
    rows = sorted(rows_by_task_id.values(), key=lambda row: clean(row.get("task_id")))
    write_tsv(DATA_DIR / GRADER_SCORE_TEMPLATE.format(grader=grader), GRADER_SCORE_FIELDNAMES, rows)


def load_scoring_tasks() -> list[dict[str, Any]]:
    started = time.perf_counter()
    log_step(f"Loading survey export from {SURVEY_EXPORT_PATH}...")
    survey_rows, _survey_header = load_survey_export(SURVEY_EXPORT_PATH)
    log_step(f"Loaded {len(survey_rows):,} survey row(s). Loading log index from {LOG_DIR}...")
    log_index = load_log_index(LOG_DIR)
    log_step(f"Loaded {len(log_index):,} log-index entries. Building merged participant dataset...")
    dataset = build_merged_dataset(survey_rows, log_index)
    participants = dataset["participants"]
    log_step(f"Merged dataset contains {len(participants):,} participant(s). Building retention answer rows...")
    answer_rows = build_retention_question_rows(participants)
    log_step(f"Retention builder returned {len(answer_rows):,} candidate answer row(s). Converting to scoring tasks...")

    tasks: list[dict[str, Any]] = []
    last_logged = 0
    for row in answer_rows:
        answer = clean(row.get("answer"))
        if not answer:
            continue

        task = {
            "participant_id": clean(row.get("participant_id")),
            "moment": clean(row.get("moment")),
            "creature_id": clean(row.get("creature_id")),
            "creature_name": clean(row.get("creature_name")),
            "question_key": clean(row.get("question")),
            "question_label": clean(row.get("question_label")),
            "answer": answer,
            "answer_hash": short_hash(answer),
        }
        task["task_id"] = stable_task_id({
            "participant_id": task["participant_id"],
            "moment": task["moment"],
            "creature_id": task["creature_id"],
            "question": task["question_key"],
        })
        tasks.append(task)

        if len(tasks) - last_logged >= TASK_LOG_BATCH_SIZE:
            start = last_logged + 1
            end = len(tasks)
            log_step(f"Tasks {start:,} - {end:,} done.")
            last_logged = end

    if len(tasks) and len(tasks) != last_logged:
        start = last_logged + 1
        end = len(tasks)
        log_step(f"Tasks {start:,} - {end:,} done.")

    by_task_id: dict[str, dict[str, Any]] = {}
    for task in tasks:
        by_task_id.setdefault(task["task_id"], task)
    deduped = list(by_task_id.values())
    elapsed = time.perf_counter() - started
    log_step(f"Scoring task build complete: {len(deduped):,} unique task(s) from {len(tasks):,} non-empty answer(s) in {elapsed:.1f}s.")
    return deduped


def deterministic_key(seed: str, task: dict[str, Any]) -> str:
    return hashlib.sha256((seed + "|" + task["task_id"]).encode("utf-8")).hexdigest()


def scoring_display_key(task: dict[str, Any]) -> tuple[Any, ...]:
    question_order = {key: index for index, (key, _label) in enumerate(RETENTION_QUESTION_SPECS)}
    moment_order = {"Immediate": 0, "Delayed": 1}
    return (
        question_order.get(clean(task.get("question_key")), 999),
        clean(task.get("creature_name")).lower(),
        moment_order.get(clean(task.get("moment")), 999),
        clean(task.get("participant_id")),
        clean(task.get("task_id")),
    )


def sort_for_scoring_display(tasks: list[dict[str, Any]]) -> list[dict[str, Any]]:
    return sorted(tasks, key=scoring_display_key)


def select_grader_tasks(tasks: list[dict[str, Any]], grader: int) -> list[dict[str, Any]]:
    started = time.perf_counter()
    log_step(f"Selecting display tasks for grader {grader} from {len(tasks):,} task(s)...")
    if grader == 1:
        selected_for_grader_1 = sort_for_scoring_display(tasks)
        log_step(f"Grader 1 task selection complete: {len(selected_for_grader_1):,} task(s) in {time.perf_counter() - started:.1f}s.")
        return selected_for_grader_1

    if grader != 2:
        raise ValueError("grader must be 1 or 2")

    # Grader 2 receives a deterministic 25% sample stratified by both
    # retention wave (Immediate/Delayed) and prompt type (img1/img2/name1/name2).
    # The sample selection remains deterministic, but the display order is grouped
    # by question so graders complete all Q1 answers before moving to Q2, etc.
    selected: list[dict[str, Any]] = []
    grouped: dict[tuple[str, str], list[dict[str, Any]]] = {}
    moments: set[str] = set()
    for task in tasks:
        moment = clean(task.get("moment"))
        question_key = clean(task.get("question_key"))
        if moment:
            moments.add(moment)
        grouped.setdefault((moment, question_key), []).append(task)

    for moment in sorted(moments):
        for question_key, _label in RETENTION_QUESTION_SPECS:
            group = grouped.get((moment, question_key), [])
            group = sorted(
                group,
                key=lambda task: deterministic_key(f"retention-grader2-{moment}-{question_key}-v2", task),
            )
            if not group:
                continue
            n_to_take = max(1, round(len(group) * 0.25))
            selected.extend(group[:n_to_take])
            log_step(f"Grader 2 sample {moment} / {question_key}: selected {n_to_take:,} of {len(group):,} task(s).")

    selected_for_grader_2 = sort_for_scoring_display(selected)
    log_step(f"Grader 2 task selection complete: {len(selected_for_grader_2):,} task(s) in {time.perf_counter() - started:.1f}s.")
    return selected_for_grader_2


def merge_score_files(all_tasks: list[dict[str, Any]], *, create_backup: bool = True) -> list[dict[str, Any]]:
    started = time.perf_counter()
    log_step("Merging grader score files into retention_scoring.tsv...")
    grader1 = load_grader_scores(1)
    grader2 = load_grader_scores(2)
    task_lookup = {task["task_id"]: task for task in all_tasks}
    all_task_ids = sorted(set(task_lookup) | set(grader1) | set(grader2))
    rows: list[dict[str, Any]] = []
    last_logged = 0

    for index, task_id in enumerate(all_task_ids, start=1):
        task = task_lookup.get(task_id, {})
        row1 = grader1.get(task_id, {})
        row2 = grader2.get(task_id, {})
        base = {
            "task_id": task_id,
            "participant_id": task.get("participant_id") or row1.get("participant_id") or row2.get("participant_id") or "",
            "moment": task.get("moment") or row1.get("moment") or row2.get("moment") or "",
            "creature_id": task.get("creature_id") or row1.get("creature_id") or row2.get("creature_id") or "",
            "creature_name": task.get("creature_name") or row1.get("creature_name") or row2.get("creature_name") or "",
            "question_key": task.get("question_key") or row1.get("question_key") or row2.get("question_key") or "",
            "question_label": task.get("question_label") or row1.get("question_label") or row2.get("question_label") or "",
            "answer_hash": task.get("answer_hash") or row1.get("answer_hash") or row2.get("answer_hash") or "",
            "grader1_score": row1.get("score", ""),
            "grader1_status": row1.get("status", ""),
            "grader1_note": row1.get("note", ""),
            "grader1_updated_at": row1.get("updated_at", ""),
            "grader2_score": row2.get("score", ""),
            "grader2_status": row2.get("status", ""),
            "grader2_note": row2.get("note", ""),
            "grader2_updated_at": row2.get("updated_at", ""),
        }

        if clean(base["grader1_status"]) == "graded" and clean(base["grader1_score"]):
            base["final_score"] = base["grader1_score"]
            base["final_status"] = "graded_from_grader1"
        elif clean(base["grader2_status"]) == "graded" and clean(base["grader2_score"]):
            base["final_score"] = base["grader2_score"]
            base["final_status"] = "graded_from_grader2_only"
        else:
            base["final_score"] = ""
            base["final_status"] = ""

        rows.append(base)

        if index - last_logged >= TASK_LOG_BATCH_SIZE:
            start = last_logged + 1
            end = index
            log_step(f"Merge rows {start:,} - {end:,} done.")
            last_logged = index

    if len(all_task_ids) and len(all_task_ids) != last_logged:
        start = last_logged + 1
        end = len(all_task_ids)
        log_step(f"Merge rows {start:,} - {end:,} done.")

    write_tsv(RETENTION_SCORES_PATH, MERGED_SCORE_FIELDNAMES, rows)
    if create_backup:
        SCORE_BACKUP_DIR.mkdir(parents=True, exist_ok=True)
        backup_path = SCORE_BACKUP_DIR / f"retention_scores-{backup_timestamp()}.tsv"
        write_tsv(backup_path, MERGED_SCORE_FIELDNAMES, rows)
        log_step(f"Backup written: {backup_path}")
    log_step(f"Score merge complete: {len(rows):,} row(s) in {time.perf_counter() - started:.1f}s.")
    return rows


def has_any_existing_grader_scores() -> bool:
    return bool(load_grader_scores(1) or load_grader_scores(2))


class RetentionScoringServer:
    def __init__(self, grader: int) -> None:
        started = time.perf_counter()
        log_step(f"Initialising retention scoring server for grader {grader}...")
        self.grader = grader
        log_step("Loading bundled source rubric JSON...")
        self.rubric = load_scoring_rubric()
        self.all_tasks = load_scoring_tasks()
        self.tasks = select_grader_tasks(self.all_tasks, grader)
        self.task_by_id = {task["task_id"]: task for task in self.tasks}
        log_step(f"Loading existing score file for grader {grader}...")
        self.scores_by_task_id = load_grader_scores(grader)
        self.lock = threading.RLock()
        self._task_payload_cache: bytes | None = None
        self._task_payload_cache_dirty = True

        if has_any_existing_grader_scores():
            log_step("Existing grader score file(s) found; refreshing merged score CSV without creating a launch backup.")
            merge_score_files(self.all_tasks, create_backup=False)
        else:
            log_step("No existing grader score file rows found; skipping startup score merge/write.")

        self.rebuild_task_payload_cache()
        log_step(f"Server state ready in {time.perf_counter() - started:.1f}s.")

    def task_payload(self) -> dict[str, Any]:
        with self.lock:
            scores = {task_id: self.public_score_row(row) for task_id, row in self.scores_by_task_id.items()}
        return {
            "grader": self.grader,
            "tasks": self.tasks,
            "scores": scores,
            "rubric": self.rubric,
            "questionOrder": [key for key, _label in RETENTION_QUESTION_SPECS],
            "questionLabels": {key: label for key, label in RETENTION_QUESTION_SPECS},
        }

    def rebuild_task_payload_cache(self) -> None:
        started = time.perf_counter()
        payload = self.task_payload()
        payload["progress"] = self.progress()
        self._task_payload_cache = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self._task_payload_cache_dirty = False
        log_step(
            f"Prepared /api/tasks payload: {len(self.tasks):,} task(s), "
            f"{len(self._task_payload_cache):,} byte(s), {time.perf_counter() - started:.1f}s."
        )

    def task_payload_bytes(self) -> bytes:
        with self.lock:
            if self._task_payload_cache is None or self._task_payload_cache_dirty:
                self.rebuild_task_payload_cache()
            return self._task_payload_cache or b"{}"

    def public_score_row(self, row: dict[str, Any]) -> dict[str, str]:
        return {
            "task_id": clean(row.get("task_id")),
            "score": clean(row.get("score")),
            "status": clean(row.get("status")),
            "note": clean(row.get("note")),
            "updated_at": clean(row.get("updated_at")),
        }

    def progress(self) -> dict[str, int]:
        relevant = [self.scores_by_task_id.get(task["task_id"], {}) for task in self.tasks]
        graded = sum(1 for row in relevant if row.get("status") == "graded")
        skipped = sum(1 for row in relevant if row.get("status") == "skipped")
        flagged = sum(1 for row in relevant if row.get("status") == "flagged")
        total = len(self.tasks)
        return {
            "total": total,
            "graded": graded,
            "skipped": skipped,
            "flagged": flagged,
            "to_be_graded": max(0, total - graded - skipped - flagged),
        }

    def save_score(self, payload: dict[str, Any]) -> dict[str, Any]:
        task_id = clean(payload.get("task_id"))
        action = clean(payload.get("action"))

        if task_id not in self.task_by_id:
            raise ValueError("Unknown task_id")
        if action not in {"grade", "skip", "flag"}:
            raise ValueError("Action must be grade, skip, or flag")

        task = self.task_by_id[task_id]
        now = utc_timestamp()
        score_text = clean(payload.get("score"))

        if action == "grade":
            try:
                score_int = int(score_text)
            except ValueError as exc:
                raise ValueError("Score must be 0, 1, 2, 3, or 4") from exc
            if score_int not in {0, 1, 2, 3, 4}:
                raise ValueError("Score must be 0, 1, 2, 3, or 4")
            status = "graded"
            score = str(score_int)
        elif action == "skip":
            status = "skipped"
            score = ""
        else:
            status = "flagged"
            score = ""

        row = {
            "task_id": task_id,
            "participant_id": task["participant_id"],
            "moment": task["moment"],
            "creature_id": task["creature_id"],
            "creature_name": task["creature_name"],
            "question_key": task["question_key"],
            "question_label": task["question_label"],
            "answer_hash": task["answer_hash"],
            "grader": str(self.grader),
            "score": score,
            "status": status,
            "note": clean(payload.get("note")),
            "updated_at": now,
        }

        with self.lock:
            self.scores_by_task_id[task_id] = row
            log_step(f"Saving {status} score for task {task_id} ({task['question_key']} / {task['creature_name']}).")
            write_grader_scores(self.grader, self.scores_by_task_id)
            self._task_payload_cache_dirty = True
            merge_score_files(self.all_tasks)

        return {"ok": True, "score": self.public_score_row(row), "progress": self.progress()}

def send_no_cache_headers(handler: BaseHTTPRequestHandler) -> None:
    handler.send_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
    handler.send_header("Pragma", "no-cache")
    handler.send_header("Expires", "0")


def json_bytes_response(handler: BaseHTTPRequestHandler, body: bytes, status: HTTPStatus = HTTPStatus.OK) -> None:
    handler.send_response(status)
    handler.send_header("Content-Type", "application/json; charset=utf-8")
    send_no_cache_headers(handler)
    handler.send_header("Content-Length", str(len(body)))
    handler.end_headers()
    handler.wfile.write(body)


def json_response(handler: BaseHTTPRequestHandler, payload: Any, status: HTTPStatus = HTTPStatus.OK) -> None:
    body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
    json_bytes_response(handler, body, status)


def file_response(handler: BaseHTTPRequestHandler, path: Path) -> None:
    if not path.exists() or not path.is_file():
        handler.send_error(HTTPStatus.NOT_FOUND, "File not found")
        return

    content_type = mimetypes.guess_type(str(path))[0] or "application/octet-stream"
    body = path.read_bytes()
    handler.send_response(HTTPStatus.OK)
    handler.send_header("Content-Type", content_type)
    send_no_cache_headers(handler)
    handler.send_header("Content-Length", str(len(body)))
    handler.end_headers()
    handler.wfile.write(body)


def build_handler(state: RetentionScoringServer) -> type[BaseHTTPRequestHandler]:
    class Handler(BaseHTTPRequestHandler):
        def log_message(self, format: str, *args: Any) -> None:  # noqa: A002
            print(f"[score_ret] {self.address_string()} - {format % args}")

        def do_GET(self) -> None:  # noqa: N802
            parsed = urlparse(self.path)
            path = parsed.path

            if path in {"/", "/index.html"}:
                template_path = TEMPLATES_DIR / "scoring_app.html"
                body = template_path.read_text(encoding="utf-8").encode("utf-8")
                self.send_response(HTTPStatus.OK)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                send_no_cache_headers(self)
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)
                return

            if path == "/api/tasks":
                started = time.perf_counter()
                body = state.task_payload_bytes()
                log_step(f"Serving /api/tasks: {len(body):,} byte(s).")
                json_bytes_response(self, body)
                log_step(f"Finished /api/tasks in {time.perf_counter() - started:.2f}s.")
                return

            if path == "/favicon.ico":
                self.send_response(HTTPStatus.NO_CONTENT)
                self.end_headers()
                return

            if path.startswith("/static/"):
                relative = Path(unquote(path.removeprefix("/static/")))
                safe_path = (STATIC_DIR / relative).resolve()
                static_root = STATIC_DIR.resolve()

                if static_root not in safe_path.parents and safe_path != static_root:
                    self.send_error(HTTPStatus.FORBIDDEN, "Forbidden")
                    return

                file_response(self, safe_path)
                return

            self.send_error(HTTPStatus.NOT_FOUND, "Unknown route")

        def do_POST(self) -> None:  # noqa: N802
            parsed = urlparse(self.path)

            if parsed.path != "/api/score":
                self.send_error(HTTPStatus.NOT_FOUND, "Unknown route")
                return

            try:
                length = int(self.headers.get("Content-Length", "0"))
                payload = json.loads(self.rfile.read(length).decode("utf-8") or "{}")
                json_response(self, state.save_score(payload))
            except Exception as exc:
                json_response(self, {"ok": False, "error": str(exc)}, HTTPStatus.BAD_REQUEST)

    return Handler


def parse_args(argv: list[str]) -> tuple[int | None, int]:
    grader: int | None = None
    port = DEFAULT_PORT

    for arg in argv:
        if arg.startswith("grader="):
            try:
                grader = int(arg.split("=", 1)[1])
            except ValueError:
                grader = None
        elif arg.startswith("port="):
            port = int(arg.split("=", 1)[1])

    return grader, port


def print_usage() -> None:
    print("Usage:")
    print("  python main.py score_ret grader=1")
    print("  python main.py score_ret grader=2")
    print("  python main.py score_ret grader=2 port=8766")


def main(argv: list[str] | None = None) -> int:
    args = argv if argv is not None else sys.argv[1:]
    grader, port = parse_args(args)

    if grader not in {1, 2}:
        print_usage()
        return 1

    state = RetentionScoringServer(grader)
    handler = build_handler(state)
    server = ThreadingHTTPServer((HOST, port), handler)
    url = f"http://{HOST}:{port}/"

    print(f"Retention scoring app for grader {grader}: {url}")
    print(f"Tasks available: {len(state.tasks)}")
    print("Press Ctrl+C to stop the server.")

    webbrowser.open(url)

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping retention scoring server.")
    finally:
        server.server_close()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
