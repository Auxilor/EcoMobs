package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.StaticBaseConfig;
import com.willfp.illusioner.IllusionerPlugin;

public class Sounds extends StaticBaseConfig {
    /**
     * Instantiate sounds.yml.
     */
    public Sounds() {
        super("sounds", IllusionerPlugin.getInstance());
    }
}
