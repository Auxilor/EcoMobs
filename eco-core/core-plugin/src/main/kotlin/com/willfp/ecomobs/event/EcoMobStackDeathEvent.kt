package com.willfp.ecomobs.event

import org.bukkit.entity.Mob
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a stacked mob dies, before the stack is either paid out in full or split.
 */
class EcoMobStackDeathEvent(
    override val entity: Mob,
    /**
     * The size of the stack that died.
     */
    val size: Int,
    /**
     * Whether the whole stack dies at once, paying out every mob's rewards. When false,
     * only the one mob dies and the rest come back. Starts as the configured setting.
     */
    var killWholeStack: Boolean
) : Event(), MobStackEvent {
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
