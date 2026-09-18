package com.willfp.ecomobs.spawner

import com.willfp.eco.core.entities.Entities
import com.willfp.ecomobs.event.EcoMobSpawnerSpawnEvent
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.stacking.stack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType

/**
 * Spawns [mobId] at [location] as the spawner at [spawnerLocation] would, marking the
 * entity so the rest of the plugin can tell it came from a spawner.
 *
 * With [noAI], the mob is spawned inert, exactly as vanilla's NoAI does.
 *
 * [stackSize] above one spawns the mob already standing for that many, which is how a
 * cycle's worth of mobs goes in as one entity while mob stacking is on.
 *
 * The one place [EcoMobSpawnerSpawnEvent] is called from.
 */
fun spawnFromSpawner(
    spawnerLocation: Location,
    location: Location,
    mobId: String,
    noAI: Boolean = false,
    stackSize: Int = 1
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

    if (stackSize > 1) {
        (entity as? Mob)?.let { it.stack.size = stackSize }
    }

    if (noAI) {
        (entity as? LivingEntity)?.setAI(false)
    }
}
