package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.ecomobs.plugin

/**
 * Ticks spawners in place of the server, when the mode is [SpawnerMode.ECOMOBS].
 */
object SpawnerSpawnLoop {
    private var task: EcoTask? = null

    fun start() {
        stop()

        if (SpawnerSettings.mode != SpawnerMode.ECOMOBS) {
            return
        }

        val rate = SpawnerSettings.tickRate

        task = plugin.scheduler.runTimer(rate, rate) {
            tickSpawners(rate.toInt())
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tickSpawners(elapsed: Int) {
        for (spawner in PlacedSpawners.values()) {
            if (!spawner.location.isWorldLoaded || !spawner.location.isChunkLoaded) {
                continue
            }

            spawner.tickSpawning(elapsed)
        }
    }
}
