package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.LivingEcoBoss
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDeathEvent

class BossKillEvent(
    boss: LivingEcoBoss,
    val killer: Player?,
    val event: EntityDeathEvent
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