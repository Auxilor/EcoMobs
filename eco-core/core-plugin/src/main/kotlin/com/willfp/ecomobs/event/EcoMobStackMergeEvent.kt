package com.willfp.ecomobs.event

import org.bukkit.entity.Mob
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a mob is about to be absorbed into a nearby stack. Cancelling leaves both
 * mobs alone.
 */
class EcoMobStackMergeEvent(
    /**
     * The mob being absorbed, which is removed once the merge goes through.
     */
    override val entity: Mob,
    /**
     * The mob it merges into.
     */
    val target: Mob,
    /**
     * The size the target ends up at.
     */
    val size: Int
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
