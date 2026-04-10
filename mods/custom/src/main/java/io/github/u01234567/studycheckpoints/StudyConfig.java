package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to read the .env file
 */

public final class StudyConfig {
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    private static final Path ENV_FILE = GAME_DIR.resolve("../../../.env").normalize().toAbsolutePath();

    private static final String QUALTRICS_URL_TEMPLATE_KEY = "QUALTRICS_URL_TEMPLATE";

    // Choose study condition from:
    // - cond_continue (forest green)
    // - cond_pause (amber)
    // - cond_choice (lilac)
    // - none (= random assignment of the above)
    private static final String FORCED_EXPERIMENT_CONDITION = "cond_choice";

    // Delay time for 'click a button' reminder during checkpoints
    private static final long CHECKPOINT_PROMPT_DELAY_MS = 20_000L;

    // Duration of the black pause overlay at checkpoint
    private static final long CHECKPOINT_PAUSE_DURATION_MS = 120_000L;

    // Testing helper: shorten the current timed study phase to this remaining time.
    private static final long TESTING_SKIP_REMAINING_MS = 20_000L;

    // Keep the chapter loading screen visible for at least this long, even if
    // the server finishes quickly. This avoids a one-frame flash.
    private static final long CHAPTER_LOADING_MIN_SCREEN_MS = 1_000L;

    // Number of chunks to keep warm around the target chapter start chunk.
    // Radius 2 = 5 x 5 chunks centred on the chapter start.
    private static final int CHAPTER_PREWARM_CHUNK_RADIUS = 2;

    // Enable local testing mode instead of the normal participant restrictions.
    // When true:
    // - the player is switched to creative mode once when joining the world
    // - building, breaking, inventory access, item dropping, pick-block, and offhand swap stay enabled
    // - chat typing remains available
    // - the alternate menu key may open the vanilla pause menu
    // - testing-only helpers such as /timetravel stay enabled
    // - shortened testing hotbar layouts stay allowed
    // The alternate menu key still uses the player-list binding, which is Tab by default.
    // Escape itself is still reserved for returning the participant to the current chapter start.
    private static final boolean TESTING_PHASE = true;

    private static Map<String, String> values;

    private StudyConfig() {
    }

    // Return personalised Qualtrics URL from .env file (should contain {MCID} placeholder)
    public static String getQualtricsUrlTemplate() {
        ensureLoaded();
        String value = values.get(QUALTRICS_URL_TEMPLATE_KEY);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + QUALTRICS_URL_TEMPLATE_KEY + " in " + ENV_FILE);
        }

        return value;
    }

    // Return specified condition
    public static String getForcedExperimentCondition() {
        return FORCED_EXPERIMENT_CONDITION;
    }

    // Return delay for 'click a button' reminder during checkpoints
    public static long getCheckpointPromptDelayMs() {
        return CHECKPOINT_PROMPT_DELAY_MS;
    }

    // Return delay of the black pause overlay
    public static long getCheckpointPauseDurationMs() {
        return CHECKPOINT_PAUSE_DURATION_MS;
    }

    // Return the remaining time used by the testing skip command.
    public static long getTestingSkipRemainingMs() {
        return TESTING_SKIP_REMAINING_MS;
    }

    // Return the minimum time that the loading screen should stay visible.
    public static long getChapterLoadingMinScreenMs() {
        return CHAPTER_LOADING_MIN_SCREEN_MS;
    }

    // Return the chunk radius used to prewarm a chapter area.
    public static int getChapterPrewarmChunkRadius() {
        return CHAPTER_PREWARM_CHUNK_RADIUS;
    }

    // Return whether testing-only helpers and relaxed restrictions are enabled.
    public static boolean isTestingPhase() {
        return TESTING_PHASE;
    }

    private static synchronized void ensureLoaded() {
        if (values != null) {
            return;
        }

        values = new HashMap<>();

        if (!Files.exists(ENV_FILE)) {
            StudyCheckpoints.LOGGER.warn("No study .env file found at {}", ENV_FILE);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(ENV_FILE);
            for (String rawLine : lines) {
                String line = rawLine.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int splitIndex = line.indexOf('=');
                if (splitIndex < 0) {
                    continue;
                }

                String key = line.substring(0, splitIndex).trim();
                String value = line.substring(splitIndex + 1).trim();
                values.put(key, value);
            }
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to read study .env file: {}", ENV_FILE, e);
        }
    }
}