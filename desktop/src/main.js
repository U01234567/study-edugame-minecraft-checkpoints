const PUBLIC_LOGO_PATH = "/assets/uni-logo.png";
const PUBLIC_CONTROLS_IMAGE_PATH = "/assets/print_controls.png";

const START_HINT_MESSAGE =
  "Game is loading and will open automatically in fullscreen in about 15 seconds.";
const SERVER_OFFLINE_MESSAGE =
  "Not connected to the study server. Make sure you have a network connection on your device.";
const LOG_RETRY_MESSAGE =
  "Close this app, check your network connection, and reopen the app. The app will try again to share the logs.";
const START_FAILED_MESSAGE =
  "Could not start the study. Please close and reopen this app, then try again. If it still fails, reinstall the app.";
const TECHNICAL_ERROR_PATTERNS = [
  /https?:\/\//i,
  /could not reach server/i,
  /dns failed/i,
  /resolve dns name/i,
  /no such host/i,
  /os error/i,
  /encrypted upload failed/i,
];
const PROGRESS_STORAGE_KEY = "minecraftStudy.progress.v1";

const DEFAULT_STUDY_CONFIG = {
  contactName: "[researcher name missing]",
  contactEmail: "[researcher email missing]",
  participantPoolLabel: "[participant pool missing]",
  redcName: "[REDC name missing]",
  redcEmail: "[REDC email missing]",
  helpUrl: "https://example.com/apps/minecraft-study/",
  websiteRoot: "https://example.com/",
  uploadRecipientConfigured: false,
  appVersion: "1.1.0",
};

const elements = {
  appHeader: document.querySelector(".app-header"),
  logo: document.querySelector("#study-logo"),
  logoPlaceholder: document.querySelector("#logo-placeholder"),
  controlsImage: document.querySelector("#controls-image"),
  controlsPlaceholder: document.querySelector("#controls-placeholder"),
  startButton: document.querySelector("#start-button"),
  startHint: document.querySelector("#start-hint"),
  helpButton: document.querySelector("#help-button"),
  uninstallButton: document.querySelector("#uninstall-button"),
  helpDialog: document.querySelector("#help-dialog"),
  uninstallDialog: document.querySelector("#uninstall-dialog"),
  helpTitle: document.querySelector("#help-title"),
  helpMessage: document.querySelector("#help-message"),
  connectionStatus: document.querySelector("#connection-status"),
  participantId: document.querySelector("#participant-id"),
  todoToggle: document.querySelector("#todo-toggle"),
  todoPopover: document.querySelector("#todo-popover"),
  todoClose: document.querySelector("#todo-close"),
  statusTitle: document.querySelector("#status-title"),
  statusDetail: document.querySelector("#status-detail"),
  statusCard: document.querySelector("#main-status"),
  studySheet: document.querySelector("#study-sheet"),
  completionScreen: document.querySelector("#completion-screen"),
};

let studyConfig = DEFAULT_STUDY_CONFIG;
let isStudyRunning = false;
let isStudyLocked = false;
let currentConnectionStatus = {
  connected: false,
  participantId: null,
  message: SERVER_OFFLINE_MESSAGE,
};

let todoState = {
  game: "pending",
  questionnaire: "pending",
  logs: "pending",
};

function getInvoke() {
  return window.__TAURI__?.core?.invoke ?? null;
}

function getListen() {
  return window.__TAURI__?.event?.listen ?? null;
}

async function invokeBackend(command, args = {}) {
  const invoke = getInvoke();

  if (!invoke) {
    throw new Error("Tauri backend is not available in this preview context.");
  }

  return invoke(command, args);
}

async function loadPrivateStudyConfig() {
  try {
    const privateConfig = await invokeBackend("get_study_config");

    return {
      ...DEFAULT_STUDY_CONFIG,
      ...privateConfig,
    };
  } catch {
    return DEFAULT_STUDY_CONFIG;
  }
}

function applyStudyText(config) {
  document.querySelectorAll("[data-study-field]").forEach((element) => {
    const fieldName = element.dataset.studyField;

    if (fieldName && config[fieldName]) {
      element.textContent = config[fieldName];
    }
  });

  document.querySelectorAll("[data-study-email]").forEach((element) => {
    const fieldName = element.dataset.studyEmail;
    const email = fieldName ? config[fieldName] : "";

    if (!email || email.startsWith("[")) {
      element.textContent = email || "[email missing]";
      element.removeAttribute("href");
      return;
    }

    element.textContent = email;
    element.href = `mailto:${email}`;
  });
}

