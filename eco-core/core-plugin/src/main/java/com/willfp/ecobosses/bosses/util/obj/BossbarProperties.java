package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

@Data
public class BossbarProperties {
    /**
     * The BossBar color.
     */
    private final BarColor color;

    /**
     * The BossBar style.
     */
    private final BarStyle style;
}
