package com.willfp.ecobosses.config;

import com.willfp.eco.core.config.yaml.YamlTransientConfig;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CustomConfig extends YamlTransientConfig {
    /**
     * The name of the config.
     */
    @Getter
    private final String configName;

    /**
     * Create new custom config.
     *
     * @param configName The name of the config.
     * @param config     The config.
     */
    public CustomConfig(@NotNull final String configName,
                        @NotNull final YamlConfiguration config) {
        super(config);
        this.configName = configName;
    }
}
