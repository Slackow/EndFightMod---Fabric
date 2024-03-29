package com.slackow.endfight;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

public abstract class EndFightCommand extends AbstractCommand {
    public static void heal(PlayerEntity player) {
        player.setHealth(20f);
        player.extinguish();
        player.getHungerManager().add(20, 1);
        player.getHungerManager().add(1, -8); // -16
        player.fallDistance = 0;
    }

    @Override
    public boolean isAccessible(CommandSource source) {
        return true;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

}
