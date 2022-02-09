package com.slackow.endfight.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderMixin {
//    @Inject(method = "method_4328", at = @At("HEAD"), cancellable = true)
//    private void renderHitboxes(Entity d, double e, double f, double g, float h, float par6, CallbackInfo ci){
//        if (d instanceof EnderDragonEntity) {
//            ci.cancel();
//        }
//    }
}
