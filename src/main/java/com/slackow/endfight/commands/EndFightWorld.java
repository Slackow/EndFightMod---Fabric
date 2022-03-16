package com.slackow.endfight.commands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ReadOnlyLevelProperties;
import org.lwjgl.input.Keyboard;

public class EndFightWorld extends MultiServerWorld {

    private static long seed;
    public EndFightWorld(long seed, MinecraftServer server, SaveHandler saveHandler, int i, ServerWorld overWorld, Profiler profiler) {
        super(g(server, seed), saveHandler, i, overWorld, profiler);
    }
    // awesome hack to run code before super
    private static MinecraftServer g(MinecraftServer server, long seed) {
        EndFightWorld.seed = seed;
        return server;
    }

    @Override
    public long getSeed() {

        return seed;
    }
}
