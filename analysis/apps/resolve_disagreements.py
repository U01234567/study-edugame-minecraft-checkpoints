from __future__ import annotations

import csv
import datetime as dt
import json
import mimetypes
import re
import sys
import threading
import time
import webbrowser
from collections import Counter, defaultdict
from dataclasses import dataclass
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import unquote, urlparse

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from helpers._retention_coding import (  # noqa: E402
    RETENTION_MERGED_PATH,
    backup_retention_tsv,
    load_rubric_json,
    score_text,
)
from helpers._shared import (  # noqa: E402
    RETENTION_ELEMENT_SPECS,
    STATIC_DIR,
    TEMPLATES_DIR,
    clean,
)
from helpers._survey_io import detect_text_encoding  # noqa: E402

HOST = "127.0.0.1"
DEFAULT_PORT = 8767
DEFAULT_INPUT_PATH = RETENTION_MERGED_PATH
TEMPLATE_PATH = TEMPLATES_DIR / "resolve_disagreements_app.html"
RUBRIC_RESOURCE_PATH = REPO_ROOT / "resources" / "retention_rubrics.json"

FINAL_COLUMNS = ["final_status", "final_score", "final_note_auto", "final_note_manual"]
FINAL_SCORE_PLACEHOLDER = "[resolve conflict]"
MAJORITY_NOTE = "Went with majority (3/4)"
HUMAN_AGREEMENT_NOTE = "Went with human agreement where GenAI (2x) disagree with each other"
VALID_SCORES = {"0", "1", "2"}
AUTO_FINAL_STATUSES = {"auto_majority_3_of_4", "auto_human_agreement"}
MANUAL_COMPLETED_FINAL_STATUSES = {"manual_adjudicated"}
COMPLETED_FINAL_STATUSES = MANUAL_COMPLETED_FINAL_STATUSES | AUTO_FINAL_STATUSES
FLAGGED_FINAL_STATUSES = {"flagged", "flagged_for_review", "manual_flagged"}
AUTO_FINAL_NOTES = {MAJORITY_NOTE, HUMAN_AGREEMENT_NOTE}
SOURCE_SCORE_RE = re.compile(r"^(?P<label>(?P<kind>genai|grader)\d+(?:_\d+)?)_score$", re.IGNORECASE)


@dataclass(frozen=True)
class SourceScore:
    label: str
    kind: str
    score: str
    note: str
    confidence: str
    status: str


def log_step(message: str) -> None:
    print(f"[resolve_disagreements] {message}", flush=True)


def utc_timestamp() -> str:
    return dt.datetime.now(dt.timezone.utc).astimezone().isoformat(timespec="seconds")


def normalise_score(value: object) -> str:
    return score_text(value)


def natural_key(text: str) -> list[Any]:
    return [int(part) if part.isdigit() else part.lower() for part in re.split(r"(\d+)", text)]


def relative_label(path: Path) -> str:
    try:
        return "./" + str(path.resolve().relative_to(REPO_ROOT.resolve())).replace("\\", "/")
    except ValueError:
        return str(path)


def read_tsv_with_header(path: Path) -> tuple[list[dict[str, str]], list[str]]:
    encoding = detect_text_encoding(path)
    with path.open("r", encoding=encoding, newline="") as handle:
        reader = csv.DictReader(handle, delimiter="\t")
        header = list(reader.fieldnames or [])
        rows = [dict(row) for row in reader]
    return rows, header


