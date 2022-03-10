package com.slackow.endfight.util;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

public class DragonTargetPos {
    public static double x;
    public static double y;
    public static double z;

    // method_4328
    public static void drawBox(int color, Box box) {
        GL11.glDepthMask(false);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        WorldRenderer.method_6886(box, color);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }
}
