package com.willfp.ecomobs.handler

import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier

/**
 * Modifiers that reduce incoming damage. Zeroed on a killing hit so that exactly the
 * mob's remaining health lands, rather than inflating the reported damage with overkill.
 */
@Suppress("DEPRECATION")
private val reductionModifiers = listOf(
    DamageModifier.ARMOR,
    DamageModifier.RESISTANCE,
    DamageModifier.MAGIC,
    DamageModifier.ABSORPTION,
    DamageModifier.BLOCKING
)

/**
 * The player behind this damage, resolving projectile shooters and TNT owners.
 */
internal fun EntityDamageEvent.attributedPlayer(): Player? =
    (this as? EntityDamageByEntityEvent)?.damager?.tryAsPlayer()

/**
 * The hit cost of this event for [mob]: the explicitly configured modifier if there is one,
 * otherwise one hit for player damage and no hits for anything else.
 */
internal fun EntityDamageEvent.hitCost(mob: EcoMob): Double =
    mob.getConfiguredDamageModifier(cause)
        ?: if (attributedPlayer() != null) 1.0 else 0.0

object DamageModifierHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return

        if (!ecoMob.usesHits) {
            event.damage *= ecoMob.getDamageModifier(event.cause)
            return
        }

        val living = ecoMob.getLivingMob(bukkitMob) as? LivingMobImpl ?: return
        val cost = event.hitCost(ecoMob)

        if (cost <= 0.0) {
            event.isCancelled = true
            return
        }

        if (living.consumeHits(cost)) {
            // Zero the reductions so exactly the remaining health lands. Never use
            // overkill damage here - other plugins read this value.
            @Suppress("DEPRECATION")
            for (reduction in reductionModifiers) {
                if (event.isApplicable(reduction)) {
                    event.setDamage(reduction, 0.0)
                }
            }

            event.damage = bukkitMob.health
        } else {
            // Deliberately not cancelled: the hurt animation, knockback, and vanilla
            // invulnerability ticks all still apply on a zero-damage event.
            event.damage = 0.0
        }
    }
}
