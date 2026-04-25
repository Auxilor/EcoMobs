package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoWrappedTask
import com.willfp.ecomobs.plugin

object SpawnerDisplay {
    @Volatile
    private var tick = 0
    private var asyncTask: EcoWrappedTask? = null

    fun start() {
        asyncTask?.cancelTask()
        asyncTask = plugin.scheduler.runTaskAsyncTimer(1, 1) { tickAsync() }
    }

    private fun tickAsync() {
        for (spawner in PlacedSpawners.values()) {
            plugin.scheduler.runTask(spawner.location) {
                if (spawner.location.isChunkLoaded) {
                    spawner.tickAsync(tick)
                }
            }
        }
        tick++
    }
}
