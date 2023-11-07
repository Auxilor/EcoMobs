package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderHealth : MobPlaceholder("health") {
    override fun getValue(mob: LivingMob): String {
        return mob.entity.health.toNiceString()
    }
}
