package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent

object ChunkHandler : Listener {
    @EventHandler
    fun handle(event: EntitiesUnloadEvent) {
        for (entity in event.entities) {
            val bukkitMob = entity as? Mob ?: continue
            val ecoMob = bukkitMob.ecoMob ?: continue
            val living = ecoMob.getLivingMob(bukkitMob.uniqueId) as? LivingMobImpl ?: continue

            living.unload()
        }
    }

    @EventHandler
    fun handle(event: EntitiesLoadEvent) {
        for (entity in event.entities) {
            val bukkitMob = entity as? Mob ?: continue
            val ecoMob = bukkitMob.ecoMob ?: continue

            // Restores the mob if it isn't already tracked.
            ecoMob.getLivingMob(bukkitMob)
        }
    }
}
