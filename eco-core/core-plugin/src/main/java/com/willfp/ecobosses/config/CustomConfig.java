package com.willfp.ecobosses.config;

import com.willfp.eco.util.config.StaticOptionalConfig;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CustomConfig extends StaticOptionalConfig {
    /**
     * Create new custom config.
     *
     * @param configName The name of the config.
     * @param config     The config.
     */
    public CustomConfig(@NotNull final String configName,
                        @NotNull final YamlConfiguration config) {
        super(configName, EcoBossesPlugin.getInstance(), config);
    }
}
