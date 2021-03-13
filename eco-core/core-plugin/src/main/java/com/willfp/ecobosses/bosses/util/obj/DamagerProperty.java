package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class DamagerProperty {
    /**
     * The player.
     */
    private final Player player;

    /**
     * The damage.
     */
    private final double damage;
}
