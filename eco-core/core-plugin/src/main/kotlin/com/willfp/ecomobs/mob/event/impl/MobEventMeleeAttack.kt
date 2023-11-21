package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object MobEventMeleeAttack : MobEvent("melee_attack") {
    @EventHandler
    fun handle(event: EntityDamageByEntityEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return
        val damager = event.damager

        val data = TriggerData(
            player = damager as? Player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        living.handleEvent(this, data.dispatch(damager.toDispatcher()))
        living.handleEvent(MobEventAnyAttack, data.dispatch(damager.toDispatcher()))
    }
}
