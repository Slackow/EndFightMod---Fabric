package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.explosion.BedExplosion;
import com.slackow.endfight.explosion.CrystalExplosion;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalMixin extends Entity {
    public EndCrystalMixin(World world) {
        super(world);
    }


    private static char state = 'c';
    /**
     * @author Slackow
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void damage(DamageSource amount, float par2, CallbackInfoReturnable<Boolean> cir){
        if (cir.getReturnValue()) {
            if (!world.isClient && BigConfig.getSelectedConfig().dGodCrystals) {

                //noinspection unchecked
                List<PlayerEntity> list = this.world.playerEntities;
                PlayerEntity player = list.get(0);
                player.sendMessage(new LiteralText("§" + (state = (char) ('c' + '4' - state)) + "*BOOM*"));
                world.spawnEntity(new EndCrystalEntity(world, x, y, z));
            }
        }
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/explosion/Explosion;"))
    private Explosion createCrystalExplosion(World instance, Entity entity, double x, double y, double z, float power, boolean destructive)  {
        // Identical to the code inside the method World::createExplosion, but we return a CrystalExplosion instead of an Explosion.
        // The type is later checked by DamageSourceMixin::setExplosionType to distinguish between crystals, connected crystals, and beds.
        Explosion explosion = new CrystalExplosion(instance, entity, x, y, z, power);
        explosion.createFire = false;
        explosion.destructive = destructive;
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void checkIfHitByArrow(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.name.equals("arrow")) {
            EndFightMod.arrowsHit += 1;
        }
    }
}
