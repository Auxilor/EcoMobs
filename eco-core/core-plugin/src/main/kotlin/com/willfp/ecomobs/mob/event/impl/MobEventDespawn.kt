package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.event.MobEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.TriggerData

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
