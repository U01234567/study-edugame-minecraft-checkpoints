use crate::config;

#[tauri::command]
pub fn open_questionnaire(mcid: String, creatures_seen: String) -> Result<String, String> {
    let template = config::qualtrics_template()?;
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