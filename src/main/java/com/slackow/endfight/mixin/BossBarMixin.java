package com.slackow.endfight.mixin;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBar.class)
public class BossBarMixin {
    @Shadow public static String name;

    @Inject(method = "update", at = @At("TAIL"))
    private static void update(BossBarProvider provider, boolean darkenSky, CallbackInfo ci) {
        name = provider.getName().asFormattedString() + ": " + (int) provider.getHealth() + "/" + (int) provider.getMaxHealth();
    }
}
