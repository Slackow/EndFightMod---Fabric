package com.slackow.endfight.util;

import com.slackow.endfight.EndFightCommand;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * A Hacky way of transferring data between client and server, don't depend on anything useful actually being here
 * it's more of an "If it's present, take it" kinda deal. I'm not too experienced with not mixing these two, but
 * I know I shouldn't do it, so I figured maybe it'd be better if I just put it all in one place because I don't know
 * how proxies work lmao.
 */
public class Medium {
    public static double targetX;
    public static double targetY;
    public static double targetZ;
    public static List<EndFightCommand> commandMap;

    // method_4328
    // I didn't know where else to place this method it doesn't really fit here
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
