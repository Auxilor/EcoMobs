package com.willfp.ecobosses.defence

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageMultiplierHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity]?.boss ?: return

        if (event.damager is Player) {
            event.damage *= boss.meleeDamageMultiplier

            if (boss.meleeDamageMultiplier == 0.0) {
                event.isCancelled = true
            }
        }

        if (event.damager is Projectile) {
            event.damage *= boss.projectileDamageMultiplier

            if (boss.projectileDamageMultiplier == 0.0) {
                event.isCancelled = true
            }
        }
    }
}
