package com.willfp.ecomobs.scheduled

import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType

private val scheduledPointKey = namespacedKeyOf("ecomobs", "scheduled_point")

/**
 * A fixed place a mob spawns at on a timer.
 *
 * The world is looked up by name each time, so points in worlds loaded after EcoMobs work.
 */
class ScheduledPoint(
    val mobId: String,
    val id: String,
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val maxAlive: Int
) {
    val key = "$mobId:$id"

    val world: World?
        get() = Bukkit.getWorld(worldName)

    val location: Location?
        get() = world?.let { Location(it, x, y, z) }
}

/**
 * The key of the scheduled point this mob was spawned at, or null if it wasn't.
 */
var Mob.scheduledPoint: String?
    get() = persistentDataContainer.get(scheduledPointKey, PersistentDataType.STRING)
    set(value) {
        if (value == null) {
            persistentDataContainer.remove(scheduledPointKey)
            return
        }

        persistentDataContainer.set(scheduledPointKey, PersistentDataType.STRING, value)
    }
