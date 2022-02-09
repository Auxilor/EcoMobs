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

        val location = "${event.location.world}: ${event.location.x}, ${event.location.y}, ${event.location.z}"

        plugin.logger.info("&a${event.boss.id}&r was spawned by &a${event.spawner?.name}&r at &a$location")
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossKillEvent) {
        if (!plugin.configYml.getBool("log-spawn-kill")) {
            return
        }

        val loc = event.boss.entity?.location
        val location = "${loc?.world}: ${loc?.x}, ${loc?.y}, ${loc?.z}"

        plugin.logger.info("&a${event.boss.boss.id}&r was killed by &a${event.killer?.name}&r at &a$location")
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    fun handle(event: BossDespawnEvent) {
        if (!plugin.configYml.getBool("log-spawn-kill")) {
            return
        }
        val loc = event.boss.entity?.location
        val location = "${loc?.world}: ${loc?.x}, ${loc?.y}, ${loc?.z}"

        plugin.logger.info("&a${event.boss.boss.id}&r despawned at &a$location")
    }
}