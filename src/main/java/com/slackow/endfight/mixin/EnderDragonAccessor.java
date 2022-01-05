package com.slackow.endfight.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonEntity.class)
public interface EnderDragonAccessor {
    @Accessor("target")
    void setTarget(Entity target);
}