function loadOptionalImage(imageElement, placeholderElement, src) {
  if (!imageElement || !placeholderElement || !src) {
    return;
  }

  const probe = new Image();

  probe.addEventListener("load", () => {
    imageElement.src = src;
    imageElement.hidden = false;
    placeholderElement.hidden = true;
  });

  probe.addEventListener("error", () => {
    imageElement.hidden = true;
    placeholderElement.hidden = false;
  });

  probe.src = src;
}

function applyStudyAssets() {
  loadOptionalImage(elements.logo, elements.logoPlaceholder, PUBLIC_LOGO_PATH);
  loadOptionalImage(
    elements.controlsImage,
    elements.controlsPlaceholder,
    PUBLIC_CONTROLS_IMAGE_PATH
  );
}

function sanitizeUserFacingMessage(message, fallback = "") {
  const text = String(message || "").trim();

  if (!text) {
    return fallback;
  }

  if (TECHNICAL_ERROR_PATTERNS.some((pattern) => pattern.test(text))) {
    return fallback;
  }

  return text;
}

function serverOfflineMessage(message = "") {
  return sanitizeUserFacingMessage(message, SERVER_OFFLINE_MESSAGE);
}

function logRetryMessage(message = "") {
  return sanitizeUserFacingMessage(message, LOG_RETRY_MESSAGE);
}

function defaultTodoNumber(key) {
  return {
    game: "2",
    questionnaire: "3",
    logs: "4",
  }[key] || "";
}

function saveTodoState() {
  try {
    window.localStorage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(todoState));
  } catch {
    // Local storage is best-effort only; backend logs still remain on disk.
  }
}

function restoreTodoState() {
  try {
    const raw = window.localStorage.getItem(PROGRESS_STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      todoState = {
        ...todoState,
        ...parsed,
      };
    }
  } catch {
    todoState = {
      game: "pending",
      questionnaire: "pending",
      logs: "pending",
    };
  }

  Object.entries(todoState).forEach(([key, state]) => applyTodoVisualState(key, state));
}

function applyTodoVisualState(key, state) {
  document.querySelectorAll(`[data-todo="${key}"]`).forEach((item) => {
    item.classList.toggle("is-complete", state === "complete");
    item.classList.toggle("is-failed", state === "failed");

    const check = item.querySelector(".todo-check");
    if (!check) {
      return;
    }

    if (state === "complete") {
      check.textContent = "✓";
    } else if (state === "failed") {
      check.textContent = "×";
    } else {
      check.textContent = defaultTodoNumber(key);
    }
  });
}

function setTodoState(key, state) {
  todoState[key] = state;
  applyTodoVisualState(key, state);
  saveTodoState();
}

function setTodoComplete(key) {
  setTodoState(key, "complete");
}

function setTodoFailed(key) {
  setTodoState(key, "failed");
}

function setTodoPending(key) {
  setTodoState(key, "pending");
}

function isTodoComplete(key) {
  return todoState[key] === "complete";
}

function isAllDoneExceptLogs() {
  return isTodoComplete("game") && isTodoComplete("questionnaire");
}

function showAllDoneButLogsNotShared(detail = "") {
  setTodoFailed("logs");
  setStatus(
    "All done, but logs were not shared",
    logRetryMessage(detail),
    "error"
  );
  toggleTodoPopover(true);
}

function setStatus(title, detail = "", kind = "normal") {
  if (elements.statusTitle) {
    elements.statusTitle.textContent = title;
  }

  if (elements.statusDetail) {
    elements.statusDetail.textContent = detail;
  }

  if (elements.statusCard) {
    elements.statusCard.classList.toggle("is-error", kind === "error");
    elements.statusCard.classList.toggle("is-success", kind === "success");
    elements.statusCard.classList.toggle("is-warning", kind === "warning");
  }
}

function setStartButtonState(state) {
  if (!elements.startButton) {
    return;
  }

  const redStates = state === "abort" || state === "aborting";
  elements.startButton.classList.toggle("button-abort", redStates);
  elements.startButton.classList.toggle("button-start", !redStates);

  if (state === "abort") {
    elements.startButton.disabled = false;
    elements.startButton.textContent = "Abort game";
  } else if (state === "aborting") {
    elements.startButton.disabled = true;
    elements.startButton.textContent = "Aborting...";
  } else if (state === "locked") {
    elements.startButton.disabled = true;
    elements.startButton.textContent = "Completed";
  } else if (state === "busy") {
    elements.startButton.disabled = true;
    elements.startButton.textContent = "Working...";
  } else {
    elements.startButton.disabled = false;
    elements.startButton.textContent = "Start!";
  }
}

