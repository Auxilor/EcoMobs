package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import wiki.ecohub.core.stdlib.toNiceString

object MobPlaceholderStagePercent : MobPlaceholder("stage_percent") {
    override fun getValue(mob: LivingMob): String {
        return (mob.damageStageProgress * 100).toNiceString()
    }
}