def write_tsv_atomic_with_backup(path: Path, header: list[str], rows: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    backup_retention_tsv(path)
    tmp_path = path.with_name(f".{path.name}.{dt.datetime.now().strftime('%Y%m%d%H%M%S%f')}.tmp")
    with tmp_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=header, delimiter="\t", extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            writer.writerow({field: clean(row.get(field)) for field in header})
    tmp_path.replace(path)


def source_score_columns(header: list[str]) -> tuple[list[str], list[str], list[str]]:
    genai: list[str] = []
    grader: list[str] = []
    ignored_aliases: list[str] = []
    for column in header:
        match = SOURCE_SCORE_RE.match(column)
        if match:
            if match.group("kind").lower() == "genai":
                genai.append(column)
            else:
                grader.append(column)
        elif column in {"genai_score", "grader_score"}:
            ignored_aliases.append(column)
    return sorted(genai, key=natural_key), sorted(grader, key=natural_key), sorted(ignored_aliases, key=natural_key)


def label_to_display(label: str, kind: str) -> str:
    match = re.search(r"(\d+)", label)
    number = match.group(1) if match else "?"
    return f"GenAI #{number}" if kind == "genai" else f"Human #{number}"


def source_scores_for_row(row: dict[str, str], genai_columns: list[str], grader_columns: list[str]) -> list[SourceScore]:
    sources: list[SourceScore] = []
    for column in genai_columns + grader_columns:
        match = SOURCE_SCORE_RE.match(column)
        if not match:
            continue
        label = match.group("label")
        kind = "genai" if match.group("kind").lower() == "genai" else "grader"
        sources.append(
            SourceScore(
                label=label,
                kind=kind,
                score=normalise_score(row.get(column)),
                note=clean(row.get(f"{label}_note")),
                confidence=clean(row.get(f"{label}_confidence")) if kind == "genai" else "",
                status=clean(row.get(f"{label}_status")) if kind == "grader" else "",
            )
        )
    return sources


def row_is_unresolved(row: dict[str, str]) -> bool:
    return clean(row.get("final_score")) == FINAL_SCORE_PLACEHOLDER


def row_final_status(row: dict[str, str]) -> str:
    return clean(row.get("final_status")).lower()


def row_is_auto_resolved(row: dict[str, str]) -> bool:
    status = row_final_status(row)
    note = clean(row.get("final_note_manual"))
    return status in AUTO_FINAL_STATUSES or note in AUTO_FINAL_NOTES


def row_is_flagged(row: dict[str, str]) -> bool:
    return row_final_status(row) in FLAGGED_FINAL_STATUSES


def row_has_manual_note(row: dict[str, str]) -> bool:
    note = clean(row.get("final_note_manual"))
    return bool(note and note != "—")


def row_is_manually_resolved(row: dict[str, str]) -> bool:
    if row_is_auto_resolved(row):
        return False
    if row_final_status(row) in MANUAL_COMPLETED_FINAL_STATUSES:
        return normalise_score(row.get("final_score")) in VALID_SCORES
    return row_has_manual_note(row) and normalise_score(row.get("final_score")) in VALID_SCORES


def row_should_appear_in_resolve_app(row: dict[str, str]) -> bool:
    if row_is_auto_resolved(row):
        return False
    return row_is_unresolved(row) or row_is_flagged(row) or row_is_manually_resolved(row)


def automatic_resolution_for_row(
    row: dict[str, str],
    genai_columns: list[str],
    grader_columns: list[str],
) -> tuple[str, str, str] | None:
    """Return (score, status, manual_note) for auto-fillable conflict rows only."""
    if not row_is_unresolved(row) or row_is_flagged(row) or row_has_manual_note(row):
        return None

    sources = source_scores_for_row(row, genai_columns, grader_columns)
    valid_sources = [source for source in sources if source.score]
    if len(sources) != 4 or len(valid_sources) != 4:
        return None

    counts = Counter(source.score for source in valid_sources)
    majority_score, majority_count = counts.most_common(1)[0]
    if majority_count == 3:
        return majority_score, "auto_majority_3_of_4", MAJORITY_NOTE

    genai_scores = [source.score for source in valid_sources if source.kind == "genai"]
    human_scores = [source.score for source in valid_sources if source.kind == "grader"]
    humans_agree = len(human_scores) == 2 and human_scores[0] == human_scores[1]
    genai_disagree = len(genai_scores) == 2 and genai_scores[0] != genai_scores[1]
    if humans_agree and genai_disagree:
        return human_scores[0], "auto_human_agreement", HUMAN_AGREEMENT_NOTE

    return None


def ensure_final_columns(header: list[str], rows: list[dict[str, str]]) -> list[str]:
    updated = list(header)
    for column in FINAL_COLUMNS:
        if column not in updated:
            updated.append(column)
            for row in rows:
                row[column] = ""
    return updated


def apply_automatic_resolutions(
    rows: list[dict[str, str]],
    genai_columns: list[str],
    grader_columns: list[str],
) -> dict[str, int]:
    counts = {
        "majority_rows": 0,
        "human_agreement_rows": 0,
    }
    for row in rows:
        resolution = automatic_resolution_for_row(row, genai_columns, grader_columns)
        if resolution is None:
            continue
        score, status, manual_note = resolution
        row["final_score"] = score
        row["final_status"] = status
        row["final_note_manual"] = manual_note
        if status == "auto_majority_3_of_4":
            counts["majority_rows"] += 1
        elif status == "auto_human_agreement":
            counts["human_agreement_rows"] += 1
    counts["total_rows"] = counts["majority_rows"] + counts["human_agreement_rows"]
    return counts


def resolve_group_key(row: dict[str, str], row_number: int) -> str:
    task_id = clean(row.get("task_id"))
    if task_id:
        return f"task:{task_id}"
    parts = [clean(row.get(key)) for key in ("q_element", "creature_id", "creature", "answer_std")]
    if any(parts):
        return "key:" + "\u0000".join(parts)
    return f"row:{row_number}"


def question_order_index(question_key: str) -> int:
    order = {key: index for index, (key, _label) in enumerate(RETENTION_ELEMENT_SPECS)}
    return order.get(question_key, 999)


def task_sort_key(task: dict[str, Any]) -> tuple[Any, ...]:
    return (
        question_order_index(clean(task.get("q_element")) or clean(task.get("question_key"))),
        clean(task.get("creature") or task.get("creature_id")).lower(),
        clean(task.get("answer_std")).lower(),
        min(task.get("row_numbers") or [10**12]),
    )


def task_status_for_rows(group_rows: list[tuple[int, dict[str, str]]]) -> str:
    rows = [row for _row_number, row in group_rows]
    if rows and all(row_is_flagged(row) for row in rows):
        return "flagged"
    if rows and all(row_is_manually_resolved(row) for row in rows):
        return "resolved"
    return "todo"


def source_payloads_for_row(row: dict[str, str], genai_columns: list[str], grader_columns: list[str]) -> list[dict[str, str]]:
    payloads: list[dict[str, str]] = []
    for source in source_scores_for_row(row, genai_columns, grader_columns):
        payloads.append({
            "label": source.label,
            "display_label": label_to_display(source.label, source.kind),
            "kind": source.kind,
            "score": source.score,
            "confidence": source.confidence,
            "status": source.status,
            "note": source.note,
        })
    return payloads


def public_final_row(row: dict[str, str], row_numbers: list[int]) -> dict[str, str]:
    return {
        "status": task_status_for_rows([(number, row) for number in row_numbers]),
        "final_score": clean(row.get("final_score")),
        "final_note_manual": clean(row.get("final_note_manual")),
        "final_status": clean(row.get("final_status")),
        "updated_at": clean(row.get("final_updated_at")),
    }


def build_tasks(
    rows: list[dict[str, str]],
    genai_columns: list[str],
    grader_columns: list[str],
) -> tuple[list[dict[str, Any]], dict[str, list[int]]]:
    groups: dict[str, list[tuple[int, dict[str, str]]]] = defaultdict(list)
    for row_number, row in enumerate(rows, start=2):
        if row_should_appear_in_resolve_app(row):
            groups[resolve_group_key(row, row_number)].append((row_number, row))

    tasks: list[dict[str, Any]] = []
    task_rows: dict[str, list[int]] = {}
    for index, (group_key, group_rows) in enumerate(groups.items(), start=1):
        representative_row = group_rows[0][1]
        row_numbers = [row_number for row_number, _row in group_rows]
        task_id = group_key.replace("\u0000", "|")
        task_rows[task_id] = row_numbers
        sources = source_payloads_for_row(representative_row, genai_columns, grader_columns)
        task = {
            "task_id": task_id,
            "row_numbers": row_numbers,
            "row_count": len(row_numbers),
            "MCID": clean(representative_row.get("MCID")),
            "moment": clean(representative_row.get("moment")),
            "creature": clean(representative_row.get("creature")),
            "creature_id": clean(representative_row.get("creature_id")),
            "q_element": clean(representative_row.get("q_element")),
            "question_key": clean(representative_row.get("question_key")) or clean(representative_row.get("q_element")),
            "question_label": clean(representative_row.get("question_label")),
            "answer": clean(representative_row.get("answer")),
            "answer_std": clean(representative_row.get("answer_std")),
            "occurrence_weight": clean(representative_row.get("occurrence_weight")),
            "final_score": clean(representative_row.get("final_score")),
            "final_note_auto": clean(representative_row.get("final_note_auto")),
            "final_note_manual": clean(representative_row.get("final_note_manual")),
            "final_status": clean(representative_row.get("final_status")),
            "sources": sources,
            "score_counts": dict(Counter(source["score"] or "missing/invalid" for source in sources)),
            "status": task_status_for_rows(group_rows),
        }
        tasks.append(task)
    tasks.sort(key=task_sort_key)
    return tasks, task_rows


class ResolveDisagreementsServer:
    def __init__(self, input_path: Path) -> None:
        started = time.perf_counter()
        self.input_path = input_path
        self.lock = threading.RLock()
        self.error: str = ""
        self.header: list[str] = []
        self.rows: list[dict[str, str]] = []
        self.genai_columns: list[str] = []
        self.grader_columns: list[str] = []
        self.ignored_aliases: list[str] = []
        self.tasks: list[dict[str, Any]] = []
        self.task_rows: dict[str, list[int]] = {}
        self.auto_summary: dict[str, int] = {"majority_rows": 0, "human_agreement_rows": 0, "total_rows": 0}
        self._payload_cache: bytes | None = None
        self._payload_cache_dirty = True

        log_step(f"Initialising resolve-disagreements app for {relative_label(input_path)}...")
        self.rubric = self.load_rubric()
        self.load_and_prepare()
        log_step(f"Server state ready in {time.perf_counter() - started:.1f}s with {len(self.tasks):,} unresolved task group(s).")

    def load_rubric(self) -> dict[str, Any]:
        try:
            rubric = load_rubric_json(RUBRIC_RESOURCE_PATH)
        except Exception as exc:  # noqa: BLE001
            log_step(f"WARNING: could not load rubric JSON: {exc}")
            rubric = {}
        workflow = """
        <section class="workflow-instructions">
          <h1>Final adjudication workflow</h1>
          <p>This interface edits only <code>final_status</code>, <code>final_score</code>, <code>final_note_auto</code>, and <code>final_note_manual</code> in <code>data/retention_scores_merged.tsv</code>.</p>
          <ul>
            <li>On startup, the app automatically resolves conflict rows with a 3/4 majority.</li>
            <li>It also automatically resolves rows where the two human graders agree and the two GenAI scores disagree with each other.</li>
            <li>The remaining rows are shown here for manual final adjudication. You must write a manual note before finalising a score.</li>
            <li>Each save creates a timestamped safety backup in <code>score_backups/</code> before the active merged TSV is rewritten.</li>
          </ul>
        </section>
        """
        rubric = dict(rubric)
        rubric["instructions_html"] = workflow + clean(rubric.get("instructions_html"))
        return rubric

    def load_and_prepare(self) -> None:
        if not self.input_path.exists():
            self.error = (
                f"Missing {relative_label(self.input_path)}. Finish the retention answer scoring process first: run sum_merged after the GenAI and human scoring files are complete so data/retention_scores_merged.tsv exists."
            )
            return

        self.rows, header = read_tsv_with_header(self.input_path)
        self.header = ensure_final_columns(header, self.rows)
        self.genai_columns, self.grader_columns, self.ignored_aliases = source_score_columns(self.header)
        missing_final = [column for column in FINAL_COLUMNS if column not in self.header]
        if missing_final:
            self.error = "Missing final columns after header normalisation: " + ", ".join(missing_final)
            return

        self.auto_summary = apply_automatic_resolutions(self.rows, self.genai_columns, self.grader_columns)
        if self.auto_summary["total_rows"]:
            log_step(
                f"Auto-filled {self.auto_summary['majority_rows']:,} row(s) with {MAJORITY_NOTE!r} and "
                f"{self.auto_summary['human_agreement_rows']:,} row(s) with {HUMAN_AGREEMENT_NOTE!r}."
            )
            write_tsv_atomic_with_backup(self.input_path, self.header, self.rows)

        self.tasks, self.task_rows = build_tasks(self.rows, self.genai_columns, self.grader_columns)
        self.rebuild_payload_cache()

    def progress(self) -> dict[str, int]:
        total = len(self.tasks)
        flagged = sum(1 for task in self.tasks if task.get("status") == "flagged")
        resolved = sum(1 for task in self.tasks if task.get("status") == "resolved")
        todo = max(0, total - flagged - resolved)
        return {"total": total, "resolved": resolved, "flagged": flagged, "to_do": todo}

    def payload(self) -> dict[str, Any]:
        return {
            "ok": not bool(self.error),
            "error": self.error,
            "input_path": relative_label(self.input_path),
            "tasks": self.tasks,
            "rubric": self.rubric,
            "questionOrder": [key for key, _label in RETENTION_ELEMENT_SPECS],
            "questionLabels": {key: label for key, label in RETENTION_ELEMENT_SPECS},
            "sourceColumns": {
                "genai": self.genai_columns,
                "grader": self.grader_columns,
                "ignored_aliases": self.ignored_aliases,
            },
            "autoSummary": self.auto_summary,
            "progress": self.progress(),
        }

    def rebuild_payload_cache(self) -> None:
        self._payload_cache = json.dumps(self.payload(), ensure_ascii=False).encode("utf-8")
        self._payload_cache_dirty = False

    def payload_bytes(self) -> bytes:
        with self.lock:
            if self._payload_cache is None or self._payload_cache_dirty:
                self.rebuild_payload_cache()
            return self._payload_cache or b"{}"

    def row_indices_for_task(self, task_id: str) -> list[int]:
        row_numbers = self.task_rows.get(task_id)
        if not row_numbers:
            raise ValueError("Unknown task_id")
        return [row_number - 2 for row_number in row_numbers]

    def task_by_id(self, task_id: str) -> dict[str, Any]:
        for task in self.tasks:
            if task.get("task_id") == task_id:
                return task
        raise ValueError("Unknown task_id")

    def save_resolution(self, payload: dict[str, Any]) -> dict[str, Any]:
        task_id = clean(payload.get("task_id"))
        action = clean(payload.get("action"))
        note = clean(payload.get("note"))
        score = normalise_score(payload.get("score"))

        if action not in {"finalise", "flag"}:
            raise ValueError("Action must be finalise or flag")
        if not task_id:
            raise ValueError("Missing task_id")
        if action == "finalise":
            if score not in VALID_SCORES:
                raise ValueError("Final score must be 0, 1, or 2")
            if not note:
                raise ValueError("A manual note is required before finalising the score")
        if action == "flag" and not note:
            note = "Flagged for later manual review"

        with self.lock:
            task = self.task_by_id(task_id)
            row_indices = self.row_indices_for_task(task_id)
            timestamp = utc_timestamp()
            for row_index in row_indices:
                row = self.rows[row_index]
                if action == "finalise":
                    row["final_status"] = "manual_adjudicated"
                    row["final_score"] = score
                    row["final_note_manual"] = note
                else:
                    row["final_status"] = "flagged_for_review"
                    row["final_score"] = FINAL_SCORE_PLACEHOLDER
                    row["final_note_manual"] = note
                if "final_updated_at" in self.header:
                    row["final_updated_at"] = timestamp

            write_tsv_atomic_with_backup(self.input_path, self.header, self.rows)

            task["status"] = "resolved" if action == "finalise" else "flagged"
            task["final_status"] = "manual_adjudicated" if action == "finalise" else "flagged_for_review"
            task["final_score"] = score if action == "finalise" else FINAL_SCORE_PLACEHOLDER
            task["final_note_manual"] = note
            self._payload_cache_dirty = True
            response_task = dict(task)
            progress = self.progress()

        return {"ok": True, "task": response_task, "progress": progress}


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


def build_handler(state: ResolveDisagreementsServer) -> type[BaseHTTPRequestHandler]:
    class Handler(BaseHTTPRequestHandler):
        def log_message(self, format: str, *args: Any) -> None:  # noqa: A002
            print(f"[resolve_disagreements] {self.address_string()} - {format % args}")

        def do_GET(self) -> None:  # noqa: N802
            parsed = urlparse(self.path)
            path = parsed.path
            if path in {"/", "/index.html"}:
                file_response(self, TEMPLATE_PATH)
                return
            if path == "/api/tasks":
                json_bytes_response(self, state.payload_bytes())
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
            if parsed.path != "/api/resolve":
                self.send_error(HTTPStatus.NOT_FOUND, "Unknown route")
                return
            try:
                length = int(self.headers.get("Content-Length", "0"))
                payload = json.loads(self.rfile.read(length).decode("utf-8") or "{}")
                json_response(self, state.save_resolution(payload))
            except Exception as exc:  # noqa: BLE001
                json_response(self, {"ok": False, "error": str(exc)}, HTTPStatus.BAD_REQUEST)

    return Handler


def parse_args(argv: list[str]) -> tuple[Path, int]:
    input_path = DEFAULT_INPUT_PATH
    port = DEFAULT_PORT
    for arg in argv:
        if arg.startswith("input="):
            input_path = Path(arg.split("=", 1)[1]).expanduser()
        elif arg.startswith("port="):
            port = int(arg.split("=", 1)[1])
    if not input_path.is_absolute():
        input_path = REPO_ROOT / input_path
    return input_path, port


def main(argv: list[str] | None = None) -> int:
    args = argv if argv is not None else sys.argv[1:]
    input_path, port = parse_args(args)
    try:
        state = ResolveDisagreementsServer(input_path=input_path)
    except Exception as exc:  # noqa: BLE001
        print(f"[resolve_disagreements] ERROR: {exc}", file=sys.stderr)
        return 1

    handler = build_handler(state)
    server = ThreadingHTTPServer((HOST, port), handler)
    url = f"http://{HOST}:{port}/"
    print(f"Resolve retention disagreements app: {url}")
    print(f"Input: {relative_label(input_path)}")
    print(f"Unresolved task groups available: {len(state.tasks)}")
    print("Press Ctrl+C to stop the server.")
    webbrowser.open(url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping resolve-disagreements server.")
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
