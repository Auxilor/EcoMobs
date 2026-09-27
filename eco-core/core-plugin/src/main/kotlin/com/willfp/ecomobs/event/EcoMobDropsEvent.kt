package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.EcoMob
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

/**
 * Called once the mob's drop table has been rolled, before anything is given out.
 * Cancelling drops nothing at all.
 *
 * Fired once per mob of a stack, as each one rolls the table separately.
 */
class EcoMobDropsEvent(
    val ecoMob: EcoMob,
    val location: Location,
    /**
     * The player the drops go to, or null if they fall on the ground.
     */
    val player: Player?,
    /**
     * The items rolled, which can be edited in place.
     */
    val drops: MutableList<ItemStack>,
    var experience: Int
) : Event(), Cancellable {
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
