package com.slackow.endfight;

import net.minecraft.command.AbstractCommand;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends AbstractCommand {
    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
