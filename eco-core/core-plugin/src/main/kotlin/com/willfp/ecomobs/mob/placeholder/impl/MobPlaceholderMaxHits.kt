package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderMaxHits : MobPlaceholder("max_hits") {
    override fun getValue(mob: LivingMob): String {
        return if (mob.mob.usesHits) mob.mob.hits.toString() else "0"
    }
}
