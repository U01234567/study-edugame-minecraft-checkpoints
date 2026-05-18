from __future__ import annotations

import shutil
import subprocess
import sys
import tempfile
import uuid
import zipfile
from dataclasses import dataclass, field
from pathlib import Path


APP_DIR = Path(__file__).resolve().parent
ANALYSIS_DIR = APP_DIR.parent
LOG_DIR = ANALYSIS_DIR / "logs"
DEFAULT_IDENTITY_FILE = Path.home() / ".minecraft-study" / "minecraft-study-logs-age-key.txt"
MAX_ARCHIVE_DEPTH = 5
COPY_CHUNK_SIZE = 1024 * 1024


@dataclass
class ExtractionResult:
    copied: list[Path] = field(default_factory=list)
    skipped_existing: list[Path] = field(default_factory=list)
    ignored_members: list[str] = field(default_factory=list)
    decrypted_archives: list[Path] = field(default_factory=list)
    processed_archives: list[Path] = field(default_factory=list)
    errors: list[str] = field(default_factory=list)


@dataclass
class RuntimeContext:
    logs_dir: Path
    identity_file: Path
    work_dir: Path
    age_command_checked: bool = False


def print_usage() -> None:
    print("Usage:")
    print("  python main.py decrypt_logs")
    print("  python main.py decrypt_logs file=./logs/all_uploads.zip")
    print("  python main.py decrypt_logs file=./logs/logs.zip.age")
    print("  python main.py decrypt_logs identity=C:/Users/YOU/.minecraft-study/minecraft-study-logs-age-key.txt")
    print("  python main.py decrypt_logs logs=./logs")
    print()
    print("Behavior:")
    print("  - With no file= argument, uses the newest .age or .zip file in the logs directory.")
    print("  - .age files are decrypted with age, then treated as zip files.")
    print("  - .zip files are scanned directly and may contain study-*.log files, nested .zip files, or .age files.")
    print("  - Only .log files whose final filename starts with study- are copied out.")


def parse_args(argv: list[str] | None = None) -> dict[str, Path]:
    args = argv if argv is not None else sys.argv[1:]
    config: dict[str, Path] = {
        "logs_dir": LOG_DIR,
        "identity_file": DEFAULT_IDENTITY_FILE,
    }

    for arg in args:
        if arg in {"-h", "--help", "help"}:
            print_usage()
            raise SystemExit(0)

        if "=" not in arg:
            continue

        key, value = arg.split("=", 1)
        key = key.strip().lower().replace("-", "_")
        value_path = Path(value.strip())

        if key in {"logs", "logs_dir", "dir"}:
            config["logs_dir"] = resolve_path(value_path)
        elif key in {"identity", "identity_file", "key"}:
            config["identity_file"] = resolve_path(value_path)
        elif key in {"file", "input", "input_file", "age", "zip", "bundle"}:
            config["input_file"] = resolve_path(value_path)

    return config


def resolve_path(path: Path) -> Path:
    if path.is_absolute():
        return path
    return (ANALYSIS_DIR / path).resolve()


def is_age_file(path: Path) -> bool:
    return path.name.lower().endswith(".age")


def is_zip_file(path: Path) -> bool:
    return path.name.lower().endswith(".zip")


def is_supported_input(path: Path) -> bool:
    return path.is_file() and (is_age_file(path) or is_zip_file(path))


def newest_input_file(logs_dir: Path) -> Path:
    candidates = [path for path in logs_dir.iterdir() if is_supported_input(path)]

    if not candidates:
        raise FileNotFoundError(f"No .age or .zip files found in {logs_dir}")

    return max(candidates, key=lambda path: path.stat().st_mtime)


def decrypted_zip_path(age_file: Path, output_dir: Path) -> Path:
    name = age_file.name

    if name.lower().endswith(".age"):
        name = name[:-4]

    if not name.lower().endswith(".zip"):
        name = f"{name}.zip"

    return output_dir / name


def require_age_command(context: RuntimeContext) -> None:
    if context.age_command_checked:
        return

    if shutil.which("age") is None:
        raise RuntimeError(
            "The age command was not found. Install it first with:\n"
            "  winget install -e --id FiloSottile.age"
        )

    context.age_command_checked = True


