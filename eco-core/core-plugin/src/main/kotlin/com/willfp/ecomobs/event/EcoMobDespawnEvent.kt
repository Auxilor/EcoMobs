package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EcoMobDespawnEvent(
    val mob: LivingMob,
) : Event() {
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
