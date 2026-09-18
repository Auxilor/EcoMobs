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

        task = plugin.scheduler.global().runTimer(rate, rate) {
            tickSpawners(rate.toInt())
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tickSpawners(elapsed: Int) {
        for (spawner in PlacedSpawners.values()) {
            val location = spawner.location

            if (!location.isWorldLoaded || !location.isChunkLoaded) {
                continue
            }

            // Reading the block state and spawning mobs both belong to the spawner's
            // own region, so the cycle is submitted there rather than run globally.
            plugin.scheduler.at(location).run {
                spawner.tickSpawning(elapsed)
            }
        }
    }
}
