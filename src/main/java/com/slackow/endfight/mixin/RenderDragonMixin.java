package com.slackow.endfight.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.slackow.endfight.config.BigConfig.getSelectedConfig;

@Mixin(EnderDragonEntityRenderer.class)
public class RenderDragonMixin {
    // method_4328
    private void drawBoxes(int color, Box... boxes) {
        GL11.glDepthMask(false);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        for (Box box : boxes) {
            WorldRenderer.method_6886(box, color);
        }
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    @Inject(method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;DDDFF)V", at = @At("TAIL"))
    private void render(EnderDragonEntity d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
        int a = getSelectedConfig().deathBox;
        if (a != 0 && (a == 2 || EntityRenderDispatcher.field_5192)) {
            double dx = e - d.x;
            double dy = f - d.y;
            double dz = g - d.z;
            drawBoxes(0xff0000, d.partHead.boundingBox.expand(1.0D, 1.0D, 1.0D).offset(dx, dy, dz));
        }
    }

}
