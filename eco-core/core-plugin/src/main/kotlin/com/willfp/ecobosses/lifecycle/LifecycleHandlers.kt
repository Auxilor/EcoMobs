package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossDespawnEvent
import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.ecobosses.events.BossSpawnEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class LifecycleHandlers : Listener {
    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: EntityDamageEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity] ?: return

        boss.boss.handleLifecycle(BossLifecycle.INJURE, entity.location, entity)
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossKillEvent) {
        val entity = event.boss.entity

        event.boss.boss.handleLifecycle(BossLifecycle.KILL, entity.location, entity)
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossDespawnEvent) {
        val entity = event.boss.entity

        event.boss.boss.handleLifecycle(BossLifecycle.DESPAWN, entity.location, entity)
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossSpawnEvent) {
        event.boss.handleLifecycle(BossLifecycle.SPAWN, event.location, null)
    }
}