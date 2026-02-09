package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class EcoMobKillEvent(
    override val mob: LivingMob,
    player: Player,
) : PlayerEvent(player), MobEvent {
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
