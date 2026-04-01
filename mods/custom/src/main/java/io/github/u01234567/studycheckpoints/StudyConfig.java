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
    private static Map<String, String> values;

    private StudyConfig() {
    }

    public static String getQualtricsUrlTemplate() {
        ensureLoaded();
        String value = values.get(QUALTRICS_URL_TEMPLATE_KEY);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + QUALTRICS_URL_TEMPLATE_KEY + " in " + ENV_FILE);
        }

        return value;
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