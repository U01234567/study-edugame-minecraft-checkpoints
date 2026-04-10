package io.github.u01234567.studycheckpoints.mixin;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    private void studycheckpoints$disableToasts(Toast toast, CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }

    @Inject(method = "showNowPlayingToast", at = @At("HEAD"), cancellable = true)
    private void studycheckpoints$disableNowPlayingToasts(CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }
}