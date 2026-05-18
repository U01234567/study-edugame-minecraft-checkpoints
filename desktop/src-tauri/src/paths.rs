use serde::Serialize;
use std::fs;
use std::path::{Path, PathBuf};
use std::process::Command;
use tauri::path::BaseDirectory;
use tauri::{AppHandle, Manager};

#[derive(Debug, Clone)]
pub struct AppPaths {
    pub app_data_dir: PathBuf,
    pub runs_dir: PathBuf,
    pub bundled_payload_dir: Option<PathBuf>,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct RuntimeStatus {
    pub payload_available: bool,
    pub payload_path: Option<String>,
    pub launch_mode: String,
    pub message: String,
}

pub fn resolve_app_paths(app: &AppHandle) -> Result<AppPaths, String> {
    let app_data_dir = app
        .path()
        .app_data_dir()
        .map_err(|error| format!("Could not resolve app data directory: {error}"))?;

    let runs_dir = app_data_dir.join("runs");

    let src_tauri_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
    let desktop_dir = src_tauri_dir
        .parent()
        .ok_or("Could not resolve desktop directory.")?
        .to_path_buf();

    let bundled_payload_dir = payload_candidates(app, &desktop_dir)
        .into_iter()
        .find(|path| path.exists());

    Ok(AppPaths {
        app_data_dir,
        runs_dir,
        bundled_payload_dir,
    })
}

#[tauri::command]
pub fn runtime_status(app: AppHandle) -> Result<RuntimeStatus, String> {
    let paths = resolve_app_paths(&app)?;

    let payload_available = paths.bundled_payload_dir.is_some();

    let launch_mode = if payload_available {
        "packaged-release".to_string()
    } else {
        "not-ready".to_string()
    };

    let message = match launch_mode.as_str() {
        "packaged-release" => "The packaged study payload is available.".to_string(),
        _ => "This installation is incomplete. Please reinstall Minecraft Study.".to_string(),
    };

    Ok(RuntimeStatus {
        payload_available,
        payload_path: paths
            .bundled_payload_dir
            .as_ref()
            .map(|path| path.to_string_lossy().to_string()),
        launch_mode,
        message,
    })
}

fn payload_candidates(app: &AppHandle, desktop_dir: &Path) -> Vec<PathBuf> {
    let mut candidates = Vec::new();

    if let Ok(path) = app
        .path()
        .resolve("resources/payload", BaseDirectory::Resource)
    {
        candidates.push(path);
    }

    if let Ok(path) = app.path().resolve("payload", BaseDirectory::Resource) {
        candidates.push(path);
    }

    candidates.push(
        desktop_dir
            .join("src-tauri")
            .join("resources")
            .join("payload"),
    );

    candidates.push(
        desktop_dir
            .join("payload-dist")
            .join(target_name())
            .join("payload"),
    );

    candidates
}

fn target_name() -> &'static str {
    if cfg!(all(target_os = "windows", target_arch = "x86_64")) {
        "win-x64"
    } else if cfg!(all(target_os = "macos", target_arch = "aarch64")) {
        "mac-arm64"
    } else if cfg!(all(target_os = "macos", target_arch = "x86_64")) {
        "mac-x64"
    } else {
        "unsupported"
    }
}

pub fn ensure_run_dirs(paths: &AppPaths) -> Result<(), String> {
    fs::create_dir_all(&paths.runs_dir)
        .map_err(|error| format!("Could not create runs directory: {error}"))
}

#[tauri::command]
pub fn open_app_data_folder(app: AppHandle) -> Result<(), String> {
    let paths = resolve_app_paths(&app)?;
    fs::create_dir_all(&paths.app_data_dir)
        .map_err(|error| format!("Could not create app data directory: {error}"))?;
    open_in_file_manager(&paths.app_data_dir)
}

#[tauri::command]
pub fn open_last_run_folder(app: AppHandle) -> Result<(), String> {
    let paths = resolve_app_paths(&app)?;

    let last_run = fs::read_dir(&paths.runs_dir)
        .map_err(|error| format!("Could not read runs directory: {error}"))?
        .filter_map(Result::ok)
        .filter(|entry| entry.path().is_dir())
        .max_by_key(|entry| {
            entry
                .metadata()
                .and_then(|metadata| metadata.modified())
                .ok()
        })
        .map(|entry| entry.path())
        .ok_or("No local run folders were found yet.")?;

    open_in_file_manager(&last_run)
}

pub fn open_in_file_manager(path: &Path) -> Result<(), String> {
    let mut command = if cfg!(target_os = "windows") {
        let mut command = Command::new("explorer");
        command.arg(path);
        command
    } else if cfg!(target_os = "macos") {
        let mut command = Command::new("open");
        command.arg(path);
        command
    } else {
        let mut command = Command::new("xdg-open");
        command.arg(path);
        command
    };

    command
        .spawn()
        .map_err(|error| format!("Could not open file manager: {error}"))?;

    Ok(())
}
