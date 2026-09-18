package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobDamageEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object DamageModifierHandler : Listener {
    @EventHandler
    fun handle(event: EntityDamageEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return

        val multiplier = ecoMob.getDamageModifier(event.cause)

        event.damage *= multiplier

        // Called here rather than from its own listener so it lands after the mob's own
        // modifiers and before DamageStageHandler, which runs at HIGH.
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        val damageEvent = EcoMobDamageEvent(living, event.attributedPlayer(), event.cause, event.damage)
        Bukkit.getPluginManager().callEvent(damageEvent)

        if (damageEvent.isCancelled) {
            event.isCancelled = true
            return
        }

        event.damage = damageEvent.damage
    }
}
