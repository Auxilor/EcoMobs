package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Called when a player breaks a stacked spawner and takes part of the stack, leaving
 * the block in place. Breaking the last one, or the whole stack at once, fires
 * [EcoMobSpawnerBreakEvent] instead.
 *
 * Cancelling leaves the stack untouched and cancels the block break.
 */
class EcoMobSpawnerUnstackEvent(
    player: Player,
    override val location: Location,
    override val mobId: String?,
    /**
     * The stack size before the break.
     */
    val currentSize: Int,
    /**
     * How many spawners are taken off the stack.
     */
    var amount: Int,
    /**
     * Whether the spawners taken off drop as an item. Starts as what the spawner's
     * pickup setting and the player's permissions allow.
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
