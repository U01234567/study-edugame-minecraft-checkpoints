use serde::Serialize;
use std::collections::HashMap;
use std::path::PathBuf;

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct StudyConfig {
    contact_name: String,
    contact_email: String,
    participant_pool_label: String,
    redc_name: String,
    redc_email: String,
}

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct StudyRunPreview {
    remote: bool,
    minecraft_available: bool,
    questionnaire_template: String,
    message: String,
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
    }
}

#[tauri::command]
pub fn prepare_study_run() -> Result<StudyRunPreview, String> {
    let template = qualtrics_template()?;
    let remote_template = add_remote_flag(&template);

    Ok(StudyRunPreview {
        remote: true,
        minecraft_available: false,
        questionnaire_template: remote_template,
        message: String::from(
            "Desktop backend is connected. Minecraft launch/upload is not connected yet.",
        ),
    })
}

#[tauri::command]
pub fn open_questionnaire(mcid: String, creatures_seen: String) -> Result<String, String> {
    let template = qualtrics_template()?;
    let url = build_questionnaire_url(&template, &mcid, &creatures_seen);

    tauri_plugin_opener::open_url(&url, None::<&str>)
        .map_err(|error| format!("Could not open questionnaire URL: {error}"))?;

    Ok(url)
}

fn build_questionnaire_url(template: &str, mcid: &str, creatures_seen: &str) -> String {
    add_remote_flag(template)
        .replace("{MCID}", mcid)
        .replace("{CREATURES_SEEN}", creatures_seen)
}

fn add_remote_flag(raw_url: &str) -> String {
    if raw_url.contains("REMOTE=") {
        return raw_url.to_string();
    }

    let (base, fragment) = match raw_url.split_once('#') {
        Some((base, fragment)) => (base, format!("#{fragment}")),
        None => (raw_url, String::new()),
    };

    match base.split_once('?') {
        Some((prefix, query)) if query.is_empty() => {
            format!("{prefix}?REMOTE=1{fragment}")
        }
        Some((prefix, query)) => {
            format!("{prefix}?REMOTE=1&{query}{fragment}")
        }
        None => {
            format!("{base}?REMOTE=1{fragment}")
        }
    }
}

fn qualtrics_template() -> Result<String, String> {
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