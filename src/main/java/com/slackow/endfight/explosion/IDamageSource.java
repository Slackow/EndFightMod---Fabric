package com.slackow.endfight.explosion;

import net.minecraft.world.explosion.Explosion;

public interface IDamageSource {
    void setExplosionType(String explosionType);
    String getExplosionType();
}
