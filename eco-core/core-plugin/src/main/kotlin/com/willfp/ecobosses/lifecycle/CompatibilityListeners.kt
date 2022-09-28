package com.willfp.ecobosses.lifecycle

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTransformEvent
import org.bukkit.event.entity.SlimeSplitEvent

class CompatibilityListeners : Listener {
    @EventHandler
    fun handle(event: SlimeSplitEvent) {
        if (!this.isBoss(event.entity)) return
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityTransformEvent) {
        if (!this.isBoss(event.entity)) return
        event.isCancelled = true
    }

    private fun isBoss(entity: Entity): Boolean {
        return Bosses[entity as? LivingEntity ?: return false] != null
    }
}
