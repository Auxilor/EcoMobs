package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;

@Data
public class TeleportOptions {
    /**
     * The teleportation range.
     */
    private final int range;

    /**
     * The chance to teleport.
     */
    private final double chance;
}
