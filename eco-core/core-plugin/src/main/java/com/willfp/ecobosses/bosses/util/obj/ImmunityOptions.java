package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;

@Data
public class ImmunityOptions {
    /**
     * If is immune to fire.
     */
    private final boolean immuneToFire;

    /**
     * If is immune to suffocation.
     */
    private final boolean immuneToSuffocation;

    /**
     * If is immune to drowning.
     */
    private final boolean immuneToDrowning;

    /**
     * If is immune to projectiles.
     */
    private final boolean immuneToProjectiles;

    /**
     * If is immune to explosions.
     */
    private final boolean immuneToExplosions;
}
