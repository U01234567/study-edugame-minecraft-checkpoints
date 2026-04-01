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
 * - the chapter-complete continue screens
 */
public class StudyOverlayScreen extends Screen {
    private final String heading;
    private final List<String> bodyLines;
    private final List<ButtonSpec> buttonSpecs;

    private StudyOverlayScreen(String heading, List<String> bodyLines, List<ButtonSpec> buttonSpecs) {
        super(Component.empty());
        this.heading = heading;
        this.bodyLines = bodyLines;
        this.buttonSpecs = buttonSpecs;
    }

    public static StudyOverlayScreen createIntroScreen() {
        List<String> lines = List.of(
                "Please read the study information.",
                "If you want to continue, click the green button below.",
                "If you do not want to continue, click the red button and the game will close."
        );

        List<ButtonSpec> buttons = new ArrayList<>();
        buttons.add(new ButtonSpec(
                "Agree and continue",
                0xFF2E8B57,
                0xFF3FAF6F,
                () -> StudyFlowController.acceptConsent(Minecraft.getInstance())
        ));
        buttons.add(new ButtonSpec(
                "Stop here",
                0xFFB22222,
                0xFFD13A3A,
                () -> StudyFlowController.stopHere(Minecraft.getInstance())
        ));

        return new StudyOverlayScreen("Intro", lines, buttons);
    }

    public static StudyOverlayScreen createChapterCompleteScreen(StudyChapter completedChapter) {
        String heading = "Chapter " + completedChapter.chapterNumber() + " complete";
        boolean finalChapter = completedChapter.next() == null;
        String message = finalChapter
                ? "Chapter 3 complete. Click below to answer the questionnaire in your browser."
                : completedChapter.completionMessage();
        String buttonLabel = finalChapter ? "Answer questionnaire" : "Continue";

        List<ButtonSpec> buttons = List.of(new ButtonSpec(
                buttonLabel,
                0xFF2F6FED,
                0xFF4C85F5,
                () -> StudyFlowController.continueAfterChapter(Minecraft.getInstance(), completedChapter)
        ));

        return new StudyOverlayScreen(heading, List.of(message), buttons);
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int buttonWidth = buttonSpecs.size() == 1 ? 140 : 170;
        int buttonHeight = 24;
        int buttonSpacing = 12;
        int totalButtonsWidth = (buttonWidth * buttonSpecs.size()) + (buttonSpacing * Math.max(0, buttonSpecs.size() - 1));
        int buttonsLeft = (this.width - totalButtonsWidth) / 2;
        int buttonY = this.height / 2 + 55;

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

        int panelWidth = Math.min(420, this.width - 40);
        int panelHeight = 150;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;
        int textLeft = panelLeft + 20;

        graphics.fill(0, 0, this.width, this.height, dimColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, panelColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, borderColour);
        graphics.fill(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, borderColour);

        int headingX = (this.width - this.font.width(heading)) / 2;
        graphics.text(this.font, heading, headingX, panelTop + 18, 0xFF111111, false);

        int y = panelTop + 48;
        for (String line : bodyLines) {
            graphics.text(this.font, line, textLeft, y, 0xFF222222, false);
            y += 14;
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

    private record ButtonSpec(String label, int baseColour, int hoverColour, Runnable onPress) {
    }
}