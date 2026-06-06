from __future__ import annotations

import datetime as dt
import json
import mimetypes
import sys
import threading
import time
import webbrowser
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import unquote, urlparse

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._delayed_response_filter import (  # noqa: E402
    DELAYED_INCLUDED_COLUMN,
    build_delayed_response_checklist_from_annotations,
    delayed_included_column_present,
    delayed_included_values_missing,
)
from helpers._retention_coding import (  # noqa: E402
    CREATURE_INFO_HTML_PATH,
    GENAI_PROMPT_PATH,
    GENAI_SCORES_PATH,
    GRADER1_SCORES_PATH,
    GRADER2_SCORES_PATH,
    LOW_CONFIDENCE_THRESHOLD,
    SCORING_RUBRICS_HTML_PATH,
    VALIDATION_SAMPLE_FRACTION,
    build_prompt_rows_from_survey,
    build_review_tasks,
    load_genai_scores,
    load_grader_scores,
    score_is_valid,
    score_text,
    write_grader_scores,
)
from helpers._shared import RESOURCES_DIR, RETENTION_QUESTION_SPECS, STATIC_DIR, SURVEY_EXPORT_PATH, TEMPLATES_DIR, clean  # noqa: E402
from helpers._survey_io import load_survey_export  # noqa: E402

RUBRIC_RESOURCE_PATH = RESOURCES_DIR / "retention_rubrics.json"
HOST = "127.0.0.1"
DEFAULT_PORT = 8765

GRADER_PATHS = {
    1: GRADER1_SCORES_PATH,
    2: GRADER2_SCORES_PATH,
}


def log_step(message: str) -> None:
    print(f"[score_ret] {message}", flush=True)


def utc_timestamp() -> str:
    return dt.datetime.now(dt.timezone.utc).astimezone().isoformat(timespec="seconds")


def load_json(path: Path) -> dict[str, Any]:
    if path.exists():
        return json.loads(path.read_text(encoding="utf-8"))
    raise FileNotFoundError(f"Missing JSON file: {path}")


def load_scoring_rubric() -> dict[str, Any]:
    rubric = load_json(RUBRIC_RESOURCE_PATH)
    return rubric


def require_delayed_included_column(survey_rows: list[dict[str, str]], header: list[str], survey_path: Path) -> None:
    if not delayed_included_column_present(header):
        raise RuntimeError(
            f"{survey_path} is missing the required {DELAYED_INCLUDED_COLUMN!r} column. "
            "score_ret only reads /data/, so run sum_merged once with PUBLIC_ROUTE=False to regenerate "
            "/data/survey_export.tsv with persisted delayed-response inclusion flags."
        )
    missing_flags = delayed_included_values_missing(survey_rows)
    if missing_flags:
        preview = ", ".join(missing_flags[:20])
        suffix = "..." if len(missing_flags) > 20 else ""
        raise RuntimeError(
            f"{survey_path} has DELAYED row(s) without a true/false {DELAYED_INCLUDED_COLUMN!r} value: "
            f"{preview}{suffix}. Regenerate /data/ with PUBLIC_ROUTE=False."
        )


def required_prompt_files_message() -> str:
    return (
        "score_ret cannot start yet: GenAI scoring is not ready. "
        "Fill data/retention_scores_genai.tsv first. Use data/config/genai_prompt.txt and attach "
        "retention_scores_genai.tsv, data/config/scoring_rubrics.html, and data/config/creature_info.html."
    )


