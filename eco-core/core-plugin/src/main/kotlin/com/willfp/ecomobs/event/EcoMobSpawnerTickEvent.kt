package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a spawner ticked by EcoMobs is about to run a spawn cycle, after its
 * nearby-mob cap has been checked.
 *
 * Only fired in [com.willfp.ecomobs.spawner.SpawnerMode.ECOMOBS]; the server runs the
 * cycle itself in the vanilla mode.
 */
class EcoMobSpawnerTickEvent(
    override val location: Location,
    override val mobId: String?,
    /**
     * How many mobs the spawner spawns per cycle.
     */
    var spawnCount: Int,
    /**
     * How many spawners the stack holds, each of which spawns [spawnCount] mobs. One
     * when spawner stacking is off.
     */
    var stackSize: Int
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
