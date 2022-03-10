package com.slackow.endfight.mixin;

import com.google.common.collect.Lists;
import com.slackow.endfight.EndFightMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slackow.endfight.config.BigConfig.getSelectedConfig;
import java.util.Iterator;
import java.util.List;
import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends LivingEntity {
    @Shadow private Entity target;
    @Shadow public double field_3742;
    @Shadow public double field_3751;
    @Shadow public double field_3752;
    private int ticksSincePickedTarget = 0;

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "method_6302", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (cir.getReturnValue()) {
            MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("Dragon damaged by " + source.getName() + ": " + amount));
            if (getHealth() <= 0) {
                int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
                seconds = clamp(seconds, 0, 86399);
                MinecraftClient.getInstance().field_3805.sendMessage(
                        new LiteralText("Dragon Killed in about " + LocalTime.ofSecondOfDay(seconds).format(DateTimeFormatter.ofPattern("mm:ss")) + " [RTA]"));
                EndFightMod.time = 0;
            }
        }
    }

    int setNewTargetCounter = 0; // increment this every time you call setNewTarget
    int lastSetNewTargetCount = 0;
    private double distanceTo(double targetX, double targetY, double targetZ) {
        double o;
        double p;
        double q;
        double r;
        o = targetX - this.x;
        p = targetY - this.y;
        q = targetZ - this.z;
        r = o * o + p * p + q * q;
        return Math.sqrt(r);
    }
    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void onUpdates(CallbackInfo ci) {
        if (lastSetNewTargetCount != setNewTargetCounter) {
            lastSetNewTargetCount = setNewTargetCounter;
            List<PlayerEntity> list = Lists.newArrayList((Iterable)this.world.playerEntities);
            PlayerEntity player = list.get(0);
            double targetX = player.x;
            double targetZ = player.z;
            double s = targetX - this.x;
            double t = targetZ - this.z;
            double u = Math.sqrt(s * s + t * t);
            double v = 0.4000000059604645D + u / 80.0D - 1.0D;
            if (v > 10.0D) {
                v = 10.0D;
            }
            double targetY = ((Entity)(player)).getBoundingBox().minY + v;
            if (this.distanceTo(targetX, targetY, targetZ) >= 10.0 && this.distanceTo(targetX, targetY, targetZ) <= 150.0D) {
                System.out.println("you got the strat");
                player.sendMessage(new LiteralText("You got the strat"));
            }
        }
    }

    @Inject(method = "method_2906", at = @At("TAIL"))
    public void newTarget(CallbackInfo ci){
        setNewTargetCounter++;
    }
}
