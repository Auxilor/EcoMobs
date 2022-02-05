package com.willfp.ecobosses.defence

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class ImmunitiesHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityDamageEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity]?.boss ?: return

        when (event.cause) {
            EntityDamageEvent.DamageCause.SUFFOCATION -> {
                if (boss.isImmuneToSuffocation) {
                    event.isCancelled = true
                }
            }

            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> {
                if (boss.isImmuneToExplosions) {
                    event.isCancelled = true
                }
            }

            EntityDamageEvent.DamageCause.HOT_FLOOR, EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA -> {
                if (boss.isImmuneToFire) {
                    event.isCancelled = true
                }
            }

            EntityDamageEvent.DamageCause.DROWNING -> {
                if (boss.isImmuneToDrowning) {
                    event.isCancelled = true
                }
            }

            else -> {}
        }
    }
}
