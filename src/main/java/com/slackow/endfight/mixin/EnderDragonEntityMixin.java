package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.slackow.endfight.config.BigConfig.getSelectedConfig;
import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends LivingEntity {
    @Shadow private Entity target;

    @Shadow public double field_3742;

    @Shadow public double field_3751;

    @Shadow public double field_3752;

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "method_6302", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (getSelectedConfig().damageInfo) {
                MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("Dragon damaged by " + source.getName() + ": " + amount));
            }
            if (getHealth() <= 0) {
                int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
                seconds = clamp(seconds, 0, 86399);
                MinecraftClient.getInstance().field_3805.sendMessage(
                        new LiteralText("Dragon Killed in about " + LocalTime.ofSecondOfDay(seconds).format(DateTimeFormatter.ofPattern("mm:ss")) + " [RTA]"));
                EndFightMod.time = System.currentTimeMillis();
            }
        }
    }
    boolean isFarFromPlayer = false;

    private static double r(double a) {
        return Math.round(a * 100) / 100.0;
    }

    boolean isPast150 = false;
    boolean isFirstPlayerTarget = true;
    int setNewTargetCounter; // increment this every time you call setNewTarget
    int lastSetNewTargetCount;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void onUpdates(CallbackInfo ci) {

        double targetX = field_3742, targetY = field_3751, targetZ = field_3752;

        if (lastSetNewTargetCount != setNewTargetCounter) {

            lastSetNewTargetCount = setNewTargetCounter;

            if (!isPast150 && isFirstPlayerTarget) {

                if (this.distanceTo(targetX, targetY, targetZ) >= 10.0) {
                    System.out.println("you got the strat");
                    MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("You got the strat"));
                }
            }

            isFirstPlayerTarget = false;
        } else if (this.distanceTo(targetX, targetY, targetZ) >= 150.0) {
            isPast150 = true;
            isFirstPlayerTarget = true;
        } else {
            isPast150 = false;
            isFirstPlayerTarget = true;
        }
    }

    @Inject(method = "method_2906", at = @At("TAIL"))
    public void newTarget(CallbackInfo ci){
        setNewTargetCounter++;
        System.out.println("------------------");
        System.out.println(setNewTargetCounter);
        System.out.println("dragon pos " + r(x) + " " + r(y) + " " + r(z) + " ");
        System.out.println("target obj " + target);
        System.out.println("target coords " + r(field_3742) + " " + r(field_3751) + " " + r(field_3752));
    }

}
