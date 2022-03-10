package com.slackow.endfight.config;

import com.slackow.endfight.util.Island;
import com.slackow.endfight.util.Kit;
import net.minecraft.world.GameMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.slackow.endfight.EndFightMod.getDataPath;

public class BigConfig {

    private static BigConfig mine;

    public void save() {
        try {
            Files.write(getDataPath(), Arrays.asList(toString().split("\n")));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save configs", e);
        }
    }

    public static BigConfig getBigConfig() {
        if (mine != null) {
            return mine;
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
                                cfg.islands = Arrays.stream(value.split(";"))
                                        .map(Island::valueOf)
                                        .collect(Collectors.toCollection(ArrayList::new));
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
                            default:
                                System.out.println("WARNING: IGNORED LINE" + line);
                                break;
                        }
                    }
                }
                return mine = new BigConfig(configs, selected);
            } catch (IOException e) {
                return mine = new BigConfig();
            }
        }
    }

    public static Config getSelectedConfig() {
        BigConfig bigConfig = getBigConfig();
        List<Config> configs = bigConfig.configs;
        if (bigConfig.selectedConfig < configs.size() && bigConfig.selectedConfig >= 0) {
            return configs.get(bigConfig.selectedConfig);
        }
        throw new IllegalStateException("Send this error straight to Slackow#7890 on discord please(with everything below):\n" +
                "" + bigConfig.selectedConfig + configs + "\n");
    }

    @Override
    public String toString() {
        StringJoiner sb = new StringJoiner("\n");
        sb.add("=" + selectedConfig);
        for (Config cfg : configs) {
            sb.add(";" + cfg.getName());
            sb.add("deathBox=" + cfg.deathBox);
            sb.add("inventory=" + cfg.inventory);
            sb.add("enderMan=" + cfg.enderMan);
            sb.add("arrowHelp=" + cfg.arrowHelp);
            sb.add("specificHealthBar=" + cfg.specificHealthBar);
            sb.add("damageInfo=" + cfg.damageInfo);
            sb.add("selectedIsland=" + cfg.selectedIsland);
            sb.add("islands=" + cfg.islands.stream().map(Island::toString).collect(Collectors.joining(";")));
            sb.add("gamemode=" + cfg.gamemode.getGameModeId());
            sb.add("showSettings=" + cfg.showSettings);
            sb.add("dGodCrystals=" + cfg.dGodCrystals);
            sb.add("dGodDragon=" + cfg.dGodDragon);
            sb.add("dGodPlayer=" + cfg.dGodPlayer);
            sb.add("dSeeTargetBlock=" + cfg.dSeeTargetBlock);
            sb.add("dPrintDebugMessages=" + cfg.dPrintDebugMessages);
        }
        return sb.toString();
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
