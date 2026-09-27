package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when one mob of a stack has died and the rest are about to come back as a
 * fresh mob. Cancelling drops the remainder entirely.
 */
class EcoMobStackSplitEvent(
    /**
     * The mob that died.
     */
    override val entity: Mob,
    /**
     * Where the remainder respawns.
     */
    val location: Location,
    /**
     * How many mobs the respawned stack stands in for.
     */
    var remaining: Int
) : Event(), MobStackEvent, Cancellable {
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
