package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin extends HostileEntity {

    public EndermanMixin(World world) {
        super(world);
    }

    @ModifyVariable(method = "isPlayerStaring", at = @At("STORE"), ordinal = 0)
    public Vec3d isStaring(Vec3d value) {
        if (BigConfig.getSelectedConfig().enderMan == 1) {
            // Replace player direction with looking straight down
            return new Vec3d(0, -1, 0);
        } else {
            return value;
        }

    }

}
