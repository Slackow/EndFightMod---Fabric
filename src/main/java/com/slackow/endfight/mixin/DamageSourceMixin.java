package com.slackow.endfight.mixin;

import com.slackow.endfight.explosion.BedExplosion;
import com.slackow.endfight.explosion.CrystalExplosion;
import com.slackow.endfight.explosion.IDamageSource;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IDamageSource {
    private String explosionType;

    @Inject(method = "explosion", at = @At("RETURN"))
    private static void checkExplosionType(Explosion explosion, CallbackInfoReturnable<DamageSource> cir) {
        DamageSource damageSource = cir.getReturnValue();
        if (explosion instanceof BedExplosion) {
            ((IDamageSource)damageSource).setExplosionType("Bed Explosion");
        } else if (explosion instanceof CrystalExplosion) { // only one instance of explosion being null and it's with crystals the dragon is healing from
            ((IDamageSource)damageSource).setExplosionType("Crystal Explosion");
        } else if (explosion == null) {
            ((IDamageSource)damageSource).setExplosionType("Crystal Bait");
        }
    }

    @Override
    public void setExplosionType(String explosionType) {
        this.explosionType = explosionType;
    }

    @Override
    public String getExplosionType() {
        return this.explosionType;
    }
}