function showCompletionScreen() {
  if (elements.studySheet) {
    elements.studySheet.hidden = true;
  }

  if (elements.completionScreen) {
    elements.completionScreen.hidden = false;
  }
}

function setConnectionStatus(status) {
  currentConnectionStatus = {
    connected: Boolean(status?.connected),
    participantId: status?.participantId ?? null,
    message: status?.connected
      ? sanitizeUserFacingMessage(status?.message, "")
      : serverOfflineMessage(status?.message),
  };

  if (!elements.connectionStatus || !elements.participantId) {
    return;
  }

  if (currentConnectionStatus.connected) {
    elements.connectionStatus.textContent = "Yes";
    elements.connectionStatus.classList.remove("status-pill-bad");
    elements.connectionStatus.classList.add("status-pill-good");
  } else {
    elements.connectionStatus.textContent = "No";
    elements.connectionStatus.classList.remove("status-pill-good");
    elements.connectionStatus.classList.add("status-pill-bad");
  }

  elements.participantId.textContent = currentConnectionStatus.participantId
    ? String(currentConnectionStatus.participantId)
    : "Not assigned yet";
}

async function ensureConnectionStatus() {
  try {
    const status = await invokeBackend("ensure_server_connection");
    setConnectionStatus(status);
    return currentConnectionStatus;
  } catch {
    setConnectionStatus({
      connected: false,
      participantId: null,
      message: SERVER_OFFLINE_MESSAGE,
    });
    return currentConnectionStatus;
  }
}

async function refreshConnectionStatus() {
  try {
    const status = await invokeBackend("get_connection_status");
    setConnectionStatus(status);
    return currentConnectionStatus;
  } catch {
    setConnectionStatus({
      connected: false,
      participantId: null,
      message: SERVER_OFFLINE_MESSAGE,
    });
    return currentConnectionStatus;
  }
}

function applyOpeningConnectionStatus(status) {
  if (todoState.logs === "complete") {
    return;
  }

  if (isAllDoneExceptLogs() && todoState.logs === "failed") {
    showAllDoneButLogsNotShared(LOG_RETRY_MESSAGE);
    return;
  }

  if (status.connected) {
    setStatus(
      "Ready to start",
      "Read the study information below, then press Start! when you are ready."
    );
  } else {
    setStatus(
      "Not connected to the study server",
      status.message || SERVER_OFFLINE_MESSAGE,
      "error"
    );
  }
}

function showHelp(title, message) {
  elements.helpTitle.textContent = title;
  elements.helpMessage.textContent = message;
  showDialog(elements.helpDialog);
}

function showDialog(dialog) {
  if (typeof dialog.showModal === "function") {
    dialog.showModal();
  } else {
    dialog.setAttribute("open", "open");
  }
}

function closeDialog(button) {
  const dialog = button.closest("dialog");

  if (!dialog) {
    return;
  }

  if (typeof dialog.close === "function") {
    dialog.close();
  } else {
    dialog.removeAttribute("open");
  }
}

function toggleTodoPopover(forceOpen = null) {
  const shouldOpen =
    forceOpen === null ? elements.todoPopover.hidden : Boolean(forceOpen);

  elements.todoPopover.hidden = !shouldOpen;
  elements.todoToggle.setAttribute("aria-expanded", String(shouldOpen));
}

function buildHelpMessage() {
  const hasConfiguredContactEmail =
    studyConfig.contactEmail && !studyConfig.contactEmail.startsWith("[");
  const contactLine = hasConfiguredContactEmail
    ? `please contact me (Jonas) at ${studyConfig.contactEmail}. WhatsApp or Instagram is also fine if you already have my details.`
    : "please contact me (Jonas). WhatsApp or Instagram is also fine if you already have my details.";

  return `Most problems are fixed by closing and reopening this app. If anything still does not work, ${contactLine}`;
}

function initialiseDialogs() {
  elements.helpButton.addEventListener("click", () => {
    showHelp("Help", buildHelpMessage());
    void refreshConnectionStatus();
  });

  elements.uninstallButton.addEventListener("click", () => {
    showDialog(elements.uninstallDialog);
  });

  document.querySelectorAll("[data-close-dialog]").forEach((button) => {
    button.addEventListener("click", () => closeDialog(button));
  });
}

