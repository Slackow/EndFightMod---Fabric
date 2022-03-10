package com.slackow.endfight.config;

import com.slackow.endfight.util.Island;
import com.slackow.endfight.util.KeyBind;
import com.slackow.endfight.util.Kit;
import com.slackow.endfight.util.Renameable;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

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
    public Kit inventory = new Kit("Default", new int[]{16777483, 16777473, 16777571, 16777477, 1073741842,
                    16777584, 16777542, 16777491, 83886446, 16777274, 16777571, 16777571, 16777571, 16777571, 16777571,
                    16777489, 402653446, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public boolean specificHealthBar = true;
    public int deathBox = 0;
    public int enderMan = 2;
    public List<KeyBind> keyBindings = new ArrayList<>();
    public String name = "Default";
    public boolean arrowHelp = false;
    public List<Island> islands = new ArrayList<>();
    public int selectedIsland = -1;
    public GameMode gamemode = GameMode.SURVIVAL;
    public boolean dGodCrystals = false;
    public boolean dGodDragon = false;
    public boolean dGodPlayer = false;
    public boolean dSeeTargetBlock = false;
    public boolean dPrintDebugMessages = false;
    public boolean showSettings = true;


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
