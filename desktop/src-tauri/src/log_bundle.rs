use crate::paths::AppPaths;
use serde::Serialize;
use std::fs;
use std::fs::File;
use std::io::{Read, Write};
use std::path::{Path, PathBuf};
use std::time::{SystemTime, UNIX_EPOCH};
use zip::write::SimpleFileOptions;
use zip::{CompressionMethod, ZipWriter};

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct BundleSummary {
    pub manifest_path: PathBuf,
    pub logs_zip_path: PathBuf,
    pub minecraft_log_path: Option<PathBuf>,
    pub study_log_path: Option<PathBuf>,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
struct LocalManifest {
    schema_version: u8,
    run_id: String,
    created_at_unix_seconds: u64,
    source_mode: String,
    files: Vec<CollectedFile>,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
struct CollectedFile {
    label: String,
    relative_path: String,
    present: bool,
}

pub fn collect_and_zip(
    run_id: &str,
    run_dir: &Path,
    terminal_path: &Path,
    app_paths: &AppPaths,
) -> Result<BundleSummary, String> {
    fs::create_dir_all(run_dir)
        .map_err(|error| format!("Could not create run directory: {error}"))?;

    let minecraft_log_path = copy_minecraft_latest_log(run_dir, app_paths)?;
    let study_log_path = copy_newest_study_log(run_dir, app_paths)?;

    let manifest_path = run_dir.join("manifest.json");
    let manifest = LocalManifest {
        schema_version: 1,
        run_id: run_id.to_string(),
        created_at_unix_seconds: now_unix_seconds(),
        source_mode: "gradle-source-dev".to_string(),
        files: vec![
            collected("terminal", terminal_path, run_dir),
            collected_optional("minecraft-latest", minecraft_log_path.as_ref(), run_dir),
            collected_optional("study-log", study_log_path.as_ref(), run_dir),
        ],
    };

    let manifest_json = serde_json::to_string_pretty(&manifest)
        .map_err(|error| format!("Could not serialize local manifest: {error}"))?;

    fs::write(&manifest_path, manifest_json)
        .map_err(|error| format!("Could not write local manifest: {error}"))?;

    let logs_zip_path = run_dir.join("logs.zip");
    create_zip(
        &logs_zip_path,
        &[
            terminal_path.to_path_buf(),
            manifest_path.clone(),
            run_dir.join("minecraft-latest.log"),
            run_dir.join("study.log"),
            run_dir.join("upload-status.json"),
        ],
        run_dir,
    )?;

    Ok(BundleSummary {
        manifest_path,
        logs_zip_path,
        minecraft_log_path,
        study_log_path,
    })
}

fn copy_minecraft_latest_log(
    run_dir: &Path,
    app_paths: &AppPaths,
) -> Result<Option<PathBuf>, String> {
    let source = app_paths
        .dev_mods_custom_dir
        .join("run")
        .join("logs")
        .join("latest.log");

    if !source.exists() {
        return Ok(None);
    }

    let destination = run_dir.join("minecraft-latest.log");
    fs::copy(&source, &destination)
        .map_err(|error| format!("Could not copy Minecraft latest.log: {error}"))?;

    Ok(Some(destination))
}

fn copy_newest_study_log(
    run_dir: &Path,
    app_paths: &AppPaths,
) -> Result<Option<PathBuf>, String> {
    let Some(source) = newest_study_log(&app_paths.dev_analysis_logs_dir)? else {
        return Ok(None);
    };

    let destination = run_dir.join("study.log");
    fs::copy(&source, &destination)
        .map_err(|error| format!("Could not copy study log: {error}"))?;

    Ok(Some(destination))
}

fn newest_study_log(logs_dir: &Path) -> Result<Option<PathBuf>, String> {
    if !logs_dir.exists() {
        return Ok(None);
    }

    let newest = fs::read_dir(logs_dir)
        .map_err(|error| format!("Could not read analysis logs directory: {error}"))?
        .filter_map(Result::ok)
        .filter(|entry| {
            let path = entry.path();

            path.is_file()
                && path
                    .file_name()
                    .and_then(|name| name.to_str())
                    .map(|name| name.starts_with("study-") && name.ends_with(".log"))
                    .unwrap_or(false)
        })
        .max_by_key(|entry| {
            entry
                .metadata()
                .and_then(|metadata| metadata.modified())
                .ok()
        })
        .map(|entry| entry.path());

    Ok(newest)
}

fn create_zip(zip_path: &Path, candidates: &[PathBuf], run_dir: &Path) -> Result<(), String> {
    let zip_file = File::create(zip_path)
        .map_err(|error| format!("Could not create log zip: {error}"))?;

    let mut zip = ZipWriter::new(zip_file);
    let options = SimpleFileOptions::default()
        .compression_method(CompressionMethod::Deflated);

    for path in candidates {
        if !path.exists() || !path.is_file() {
            continue;
        }

        let relative_name = path
            .strip_prefix(run_dir)
            .unwrap_or(path)
            .to_string_lossy()
            .replace('\\', "/");

        zip.start_file(relative_name, options)
            .map_err(|error| format!("Could not add file to zip: {error}"))?;

        let mut input = File::open(path)
            .map_err(|error| format!("Could not read file for zip: {error}"))?;

        let mut buffer = Vec::new();
        input
            .read_to_end(&mut buffer)
            .map_err(|error| format!("Could not read file content for zip: {error}"))?;

        zip.write_all(&buffer)
            .map_err(|error| format!("Could not write file to zip: {error}"))?;
    }

    zip.finish()
        .map_err(|error| format!("Could not finish log zip: {error}"))?;

    Ok(())
}

fn collected(label: &str, path: &Path, run_dir: &Path) -> CollectedFile {
    CollectedFile {
        label: label.to_string(),
        relative_path: relative_path(path, run_dir),
        present: path.exists(),
    }
}

fn collected_optional(label: &str, path: Option<&PathBuf>, run_dir: &Path) -> CollectedFile {
    match path {
        Some(path) => collected(label, path, run_dir),
        None => CollectedFile {
            label: label.to_string(),
            relative_path: String::new(),
            present: false,
        },
    }
}

fn relative_path(path: &Path, run_dir: &Path) -> String {
    path.strip_prefix(run_dir)
        .unwrap_or(path)
        .to_string_lossy()
        .replace('\\', "/")
}

fn now_unix_seconds() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap_or_default()
        .as_secs()
}