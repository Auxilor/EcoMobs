package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent

object MobEventInteract: MobEvent("interact") {
    @EventHandler
    fun handle(event: PlayerInteractEntityEvent) {
        val bukkitMob = event.rightClicked as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val player = event.player

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        ecoMob.handleEvent(this, data.dispatch(player))
    }
}
