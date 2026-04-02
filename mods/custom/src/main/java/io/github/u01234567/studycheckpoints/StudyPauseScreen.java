package io.github.u01234567.studycheckpoints;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Full-screen pause overlay used by the pause conditions. Design:
 * - Black background
 * - Centred white countdown
 */
public class StudyPauseScreen extends Screen {
    private static final int BACKGROUND_COLOUR = 0xFF000000;
    private static final int TEXT_COLOUR = 0xFFFFFFFF;

    private final long durationMs;
    private final Runnable onFinished;
    private long deadlineMs;
    private boolean finished;

    public StudyPauseScreen(long durationMs, Runnable onFinished) {
        super(Component.empty());
        this.durationMs = durationMs;
        this.onFinished = onFinished;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        if (this.deadlineMs == 0L) {
            this.deadlineMs = System.currentTimeMillis() + durationMs;
        }
    }

    @Override
    public void tick() {
        if (!finished && System.currentTimeMillis() >= deadlineMs) {
            finished = true;
            onFinished.run();
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOUR);

        long remainingMs = Math.max(0L, deadlineMs - System.currentTimeMillis());
        int totalSeconds = (int) Math.ceil(remainingMs / 1000.0D);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String countdownText = String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
        int countdownX = (this.width - this.font.width(countdownText)) / 2;
        int countdownY = this.height / 2 - 4;

        graphics.text(this.font, countdownText, countdownX, countdownY, TEXT_COLOUR, false);
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
}