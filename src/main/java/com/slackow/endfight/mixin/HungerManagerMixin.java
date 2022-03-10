package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(PlayerEntity par1, CallbackInfo ci) {
        if (BigConfig.getSelectedConfig().dGodPlayer) {
            ci.cancel();
        }
    }
}
