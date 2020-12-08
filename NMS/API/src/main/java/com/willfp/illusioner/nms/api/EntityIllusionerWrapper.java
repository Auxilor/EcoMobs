package com.willfp.illusioner.nms.api;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.plugin.Plugin;

/**
 * NMS Interface for managing illusioner bosses
 */
public interface EntityIllusionerWrapper {
    void createBossbar(Plugin plugin, BarColor color, BarStyle style);
}