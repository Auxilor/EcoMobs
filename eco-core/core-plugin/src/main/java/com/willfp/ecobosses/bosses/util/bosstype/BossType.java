package com.willfp.ecobosses.bosses.util.bosstype;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class BossType {
    /**
     * Spawn boss entity.
     *
     * @param location The location.
     * @return The entity.
     */
    public abstract LivingEntity spawnBossEntity(@NotNull Location location);
}
