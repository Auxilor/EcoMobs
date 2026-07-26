package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderHitsPercent : MobPlaceholder("hits_percent") {
    override fun getValue(mob: LivingMob): String {
        val hits = mob.mob.hits

        if (!mob.mob.usesHits || hits < 1) {
            return "100"
        }

        return (mob.hitsRemaining / hits * 100).toNiceString()
    }
}
