package com.willfp.ecomobs.mob.placeholder

import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.event.Listener
import wiki.ecohub.core.registry.KRegistrable

abstract class MobPlaceholder(
    final override val id: String
) : KRegistrable, Listener {
    /**
     * Get the value of the placeholder.
     */
    abstract fun getValue(mob: LivingMob): String
}
