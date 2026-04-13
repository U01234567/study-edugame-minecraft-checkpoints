package io.github.u01234567.studycheckpoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Design for reusable white overlay. Used for example for:
 * - the consent intro screen
 * - the final questionnaire hand-off screen
 */
public class StudyOverlayScreen extends Screen {
    private static final long INTRO_BUTTON_ENABLE_DELAY_MS = 2_000L;

    private final String heading;
    private final List<String> bodyLines;
    private final List<ButtonSpec> buttonSpecs;
    private final IndicatorSpec indicatorSpec;
    private final boolean showCloseButton;
    private final String screenKey;
    private final String stepKey;
    private final long buttonEnableDelayMs;
    private StudyColourButton closeButton;
    private final List<TrackedButton> delayedButtons = new ArrayList<>();
    private long openedAtMs;
    private boolean instructionDisplayLogged;

    private StudyOverlayScreen(String heading,
                               List<String> bodyLines,
                               List<ButtonSpec> buttonSpecs,
                               IndicatorSpec indicatorSpec,
                               boolean showCloseButton,
                               String screenKey,
                               String stepKey,
                               long buttonEnableDelayMs) {
        super(Component.empty());
        this.heading = heading;
        this.bodyLines = bodyLines;
        this.buttonSpecs = buttonSpecs;
        this.indicatorSpec = indicatorSpec;
        this.showCloseButton = showCloseButton;
        this.screenKey = screenKey;
        this.stepKey = stepKey;
        this.buttonEnableDelayMs = Math.max(0L, buttonEnableDelayMs);
    }

    public static StudyOverlayScreen createIntroScreen() {
        StudyExperimentCondition assignedCondition = StudyFlowController.getAssignedCondition();

        List<String> lines = StudyConfig.getIntroBodyLines();

        List<ButtonSpec> buttons = new ArrayList<>();
        buttons.add(new ButtonSpec(
                "agree_and_continue",
                StudyConfig.getIntroAgreeButtonLabel(),
                0xFF2E8B57,
                0xFF3FAF6F,
                () -> StudyFlowController.acceptConsent(Minecraft.getInstance())
        ));
        buttons.add(new ButtonSpec(
                "stop_here",
                StudyConfig.getIntroStopButtonLabel(),
                0xFFB22222,
                0xFFD13A3A,
                () -> StudyFlowController.stopHere(Minecraft.getInstance())
        ));

        IndicatorSpec indicator = new IndicatorSpec(
                assignedCondition.indicatorLabel(),
                assignedCondition.indicatorColour()
        );

        return new StudyOverlayScreen(StudyConfig.getIntroHeading(), lines, buttons, indicator, false, "intro_overlay", "consent", INTRO_BUTTON_ENABLE_DELAY_MS);
    }

    public static StudyOverlayScreen createChapterZeroTransitionScreen(StudyChapter completedChapter) {
        List<String> messageLines = StudyConfig.getChapterZeroTransitionBodyLines();

        List<ButtonSpec> buttons = List.of(new ButtonSpec(
                "start_game",
                StudyConfig.getChapterZeroTransitionStartButtonLabel(),
                0xFF2F6FED,
                0xFF4C85F5,
                () -> StudyFlowController.continueFromChapterZeroTransition(Minecraft.getInstance())
        ));

        return new StudyOverlayScreen(
                StudyConfig.getChapterZeroTransitionHeading(),
                messageLines,
                buttons,
                null,
                false,
                "intro_overlay",
                "chapter_zero_transition",
                INTRO_BUTTON_ENABLE_DELAY_MS
        );
    }

