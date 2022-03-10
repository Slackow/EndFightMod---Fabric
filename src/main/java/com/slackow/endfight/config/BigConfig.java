package com.slackow.endfight.config;

import com.slackow.endfight.util.ConfigLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.slackow.endfight.EndFightMod.getDataPath;
import static java.util.Collections.emptyList;

public class BigConfig {

    private static BigConfig mine;

    public void save() {
        try {
            ConfigLoader.saveConfig(this, getDataPath().toFile());
        } catch (IOException e) {
            throw new RuntimeException("Unable to save configs", e);
        }
    }

    public static BigConfig getBigConfig() {
        if (mine != null) {
            return mine;
        } else {
            try {
                mine = ConfigLoader.loadConfig(BigConfig.class, getDataPath().toFile());
                if (mine == null) {
                    return mine = new BigConfig();
                }
                return mine;
            } catch (IOException e) {
                return new BigConfig();
            }
        }
    }

    public static Config getSelectedConfig() {
        BigConfig bigConfig = getBigConfig();
        List<Config> configs = bigConfig.configs;
        if (bigConfig.selectedConfig < configs.size() && bigConfig.selectedConfig >= 0) {
            return configs.get(bigConfig.selectedConfig);
        } else {
            return emergency;
        }
    }

    // to prevent null pointers
    private static final Config emergency = new Config();

    static {
        emergency.name = "emergency";
        emergency.damageInfo = false;
        emergency.deathBox = 0;
        emergency.inventories = emptyList();
        emergency.specificHealthBar = false;
        emergency.keyBindings = emptyList();
        emergency.selectedInv = 0;
    }


    private BigConfig() {
        configs = new ArrayList<>();
    }

    public List<Config> configs;
    public int selectedConfig;

}
