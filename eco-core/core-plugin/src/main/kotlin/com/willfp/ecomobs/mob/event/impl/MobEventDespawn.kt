package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.EventHandler

object MobEventDespawn: MobEvent("despawn") {
    @EventHandler
    fun handle(event: EcoMobDespawnEvent) {
        val ecoMob = event.mob

        val data = TriggerData(
            victim = ecoMob.entity,
            location = ecoMob.entity.location,
            event = event
        )

        val player = getArbitraryPlayer() ?: return

        ecoMob.mob.handleEvent(this, data.dispatch(player))
    }
}
