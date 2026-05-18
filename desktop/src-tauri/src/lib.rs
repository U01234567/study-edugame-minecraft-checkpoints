mod config;
mod game_runner;
mod log_bundle;
mod paths;
mod questionnaire;
mod upload;

use tauri::Manager;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .invoke_handler(tauri::generate_handler![
            config::get_study_config,
            config::get_runtime_status,
            game_runner::start_study,
            game_runner::abort_study,
            questionnaire::open_questionnaire,
            upload::ensure_server_connection,
            upload::get_connection_status,
            upload::retry_pending_log_upload,
        ])
        .on_window_event(|window, event| {
            if matches!(event, tauri::WindowEvent::CloseRequested { .. }) {
                let _ = upload::send_status(
                    window.app_handle(),
                    "program_killed",
                    serde_json::json!({
                        "message": "The desktop app window was closed while Minecraft may have been running.",
                        "phase": "program_killed",
                        "killReason": "app_window_closed",
                    }),
                );

                game_runner::terminate_active_study_process();

                if let Some(app) = window.app_handle().try_state::<tauri::async_runtime::Mutex<()>>() {
                    drop(app);
                }
            }
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}