package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.events.BossDespawnEvent
import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ConsoleLoggers(
    private val plugin: LibReforgePlugin
) : Listener {
    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossSpawnEvent) {
        if (!plugin.configYml.getBool("log-spawn-kill")) {
            return
        }

        plugin.logger.info("${event.boss.id} was spawned by ${event.spawner} at ${event.location}")
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossKillEvent) {
        if (!plugin.configYml.getBool("log-spawn-kill")) {
            return
        }

        plugin.logger.info("${event.boss.boss.id} was killed by ${event.killer} at ${event.boss.entity?.location}")
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossDespawnEvent) {
        if (!plugin.configYml.getBool("log-spawn-kill")) {
            return
        }

        plugin.logger.info("${event.boss.boss.id} despawned at ${event.boss.entity?.location}")
    }
}