package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.LivingEcoBoss
import com.willfp.ecobosses.lifecycle.BossLifecycle
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

    override val lifecycle: BossLifecycle = BossLifecycle.KILL

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}