use serde::Serialize;
use std::fs;
use std::path::{Path, PathBuf};
use std::time::{SystemTime, UNIX_EPOCH};

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct UploadSummary {
    pub status: String,
    pub upload_status_path: PathBuf,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
struct UploadStatusFile {
    status: String,
    message: String,
    created_at_unix_seconds: u64,
}

pub fn mark_not_configured(run_dir: &Path) -> Result<UploadSummary, String> {
    let upload_status_path = run_dir.join("upload-status.json");

    let status_file = UploadStatusFile {
        status: "not_configured".to_string(),
        message: "Server upload is not implemented in this app milestone.".to_string(),
        created_at_unix_seconds: now_unix_seconds(),
    };

    let json = serde_json::to_string_pretty(&status_file)
        .map_err(|error| format!("Could not serialize upload status: {error}"))?;

    fs::write(&upload_status_path, json)
        .map_err(|error| format!("Could not write upload status: {error}"))?;

    Ok(UploadSummary {
        status: status_file.status,
        upload_status_path,
    })
}

fn now_unix_seconds() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_secs()
}