function initialiseTodoPopover() {
  elements.todoToggle.addEventListener("click", () => toggleTodoPopover());
  elements.todoClose.addEventListener("click", () => toggleTodoPopover(false));

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !elements.todoPopover.hidden) {
      toggleTodoPopover(false);
    }
  });

  document.addEventListener("click", (event) => {
    if (
      elements.todoPopover.hidden ||
      elements.todoPopover.contains(event.target) ||
      elements.todoToggle.contains(event.target)
    ) {
      return;
    }

    toggleTodoPopover(false);
  });
}

function initialiseStickyHeader() {
  const shrinkAfterPx = 48;
  const expandBeforePx = 4;
  let isCompact = false;

  const updateHeader = () => {
    const scrollY = window.scrollY || document.documentElement.scrollTop || 0;

    if (!isCompact && scrollY > shrinkAfterPx) {
      isCompact = true;
      elements.appHeader.classList.add("is-scrolled");
      elements.todoPopover.classList.add("is-header-scrolled");
      return;
    }

    if (isCompact && scrollY < expandBeforePx) {
      isCompact = false;
      elements.appHeader.classList.remove("is-scrolled");
      elements.todoPopover.classList.remove("is-header-scrolled");
    }
  };

  updateHeader();
  window.addEventListener("scroll", updateHeader, { passive: true });
}

async function initialiseBackendEvents() {
  const listen = getListen();

  if (!listen) {
    return;
  }

  await listen("study-status", (event) => {
    const payload = event.payload || {};
    const phase = payload.phase || "status";
    const message = payload.message || "";

    if (phase === "starting") {
      setStatus("Starting Minecraft...", message);
      setStartButtonState("abort");
    } else if (phase === "game_started") {
      setStatus("Minecraft is running.", message);
      setStartButtonState("abort");
    } else if (phase === "questionnaire_button_pressed") {
      setTodoComplete("game");
      setTodoComplete("questionnaire");
      setStatus(
        "Questionnaire opened.",
        message || "Minecraft will close automatically in about 20 seconds. Keep this app open so it can send the encrypted logs.",
        "success"
      );
      setStartButtonState("locked");
      isStudyLocked = true;
      toggleTodoPopover(true);
    } else if (phase === "closing_after_questionnaire") {
      setStatus("Closing Minecraft...", message, "success");
      setStartButtonState("locked");
    } else if (phase === "collecting_logs") {
      setStatus("Collecting logs...", message);
      setStartButtonState("busy");
    } else if (phase === "logs_packaged") {
      setStatus("Logs encrypted.", message);
    } else if (phase === "uploading_logs") {
      setStatus("Sharing encrypted logs...", message);
    } else if (phase === "logs_uploaded") {
      setTodoComplete("logs");
      setStatus("Encrypted logs shared.", message, "success");
    } else if (phase === "upload_failed") {
      if (isAllDoneExceptLogs()) {
        showAllDoneButLogsNotShared(message);
      } else {
        setTodoFailed("logs");
        setStatus(
          "Automatic log sharing failed.",
          logRetryMessage(message),
          "error"
        );
        toggleTodoPopover(true);
      }
    } else if (phase === "completed_upload_failed") {
      setTodoComplete("game");
      setTodoComplete("questionnaire");
      showAllDoneButLogsNotShared(message);
      setStartButtonState("locked");
      isStudyLocked = true;
    } else if (phase === "completed") {
      setTodoComplete("game");
      setTodoComplete("questionnaire");
      setTodoComplete("logs");
      setStatus(
        "Study finished.",
        message || "Everything was completed and the encrypted logs were shared.",
        "success"
      );
      showCompletionScreen();
      setStartButtonState("locked");
      isStudyLocked = true;
    } else if (phase === "game_failed") {
      setStatus(
        "Minecraft closed with an issue.",
        sanitizeUserFacingMessage(message, START_FAILED_MESSAGE),
        "error"
      );
    } else if (phase === "aborted") {
      if (!isTodoComplete("questionnaire")) {
        setTodoPending("game");
      }
      setStatus(
        "Game aborted.",
        sanitizeUserFacingMessage(
          message,
          "The game was closed and available logs were collected."
        ),
        "warning"
      );
    } else {
      setStatus("Working...", message);
    }
  });
}

