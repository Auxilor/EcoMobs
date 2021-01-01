package com.willfp.illusioner.proxy.proxies;

import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface EntityIllusionerProxy extends AbstractProxy {
    /**
     * Create boss bar for an illusioner.
     *
     * @param plugin The plugin that owns the boss bar.
     * @param color  The color of the boss bar.
     * @param style  The style of the boss bar.
     * @return The created boss bar.
     */
    BossBar createBossbar(@NotNull Plugin plugin,
                          @NotNull BarColor color,
                          @NotNull BarStyle style);
}