def require_identity_file(identity_file: Path) -> None:
    if not identity_file.exists():
        raise FileNotFoundError(f"Identity file not found: {identity_file}")


def decrypt_age_file(age_file: Path, output_zip: Path, identity_file: Path) -> bool:
    if output_zip.exists() and output_zip.stat().st_mtime >= age_file.stat().st_mtime:
        print(f"Skipped decrypting because this file is already up to date: {output_zip}")
        return False

    output_zip.parent.mkdir(parents=True, exist_ok=True)

    with tempfile.NamedTemporaryFile(
        prefix=f"{output_zip.stem}-",
        suffix=".tmp",
        dir=output_zip.parent,
        delete=False,
    ) as temp_file:
        temp_path = Path(temp_file.name)

    try:
        subprocess.run(
            [
                "age",
                "--decrypt",
                "--identity",
                str(identity_file),
                "--output",
                str(temp_path),
                str(age_file),
            ],
            check=True,
        )
        temp_path.replace(output_zip)
        print(f"Decrypted: {age_file}")
        print(f"Wrote zip: {output_zip}")
        return True
    except subprocess.CalledProcessError as exc:
        temp_path.unlink(missing_ok=True)
        raise RuntimeError(f"age failed with exit code {exc.returncode} while decrypting {age_file}") from exc
    except Exception:
        temp_path.unlink(missing_ok=True)
        raise


def safe_member_name(member_name: str) -> str:
    cleaned = member_name.replace("\\", "/").strip("/")
    return Path(cleaned).name.replace("\x00", "").strip()


def is_study_log_name(filename: str) -> bool:
    return filename.startswith("study-") and filename.lower().endswith(".log")


def copy_zip_member_to_file(archive: zipfile.ZipFile, member: zipfile.ZipInfo, target_path: Path) -> None:
    target_path.parent.mkdir(parents=True, exist_ok=True)
    with archive.open(member, "r") as source, target_path.open("wb") as destination:
        shutil.copyfileobj(source, destination, length=COPY_CHUNK_SIZE)


def copy_zip_member_to_temp(
    archive: zipfile.ZipFile,
    member: zipfile.ZipInfo,
    member_name: str,
    work_dir: Path,
) -> Path:
    temp_name = f"nested-{uuid.uuid4().hex}-{member_name}"
    temp_path = work_dir / temp_name
    copy_zip_member_to_file(archive, member, temp_path)
    return temp_path


def process_age_file(
    age_file: Path,
    context: RuntimeContext,
    result: ExtractionResult,
    depth: int,
    keep_decrypted_zip: bool,
) -> None:
    require_identity_file(context.identity_file)
    require_age_command(context)

    output_dir = context.logs_dir if keep_decrypted_zip else context.work_dir
    output_zip = decrypted_zip_path(age_file, output_dir)

    decrypt_age_file(age_file, output_zip, context.identity_file)
    result.decrypted_archives.append(output_zip)
    process_zip_file(output_zip, context, result, depth=depth + 1)


def process_zip_file(
    zip_path: Path,
    context: RuntimeContext,
    result: ExtractionResult,
    depth: int,
) -> None:
    if depth > MAX_ARCHIVE_DEPTH:
        result.errors.append(f"Skipped {zip_path}: archive nesting exceeded depth {MAX_ARCHIVE_DEPTH}.")
        return

    if not zipfile.is_zipfile(zip_path):
        result.errors.append(f"Skipped {zip_path}: not a valid zip file.")
        return

    result.processed_archives.append(zip_path)

    try:
        with zipfile.ZipFile(zip_path, "r") as archive:
            for member in archive.infolist():
                if member.is_dir():
                    continue

                member_name = safe_member_name(member.filename)

                if not member_name:
                    result.ignored_members.append(member.filename)
                    continue

                if is_study_log_name(member_name):
                    target_path = context.logs_dir / member_name

                    if target_path.exists():
                        result.skipped_existing.append(target_path)
                        continue

                    copy_zip_member_to_file(archive, member, target_path)
                    result.copied.append(target_path)
                    continue

                if is_age_file(Path(member_name)) or is_zip_file(Path(member_name)):
                    try:
                        nested_path = copy_zip_member_to_temp(archive, member, member_name, context.work_dir)
                        process_input_file(
                            nested_path,
                            context,
                            result,
                            depth=depth + 1,
                            keep_decrypted_zip=False,
                        )
                    except Exception as exc:
                        result.errors.append(f"Could not process nested archive {member.filename} in {zip_path}: {exc}")
                    continue

                result.ignored_members.append(member.filename)
    except zipfile.BadZipFile:
        result.errors.append(f"Skipped {zip_path}: bad zip file.")


