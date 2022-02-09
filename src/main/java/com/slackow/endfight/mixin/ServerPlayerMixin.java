package com.slackow.endfight.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity {

    @Shadow
    public boolean killedEnderdragon;

    public ServerPlayerMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "teleportToDimension", at = @At("HEAD"))
    private void tp(int par1, CallbackInfo ci){
    }
}
