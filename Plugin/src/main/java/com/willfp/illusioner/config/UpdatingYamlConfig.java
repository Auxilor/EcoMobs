package com.willfp.illusioner.config;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.util.internal.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class UpdatingYamlConfig {
    public YamlConfiguration config;
    public File configFile;
    private final String name;
    private final boolean removeUnused;

    public UpdatingYamlConfig(String name, boolean removeUnused) {
        this.name = name + ".yml";
        this.removeUnused = removeUnused;
        init();
    }

    private void init() {
        if (!new File(IllusionerPlugin.getInstance().getDataFolder(), name).exists()) {
            createFile();
        }

        this.configFile = new File(IllusionerPlugin.getInstance().getDataFolder(), name);
        this.config = YamlConfiguration.loadConfiguration(configFile);

        update();
    }

    private void createFile() {
        IllusionerPlugin.getInstance().saveResource(name, false);
    }

    public void update() {
        try {
            config.load(configFile);

            InputStream newIn = IllusionerPlugin.getInstance().getResource(name);
            if(newIn == null) {
                Logger.error(name + " is null?");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(newIn, StandardCharsets.UTF_8));
            YamlConfiguration newConfig = new YamlConfiguration();
            newConfig.load(reader);

            if(newConfig.getKeys(true).equals(config.getKeys(true)))
                return;

            newConfig.getKeys(true).forEach((s -> {
                if (!config.getKeys(true).contains(s)) {
                    config.set(s, newConfig.get(s));
                }
            }));

            if(this.removeUnused) {
                config.getKeys(true).forEach((s -> {
                    if(!newConfig.getKeys(true).contains(s)) {
                        config.set(s, null);
                    }
                }));
            }

            config.save(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
