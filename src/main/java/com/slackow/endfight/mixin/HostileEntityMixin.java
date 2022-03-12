package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {

    @Redirect(method = "canSpawn", at = @At(target = "Lnet/minecraft/world/World;field_7173:Lnet/minecraft/world/Difficulty;", value = "FIELD", opcode = Opcodes.GETFIELD))
    public Difficulty a(World instance) {
        if (BigConfig.getSelectedConfig().enderMan == 0) {
            return Difficulty.PEACEFUL;
        }
        return instance.field_7173;
    }
}
