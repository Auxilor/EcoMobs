package com.willfp.ecobosses.spawn

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecobosses.bosses.Bosses

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

                if (Bosses.getAllAlive().mapNotNull { it.entity }.map { it.world } == world) {
                    continue
                }

                boss.spawn(location)
            }

            tick++
        }
    }
}
