const PUBLIC_LOGO_PATH = "/assets/uni-logo.png";
const PUBLIC_CONTROLS_IMAGE_PATH = "/assets/print_controls.png";

const DEFAULT_STUDY_CONFIG = {
  contactName: "[add researcher name to desktop/.env]",
  contactEmail: "[add researcher email to desktop/.env]",
  participantPoolLabel: "[add participant pool label to desktop/.env]",
  redcName: "[add REDC name to desktop/.env]",
  redcEmail: "[add REDC email to desktop/.env]",
  helpUrl: "https://example.com/apps/minecraft-study/help/",
  appVersion: "0.1.0",
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
  todoToggle: document.querySelector("#todo-toggle"),
  todoPopover: document.querySelector("#todo-popover"),
  todoClose: document.querySelector("#todo-close"),
  statusTitle: document.querySelector("#status-title"),
  statusDetail: document.querySelector("#status-detail"),
  openRunFolderButton: document.querySelector("#open-run-folder-button"),
};

let studyConfig = DEFAULT_STUDY_CONFIG;

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
      element.textContent = email || "[add email to desktop/.env]";
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

function setTodoComplete(key) {
  document.querySelectorAll(`[data-todo="${key}"]`).forEach((item) => {
    item.classList.add("is-complete");

    const check = item.querySelector(".todo-check");
    if (check) {
      check.textContent = "✓";
    }
  });
}

function setStatus(title, detail = "", kind = "normal") {
  if (elements.statusTitle) {
    elements.statusTitle.textContent = title;
  }

  if (elements.statusDetail) {
    elements.statusDetail.textContent = detail;
  }

  const statusCard = document.querySelector(".status-card");
  if (statusCard) {
    statusCard.classList.toggle("is-error", kind === "error");
    statusCard.classList.toggle("is-success", kind === "success");
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

function initialiseDialogs() {
  elements.helpButton.addEventListener("click", () => {
    showHelp(
      "Help",
      `If Minecraft does not start, close Minecraft if it is open, reopen this app, and try again. If the problem remains, contact ${studyConfig.contactEmail}.`
    );
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
    } else if (phase === "game_started") {
      setStatus("Minecraft is running.", message);
    } else if (phase === "collecting_logs") {
      setTodoComplete("game");
      setStatus("Collecting logs...", message);
    } else if (phase === "logs_packaged") {
      setTodoComplete("logs");
      setStatus("Logs collected.", message);
    } else if (phase === "completed") {
      setStatus(
        "Study run finished.",
        "The game has closed and local logs were collected. Continue with the questionnaire if it opened from Minecraft.",
        "success"
      );
    } else if (phase === "game_failed") {
      setStatus(
        "Minecraft closed with an issue.",
        message,
        "error"
      );
    } else {
      setStatus("Working...", message);
    }
  });
}

function initialiseOpenRunFolderButton() {
  if (!elements.openRunFolderButton) {
    return;
  }

  elements.openRunFolderButton.addEventListener("click", async () => {
    try {
      await invokeBackend("open_last_run_folder");
    } catch (error) {
      showHelp("No log folder found", String(error));
    }
  });
}

function initialiseStartButton() {
  elements.startButton.addEventListener("click", async () => {
    elements.startButton.disabled = true;
    elements.startButton.textContent = "Starting...";

    if (elements.startHint) {
      elements.startHint.hidden = false;
      elements.startHint.textContent =
        "Game is loading and will open automatically in fullscreen in about 15 seconds.";
    }

    setStatus(
      "Starting Minecraft...",
      "Minecraft will open automatically in fullscreen."
    );

    try {
      const result = await invokeBackend("start_study");

      if (elements.openRunFolderButton) {
        elements.openRunFolderButton.hidden = false;
      }

      if (result.status === "completed") {
        setTodoComplete("game");
        setTodoComplete("logs");

        setStatus(
          "Study run finished.",
          "Local logs were collected. Upload is not configured in this milestone.",
          "success"
        );
      } else {
        setStatus(
          "Minecraft closed with an issue.",
          result.message || "A local log folder was still created where possible.",
          "error"
        );
      }
    } catch (error) {
      setStatus(
        "Start failed.",
        String(error || "Could not start the study."),
        "error"
      );

      showHelp(
        "Start failed",
        String(error || "Could not start the study. Check that the mod project and Java setup are available.")
      );
    } finally {
      elements.startButton.disabled = false;
      elements.startButton.textContent = "Start!";

      if (elements.startHint) {
        elements.startHint.hidden = true;
        elements.startHint.textContent =
          "Game is loading and will open automatically in fullscreen in about 15 seconds.";
      }
    }
  });
}

async function showRuntimeStatus() {
  // No visible runtime status is shown before the participant clicks Start.
}

window.addEventListener("DOMContentLoaded", async () => {
  studyConfig = await loadPrivateStudyConfig();

  applyStudyText(studyConfig);
  applyStudyAssets();

  initialiseDialogs();
  initialiseTodoPopover();
  initialiseStickyHeader();
  initialiseOpenRunFolderButton();
  await initialiseBackendEvents();
  initialiseStartButton();
  await showRuntimeStatus();
});