package com.slackow.endfight.gui.config;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.core.ListGUI;
import com.slackow.endfight.util.Island;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameMode;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;

import static net.minecraft.util.Formatting.*;

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
    private static final String healthBar = "Specific Health bar: ";
    private static final String damageAlerts = "Show Damage Alerts: ";
    private static final String deathBox = "Death box visibility: ";
    private static final String chaosTech = "Chaos Tech: ";
    private static final String arrowHelp = "Arrow Help: ";
    private static final String enderMan = "Enderman: ";
    private final boolean advanced;
    private final String gamemode = "Gamemode: ";
    private final String seeTargetBlock = "See Target Block: ";
    private final String showSettings = "Show Settings On Reset: ";
    private final String printDebugMessages = "See Debug Messages: ";

    public ConfigGUI(Screen from, Config obj, boolean advanced) {
        this.from = from;
        this.obj = obj;
        this.advanced = advanced;
    }

    public static String buttonName(String prefix, boolean toggle){
        return prefix + (toggle ? "on" : "off");
    }

    public static final String[] deathBoxNames = {"never", "hitboxes", "always"};
    public static final String[] enderManNames = {"off", "no agro", "on"};
    public static final String[] islandNames = {"Match World", "Random"};
    public static final String[] chaosTechNames = {"off", "debug", "on"};

    public static final String[] tips = {
            "Displays a box around the dragon that shows\n" +
            "the range in which it will yeet you",
            "Shows the exact health of the dragon in the bossbar",
            "Shows how much damage the dragon is taking in chat",
            ""
    };


    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.clear();

        if (advanced) {
            buttons.add(new ButtonWidget(9, width / 2 - 155, height / 6 + 65 - 25, 150, 20, gamemode + obj.gamemode.getGameModeName()));
            buttons.add(new ButtonWidget(10, width / 2 - 155, height / 6 + 65, 150, 20, buttonName(seeTargetBlock, obj.dSeeTargetBlock)));
            buttons.add(new ButtonWidget(11, width / 2 + 5, height / 6 + 65 - 25, 150, 20, chaosTech + chaosTechNames[obj.chaosTech]));
            buttons.add(new ButtonWidget(12, width / 2 + 5, height / 6 + 65, 150, 20, buttonName(showSettings, obj.showSettings)));
            buttons.add(new ButtonWidget(13, width / 2 - 155, height / 6 + 90, 150, 20, buttonName(printDebugMessages, obj.dPrintDebugMessages)));
            buttons.add(new ButtonWidget(14, width / 2 + 5, height / 6 + 90, 150, 20, "\u00AF\\_(\u30C4)_/\u00AF"));
        } else {
            buttons.add(new ButtonWidget(0, width / 2 - 155, height / 6 + 65 - 25, 150, 20, deathBox + deathBoxNames[obj.deathBox]));
            buttons.add(new ButtonWidget(1, width / 2 - 155, height / 6 + 90 - 25, 150, 20, buttonName(damageAlerts, obj.damageInfo)));
            buttons.add(new ButtonWidget(2, width / 2 - 155, height / 6 + 115 - 25, 150, 20, buttonName(healthBar, obj.specificHealthBar)));
            buttons.add(new ButtonWidget(3, width / 2 - 155, height / 6 + 115, 150, 20, buttonName(arrowHelp, obj.arrowHelp)));


            buttons.add(new ButtonWidget(4, width / 2 + 5, height / 6 + 90 - 50, 150, 20, "Inventory..."));
            buttons.add(new ButtonWidget(5, width / 2 + 5, height / 6 + 115 - 50, 150, 20, "Keybindings..."));
            buttons.add(new ButtonWidget(6, width / 2 + 5, height / 6 + 115 - 25, 74, 20, "Islands..."));
            buttons.add(new ButtonWidget(7, width / 2 + 81, height / 6 + 115 - 25, 74, 20, obj.selectedIslandName()));
            buttons.add(new ButtonWidget(8, width / 2 + 5, height / 6 + 115, 150, 20, enderMan + enderManNames[obj.enderMan]));
        }
        buttons.add(new ButtonWidget(-1, width / 2 - 155, height / 6 - 2, 20, 20, "<"));
        buttons.add(new ButtonWidget(-2, width / 2 + 135, height / 6 - 2, 20, 20, (advanced ? GRAY : "") + "..."));
        buttons.add(new ButtonWidget(15, width / 2 - 100, height / 6 + 150, 200, 20, I18n.translate("gui.done")));
        super.init();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                obj.deathBox = (obj.deathBox + 1) % 3;
                button.message = deathBox + deathBoxNames[obj.deathBox];
                break;
            case 1:
                obj.damageInfo ^= true;
                button.message = buttonName(damageAlerts, obj.damageInfo);
                break;
            case 2:
                obj.specificHealthBar ^= true;
                button.message = buttonName(healthBar, obj.specificHealthBar);
                break;
            case 3:
                obj.arrowHelp ^= true;
                button.message = buttonName(arrowHelp, obj.arrowHelp);
                break;
            case 4:
                client.openScreen(new InventoryCfgGUI(this, obj.inventory));
                return;
            case 5:
                client.openScreen(new ListGUI<KeyBind>(this, obj.keyBindings, -1,
                        () -> new KeyBind("", Keyboard.KEY_ESCAPE, ""),
                        (gui, keybind) -> { //
                            client.openScreen(new KeybindGUI(gui, keybind));
                        },
                        (data, selected) -> {
                            obj.keyBindings = data;
                            BigConfig.save();
                        }, "Keybindings") {
                    @Override
                    public boolean isSelectable() {
                        return false;
                    }
                });
                return;
            case 6:
                client.openScreen(new ListGUI<>(this, obj.islands, obj.selectedIsland, () -> {
                    MinecraftServer server = MinecraftServer.getServer();
                    if (server.worlds.length < 3) {
                        server = null;
                    }
                    Island a = new Island(server != null ? server.getWorld(1).getSeed() : 0);
                    a.setName("");
                    return a;
                }, (gui, obj) -> { //
                    client.openScreen(new IslandGUI(gui, obj));
                }, (data, selected) -> {
                    obj.islands = data;
                    obj.selectedIsland = selected;
                    BigConfig.save();
                }, "Islands"));
                return;
            case 7:
                obj.selectedIsland = (obj.selectedIsland + 3) % (obj.islands.size() + 2) - 2;
                button.message = obj.selectedIslandName();
                break;
            case 8:
                obj.enderMan = (obj.enderMan + 1) % 3;
                button.message = enderMan + enderManNames[obj.enderMan];
                break;
            case 9:
                obj.gamemode = obj.gamemode == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL;
                button.message = gamemode + obj.gamemode.getGameModeName();
                break;
            case 10:
                obj.dSeeTargetBlock ^= true;
                button.message = buttonName(seeTargetBlock, obj.dSeeTargetBlock);
                break;
            case 11:
                obj.chaosTech = (obj.chaosTech + 1) % 3;
                button.message = chaosTech + chaosTechNames[obj.chaosTech];
                break;
            case 12:
                obj.showSettings ^= true;
                button.message = buttonName(showSettings, obj.showSettings);
                break;
            case 13:
                obj.dPrintDebugMessages ^= true;
                button.message = buttonName(printDebugMessages, obj.dPrintDebugMessages);
                break;
            case -1:
                client.openScreen(new ListGUI<>(from,
                        BigConfig.getBigConfig().configs,
                        BigConfig.getBigConfig().selectedConfig,
                        Config::new,
                        (gui, obj) -> { //
                            client.openScreen(new ConfigGUI(gui, obj, false));
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
                        }, "Profiles"));
                return;
            case -2:
                client.openScreen(new ConfigGUI(from, obj, !advanced));
                return;
            case 15:
                BigConfig.save();
                client.openScreen(from);
                return;
        }
        BigConfig.save();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, obj.getName(), width / 2, height / 6 + 10, 0xFFFFFF);
        drawCenteredString(textRenderer, "Profile", width / 2, height / 6 - 2, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
    }
}
