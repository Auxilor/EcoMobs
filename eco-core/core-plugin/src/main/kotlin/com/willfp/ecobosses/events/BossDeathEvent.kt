package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.LivingEcoBoss
import com.willfp.ecobosses.lifecycle.BossLifecycle
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDeathEvent

abstract class BossDeathEvent(
    val boss: LivingEcoBoss
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