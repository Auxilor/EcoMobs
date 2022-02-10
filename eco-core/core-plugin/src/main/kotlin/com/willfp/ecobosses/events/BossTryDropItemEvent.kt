package com.willfp.ecobosses.events

import com.willfp.ecobosses.bosses.EcoBoss
import com.willfp.ecobosses.bosses.LivingEcoBoss
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class BossTryDropItemEvent(
    val boss: EcoBoss,
    val location: Location,
    var items: MutableCollection<ItemStack>,
    var chance: Double,
    val player: Player?
): Event() {
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
