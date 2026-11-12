package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import org.bukkit.attribute.Attribute
import wiki.ecohub.core.stdlib.toNiceString

object MobPlaceholderMaxHealth : MobPlaceholder("max_health") {
    override fun getValue(mob: LivingMob): String {
        return mob.entity.getAttribute(Attribute.MAX_HEALTH)?.value.toNiceString()
    }
}
