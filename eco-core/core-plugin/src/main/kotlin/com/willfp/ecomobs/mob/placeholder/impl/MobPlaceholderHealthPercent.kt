package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import org.bukkit.attribute.Attribute

object MobPlaceholderHealthPercent : MobPlaceholder("health_percent") {
    override fun getValue(mob: LivingMob): String {
        val health = mob.entity.health
        val maxHealth = mob.entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: return "0"

        return (health / maxHealth * 100).toNiceString()
    }
}
