package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.EventHandler

object MobEventDespawn: MobEvent("despawn") {
    @EventHandler
    fun handle(event: EcoMobDespawnEvent) {
        val living = event.mob

        val data = TriggerData(
            victim = living.entity,
            location = living.entity.location,
            event = event
        )

        living.handleEvent(this, data.dispatch(living.entity.toDispatcher()))
    }
}
