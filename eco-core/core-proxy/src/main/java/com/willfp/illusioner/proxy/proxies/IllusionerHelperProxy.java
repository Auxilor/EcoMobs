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
     * @param maxHealth    The health for the illusioner to have.
     * @param attackDamage The attack damage for the illusioner to have.
     * @param name         The name of the illusioner.
     * @return The created illusioner.
     */
    EntityIllusionerProxy spawn(@NotNull Location location,
                                double maxHealth,
                                double attackDamage,
                                @NotNull String name);

    /**
     * Convert a normal illusioner to a plugin-based one.
     *
     * @param illusioner   The illusioner to convert.
     * @param location     The location to spawn it at.
     * @param maxHealth    The health for the illusioner to have.
     * @param attackDamage The attack damage for the illusioner to have.
     * @param name         The name of the illusioner.
     * @return The created illusioner.
     */
    EntityIllusionerProxy adapt(@NotNull Illusioner illusioner,
                                @NotNull Location location,
                                double maxHealth,
                                double attackDamage,
                                @NotNull String name);
}
