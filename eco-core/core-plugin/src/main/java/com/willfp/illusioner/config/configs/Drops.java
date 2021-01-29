package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.StaticBaseConfig;
import com.willfp.illusioner.IllusionerPlugin;

public class Drops extends StaticBaseConfig {
    /**
     * Instantiate drops.yml.
     */
    public Drops() {
        super("drops", IllusionerPlugin.getInstance());
    }
}
