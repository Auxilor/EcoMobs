package com.willfp.ecomobs.spawner

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.ecomobs.folia.atRegion
import com.willfp.ecomobs.folia.onEntity
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
        val currentTick = tick

        // Particles touch the world, so each chunk's spawners are ticked on the region
        // that owns them rather than from the global one.
        PlacedSpawners.forEachChunk { world, chunkX, chunkZ, spawners ->
            atRegion(world, chunkX, chunkZ) {
                for (spawner in spawners) {
                    if (!spawner.location.isChunkLoaded) {
                        continue
                    }

                    spawner.tick(currentTick)
                }
            }
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
            // The ray trace touches the world around the player, so it's run on their
            // own region rather than from the global one.
            onEntity(player) {
                SpawnerHolograms.updateLookAt(player)
            }
        }
    }
}
