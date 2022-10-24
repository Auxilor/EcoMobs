package com.willfp.ecobosses.spawn

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossSpawnEvent
import org.bukkit.Bukkit

object AutospawnHandler {
    private var tick = 1

    fun startSpawning(plugin: EcoPlugin) {
        plugin.scheduler.runTimer(1, 1) {
            for (boss in Bosses.values()) {
                if (boss.autoSpawnInterval < 0) {
                    continue
                }

                if (tick % boss.autoSpawnInterval != 0) {
                    continue
                }

                val location = boss.autoSpawnLocations.randomOrNull() ?: continue
                val world = location.world ?: continue

                if (plugin.configYml.getBool("autospawn.one-boss-per-world")) {
                    if (Bosses.getAllAlive().map { it.entity }.any { it.world == world }) {
                        continue
                    }
                }

                val spawnEvent = BossSpawnEvent(boss, location, BossSpawnEvent.SpawnReason.AUTOSPAWN, null)

                Bukkit.getPluginManager().callEvent(spawnEvent)

                if (!spawnEvent.isCancelled) {
                    boss.spawn(location)
                }
            }

            tick++
        }
    }
}
