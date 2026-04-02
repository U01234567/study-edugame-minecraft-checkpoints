package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Assigns one experimental condition per session and keeps a simple persistent
 * count file across sessions.
 */
public final class StudyConditionAllocator {
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    private static final Path COUNTS_FILE = GAME_DIR.resolve("study-condition-counts.txt");

    private StudyConditionAllocator() {
    }

    public static synchronized ConditionAssignment assignConditionForSession(String forcedConditionValue) {
        StudyExperimentCondition forcedCondition = StudyExperimentCondition.fromConfigValue(forcedConditionValue);
        StudyExperimentCondition selectedCondition = forcedCondition != null
                ? forcedCondition
                : StudyExperimentCondition.randomCondition();
        String assignmentSource = forcedCondition != null ? "forced" : "random";

        EnumMap<StudyExperimentCondition, Integer> counts = readCounts();
        counts.put(selectedCondition, counts.get(selectedCondition) + 1);
        writeCounts(counts);

        return new ConditionAssignment(selectedCondition, assignmentSource, COUNTS_FILE);
    }

    private static EnumMap<StudyExperimentCondition, Integer> readCounts() {
        EnumMap<StudyExperimentCondition, Integer> counts = createEmptyCounts();

        if (!Files.exists(COUNTS_FILE)) {
            return counts;
        }

        try {
            List<String> lines = Files.readAllLines(COUNTS_FILE);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    continue;
                }

                StudyExperimentCondition condition = StudyExperimentCondition.fromConfigValue(parts[0]);
                if (condition == null) {
                    continue;
                }

                try {
                    int value = Integer.parseInt(parts[1]);
                    counts.put(condition, Math.max(0, value));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed lines and keep the default zero value.
                }
            }
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to read condition count file: {}", COUNTS_FILE, e);
        }

        return counts;
    }

    private static void writeCounts(Map<StudyExperimentCondition, Integer> counts) {
        StringBuilder content = new StringBuilder();

        for (StudyExperimentCondition condition : StudyExperimentCondition.values()) {
            content.append(condition.id())
                    .append(' ')
                    .append(counts.getOrDefault(condition, 0))
                    .append(System.lineSeparator());
        }

        try {
            Files.createDirectories(COUNTS_FILE.getParent());
            Files.writeString(COUNTS_FILE, content.toString());
        } catch (IOException e) {
            StudyCheckpoints.LOGGER.error("Failed to write condition count file: {}", COUNTS_FILE, e);
        }
    }

    private static EnumMap<StudyExperimentCondition, Integer> createEmptyCounts() {
        EnumMap<StudyExperimentCondition, Integer> counts = new EnumMap<>(StudyExperimentCondition.class);

        for (StudyExperimentCondition condition : StudyExperimentCondition.values()) {
            counts.put(condition, 0);
        }

        return counts;
    }

    // Small value object describing the session assignment decision.
    public record ConditionAssignment(
            StudyExperimentCondition condition,
            String source,
            Path countsFile
    ) {
    }
}