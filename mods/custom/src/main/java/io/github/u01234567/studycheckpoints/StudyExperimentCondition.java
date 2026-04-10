package io.github.u01234567.studycheckpoints;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Experimental checkpoint condition assigned to a participant for the session.
 */
public enum StudyExperimentCondition {
    CONTINUE("cond_continue", 0xFF228B22, "forest green"),
    PAUSE("cond_pause", 0xFFFFBF00, "amber"),
    CHOICE("cond_choice", 0xFFC8A2C8, "lilac");

    private static final int CONTINUE_BUTTON_COLOUR = 0xFF2E8B57;
    private static final int CONTINUE_BUTTON_HOVER_COLOUR = 0xFF3FAF6F;
    private static final int PAUSE_BUTTON_COLOUR = 0xFF2E8B57;
    private static final int PAUSE_BUTTON_HOVER_COLOUR = 0xFF3FAF6F;

    private final String id;
    private final int indicatorColour;
    private final String indicatorColourName;

    StudyExperimentCondition(String id, int indicatorColour, String indicatorColourName) {
        this.id = id;
        this.indicatorColour = indicatorColour;
        this.indicatorColourName = indicatorColourName;
    }

    public String id() {
        return id;
    }

    public int indicatorColour() {
        return indicatorColour;
    }

    public String indicatorColourName() {
        return indicatorColourName;
    }

    public String indicatorLabel() {
        return StudyConfig.getConditionIndicatorLabel(this);
    }

    public List<String> checkpointBodyLines(StudyChapter completedChapter, StudyChapter nextChapter) {
        return StudyConfig.getCheckpointBodyLines(this, completedChapter, nextChapter);
    }

    public boolean showsContinueButton() {
        return this == CONTINUE || this == CHOICE;
    }

    public boolean showsPauseButton() {
        return this == PAUSE || this == CHOICE;
    }

    // Buttons
    public String continueButtonLabel() { return StudyConfig.getCheckpointContinueButtonLabel(); }
    public String pauseButtonLabel() {
        return StudyConfig.getCheckpointPauseButtonLabel();
    }

    public int continueButtonColour() {
        return CONTINUE_BUTTON_COLOUR;
    }

    public int continueButtonHoverColour() {
        return CONTINUE_BUTTON_HOVER_COLOUR;
    }

    public int pauseButtonColour() {
        return PAUSE_BUTTON_COLOUR;
    }

    public int pauseButtonHoverColour() {
        return PAUSE_BUTTON_HOVER_COLOUR;
    }

    public String promptText() {
        return StudyConfig.getCheckpointPromptText(this);
    }

    // Parse a configured value into a known condition.
    public static StudyExperimentCondition fromConfigValue(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String normalised = rawValue.trim().toLowerCase(Locale.ROOT);
        if (normalised.isEmpty() || normalised.equals("none") || normalised.equals("random")) {
            return null;
        }

        return switch (normalised) {
            case "cond_continue", "continue" -> CONTINUE;
            case "cond_pause", "pause" -> PAUSE;
            case "cond_choice", "choice" -> CHOICE;
            default -> null;
        };
    }

    public static StudyExperimentCondition randomCondition() {
        StudyExperimentCondition[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}