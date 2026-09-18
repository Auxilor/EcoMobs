package com.willfp.ecomobs.spawner

import com.willfp.eco.core.entities.Entities
import com.willfp.ecomobs.event.EcoMobSpawnerSpawnEvent
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType

/**
 * Spawns [mobId] at [location] as the spawner at [spawnerLocation] would, marking the
 * entity so the rest of the plugin can tell it came from a spawner.
 *
 * With [noAI], the mob is spawned inert, exactly as vanilla's NoAI does.
 *
 * Shared by both spawner modes so they can't drift apart, which also makes it the one
 * place [EcoMobSpawnerSpawnEvent] has to be called from.
 */
fun spawnFromSpawner(
    spawnerLocation: Location,
    location: Location,
    mobId: String,
    noAI: Boolean = false
) {
    val spawnEvent = EcoMobSpawnerSpawnEvent(spawnerLocation, mobId, location)
    Bukkit.getPluginManager().callEvent(spawnEvent)

    if (spawnEvent.isCancelled) {
        return
    }

    val ecoMob = EcoMobs[mobId]

    val entity = if (ecoMob != null) {
        ecoMob.spawn(location, SpawnReason.SPAWNER)?.entity
    } else {
        Entities.lookup(mobId).spawn(location)
    }

    entity?.persistentDataContainer?.set(entityFromSpawnerKey, PersistentDataType.BYTE, 1)

    if (noAI) {
        (entity as? LivingEntity)?.setAI(false)
    }
}
