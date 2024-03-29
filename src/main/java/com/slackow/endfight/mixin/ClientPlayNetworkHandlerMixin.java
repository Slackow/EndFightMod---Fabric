package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onJoin(GameJoinS2CPacket par1, CallbackInfo ci) {
        MinecraftClient.getInstance().player.sendMessage(new LiteralText("End Fight Mod Enabled"));
        if (EndFightMod.SRIGT_LOADED) {
            Medium.onGameJoinIGT();
        }
    }
}
