package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EcoMobPreSpawnEvent(
    val mob: EcoMob,
    val reason: SpawnReason,
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
