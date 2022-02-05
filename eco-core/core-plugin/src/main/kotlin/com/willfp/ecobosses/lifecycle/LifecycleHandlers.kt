package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossDeathEvent
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
    fun onInjure(event: EntityDamageEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity] ?: return

        boss.boss.handleLifecycle(BossLifecycle.INJURE, entity.location)
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun onInjure(event: BossDeathEvent) {
        event.boss.boss.handleLifecycle(event.lifecycle, event.boss.entity?.location ?: return)
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun onInjure(event: BossSpawnEvent) {
        event.boss.handleLifecycle(BossLifecycle.SPAWN, event.location)
    }
}