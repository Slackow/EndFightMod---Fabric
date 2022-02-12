package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.config.ConfigGUI;
import com.slackow.endfight.gui.core.ListGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci){
        buttons.add(new ButtonWidget(6_22_2019, width / 2 + 200, height / 6 - 12, 60, 20, "End"));
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    private void buttonClicked(ButtonWidget par1, CallbackInfo ci) {
        if (par1.id == 6_22_2019) {
            BigConfig bigConfig = BigConfig.getBigConfig();
            MinecraftClient.getInstance().openScreen(
                    new ListGUI<>(this, bigConfig.configs, bigConfig.selectedConfig,
                            Config::new,
                            (gui, obj) -> {
                                // open config GUI
                                MinecraftClient.getInstance().openScreen(new ConfigGUI(gui, obj));
                             },
                            (data, selected) -> {
                                bigConfig.configs = data;
                                bigConfig.selectedConfig = selected;
                                BigConfig.save();
                            }));
        }
    }
}
