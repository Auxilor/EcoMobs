package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called for every mob a spawner is about to spawn, in both spawner modes and once per
 * mob in a stacked spawner's cycle.
 */
class EcoMobSpawnerSpawnEvent(
    override val location: Location,
    override val mobId: String?,
    /**
     * Where the mob appears, which is offset from the spawner itself.
     */
    val spawnLocation: Location
) : Event(), SpawnerEvent, Cancellable {
    private var isCancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        isCancelled = cancelled
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}
