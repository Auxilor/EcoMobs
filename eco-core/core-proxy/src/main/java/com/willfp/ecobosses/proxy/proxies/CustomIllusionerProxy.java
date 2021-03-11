package com.willfp.ecobosses.proxy.proxies;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

public interface CustomIllusionerProxy extends AbstractProxy {
    /**
     * Spawn an illusioner.
     *
     * @param location     The location to spawn it at.
     * @return The created illusioner.
     */
    CustomIllusionerProxy spawn(@NotNull Location location);
}
