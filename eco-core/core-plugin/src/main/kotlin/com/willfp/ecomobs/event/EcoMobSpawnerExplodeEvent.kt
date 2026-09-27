package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called for every custom spawner caught in an explosion, whether or not it is
 * explosion proof. Set [isProtected] to decide whether it survives.
 */
class EcoMobSpawnerExplodeEvent(
    override val location: Location,
    override val mobId: String?,
    val stackSize: Int,
    /**
     * Whether the spawner survives the explosion. Starts as the spawner's own
     * explosion-proof setting.
     */
    var isProtected: Boolean
) : Event(), SpawnerEvent {
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
