package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.StaticBaseConfig;
import com.willfp.illusioner.IllusionerPlugin;

import java.io.IOException;

public class Drops extends StaticBaseConfig {
    /**
     * Instantiate drops.yml.
     */
    public Drops() {
        super("drops", IllusionerPlugin.getInstance());
    }

    /**
     * Save config to drops.yml.
     */
    public void save() {
        try {
            this.getConfig().save(this.getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
