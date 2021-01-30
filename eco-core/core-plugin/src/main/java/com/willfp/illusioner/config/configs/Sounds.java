package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.BaseConfig;
import com.willfp.illusioner.IllusionerPlugin;

public class Sounds extends BaseConfig {
    /**
     * Instantiate sounds.yml.
     */
    public Sounds() {
        super("sounds", false, IllusionerPlugin.getInstance());
    }
}
