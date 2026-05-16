mod config;
mod game_runner;
mod log_bundle;
mod paths;
mod questionnaire;
mod upload;

use tauri::{Manager, UserAttentionType, WindowEvent};

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .setup(|app| {
            if let Some(window) = app.get_webview_window("main") {
                let _ = window.show();
                let _ = window.unminimize();
                let _ = window.set_focus();
                let _ = window.request_user_attention(Some(UserAttentionType::Informational));
            }

            Ok(())
        })
        .on_window_event(|_window, event| {
            if let WindowEvent::CloseRequested { .. } = event {
                game_runner::terminate_active_study_process();
            }
        })
        .invoke_handler(tauri::generate_handler![
            config::get_study_config,
            config::get_runtime_status,
            game_runner::start_study,
            paths::open_app_data_folder,
            paths::open_last_run_folder,
            questionnaire::open_questionnaire,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}