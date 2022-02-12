package com.slackow.endfight.mixin;

import net.minecraft.client.gui.screen.CreditsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsScreen.class)
public abstract class CreditsScreenMixin {

    @Shadow protected abstract void close();

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci){
        close();
        ci.cancel();
    }

}
