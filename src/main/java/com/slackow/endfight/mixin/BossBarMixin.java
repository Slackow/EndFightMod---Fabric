package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarProvider;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
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
        // get name, get health, get max health
        if (BigConfig.getSelectedConfig().specificHealthBar) {
            name = provider.getName().asFormattedString() + ": " + provider.getHealth() + "/" + provider.getMaxHealth();
        }
    }
}
