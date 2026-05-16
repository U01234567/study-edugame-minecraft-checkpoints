use crate::log_bundle;
use crate::paths;
use crate::upload;
use serde::Serialize;
use std::fs::{self, File, OpenOptions};
use std::io::{BufRead, BufReader, Write};
use std::path::PathBuf;
use std::process::{Command, Stdio};
use std::sync::{Arc, Mutex, OnceLock};
use std::thread;
use std::time::{SystemTime, UNIX_EPOCH};
use tauri::{AppHandle, Emitter};

#[derive(Debug, Clone, Serialize)]
#[serde(rename_all = "camelCase")]
struct StudyStatusEvent {
    phase: String,
    message: String,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct StudyRunResult {
    pub run_id: String,
    pub status: String,
    pub message: String,
    pub exit_code: i32,
    pub run_dir: String,
    pub terminal_log: String,
    pub logs_zip: Option<String>,
    pub upload_status: String,
}

static ACTIVE_STUDY_PROCESS: OnceLock<Arc<Mutex<Option<u32>>>> = OnceLock::new();

fn active_study_process() -> &'static Arc<Mutex<Option<u32>>> {
    ACTIVE_STUDY_PROCESS.get_or_init(|| Arc::new(Mutex::new(None)))
}

pub fn terminate_active_study_process() {
    let pid = match active_study_process().lock() {
        Ok(mut guard) => guard.take(),
        Err(_) => None,
    };

    if let Some(pid) = pid {
        terminate_process_tree(pid);
    }
}

fn remember_active_study_process(pid: u32) {
    if let Ok(mut guard) = active_study_process().lock() {
        *guard = Some(pid);
    }
}

fn clear_active_study_process(pid: u32) {
    if let Ok(mut guard) = active_study_process().lock() {
        if guard.as_ref() == Some(&pid) {
            *guard = None;
        }
    }
}

#[cfg(target_os = "windows")]
fn terminate_process_tree(pid: u32) {
    let _ = Command::new("taskkill")
        .args(["/PID", &pid.to_string(), "/T", "/F"])
        .output();
}

#[cfg(not(target_os = "windows"))]
fn terminate_process_tree(pid: u32) {
    let _ = Command::new("sh")
        .arg("-c")
        .arg(format!("pkill -TERM -P {pid}; kill -TERM {pid}"))
        .output();
}

#[tauri::command]
pub async fn start_study(app: AppHandle) -> Result<StudyRunResult, String> {
    let app_for_task = app.clone();

    tauri::async_runtime::spawn_blocking(move || run_study_blocking(app_for_task))
        .await
        .map_err(|error| format!("Study task failed: {error}"))?
}

fn run_study_blocking(app: AppHandle) -> Result<StudyRunResult, String> {
    let app_paths = paths::resolve_app_paths(&app)?;
    paths::ensure_run_dirs(&app_paths)?;

    let run_id = create_run_id();
    let run_dir = app_paths.runs_dir.join(&run_id);

    fs::create_dir_all(&run_dir)
        .map_err(|error| format!("Could not create run directory: {error}"))?;

    let terminal_path = run_dir.join("terminal.txt");

    emit_status(
        &app,
        "starting",
        "Preparing Minecraft Study launch...",
    );

    append_terminal_line(
        &terminal_path,
        "Minecraft Study run starting.",
    )?;
    append_terminal_line(
        &terminal_path,
        &format!("Run ID: {run_id}"),
    )?;
    append_terminal_line(
        &terminal_path,
        &format!("Run directory: {}", run_dir.to_string_lossy()),
    )?;

    let launch_plan = resolve_launch_plan(&app_paths)?;

    emit_status(
        &app,
        "game_started",
        "Minecraft is starting. Keep this app open while you play.",
    );

    let exit_code = match launch_plan {
        LaunchPlan::GradleSourceDev { cwd, program, args } => {
            run_process_live(&terminal_path, cwd, program, args)?
        }
    };

    append_terminal_line(
        &terminal_path,
        &format!("Minecraft process exited with code {exit_code}."),
    )?;

    emit_status(
        &app,
        "collecting_logs",
        "Minecraft closed. Collecting local logs...",
    );

    let upload_summary = upload::mark_not_configured(&run_dir)?;

    let bundle_summary =
        log_bundle::collect_and_zip(&run_id, &run_dir, &terminal_path, &app_paths)?;

    emit_status(
        &app,
        "logs_packaged",
        "Logs were collected into a local zip file.",
    );

    let status = if exit_code == 0 {
        "completed"
    } else {
        "game_failed"
    };

    let message = if exit_code == 0 {
        "Minecraft closed normally. Logs were collected locally. Upload is not configured in this milestone."
    } else {
        "Minecraft closed with an error code. Logs were still collected locally where possible."
    };

    emit_status(&app, status, message);

    Ok(StudyRunResult {
        run_id,
        status: status.to_string(),
        message: message.to_string(),
        exit_code,
        run_dir: run_dir.to_string_lossy().to_string(),
        terminal_log: terminal_path.to_string_lossy().to_string(),
        logs_zip: Some(bundle_summary.logs_zip_path.to_string_lossy().to_string()),
        upload_status: upload_summary.status,
    })
}

