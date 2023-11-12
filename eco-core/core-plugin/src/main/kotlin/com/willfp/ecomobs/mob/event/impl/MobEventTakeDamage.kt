package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object MobEventTakeDamage : MobEvent("take_damage") {
    @EventHandler
    fun handle(event: EntityDamageEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        val data = TriggerData(
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        val player = Bukkit.getOnlinePlayers().firstOrNull() ?: return

        living.handleEvent(this, data.dispatch(player))
    }
}
