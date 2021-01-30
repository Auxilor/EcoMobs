package com.willfp.illusioner.proxy.proxies;

import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.Location;
import org.bukkit.entity.Illusioner;
import org.jetbrains.annotations.NotNull;

public interface IllusionerHelperProxy extends AbstractProxy {
    /**
     * Spawn an illusioner.
     *
     * @param location     The location to spawn it at.
     * @return The created illusioner.
     */
    EntityIllusionerProxy spawn(@NotNull Location location);

    /**
     * Convert a normal illusioner to a plugin-based one.
     *
     * @param illusioner   The illusioner to convert.
     * @return The created illusioner.
     */
    EntityIllusionerProxy adapt(@NotNull Illusioner illusioner);
}
