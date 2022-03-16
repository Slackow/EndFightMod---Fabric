package com.slackow.endfight.config;

import com.slackow.endfight.util.Island;
import com.slackow.endfight.util.KeyBind;
import com.slackow.endfight.util.Kit;
import com.slackow.endfight.util.Renameable;
import net.minecraft.world.level.LevelInfo.GameMode;

import java.util.ArrayList;
import java.util.List;

import static com.slackow.endfight.gui.config.ConfigGUI.islandNames;

public class Config implements Renameable {

    // Options:
    // Death box visibility, (off, with hitboxes, always)
    // Health bar number, (off, on)
    // Inventories (List)
    // Damage info (off, on)
    // list for entire configs?
    // optional keybindings? (List??)
    // show options on start
    // show arrow help
    // 50/50
    // islands (with names)
    // god dragon
    // wait multiple configs

    public boolean damageInfo = true;
    public boolean specificHealthBar = true;
    public boolean showSettings = true;
    public boolean arrowHelp = false;
    public int deathBox = 0;
    public int enderMan = 2;
    public Kit inventory = new Kit(new int[]{16777483, 16777473, 16777571, 16777477, 1073741842,
            16777584, 16777542, 16777491, 83886446, 16777274, 16777571, 16777571, 16777571, 16777571, 16777571,
            16777489, 402653446, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public List<KeyBind> keyBindings = new ArrayList<>();
    public List<Island> islands = new ArrayList<>();
    public int selectedIsland = -2;
    public GameMode gamemode = GameMode.SURVIVAL;
    public boolean dSeeTargetBlock = false;
    public boolean dGodCrystals = false;
    public boolean dGodDragon = false;
    public boolean dGodPlayer = false;
    public boolean dPrintDebugMessages = false;
    public int chaosTech = 0;

    public String name = "Default";

    public String selectedIslandName() {
        return selectedIsland >= 0 ? "'" + islands.get(selectedIsland).getName() + "'" : islandNames[~selectedIsland];
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public Kit getInv() {
        return inventory;
    }
}
