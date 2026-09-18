package com.willfp.ecomobs.event

import org.bukkit.Location

/**
 * An event about a spawner block, rather than about a mob.
 */
interface SpawnerEvent {
    /**
     * The location of the spawner.
     */
    val location: Location

    /**
     * The ID of the mob the spawner spawns, or null if it has never been set.
     */
    val mobId: String?
}
