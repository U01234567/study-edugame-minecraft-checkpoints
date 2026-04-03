package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper: writes the main study log.
 */
public final class StudyEventLog {
    // Game directory (at custom/run)
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();

    // Log file (at custom/run/logs/study-checkpoints.log)
    private static final Path LOG_FILE = GAME_DIR.resolve("logs").resolve("study-checkpoints.log");

    // World directories (repo original and copy)
    private static final Path REPO_WORLD_DIR =
            GAME_DIR.resolve("../../../world").normalize().toAbsolutePath();
    private static final Path RUNTIME_WORLD_DIR =
            GAME_DIR.resolve("saves").resolve(StudyCheckpoints.STUDY_WORLD_SAVE_NAME).normalize().toAbsolutePath();

    // ONE metadata header
    private static final AtomicBoolean HEADER_WRITTEN = new AtomicBoolean(false);

    // Per-session ID (to group events in data analysis)
    private static final String SESSION_ID = UUID.randomUUID().toString();

    // Timestamp
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
        appendLine("repo_world_source=" + REPO_WORLD_DIR);
        appendLine("repo_world_source_last_modified=" + getWorldLastModified(REPO_WORLD_DIR));
        appendLine("runtime_world_copy=" + RUNTIME_WORLD_DIR);
        appendLine("runtime_world_copy_last_modified=" + getWorldLastModified(RUNTIME_WORLD_DIR));
        appendLine("================================================================");
    }

    public static void logPlayerJoined(String playerName) {
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

    public static void logGameEnded(String playerName, String reason) {
        logSessionHeader();
        logEvent(
                "game_ended",
                "player=" + safe(playerName),
                "reason=" + safe(reason)
        );
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

    // Log ONE event at a time
    private static synchronized void appendLine(String line) {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.writeString(
                    LOG_FILE,
                    line + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to write study log file: {}", LOG_FILE, e);
        }
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

    // Avoid blank / null text
    private static String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value;
    }
}