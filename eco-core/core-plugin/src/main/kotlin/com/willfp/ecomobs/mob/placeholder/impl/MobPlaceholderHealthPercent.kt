package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.game.entity.getAttributeValue

object MobPlaceholderHealthPercent : MobPlaceholder("health_percent") {
    override fun getValue(mob: LivingMob): String {
        val health = mob.entity.health
        val maxHealth = mob.entity.getAttributeValue("minecraft:max_health") ?: return "0"

        return (health / maxHealth * 100).toNiceString()
    }
}
