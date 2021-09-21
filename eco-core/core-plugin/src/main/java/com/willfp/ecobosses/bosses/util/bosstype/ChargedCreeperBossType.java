package com.willfp.ecobosses.bosses.util.bosstype;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class ChargedCreeperBossType extends VanillaBossType {
    /**
     * Create new Charged Creeper boss type.
     */
    ChargedCreeperBossType() {
        super(Creeper.class);
    }

    /**
     * Spawn a charged creeper.
     *
     * @param location The location.
     * @return The entity.
     */
    @Override
    public LivingEntity spawnBossEntity(@NotNull final Location location) {
        Creeper creeper = Objects.requireNonNull(location.getWorld()).spawn(location, Creeper.class);
        creeper.setPowered(true);
        return creeper;
    }
}
