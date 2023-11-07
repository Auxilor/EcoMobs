package com.willfp.ecomobs.mob.placeholder

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class MobPlaceholder(
    final override val id: String
) : KRegistrable, Listener {
    /**
     * Get the value of the placeholder.
     */
    abstract fun getValue(mob: LivingMob): String
}
