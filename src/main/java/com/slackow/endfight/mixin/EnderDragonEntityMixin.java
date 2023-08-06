package com.slackow.endfight.mixin;

import com.redlimerl.speedrunigt.SpeedRunIGT;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.explosion.IDamageSource;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
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
    private int bedDamaged = 0;

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    /**
     * Move the damage tracking and logging to this method to avoid the bug caused by multiple DragonParts being damaged
     * simultaneously and accumulating.
     */
    @Override
    protected void applyDamage(DamageSource source, float damage) {
        if (this.method_4447()) {
            return;
        }
        damage = this.applyArmorDamage(source, damage);
        float f = damage = this.applyEnchantmentsToDamage(source, damage);
        damage = Math.max(damage - this.getAbsorption(), 0.0f);
        this.setAbsorption(this.getAbsorption() - (f - damage));
        if (damage == 0.0f) {
            return;
        }
        float f2 = this.getHealth();
        this.setHealth(f2 - damage);
        // Everything from here to the last 2 lines of this method is vanilla code from LivingEntity:applyDamage
        EndFightMod.totalDamage += damage;
        String sourceName = source.name;
        if (sourceName.equals("explosion")) {
            String explosionType = ((IDamageSource)source).getExplosionType();
            if (explosionType.equals("Bed Explosion")) {
                sourceName = explosionType;
                EndFightMod.totalBedDamage += damage;
            } else if (explosionType.equals("Crystal Explosion")) {
                sourceName = explosionType;
                EndFightMod.totalCrystalDamage += damage;
            }
        } else if (sourceName.equals("player")) {
            EndFightMod.totalMeleeDamage += damage;
        } else if (sourceName.equals("arrow")) {
            EndFightMod.totalArrowDamage += damage;
            EndFightMod.arrowsHit += 1;
        }
        if (getSelectedConfig().damageInfo) {
            MinecraftClient.getInstance().field_3805.sendMessage(new LiteralText("Dragon damaged by " + sourceName + ": " + damage));
        }
        //
        this.getDamageTracker().onDamage(source, f2, damage);
        this.setAbsorption(this.getAbsorption() - damage);
    }

    @Inject(method = "method_6302", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (getSelectedConfig().dGodDragon) {
                setHealth(getMaxHealth() - amount);
            }
            if (getHealth() <= 0) {
                int seconds;
                String timeType;
                EndFightMod.gameMode = world.getClosestPlayer(this, 1000).abilities.creativeMode ? 1 : 0;
                if (EndFightMod.SRIGT_LOADED) {
                    seconds = (int) (Medium.getInGameTime() / 1000);
                    Medium.completeEndfightTimer();
                    timeType = "[IGT]";
                } else {
                    seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
                    timeType = "[RTA]";
                }
                seconds = clamp(seconds, 0, 86399);
                MinecraftClient.getInstance().field_3805.sendMessage(
                        new LiteralText("Dragon Killed in " + LocalTime.ofSecondOfDay(seconds)
                                .format(DateTimeFormatter.ofPattern("mm:ss")) + " " + timeType));
                MinecraftClient.getInstance().field_3805.sendMessage(
                        new LiteralText("Total endfight time: " + LocalTime.ofSecondOfDay(seconds + 10)
                                .format(DateTimeFormatter.ofPattern("mm:ss")) + " " + timeType));
                EndFightMod.time = System.currentTimeMillis();
                EndFightMod.resetStats();
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
        if (!world.isClient && getSelectedConfig().chaosTech > 0) {
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
                if (dist >= 10.0 && dist <= 150.0D && (getSelectedConfig().chaosTech == 1 || bedDamaged > 0)) {
                    // System.out.println("you got the strat");
                    player.sendMessage(new LiteralText("You got Chaos Tech"));
                }
            }
            if (bedDamaged > 0) {
                bedDamaged--;
            }
        }
    }

    @Inject(method = "setAngry", at = @At("HEAD"))
    private void setAngry(EnderDragonPart source, DamageSource angry, float par3, CallbackInfoReturnable<Boolean> cir) {
        if (angry.isExplosive()) {
            bedDamaged = 20;
        }
    }

    private static int a = 0;

    @Inject(method = "method_2906", at = @At("TAIL"))
    public void newTarget(CallbackInfo ci){
        setNewTargetCounter++;
        if (!world.isClient && getSelectedConfig().dPrintDebugMessages) {
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
