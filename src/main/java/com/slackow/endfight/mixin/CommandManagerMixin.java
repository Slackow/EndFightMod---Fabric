package com.slackow.endfight.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.slackow.endfight.EndFightCommand;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.gui.core.ListGUI;
import com.slackow.endfight.util.SimpleStr;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static net.minecraft.util.Formatting.RED;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin extends CommandRegistry {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void epicCommands(CallbackInfo ci) {
        // god
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "god";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/god";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                if (source instanceof PlayerEntity) {
                    source.sendMessage(new LiteralText((EndFightMod.godMode ^= true) ? "God Mode Enabled" :
                            "God Mode Disabled"));
                    PlayerEntity player = (PlayerEntity) source;
                    player.setHealth(20f);
                    player.extinguish();
                    player.getHungerManager().add(20, 1);
                    player.getHungerManager().add(1, -8); // -16
                }
            }
        });
        // heal
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "heal";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/heal";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                if (source instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) source;
                    heal(player);
                    player.sendMessage(new LiteralText("Healed"));
                }
            }
        });
        // killcrystals
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "killcrystals";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/killcrystals";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                long count = 0;
                for (Object loadedEntity : source.getWorld().getLoadedEntities()) {
                    if (loadedEntity instanceof EndCrystalEntity) {
                        if (((EndCrystalEntity) loadedEntity).isAlive()) {
                            ((EndCrystalEntity) loadedEntity).remove();
                            count++;
                        }
                    }
                }
                if (count <= 0) {
                    source.sendMessage(new LiteralText(RED + "No End Crystals Found"));
                } else {
                    source.sendMessage(new LiteralText("Killed " + count + " End Crystals"));
                }
            }
        });
        // setinv
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "setinv";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/setinv";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) source;
                    JsonArray result =
                            Stream.concat(Arrays.stream(player.inventory.main),
                                            Arrays.stream(player.inventory.armor))
                                    .mapToInt(EndFightMod::itemToInt)
                                    .mapToObj(JsonPrimitive::new)
                                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
                    try {
                        Files.write(EndFightMod.getDataPath(), Collections.singleton(new Gson().toJson(result)));
                    } catch (IOException e) {
                        throw new CommandException("commands.generic.exception");
                    }
                    source.sendMessage(new LiteralText("Saved Inventory"));
                }
            }
        });
        // getinv
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "getinv";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/getinv";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    EndFightMod.giveInventory((PlayerEntity) source);
                }
            }
        });
        // reset (THE BIG ONE)
        registerCommand(new EndFightCommand() {
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
                    //noinspection unchecked
                    getCommandMap().values().stream().sorted().forEachOrdered(command -> {
                        if (command instanceof EndFightCommand) {
                            source.sendMessage(new LiteralText(RED + ((EndFightCommand) command).getUsageTranslationKey(source)));
                        }
                    });

                    MinecraftClient.getInstance().openScreen(
                            new ListGUI<>(null, Arrays.asList(
                                    new SimpleStr("one"),
                                    new SimpleStr("two"),
                                    new SimpleStr("three"),
                                    new SimpleStr("four"),
                                    new SimpleStr("five")),
                                    0, () -> new SimpleStr("Added"), (a, b) -> {}, (data, selected) -> {
                            }, "Profiles"));
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
                    boolean createIt = dim1.exists();
                    if (createIt) {
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
                        if (!dim1.exists() || FileUtils.deleteQuietly(dim1)) {
                            player.sendMessage(new LiteralText("Completely Removed the End Dimension"));
                        } else {
                            player.sendMessage(new LiteralText(RED + "Failed to remove End Dimension :("));
                        }
                    }
                    if (twice || !createIt){
                        ServerWorld overWorld = server.worlds[0];
                        LevelProperties oldInfo = overWorld.getLevelProperties();
                        LevelInfo levelInfo = new LevelInfo(new Random().nextLong(),
                                oldInfo.method_233(),
                                oldInfo.hasStructures(),
                                oldInfo.isHardcore(),
                                oldInfo.getGeneratorType());
                        System.out.println(levelInfo.getSeed());
                        ServerWorld newEnd = new MultiServerWorld(server, overWorld.getSaveHandler(), server.getLevelName(), 1, levelInfo, overWorld, server.profiler);
                        newEnd.field_7173 = overWorld.field_7173;
                        server.worlds = ArrayUtils.add(server.worlds, newEnd);
                        server.field_3858 = ArrayUtils.add(server.field_3858, new long[100]);
                        newEnd.addListener(new ServerWorldManager(server, newEnd));
                        heal(player);
                        // set survival
                        player.method_3170(GameMode.SURVIVAL);
                        //EndFightMod.giveInventory(player);
                        player.teleportToDimension(1);
                        player.sendMessage(new LiteralText("Sent to End"));
                        EndFightMod.time = System.currentTimeMillis();
                    }

                }
            }
        });
        // dragon health
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "dragonhealth";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/dragonhealth [health]";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                dragonHealth(source, args);
            }
        });
        // charge
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "charge";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/charge [health]";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                chargeCommand(source, args);
            }
        });
        // goodcharge
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "goodcharge";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/goodcharge [distance. height] [health]";
            }

            @Override
            public void execute(CommandSource source, String[] args) throws CommandException {
                if (source instanceof PlayerEntity) {
                    Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
                    if (dragon.isPresent()) {
                        int dist = 75;
                        int height = 24;
                        if (args.length >= 2) {
                            try {
                                dist = Integer.parseUnsignedInt(args[0]);
                                height = Integer.parseUnsignedInt(args[1]);
                            } catch (NumberFormatException e) {
                                throw new CommandException("invalid.input");
                            }
                        }

                        float yaw = ((PlayerEntity) source).getHeadRotation();
                        double sin = Math.sin((yaw + 90) * Math.PI / 180f),
                                cos = Math.cos((yaw + 90) * Math.PI / 180f);
                        EnderDragonEntity entityDragon = dragon.get();
                        entityDragon.updatePositionAndAngles(((PlayerEntity) source).x + cos * dist,
                                ((PlayerEntity) source).y + height,
                                ((PlayerEntity) source).z + sin * dist,
                                0,
                                (yaw + 360) % 360 - 180);
                        entityDragon.velocityX = 0;
                        entityDragon.velocityY = 0;
                        entityDragon.velocityZ = 0;
                        entityDragon.setForwardSpeed(0);
                        if ((args.length & 1) == 0) {
                            chargeCommand(source, new String[0]);
                        } else if (args.length > 0) {
                            chargeCommand(source, new String[]{args[args.length - 1]});
                        }
                    } else {
                        source.sendMessage(new LiteralText(RED + "No Dragon Found"));
                    }
                }
            }
        });
    }

    private void chargeCommand(CommandSource source, String[] args) {
        if (args.length > 0) {
            dragonHealth(source, args);
        }

        if (source instanceof PlayerEntity) {
            Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
            if (dragon.isPresent()) {
                EnderDragonEntity entityDragon = dragon.get();
                ((EnderDragonAccessor) entityDragon).setTarget((Entity) source);

                source.sendMessage(new LiteralText("Forced Dragon Charge"));
            } else if (args.length == 0) {
                source.sendMessage(new LiteralText(RED + "No Dragon Found"));
            }
        }
    }

    private static void heal(PlayerEntity player) {
        player.setHealth(20f);
        player.extinguish();
        player.getHungerManager().add(20, 1);
        player.getHungerManager().add(1, -8); // -16
        player.fallDistance = 0;
    }

    private static void dragonHealth(CommandSource source, String[] args) {
        Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
        if (dragon.isPresent() && args.length > 0) {
            try {
                dragon.get().setHealth(Math.min(200, Math.max(Float.parseFloat(args[0]), 0)));
            } catch (NumberFormatException e) {
                source.sendMessage(new LiteralText(RED + "Not a valid health"));
            }
        }
        source.sendMessage(new LiteralText(dragon.map(entityDragon -> "Dragon Health is: " + entityDragon.getHealth()).orElseGet(() -> RED + "No Dragon Found")));
    }
    private static Optional<EnderDragonEntity> getDragon(World world) {
        for (Object loadedEntity : world.getLoadedEntities()) {
            if (loadedEntity instanceof EnderDragonEntity) {
                return Optional.of((EnderDragonEntity) loadedEntity);
            }
        }
        return Optional.empty();
    }
}
