package com.willfp.ecobosses.defence

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class PickupHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityPickupItemEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity]?.boss ?: return

        event.isCancelled = true
    }
}
