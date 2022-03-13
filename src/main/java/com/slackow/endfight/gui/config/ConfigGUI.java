package com.slackow.endfight.gui.config;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.core.ListGUI;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;

// Options:
// Death box visibility, (off, with hitboxes, always)
// Health bar number, (off, on)
// Inventories (List)
// Damage info (off, on)
// list for entire configs?
// optional keybindings? (List??)

public class ConfigGUI extends Screen {

    private final Screen from;
    private final Config obj;

    public ConfigGUI(Screen from, Config obj) {
        this.from = from;
        this.obj = obj;
    }

    public static String buttonName(String prefix, boolean toggle){
        return prefix + (toggle ? "on" : "off");
    }

    public static final String[] deathBoxNames = {"never", "hitboxes", "always"};
    public static final String[] enderManNames = {"off", "no agro", "on"};


    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.clear();
        buttons.add(new ButtonWidget(0, width / 2 - 155, height / 6 + 65, 150, 20, "Death box visibility: " + deathBoxNames[obj.deathBox]));
        buttons.add(new ButtonWidget(1, width / 2 + 5, height / 6 + 65, 150, 20, buttonName("Specific Health bar: ", obj.specificHealthBar)));
        buttons.add(new ButtonWidget(2, width / 2 - 155, height / 6 + 90, 150, 20, buttonName("Show Damage Info: ", obj.damageInfo)));
        buttons.add(new ButtonWidget(3, width / 2 + 5, height / 6 + 90, 150, 20, "Inventory..."));
        buttons.add(new ButtonWidget(4, width / 2 + 5, height / 6 + 115, 150, 20, "Keybindings..."));




        buttons.add(new ButtonWidget(-1, width / 2 - 200, height / 6 - 2, 20, 20, "<"));
        buttons.add(new ButtonWidget(10, width / 2 - 100, height / 6 + 150, 200, 20, I18n.translate("gui.done")));
        super.init();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                obj.deathBox = (obj.deathBox + 1) % 3;
                break;
            case 1:
                obj.specificHealthBar ^= true;
                break;
            case 2:
                obj.damageInfo ^= true;
                break;
            case 3:
                client.openScreen(new InventoryCfgGUI(this, obj.inventory));
                return;
            case 4:
                client.openScreen(new ListGUI<KeyBind>(this, obj.keyBindings, -1,
                        () -> new KeyBind("Shortcut", Keyboard.KEY_ESCAPE, ""),
                        (gui, keybind) -> {
                    //
                            client.openScreen(new KeybindGUI(gui, keybind));
                        },
                        (data, selected) -> obj.keyBindings = data, "Keybindings") {
                    @Override
                    public boolean isSelectable() {
                        return false;
                    }
                });
                return;
            case -1:
                client.openScreen(new ListGUI<>(from,
                        BigConfig.getBigConfig().configs,
                        BigConfig.getBigConfig().selectedConfig,
                        Config::new,
                        (gui, obj) -> {
                    //
                            client.openScreen(new ConfigGUI(gui, obj));
                        },
                        (list, selected) -> {
                            if (list.isEmpty()) {
                                BigConfig.getBigConfig().configs = new ArrayList<>(Collections.singleton(new Config()));
                                BigConfig.getBigConfig().selectedConfig = 0;
                            } else {
                                BigConfig.getBigConfig().configs = list;
                                BigConfig.getBigConfig().selectedConfig = selected;
                            }
                            BigConfig.save();
                        }, "Profiles"
                ));
                return;
            case 10:
                client.openScreen(from);
                BigConfig.save();
                return;
        }
        init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, obj.getName(), width / 2, height / 6 + 10, 0xFFFFFF);
        drawCenteredString(textRenderer, "Config", width / 2, height / 6 - 2, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
    }
}
