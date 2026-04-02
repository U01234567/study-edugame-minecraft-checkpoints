package io.github.u01234567.studycheckpoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Small participant-facing creature calling-card overlay.
 */
public class StudyCreatureInfoScreen extends Screen {
    private final StudyCreatureCards.CreatureCard creatureCard;

    public StudyCreatureInfoScreen(StudyCreatureCards.CreatureCard creatureCard) {
        super(Component.empty());
        this.creatureCard = creatureCard;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int panelWidth = Math.min(320, this.width - 40);
        int panelHeight = 160;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;

        this.addRenderableWidget(new StudyColourButton(
                panelLeft + panelWidth - 28,
                panelTop + 10,
                16,
                16,
                Component.literal("X"),
                0xFF999999,
                0xFFBBBBBB,
                () -> Minecraft.getInstance().setScreen(null)
        ));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int dimColour = 0x88000000;
        int panelColour = 0xF5FFFFFF;
        int borderColour = 0xFFDDDDDD;

        int panelWidth = Math.min(320, this.width - 40);
        int panelHeight = 160;
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = (this.height - panelHeight) / 2;
        int textLeft = panelLeft + 18;
        int maxTextWidth = panelWidth - 36;

        graphics.fill(0, 0, this.width, this.height, dimColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, panelColour);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, borderColour);
        graphics.fill(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, borderColour);
        graphics.fill(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, borderColour);

        graphics.text(this.font, creatureCard.displayName(), textLeft, panelTop + 18, 0xFF111111, false);

        int y = panelTop + 46;
        for (String fact : creatureCard.facts()) {
            List<String> wrappedFact = wrapLine("• " + fact, maxTextWidth);
            for (String line : wrappedFact) {
                graphics.text(this.font, line, textLeft, y, 0xFF222222, false);
                y += 14;
            }
            y += 2;
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