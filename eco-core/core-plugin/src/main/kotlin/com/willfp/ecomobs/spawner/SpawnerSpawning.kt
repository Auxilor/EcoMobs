package com.willfp.ecomobs.spawner

import com.willfp.eco.core.entities.Entities
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.Location
import org.bukkit.persistence.PersistentDataType

/**
 * Spawns [mobId] at [location] as a spawner would, marking the entity so the rest of
 * the plugin can tell it came from a spawner.
 *
 * Shared by both spawner modes so they can't drift apart.
 */
fun spawnFromSpawner(location: Location, mobId: String) {
    val ecoMob = EcoMobs[mobId]

    val entity = if (ecoMob != null) {
        ecoMob.spawn(location, SpawnReason.SPAWNER)?.entity
    } else {
        Entities.lookup(mobId).spawn(location)
    }

    entity?.persistentDataContainer?.set(entityFromSpawnerKey, PersistentDataType.BYTE, 1)
}
