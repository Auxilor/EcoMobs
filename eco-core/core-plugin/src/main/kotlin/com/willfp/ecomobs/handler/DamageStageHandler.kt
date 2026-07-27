package com.willfp.ecomobs.handler

import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.mob.stage.DamageStageMode
import com.willfp.ecomobs.plugin
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier

@Suppress("DEPRECATION")
private val reductionModifiers = listOf(
    DamageModifier.ARMOR,
    DamageModifier.RESISTANCE,
    DamageModifier.MAGIC,
    DamageModifier.ABSORPTION,
    DamageModifier.BLOCKING
)

internal fun EntityDamageEvent.attributedPlayer(): Player? =
    (this as? EntityDamageByEntityEvent)?.damager?.tryAsPlayer()

object DamageStageHandler : Listener {
    // HIGH, so that finalDamage already includes the plugin's own damage modifiers,
    // vanilla armour, and other plugins' edits.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: EntityDamageEvent) {
        val bukkitMob = event.entity as? Mob ?: return
        val ecoMob = bukkitMob.ecoMob ?: return
        val living = ecoMob.getLivingMob(bukkitMob) as? LivingMobImpl ?: return
        val tracker = living.stageTracker ?: return

        val player = event.attributedPlayer()

        val cost = when (tracker.stage.mode) {
            DamageStageMode.HEALTH -> event.finalDamage
            DamageStageMode.HITS -> if (!tracker.stage.playerOnly || player != null) 1.0 else 0.0
        }

        if (cost <= 0.0) {
            event.isCancelled = true
            return
        }

        if (player != null) {
            plugin.topDamagerHandler.credit(bukkitMob, player, cost)
        }

        if (tracker.consume(cost, player)) {
            // Zero the reductions so exactly the remaining health lands. Never use overkill
            // damage here - other plugins read this value.
            @Suppress("DEPRECATION")
            for (reduction in reductionModifiers) {
                if (event.isApplicable(reduction)) {
                    event.setDamage(reduction, 0.0)
                }
            }

            event.damage = bukkitMob.health
        } else {
            // Not cancelled: the hurt animation, knockback, and vanilla invulnerability
            // ticks all still apply on a zero-damage event.
            event.damage = 0.0
        }
    }
}
