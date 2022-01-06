package com.willfp.ecobosses.config;

import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.ExtendableConfig;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.jetbrains.annotations.NotNull;

public class BaseBossConfig extends ExtendableConfig {
    /**
     * Create new EcoBoss config.
     *
     * @param configName The name of the config.
     */
    public BaseBossConfig(@NotNull final String configName) {
        super(configName, true, EcoBossesPlugin.getInstance(), EcoBossesPlugin.class, "bosses/", ConfigType.YAML);
    }
}
