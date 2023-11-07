package com.willfp.ecomobs.mob.event.impl

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object MobEventKillPlayer : MobEvent("kill_player") {
    @EventHandler
    fun handle(event: EntityDeathByEntityEvent) {
        val player = event.victim as? Player ?: return
        val bukkitMob = event.killer as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        ecoMob.handleEvent(this, data.dispatch(player))
    }
}
