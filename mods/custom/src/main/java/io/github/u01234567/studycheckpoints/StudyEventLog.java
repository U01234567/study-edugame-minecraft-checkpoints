package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper: writes the main study log.
 */
public final class StudyEventLog {
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();

    private static final Path PRIMARY_LOG_DIR =
            GAME_DIR.resolve("../../../analysis/logs").normalize().toAbsolutePath();
    private static final Path MIRROR_LOG_DIR =
            GAME_DIR.resolve("logs").resolve("study-checkpoints-sessions").normalize().toAbsolutePath();
    private static final Path[] ANALYSIS_DIR_CANDIDATES = new Path[] {
            GAME_DIR.resolve("../../../analysis").normalize().toAbsolutePath(),
            GAME_DIR.resolve("analysis").normalize().toAbsolutePath()
    };

    // World directories (repo original and copy)
    private static final Path REPO_WORLD_DIR =
            GAME_DIR.resolve("../../../world").normalize().toAbsolutePath();
    private static final Path RUNTIME_WORLD_DIR =
            GAME_DIR.resolve("saves").resolve(StudyCheckpoints.STUDY_WORLD_SAVE_NAME).normalize().toAbsolutePath();

    private static final AtomicBoolean HEADER_WRITTEN = new AtomicBoolean(false);
    private static final AtomicBoolean SESSION_SUMMARY_TRIGGERED = new AtomicBoolean(false);

    private static final AtomicReference<Path> PRIMARY_LOG_FILE = new AtomicReference<>();
    private static final AtomicReference<Path> MIRROR_LOG_FILE = new AtomicReference<>();
    private static final AtomicReference<String> SESSION_PLAYER_NAME = new AtomicReference<>("unknown");

    private static final String SESSION_ID = UUID.randomUUID().toString();

