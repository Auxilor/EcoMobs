package com.willfp.ecomobs.event

import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when spawners are about to be added to a stack, whether by right-clicking it
 * or by placing against it.
 */
class EcoMobSpawnerStackEvent(
    override val location: Location,
    override val mobId: String?,
    /**
     * The stack size before the merge.
     */
    val currentSize: Int,
    /**
     * How many spawners are added. Already capped to the room left in the stack;
     * lowering it takes fewer, and raising it is capped again afterwards.
     */
    var amount: Int
) : Event(), SpawnerEvent, Cancellable {
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
