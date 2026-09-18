package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit

private const val LOOK_AT_RATE = 5

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

        if (tick % LOOK_AT_RATE == 0) {
            tickLookAt()
        }

        tick++
    }

    /**
     * A look-at-only hologram is hidden from everyone, and shown to a player only while
     * they're aimed at the column it belongs to.
     */
    private fun tickLookAt() {
        if (!SpawnerStackSettings.enabled ||
            !SpawnerStackSettings.hologramEnabled ||
            !SpawnerStackSettings.lookAtOnly
        ) {
            return
        }

        for (player in Bukkit.getOnlinePlayers()) {
            // The ray trace touches the world around the player, so it's submitted to
            // their own context rather than run from the global one.
            plugin.scheduler.on(player).run {
                SpawnerHolograms.updateLookAt(player)
            }
        }
    }
}
