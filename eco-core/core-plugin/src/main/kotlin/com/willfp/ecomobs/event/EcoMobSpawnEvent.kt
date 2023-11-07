package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EcoMobSpawnEvent(
    val mob: LivingMob,
    val reason: SpawnReason,
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
