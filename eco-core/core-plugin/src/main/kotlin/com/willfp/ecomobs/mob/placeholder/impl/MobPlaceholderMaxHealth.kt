package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.game.entity.getAttributeValue

object MobPlaceholderMaxHealth : MobPlaceholder("max_health") {
    override fun getValue(mob: LivingMob): String {
        return mob.entity.getAttributeValue("minecraft:max_health").toNiceString()
    }
}
