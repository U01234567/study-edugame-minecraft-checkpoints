use crate::config;
use crate::paths::AppPaths;
use serde::Serialize;
use std::fs;
use std::fs::File;
use std::io::{Read, Write};
use std::path::{Path, PathBuf};
use std::str::FromStr;
use std::time::{SystemTime, UNIX_EPOCH};
use zip::write::SimpleFileOptions;
use zip::{CompressionMethod, ZipWriter};

#[derive(Debug, Clone)]
pub struct StudyLogMetadata {
    pub path: PathBuf,
    pub mcid: Option<String>,
    pub creatures_seen: Option<String>,
    pub questionnaire_button_pressed: bool,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct BundleSummary {
    pub manifest_path: PathBuf,
    pub logs_zip_path: PathBuf,
    pub encrypted_logs_path: Option<PathBuf>,
    pub minecraft_log_path: Option<PathBuf>,
    pub study_log_path: Option<PathBuf>,
    pub mcid: Option<String>,
    pub questionnaire_button_pressed: bool,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
struct LocalManifest {
    schema_version: u8,
    run_id: String,
    created_at_unix_seconds: u64,
    source_mode: String,
    mcid: Option<String>,
    questionnaire_button_pressed: bool,
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
    run_started_at: Option<SystemTime>,
) -> Result<BundleSummary, String> {
    fs::create_dir_all(run_dir)
        .map_err(|error| format!("Could not create run directory: {error}"))?;

    let minecraft_log_path = copy_minecraft_latest_log(run_dir, app_paths)?;
    let study_log = copy_newest_study_log(run_dir, app_paths, run_started_at)?;
    let study_log_path = study_log.as_ref().map(|log| log.path.clone());
    let mcid = study_log.as_ref().and_then(|log| log.mcid.clone());
    let questionnaire_button_pressed = study_log
        .as_ref()
        .map(|log| log.questionnaire_button_pressed)
        .unwrap_or(false);

    let manifest_path = run_dir.join("manifest.json");
    let manifest = LocalManifest {
        schema_version: 2,
        run_id: run_id.to_string(),
        created_at_unix_seconds: now_unix_seconds(),
        source_mode: "packaged-release".to_string(),
        mcid: mcid.clone(),
        questionnaire_button_pressed,
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

    let bundle_stem = bundle_stem(run_id, mcid.as_deref(), study_log_path.as_ref());
    let logs_zip_path = run_dir.join(format!("{bundle_stem}-logs.zip"));

    let mut zip_candidates = vec![
        terminal_path.to_path_buf(),
        manifest_path.clone(),
        run_dir.join("minecraft-latest.log"),
        run_dir.join("upload-status.json"),
    ];

    if let Some(path) = study_log_path.as_ref() {
        zip_candidates.push(path.clone());
    }

    create_zip(&logs_zip_path, &zip_candidates, run_dir)?;

    let encrypted_logs_path = match config::upload_recipient() {
        Some(recipient) => Some(encrypt_file_for_recipient(&logs_zip_path, &recipient)?),
        None => None,
    };

    Ok(BundleSummary {
        manifest_path,
        logs_zip_path,
        encrypted_logs_path,
        minecraft_log_path,
        study_log_path,
        mcid,
        questionnaire_button_pressed,
    })
}

fn copy_minecraft_latest_log(
    run_dir: &Path,
    app_paths: &AppPaths,
) -> Result<Option<PathBuf>, String> {
    let candidates = [
        run_dir
            .join("payload")
            .join("game")
            .join("run")
            .join("logs")
            .join("latest.log"),
    ];

    let Some(source) = candidates.into_iter().find(|path| path.exists()) else {
        return Ok(None);
    };

    let destination = run_dir.join("minecraft-latest.log");
    fs::copy(&source, &destination)
        .map_err(|error| format!("Could not copy Minecraft latest.log: {error}"))?;

    Ok(Some(destination))
}

fn copy_newest_study_log(
    run_dir: &Path,
    app_paths: &AppPaths,
    run_started_at: Option<SystemTime>,
) -> Result<Option<StudyLogMetadata>, String> {
    let Some(source_log) = find_newest_study_log(app_paths, run_started_at)? else {
        return Ok(None);
    };

    let source_name = source_log
        .path
        .file_name()
        .and_then(|name| name.to_str())
        .unwrap_or("study.log");

    let destination = run_dir.join(source_name);

    fs::copy(&source_log.path, &destination)
        .map_err(|error| format!("Could not copy study log: {error}"))?;

    Ok(Some(StudyLogMetadata {
        path: destination,
        mcid: source_log.mcid,
        creatures_seen: source_log.creatures_seen,
        questionnaire_button_pressed: source_log.questionnaire_button_pressed,
    }))
}

pub fn find_newest_study_log(
    app_paths: &AppPaths,
    modified_after: Option<SystemTime>,
) -> Result<Option<StudyLogMetadata>, String> {
    let mut candidates = Vec::new();

    collect_study_log_candidates_recursive(&app_paths.runs_dir, modified_after, &mut candidates, 8)?;

    let newest = candidates
        .into_iter()
        .max_by_key(|candidate| candidate.0)
        .map(|(_, path)| path);

    let Some(path) = newest else {
        return Ok(None);
    };

    let contents = fs::read_to_string(&path).unwrap_or_default();

    Ok(Some(StudyLogMetadata {
        mcid: mcid_from_study_log(&path, &contents),
        creatures_seen: creatures_seen_from_questionnaire_url(&contents),
        questionnaire_button_pressed: contents.contains("questionnaire_button_pressed"),
        path,
    }))
}

fn collect_study_log_candidates(
    logs_dir: &Path,
    modified_after: Option<SystemTime>,
    candidates: &mut Vec<(SystemTime, PathBuf)>,
) -> Result<(), String> {
    if !logs_dir.exists() {
        return Ok(());
    }

    for entry in fs::read_dir(logs_dir)
        .map_err(|error| format!("Could not read study logs directory {}: {error}", logs_dir.to_string_lossy()))?
        .filter_map(Result::ok)
    {
        let path = entry.path();

        if !is_study_log_file(&path) {
            continue;
        }

        let modified_at = entry
            .metadata()
            .and_then(|metadata| metadata.modified())
            .unwrap_or(SystemTime::UNIX_EPOCH);

        if let Some(after) = modified_after {
            if modified_at < after {
                continue;
            }
        }

        candidates.push((modified_at, path));
    }

    Ok(())
}

fn collect_study_log_candidates_recursive(
    root_dir: &Path,
    modified_after: Option<SystemTime>,
    candidates: &mut Vec<(SystemTime, PathBuf)>,
    max_depth: usize,
) -> Result<(), String> {
    if !root_dir.exists() || max_depth == 0 {
        return Ok(());
    }

    for entry in fs::read_dir(root_dir)
        .map_err(|error| format!("Could not read study logs directory {}: {error}", root_dir.to_string_lossy()))?
        .filter_map(Result::ok)
    {
        let path = entry.path();

        if path.is_dir() {
            collect_study_log_candidates_recursive(&path, modified_after, candidates, max_depth - 1)?;
            continue;
        }

        if !is_study_log_file(&path) {
            continue;
        }

        let modified_at = entry
            .metadata()
            .and_then(|metadata| metadata.modified())
            .unwrap_or(SystemTime::UNIX_EPOCH);

        if let Some(after) = modified_after {
            if modified_at < after {
                continue;
            }
        }

        candidates.push((modified_at, path));
    }

    Ok(())
}

fn is_study_log_file(path: &Path) -> bool {
    path.is_file()
        && path
            .file_name()
            .and_then(|name| name.to_str())
            .map(|name| name.starts_with("study-") && name.ends_with(".log"))
            .unwrap_or(false)
}

fn mcid_from_study_log(path: &Path, contents: &str) -> Option<String> {
    mcid_from_log_contents(contents).or_else(|| mcid_from_study_log_filename(path))
}

fn mcid_from_log_contents(contents: &str) -> Option<String> {
    for line in contents.lines() {
        for part in line.split('|') {
            let part = part.trim();
            if let Some(value) = part.strip_prefix("session_id=") {
                let value = value.trim();
                if !value.is_empty() {
                    return Some(value.to_string());
                }
            }
        }
    }

    None
}

fn mcid_from_study_log_filename(path: &Path) -> Option<String> {
    let stem = path.file_stem()?.to_str()?;
    let parts: Vec<&str> = stem.split('-').collect();

    // Mod source uses: study-yyyyMMdd-HHmmss-SSS-<player>-<ABCD>-<EFGH>.log
    if parts.len() >= 7 && parts.first() == Some(&"study") {
        let last = parts[parts.len() - 1];
        let previous = parts[parts.len() - 2];
        return Some(format!("{previous}-{last}"));
    }

    None
}

fn creatures_seen_from_questionnaire_url(contents: &str) -> Option<String> {
    for line in contents.lines().rev() {
        if !line.contains("questionnaire_button_pressed") {
            continue;
        }

        let Some(url_field) = line.split('|').find_map(|part| part.trim().strip_prefix("url=")) else {
            continue;
        };

        if let Some(value) = query_value(url_field, "CREATURES_SEEN") {
            return Some(value);
        }

        if let Some(value) = query_value(url_field, "creatures_seen") {
            return Some(value);
        }
    }

    None
}

fn query_value(url: &str, key: &str) -> Option<String> {
    let query = url.split_once('?')?.1.split('#').next().unwrap_or("");

    for pair in query.split('&') {
        let (candidate_key, value) = pair.split_once('=')?;
        if candidate_key.eq_ignore_ascii_case(key) && !value.is_empty() {
            return Some(value.replace("%2C", ",").replace("%2c", ","));
        }
    }

    None
}

fn encrypt_file_for_recipient(input_path: &Path, recipient_text: &str) -> Result<PathBuf, String> {
    let recipient = age::x25519::Recipient::from_str(recipient_text.trim())
        .map_err(|error| format!("Invalid age upload recipient: {error}"))?;

    let plaintext = fs::read(input_path)
        .map_err(|error| format!("Could not read log zip before encryption: {error}"))?;

    let encrypted = age::encrypt(&recipient, &plaintext)
        .map_err(|error| format!("Could not encrypt log zip: {error}"))?;

    let output_path = PathBuf::from(format!("{}.age", input_path.to_string_lossy()));

    fs::write(&output_path, encrypted)
        .map_err(|error| format!("Could not write encrypted log file: {error}"))?;

    Ok(output_path)
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

fn bundle_stem(run_id: &str, mcid: Option<&str>, study_log_path: Option<&PathBuf>) -> String {
    if let Some(path) = study_log_path {
        if let Some(stem) = path.file_stem().and_then(|name| name.to_str()) {
            return safe_file_stem(stem);
        }
    }

    match mcid {
        Some(mcid) => format!("minecraft-study-{}", safe_file_stem(mcid)),
        None => safe_file_stem(run_id),
    }
}

fn safe_file_stem(value: &str) -> String {
    value
        .chars()
        .map(|character| {
            if character.is_ascii_alphanumeric() || matches!(character, '-' | '_' | '.') {
                character
            } else {
                '_'
            }
        })
        .collect()
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