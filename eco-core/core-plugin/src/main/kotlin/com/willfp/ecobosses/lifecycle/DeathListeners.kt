package com.willfp.ecobosses.lifecycle

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.libreforge.tryAsPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class DeathListeners : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityDeathByEntityEvent) {
        val boss = Bosses[event.victim] ?: return

        boss.remove(BossLifecycle.KILL)

        val deathEvent = BossKillEvent(boss, event.killer.tryAsPlayer(), event.deathEvent)
        Bukkit.getPluginManager().callEvent(deathEvent)
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityDeathEvent) {
        val boss = Bosses[event.entity] ?: return

        boss.remove(BossLifecycle.KILL)

        val deathEvent = BossKillEvent(boss, null, event)
        Bukkit.getPluginManager().callEvent(deathEvent)
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: BossKillEvent) {
        event.boss.boss.processRewards(event)
    }
}