enum LaunchPlan {
    GradleSourceDev {
        cwd: PathBuf,
        program: String,
        args: Vec<String>,
    },
}

fn resolve_launch_plan(app_paths: &paths::AppPaths) -> Result<LaunchPlan, String> {
    let gradlew_bat = app_paths.dev_mods_custom_dir.join("gradlew.bat");
    let gradlew = app_paths.dev_mods_custom_dir.join("gradlew");

    if app_paths.dev_mods_custom_dir.exists() && (gradlew_bat.exists() || gradlew.exists()) {
        if cfg!(target_os = "windows") {
            return Ok(LaunchPlan::GradleSourceDev {
                cwd: app_paths.dev_mods_custom_dir.clone(),
                program: "cmd.exe".to_string(),
                args: vec![
                    "/c".to_string(),
                    "gradlew.bat".to_string(),
                    "runClient".to_string(),
                ],
            });
        }

        return Ok(LaunchPlan::GradleSourceDev {
            cwd: app_paths.dev_mods_custom_dir.clone(),
            program: "./gradlew".to_string(),
            args: vec!["runClient".to_string()],
        });
    }

    if app_paths.bundled_payload_dir.is_some() {
        return Err(String::from(
            "Prepared payload was found, but the exact no-Gradle Java launch command is not connected yet.",
        ));
    }

    Err(String::from(
        "Could not find mods/custom Gradle launcher or a launchable prepared payload.",
    ))
}

fn run_process_live(
    terminal_path: &PathBuf,
    cwd: PathBuf,
    program: String,
    args: Vec<String>,
) -> Result<i32, String> {
    append_terminal_line(
        terminal_path,
        &format!(
            "Running command from {}: {} {}",
            cwd.to_string_lossy(),
            program,
            args.join(" ")
        ),
    )?;

    let terminal = OpenOptions::new()
        .create(true)
        .append(true)
        .open(terminal_path)
        .map_err(|error| format!("Could not open terminal log: {error}"))?;

    let terminal = Arc::new(Mutex::new(terminal));

    let mut command = Command::new(&program);
    command
        .args(&args)
        .current_dir(&cwd)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .env("PYTHONUTF8", "1")
        .env("PYTHONIOENCODING", "utf-8");

    let mut child = command
        .spawn()
        .map_err(|error| format!("Could not start Minecraft command: {error}"))?;

    let child_pid = child.id();
    remember_active_study_process(child_pid);

    let stdout_handle = child.stdout.take().map(|stdout| {
        let terminal = Arc::clone(&terminal);

        thread::spawn(move || {
            let reader = BufReader::new(stdout);
            for line in reader.lines().map_while(Result::ok) {
                write_terminal_stream(&terminal, "stdout", &line);
            }
        })
    });

    let stderr_handle = child.stderr.take().map(|stderr| {
        let terminal = Arc::clone(&terminal);

        thread::spawn(move || {
            let reader = BufReader::new(stderr);
            for line in reader.lines().map_while(Result::ok) {
                write_terminal_stream(&terminal, "stderr", &line);
            }
        })
    });

    let status = child
        .wait()
        .map_err(|error| format!("Could not wait for Minecraft process: {error}"))?;

    clear_active_study_process(child_pid);

    if let Some(handle) = stdout_handle {
        let _ = handle.join();
    }

    if let Some(handle) = stderr_handle {
        let _ = handle.join();
    }

    Ok(status.code().unwrap_or(1))
}

fn write_terminal_stream(terminal: &Arc<Mutex<File>>, stream: &str, line: &str) {
    if let Ok(mut file) = terminal.lock() {
        let _ = writeln!(file, "[{stream}] {line}");
    }
}

fn append_terminal_line(path: &PathBuf, line: &str) -> Result<(), String> {
    let mut file = OpenOptions::new()
        .create(true)
        .append(true)
        .open(path)
        .map_err(|error| format!("Could not open terminal log: {error}"))?;

    writeln!(file, "{line}")
        .map_err(|error| format!("Could not write terminal log: {error}"))
}

fn emit_status(app: &AppHandle, phase: &str, message: &str) {
    let _ = app.emit(
        "study-status",
        StudyStatusEvent {
            phase: phase.to_string(),
            message: message.to_string(),
        },
    );
}

fn create_run_id() -> String {
    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default();

    format!(
        "run-{}-{}-{}",
        now.as_secs(),
        now.subsec_millis(),
        std::process::id()
    )
}