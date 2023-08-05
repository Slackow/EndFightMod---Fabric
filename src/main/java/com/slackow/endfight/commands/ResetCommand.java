package com.slackow.endfight.commands;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import com.slackow.endfight.EndFightCommand;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.config.ConfigGUI;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.slackow.endfight.speedrunigt.EndFightCategory.END_FIGHT_CATEGORY;
import static net.minecraft.util.Formatting.*;

public class ResetCommand extends EndFightCommand {
    @Override
    public String getCommandName() {
        return "reset";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/reset [out|options]";
    }

    @Override
    public List<String> method_3276(CommandSource source, String[] args) {
        if (args.length == 1) {
            return Stream.of("options", "out")
                    .filter(option -> option.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length > 0 && "options".equals(args[0])) {
            Medium.commandMap.forEach(command ->
                    source.sendMessage(new LiteralText(RED + command.getUsageTranslationKey(source))));
            return;
        }
        boolean twice = args.length == 0 || !args[0].contains("o");
        if (source instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source;
            EndFightMod.gameMode = ((PlayerEntity) source).abilities.creativeMode ? 1 : 0;
            MinecraftServer server = MinecraftServer.getServer();
            for (Object objP : server.getPlayerManager().players) {
                if (objP instanceof PlayerEntity) {
                    PlayerEntity p = (PlayerEntity) objP;
                    if (p.dimension == 1) {
                        Arrays.fill(p.inventory.main, null);
                        Arrays.fill(p.inventory.armor, null);
                        // set creative mode
                        p.method_3170(GameMode.CREATIVE);
                        if (!twice) {
                            p.teleportToDimension(1);
                        }
                        p.teleportToDimension(0);

                    }
                }
            }
            File dim1 = new File(MinecraftClient.getInstance().runDirectory, "saves/" + server.getLevelName() + "/DIM1");
            boolean endExists = dim1.exists();
            if (endExists) {
                ServerWorld end = server.getWorld(1);
                // delete it then
                end.close();
                server.worlds = ArrayUtils.remove(server.worlds, 2);
                server.field_3858 = ArrayUtils.remove(server.field_3858, 2);
                end.close();
                try {
                    FileUtils.forceDelete(dim1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (dim1.exists() && !FileUtils.deleteQuietly(dim1)) {
                    player.sendMessage(new LiteralText(RED + "Failed to remove End Dimension :("));
                }
            }
            if (twice || !endExists){
                ServerWorld overWorld = server.worlds[0];
                LevelProperties oldInfo = overWorld.getLevelProperties();
                LevelInfo levelInfo = new LevelInfo(oldInfo.getSeed(),
                        oldInfo.getGamemode(),
                        oldInfo.hasStructures(),
                        oldInfo.isHardcore(),
                        oldInfo.getGeneratorType());
                long seed;
                Config cfg = BigConfig.getSelectedConfig();
                if (cfg.selectedIsland == -2) {
                    seed = new Random().nextLong();
                } else if (cfg.selectedIsland == -1) {
                    seed = overWorld.getSeed();
                } else {
                    seed = cfg.islands.get(cfg.selectedIsland).getSeed();
                }



                ServerWorld newEnd = new EndFightWorld(seed, server, overWorld.getSaveHandler(), server.getLevelName(), 1, levelInfo, overWorld, server.profiler);
                // copy difficulty
                newEnd.field_7173 = overWorld.field_7173;

                server.worlds = ArrayUtils.add(server.worlds, newEnd);
                server.field_3858 = ArrayUtils.add(server.field_3858, new long[100]);
                newEnd.addListener(new ServerWorldManager(server, newEnd));
                heal(player);
                player.clearStatusEffects();
                if (cfg.dGodPlayer) {
                    player.addStatusEffect(new StatusEffectInstance(11, 100000, 255, true));
                }
                // set Gamemode
                player.method_3170(cfg.gamemode);
                EndFightMod.giveInventory(player, cfg.inventory);
                EndFightMod.resetStats();
                player.teleportToDimension(1);

                if (cfg.showSettings) {
                    Formatting[] three = {RED, YELLOW, GREEN};
                    player.sendMessage(txt(""));
                    player.sendMessage(txt("Selected Profile: " + YELLOW + "'" + cfg.getName() + YELLOW + "'"));
                    player.sendMessage(txt("Island Type: " + three[Math.max(0, -cfg.selectedIsland)] + "[" + (cfg.selectedIslandName()) + "]"));
                    player.sendMessage(txt("Endermen: " + three[cfg.enderMan] + "[" + ConfigGUI.enderManNames[cfg.enderMan] + "]"));
                    if (cfg.dGodPlayer) {
                        player.sendMessage(txt(RED + "You are in god mode"));
                    }
                    if (cfg.dGodDragon) {
                        player.sendMessage(txt(RED + "The dragon is in god mode"));
                    }
                    if (cfg.dGodCrystals) {
                        player.sendMessage(txt(RED + "The crystals are in god mode"));
                    }
                }


                player.sendMessage(new LiteralText("Sent to End"));
                EndFightMod.time = System.currentTimeMillis();
                EndFightMod.arrowsHit = 0;
                EndFightMod.arrowsUsed = 0;
                EndFightMod.bedsUsed = 0;
                if (EndFightMod.SRIGT_LOADED) {
                    if (InGameTimer.getInstance().getCategory() == END_FIGHT_CATEGORY) {
                        if (!InGameTimer.getInstance().isCompleted()) {
                            InGameTimer.getInstance().writeRecordFile(false); // DNF
                        }
                        InGameTimer.reset();
                        InGameTimer.getInstance().setCategory(END_FIGHT_CATEGORY, false);
                        InGameTimer.getInstance().setStatus(TimerStatus.RUNNING);
                        InGameTimer.getInstance().setStartTime(EndFightMod.time);
                    }
                }
            }

        }
    }

    private static LiteralText txt(String s) {
        return new LiteralText(s);
    }
}
