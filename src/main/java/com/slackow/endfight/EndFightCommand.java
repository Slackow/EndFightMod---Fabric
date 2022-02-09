package com.slackow.endfight;

import net.minecraft.command.AbstractCommand;
import org.jetbrains.annotations.NotNull;

public abstract class EndFightCommand extends AbstractCommand {
    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof EndFightCommand) {
            return getCommandName().compareTo(((EndFightCommand) o).getCommandName());
        }
        return 1;
    }
}
