package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalMixin extends Entity {
    public EndCrystalMixin(World world) {
        super(world);
    }

    /**
     * @author Slackow
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void damage(DamageSource amount, float par2, CallbackInfoReturnable<Boolean> cir){
        if (cir.getReturnValue()) {
            if (!world.isClient && BigConfig.getSelectedConfig().dGodCrystals) {
                world.spawnEntity(new EndCrystalEntity(world, x, y, z));
            }
        }
    }
}
