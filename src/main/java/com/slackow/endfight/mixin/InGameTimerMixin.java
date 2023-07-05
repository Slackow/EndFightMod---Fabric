package com.slackow.endfight.mixin;
// the necessity of this class is quite frankly hilarious

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import com.redlimerl.speedrunigt.timer.category.RunCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.slackow.endfight.speedrunigt.EndFightCategory.END_FIGHT_CATEGORY;

@Pseudo
@Mixin(value = InGameTimer.class, remap = false)
public abstract class InGameTimerMixin {
    @Shadow
    public abstract void setStatus(@NotNull TimerStatus status);

    @Inject(method = "setCategory", at = @At("HEAD"))
    public void setCategory(RunCategory category, boolean canSendPacket, CallbackInfo ci) {
        if (category == END_FIGHT_CATEGORY) {
            setStatus(TimerStatus.NONE);
            PlayerEntity player = MinecraftClient.getInstance().player;
            // canSendPacket only true if done through GUI
            if (player != null && canSendPacket) {
                player.sendMessage(new LiteralText("Timer will start on execution of /reset"));
            }
        }
    }
}
