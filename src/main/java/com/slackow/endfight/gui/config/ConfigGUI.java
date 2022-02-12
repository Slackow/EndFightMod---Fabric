package com.slackow.endfight.gui.config;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.core.ListGUI;
import com.slackow.endfight.gui.core.ViewGUI;
import com.slackow.endfight.util.KeyBind;
import com.slackow.endfight.util.Kit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.input.Keyboard;

// Options:
// Death box visibility, (off, with hitboxes, always)
// Health bar number, (off, on)
// Inventories (List)
// Damage info (off, on)
// list for entire configs?
// optional keybindings? (List??)

public class ConfigGUI extends Screen {

    private final ViewGUI<Config> from;
    private final Config obj;

    public ConfigGUI(ViewGUI<Config> from, Config obj) {
        this.from = from;
        this.obj = obj;
    }

    private static String buttonName(String prefix, boolean toggle){
        return prefix + (toggle ? "on11" : "off");
    }

    private static final String[] deathBoxNames = {"off", "hitboxes", "always"};


    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.clear();
        buttons.add(new ButtonWidget(0, width / 2 - 50, height / 6, 100, 20, "Death box visibility: " + deathBoxNames[obj.deathBox]));
        buttons.add(new ButtonWidget(1, width / 2 - 50, height / 6 + 25, 100, 20, buttonName("Specific Health bar: ", obj.specificHealthBar)));
        buttons.add(new ButtonWidget(2, width / 2 - 50, height / 6 + 50, 100, 20, buttonName("Show Damage Info: ", obj.damageInfo)));
        buttons.add(new ButtonWidget(3, width / 2 - 50, height / 6 + 75, 100, 20, "Inventories"));
        buttons.add(new ButtonWidget(4, width / 2 - 50, height / 6 + 100, 100, 20, "KeyBindings"));
        buttons.add(new ButtonWidget(5, width / 2 - 50, height / 6 + 125, 100, 20, "Done"));
        super.init();
        from.init();
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
                MinecraftClient.getInstance().openScreen(new ListGUI<>(this, obj.inventories, obj.selectedInv,
                        () -> new Kit("default", new int[40]),
                        (gui, inv) -> {
                            MinecraftClient.getInstance().openScreen(new InventoryCfgGUI(gui, inv));
                        },
                        (data, selected) -> {
                            obj.inventories = data;
                            obj.selectedInv = selected;
                        }));
                return;
            case 4:
                MinecraftClient.getInstance().openScreen(new ListGUI<KeyBind>(this, obj.keyBindings, -1,
                        () -> new KeyBind("default", Keyboard.KEY_ESCAPE, ""),
                        (gui, keybind) -> {
                            MinecraftClient.getInstance().openScreen(new KeybindGUI(gui, keybind));
                        },
                        (data, selected) -> obj.keyBindings = data) {
                    @Override
                    public boolean isSelectable() {
                        return false;
                    }
                });
                return;
            case 5:
                MinecraftClient.getInstance().openScreen(from);
                return;
        }
        init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        from.render(-1, -1, tickDelta);
        if (mouseX >= 0 || mouseY >= 0) {
            renderBackground();
        }
        super.render(mouseX, mouseY, tickDelta);
    }
}