    private static final String SESSION_STARTED_AT_FILE_SAFE =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.now());

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("'['yyyy-MM-dd'] ['HH:mm:ss:SSS']'")
                    .withZone(ZoneId.systemDefault());

    private StudyEventLog() {
        // Utility class; do not instantiate.
    }

    // MCID
    public static String getSessionId() {
        return SESSION_ID;
    }

    public static void registerSessionParticipant(String playerName) {
        String resolvedPlayerName = sanitiseFileNameSegment(safe(playerName));
        SESSION_PLAYER_NAME.compareAndSet("unknown", resolvedPlayerName);
    }

    public static void logCheckpointSlideDisplayed(int completedChapterNumber,
                                                   int nextChapterNumber,
                                                   int slideIndex,
                                                   int slideCount,
                                                   String slideKey) {
        logSessionHeader();
        logEvent(
                "checkpoint_slide_displayed",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "slide_index=" + slideIndex,
                "slide_count=" + slideCount,
                "slide_key=" + safe(slideKey)
        );
    }

    public static void logCheckpointSlideAdvanced(int completedChapterNumber,
                                                  int nextChapterNumber,
                                                  int fromSlideIndex,
                                                  int toSlideIndex) {
        logSessionHeader();
        logEvent(
                "checkpoint_slide_advanced",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "from_slide_index=" + fromSlideIndex,
                "to_slide_index=" + toSlideIndex
        );
    }

    public static void logChapterZeroConditionSatisfied(String conditionKey,
                                                        String playerName,
                                                        double x,
                                                        double y,
                                                        double z) {
        logSessionHeader();
        logEvent(
                "chapter_zero_condition_satisfied",
                "condition=" + safe(conditionKey),
                "player=" + safe(playerName),
                "x=" + formatDouble(x),
                "y=" + formatDouble(y),
                "z=" + formatDouble(z)
        );
    }

    public static void logChapterZeroCompletionArmed(String playerName, long delayMs) {
        logSessionHeader();
        logEvent(
                "chapter_zero_completion_armed",
                "player=" + safe(playerName),
                "delay_ms=" + delayMs
        );
    }

    public static void logInventoryCleared(String playerName, int removedSlotCount, int removedItemCount) {
        logSessionHeader();
        logEvent(
                "inventory_cleared",
                "player=" + safe(playerName),
                "removed_slot_count=" + removedSlotCount,
                "removed_item_count=" + removedItemCount
        );
    }

    public static void logGroundItemsPurged(String playerName, int purgedItemEntityCount) {
        logSessionHeader();
        logEvent(
                "ground_items_purged",
                "player=" + safe(playerName),
                "purged_item_entity_count=" + purgedItemEntityCount
        );
    }

    // Generate metadata header. Includes:
    // - Start of session (incl. ID)
    // - Study mod version
    // - Minecraft version
    // - Fabric version
    // - Source world path
    // - When the source Minecraft world was last updated
    // - Runtime copied world path
    // - When the runtime Minecraft world was last updated
    public static void logSessionHeader() {
        if (!HEADER_WRITTEN.compareAndSet(false, true)) {
            return;
        }

        appendLine("");
        appendLine("================================================================");
        appendLine(now() + " | study_session_started | session_id=" + SESSION_ID);
        appendLine("study_mod_version=" + getModVersion(StudyCheckpoints.MOD_ID));
        appendLine("minecraft_version=" + getModVersion("minecraft"));
        appendLine("fabric_loader_version=" + getModVersion("fabricloader"));
        appendLine("fabric_api_version=" + getModVersion("fabric-api"));
        appendLine("session_player=" + SESSION_PLAYER_NAME.get());
        appendLine("analysis_log_file=" + ensurePrimaryLogFile());
        appendLine("runtime_log_mirror=" + ensureMirrorLogFile());
        appendLine("repo_world_source=" + REPO_WORLD_DIR);
        appendLine("repo_world_source_last_modified=" + getWorldLastModified(REPO_WORLD_DIR));
        appendLine("runtime_world_copy=" + RUNTIME_WORLD_DIR);
        appendLine("runtime_world_copy_last_modified=" + getWorldLastModified(RUNTIME_WORLD_DIR));
        appendLine("================================================================");
    }

    public static void logPlayerJoined(String playerName) {
        registerSessionParticipant(playerName);
        logSessionHeader();
        logEvent(
                "player_joined",
                "player=" + safe(playerName)
        );
    }

    public static void logConsentChoice(String choice) {
        logSessionHeader();
        logEvent(
                "consent_choice",
                "choice=" + safe(choice)
        );
    }

    public static void logInstructionScreenDisplayed(String screenKey,
                                                     String stepKey) {
        logSessionHeader();
        logEvent(
                "instruction_screen_displayed",
                "screen_key=" + safe(screenKey),
                "step_key=" + safe(stepKey)
        );
    }

    public static void logInstructionButtonPressed(String screenKey,
                                                   String stepKey,
                                                   String buttonKey,
                                                   String buttonLabel,
                                                   long timeOnScreenMs) {
        logSessionHeader();
        logEvent(
                "instruction_button_pressed",
                "screen_key=" + safe(screenKey),
                "step_key=" + safe(stepKey),
                "button_key=" + safe(buttonKey),
                "button_label=" + safe(buttonLabel),
                "time_on_screen_ms=" + timeOnScreenMs
        );
    }

    public static void logInstructionButtonClickedWhileDisabled(String screenKey,
                                                                String stepKey,
                                                                String buttonKey,
                                                                String buttonLabel,
                                                                long timeSinceDisplayedMs,
                                                                long remainingDelayMs) {
        logSessionHeader();
        logEvent(
                "instruction_button_clicked_while_disabled",
                "screen_key=" + safe(screenKey),
                "step_key=" + safe(stepKey),
                "button_key=" + safe(buttonKey),
                "button_label=" + safe(buttonLabel),
                "time_since_displayed_ms=" + timeSinceDisplayedMs,
                "remaining_delay_ms=" + remainingDelayMs
        );
    }

    public static void logExperimentConditionAssigned(String conditionId,
                                                      String source,
                                                      String indicatorColourName,
                                                      String countsFilePath) {
        logSessionHeader();
        logEvent(
                "experiment_condition_assigned",
                "condition=" + safe(conditionId),
                "source=" + safe(source),
                "indicator_colour=" + safe(indicatorColourName),
                "counts_file=" + safe(countsFilePath)
        );
    }

    public static void logChapterStarted(int chapterNumber, String coordinates, String facing, long durationMs) {
        logSessionHeader();
        logEvent(
                "chapter_started",
                "chapter=" + chapterNumber,
                "coords=" + safe(coordinates),
                "facing=" + safe(facing),
                "duration_ms=" + durationMs
        );
    }

    public static void logChapterLoadingStarted(int chapterNumber, String chapterTitle) {
        logSessionHeader();
        logEvent(
                "chapter_loading_started",
                "chapter=" + chapterNumber,
                "chapter_title=" + safe(chapterTitle)
        );
    }

    public static void logChapterLoadingFinished(int chapterNumber, String chapterTitle) {
        logSessionHeader();
        logEvent(
                "chapter_loading_finished",
                "chapter=" + chapterNumber,
                "chapter_title=" + safe(chapterTitle)
        );
    }

    public static void logChapterCompleted(int chapterNumber) {
        logSessionHeader();
        logEvent(
                "chapter_completed",
                "chapter=" + chapterNumber
        );
    }

    public static void logCheckpointDisplayed(int completedChapterNumber,
                                              int nextChapterNumber,
                                              String conditionId) {
        logSessionHeader();
        logEvent(
                "checkpoint_displayed",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "condition=" + safe(conditionId)
        );
    }

    public static void logCheckpointPromptDisplayed(int completedChapterNumber,
                                                    int nextChapterNumber,
                                                    String conditionId,
                                                    String promptText) {
        logSessionHeader();
        logEvent(
                "checkpoint_prompt_displayed",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "condition=" + safe(conditionId),
                "prompt=" + safe(promptText)
        );
    }

    public static void logCheckpointPromptDismissed(int completedChapterNumber,
                                                    int nextChapterNumber,
                                                    String conditionId,
                                                    String promptText,
                                                    long promptVisibleForMs) {
        logSessionHeader();
        logEvent(
                "checkpoint_prompt_dismissed",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "condition=" + safe(conditionId),
                "prompt=" + safe(promptText),
                "prompt_visible_for_ms=" + promptVisibleForMs
        );
    }

    public static void logCheckpointChoice(int completedChapterNumber,
                                           int nextChapterNumber,
                                           String conditionId,
                                           String choice) {
        logSessionHeader();
        logEvent(
                "checkpoint_choice_made",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "condition=" + safe(conditionId),
                "choice=" + safe(choice)
        );
    }

    public static void logCheckpointChoiceContext(int completedChapterNumber,
                                                  int nextChapterNumber,
                                                  String conditionId,
                                                  String choice,
                                                  boolean promptVisibleAtChoice,
                                                  boolean promptDismissedBeforeChoice,
                                                  long timeOnScreenMs) {
        logSessionHeader();
        logEvent(
                "checkpoint_choice_context",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "condition=" + safe(conditionId),
                "choice=" + safe(choice),
                "prompt_visible_at_choice=" + promptVisibleAtChoice,
                "prompt_dismissed_before_choice=" + promptDismissedBeforeChoice,
                "time_on_screen_ms=" + timeOnScreenMs
        );
    }

    public static void logCheckpointPauseStarted(int completedChapterNumber,
                                                 int nextChapterNumber,
                                                 long pauseDurationMs) {
        logSessionHeader();
        logEvent(
                "checkpoint_pause_started",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber,
                "pause_duration_ms=" + pauseDurationMs
        );
    }

    public static void logCheckpointPauseFinished(int completedChapterNumber, int nextChapterNumber) {
        logSessionHeader();
        logEvent(
                "checkpoint_pause_finished",
                "completed_chapter=" + completedChapterNumber,
                "next_chapter=" + nextChapterNumber
        );
    }

    public static void logQuestionnaireButtonPressed(String url) {
        logSessionHeader();
        logEvent(
                "questionnaire_button_pressed",
                "url=" + safe(url)
        );
    }

    public static void logCreatureCardOpened(String playerName,
                                             String entityType,
                                             String creatureLabel,
                                             String creatureName,
                                             String entityUuid,
                                             String entityBlockPos,
                                             boolean interactedBefore) {
        logSessionHeader();
        logEvent(
                "creature_card_opened",
                "player=" + safe(playerName),
                "entity_type=" + safe(entityType),
                "creature_label=" + safe(creatureLabel),
                "creature_name=" + safe(creatureName),
                "entity_uuid=" + safe(entityUuid),
                "entity_block_pos=" + safe(entityBlockPos),
                "interacted_before=" + interactedBefore
        );
    }

    public static void logCreatureCardClosed(String playerName,
                                             String creatureLabel,
                                             String chapterTitle,
                                             String activeChapterTitle,
                                             long readDurationMs) {
        logSessionHeader();
        logEvent(
                "creature_card_closed",
                "player=" + safe(playerName),
                "creature_label=" + safe(creatureLabel),
                "chapter_title=" + safe(chapterTitle),
                "active_chapter_title=" + safe(activeChapterTitle),
                "read_duration_ms=" + readDurationMs
        );
    }

    public static void logStudyCreatureSpawned(String entityType,
                                               String creatureLabel,
                                               String creatureName,
                                               String entityUuid,
                                               String chapterTitle,
                                               String entityBlockPos,
                                               String facing,
                                               String movementMode,
                                               boolean success) {
        logSessionHeader();
        logEvent(
                "study_creature_spawned",
                "entity_type=" + safe(entityType),
                "creature_label=" + safe(creatureLabel),
                "creature_name=" + safe(creatureName),
                "entity_uuid=" + safe(entityUuid),
                "chapter_title=" + safe(chapterTitle),
                "entity_block_pos=" + safe(entityBlockPos),
                "facing=" + safe(facing),
                "movement_mode=" + safe(movementMode),
                "success=" + success
        );
    }

    public static void logBlockedAction(String playerName, String action, String detail) {
        logSessionHeader();
        logEvent(
                "blocked_action",
                "player=" + safe(playerName),
                "action=" + safe(action),
                "detail=" + safe(detail)
        );
    }

    public static void logChapterZeroValidationFailed(String playerName,
                                                      String trigger,
                                                      boolean reachedDepth,
                                                      boolean interactedWithCreature,
                                                      boolean missingDepthRequirement,
                                                      boolean missingCreatureInteraction,
                                                      double x,
                                                      double y,
                                                      double z) {
        logSessionHeader();
        logEvent(
                "chapter_zero_validation_failed",
                "player=" + safe(playerName),
                "trigger=" + safe(trigger),
                "reached_depth=" + reachedDepth,
                "interacted_with_creature=" + interactedWithCreature,
                "missing_depth_requirement=" + missingDepthRequirement,
                "missing_creature_interaction=" + missingCreatureInteraction,
                "x=" + formatDouble(x),
                "y=" + formatDouble(y),
                "z=" + formatDouble(z)
        );
    }

    public static void logTestingTimeSkip(String playerName,
                                          String phase,
                                          long remainingBeforeMs,
                                          long remainingAfterMs) {
        logSessionHeader();
        logEvent(
                "testing_time_skip",
                "player=" + safe(playerName),
                "phase=" + safe(phase),
                "remaining_before_ms=" + remainingBeforeMs,
                "remaining_after_ms=" + remainingAfterMs
        );
    }

    public static void logMovementSample(String playerName,
                                         String chapterTitle,
                                         double x,
                                         double y,
                                         double z,
                                         double sampleDistance,
                                         double totalDistance,
                                         double totalSprintDistance,
                                         boolean sprinting,
                                         boolean onGround) {
        logSessionHeader();
        logEvent(
                "movement_sample",
                "player=" + safe(playerName),
                "chapter_title=" + safe(chapterTitle),
                "x=" + formatDouble(x),
                "y=" + formatDouble(y),
                "z=" + formatDouble(z),
                "sample_distance=" + formatDouble(sampleDistance),
                "total_distance=" + formatDouble(totalDistance),
                "total_sprint_distance=" + formatDouble(totalSprintDistance),
                "sprinting=" + sprinting,
                "on_ground=" + onGround
        );
    }

    public static void logJump(String playerName,
                               String chapterTitle,
                               double x,
                               double y,
                               double z) {
        logSessionHeader();
        logEvent(
                "jump_started",
                "player=" + safe(playerName),
                "chapter_title=" + safe(chapterTitle),
                "x=" + formatDouble(x),
                "y=" + formatDouble(y),
                "z=" + formatDouble(z)
        );
    }

    public static void logCowClick(String playerName, String clickType, String cowUuid, String cowBlockPos) {
        logSessionHeader();
        logEvent(
                "cow_clicked",
                "player=" + safe(playerName),
                "click_type=" + safe(clickType),
                "cow_uuid=" + safe(cowUuid),
                "cow_block_pos=" + safe(cowBlockPos)
        );
    }

    public static void logEmptyClick(String playerName,
                                     String clickType,
                                     String chapterTitle,
                                     double x,
                                     double y,
                                     double z,
                                     boolean sprinting,
                                     boolean onGround) {
        logSessionHeader();
        logEvent(
                "empty_click",
                "player=" + safe(playerName),
                "click_type=" + safe(clickType),
                "chapter_title=" + safe(chapterTitle),
                "x=" + formatDouble(x),
                "y=" + formatDouble(y),
                "z=" + formatDouble(z),
                "sprinting=" + sprinting,
                "on_ground=" + onGround
        );
    }

    public static void logGameEnded(String playerName, String reason) {
        logSessionHeader();
        logEvent(
                "game_ended",
                "player=" + safe(playerName),
                "reason=" + safe(reason)
        );
        triggerSessionSummaryBestEffort();
    }

    private static void triggerSessionSummaryBestEffort() {
        if (!SESSION_SUMMARY_TRIGGERED.compareAndSet(false, true)) {
            return;
        }

        try {
            Path analysisMainPy = locateAnalysisMainPy();
            if (analysisMainPy == null) {
                StudyCheckpoints.LOGGER.warn("Could not find analysis/main.py. Skipping automatic session summary.");
                return;
            }

            Path analysisDir = analysisMainPy.getParent();
            Path summaryOutput = analysisDir.resolve("output").resolve("last_session_summary.html")
                    .normalize()
                    .toAbsolutePath();

            StudyCheckpoints.LOGGER.info("Session summary HTML target: {}", summaryOutput);
            StudyCheckpoints.LOGGER.info("Session summary HTML link: {}", summaryOutput.toUri());

            Process process = startSummaryProcess(analysisDir);
            if (process == null) {
                StudyCheckpoints.LOGGER.warn("Could not start the automatic session summary process.");
                return;
            }

            process.onExit().thenAccept(completed ->
                    StudyCheckpoints.LOGGER.info(
                            "Automatic session summary process finished with exit code {}.",
                            completed.exitValue()
                    )
            );
        } catch (Exception e) {
            StudyCheckpoints.LOGGER.warn("Automatic session summary failed to start.", e);
        }
    }

    private static Path locateAnalysisMainPy() {
        for (Path analysisDir : ANALYSIS_DIR_CANDIDATES) {
            Path candidate = analysisDir.resolve("main.py");
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    private static Process startSummaryProcess(Path analysisDir) throws IOException {
        String[][] commands = isWindows()
                ? new String[][] {
                        {"py", "-3", "main.py", "sum_last"},
                        {"python", "main.py", "sum_last"},
                        {"python3", "main.py", "sum_last"}
                }
                : new String[][] {
                        {"python3", "main.py", "sum_last"},
                        {"python", "main.py", "sum_last"}
                };

        IOException lastException = null;

        for (String[] command : commands) {
            try {
                StudyCheckpoints.LOGGER.info("Starting automatic session summary command: {}", String.join(" ", command));
                return new ProcessBuilder(command)
                        .directory(analysisDir.toFile())
                        .inheritIO()
                        .start();
            } catch (IOException e) {
                lastException = e;
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        return null;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "")
                .toLowerCase(Locale.ROOT)
                .contains("win");
    }

    // Generate general log structure (both human-readable and easy to parse)
    // Structure example: [yyyy-mm-dd] [hh:mm:ss:ms] | <event_type> | session_id=... | key=value | key=value ...
    private static void logEvent(String eventType, String... fields) {
        StringBuilder line = new StringBuilder();
        line.append(now());
        line.append(" | ");
        line.append(eventType);
        line.append(" | session_id=");
        line.append(SESSION_ID);

        for (String field : fields) {
            line.append(" | ");
            line.append(field);
        }

        appendLine(line.toString());
    }

    private static synchronized void appendLine(String line) {
        try {
            appendLineToFile(ensurePrimaryLogFile(), line);
            appendLineToFile(ensureMirrorLogFile(), line);
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to write study log file for session {}.", SESSION_ID, e);
        }
    }

    private static void appendLineToFile(Path logFile, String line) throws IOException {
        Files.createDirectories(logFile.getParent());

        byte[] encodedLine = (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
        try (FileChannel channel = FileChannel.open(
                logFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        )) {
            channel.write(ByteBuffer.wrap(encodedLine));
            channel.force(true);
        }
    }

    private static Path ensurePrimaryLogFile() {
        Path existing = PRIMARY_LOG_FILE.get();
        if (existing != null) {
            return existing;
        }

        Path created = PRIMARY_LOG_DIR.resolve(sessionLogFileName());
        PRIMARY_LOG_FILE.compareAndSet(null, created);
        return PRIMARY_LOG_FILE.get();
    }

    private static Path ensureMirrorLogFile() {
        Path existing = MIRROR_LOG_FILE.get();
        if (existing != null) {
            return existing;
        }

        Path created = MIRROR_LOG_DIR.resolve(sessionLogFileName());
        MIRROR_LOG_FILE.compareAndSet(null, created);
        return MIRROR_LOG_FILE.get();
    }

    private static String sessionLogFileName() {
        return "study-"
                + SESSION_STARTED_AT_FILE_SAFE
                + "-"
                + SESSION_PLAYER_NAME.get()
                + "-"
                + SESSION_ID
                + ".log";
    }

    // Metadata: mod version?
    private static String getModVersion(String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    // Metadata: Minecraft world last updated?
    private static String getWorldLastModified(Path worldDir) {
        Path preferredPath = worldDir.resolve("level.dat");
        Path pathToInspect = Files.exists(preferredPath) ? preferredPath : worldDir;

        try {
            if (!Files.exists(pathToInspect)) {
                return "missing";
            }

            return Files.getLastModifiedTime(pathToInspect).toInstant().toString();
        } catch (IOException e) {
            return "unknown (" + e.getClass().getSimpleName() + ")";
        }
    }

    private static String now() {
        return TIMESTAMP_FORMAT.format(Instant.now());
    }

    private static String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value;
    }

    private static String sanitiseFileNameSegment(String value) {
        return safe(value).replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}