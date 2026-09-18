package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.EcoMob
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a spawn egg is about to spawn its mob, before the egg's conditions are
 * tested. Cancelling stops the spawn and leaves the egg in the inventory.
 */
class EcoMobEggUseEvent(
    val mob: EcoMob,
    val location: Location,
    val player: Player?,
    val source: Source
) : Event(), Cancellable {
    private var isCancelled: Boolean = false

    /**
     * What used the egg.
     */
    enum class Source {
        /**
         * A player right-clicked a block with the egg.
         */
        PLAYER,

        /**
         * A dispenser or dropper fired the egg.
         */
        DISPENSER
    }

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
