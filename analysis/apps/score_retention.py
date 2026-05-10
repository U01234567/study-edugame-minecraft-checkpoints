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
from helpers._shared import DATA_DIR, LOG_DIR, RETENTION_QUESTION_SPECS, RETENTION_SCORES_PATH, SURVEY_EXPORT_PATH, STATIC_DIR, TEMPLATES_DIR, clean
from helpers._survey_io import detect_text_encoding, load_survey_export

RUBRIC_PATH = REPO_ROOT / "resources" / "retention_rubrics.json"
SCORE_BACKUP_DIR = REPO_ROOT / "score_backups"
GRADER_SCORE_TEMPLATE = "retention_scores_grader{grader}.tsv"
HOST = "127.0.0.1"
DEFAULT_PORT = 8765

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
    if not path.exists():
        raise FileNotFoundError(f"Missing rubric file: {path}")
    return json.loads(path.read_text(encoding="utf-8"))


def read_tsv(path: Path) -> list[dict[str, str]]:
    if not path.exists():
        return []
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        return [dict(row) for row in csv.DictReader(handle, delimiter="\t")]


def write_tsv(path: Path, fieldnames: list[str], rows: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames, delimiter="\t", extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({field: clean(row.get(field)) for field in fieldnames})


def load_grader_scores(grader: int) -> dict[str, dict[str, str]]:
    rows = read_tsv(DATA_DIR / GRADER_SCORE_TEMPLATE.format(grader=grader))
    return {clean(row.get("task_id")): row for row in rows if clean(row.get("task_id"))}


def write_grader_scores(grader: int, rows_by_task_id: dict[str, dict[str, Any]]) -> None:
    rows = sorted(rows_by_task_id.values(), key=lambda row: clean(row.get("task_id")))
    write_tsv(DATA_DIR / GRADER_SCORE_TEMPLATE.format(grader=grader), GRADER_SCORE_FIELDNAMES, rows)


def load_scoring_tasks() -> list[dict[str, Any]]:
    survey_rows, _survey_header = load_survey_export(SURVEY_EXPORT_PATH)
    log_index = load_log_index(LOG_DIR)
    dataset = build_merged_dataset(survey_rows, log_index)
    answer_rows = build_retention_question_rows(dataset["participants"])

    tasks: list[dict[str, Any]] = []
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

    by_task_id: dict[str, dict[str, Any]] = {}
    for task in tasks:
        by_task_id.setdefault(task["task_id"], task)
    return list(by_task_id.values())


def deterministic_key(seed: str, task: dict[str, Any]) -> str:
    return hashlib.sha256((seed + "|" + task["task_id"]).encode("utf-8")).hexdigest()


def select_grader_tasks(tasks: list[dict[str, Any]], grader: int) -> list[dict[str, Any]]:
    if grader == 1:
        return sorted(tasks, key=lambda task: deterministic_key("retention-grader1-order-v1", task))

    if grader != 2:
        raise ValueError("grader must be 1 or 2")

    # Grader 2 receives a deterministic 25% sample stratified by both
    # retention wave (Immediate/Delayed) and prompt type (img1/img2/name1/name2).
    # This keeps each of the eight wave × prompt strata represented at ~25%.
    selected: list[dict[str, Any]] = []
    moments = sorted({clean(task.get("moment")) for task in tasks if clean(task.get("moment"))})

    for moment in moments:
        for question_key, _label in RETENTION_QUESTION_SPECS:
            group = [
                task
                for task in tasks
                if clean(task.get("moment")) == moment and task.get("question_key") == question_key
            ]
            group = sorted(
                group,
                key=lambda task: deterministic_key(f"retention-grader2-{moment}-{question_key}-v2", task),
            )
            if not group:
                continue
            n_to_take = max(1, round(len(group) * 0.25))
            selected.extend(group[:n_to_take])

    return sorted(selected, key=lambda task: deterministic_key("retention-grader2-order-v2", task))


def merge_score_files(all_tasks: list[dict[str, Any]]) -> list[dict[str, Any]]:
    grader1 = load_grader_scores(1)
    grader2 = load_grader_scores(2)
    task_lookup = {task["task_id"]: task for task in all_tasks}
    all_task_ids = sorted(set(task_lookup) | set(grader1) | set(grader2))
    rows: list[dict[str, Any]] = []

    for task_id in all_task_ids:
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

    write_tsv(RETENTION_SCORES_PATH, MERGED_SCORE_FIELDNAMES, rows)
    SCORE_BACKUP_DIR.mkdir(parents=True, exist_ok=True)
    backup_path = SCORE_BACKUP_DIR / f"retention_scores-{backup_timestamp()}.tsv"
    write_tsv(backup_path, MERGED_SCORE_FIELDNAMES, rows)
    return rows


class RetentionScoringServer:
    def __init__(self, grader: int) -> None:
        self.grader = grader
        self.rubric = load_json(RUBRIC_PATH)
        self.all_tasks = load_scoring_tasks()
        self.tasks = select_grader_tasks(self.all_tasks, grader)
        self.task_by_id = {task["task_id"]: task for task in self.tasks}
        self.scores_by_task_id = load_grader_scores(grader)
        self.lock = threading.Lock()
        merge_score_files(self.all_tasks)

    def task_payload(self) -> dict[str, Any]:
        with self.lock:
            scores = {task_id: self.public_score_row(row) for task_id, row in self.scores_by_task_id.items()}
        return {
            "grader": self.grader,
            "tasks": self.tasks,
            "scores": scores,
            "rubric": self.rubric,
            "questionOrder": [key for key, _label in RETENTION_QUESTION_SPECS],
        }

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
        total = len(self.tasks)
        return {"total": total, "graded": graded, "skipped": skipped, "to_be_graded": max(0, total - graded - skipped)}

    def save_score(self, payload: dict[str, Any]) -> dict[str, Any]:
        task_id = clean(payload.get("task_id"))
        action = clean(payload.get("action"))

        if task_id not in self.task_by_id:
            raise ValueError("Unknown task_id")
        if action not in {"grade", "skip"}:
            raise ValueError("Action must be grade or skip")

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
        else:
            status = "skipped"
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
            write_grader_scores(self.grader, self.scores_by_task_id)
            merge_score_files(self.all_tasks)

        return {"ok": True, "score": self.public_score_row(row), "progress": self.progress()}
    
def json_response(handler: BaseHTTPRequestHandler, payload: Any, status: HTTPStatus = HTTPStatus.OK) -> None:
    body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
    handler.send_response(status)
    handler.send_header("Content-Type", "application/json; charset=utf-8")
    handler.send_header("Content-Length", str(len(body)))
    handler.end_headers()
    handler.wfile.write(body)


def file_response(handler: BaseHTTPRequestHandler, path: Path) -> None:
    if not path.exists() or not path.is_file():
        handler.send_error(HTTPStatus.NOT_FOUND, "File not found")
        return

    content_type = mimetypes.guess_type(str(path))[0] or "application/octet-stream"
    body = path.read_bytes()
    handler.send_response(HTTPStatus.OK)
    handler.send_header("Content-Type", content_type)
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
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)
                return

            if path == "/api/tasks":
                payload = state.task_payload()
                payload["progress"] = state.progress()
                json_response(self, payload)
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
