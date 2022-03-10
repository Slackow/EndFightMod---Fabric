package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(World world) {
        super(world);
    }

    @Inject(method = "damage", at = @At("TAIL"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (BigConfig.getSelectedConfig().dGodPlayer && !source.isOutOfWorld()) {
            setHealth(20f);
        }
    }

}
