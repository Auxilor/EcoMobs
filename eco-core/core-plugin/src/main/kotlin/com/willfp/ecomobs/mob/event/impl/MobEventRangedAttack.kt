package com.willfp.ecomobs.mob.event.impl

import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object MobEventRangedAttack : MobEvent("ranged_attack") {
    @EventHandler
    fun handle(event: EntityDamageByEntityEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) ?: return

        // If the damager is a player, it's a melee attack
        if (event.damager is Player) {
            return
        }

        val player = event.damager.tryAsPlayer() ?: return

        val data = TriggerData(
            player = player,
            victim = bukkitMob,
            location = bukkitMob.location,
            event = event
        )

        living.handleEvent(this, data.dispatch(player))
        living.handleEvent(MobEventAnyAttack, data.dispatch(player))
    }
}
