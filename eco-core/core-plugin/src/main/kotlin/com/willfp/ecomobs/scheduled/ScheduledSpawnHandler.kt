package com.willfp.ecomobs.scheduled

import com.willfp.eco.core.Prerequisite
import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.plugin
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import java.util.UUID
import java.util.logging.Level

object ScheduledSpawnHandler : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDeathEvent) {
        val mob = event.entity as? Mob ?: return
        free(mob.scheduledPoint ?: return, mob.uniqueId)
    }

    @EventHandler
    fun handle(event: EcoMobDespawnEvent) {
        val mob = event.mob.entity as? Mob ?: return
        free(mob.scheduledPoint ?: return, mob.uniqueId)
    }

    /**
     * Runs before ChunkHandler untracks the mob, so the chunk the mob is leaving in is
     * recorded while the mob is still known to EcoMobs.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun handleUnload(event: EntitiesUnloadEvent) {
        for (entity in event.entities) {
            val mob = entity as? Mob ?: continue
            val key = mob.scheduledPoint ?: continue

            ScheduledSpawnState.moved(key, mob.uniqueId, mob.location.toMobPosition())
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleLoad(event: EntitiesLoadEvent) {
        for (entity in event.entities) {
            val mob = entity as? Mob ?: continue
            val key = mob.scheduledPoint ?: continue
            val ecoMob = mob.ecoMob ?: continue
            val point = EcoMobs[key.substringBefore(':')]?.scheduledSpawn?.points?.firstOrNull { it.key == key }
                ?: continue

            val outcome = ScheduledSpawnState.onLoad(key, mob.uniqueId, mob.location.toMobPosition(), point.maxAlive)

            if (outcome != LoadOutcome.REMOVE) {
                continue
            }

            val living = ecoMob.getLivingMob(mob) as? LivingMobImpl

            if (living == null) {
                mob.remove()
            } else {
                living.discard()
            }
        }
    }

    fun saveOnDisable() {
        try {
            if (!Prerequisite.HAS_FOLIA.isMet) {
                for ((key, mobs) in ScheduledSpawnState.tracked()) {
                    val ecoMob = EcoMobs[key.substringBefore(':')] ?: continue

                    for (uuid in mobs.keys) {
                        val living = ecoMob.getLivingMob(uuid) ?: continue

                        ScheduledSpawnState.moved(key, uuid, living.entity.location.toMobPosition())
                    }
                }
            }

            ScheduledSpawnState.save()
        } catch (exception: Exception) {
            plugin.logger.log(Level.WARNING, "Could not save scheduled spawns", exception)
        }
    }

    private fun free(key: String, uuid: UUID) {
        ScheduledSpawnState.free(key, uuid, EcoMobs[key.substringBefore(':')]?.scheduledSpawn?.nextDue() ?: 0L)
    }
}
