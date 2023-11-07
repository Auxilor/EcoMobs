package com.willfp.ecomobs.mob.event.impl

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.event.EcoMobKillEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

object MobEventKill : MobEvent("kill") {
    @EventHandler
    fun handle(event: EntityDeathByEntityEvent) {
        val player = event.killer.tryAsPlayer() ?: return
        val bukkitMob = event.victim as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        Bukkit.getPluginManager().callEvent(EcoMobKillEvent(living, player))

        ecoMob.handleEvent(this, data.dispatch(player))
        living.kill(player)
    }
}