def human_readable_genai_error(problems: list[str]) -> str:
    if not problems:
        return required_prompt_files_message()

    if any(problem.startswith("Missing ") for problem in problems):
        return required_prompt_files_message() + " Run sum_merged with PUBLIC_ROUTE=False if the prompt files do not exist yet."

    if any(" is empty" in problem for problem in problems):
        return required_prompt_files_message() + " The file exists, but it is empty."

    score_confidence_problem_count = sum(
        1
        for problem in problems
        if "score (0-4) must be" in problem or "confidence (0-100%) must be" in problem
    )
    mostly_unfilled = score_confidence_problem_count >= max(1, len(problems) * 0.8)
    if mostly_unfilled:
        return (
            required_prompt_files_message()
            + " The file exists, but the score/confidence columns appear to be empty or unfinished."
        )

    preview = " | ".join(problems[:5])
    suffix = "" if len(problems) <= 5 else f" | ... plus {len(problems) - 5} more problem(s)."
    return (
        required_prompt_files_message()
        + " The file has invalid values: "
        + preview
        + suffix
    )


def append_scoring_workflow_instructions(rubric: dict[str, Any], task_count: int, stats: dict[str, Any]) -> dict[str, Any]:
    copy = dict(rubric)
    existing = clean(copy.get("instructions_html"))
    workflow = f"""
    <section class="workflow-instructions">
      <h1>Human validation workflow</h1>
      <p>You are scoring a blinded review queue of unique standardised retention answers. GenAI scores, confidence values, and notes are hidden while you score so that the validation remains independent.</p>
      <ul>
        <li>The queue contains a deterministic stratified {int(VALIDATION_SAMPLE_FRACTION * 100)}% validation sample of unique non-empty answers.</li>
        <li>It also contains extra GenAI answers below the low-confidence threshold ({LOW_CONFIDENCE_THRESHOLD:g}%).</li>
        <li>It also contains every answer for which GenAI added a note, because those notes should signal ambiguity, uncertainty, a borderline score, or a possible rubric issue.</li>
        <li>If an answer belongs to multiple review groups, it still appears only once.</li>
        <li>Duplicates are collapsed before human scoring: normally by question + standardised answer, but split by creature when the same question + answer occurs for multiple creatures.</li>
        <li>Blank administered answers are not shown here; they are automatically scored 0.</li>
        <li>Do not worry about whether an item is from the validation sample, the low-confidence extra-check set, or the GenAI-note extra-check set. Score each row using the rubric only.</li>
      </ul>
      <p><strong>Current queue:</strong> {task_count:,} review task(s) for this grader. Source data: {stats.get('prompt_rows', 0):,} administered prompt row(s), {stats.get('unique_nonblank_answers', 0):,} unique non-empty GenAI row(s), {stats.get('blank_prompt_rows', 0):,} blank administered prompt row(s).</p>
    </section>
    """
    copy["instructions_html"] = workflow + existing
    return copy


def scoring_display_key(task: dict[str, Any]) -> tuple[Any, ...]:
    question_order = {key: index for index, (key, _label) in enumerate(RETENTION_QUESTION_SPECS)}
    return (
        question_order.get(clean(task.get("question_key")), 999),
        clean(task.get("creature_name") or task.get("creature")).lower(),
        clean(task.get("answer_std")),
        clean(task.get("task_id")),
    )


def public_score_row(row: dict[str, Any]) -> dict[str, str]:
    return {
        "task_id": clean(row.get("task_id")),
        "score": clean(row.get("score (0-4)")),
        "status": clean(row.get("status")),
        "note": clean(row.get("note (optional)")),
        "updated_at": clean(row.get("updated_at")),
    }


