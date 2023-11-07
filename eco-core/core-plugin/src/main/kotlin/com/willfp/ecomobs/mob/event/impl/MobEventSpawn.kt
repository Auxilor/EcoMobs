package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object MobEventSpawn: MobEvent("spawn") {
    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob

        val data = TriggerData(
            victim = mob.entity,
            location = mob.entity.location,
            event = event
        )

        val player = getArbitraryPlayer() ?: return

        mob.mob.handleEvent(this, data.dispatch(player))
    }
}