async function abortStudy() {
  try {
    setStartButtonState("aborting");
    await invokeBackend("abort_study");
    setStatus(
      "Game abort requested.",
      "Minecraft is being closed. The app will collect any logs that are available.",
      "warning"
    );
  } catch {
    setStatus(
      "Could not abort the game.",
      "Please close Minecraft manually, then reopen this app.",
      "error"
    );
    setStartButtonState("abort");
  }
}

function initialiseStartButton() {
  elements.startButton.addEventListener("click", async () => {
    if (isStudyLocked) {
      return;
    }

    if (isStudyRunning) {
      await abortStudy();
      return;
    }

    isStudyRunning = true;
    setStartButtonState("abort");

    if (elements.startHint) {
      elements.startHint.hidden = false;
      elements.startHint.textContent = START_HINT_MESSAGE;
    }

    setStatus(
      "Starting Minecraft...",
      START_HINT_MESSAGE
    );

    try {
      await ensureConnectionStatus();

      const result = await invokeBackend("start_study");

      if (result.questionnaireButtonPressed) {
        setTodoComplete("questionnaire");
      }

      if (result.status === "completed") {
        setTodoComplete("game");

        if (result.uploadStatus === "completed") {
          setTodoComplete("logs");
          setStatus(
            "Study finished.",
            "You answered the questionnaire and encrypted logs were shared successfully.",
            "success"
          );
          showCompletionScreen();
          isStudyLocked = true;
          setStartButtonState("locked");
        } else {
          setTodoComplete("questionnaire");
          showAllDoneButLogsNotShared(LOG_RETRY_MESSAGE);
          isStudyLocked = true;
          setStartButtonState("locked");
        }
      } else if (result.status === "completed_upload_failed") {
        setTodoComplete("game");
        setTodoComplete("questionnaire");
        showAllDoneButLogsNotShared(result.message);
        isStudyLocked = true;
        setStartButtonState("locked");
      } else if (result.status === "aborted") {
        if (!isTodoComplete("questionnaire")) {
          setTodoPending("game");
        }
        setStatus(
          "Game aborted.",
          sanitizeUserFacingMessage(
            result.message,
            "The game was closed and available logs were collected."
          ),
          "warning"
        );
        setStartButtonState("ready");
      } else {
        setStatus(
          "Minecraft closed with an issue.",
          sanitizeUserFacingMessage(
            result.message,
            "Available local information was saved where possible."
          ),
          "error"
        );
        setStartButtonState("ready");
      }
    } catch {
      setStatus("Start failed.", START_FAILED_MESSAGE, "error");

      showHelp("Start failed", START_FAILED_MESSAGE);
      setStartButtonState("ready");
    } finally {
      isStudyRunning = false;

      if (!isStudyLocked) {
        setStartButtonState("ready");
      }

      if (elements.startHint) {
        elements.startHint.hidden = true;
        elements.startHint.textContent = START_HINT_MESSAGE;
      }
    }
  });
}

async function retryPendingLogUploadIfNeeded() {
  if (!(isAllDoneExceptLogs() && todoState.logs === "failed")) {
    return;
  }

  setStatus(
    "All done, but logs were not shared",
    "Trying again to share the encrypted logs now. If this fails again: " + LOG_RETRY_MESSAGE,
    "warning"
  );

  try {
    const result = await invokeBackend("retry_pending_log_upload");

    if (result.status === "completed") {
      setTodoComplete("logs");
      setStatus(
        "Study finished.",
        "Everything was completed and the encrypted logs were shared.",
        "success"
      );
      showCompletionScreen();
      setStartButtonState("locked");
      isStudyLocked = true;
    } else {
      showAllDoneButLogsNotShared(result.message);
    }
  } catch {
    showAllDoneButLogsNotShared(LOG_RETRY_MESSAGE);
  }
}

async function showRuntimeStatus() {
  // No visible runtime status is shown before the participant clicks Start.
}

window.addEventListener("DOMContentLoaded", async () => {
  studyConfig = await loadPrivateStudyConfig();

  applyStudyText(studyConfig);
  applyStudyAssets();
  restoreTodoState();

  initialiseDialogs();
  initialiseTodoPopover();
  initialiseStickyHeader();
  await initialiseBackendEvents();
  initialiseStartButton();

  const openingStatus = await ensureConnectionStatus();
  applyOpeningConnectionStatus(openingStatus);
  await retryPendingLogUploadIfNeeded();
  await showRuntimeStatus();
});
