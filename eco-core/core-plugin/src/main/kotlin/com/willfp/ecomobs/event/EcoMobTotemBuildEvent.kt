package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.SpawnTotem
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Called when a player completes a spawn totem and its conditions are met, before the
 * mob is spawned.
 */
class EcoMobTotemBuildEvent(
    val mob: EcoMob,
    val totem: SpawnTotem,
    val location: Location,
    player: Player
) : PlayerEvent(player), Cancellable {
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
