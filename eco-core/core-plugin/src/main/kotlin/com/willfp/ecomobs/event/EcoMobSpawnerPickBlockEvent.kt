package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

/**
 * Called when a creative player middle-clicks a custom spawner, before the spawner item
 * is put in their hand.
 */
class EcoMobSpawnerPickBlockEvent(
    player: Player,
    override val location: Location,
    override val mobId: String?,
    val item: ItemStack
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
