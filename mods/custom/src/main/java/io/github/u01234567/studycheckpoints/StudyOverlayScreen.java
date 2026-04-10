package io.github.u01234567.studycheckpoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    private final String heading;
    private final List<String> bodyLines;
    private final List<ButtonSpec> buttonSpecs;
    private final IndicatorSpec indicatorSpec;
    private final boolean showCloseButton;
    private StudyColourButton closeButton;

    private StudyOverlayScreen(String heading,
                               List<String> bodyLines,
                               List<ButtonSpec> buttonSpecs,
                               IndicatorSpec indicatorSpec,
                               boolean showCloseButton) {
        super(Component.empty());
        this.heading = heading;
        this.bodyLines = bodyLines;
        this.buttonSpecs = buttonSpecs;
        this.indicatorSpec = indicatorSpec;
        this.showCloseButton = showCloseButton;
    }

    public static StudyOverlayScreen createIntroScreen() {
        StudyExperimentCondition assignedCondition = StudyFlowController.getAssignedCondition();

        List<String> lines = StudyConfig.getIntroBodyLines();

        List<ButtonSpec> buttons = new ArrayList<>();
        buttons.add(new ButtonSpec(
                StudyConfig.getIntroAgreeButtonLabel(),
                0xFF2E8B57,
                0xFF3FAF6F,
                () -> StudyFlowController.acceptConsent(Minecraft.getInstance())
        ));
        buttons.add(new ButtonSpec(
                StudyConfig.getIntroStopButtonLabel(),
                0xFFB22222,
                0xFFD13A3A,
                () -> StudyFlowController.stopHere(Minecraft.getInstance())
        ));

        IndicatorSpec indicator = new IndicatorSpec(
                assignedCondition.indicatorLabel(),
                assignedCondition.indicatorColour()
        );

        return new StudyOverlayScreen(StudyConfig.getIntroHeading(), lines, buttons, indicator, false);
    }

    public static StudyOverlayScreen createChapterZeroTransitionScreen(StudyChapter completedChapter) {
        List<String> messageLines = StudyConfig.getChapterZeroTransitionBodyLines();

        List<ButtonSpec> buttons = List.of(new ButtonSpec(
                StudyConfig.getChapterZeroTransitionStartButtonLabel(),
                0xFF2F6FED,
                0xFF4C85F5,
                () -> StudyFlowController.continueFromChapterZeroTransition(Minecraft.getInstance())
        ));

        return new StudyOverlayScreen(StudyConfig.getChapterZeroTransitionHeading(), messageLines, buttons, null, false);
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
                StudyConfig.getFinalQuestionnaireButtonLabel(),
                0xFF2F6FED,
                0xFF4C85F5,
                () -> StudyFlowController.continueFromFinalScreen(Minecraft.getInstance(), completedChapter)
        ));

        return new StudyOverlayScreen(heading, messageLines, buttons, null, true);
    }

    @Override
    protected void init() {
        this.clearWidgets();

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

        for (int i = 0; i < buttonSpecs.size(); i++) {
            ButtonSpec spec = buttonSpecs.get(i);
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
        if (showCloseButton && closeButton != null) {
            closeButton.visible = StudyFlowController.canCloseQuestionnaireScreen();
            closeButton.active = closeButton.visible;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private record ButtonSpec(String label, int baseColour, int hoverColour, Runnable onPress) {
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