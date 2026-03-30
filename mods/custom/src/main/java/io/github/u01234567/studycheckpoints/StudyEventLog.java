package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

// Helper: writes study log
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
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

    private StudyEventLog() {
        // Utility class; do not instantiate.
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
        appendLine("study_session_started | time=" + now() + " | session_id=" + SESSION_ID);
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
    // Structure example: <timestamp> | <event_type> | session_id=... | key=value | key=value ...
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

    // Avoid blank / null text
    private static String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value;
    }
}