class RetentionScoringServer:
    def __init__(self, grader: int) -> None:
        started = time.perf_counter()
        log_step(f"Initialising retention scoring server for grader {grader}...")
        self.grader = grader
        self.grader_path = GRADER_PATHS[grader]

        log_step(f"Loading survey export from {SURVEY_EXPORT_PATH}...")
        self.survey_rows, survey_header = load_survey_export(SURVEY_EXPORT_PATH)
        require_delayed_included_column(self.survey_rows, survey_header, SURVEY_EXPORT_PATH)
        delayed_block = build_delayed_response_checklist_from_annotations(self.survey_rows)
        delayed_diagnostics = delayed_block.get("diagnostics", {})
        log_step(
            "Delayed-response filter complete: "
            f"{len(delayed_diagnostics.get('included_ids') or [])} included delayed response(s); "
            f"{len(delayed_diagnostics.get('unverifiable_ids') or [])} unverifiable; "
            f"{len(delayed_diagnostics.get('early_ids') or [])} early; "
            f"{len(delayed_diagnostics.get('late_ids') or [])} late."
        )

        prompt_rows = build_prompt_rows_from_survey(self.survey_rows)
        genai_lookup, genai_problems = load_genai_scores(GENAI_SCORES_PATH)
        if genai_problems:
            raise RuntimeError(human_readable_genai_error(genai_problems))

        self.tasks = sorted(build_review_tasks(prompt_rows, genai_lookup), key=scoring_display_key)
        self.task_by_id = {task["task_id"]: task for task in self.tasks}
        self.scores_by_task_id = load_grader_scores(self.grader_path)
        self.lock = threading.RLock()
        self._task_payload_cache: bytes | None = None
        self._task_payload_cache_dirty = True

        stats = {
            "prompt_rows": len(prompt_rows),
            "blank_prompt_rows": sum(1 for row in prompt_rows if not clean(row.get("answer_std"))),
            "unique_nonblank_answers": len(genai_lookup),
        }
        self.rubric = append_scoring_workflow_instructions(load_scoring_rubric(), len(self.tasks), stats)
        self.rebuild_task_payload_cache()
        log_step(f"Server state ready in {time.perf_counter() - started:.1f}s with {len(self.tasks):,} review task(s).")

    def task_payload(self) -> dict[str, Any]:
        with self.lock:
            scores = {task_id: public_score_row(row) for task_id, row in self.scores_by_task_id.items()}
        return {
            "grader": self.grader,
            "tasks": self.tasks,
            "scores": scores,
            "rubric": self.rubric,
            "questionOrder": [key for key, _label in RETENTION_QUESTION_SPECS],
            "questionLabels": {key: label for key, label in RETENTION_QUESTION_SPECS},
            "metadata": {
                "validation_sample_fraction": VALIDATION_SAMPLE_FRACTION,
                "low_confidence_threshold": LOW_CONFIDENCE_THRESHOLD,
            },
        }

    def rebuild_task_payload_cache(self) -> None:
        payload = self.task_payload()
        payload["progress"] = self.progress()
        self._task_payload_cache = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self._task_payload_cache_dirty = False

    def task_payload_bytes(self) -> bytes:
        with self.lock:
            if self._task_payload_cache is None or self._task_payload_cache_dirty:
                self.rebuild_task_payload_cache()
            return self._task_payload_cache or b"{}"

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
        score = ""
        status = {"grade": "graded", "skip": "skipped", "flag": "flagged"}[action]
        if action == "grade":
            if not score_is_valid(payload.get("score")):
                raise ValueError("Score must be 0, 1, 2, 3, or 4")
            score = score_text(payload.get("score"))

        row = {
            "task_id": task_id,
            "question": task["question"],
            "question_key": task["question_key"],
            "creature": task["creature"],
            "creature_id": task["creature_id"],
            "answer_std": task["answer_std"],
            "score (0-4)": score,
            "status": status,
            "note (optional)": clean(payload.get("note")),
            "updated_at": utc_timestamp(),
        }

        with self.lock:
            self.scores_by_task_id[task_id] = row
            write_grader_scores(self.grader_path, self.scores_by_task_id)
            self._task_payload_cache_dirty = True
        return {"ok": True, "score": public_score_row(row), "progress": self.progress()}
    

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
                file_response(self, TEMPLATES_DIR / "scoring_app.html")
                return
            if path == "/api/tasks":
                json_bytes_response(self, state.task_payload_bytes())
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
    try:
        state = RetentionScoringServer(grader)
    except Exception as exc:
        print(f"[score_ret] ERROR: {exc}", file=sys.stderr)
        return 1
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
