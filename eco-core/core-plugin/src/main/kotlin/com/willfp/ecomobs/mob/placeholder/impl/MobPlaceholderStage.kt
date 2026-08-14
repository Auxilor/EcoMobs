package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderStage : MobPlaceholder("stage") {
    override fun getValue(mob: LivingMob): String {
        return mob.damageStageNumber.toString()
    }
}
