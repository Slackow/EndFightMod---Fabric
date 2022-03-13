package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightCommand;
import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.commands.ResetCommand;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistry;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.math.MathHelper.clamp;

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
                    if (args.length <= 0) {
                        boolean god = BigConfig.getSelectedConfig().dGodPlayer ^= true;
                        source.sendMessage(new LiteralText((god) ? "God Mode Enabled" :
                                "God Mode Disabled"));
                        PlayerEntity player = (PlayerEntity) source;
                        player.setHealth(20f);
                        player.extinguish();
                        if (god) {
                            player.addStatusEffect(new StatusEffectInstance(11, 100000, 255, true));
                        } else {
                            player.clearStatusEffects();
                        }
                        player.getHungerManager().add(20, 1);
                        player.getHungerManager().add(1, -8); // -16
                    } else {
                        switch (args[0]) {
                            case "crystal": {
                                boolean god = BigConfig.getSelectedConfig().dGodCrystals ^= true;
                                source.sendMessage(new LiteralText((god) ? "Crystal God Mode Enabled" :
                                        "Crystal God Mode Disabled"));
                                break;
                            }
                            case "dragon": {
                                boolean god = BigConfig.getSelectedConfig().dGodDragon ^= true;
                                source.sendMessage(new LiteralText((god) ? "Dragon God Mode Enabled" :
                                        "Dragon God Mode Disabled"));
                                break;
                            }
                            default: {
                                source.sendMessage(new LiteralText("Unrecognized Subcommand"));
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public List<String> method_3276(CommandSource source, String[] args) {
                if (args.length == 1) {
                    if (args[0].isEmpty()) {
                        return Arrays.asList("crystal", "dragon");
                    }
                    if ("crystal".startsWith(args[0])) {
                        return Collections.singletonList("crystal");
                    } else if ("dragon".startsWith(args[0])) {
                        return Collections.singletonList("dragon");
                    }
                }
                return Collections.emptyList();
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
                for (Object loadedEntity : ((PlayerEntity) source).world.getLoadedEntities()) {
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
        // killdragon
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "killdragon";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/killdragon";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
                if (dragon.isPresent()) {
                    dragon.get().remove();
                    source.sendMessage(new LiteralText("Killed dragon"));
                } else  {
                    source.sendMessage(new LiteralText(RED + "No dragon found"));
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
                    EndFightMod.setInventory((PlayerEntity) source, BigConfig.getSelectedConfig().inventory);
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
                    EndFightMod.giveInventory((PlayerEntity) source, BigConfig.getSelectedConfig().getInv());
                }
            }
        });
        // reset (THE BIG ONE)
        registerCommand(new ResetCommand());
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
                return "/charge";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                chargeCommand(source);
            }
        });
        // roll
        registerCommand(new EndFightCommand() {
            @Override
            public String getCommandName() {
                return "roll";
            }

            @Override
            public String getUsageTranslationKey(CommandSource source) {
                return "/roll";
            }

            @Override
            public void execute(CommandSource source, String[] args) {
                getDragon(source.getWorld()).ifPresent(dragon -> {
                    dragon.field_3742 = dragon.x;
                    dragon.field_3751 = dragon.y + 1;
                    dragon.field_3752 = dragon.z;
                });
                source.sendMessage(new LiteralText("Rolled"));
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
                return "/goodcharge [distance, height]";
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
                        chargeCommand(source);
                    } else {
                        source.sendMessage(new LiteralText(RED + "No Dragon Found"));
                    }
                }
            }
        });
        //noinspection unchecked
        Medium.commandMap = (List<EndFightCommand>) getCommandMap().values().stream()
                .filter(cmd -> cmd instanceof EndFightCommand)
                .sorted()
                .collect(Collectors.toList());
    }

    private void chargeCommand(CommandSource source) {

        if (source instanceof PlayerEntity) {
            Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
            if (dragon.isPresent()) {
                EnderDragonEntity entityDragon = dragon.get();
                ((EnderDragonAccessor) entityDragon).setTarget((Entity) source);

                source.sendMessage(new LiteralText("Forced Dragon Charge"));
            } else {
                source.sendMessage(new LiteralText(RED + "No Dragon Found"));
            }
        }
    }

    private static void dragonHealth(CommandSource source, String[] args) {
        Optional<EnderDragonEntity> dragon = getDragon(source.getWorld());
        if (dragon.isPresent() && args.length > 0) {
            try {
                dragon.get().setHealth(clamp(Float.parseFloat(args[0]), 0, 200));
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
