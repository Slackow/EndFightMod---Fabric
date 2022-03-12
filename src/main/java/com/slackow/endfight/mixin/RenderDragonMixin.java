package com.slackow.endfight.mixin;

import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.slackow.endfight.config.BigConfig.getSelectedConfig;
import static com.slackow.endfight.util.Medium.*;

@Mixin(EnderDragonEntityRenderer.class)
public class RenderDragonMixin {

    @Inject(method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;DDDFF)V", at = @At("TAIL"))
    private void render(EnderDragonEntity d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
        int a = getSelectedConfig().deathBox;
        if (a != 0 && (a == 2 || EntityRenderDispatcher.field_5192)) {
            double dx = e - d.x;
            double dy = f - d.y;
            double dz = g - d.z;
            drawBox(0xff0000, d.partHead.boundingBox.expand(1.0D, 1.0D, 1.0D).offset(dx, dy, dz));
            drawBox(0x00ff00, Box.of(targetX, targetY, targetZ, targetX, targetY, targetZ)
                    .expand(0.5, 0.5, 0.5).offset(dx, dy, dz));
        }
    }

}
