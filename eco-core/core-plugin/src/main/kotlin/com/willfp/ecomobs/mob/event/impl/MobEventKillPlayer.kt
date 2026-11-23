package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobKillPlayerEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import wiki.ecohub.core.event.EntityDeathByEntityEvent
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.TriggerData

object MobEventKillPlayer : MobEvent("kill_player") {
    @EventHandler
    fun handle(event: EntityDeathByEntityEvent) {
        val player = event.victim as? Player ?: return
        val bukkitMob = event.killer as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        Bukkit.getPluginManager().callEvent(EcoMobKillPlayerEvent(living, player))

        living.handleEvent(this, data.dispatch(player.toDispatcher()))
    }
}
