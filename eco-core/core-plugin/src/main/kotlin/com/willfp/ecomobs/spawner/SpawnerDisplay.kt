package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.plugin
import org.bukkit.scheduler.BukkitTask

object SpawnerDisplay {
    @Volatile
    private var tick = 0
    private var asyncTask: BukkitTask? = null

    fun start() {
        asyncTask?.cancel()
        asyncTask = plugin.scheduler.runAsyncTimer(1, 1) { tickAsync() }
    }

    private fun tickAsync() {
        for (spawner in PlacedSpawners.values()) {
            plugin.scheduler.run {
                if (spawner.location.isChunkLoaded) {
                    spawner.tickAsync(tick)
                }
            }
        }
        tick++
    }
}
