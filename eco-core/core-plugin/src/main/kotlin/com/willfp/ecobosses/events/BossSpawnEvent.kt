package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.EcoBoss
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class BossSpawnEvent(
    val boss: EcoBoss,
    val location: Location,
    val reason: SpawnReason
) : Event(), Cancellable {
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

    enum class SpawnReason {
        TOTEM,
        EGG,
        COMMAND,
        UNKNOWN
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}