package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.event.EcoMobDeathEvent
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent

object MobEventDeath : MobEvent("death") {
    // Highest priority to let player run first
    @EventHandler(priority = EventPriority.HIGHEST)
    fun handle(event: EntityDeathEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        val data = TriggerData(
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        // Clear default drops
        event.drops.clear()

        val player = getArbitraryPlayer() ?: return

        Bukkit.getPluginManager().callEvent(EcoMobDeathEvent(living))

        ecoMob.handleEvent(this, data.dispatch(player))
        living.kill(null)
    }
}
