package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.explosion.BedExplosion;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedUsedMixin {
    @Redirect(method = "onActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/explosion/Explosion;"))
    private Explosion createBedExplosion(World instance, Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
        EndFightMod.bedsUsed += 1;

        // Identical to the code inside the method World::createExplosion, but we return a BedExplosion instead of an Explosion.
        // The type is later checked by DamageSourceMixin::setExplosionType to distinguish between crystals and beds.
        Explosion explosion = new BedExplosion(instance, entity, x, y, z, power);
        explosion.createFire = createFire;
        explosion.destructive = destructive;
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }
}
