package com.willfp.ecomobs.mob.event.impl

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.TriggerData

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

        living.handleEvent(this, data.dispatch(bukkitMob.toDispatcher()))
    }
}
