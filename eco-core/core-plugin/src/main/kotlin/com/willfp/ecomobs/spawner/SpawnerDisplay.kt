package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.ecomobs.plugin

object SpawnerDisplay {
    private var tick = 0
    private var task: EcoTask? = null

    fun start() {
        task?.cancel()
        tick = 0
        task = plugin.scheduler.global().runTimer(1, 1) { tickSpawners() }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tickSpawners() {
        for (spawner in PlacedSpawners.values()) {
            if (!spawner.location.isWorldLoaded || !spawner.location.isChunkLoaded) {
                continue
            }

            spawner.tick(tick)
        }

        tick++
    }
}
