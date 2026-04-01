package io.github.u01234567.studycheckpoints;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Minimal custom button (green and red) for the overlay.
 * Vanilla buttons are left alone.
 */

public class StudyColourButton extends AbstractWidget {
    private final int baseColour;
    private final int hoverColour;
    private final Runnable onPress;

    public StudyColourButton(int x,
                             int y,
                             int width,
                             int height,
                             Component message,
                             int baseColour,
                             int hoverColour,
                             Runnable onPress) {
        super(x, y, width, height, message);
        this.baseColour = baseColour;
        this.hoverColour = hoverColour;
        this.onPress = onPress;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int colour = this.isHovered() ? hoverColour : baseColour;

        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, colour);
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, 0x55FFFFFF);
        graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, 0x55000000);

        int textColour = this.active ? 0xFFFFFFFF : 0xFFBBBBBB;
        graphics.centeredText(
                MinecraftAccess.font(),
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                textColour
        );
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (this.active && this.visible) {
            onPress.run();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    // Tiny bridge to get live font without needed Minecraft instance field
    private static final class MinecraftAccess {
        private MinecraftAccess() {
        }

        private static net.minecraft.client.gui.Font font() {
            return net.minecraft.client.Minecraft.getInstance().font;
        }
    }
}
