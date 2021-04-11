package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;

import java.util.UUID;

@Data
public class DamagerProperty {
    /**
     * The player.
     */
    private final UUID playerUUID;

    /**
     * The damage.
     */
    private final double damage;
}
