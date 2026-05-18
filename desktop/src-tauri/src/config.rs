use crate::paths;
use serde::Serialize;
use std::collections::HashMap;
use std::fs;
use std::path::{Path, PathBuf};
use tauri::AppHandle;

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct StudyConfig {
    contact_name: String,
    contact_email: String,
    participant_pool_label: String,
    redc_name: String,
    redc_email: String,
    help_url: String,
    website_root: String,
    upload_recipient_configured: bool,
    app_version: String,
}

#[tauri::command]
pub fn get_study_config() -> StudyConfig {
    let env_values = read_desktop_env();
    let root = website_root();

    StudyConfig {
        contact_name: env_value(
            &env_values,
            "STUDY_CONTACT_NAME",
            "[researcher name missing]",
        ),
        contact_email: env_value(
            &env_values,
            "STUDY_CONTACT_EMAIL",
            "[researcher email missing]",
        ),
        participant_pool_label: env_value(
            &env_values,
            "STUDY_PARTICIPANT_POOL_LABEL",
            "[participant pool missing]",
        ),
        redc_name: env_value(
            &env_values,
            "STUDY_REDC_NAME",
            "[REDC name missing]",
        ),
        redc_email: env_value(
            &env_values,
            "STUDY_REDC_EMAIL",
            "[REDC email missing]",
        ),
        help_url: env_value(
            &env_values,
            "STUDY_HELP_URL",
            &format!("{root}apps/minecraft-study/help/"),
        ),
        website_root: root,
        upload_recipient_configured: upload_recipient().is_some(),
        app_version: env!("CARGO_PKG_VERSION").to_string(),
    }
}

#[tauri::command]
pub fn get_runtime_status(app: AppHandle) -> Result<paths::RuntimeStatus, String> {
    paths::runtime_status(app)
}

pub(crate) fn write_runtime_env_file(run_dir: &Path) -> Result<PathBuf, String> {
    let template = qualtrics_template()?;
    let path = run_dir.join("study-runtime.env");
    let contents = format!("QUALTRICS_URL_TEMPLATE={}\n", template.trim());

    fs::write(&path, contents)
        .map_err(|error| format!("Could not write runtime study env file: {error}"))?;

    Ok(path)
}

pub(crate) fn qualtrics_template() -> Result<String, String> {
    let mut env_values = read_root_env();

    // The desktop environment file is read after the root environment file so release-specific values can override shared defaults.
    for (key, value) in read_desktop_env() {
        env_values.insert(key, value);
    }

    let template = env_value(&env_values, "QUALTRICS_URL_TEMPLATE", "");

    if template.is_empty() {
        return Err(String::from(
            "The questionnaire link is missing from this build.",
        ));
    }

    Ok(template)
}

pub(crate) fn upload_recipient() -> Option<String> {
    let env_values = read_desktop_env();
    let value = env_value(&env_values, "MINECRAFT_STUDY_UPLOAD_RECIPIENT", "");

    if value.trim().is_empty() {
        None
    } else {
        Some(value)
    }
}

pub(crate) fn website_root() -> String {
    let env_values = read_desktop_env();
    let value = env_value(
        &env_values,
        "MINECRAFT_STUDY_WEBSITE_ROOT",
        "https://example.com/",
    );

    with_trailing_slash(value.trim())
}

pub(crate) fn website_url(path: &str) -> String {
    let root = website_root();
    let path = path.trim_start_matches('/');

    format!("{root}{path}")
}

fn with_trailing_slash(value: &str) -> String {
    let trimmed = value.trim();

    if trimmed.ends_with('/') {
        trimmed.to_string()
    } else {
        format!("{trimmed}/")
    }
}

fn env_value(values: &HashMap<String, String>, key: &str, fallback: &str) -> String {
    if let Ok(value) = std::env::var(key) {
        if !value.trim().is_empty() {
            return value;
        }
    }

    values
        .get(key)
        .map(|value| value.trim())
        .filter(|value| !value.is_empty())
        .map(str::to_string)
        .or_else(|| compiled_env_value(key))
        .unwrap_or_else(|| fallback.to_string())
}

fn compiled_env_value(key: &str) -> Option<String> {
    let value = match key {
        "QUALTRICS_URL_TEMPLATE" => option_env!("QUALTRICS_URL_TEMPLATE"),
        "STUDY_CONTACT_NAME" => option_env!("STUDY_CONTACT_NAME"),
        "STUDY_CONTACT_EMAIL" => option_env!("STUDY_CONTACT_EMAIL"),
        "STUDY_PARTICIPANT_POOL_LABEL" => option_env!("STUDY_PARTICIPANT_POOL_LABEL"),
        "STUDY_REDC_NAME" => option_env!("STUDY_REDC_NAME"),
        "STUDY_REDC_EMAIL" => option_env!("STUDY_REDC_EMAIL"),
        "STUDY_HELP_URL" => option_env!("STUDY_HELP_URL"),
        "MINECRAFT_STUDY_WEBSITE_ROOT" => option_env!("MINECRAFT_STUDY_WEBSITE_ROOT"),
        "MINECRAFT_STUDY_UPLOAD_RECIPIENT" => option_env!("MINECRAFT_STUDY_UPLOAD_RECIPIENT"),
        _ => None,
    }?;

    if value.trim().is_empty() {
        None
    } else {
        Some(value.to_string())
    }
}

fn read_desktop_env() -> HashMap<String, String> {
    let mut candidates = Vec::new();

    if let Ok(explicit_path) = std::env::var("MINECRAFT_STUDY_DESKTOP_ENV") {
        candidates.push(PathBuf::from(explicit_path));
    }

    if let Some(desktop_dir) = desktop_dir() {
        candidates.push(desktop_dir.join(".env"));
    }

    read_first_existing_env(candidates)
}

fn read_root_env() -> HashMap<String, String> {
    let mut candidates = Vec::new();

    if let Ok(explicit_path) = std::env::var("MINECRAFT_STUDY_ROOT_ENV") {
        candidates.push(PathBuf::from(explicit_path));
    }

    if let Some(root_dir) = repo_root_dir() {
        candidates.push(root_dir.join(".env"));
    }

    read_first_existing_env(candidates)
}

fn read_first_existing_env(candidates: Vec<PathBuf>) -> HashMap<String, String> {
    for path in candidates {
        if !path.exists() {
            continue;
        }

        let Ok(iter) = dotenvy::from_path_iter(&path) else {
            continue;
        };

        let mut values = HashMap::new();

        for item in iter.flatten() {
            let (key, value) = item;
            values.insert(key, value);
        }

        return values;
    }

    HashMap::new()
}

fn desktop_dir() -> Option<PathBuf> {
    PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .parent()
        .map(PathBuf::from)
}

fn repo_root_dir() -> Option<PathBuf> {
    desktop_dir()?.parent().map(PathBuf::from)
}