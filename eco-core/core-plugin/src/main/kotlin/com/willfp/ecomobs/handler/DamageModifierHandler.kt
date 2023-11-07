package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DamageModifierHandler : Listener {
    @EventHandler
    fun handle(event: EntityDamageEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return

        val multiplier = ecoMob.getDamageModifier(event.cause)

        event.damage *= multiplier
    }
}
