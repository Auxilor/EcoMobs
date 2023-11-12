package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import org.bukkit.attribute.Attribute

object MobPlaceholderMaxHealth : MobPlaceholder("max_health") {
    override fun getValue(mob: LivingMob): String {
        return mob.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value.toNiceString()
    }
}
