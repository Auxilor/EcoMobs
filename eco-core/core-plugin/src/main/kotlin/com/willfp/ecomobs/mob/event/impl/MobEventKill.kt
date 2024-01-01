package com.willfp.ecomobs.mob.event.impl

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.event.EcoMobKillEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.toDispatcher
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

        // Clear vanilla drops
        event.deathEvent.drops.clear()

        Bukkit.getPluginManager().callEvent(EcoMobKillEvent(living, player))

        living.handleEvent(this, data.dispatch(player.toDispatcher()))

        // Tracking isn't removed here because it's removed in MobEventDeath, which is called after this one.
        // Otherwise, player kills wouldn't fire the death event.
        living.kill(
            player,
            removeTracking = false
        )
    }
}
