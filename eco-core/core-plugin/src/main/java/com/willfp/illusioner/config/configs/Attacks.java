package com.willfp.illusioner.config.configs;

import com.willfp.eco.util.config.BaseConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.illusioner.IllusionerPlugin;

public class Attacks extends BaseConfig {
    /**
     * Instantiate attacks.yml.
     */
    public Attacks() {
        super("attacks", false, IllusionerPlugin.getInstance());
    }
}
