package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.config.ConfigGUI;
import com.slackow.endfight.gui.core.ListGUI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen {
    @Shadow @Final private Screen parent;

    @Shadow @Final private GameOptions options;

    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci){
        buttons.removeIf((Predicate<ButtonWidget>) button -> button.id == 107);
        buttons.add(new ButtonWidget(6_22_2019, width / 2 + 5, height / 6 + 72 - 6, 150, 20, "End Fight Settings..."));
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    private void buttonClicked(ButtonWidget par1, CallbackInfo ci) {
        if (par1.id == 6_22_2019) {
            client.openScreen(new ConfigGUI(this, BigConfig.getSelectedConfig(), false));
        }
    }
}
