package com.willfp.illusioner.config;

import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.illusioner.config.configs.Attacks;
import com.willfp.illusioner.config.configs.Drops;
import com.willfp.illusioner.config.configs.Sounds;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IllusionerConfigs {
    /**
     * drops.yml.
     */
    public static final Drops DROPS = new Drops();

    /**
     * sounds.yml.
     */
    public static final Sounds SOUNDS = new Sounds();

    /**
     * attacks.yml.
     */
    public static final Attacks ATTACKS = new Attacks();

    /**
     * Update all configs.
     */
    @ConfigUpdater
    public void updateConfigs() {
        DROPS.save();
        SOUNDS.update();
        ATTACKS.update();
    }
}
