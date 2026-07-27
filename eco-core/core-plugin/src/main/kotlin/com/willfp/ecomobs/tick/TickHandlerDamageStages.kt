package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import org.bukkit.attribute.Attribute

class TickHandlerDamageStages : TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 2 != 0) {
            return
        }

        val tracker = (mob as? LivingMobImpl)?.stageTracker ?: return
        val entity = mob.entity

        if (!entity.isValid) {
            return
        }

        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: return

        if (tracker.isFinished) {
            // Safety net for a lethal hit that was absorbed anyway, e.g. Resistance V.
            entity.health = 0.0
            return
        }

        // The 0.01 floor stops floating-point drift from killing the mob a hit early; the
        // ceiling stops setHealth throwing if progress ever goes negative.
        entity.health = (maxHealth * (1 - tracker.progress)).coerceIn(0.01, maxHealth)
    }
}
