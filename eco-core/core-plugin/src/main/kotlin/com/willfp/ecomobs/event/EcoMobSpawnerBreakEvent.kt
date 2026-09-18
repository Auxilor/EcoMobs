package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Called when a player breaks a custom spawner and the whole thing comes up, after the
 * pickup rules have been applied. A stacked spawner that only loses one of its spawners
 * fires [EcoMobSpawnerUnstackEvent] instead.
 *
 * Cancelling also cancels the block break.
 */
class EcoMobSpawnerBreakEvent(
    player: Player,
    override val location: Location,
    override val mobId: String?,
    val stackSize: Int,
    /**
     * Whether the spawner drops as an item. Starts as what the spawner's pickup setting
     * and the player's permissions allow.
     */
    var dropsItem: Boolean
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
