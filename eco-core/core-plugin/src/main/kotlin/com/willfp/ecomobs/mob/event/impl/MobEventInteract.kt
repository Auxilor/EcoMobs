package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.TriggerData

object MobEventInteract: MobEvent("interact") {
    @EventHandler
    fun handle(event: PlayerInteractEntityEvent) {
        val bukkitMob = event.rightClicked as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return
        val player = event.player

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        living.handleEvent(this, data.dispatch(player.toDispatcher()))
    }
}
