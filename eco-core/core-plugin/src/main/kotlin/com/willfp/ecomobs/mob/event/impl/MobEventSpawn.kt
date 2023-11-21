package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.EventHandler

object MobEventSpawn: MobEvent("spawn") {
    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val living = event.mob

        val data = TriggerData(
            victim = living.entity,
            location = living.entity.location,
            event = event
        )

        living.handleEvent(this, data.dispatch(living.entity.toDispatcher()))
    }
}
