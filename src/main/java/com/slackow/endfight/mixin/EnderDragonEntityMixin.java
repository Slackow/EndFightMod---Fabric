package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends LivingEntity {
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
                EndFightMod.time = System.currentTimeMillis();
            }
        }
    }
}
