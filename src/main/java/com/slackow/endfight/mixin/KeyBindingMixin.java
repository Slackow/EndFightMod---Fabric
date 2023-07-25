package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Inject(method = "onKeyPressed", at = @At("HEAD"))
    private static void onKeyPressed(int keyCode, CallbackInfo ci) {
        if (keyCode != 0) {
            Config selectedConfig = BigConfig.getSelectedConfig();
            if (selectedConfig != null) {
                for (KeyBind keyBinding : selectedConfig.keyBindings) {
                    if (keyBinding.code == keyCode) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            // sendChatMessage
                            player.sendChatMessage(keyBinding.message);
                        }
                    }
                }
            }
        }
    }
}
