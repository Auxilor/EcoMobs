package com.willfp.ecomobs.scheduled

import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.spawner.setOrRemove
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

private val scheduledPointKey = namespacedKeyOf(namespace = "ecomobs", key = "scheduled_point")

/**
 * A fixed place a mob spawns at on a timer.
 *
 * The world is looked up by name each time, so points in worlds loaded after EcoMobs work.
 */
class ScheduledPoint(
    val mobId: String,
    val id: String,
    val worldName: String,
    val position: Vector,
    val maxAlive: Int
) {
    val key = "$mobId:$id"

    val world: World?
        get() = Bukkit.getWorld(worldName)

    val location: Location?
        get() = world?.let { position.toLocation(it) }
}

/**
 * The key of the scheduled point this mob was spawned at, or null if it wasn't.
 */
var Mob.scheduledPoint: String?
    get() = persistentDataContainer.get(scheduledPointKey, PersistentDataType.STRING)
    set(value) = persistentDataContainer.setOrRemove(scheduledPointKey, value)

/**
 * The mob that owns the scheduled point [key], or null if no loaded mob does.
 */
internal fun scheduledPointOwner(key: String): EcoMob? = EcoMobs[key.substringBefore(':')]
