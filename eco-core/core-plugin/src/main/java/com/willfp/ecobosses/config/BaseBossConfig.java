package com.willfp.ecobosses.config;

import com.willfp.eco.util.config.ExtendableConfig;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.jetbrains.annotations.NotNull;

public class BaseBossConfig extends ExtendableConfig {
    /**
     * Create new EcoBoss config.
     *
     * @param configName The name of the config.
     */
    public BaseBossConfig(@NotNull final String configName) {
        super(configName, true, EcoBossesPlugin.getInstance(), EcoBossesPlugin.class, "bosses/");
    }
}
