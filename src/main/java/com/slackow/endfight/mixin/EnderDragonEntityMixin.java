package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.List;

import static com.slackow.endfight.config.BigConfig.getSelectedConfig;
import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends LivingEntity {

    @Shadow public double field_3742;

    @Shadow public double field_3751;

    @Shadow public double field_3752;

    @Shadow private Entity target;

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "method_6302", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (getSelectedConfig().damageInfo) {
                MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("Dragon damaged by " + source.getName() + ": " + amount));
            }
            if (getSelectedConfig().dGodDragon) {
                setHealth(getMaxHealth() - amount);
            }
            if (getHealth() <= 0) {
                int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
                seconds = clamp(seconds, 0, 86399);
                MinecraftClient.getInstance().field_3805.sendMessage(
                        new LiteralText("Dragon Killed in about " + LocalTime.ofSecondOfDay(seconds)
                                .format(DateTimeFormatter.ofPattern("mm:ss")) + " [RTA]"));
                EndFightMod.time = System.currentTimeMillis();
            }
        }
    }

    int setNewTargetCounter = 0; // increment this every time you call setNewTarget
    int lastSetNewTargetCount = 0;
    private double myDistanceTo(double targetX, double targetY, double targetZ) {
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
            //noinspection unchecked
            List<PlayerEntity> list = this.world.playerEntities;
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
            double targetY = player.boundingBox.minY + v;
            double dist = this.myDistanceTo(targetX, targetY, targetZ);
            if (dist >= 10.0 && dist <= 150.0D) {
                System.out.println("you got the strat");
                player.sendMessage(new LiteralText("You got the strat"));
            }
        }
    }

    private static int a = 0;

    @Inject(method = "method_2906", at = @At("TAIL"))
    public void newTarget(CallbackInfo ci){
        if (!world.isClient) {
            setNewTargetCounter++;
            int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
            seconds = clamp(seconds, 0, 186399);
            String format = LocalTime.ofSecondOfDay(seconds).format(DateTimeFormatter.ofPattern("h:mm:ss"));
            MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("\u00A7" + (6 + ((a++) & 3)) +
                    "Rolled 50/50 at " + format + " targeted (" + (target != null ? "player" : "block") + ")"));
//            System.out.println("------------------");
//            System.out.println(setNewTargetCounter);
//            System.out.println("dragon pos " + r(x) + " " + r(y) + " " + r(z) + " ");
//            System.out.println("target obj " + target);
//            System.out.println("target coords " + r(field_3742) + " " + r(field_3751) + " " + r(field_3752));
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void onTick(CallbackInfo ci){
        if (!world.isClient && getSelectedConfig().dSeeTargetBlock) {
            Medium.targetX = field_3742;
            Medium.targetY = field_3751;
            Medium.targetZ = field_3752;
        }
    }

}
