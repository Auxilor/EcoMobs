package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTransformEvent
import org.bukkit.event.entity.SlimeSplitEvent

class VanillaCompatibilityHandlers : Listener {
    @EventHandler
    fun handle(event: SlimeSplitEvent) {
        if (event.entity.ecoMob != null) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handle(event: EntityTransformEvent) {
        val bukkitEntity = event.entity as? Mob ?: return
        if (bukkitEntity.ecoMob != null) {
            event.isCancelled = true
        }
    }
}
