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

    // Prevent the player from opening the escape menu during the experiment.
    // When this is false, chat typing is also blocked.
    // This should remain false for real study sessions.
    private static final boolean ALLOW_ESCAPE_MENU = true;

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

    // Return whether the participant may open the escape menu.
    // When false, chat typing is also blocked on the client.
    public static boolean isEscapeMenuAllowed() {
        return ALLOW_ESCAPE_MENU;
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