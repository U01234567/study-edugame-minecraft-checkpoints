use crate::config;
use crate::log_bundle;
use crate::paths;
use crate::upload;
use serde::{Deserialize, Serialize};
use serde_json::json;
use std::fs::{self, File, OpenOptions};
use std::io::{BufRead, BufReader, Write};
use std::path::{Path, PathBuf};
use std::process::{Child, Command, Stdio};
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::{Arc, Mutex, OnceLock};
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};
use tauri::{AppHandle, Emitter};

const QUESTIONNAIRE_CLOSE_DELAY: Duration = Duration::from_secs(20);
const PROCESS_POLL_DELAY: Duration = Duration::from_millis(750);

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
    pub encrypted_logs: Option<String>,
    pub upload_status: String,
    pub questionnaire_button_pressed: bool,
    pub intro_declined: bool,
    pub mcid: Option<String>,
}

#[derive(Debug)]
struct ProcessOutcome {
    exit_code: i32,
    questionnaire_button_pressed: bool,
    intro_declined: bool,
    mcid: Option<String>,
    aborted_by_app: bool,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
struct PayloadManifest {
    game: PayloadGame,
    runtime: PayloadRuntime,
    launch: PayloadLaunch,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
struct PayloadGame {
    run_directory: String,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
struct PayloadRuntime {
    java_executable: String,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
struct PayloadLaunch {
    direct_java_launch_ready: bool,
    arg_file: Option<String>,
    launch_config: Option<String>,
}

static ACTIVE_STUDY_PROCESS: OnceLock<Arc<Mutex<Option<u32>>>> = OnceLock::new();
static ABORT_REQUESTED: AtomicBool = AtomicBool::new(false);

fn active_study_process() -> &'static Arc<Mutex<Option<u32>>> {
    ACTIVE_STUDY_PROCESS.get_or_init(|| Arc::new(Mutex::new(None)))
}

#[tauri::command]
pub fn abort_study(app: AppHandle) -> Result<(), String> {
    let _ = upload::send_status(
        &app,
        "program_killed",
        json!({
            "message": "Participant clicked Abort game in the desktop app.",
            "phase": "program_killed",
            "killReason": "participant_abort_button",
        }),
    );

    terminate_active_study_process();
    emit_status(
        &app,
        "aborted",
        "The game is being closed. Available logs will be collected.",
    );

    Ok(())
}

pub fn terminate_active_study_process() {
    ABORT_REQUESTED.store(true, Ordering::SeqCst);

    let pid = match active_study_process().lock() {
        Ok(mut guard) => guard.take(),
        Err(_) => None,
    };

    if let Some(pid) = pid {
        terminate_process_tree(pid);
    }
}

fn study_abort_requested() -> bool {
    ABORT_REQUESTED.load(Ordering::SeqCst)
}

fn clear_abort_request() {
    ABORT_REQUESTED.store(false, Ordering::SeqCst);
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
    clear_abort_request();

    let app_paths = paths::resolve_app_paths(&app)?;
    paths::ensure_run_dirs(&app_paths)?;

    let run_id = create_run_id();
    let run_dir = app_paths.runs_dir.join(&run_id);

    fs::create_dir_all(&run_dir)
        .map_err(|error| format!("Could not create run directory: {error}"))?;

    let terminal_path = run_dir.join("terminal.txt");
    let run_started_at = SystemTime::now();

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

    let _ = upload::send_status(
        &app,
        "game_starting",
        json!({
            "message": "Participant clicked Start.",
        }),
    );

    let launch_plan = resolve_launch_plan(&app_paths, &run_dir)?;

    emit_status(
        &app,
        "game_started",
        "Minecraft is starting. Keep this app open while you play.",
    );

    let _ = upload::send_status(
        &app,
        "game_started",
        json!({
            "message": "Minecraft process was launched.",
        }),
    );

    let process_outcome = match launch_plan {
        LaunchPlan::Process { cwd, program, args } => run_process_live(
            &app,
            &terminal_path,
            &app_paths,
            cwd,
            program,
            args,
            run_started_at,
        )?,
    };

    append_terminal_line(
        &terminal_path,
        &format!("Minecraft process exited with code {}.", process_outcome.exit_code),
    )?;

    if process_outcome.aborted_by_app {
        let _ = upload::send_status(
            &app,
            "program_killed",
            json!({
                "message": "The Minecraft process was terminated by the desktop app.",
                "mcid": process_outcome.mcid.clone(),
            }),
        );
    } else if process_outcome.intro_declined {
        let _ = upload::send_status(
            &app,
            "intro_declined",
            json!({
                "message": "The participant declined participation on the Minecraft intro screen.",
                "exitCode": process_outcome.exit_code,
                "mcid": process_outcome.mcid.clone(),
            }),
        );
    } else if !process_outcome.questionnaire_button_pressed {
        let failure_status = if process_outcome.exit_code == 0 {
            "game_closed_before_questionnaire"
        } else {
            "game_crashed_before_questionnaire"
        };

        let _ = upload::send_status(
            &app,
            failure_status,
            json!({
                "message": "Minecraft exited before the questionnaire button was pressed.",
                "exitCode": process_outcome.exit_code,
                "mcid": process_outcome.mcid.clone(),
            }),
        );
    }

    emit_status(
        &app,
        "collecting_logs",
        "Minecraft closed. Collecting local logs...",
    );

    let bundle_summary = log_bundle::collect_and_zip(
        &run_id,
        &run_dir,
        &terminal_path,
        &app_paths,
        Some(run_started_at),
    )?;

    let mcid = bundle_summary
        .mcid
        .clone()
        .or_else(|| process_outcome.mcid.clone());

    emit_status(
        &app,
        "logs_packaged",
        "Logs were collected and encrypted locally.",
    );

    let _ = upload::send_status(
        &app,
        "logs_packaged",
        json!({
            "message": "Minecraft logs were collected and encrypted locally.",
            "mcid": mcid.clone(),
            "questionnaireButtonPressed": process_outcome.questionnaire_button_pressed,
        }),
    );

    let upload_summary = match bundle_summary.encrypted_logs_path.as_ref() {
        Some(encrypted_path) => {
            emit_status(
                &app,
                "uploading_logs",
                "Uploading encrypted logs...",
            );

            upload::upload_encrypted_logs(
                &app,
                &run_dir,
                encrypted_path,
                mcid.as_deref(),
            )?
        }
        None => upload::mark_not_configured(&run_dir)?,
    };

    if upload_summary.status == "completed" {
        emit_status(
            &app,
            "logs_uploaded",
            "Encrypted logs were shared successfully.",
        );
    } else {
        emit_status(
            &app,
            "upload_failed",
            &upload_summary.message,
        );
    }

    let status = if process_outcome.questionnaire_button_pressed && upload_summary.status == "completed" {
        "completed"
    } else if process_outcome.questionnaire_button_pressed && upload_summary.status != "completed" {
        "completed_upload_failed"
    } else if process_outcome.aborted_by_app {
        "aborted"
    } else if process_outcome.intro_declined {
        "intro_declined"
    } else if process_outcome.exit_code == 0 {
        "game_closed_before_questionnaire"
    } else {
        "game_failed"
    };

    let message = if status == "completed" {
        "The questionnaire was opened, Minecraft was closed, and encrypted logs were uploaded."
    } else if status == "completed_upload_failed" {
        "All done, but logs were not shared. Close this app, fix your network connection, and reopen the app. The app will try again to share the logs."
    } else if status == "intro_declined" {
        "If that is the case, feel free to uninstall the app. If you merely tried to close the game, you can press Start! again whenever you have time."
    } else if process_outcome.aborted_by_app {
        "The game was aborted from the desktop app. Available logs were collected."
    } else if !process_outcome.questionnaire_button_pressed {
        "Minecraft closed before the questionnaire button was pressed. Available logs were collected."
    } else {
        "Minecraft closed with an error code. Logs were still collected locally where possible."
    };

    emit_status(&app, status, message);

    Ok(StudyRunResult {
        run_id,
        status: status.to_string(),
        message: message.to_string(),
        exit_code: process_outcome.exit_code,
        run_dir: run_dir.to_string_lossy().to_string(),
        terminal_log: terminal_path.to_string_lossy().to_string(),
        logs_zip: Some(bundle_summary.logs_zip_path.to_string_lossy().to_string()),
        encrypted_logs: bundle_summary
            .encrypted_logs_path
            .map(|path| path.to_string_lossy().to_string()),
        upload_status: upload_summary.status,
        questionnaire_button_pressed: process_outcome.questionnaire_button_pressed,
        intro_declined: process_outcome.intro_declined,
        mcid,
    })
}

enum LaunchPlan {
    Process {
        cwd: PathBuf,
        program: String,
        args: Vec<String>,
    },
}

fn resolve_launch_plan(app_paths: &paths::AppPaths, run_dir: &Path) -> Result<LaunchPlan, String> {
    let Some(payload_template_dir) = app_paths.bundled_payload_dir.as_ref() else {
        return Err(String::from(
            "This Minecraft Study installation is incomplete. Please reinstall the app and try again.",
        ));
    };

    resolve_prepared_payload_launch(payload_template_dir, run_dir)
}

fn resolve_prepared_payload_launch(payload_template_dir: &Path, run_dir: &Path) -> Result<LaunchPlan, String> {
    let manifest = read_payload_manifest(payload_template_dir)?;

    if !manifest.launch.direct_java_launch_ready {
        return Err(String::from(
            "This Minecraft Study installation is incomplete. Please reinstall the app and try again.",
        ));
    }

    let java_executable = payload_template_dir.join(&manifest.runtime.java_executable);
    if !java_executable.exists() {
        return Err(String::from(
            "This Minecraft Study installation is missing its Java runtime. Please reinstall the app and try again.",
        ));
    }
    ensure_executable_if_possible(&java_executable);

    let payload_run_dir = run_dir.join("payload");
    if payload_run_dir.exists() {
        fs::remove_dir_all(&payload_run_dir)
            .map_err(|error| format!("Could not reset local study files: {error}"))?;
    }

    let source_game_run_dir = payload_template_dir.join(&manifest.game.run_directory);
    if !source_game_run_dir.exists() {
        return Err(String::from(
            "This Minecraft Study installation is missing its prepared game folder. Please reinstall the app and try again.",
        ));
    }

    let game_run_dir = payload_run_dir.join(&manifest.game.run_directory);
    copy_directory_recursive(&source_game_run_dir, &game_run_dir)?;

    let runtime_arg_file = materialize_launch_files(payload_template_dir, &payload_run_dir, &manifest)?;

    Ok(LaunchPlan::Process {
        cwd: game_run_dir,
        program: java_executable.to_string_lossy().to_string(),
        args: vec![
            format!("-Dstudy.output.dir={}", run_dir.to_string_lossy()),
            "-Dstudy.enable_python_summary=false".to_string(),
            "-Dstudy.open_qualtrics_in_game=true".to_string(),
            format!("@{}", runtime_arg_file.to_string_lossy()),
        ],
    })
}

fn materialize_launch_files(
    payload_template_dir: &Path,
    payload_run_dir: &Path,
    manifest: &PayloadManifest,
) -> Result<PathBuf, String> {
    let template_game_run_dir = payload_template_dir.join(&manifest.game.run_directory);

    let arg_file_relative = manifest
        .launch
        .arg_file
        .as_ref()
        .ok_or("This Minecraft Study installation is missing launch instructions. Please reinstall the app.")?;

    let launch_config_relative = manifest
        .launch
        .launch_config
        .clone()
        .unwrap_or_else(|| "game/launch/loom-cache/launch.cfg".to_string());

    let source_arg_file = payload_template_dir.join(arg_file_relative);
    let source_launch_config = payload_template_dir.join(&launch_config_relative);
    let runtime_arg_file = payload_run_dir.join(arg_file_relative);
    let runtime_launch_config = payload_run_dir.join(&launch_config_relative);

    if !source_arg_file.exists() {
        return Err(String::from(
            "This Minecraft Study installation is missing launch instructions. Please reinstall the app and try again.",
        ));
    }

    if !source_launch_config.exists() {
        return Err(String::from(
            "This Minecraft Study installation is missing launch configuration. Please reinstall the app and try again.",
        ));
    }

    materialize_launch_config(
        &source_launch_config,
        &runtime_launch_config,
        &template_game_run_dir,
    )?;

    materialize_arg_file(
        &source_arg_file,
        &runtime_arg_file,
        &runtime_launch_config,
        &template_game_run_dir,
    )?;

    Ok(runtime_arg_file)
}

fn materialize_launch_config(
    source: &Path,
    destination: &Path,
    template_game_run_dir: &Path,
) -> Result<(), String> {
    let raw = fs::read_to_string(source)
        .map_err(|error| format!("Could not read packaged launch config: {error}"))?;

    let mut rewritten = Vec::new();
    let mut section: Option<String> = None;

    for line in raw.lines() {
        if line.trim().is_empty() {
            rewritten.push(String::new());
            continue;
        }

        let is_indented = line.chars().next().map(|ch| ch.is_whitespace()).unwrap_or(false);

        if !is_indented {
            section = Some(line.trim().to_string());
            rewritten.push(line.trim().to_string());
            continue;
        }

        let indent_len = line.len() - line.trim_start().len();
        let indent = &line[..indent_len];
        let value = line.trim();

        if section.as_deref() == Some("commonProperties") {
            if let Some((key, raw_value)) = value.split_once('=') {
                let rewritten_value = rewrite_payload_relative_value(raw_value, template_game_run_dir);
                rewritten.push(format!("{indent}{key}={rewritten_value}"));
            } else {
                let rewritten_value = rewrite_payload_relative_value(value, template_game_run_dir);
                rewritten.push(format!("{indent}{rewritten_value}"));
            }
            continue;
        }

        let rewritten_value = rewrite_payload_relative_value(value, template_game_run_dir);
        rewritten.push(format!("{indent}{rewritten_value}"));
    }

    if let Some(parent) = destination.parent() {
        fs::create_dir_all(parent)
            .map_err(|error| format!("Could not prepare local launch config folder: {error}"))?;
    }

    fs::write(destination, format!("{}\n", rewritten.join("\n")).as_bytes())
        .map_err(|error| format!("Could not write local launch config: {error}"))
}

fn materialize_arg_file(
    source: &Path,
    destination: &Path,
    runtime_launch_config: &Path,
    template_game_run_dir: &Path,
) -> Result<(), String> {
    let tokens = read_arg_file_tokens(source)?;
    let mut rewritten = Vec::with_capacity(tokens.len());
    let mut previous_token: Option<String> = None;

    for token in tokens {
        let rewritten_token = if previous_token
            .as_deref()
            .map(is_classpath_argument)
            .unwrap_or(false)
        {
            rewrite_classpath_token(&token, template_game_run_dir)
        } else if let Some(value) = token.strip_prefix("-Dfabric.dli.config=") {
            let _ = value;
            format!("-Dfabric.dli.config={}", path_to_java_string(runtime_launch_config))
        } else if let Some((key, value)) = token.split_once('=') {
            if key.starts_with("-D") {
                format!("{key}={}", rewrite_payload_relative_value(value, template_game_run_dir))
            } else {
                rewrite_payload_relative_value(&token, template_game_run_dir)
            }
        } else {
            rewrite_payload_relative_value(&token, template_game_run_dir)
        };

        previous_token = Some(token.to_ascii_lowercase());
        rewritten.push(rewritten_token);
    }

    if let Some(parent) = destination.parent() {
        fs::create_dir_all(parent)
            .map_err(|error| format!("Could not prepare local launch argfile folder: {error}"))?;
    }

    let contents = rewritten
        .iter()
        .map(|token| quote_arg_file_token(token))
        .collect::<Vec<_>>()
        .join("\n")
        + "\n";

    fs::write(destination, contents)
        .map_err(|error| format!("Could not write local launch argfile: {error}"))
}

fn read_arg_file_tokens(path: &Path) -> Result<Vec<String>, String> {
    let raw = fs::read_to_string(path)
        .map_err(|error| format!("Could not read packaged launch argfile: {error}"))?;

    raw.lines()
        .map(str::trim)
        .filter(|line| !line.is_empty())
        .map(unquote_arg_file_token)
        .collect()
}

fn unquote_arg_file_token(token: &str) -> Result<String, String> {
    if !(token.starts_with('"') && token.ends_with('"') && token.len() >= 2) {
        return Ok(token.to_string());
    }

    let inner = &token[1..token.len() - 1];
    let mut result = String::new();
    let mut escaped = false;

    for ch in inner.chars() {
        if escaped {
            result.push(ch);
            escaped = false;
            continue;
        }

        if ch == '\\' {
            escaped = true;
            continue;
        }

        result.push(ch);
    }

    if escaped {
        result.push('\\');
    }

    Ok(result)
}

fn quote_arg_file_token(token: &str) -> String {
    if token
        .chars()
        .all(|ch| ch.is_ascii_alphanumeric() || matches!(ch, '_' | '.' | '/' | ':' | '@' | '%' | '+' | '=' | ',' | ';' | '\\' | '-'))
    {
        return token.to_string();
    }

    format!(
        "\"{}\"",
        token.replace('\\', "\\\\").replace('"', "\\\"")
    )
}

fn is_classpath_argument(token: &str) -> bool {
    matches!(token, "-cp" | "-classpath" | "--class-path")
}

fn rewrite_classpath_token(token: &str, template_game_run_dir: &Path) -> String {
    let delimiter = if cfg!(target_os = "windows") { ';' } else { ':' };

    token
        .split(delimiter)
        .filter(|part| !part.is_empty())
        .map(|part| rewrite_payload_relative_value(part, template_game_run_dir))
        .collect::<Vec<_>>()
        .join(&delimiter.to_string())
}

fn rewrite_payload_relative_value(value: &str, template_game_run_dir: &Path) -> String {
    if !is_payload_relative_path(value) {
        return value.to_string();
    }

    let candidate = template_game_run_dir.join(value);
    let resolved = fs::canonicalize(&candidate).unwrap_or(candidate);
    path_to_java_string(&resolved)
}

fn is_payload_relative_path(value: &str) -> bool {
    value.starts_with("../")
        || value.starts_with("..\\")
        || value.starts_with("./")
        || value.starts_with(".\\")
}

fn path_to_java_string(path: &Path) -> String {
    normalize_java_path_string(&path.to_string_lossy().replace('\\', "/"))
}

fn normalize_java_path_string(path: &str) -> String {
    #[cfg(target_os = "windows")]
    {
        if let Some(rest) = path.strip_prefix("//?/UNC/") {
            return format!("//{rest}");
        }

        if let Some(rest) = path.strip_prefix("//?/") {
            return rest.to_string();
        }
    }

    path.to_string()
}

fn read_payload_manifest(payload_dir: &Path) -> Result<PayloadManifest, String> {
    let manifest_path = payload_dir.join("manifest.json");
    let raw = fs::read_to_string(&manifest_path)
        .map_err(|error| format!("Could not read packaged study manifest: {error}"))?;

    serde_json::from_str(&raw)
        .map_err(|error| format!("Could not read packaged study manifest: {error}"))
}

fn copy_directory_recursive(source: &Path, destination: &Path) -> Result<(), String> {
    if !source.exists() {
        return Err(String::from(
            "This Minecraft Study installation is incomplete. Please reinstall the app and try again.",
        ));
    }

    fs::create_dir_all(destination)
        .map_err(|error| format!("Could not prepare local study files: {error}"))?;

    for entry in fs::read_dir(source)
        .map_err(|error| format!("Could not read packaged study files: {error}"))?
        .filter_map(Result::ok)
    {
        let source_path = entry.path();
        let destination_path = destination.join(entry.file_name());
        let file_type = entry
            .file_type()
            .map_err(|error| format!("Could not inspect packaged study files: {error}"))?;

        if file_type.is_dir() {
            copy_directory_recursive(&source_path, &destination_path)?;
        } else if file_type.is_file() {
            if let Some(parent) = destination_path.parent() {
                fs::create_dir_all(parent)
                    .map_err(|error| format!("Could not prepare local study files: {error}"))?;
            }

            fs::copy(&source_path, &destination_path)
                .map_err(|error| format!("Could not copy packaged study files: {error}"))?;
        }
    }

    Ok(())
}

#[cfg(unix)]
fn ensure_executable_if_possible(path: &Path) {
    use std::os::unix::fs::PermissionsExt;

    if let Ok(metadata) = fs::metadata(path) {
        let mut permissions = metadata.permissions();
        permissions.set_mode(permissions.mode() | 0o755);
        let _ = fs::set_permissions(path, permissions);
    }
}

#[cfg(not(unix))]
fn ensure_executable_if_possible(_path: &Path) {}

fn run_process_live(
    app: &AppHandle,
    terminal_path: &PathBuf,
    app_paths: &paths::AppPaths,
    cwd: PathBuf,
    program: String,
    args: Vec<String>,
    run_started_at: SystemTime,
) -> Result<ProcessOutcome, String> {
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

    if let Some(run_dir) = terminal_path.parent() {
        match config::write_runtime_env_file(run_dir) {
            Ok(path) => {
                command.env("MINECRAFT_STUDY_CONFIG", &path);
                append_terminal_line(
                    terminal_path,
                    &format!("Runtime study config: {}", path.to_string_lossy()),
                )?;
            }
            Err(error) => {
                append_terminal_line(
                    terminal_path,
                    &format!("No runtime study config was written: {error}"),
                )?;
            }
        }
    }

    #[cfg(target_os = "windows")]
    apply_windows_no_console(&mut command);

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

    let outcome = monitor_child_process(
        app,
        app_paths,
        terminal_path,
        &mut child,
        run_started_at,
    )?;

    clear_active_study_process(child_pid);

    if let Some(handle) = stdout_handle {
        let _ = handle.join();
    }

    if let Some(handle) = stderr_handle {
        let _ = handle.join();
    }

    Ok(outcome)
}

#[cfg(target_os = "windows")]
fn apply_windows_no_console(command: &mut Command) {
    use std::os::windows::process::CommandExt;

    const CREATE_NO_WINDOW: u32 = 0x08000000;
    command.creation_flags(CREATE_NO_WINDOW);
}

fn monitor_child_process(
    app: &AppHandle,
    app_paths: &paths::AppPaths,
    terminal_path: &PathBuf,
    child: &mut Child,
    run_started_at: SystemTime,
) -> Result<ProcessOutcome, String> {
    let mut questionnaire_button_pressed = false;
    let mut questionnaire_detected_at: Option<SystemTime> = None;
    let mut mcid: Option<String> = None;
    let mut creatures_seen: Option<String> = None;
    let mut intro_declined = false;
    let mut close_requested_after_questionnaire = false;

    loop {
        if let Some(status) = child
            .try_wait()
            .map_err(|error| format!("Could not poll Minecraft process: {error}"))?
        {
            if let Some(study_log) = log_bundle::find_newest_study_log(app_paths, Some(run_started_at))? {
                if study_log.mcid.is_some() {
                    mcid = study_log.mcid.clone();
                }

                if study_log.intro_declined {
                    intro_declined = true;
                }

                if study_log.questionnaire_button_pressed {
                    questionnaire_button_pressed = true;
                }
            }

            return Ok(ProcessOutcome {
                exit_code: status.code().unwrap_or(1),
                questionnaire_button_pressed,
                intro_declined,
                mcid,
                aborted_by_app: study_abort_requested(),
            });
        }

        if !questionnaire_button_pressed {
            if let Some(study_log) = log_bundle::find_newest_study_log(app_paths, Some(run_started_at))? {
                if study_log.mcid.is_some() {
                    mcid = study_log.mcid.clone();
                }

                if study_log.creatures_seen.is_some() {
                    creatures_seen = study_log.creatures_seen.clone();
                }

                if study_log.intro_declined {
                    intro_declined = true;
                }

                if study_log.questionnaire_button_pressed {
                    questionnaire_button_pressed = true;
                    questionnaire_detected_at = Some(SystemTime::now());
                    mcid = study_log.mcid.clone().or(mcid);
                    creatures_seen = study_log.creatures_seen.clone().or(creatures_seen);

                    append_terminal_line(
                        terminal_path,
                        "Detected questionnaire_button_pressed in the study log.",
                    )?;

                    emit_status(
                        app,
                        "questionnaire_button_pressed",
                        "Questionnaire opened. Minecraft will close automatically in about 20 seconds, then this app will send the encrypted logs.",
                    );

                    let _ = upload::send_status(
                        app,
                        "questionnaire_button_pressed",
                        json!({
                            "message": "The Minecraft mod logged questionnaire_button_pressed.",
                            "mcid": mcid.clone(),
                            "creaturesSeen": creatures_seen.clone(),
                        }),
                    );
                }
            }
        }

        if questionnaire_button_pressed && !close_requested_after_questionnaire {
            if let Some(detected_at) = questionnaire_detected_at {
                if detected_at.elapsed().unwrap_or_default() >= QUESTIONNAIRE_CLOSE_DELAY {
                    close_requested_after_questionnaire = true;
                    emit_status(
                        app,
                        "closing_after_questionnaire",
                        "The questionnaire was opened 20 seconds ago. Closing Minecraft and preparing the encrypted logs...",
                    );
                    terminate_process_tree(child.id());
                }
            }
        }

        if study_abort_requested() {
            terminate_process_tree(child.id());
        }

        thread::sleep(PROCESS_POLL_DELAY);
    }
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