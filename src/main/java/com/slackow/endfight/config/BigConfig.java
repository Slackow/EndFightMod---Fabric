package com.slackow.endfight.config;

import com.slackow.endfight.util.Island;
import com.slackow.endfight.util.KeyBind;
import com.slackow.endfight.util.Kit;
import net.minecraft.world.GameMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.slackow.endfight.EndFightMod.getDataPath;

public class BigConfig {

    private static BigConfig bigConfig;

    private void saveThis() {
        try {
            Files.write(getDataPath(), Arrays.asList(toString().split("\n")));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save configs", e);
        }
    }

    public static void save() {
        if (bigConfig != null) {
            bigConfig.saveThis();
        }
    }


    public static BigConfig getBigConfig() {
        if (bigConfig != null) {
            return bigConfig;
        } else {
            try {
                Path dataPath = getDataPath();
                int selected = 0;
                List<Config> configs = new ArrayList<>();
                for (String line : Files.lines(dataPath).collect(Collectors.toList())) {
                    if (line.startsWith(";")) {
                        Config cfg = new Config();
                        cfg.setName(line.substring(1).trim());
                        configs.add(cfg);
                    }
                    int sign = line.indexOf('=');
                    if (sign >= 0) {
                        String key = line.substring(0, sign).trim();
                        String value = line.substring(sign + 1).trim();
                        if (key.isEmpty()) {
                            selected = Integer.parseUnsignedInt(value);
                            continue;
                        }
                        Config cfg = configs.get(configs.size() - 1);
                        switch (key) {
                            case "deathBox":
                                cfg.deathBox = Integer.parseUnsignedInt(value);
                                break;
                            case "inventory":
                                cfg.inventory = Kit.valueOf(value);
                                break;
                            case "enderMan":
                                cfg.enderMan = Integer.parseUnsignedInt(value);
                                break;
                            case "arrowHelp":
                                cfg.arrowHelp = Boolean.parseBoolean(value);
                                break;
                            case "specificHealthBar":
                                cfg.specificHealthBar = Boolean.parseBoolean(value);
                                break;
                            case "damageInfo":
                                cfg.damageInfo = Boolean.parseBoolean(value);
                                break;
                            case "selectedIsland":
                                cfg.selectedIsland = Integer.parseInt(value);
                                break;
                            case "islands":
                                if (value.contains(";")) {
                                    cfg.islands = Arrays.stream(value.split(";"))
                                            .map(Island::valueOf)
                                            .collect(Collectors.toCollection(ArrayList::new));
                                } else if (value.isEmpty()) {
                                    cfg.islands = new ArrayList<>();
                                } else {
                                    cfg.islands = new ArrayList<>(Collections.singleton(Island.valueOf(value)));
                                }
                                break;
                            case "keyBindings":
                                if (value.contains(";")) {
                                    cfg.keyBindings = Arrays.stream(value.split(";"))
                                            .map(KeyBind::valueOf)
                                            .collect(Collectors.toCollection(ArrayList::new));
                                } else if (value.isEmpty()) {
                                    cfg.keyBindings = new ArrayList<>();
                                } else {
                                    cfg.keyBindings = new ArrayList<>(Collections.singleton(KeyBind.valueOf(value)));
                                }
                                break;
                            case "gamemode":
                                cfg.gamemode = "1".equals(value) ? GameMode.CREATIVE : GameMode.SURVIVAL;
                                break;
                            case "showSettings":
                                cfg.showSettings = Boolean.parseBoolean(value);
                                break;
                            case "dGodCrystals":
                                cfg.dGodCrystals = Boolean.parseBoolean(value);
                                break;
                            case "dGodDragon":
                                cfg.dGodDragon = Boolean.parseBoolean(value);
                                break;
                            case "dGodPlayer":
                                cfg.dGodPlayer = Boolean.parseBoolean(value);
                                break;
                            case "dSeeTargetBlock":
                                cfg.dSeeTargetBlock = Boolean.parseBoolean(value);
                                break;
                            case "dPrintDebugMessages":
                                cfg.dPrintDebugMessages = Boolean.parseBoolean(value);
                                break;
                            case "chaosTech":
                                cfg.chaosTech = Integer.parseUnsignedInt(value);
                                break;
                            default:
                                System.out.println("WARNING: IGNORED LINE" + line);
                                break;
                        }
                    }
                }
                return bigConfig = new BigConfig(configs, selected);
            } catch (IOException e) {
                return bigConfig = new BigConfig();
            }
        }
    }

    public static Config getSelectedConfig() {
        BigConfig bigConfig = getBigConfig();
        List<Config> configs = bigConfig.configs;
        int selectedConfig = bigConfig.selectedConfig;
        if (selectedConfig < configs.size() && selectedConfig >= 0) {
            return configs.get(selectedConfig);
        }
        throw new IllegalStateException("Send this error straight to Slackow#7890 on discord please(with everything below):\n" +
                "" + selectedConfig + configs + "\n");
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("=" + selectedConfig);
        for (Config cfg : configs) {
            sj.add(";" + cfg.getName());
            sj.add("deathBox=" + cfg.deathBox);
            sj.add("inventory=" + cfg.inventory);
            sj.add("enderMan=" + cfg.enderMan);
            sj.add("arrowHelp=" + cfg.arrowHelp);
            sj.add("specificHealthBar=" + cfg.specificHealthBar);
            sj.add("damageInfo=" + cfg.damageInfo);
            sj.add("selectedIsland=" + cfg.selectedIsland);
            sj.add("islands=" + cfg.islands.stream().map(Island::toString).collect(Collectors.joining(";")));
            sj.add("keyBindings=" + cfg.keyBindings.stream().map(KeyBind::toString).collect(Collectors.joining(";")));
            sj.add("gamemode=" + cfg.gamemode.getGameModeId());
            sj.add("showSettings=" + cfg.showSettings);
            sj.add("chaosTech=" + cfg.chaosTech);
            sj.add("dGodCrystals=" + cfg.dGodCrystals);
            sj.add("dGodDragon=" + cfg.dGodDragon);
            sj.add("dGodPlayer=" + cfg.dGodPlayer);
            sj.add("dSeeTargetBlock=" + cfg.dSeeTargetBlock);
            sj.add("dPrintDebugMessages=" + cfg.dPrintDebugMessages);
        }
        return sj.toString();
    }

    private BigConfig() {
        configs = new ArrayList<>();
        configs.add(new Config());
        selectedConfig = 0;
    }

    private BigConfig(List<Config> configs, int selectedConfig) {
        this.configs = configs;
        this.selectedConfig = selectedConfig;
    }

    public List<Config> configs;
    public int selectedConfig;

}
