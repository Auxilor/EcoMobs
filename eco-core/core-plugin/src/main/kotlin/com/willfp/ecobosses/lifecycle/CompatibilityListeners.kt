package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.bosses.Bosses.isBoss
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTransformEvent
import org.bukkit.event.entity.SlimeSplitEvent

class CompatibilityListeners : Listener {
    @EventHandler
    fun handle(event: SlimeSplitEvent) {
        if (!event.entity.isBoss) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityTransformEvent) {
        if (!event.entity.isBoss) {
            return
        }
        event.isCancelled = true
    }
}
