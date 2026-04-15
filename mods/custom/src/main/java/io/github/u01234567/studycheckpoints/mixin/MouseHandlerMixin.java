package io.github.u01234567.studycheckpoints.mixin;

import io.github.u01234567.studycheckpoints.StudyInteractionController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "onButton", at = @At("HEAD"))
    private void studycheckpoints$logEmptyClick(long windowPointer,
                                                MouseButtonInfo buttonInfo,
                                                int action,
                                                CallbackInfo callbackInfo) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }

        StudyInteractionController.logEmptyClick(minecraft, buttonInfo.button());
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void studycheckpoints$blockMouseWheel(long windowPointer,
                                                  double horizontalScrollAmount,
                                                  double verticalScrollAmount,
                                                  CallbackInfo callbackInfo) {
        if (!StudyInteractionController.shouldBlockMouseWheel(minecraft)) {
            return;
        }

        StudyInteractionController.logMouseWheelBlocked(minecraft, verticalScrollAmount);
        callbackInfo.cancel();
    }
}