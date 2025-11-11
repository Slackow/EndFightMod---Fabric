package com.slackow.endfight.mixin;
// the necessity of this class is quite frankly hilarious

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import com.redlimerl.speedrunigt.timer.category.RunCategory;
import com.slackow.endfight.EndFightMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.ControllablePlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;

import static com.slackow.endfight.speedrunigt.EndFightCategory.END_FIGHT_CATEGORY;

@Pseudo
@Mixin(value = InGameTimer.class, remap = false)
public abstract class InGameTimerMixin {
    @Shadow
    public abstract void setStatus(@NotNull TimerStatus status);

    @Shadow protected abstract void updateRecordString();

    @Shadow private String resultRecord;

    @Shadow public abstract boolean isCompleted();

    @Inject(method = "setCategory", at = @At("HEAD"))
    public void setCategory(RunCategory category, boolean canSendPacket, CallbackInfo ci){
        if (category == END_FIGHT_CATEGORY) {
            setStatus(TimerStatus.NONE);
            ControllablePlayerEntity player = MinecraftClient.getInstance().field_3805;
            // canSendPacket only true if done through GUI
            if (player != null && canSendPacket) {
                player.sendMessage(new LiteralText("Timer will start on execution of /reset"));
            }
        }
    }

    @ModifyArg(method = "writeRecordFile", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/io/File;Ljava/lang/String;)V", ordinal = 0), index = 0)
    private File writeToEndfightRecordsDir(File original) {
        return EndFightMod.endFightRecordsFile;
    }

    /**
     * Because we stop the timer on finishing runs and then immediately reset the stats fields to default values, we
     * need ot be careful we don't write these to the records file.
     */
    @Redirect(method = "setPause(ZLcom/redlimerl/speedrunigt/timer/TimerStatus;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lcom/redlimerl/speedrunigt/timer/InGameTimer;updateRecordString()V"))
    private void dontUpdateRecordStringAfterRun(InGameTimer instance, boolean toPause, TimerStatus toStatus, String reason) {
        if (!isCompleted()) {
            updateRecordString();
        }
    }
}
