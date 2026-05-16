use crate::paths;
use serde::Serialize;
use std::collections::HashMap;
use std::path::PathBuf;
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
    app_version: String,
}

#[tauri::command]
pub fn get_study_config() -> StudyConfig {
    let env_values = read_desktop_env();

    StudyConfig {
        contact_name: env_value(
            &env_values,
            "STUDY_CONTACT_NAME",
            "[add researcher name to desktop/.env]",
        ),
        contact_email: env_value(
            &env_values,
            "STUDY_CONTACT_EMAIL",
            "[add researcher email to desktop/.env]",
        ),
        participant_pool_label: env_value(
            &env_values,
            "STUDY_PARTICIPANT_POOL_LABEL",
            "[add participant pool label to desktop/.env]",
        ),
        redc_name: env_value(
            &env_values,
            "STUDY_REDC_NAME",
            "[add REDC name to desktop/.env]",
        ),
        redc_email: env_value(
            &env_values,
            "STUDY_REDC_EMAIL",
            "[add REDC email to desktop/.env]",
        ),
        help_url: env_value(
            &env_values,
            "STUDY_HELP_URL",
            "https://example.com/apps/minecraft-study/help/",
        ),
        app_version: env!("CARGO_PKG_VERSION").to_string(),
    }
}

#[tauri::command]
pub fn get_runtime_status(app: AppHandle) -> Result<paths::RuntimeStatus, String> {
    paths::runtime_status(&app)
}

pub(crate) fn qualtrics_template() -> Result<String, String> {
    let root_env = read_root_env();
    let template = env_value(&root_env, "QUALTRICS_URL_TEMPLATE", "");

    if template.is_empty() {
        return Err(String::from(
            "QUALTRICS_URL_TEMPLATE is missing from the repository root .env file.",
        ));
    }

    Ok(template)
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
        .unwrap_or(fallback)
        .to_string()
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