package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.LivingEcoBoss
import org.bukkit.event.HandlerList

class BossDespawnEvent(
    boss: LivingEcoBoss,
) : BossDeathEvent(boss) {
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