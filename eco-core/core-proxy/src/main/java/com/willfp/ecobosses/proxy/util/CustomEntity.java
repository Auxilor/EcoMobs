package com.willfp.ecobosses.proxy.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface CustomEntity<T> {
    /**
     * Spawn a custom entity.
     *
     * @param location The location to spawn it at.
     * @return The created entity.
     */
    T spawn(@NotNull Location location);
}
