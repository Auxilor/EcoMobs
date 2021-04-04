package com.willfp.ecobosses.proxy.proxies;

import com.willfp.eco.core.proxy.AbstractProxy;
import com.willfp.ecobosses.proxy.util.CustomEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomEntitySpawnerProxy extends AbstractProxy {
    /**
     * Spawn custom entity.
     *
     * @param <T>         The class.
     * @param entityClass The custom entity proxy class.
     * @param location    The location.
     * @return The bukkit entity, or null if invalid class.
     */
    @Nullable <T extends LivingEntity> T spawnCustomEntity(Class<? extends CustomEntity<? extends LivingEntity>> entityClass,
                                                           @NotNull Location location);
}
