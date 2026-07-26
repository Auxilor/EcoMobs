package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.attribute.Attribute

/**
 * Mirrors the mob's vanilla health onto its remaining hits.
 *
 * This is what makes the hit counter authoritative: armour reduction, Regeneration,
 * /heal, and other plugins writing to health are all overwritten before they can buy
 * an extra hit. It also keeps the boss bar and health placeholders proportional
 * without those needing to know about hits at all.
 */
class TickHandlerHitsHealth : TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 2 != 0) {
            return
        }

        val entity = mob.entity

        if (!entity.isValid) {
            return
        }

        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: return
        val hits = mob.mob.hits

        if (hits < 1) {
            return
        }

        if (mob.hitsRemaining <= 0.0) {
            // Safety net for a killing hit that was absorbed anyway, e.g. Resistance V.
            entity.health = 0.0
            return
        }

        entity.health = (maxHealth * mob.hitsRemaining / hits).coerceIn(0.01, maxHealth)
    }
}
