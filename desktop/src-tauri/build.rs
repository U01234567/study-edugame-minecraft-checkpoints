use std::collections::HashMap;
use std::fs;
use std::path::PathBuf;

fn main() {
    embed_env_files();
    tauri_build::build()
}

fn embed_env_files() {
    let manifest_dir = PathBuf::from(
        std::env::var("CARGO_MANIFEST_DIR")
            .expect("CARGO_MANIFEST_DIR should be available during build"),
    );

    let desktop_dir = manifest_dir
        .parent()
        .expect("src-tauri should have a desktop parent directory")
        .to_path_buf();

    let repo_root = desktop_dir
        .parent()
        .expect("desktop should have a repository root parent directory")
        .to_path_buf();

    let desktop_env = desktop_dir.join(".env");
    let root_env = repo_root.join(".env");

    println!("cargo:rerun-if-changed={}", desktop_env.display());
    println!("cargo:rerun-if-changed={}", root_env.display());

    let mut values = HashMap::new();

    for (key, value) in read_env_file(&root_env) {
        values.insert(key, value);
    }

    for (key, value) in read_env_file(&desktop_env) {
        values.insert(key, value);
    }

    for key in [
        "QUALTRICS_URL_TEMPLATE",
        "STUDY_CONTACT_NAME",
        "STUDY_CONTACT_EMAIL",
        "STUDY_PARTICIPANT_POOL_LABEL",
        "STUDY_REDC_NAME",
        "STUDY_REDC_EMAIL",
        "STUDY_HELP_URL",
        "MINECRAFT_STUDY_WEBSITE_ROOT",
        "MINECRAFT_STUDY_UPLOAD_RECIPIENT",
    ] {
        if let Some(value) = values.get(key) {
            println!("cargo:rustc-env={key}={value}");
        }
    }
}

fn read_env_file(path: &PathBuf) -> HashMap<String, String> {
    let mut values = HashMap::new();

    let Ok(raw) = fs::read_to_string(path) else {
        return values;
    };

    for line in raw.lines() {
        let line = line.trim();

        if line.is_empty() || line.starts_with('#') {
            continue;
        }

        let Some((key, value)) = line.split_once('=') else {
            continue;
        };

        let key = key.trim();

        if key.is_empty() {
            continue;
        }

        values.insert(key.to_string(), clean_env_value(value));
    }

    values
}

fn clean_env_value(value: &str) -> String {
    let value = value.trim();

    if value.len() >= 2 {
        let first = value.chars().next();
        let last = value.chars().last();

        if matches!((first, last), (Some('"'), Some('"')) | (Some('\''), Some('\''))) {
            return value[1..value.len() - 1].to_string();
        }
    }

    value.to_string()
}