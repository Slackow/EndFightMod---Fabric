package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {

    @Redirect(method = "canSpawn", at = @At(target = "Lnet/minecraft/world/World;getGlobalDifficulty()Lnet/minecraft/world/Difficulty;", value = "INVOKE"))
    public Difficulty a(World instance) {
        if (BigConfig.getSelectedConfig().enderMan == 0) {
            return Difficulty.PEACEFUL;
        }
        return instance.getGlobalDifficulty();
    }
}
