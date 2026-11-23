package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import wiki.ecohub.core.stdlib.toNiceString

object MobPlaceholderHealth : MobPlaceholder("health") {
    override fun getValue(mob: LivingMob): String {
        return mob.entity.health.toNiceString()
    }
}
