package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.StaticBaseConfig;
import com.willfp.illusioner.IllusionerPlugin;

public class Attacks extends StaticBaseConfig {
    /**
     * Instantiate attacks.yml.
     */
    public Attacks() {
        super("attacks", IllusionerPlugin.getInstance());
    }
}
