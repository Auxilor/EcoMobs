package com.willfp.illusioner.proxy.proxies;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

public interface EntityIllusionerProxy extends AbstractProxy {
    /**
     * Create boss bar for an illusioner.
     *
     * @param plugin The plugin that owns the boss bar.
     * @return The created boss bar.
     */
    BossBar createBossbar(@NotNull AbstractEcoPlugin plugin);
}
