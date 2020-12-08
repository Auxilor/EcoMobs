package com.willfp.illusioner.config;

import com.willfp.illusioner.config.configs.Config;
import com.willfp.illusioner.config.configs.Lang;

public class ConfigManager {
    private static final Lang LANG = new Lang();
    private static final Config CONFIG = new Config();

    /**
     * Update all configs
     * Called on /ecoreload
     */
    public static void updateConfigs() {
        LANG.update();
        CONFIG.update();
    }

    /**
     * Get lang.yml
     * @return lang.yml
     */
    public static Lang getLang() {
        return LANG;
    }

    /**
     * Get config.yml
     * @return config.yml
     */
    public static Config getConfig() {
        return CONFIG;
    }
}