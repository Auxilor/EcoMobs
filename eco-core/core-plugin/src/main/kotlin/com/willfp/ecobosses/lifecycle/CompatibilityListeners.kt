package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.SlimeSplitEvent

class CompatibilityListeners : Listener {
    @EventHandler
    fun handle(event: SlimeSplitEvent) {
        if (Bosses[event.entity as? LivingEntity ?: return] != null) {
            event.isCancelled = true
        }
    }
}
