package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

/**
 * Called when an EcoMob takes damage, after the mob's own damage modifiers have been
 * applied and before its damage stages see the hit.
 *
 * Cancelling cancels the underlying [org.bukkit.event.entity.EntityDamageEvent].
 */
class EcoMobDamageEvent(
    override val mob: LivingMob,
    /**
     * The player who dealt the damage, or null if it came from elsewhere.
     */
    val player: Player?,
    val cause: DamageCause,
    /**
     * The damage dealt. Writing to it writes back to the bukkit event.
     */
    var damage: Double
) : Event(), MobEvent, Cancellable {
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
