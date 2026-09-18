package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a mob has run out its lifespan and is about to despawn, which is what
 * separates it from the despawns [EcoMobDespawnEvent] covers.
 *
 * Cancelling keeps the mob alive for now. Its lifespan is still spent, so the event is
 * called again on the next tick unless something else steps in.
 */
class EcoMobLifespanExpireEvent(
    override val mob: LivingMob
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
