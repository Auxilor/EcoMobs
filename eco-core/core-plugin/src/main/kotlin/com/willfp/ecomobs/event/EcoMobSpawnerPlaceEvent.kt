package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Called when a player places a custom spawner, before its settings are written to the
 * placed block. Cancelling also cancels the block placement.
 */
class EcoMobSpawnerPlaceEvent(
    player: Player,
    override val location: Location,
    override val mobId: String?,
    val stackSize: Int
) : PlayerEvent(player), SpawnerEvent, Cancellable {
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
