package com.slackow.endfight.mixin;

import com.google.gson.JsonObject;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.InGameTimerUtils;
import com.slackow.endfight.EndFightMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = InGameTimerUtils.class, remap = false)
public class InGameTimerUtilsMixin {
    @Redirect(method = "convertTimelineJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V", ordinal = 0))
    private static void writeEndfightProperties(JsonObject instance, String property, Number value) {
        instance.addProperty(property, value);
        instance.addProperty("current_gamemode", EndFightMod.gameMode);
    }
}
