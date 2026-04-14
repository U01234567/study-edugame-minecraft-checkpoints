package io.github.u01234567.studycheckpoints;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central study configuration.
 *
 * This file intentionally holds:
 * - technical config values (timings, testing flags, etc.)
 * - participant-facing flow/UI text
 *
 * The goal is that most study wording can be edited in one place
 * without touching the control-flow code.
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

    // Delay time for the delayed "click a button" reminder during checkpoints.
    private static final long CHECKPOINT_PROMPT_DELAY_MS = 20_000L;

    // Duration of the black pause overlay at checkpoint.
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
    private static final boolean TESTING_PHASE = false;

    // -------------------------------------------------------------------------
    // Participant-facing flow/UI text
    // -------------------------------------------------------------------------

    // Heading shown on the very first consent overlay.
    private static final String INTRO_HEADING = "Intro";

    // Body text shown on the very first consent overlay.
    private static final List<String> INTRO_BODY_LINES = List.of(
            "Please read the printed study information.",
            "If you want to continue, click the green button below.",
            "If you do not want to continue, click the red button and this study will end for you."
    );

    // Green consent button on the intro overlay.
    private static final String INTRO_AGREE_BUTTON_LABEL = "Agree and continue";

    // Red decline button on the intro overlay.
    private static final String INTRO_STOP_BUTTON_LABEL = "Stop here";

    // The short loading message shown while a chapter area is prepared.
    private static final String CHAPTER_LOADING_MESSAGE = "Chapter loading...";

    // Message shown after the player presses ESC and is teleported back to the
    // start of the active chapter.
    private static final String ESCAPE_RECOVERY_MESSAGE = "Esc pressed: back to chapter start.";

    // The generic heading used by manipulated checkpoint overlays.
    private static final String CHECKPOINT_HEADING = "Checkpoint";

    // Button label on the first slide of the pre-Chapter-0 intro transition.
    private static final String INITIAL_TRANSITION_NEXT_BUTTON_LABEL = "Next";

    // Button label on the second slide of the pre-Chapter-0 intro transition.
    private static final String INITIAL_TRANSITION_START_BUTTON_LABEL = "Start Chapter 0";

    // Delayed reminder shown on the pre-Chapter-0 intro transition.
    private static final String INITIAL_TRANSITION_PROMPT_TEXT = "Please press the button!";

    // First slide shown before Chapter 0 starts.
    private static final List<String> INITIAL_TRANSITION_SLIDE_ONE_LINES = List.of(
            "Welcome to our custom-built world full of fantasy creatures.",
            "You have one task: find as many different creatures as you can to learn more about them.",
            "At the end, your obtained knowledge of these creatures will be quizzed.",
            "Want to know how many creatures are left to find? Look at the circles below (gray = to be found, green = found)."
    );

    // Second slide shown before Chapter 0 starts.
    private static final List<String> INITIAL_TRANSITION_SLIDE_TWO_LINES = List.of(
            "You will start with an introduction (Chapter 0) to learn about the game (Minecraft) and its controls.",
            "After that, we will explain more."
    );

    // Heading shown for the standard Chapter 0 -> 1 transition overlay.
    private static final String CHAPTER_ZERO_TRANSITION_HEADING = "Get Started complete";

    // Body text shown for the standard Chapter 0 -> 1 transition overlay.
    private static final List<String> CHAPTER_ZERO_TRANSITION_BODY_LINES = List.of(
            "You completed the introduction.",
            "Next, the real game starts.",
            "The world consists of three chapters (1. The Museum, 2. The Farm, 3. The Jungle). Each chapter has its own creatures. You get 8 minutes per chapter to find as many as possible. No need to go off the islands!"
    );

    // Button label shown on the Chapter 0 -> 1 transition overlay.
    private static final String CHAPTER_ZERO_TRANSITION_START_BUTTON_LABEL = "Start the game";

    // Heading shown on the final questionnaire hand-off overlay.
    private static final String FINAL_QUESTIONNAIRE_HEADING = "You completed the game";

    // Button label shown on the final questionnaire hand-off overlay.
    private static final String FINAL_QUESTIONNAIRE_BUTTON_LABEL = "Answer follow-up questions";

    // Generic continue button used on manipulated checkpoints.
    private static final String CHECKPOINT_CONTINUE_BUTTON_LABEL = "Continue to the next chapter";

    // Pause button used on manipulated checkpoints.
    private static final String CHECKPOINT_PAUSE_BUTTON_LABEL = "Take a two-minute break";

    private static Map<String, String> values;

    private StudyConfig() {
    }

    // Return personalised Qualtrics URL from .env file (should contain {MCID} placeholder).
    public static String getQualtricsUrlTemplate() {
        ensureLoaded();
        String value = values.get(QUALTRICS_URL_TEMPLATE_KEY);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + QUALTRICS_URL_TEMPLATE_KEY + " in " + ENV_FILE);
        }

        return value;
    }

    // Return specified condition.
    public static String getForcedExperimentCondition() {
        return FORCED_EXPERIMENT_CONDITION;
    }

    // Return delay for the checkpoint prompt reminder.
    public static long getCheckpointPromptDelayMs() {
        return CHECKPOINT_PROMPT_DELAY_MS;
    }

    // Return delay of the black pause overlay.
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

    // Title of the very first consent overlay.
    public static String getIntroHeading() {
        return INTRO_HEADING;
    }

    // Body lines of the very first consent overlay.
    public static List<String> getIntroBodyLines() {
        return INTRO_BODY_LINES;
    }

    // Green consent button text.
    public static String getIntroAgreeButtonLabel() {
        return INTRO_AGREE_BUTTON_LABEL;
    }

    // Red decline button text.
    public static String getIntroStopButtonLabel() {
        return INTRO_STOP_BUTTON_LABEL;
    }

    // Colour-condition label shown on the intro overlay.
    public static String getConditionIndicatorLabel(StudyExperimentCondition condition) {
        return switch (condition) {
            case CONTINUE -> "cond_continue";
            case PAUSE -> "cond_pause";
            case CHOICE -> "cond_choice";
        };
    }

    // Chapter title shown to participants.
    public static String getChapterDisplayTitle(StudyChapter chapter) {
        return switch (chapter) {
            case CHAPTER_0 -> "Chapter 0 (Get Started)";
            case CHAPTER_1 -> "Chapter 1 (The Museum)";
            case CHAPTER_2 -> "Chapter 2 (The Farm)";
            case CHAPTER_3 -> "Chapter 3 (The Jungle)";
        };
    }

    // Welcome notification shown at the top of the screen when a chapter starts.
    public static String getChapterWelcomeMessage(StudyChapter chapter) {
        return "Welcome to " + chapter.displayTitle() + "!";
    }

    // Full-screen loading text shown while preparing a chapter.
    public static String getChapterLoadingMessage() {
        return CHAPTER_LOADING_MESSAGE;
    }

    // Top-of-screen notification shown after ESC teleports the participant back
    // to the current chapter start.
    public static String getEscapeRecoveryMessage() {
        return ESCAPE_RECOVERY_MESSAGE;
    }

    // Generic manipulated checkpoint heading.
    public static String getCheckpointHeading() {
        return CHECKPOINT_HEADING;
    }

    // First-slide button on the pre-Chapter-0 intro transition.
    public static String getInitialTransitionNextButtonLabel() {
        return INITIAL_TRANSITION_NEXT_BUTTON_LABEL;
    }

    // Second-slide button on the pre-Chapter-0 intro transition.
    public static String getInitialTransitionStartButtonLabel() {
        return INITIAL_TRANSITION_START_BUTTON_LABEL;
    }

    // Delayed prompt shown on the pre-Chapter-0 intro transition.
    public static String getInitialTransitionPromptText() {
        return INITIAL_TRANSITION_PROMPT_TEXT;
    }

    // First slide shown before Chapter 0.
    public static List<String> getInitialTransitionSlideOneLines() {
        return INITIAL_TRANSITION_SLIDE_ONE_LINES;
    }

    // Second slide shown before Chapter 0.
    public static List<String> getInitialTransitionSlideTwoLines() {
        return INITIAL_TRANSITION_SLIDE_TWO_LINES;
    }

    // Heading shown on the standard Chapter 0 -> 1 transition overlay.
    public static String getChapterZeroTransitionHeading() {
        return CHAPTER_ZERO_TRANSITION_HEADING;
    }

    // Body lines shown on the standard Chapter 0 -> 1 transition overlay.
    public static List<String> getChapterZeroTransitionBodyLines() {
        return CHAPTER_ZERO_TRANSITION_BODY_LINES;
    }

    // Button label shown on the standard Chapter 0 -> 1 transition overlay.
    public static String getChapterZeroTransitionStartButtonLabel() {
        return CHAPTER_ZERO_TRANSITION_START_BUTTON_LABEL;
    }

    // Generic continue button on manipulated checkpoints.
    public static String getCheckpointContinueButtonLabel() {
        return CHECKPOINT_CONTINUE_BUTTON_LABEL;
    }

    // Pause button on manipulated checkpoints.
    public static String getCheckpointPauseButtonLabel() {
        return CHECKPOINT_PAUSE_BUTTON_LABEL;
    }

    // Delayed checkpoint prompt text.
    public static String getCheckpointPromptText(StudyExperimentCondition condition) {
        return condition == StudyExperimentCondition.CHOICE
                ? "Please press a button!"
                : "Please press the button!";
    }

    // Main body text for manipulated checkpoints.
    public static List<String> getCheckpointBodyLines(StudyExperimentCondition condition,
                                                      StudyChapter completedChapter,
                                                      StudyChapter nextChapter) {
        return switch (condition) {
            case CONTINUE -> List.of(
                    "Checkpoint: you have finished " + completedChapter.displayTitle() + ".",
                    "Press continue to start " + nextChapter.displayTitle() + "."
            );
            case PAUSE -> List.of(
                    "Checkpoint: you have finished " + completedChapter.displayTitle() + ".",
                    "This is a good place to take a break.",
                    "Your progress is saved here, and you will continue from this point after the break."
            );
            case CHOICE -> List.of(
                    "Checkpoint: you have finished " + completedChapter.displayTitle() + ".",
                    "This is a good place to take a break.",
                    "Your progress is saved here, and you can continue later from this point."
            );
        };
    }

    // Heading shown on the final questionnaire overlay.
    public static String getFinalQuestionnaireHeading() {
        return FINAL_QUESTIONNAIRE_HEADING;
    }

    // Body text shown on the final questionnaire overlay.
    public static List<String> getFinalQuestionnaireBodyLines(StudyChapter completedChapter,
                                                              int interactedCreatureCount,
                                                              int totalCreatureCount) {
        return List.of(
                "With " + completedChapter.displayTitle() + " finished, you completed the game.",
                "You interacted with " + interactedCreatureCount + " of " + totalCreatureCount + " different creatures.",
                "For the second part of the study, please answer some follow-up questions."
        );
    }

    // Button shown on the final questionnaire overlay.
    public static String getFinalQuestionnaireButtonLabel() {
        return FINAL_QUESTIONNAIRE_BUTTON_LABEL;
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