package io.github.u01234567.studycheckpoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition-specific checkpoint screen shown between manipulated chapters. Shows:
 * - correct wording for assigned condition
 * - correct button(s)
 * - delayed red prompt after inactivity
 */
public class StudyCheckpointScreen extends Screen {
    private static final int PROMPT_COLOUR = 0xFFD46A6A;
    private static final int PROMPT_BACKGROUND_COLOUR = 0xFFFBE3E3;
    private static final int PROMPT_BORDER_COLOUR = 0xFFE5A0A0;
    private static final int INTRO_SLIDE_COUNT = 2;
    private static final int NEXT_BUTTON_COLOUR = 0xFF2F6FED;
    private static final int NEXT_BUTTON_HOVER_COLOUR = 0xFF4C85F5;

    private final StudyChapter completedChapter;
    private final StudyChapter nextChapter;
    private final boolean initialTransitionCheckpoint;
    private final StudyExperimentCondition condition;
    private final List<String> bodyLines;
    private final long promptDelayMs;

    private long openedAtMs;
    private boolean promptVisible;
    private boolean promptLogged;
    private boolean promptDismissed;
    private boolean actionTaken;
    private int introSlideIndex;
    private StudyColourButton promptCloseButton;

    public StudyCheckpointScreen(StudyChapter completedChapter, StudyExperimentCondition condition) {
        this(completedChapter, completedChapter != null ? completedChapter.next() : null, condition, false);
    }

    public static StudyCheckpointScreen createInitialIntroCheckpoint(StudyExperimentCondition condition) {
        return new StudyCheckpointScreen(null, StudyChapter.first(), condition, true);
    }

    private StudyCheckpointScreen(StudyChapter completedChapter,
                                  StudyChapter nextChapter,
                                  StudyExperimentCondition condition,
                                  boolean initialTransitionCheckpoint) {
        super(Component.empty());
        this.completedChapter = completedChapter;
        this.nextChapter = nextChapter;
        this.condition = condition;
        this.initialTransitionCheckpoint = initialTransitionCheckpoint;
        this.bodyLines = createBodyLines(initialTransitionCheckpoint, completedChapter, condition, nextChapter);
        this.promptDelayMs = StudyConfig.getCheckpointPromptDelayMs();
    }

    // Return the remaining time before the delayed red prompt should appear.
    public long remainingPromptDelayMs() {
        if (this.openedAtMs == 0L) {
            return this.promptDelayMs;
        }

        long promptAtMs = this.openedAtMs + this.promptDelayMs;
        return Math.max(0L, promptAtMs - System.currentTimeMillis());
    }

    // Testing helper: shorten the remaining prompt delay without extending it.
    public void shortenRemainingPromptDelayTo(long remainingMs) {
        long currentRemainingMs = remainingPromptDelayMs();
        long clampedRemainingMs = Math.min(currentRemainingMs, Math.max(0L, remainingMs));
        this.openedAtMs = System.currentTimeMillis() - (this.promptDelayMs - clampedRemainingMs);
    }

    @Override
    protected void init() {
        this.clearWidgets();

        if (this.openedAtMs == 0L) {
            this.openedAtMs = System.currentTimeMillis();
        }

        logCurrentSlideDisplayed();

        List<ButtonSpec> buttons = buildButtonSpecs();

        int buttonWidth = buttons.size() == 1 ? 190 : 170;
        int buttonHeight = 24;
        int buttonSpacing = 12;
        int totalButtonsWidth = (buttonWidth * buttons.size()) + (buttonSpacing * Math.max(0, buttons.size() - 1));
        int buttonsLeft = (this.width - totalButtonsWidth) / 2;
        int buttonY = this.height / 2 + 62;

        for (int i = 0; i < buttons.size(); i++) {
            ButtonSpec spec = buttons.get(i);
            int x = buttonsLeft + (i * (buttonWidth + buttonSpacing));
            this.addRenderableWidget(new StudyColourButton(
                    x,
                    buttonY,
                    buttonWidth,
                    buttonHeight,
                    Component.literal(spec.label()),
                    spec.baseColour(),
                    spec.hoverColour(),
                    spec.onPress()
            ));
        }

        int panelWidth = Math.min(470, this.width - 40);
        int panelHeight = 212;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;
        int promptWidth = Math.min(260, panelWidth - 40);
        int promptHeight = 24;
        int promptLeft = panelLeft + (panelWidth - promptWidth) / 2;
        int promptTop = buttonY - 36;

        promptCloseButton = this.addRenderableWidget(new StudyColourButton(
                promptLeft + promptWidth - 20,
                promptTop + 4,
                12,
                12,
                Component.literal("X"),
                0xFFCC8B8B,
                0xFFD89A9A,
                () -> {
                    promptVisible = false;
                    promptDismissed = true;
                    if (promptCloseButton != null) {
                        promptCloseButton.visible = false;
                        promptCloseButton.active = false;
                    }
                }
        ));
        promptCloseButton.visible = false;
        promptCloseButton.active = false;
    }

