package io.github.u01234567.studycheckpoints;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Full-screen loading overlay shown while a chapter area is being prepared.
 * This screen must not pause the game. The world needs to keep progressing
 * behind the black cover so chunks can arrive on the client.
 */
public class StudyLoadingScreen extends Screen {
    private static final int BACKGROUND_COLOUR = 0xFF000000;
    private static final int TEXT_COLOUR = 0xFFFFFFFF;

    private final String message;

    public StudyLoadingScreen(String message) {
        super(Component.empty());
        this.message = message == null || message.isBlank() ? StudyConfig.getChapterLoadingMessage() : message;
    }

    @Override
    protected void init() {
        this.clearWidgets();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOUR);

        int textX = (this.width - this.font.width(message)) / 2;
        int textY = (this.height / 2) - 4;
        graphics.text(this.font, message, textX, textY, TEXT_COLOUR, false);
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
        return false;
    }
}