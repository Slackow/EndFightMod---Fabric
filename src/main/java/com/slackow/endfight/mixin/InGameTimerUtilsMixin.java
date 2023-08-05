package com.slackow.endfight.mixin;

import com.google.gson.JsonObject;
import com.redlimerl.speedrunigt.timer.InGameTimerUtils;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(value = InGameTimerUtils.class, remap = false)
public class InGameTimerUtilsMixin {
    @Redirect(method = "convertTimelineJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V", ordinal = 0))
    private static void writeEndfightProperties(JsonObject instance, String property, Number value) {
        instance.addProperty(property, value);
        instance.addProperty("current_gamemode", EndFightMod.gameMode);
        instance.addProperty("island_type", BigConfig.getSelectedConfig().selectedIsland);
        instance.addProperty("initial_beds", EndFightMod.initialBeds);
        instance.addProperty("initial_arrows", EndFightMod.initialArrows);
        instance.addProperty("beds_used", EndFightMod.bedsUsed);
        instance.addProperty("arrows_used", EndFightMod.arrowsUsed);
        instance.addProperty("arrows_hit", EndFightMod.arrowsHit);
        instance.addProperty("total_damage", EndFightMod.totalDamage);
        instance.addProperty("total_crystal_damage", EndFightMod.totalCrystalDamage);
        instance.addProperty("total_bed_damage", EndFightMod.totalBedDamage);
        instance.addProperty("total_arrow_damage", EndFightMod.totalArrowDamage);
        instance.addProperty("total_melee_damage", EndFightMod.totalMeleeDamage);
    }
}
