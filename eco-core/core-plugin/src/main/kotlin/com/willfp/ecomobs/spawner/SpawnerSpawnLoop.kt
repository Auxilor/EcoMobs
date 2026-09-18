package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.ecomobs.folia.atRegion
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
        // Spawning reads the spawner's block state and puts mobs into the world, so each
        // chunk's spawners are ticked on the region that owns them.
        PlacedSpawners.forEachChunk { world, chunkX, chunkZ, spawners ->
            atRegion(world, chunkX, chunkZ) {
                for (spawner in spawners) {
                    if (!spawner.location.isChunkLoaded) {
                        continue
                    }

                    spawner.tickSpawning(elapsed)
                }
            }
        }
    }
}