def process_input_file(
    input_file: Path,
    context: RuntimeContext,
    result: ExtractionResult,
    depth: int = 0,
    keep_decrypted_zip: bool = True,
) -> None:
    if depth > MAX_ARCHIVE_DEPTH:
        result.errors.append(f"Skipped {input_file}: archive nesting exceeded depth {MAX_ARCHIVE_DEPTH}.")
        return

    if not input_file.exists():
        result.errors.append(f"Input file not found: {input_file}")
        return

    if is_age_file(input_file):
        process_age_file(
            input_file,
            context,
            result,
            depth=depth,
            keep_decrypted_zip=keep_decrypted_zip,
        )
        return

    if is_zip_file(input_file):
        process_zip_file(input_file, context, result, depth=depth)
        return

    result.errors.append(f"Unsupported input file type: {input_file}")


def print_limited_paths(title: str, paths: list[Path], limit: int = 25) -> None:
    if not paths:
        return

    print()
    print(title)
    for path in paths[:limit]:
        print(f"  - {path.name}")

    remaining = len(paths) - limit
    if remaining > 0:
        print(f"  ... and {remaining} more")


def print_limited_strings(title: str, values: list[str], limit: int = 25) -> None:
    if not values:
        return

    print()
    print(title)
    for value in values[:limit]:
        print(f"  - {value}")

    remaining = len(values) - limit
    if remaining > 0:
        print(f"  ... and {remaining} more")


def print_summary(result: ExtractionResult, logs_dir: Path) -> None:
    print()
    print(f"Copied {len(result.copied)} study log file(s) to {logs_dir}.")
    for path in result.copied:
        print(f"  + {path.name}")

    print_limited_paths(
        f"Skipped {len(result.skipped_existing)} study log file(s) because they already exist:",
        result.skipped_existing,
    )

    if result.ignored_members:
        print()
        print(f"Ignored {len(result.ignored_members)} non-study-log file(s).")
        print("Only files whose final filename matches study-*.log are extracted.")

    print_limited_paths(
        f"Processed {len(result.processed_archives)} zip archive(s):",
        result.processed_archives,
    )
    print_limited_paths(
        f"Created or reused {len(result.decrypted_archives)} decrypted zip archive(s):",
        result.decrypted_archives,
    )
    print_limited_strings(
        f"Encountered {len(result.errors)} error(s):",
        result.errors,
    )

    print()
    print("Done. Original .age and .zip files were not removed.")


def main(argv: list[str] | None = None) -> int:
    config = parse_args(argv)
    logs_dir = config["logs_dir"]
    identity_file = config["identity_file"]

    logs_dir.mkdir(parents=True, exist_ok=True)

    try:
        input_file = config.get("input_file") or newest_input_file(logs_dir)
    except Exception as exc:
        print(str(exc))
        return 1

    if not input_file.exists():
        print(f"Input file not found: {input_file}")
        return 1

    if not (is_age_file(input_file) or is_zip_file(input_file)):
        print(f"Unsupported input file type: {input_file}")
        print("Expected a .age or .zip file.")
        return 1

    print(f"Using input file: {input_file}")
    if is_age_file(input_file):
        print(f"Using identity file: {identity_file}")
    else:
        print("Using zip file directly. Identity file is only needed if this zip contains .age files.")

    result = ExtractionResult()

    with tempfile.TemporaryDirectory(prefix="minecraft-study-analysis-") as temp_dir:
        context = RuntimeContext(
            logs_dir=logs_dir,
            identity_file=identity_file,
            work_dir=Path(temp_dir),
        )

        try:
            process_input_file(
                input_file,
                context,
                result,
                keep_decrypted_zip=True,
            )
        except Exception as exc:
            result.errors.append(str(exc))

    print_summary(result, logs_dir)
    return 1 if result.errors else 0


if __name__ == "__main__":
    raise SystemExit(main())