    public static StudyOverlayScreen createFinalQuestionnaireScreen(StudyChapter completedChapter) {
        int interactedCreatureCount = StudyInteractionController.interactedCreatureTypeCountExcludingChapterZero();
        int totalCreatureCount = StudyCreatureCards.totalTrackedCreatureTypeCountExcludingChapterZero();
        String heading = StudyConfig.getFinalQuestionnaireHeading();
        List<String> messageLines = StudyConfig.getFinalQuestionnaireBodyLines(
                completedChapter,
                interactedCreatureCount,
                totalCreatureCount
        );

        List<ButtonSpec> buttons = List.of(new ButtonSpec(
                "open_questionnaire",
                StudyConfig.getFinalQuestionnaireButtonLabel(),
                0xFF2F6FED,
                0xFF4C85F5,
                () -> StudyFlowController.continueFromFinalScreen(Minecraft.getInstance(), completedChapter)
        ));

        return new StudyOverlayScreen(heading, messageLines, buttons, null, true, "questionnaire_overlay", null, 0L);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.delayedButtons.clear();

        if (this.openedAtMs == 0L) {
            this.openedAtMs = System.currentTimeMillis();
        }

        logInstructionScreenDisplayedOnce();

        if (showCloseButton) {
            int panelWidth = Math.min(470, this.width - 40);
            int panelHeight = 212;
            int panelLeft = (this.width - panelWidth) / 2;
            int panelTop = (this.height - panelHeight) / 2;

            closeButton = this.addRenderableWidget(new StudyColourButton(
                    panelLeft + panelWidth - 30,
                    panelTop + 10,
                    16,
                    16,
                    Component.literal("X"),
                    0xFF999999,
                    0xFFBBBBBB,
                    () -> StudyFlowController.closeAfterQuestionnaire(Minecraft.getInstance())
            ));
            closeButton.visible = StudyFlowController.canCloseQuestionnaireScreen();
            closeButton.active = closeButton.visible;
        }

        int buttonWidth = buttonSpecs.size() == 1 ? 160 : 170;
        int buttonHeight = 24;
        int buttonSpacing = 12;
        int totalButtonsWidth = (buttonWidth * buttonSpecs.size()) + (buttonSpacing * Math.max(0, buttonSpecs.size() - 1));
        int buttonsLeft = (this.width - totalButtonsWidth) / 2;
        int buttonY = this.height / 2 + 62;

        boolean buttonsEnabled = shouldEnableButtons();

        for (int i = 0; i < buttonSpecs.size(); i++) {
            ButtonSpec spec = buttonSpecs.get(i);
            int x = buttonsLeft + (i * (buttonWidth + buttonSpacing));
            StudyColourButton button = this.addRenderableWidget(new StudyColourButton(
                    x,
                    buttonY,
                    buttonWidth,
                    buttonHeight,
                    Component.literal(spec.label()),
                    spec.baseColour(),
                    spec.hoverColour(),
                    () -> {
                        logInstructionButtonPressed(spec);
                        spec.onPress().run();
                    }
            ));
            button.active = buttonsEnabled;
            if (buttonEnableDelayMs > 0L) {
                delayedButtons.add(new TrackedButton(button, spec, x, buttonY, buttonWidth, buttonHeight));
            }
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

        graphics.fill(0, 0, this.width, this.height, dimColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, panelColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, borderColour);
        graphics.fill(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, borderColour);

        int headingX = (this.width - this.font.width(heading)) / 2;
        graphics.text(this.font, heading, headingX, panelTop + 18, 0xFF111111, false);

        if (indicatorSpec != null) {
            int swatchSize = 7;
            int swatchRight = panelLeft + panelWidth - 20;
            int swatchLeft = swatchRight - swatchSize;
            int swatchTop = panelTop + 18;

            graphics.fill(swatchLeft, swatchTop, swatchRight, swatchTop + swatchSize, indicatorSpec.colour());
        }

        int y = panelTop + 48;
        for (String line : bodyLines) {
            for (String wrappedLine : wrapLine(line, maxTextWidth)) {
                graphics.text(this.font, wrappedLine, textLeft, y, 0xFF222222, false);
                y += 14;
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        if (buttonEnableDelayMs > 0L) {
            boolean buttonsEnabled = shouldEnableButtons();
            for (TrackedButton trackedButton : delayedButtons) {
                trackedButton.button().active = buttonsEnabled;
            }
        }

        if (showCloseButton && closeButton != null) {
            closeButton.visible = StudyFlowController.canCloseQuestionnaireScreen();
            closeButton.active = closeButton.visible;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (buttonEnableDelayMs > 0L && !shouldEnableButtons()) {
            double mouseX = event.x();
            double mouseY = event.y();

            for (TrackedButton trackedButton : delayedButtons) {
                if (trackedButton.contains(mouseX, mouseY)) {
                    logInstructionButtonClickedWhileDisabled(trackedButton.spec());
                    return true;
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private boolean shouldEnableButtons() {
        return buttonEnableDelayMs <= 0L
                || openedAtMs == 0L
                || System.currentTimeMillis() >= openedAtMs + buttonEnableDelayMs;
    }

    private void logInstructionScreenDisplayedOnce() {
        if (instructionDisplayLogged || stepKey == null || stepKey.isBlank()) {
            return;
        }

        instructionDisplayLogged = true;
        StudyEventLog.logInstructionScreenDisplayed(
                screenKey,
                stepKey
        );
    }

    private void logInstructionButtonPressed(ButtonSpec spec) {
        if (stepKey == null || stepKey.isBlank()) {
            return;
        }

        StudyEventLog.logInstructionButtonPressed(
                screenKey,
                stepKey,
                spec.analyticsKey(),
                spec.label(),
                elapsedSinceOpenedMs()
        );
    }

    private void logInstructionButtonClickedWhileDisabled(ButtonSpec spec) {
        if (stepKey == null || stepKey.isBlank()) {
            return;
        }

        StudyEventLog.logInstructionButtonClickedWhileDisabled(
                screenKey,
                stepKey,
                spec.analyticsKey(),
                spec.label(),
                elapsedSinceOpenedMs(),
                remainingButtonEnableDelayMs()
        );
    }

    private long elapsedSinceOpenedMs() {
        return openedAtMs == 0L ? 0L : Math.max(0L, System.currentTimeMillis() - openedAtMs);
    }

    private long remainingButtonEnableDelayMs() {
        return Math.max(0L, (openedAtMs + buttonEnableDelayMs) - System.currentTimeMillis());
    }

    private record ButtonSpec(String analyticsKey, String label, int baseColour, int hoverColour, Runnable onPress) {
    }

    private record TrackedButton(StudyColourButton button,
                                 ButtonSpec spec,
                                 int x,
                                 int y,
                                 int width,
                                 int height) {
        private boolean contains(double mouseX, double mouseY) {
            return mouseX >= x
                    && mouseX < x + width
                    && mouseY >= y
                    && mouseY < y + height;
        }
    }

    private record IndicatorSpec(String label, int colour) {
    }

    // Margins so text cannot disappear off-screen
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