    @Override
    public void tick() {
        if (!promptVisible && !promptDismissed && System.currentTimeMillis() - openedAtMs >= promptDelayMs) {
            promptVisible = true;

            if (!promptLogged && nextChapter != null) {
                promptLogged = true;
                StudyEventLog.logCheckpointPromptDisplayed(
                        completedChapterNumber(),
                        nextChapter.chapterNumber(),
                        condition.id(),
                        promptText()
                );
            }
        }

        if (promptCloseButton != null) {
            promptCloseButton.visible = promptVisible;
            promptCloseButton.active = promptVisible;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int dimColour = 0xAA000000;
        int panelColour = 0xF5FFFFFF;
        int borderColour = 0xFFDDDDDD;

        int panelWidth = Math.min(470, this.width - 40);
        int panelHeight = 212;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;
        int textLeft = panelLeft + 20;
        int textRight = panelLeft + panelWidth - 20;
        int maxTextWidth = textRight - textLeft;
        int buttonY = this.height / 2 + 62;

        graphics.fill(0, 0, this.width, this.height, dimColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, panelColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, borderColour);
        graphics.fill(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, borderColour);

        String heading = StudyConfig.getCheckpointHeading();
        int headingX = (this.width - this.font.width(heading)) / 2;
        graphics.text(this.font, heading, headingX, panelTop + 18, 0xFF111111, false);

        int y = panelTop + 48;
        for (String line : currentBodyLines()) {
            for (String wrappedLine : wrapLine(line, maxTextWidth)) {
                graphics.text(this.font, wrappedLine, textLeft, y, 0xFF222222, false);
                y += 14;
            }
        }

        if (promptVisible) {
            String promptText = promptText();
            int promptWidth = Math.min(260, panelWidth - 40);
            int promptHeight = 24;
            int promptLeft = panelLeft + (panelWidth - promptWidth) / 2;
            int promptTop = buttonY - 36;
            int promptTextX = promptLeft + 10;
            int promptTextY = promptTop + 8;

            graphics.fill(promptLeft, promptTop, promptLeft + promptWidth, promptTop + promptHeight, PROMPT_BACKGROUND_COLOUR);
            graphics.fill(promptLeft, promptTop, promptLeft + promptWidth, promptTop + 1, PROMPT_BORDER_COLOUR);
            graphics.fill(promptLeft, promptTop + promptHeight - 1, promptLeft + promptWidth, promptTop + promptHeight, PROMPT_BORDER_COLOUR);
            graphics.fill(promptLeft, promptTop, promptLeft + 1, promptTop + promptHeight, PROMPT_BORDER_COLOUR);
            graphics.fill(promptLeft + promptWidth - 1, promptTop, promptLeft + promptWidth, promptTop + promptHeight, PROMPT_BORDER_COLOUR);
            graphics.text(
                    this.font,
                    promptText,
                    promptTextX,
                    promptTextY,
                    PROMPT_COLOUR,
                    false
            );
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private List<ButtonSpec> buildButtonSpecs() {
        List<ButtonSpec> buttons = new ArrayList<>();

        if (isIntroTransitionCheckpoint()) {
            if (introSlideIndex == 0) {
                buttons.add(new ButtonSpec(
                        StudyConfig.getInitialTransitionNextButtonLabel(),
                        NEXT_BUTTON_COLOUR,
                        NEXT_BUTTON_HOVER_COLOUR,
                        () -> runOnce(this::advanceIntroSlide)
                ));
                return buttons;
            }

            buttons.add(new ButtonSpec(
                    StudyConfig.getInitialTransitionStartButtonLabel(),
                    condition.continueButtonColour(),
                    condition.continueButtonHoverColour(),
                    () -> runOnce(() -> StudyFlowController.continueFromInitialCheckpoint(
                            Minecraft.getInstance()
                    ))
            ));
            return buttons;
        }

        if (condition.showsPauseButton()) {
            buttons.add(new ButtonSpec(
                    condition.pauseButtonLabel(),
                    condition.pauseButtonColour(),
                    condition.pauseButtonHoverColour(),
                    () -> runOnce(() -> StudyFlowController.startPauseFromCheckpoint(
                            Minecraft.getInstance(),
                            completedChapter,
                            condition
                    ))
            ));
        }

        if (condition.showsContinueButton()) {
            buttons.add(new ButtonSpec(
                    condition.continueButtonLabel(),
                    condition.continueButtonColour(),
                    condition.continueButtonHoverColour(),
                    () -> runOnce(() -> StudyFlowController.continueFromCheckpoint(
                            Minecraft.getInstance(),
                            completedChapter,
                            condition
                    ))
            ));
        }

        return buttons;
    }

    private void runOnce(Runnable action) {
        if (actionTaken) {
            return;
        }

        actionTaken = true;
        action.run();
    }

    private boolean isIntroTransitionCheckpoint() {
        return initialTransitionCheckpoint;
    }

    private void advanceIntroSlide() {
        if (!isIntroTransitionCheckpoint() || introSlideIndex >= INTRO_SLIDE_COUNT - 1) {
            return;
        }

        int previousSlideIndex = introSlideIndex;
        introSlideIndex++;
        actionTaken = false;
        promptVisible = false;
        promptDismissed = false;
        promptLogged = false;
        openedAtMs = System.currentTimeMillis();

        if (nextChapter != null) {
            StudyEventLog.logCheckpointSlideAdvanced(
                    completedChapterNumber(),
                    nextChapter.chapterNumber(),
                    previousSlideIndex + 1,
                    introSlideIndex + 1
            );
        }

        if (this.minecraft != null) {
            this.init(this.width, this.height);
        }
    }

    private String promptText() {
        return isIntroTransitionCheckpoint()
                ? StudyConfig.getInitialTransitionPromptText()
                : condition.promptText();
    }

    private static List<String> createBodyLines(boolean initialTransitionCheckpoint,
                                                StudyChapter completedChapter,
                                                StudyExperimentCondition condition,
                                                StudyChapter nextChapter) {
        if (initialTransitionCheckpoint) {
            return StudyConfig.getInitialTransitionSlideOneLines();
        }

        return condition.checkpointBodyLines(completedChapter, nextChapter);
    }

    private void logCurrentSlideDisplayed() {
        if (!isIntroTransitionCheckpoint() || nextChapter == null) {
            return;
        }

        StudyEventLog.logCheckpointSlideDisplayed(
                completedChapterNumber(),
                nextChapter.chapterNumber(),
                introSlideIndex + 1,
                INTRO_SLIDE_COUNT,
                introSlideIndex == 0 ? "pre_chapter_zero_overview" : "pre_chapter_zero_start_button"
        );
    }

    private List<String> currentBodyLines() {
        if (!isIntroTransitionCheckpoint()) {
            return bodyLines;
        }

        return introSlideIndex == 0
                ? StudyConfig.getInitialTransitionSlideOneLines()
                : StudyConfig.getInitialTransitionSlideTwoLines();
    }

    private int completedChapterNumber() {
        return completedChapter != null ? completedChapter.chapterNumber() : -1;
    }

    private record ButtonSpec(String label, int baseColour, int hoverColour, Runnable onPress) {
    }

    private List<String> wrapLine(String line, int maxWidth) {
        List<String> wrappedLines = new ArrayList<>();

        if (line == null || line.isBlank()) {
            wrappedLines.add("");
            return wrappedLines;
        }

        String[] words = line.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;

            if (!currentLine.isEmpty() && this.font.width(candidate) > maxWidth) {
                wrappedLines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            } else {
                if (!currentLine.isEmpty()) {
                    currentLine.append(' ');
                }
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            wrappedLines.add(currentLine.toString());
        }

        return wrappedLines;
    }
}