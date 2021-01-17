package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.BaseConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.illusioner.IllusionerPlugin;

public class Drops extends BaseConfig {
    /**
     * Instantiate drops.yml.
     */
    public Drops() {
        super("drops", false, IllusionerPlugin.getInstance());
    }
}
