package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called for every entity a spawner is about to spawn, vanilla spawners included.
 *
 * With mob stacking off, that is once per mob. With it on, a cycle goes in as one
 * stacked entity, so this is called once for it, and not at all when the cycle only
 * grows a stack that is already there.
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
