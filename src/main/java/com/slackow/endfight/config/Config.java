package com.slackow.endfight.config;

import com.slackow.endfight.util.KeyBind;
import com.slackow.endfight.util.Kit;
import com.slackow.endfight.util.Renameable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config implements Renameable {

    // Options:
    // Death box visibility, (off, with hitboxes, always)
    // Health bar number, (off, on)
    // Inventories (List)
    // Damage info (off, on)
    // list for entire configs?
    // optional keybindings? (List??)

    public boolean damageInfo = true;
    public List<Kit> inventories = new ArrayList<>(
            (Collections.singleton(new Kit("Default", new int[]{16777483, 16777473, 16777571, 16777477, 1073741842,
                    16777584, 16777542, 16777491, 83886446, 16777274, 16777571, 16777571, 16777571, 16777571, 16777571,
                    16777489, 402653446, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}))));
    public int selectedInv = 0;
    public boolean specificHealthBar = true;
    public int deathBox = 0;
    public int enderMan = 2;
    public List<KeyBind> keyBindings = new ArrayList<>();
    public String name = "Default";

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
