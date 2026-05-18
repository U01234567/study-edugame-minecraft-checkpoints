use crate::config;
use crate::paths;
use serde::{Deserialize, Serialize};
use serde_json::json;
use std::fs;
use std::path::{Path, PathBuf};
use std::time::{SystemTime, UNIX_EPOCH};
use tauri::AppHandle;

const CONNECTION_FILE_NAME: &str = "server-connection.json";
const INSTALL_FILE_NAME: &str = "install-id.txt";

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct UploadSummary {
    pub status: String,
    pub message: String,
    pub upload_status_path: PathBuf,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
struct ServerConnection {
    connected: bool,
    participant_id: u64,
    uuid: String,
    token: String,
    install_id: String,
    created_at_unix_seconds: u64,
    last_seen_at_unix_seconds: u64,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ConnectionStatus {
    pub connected: bool,
    pub participant_id: Option<u64>,
    pub message: String,
}

#[derive(Debug, Deserialize)]
struct StartRunResponse {
    connected: bool,
    id: u64,
    uuid: String,
    token: String,
}

#[derive(Debug, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
struct UploadStatusFile {
    status: String,
    message: String,
    participant_id: Option<u64>,
    created_at_unix_seconds: u64,
}

#[tauri::command]
pub fn ensure_server_connection(app: AppHandle) -> Result<ConnectionStatus, String> {
    verify_server_connection(&app, "app_opened")
}

#[tauri::command]
pub fn get_connection_status(app: AppHandle) -> Result<ConnectionStatus, String> {
    verify_server_connection(&app, "connection_check")
}

fn verify_server_connection(app: &AppHandle, phase: &str) -> Result<ConnectionStatus, String> {
    match ensure_connection(app) {
        Ok(connection) => match send_status_with_connection(
            &connection,
            phase,
            json!({
                "phase": phase,
                "message": "Desktop app checked the server connection.",
            }),
        ) {
            Ok(()) => Ok(ConnectionStatus {
                connected: true,
                participant_id: Some(connection.participant_id),
                message: String::from("Connected to the study server."),
            }),
            Err(error) => Ok(ConnectionStatus {
                connected: false,
                participant_id: Some(connection.participant_id),
                message: format!(
                    "Not connected to the study server. Make sure you have a network connection on your device. {error}"
                ),
            }),
        },
        Err(error) => Ok(ConnectionStatus {
            connected: false,
            participant_id: None,
            message: format!(
                "Not connected to the study server. Make sure you have a network connection on your device. {error}"
            ),
        }),
    }
}

pub fn send_status(
    app: &AppHandle,
    status: &str,
    metadata: serde_json::Value,
) -> Result<(), String> {
    let connection = ensure_connection(app)?;
    send_status_with_connection(&connection, status, metadata)
}

#[tauri::command]
pub fn retry_pending_log_upload(app: AppHandle) -> Result<UploadSummary, String> {
    let app_paths = paths::resolve_app_paths(&app)?;

    let Some(run_dir) = latest_run_with_pending_upload(&app_paths.runs_dir)? else {
        return Err(String::from(
            "No pending encrypted log upload was found on this device.",
        ));
    };

    let Some(encrypted_path) = find_encrypted_log_file(&run_dir)? else {
        return Err(format!(
            "No encrypted log file was found in {}.",
            run_dir.to_string_lossy()
        ));
    };

    upload_encrypted_logs(&app, &run_dir, &encrypted_path, None)
}

pub fn upload_encrypted_logs(
    app: &AppHandle,
    run_dir: &Path,
    encrypted_path: &Path,
    mcid: Option<&str>,
) -> Result<UploadSummary, String> {
    let upload_status_path = run_dir.join("upload-status.json");

    let connection = match ensure_connection(app) {
        Ok(connection) => connection,
        Err(error) => {
            return write_upload_status(
                &upload_status_path,
                "no_connection",
                &format!("Could not connect to the upload server: {error}"),
                None,
            );
        }
    };

    let _ = send_status_with_connection(
        &connection,
        "upload_started",
        json!({
            "phase": "upload_started",
            "message": "Encrypted log upload started.",
            "mcid": mcid,
        }),
    );

    let bytes = fs::read(encrypted_path)
        .map_err(|error| format!("Could not read encrypted log file: {error}"))?;

    let filename = encrypted_path
        .file_name()
        .and_then(|name| name.to_str())
        .unwrap_or("minecraft-study-logs.zip.age");

    let url = config::website_url(&format!(
        "apps/minecraft-study/api/runs/{}/upload/",
        connection.uuid
    ));

    let response = response_to_string(
        ureq::post(&url)
            .set("Authorization", &format!("Bearer {}", connection.token))
            .set("X-Minecraft-Study-Token", &connection.token)
            .set("X-Minecraft-Study-ID", &connection.participant_id.to_string())
            .set("X-Minecraft-Study-Filename", filename)
            .set("Content-Type", "application/octet-stream")
            .send_bytes(&bytes),
    );

    match response {
        Ok(_) => {
            let _ = send_status_with_connection(
                &connection,
                "upload_completed",
                json!({
                    "phase": "upload_completed",
                    "message": "Encrypted logs were uploaded.",
                    "fileName": filename,
                    "fileSize": bytes.len(),
                    "mcid": mcid,
                }),
            );

            write_upload_status(
                &upload_status_path,
                "completed",
                "Encrypted logs were uploaded to the Minecraft Study server.",
                Some(connection.participant_id),
            )
        }
        Err(error) => {
            let _ = send_status_with_connection(
                &connection,
                "upload_failed",
                json!({
                    "phase": "upload_failed",
                    "message": error,
                    "fileName": filename,
                    "fileSize": bytes.len(),
                    "mcid": mcid,
                }),
            );

            write_upload_status(
                &upload_status_path,
                "failed",
                &format!("Encrypted upload failed: {error}"),
                Some(connection.participant_id),
            )
        }
    }
}

pub fn mark_not_configured(run_dir: &Path) -> Result<UploadSummary, String> {
    let upload_status_path = run_dir.join("upload-status.json");

    write_upload_status(
        &upload_status_path,
        "not_configured",
        "Encrypted upload is not configured because no age recipient public key is available.",
        None,
    )
}

fn latest_run_with_pending_upload(runs_dir: &Path) -> Result<Option<PathBuf>, String> {
    if !runs_dir.exists() {
        return Ok(None);
    }

    let mut candidates: Vec<(SystemTime, PathBuf)> = Vec::new();

    for entry in fs::read_dir(runs_dir)
        .map_err(|error| format!("Could not read runs directory: {error}"))?
        .filter_map(Result::ok)
    {
        let path = entry.path();

        if !path.is_dir() || upload_already_completed(&path) {
            continue;
        }

        if find_encrypted_log_file(&path)?.is_none() {
            continue;
        }

        let modified_at = entry
            .metadata()
            .and_then(|metadata| metadata.modified())
            .unwrap_or(SystemTime::UNIX_EPOCH);

        candidates.push((modified_at, path));
    }

    Ok(candidates
        .into_iter()
        .max_by_key(|candidate| candidate.0)
        .map(|candidate| candidate.1))
}

fn upload_already_completed(run_dir: &Path) -> bool {
    let path = run_dir.join("upload-status.json");
    let Ok(raw) = fs::read_to_string(path) else {
        return false;
    };

    let Ok(status) = serde_json::from_str::<UploadStatusFile>(&raw) else {
        return false;
    };

    status.status == "completed"
}

fn find_encrypted_log_file(run_dir: &Path) -> Result<Option<PathBuf>, String> {
    if !run_dir.exists() {
        return Ok(None);
    }

    let mut candidates: Vec<(SystemTime, PathBuf)> = Vec::new();

    for entry in fs::read_dir(run_dir)
        .map_err(|error| format!("Could not read run directory {}: {error}", run_dir.to_string_lossy()))?
        .filter_map(Result::ok)
    {
        let path = entry.path();

        if !path.is_file() || !is_encrypted_log_file(&path) {
            continue;
        }

        let modified_at = entry
            .metadata()
            .and_then(|metadata| metadata.modified())
            .unwrap_or(SystemTime::UNIX_EPOCH);

        candidates.push((modified_at, path));
    }

    Ok(candidates
        .into_iter()
        .max_by_key(|candidate| candidate.0)
        .map(|candidate| candidate.1))
}

fn is_encrypted_log_file(path: &Path) -> bool {
    let Some(name) = path.file_name().and_then(|value| value.to_str()) else {
        return false;
    };

    (name.ends_with(".zip.age") || name.ends_with(".zip.enc") || name.ends_with(".age"))
        && name.contains("logs")
}

fn ensure_connection(app: &AppHandle) -> Result<ServerConnection, String> {
    let app_paths = paths::resolve_app_paths(app)?;

    fs::create_dir_all(&app_paths.app_data_dir)
        .map_err(|error| format!("Could not create app data directory: {error}"))?;

    let path = connection_path(&app_paths.app_data_dir);

    if let Ok(connection) = read_connection(&path) {
        return Ok(connection);
    }

    let install_id = ensure_install_id(&app_paths.app_data_dir)?;
    let connection = start_run_on_server(&install_id)?;
    write_connection(&path, &connection)?;

    Ok(connection)
}

fn start_run_on_server(install_id: &str) -> Result<ServerConnection, String> {
    let url = config::website_url("apps/minecraft-study/api/runs/start/");

    let body = json!({
        "appVersion": env!("CARGO_PKG_VERSION"),
        "os": std::env::consts::OS,
        "device": format!("{}-{}", std::env::consts::OS, std::env::consts::ARCH),
        "architecture": std::env::consts::ARCH,
        "installId": install_id,
    });

    let response_text = response_to_string(
        ureq::post(&url)
            .set("Content-Type", "application/json")
            .send_string(&body.to_string()),
    )?;

    let response: StartRunResponse = serde_json::from_str(&response_text)
        .map_err(|error| format!("Could not parse server start response: {error}"))?;

    if !response.connected {
        return Err(String::from("Server did not confirm the connection."));
    }

    let now = now_unix_seconds();

    Ok(ServerConnection {
        connected: true,
        participant_id: response.id,
        uuid: response.uuid,
        token: response.token,
        install_id: install_id.to_string(),
        created_at_unix_seconds: now,
        last_seen_at_unix_seconds: now,
    })
}

fn send_status_with_connection(
    connection: &ServerConnection,
    status: &str,
    metadata: serde_json::Value,
) -> Result<(), String> {
    let url = config::website_url(&format!(
        "apps/minecraft-study/api/runs/{}/status/",
        connection.uuid
    ));

    let body = json!({
        "status": status,
        "phase": status,
        "message": metadata.get("message").and_then(|value| value.as_str()).unwrap_or(""),
        "mcid": metadata.get("mcid").cloned().unwrap_or(serde_json::Value::Null),
        "appVersion": env!("CARGO_PKG_VERSION"),
        "installId": connection.install_id.clone(),
        "exitCode": metadata.get("exitCode").cloned().unwrap_or(serde_json::Value::Null),
        "fileName": metadata.get("fileName").cloned().unwrap_or(serde_json::Value::Null),
        "fileSize": metadata.get("fileSize").cloned().unwrap_or(serde_json::Value::Null),
        "creaturesSeen": metadata.get("creaturesSeen").cloned().unwrap_or(serde_json::Value::Null),
        "killReason": metadata.get("killReason").cloned().unwrap_or(serde_json::Value::Null),
        "questionnaireButtonPressed": metadata.get("questionnaireButtonPressed").cloned().unwrap_or(serde_json::Value::Null),
    });

    response_to_string(
        ureq::post(&url)
            .set("Authorization", &format!("Bearer {}", connection.token))
            .set("X-Minecraft-Study-Token", &connection.token)
            .set("X-Minecraft-Study-ID", &connection.participant_id.to_string())
            .set("Content-Type", "application/json")
            .send_string(&body.to_string()),
    )?;

    Ok(())
}

fn ensure_install_id(app_data_dir: &Path) -> Result<String, String> {
    let path = app_data_dir.join(INSTALL_FILE_NAME);

    if let Ok(existing) = fs::read_to_string(&path) {
        let trimmed = existing.trim();
        if !trimmed.is_empty() {
            return Ok(trimmed.to_string());
        }
    }

    let install_id = create_install_id();
    fs::write(&path, &install_id)
        .map_err(|error| format!("Could not write install ID: {error}"))?;

    Ok(install_id)
}

fn create_install_id() -> String {
    let now = now_unix_seconds();
    format!("install-{now}-{}", std::process::id())
}

fn write_connection(path: &Path, connection: &ServerConnection) -> Result<(), String> {
    let json = serde_json::to_string_pretty(connection)
        .map_err(|error| format!("Could not serialize server connection: {error}"))?;

    fs::write(path, json)
        .map_err(|error| format!("Could not write server connection: {error}"))
}

fn read_connection(path: &Path) -> Result<ServerConnection, String> {
    let raw = fs::read_to_string(path)
        .map_err(|error| format!("Could not read server connection: {error}"))?;

    serde_json::from_str(&raw)
        .map_err(|error| format!("Could not parse server connection: {error}"))
}

fn connection_path(app_data_dir: &Path) -> PathBuf {
    app_data_dir.join(CONNECTION_FILE_NAME)
}

fn write_upload_status(
    upload_status_path: &Path,
    status: &str,
    message: &str,
    participant_id: Option<u64>,
) -> Result<UploadSummary, String> {
    let status_file = UploadStatusFile {
        status: status.to_string(),
        message: message.to_string(),
        participant_id,
        created_at_unix_seconds: now_unix_seconds(),
    };

    let json = serde_json::to_string_pretty(&status_file)
        .map_err(|error| format!("Could not serialize upload status: {error}"))?;

    fs::write(upload_status_path, json)
        .map_err(|error| format!("Could not write upload status: {error}"))?;

    Ok(UploadSummary {
        status: status_file.status,
        message: status_file.message,
        upload_status_path: upload_status_path.to_path_buf(),
    })
}

fn response_to_string(result: Result<ureq::Response, ureq::Error>) -> Result<String, String> {
    match result {
        Ok(response) => response
            .into_string()
            .map_err(|error| format!("Could not read server response: {error}")),
        Err(ureq::Error::Status(code, response)) => {
            let body = response.into_string().unwrap_or_default();
            Err(format!("Server returned HTTP {code}: {body}"))
        }
        Err(error) => Err(format!("Could not reach server: {error}")),
    }
}

fn now_unix_seconds() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_secs()
}