package com.slackow.endfight.commands;

import com.slackow.endfight.EndFightCommand;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.config.Config;
import com.slackow.endfight.gui.config.ConfigGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ReadOnlyLevelProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.slackow.endfight.gui.config.ConfigGUI.buttonName;
import static net.minecraft.util.Formatting.RED;

public class ResetCommand extends EndFightCommand {
    @Override
    public String getCommandName() {
        return "reset";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/reset [out] | /reset options";
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length > 0 && "options".equals(args[0])) {
//            //noinspection unchecked
//            CommandManager.getCommandMap().values().stream().sorted().forEachOrdered(command -> {
//                if (command instanceof EndFightCommand) {
//                    source.sendMessage(new LiteralText(RED + ((EndFightCommand) command).getUsageTranslationKey(source)));
//                }
//            });
            return;
        }
        boolean twice = args.length == 0 || !args[0].contains("o");
        if (source instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source;
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
                        oldInfo.method_233(),
                        oldInfo.hasStructures(),
                        oldInfo.isHardcore(),
                        oldInfo.getGeneratorType());
                long seed;
                Config cfg = BigConfig.getSelectedConfig();
                if (cfg.selectedIsland == -1) {
                    seed = new Random().nextLong();
                } else if (cfg.selectedIsland == -2) {
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
                // set Gamemode
                player.method_3170(cfg.gamemode);
                EndFightMod.giveInventory(player, cfg.inventory);
                player.teleportToDimension(1);
                String s;

                if (cfg.showSettings) {
                    String[] islands = {"Random", "Match World"};
                    s = "\n" +
                            "Island Type: [" + (cfg.selectedIsland < 0 ? islands[~cfg.selectedIsland] : cfg.islands.get(cfg.selectedIsland).getName() ) + "]\n" +
                            "Endermen: [" + ConfigGUI.enderManNames[cfg.enderMan] + "]\n" +
                            buttonName("Damage Info: [", cfg.damageInfo) + "]\n" +
                            "" +
                            "" +
                            "";
                } else {
                    s = "";
                }


                player.sendMessage(new LiteralText("Sent to End" + s));
                EndFightMod.time = System.currentTimeMillis();
            }

        }
    }
}
