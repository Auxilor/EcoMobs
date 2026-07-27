package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import com.willfp.ecomobs.mob.stage.DamageStageMode

object MobPlaceholderHitsPercent : MobPlaceholder("hits_percent") {
    override fun getValue(mob: LivingMob): String {
        val stage = mob.damageStage ?: return "100"

        if (stage.mode != DamageStageMode.HITS) {
            return "100"
        }

        return (mob.hitsRemaining / stage.amount * 100).toNiceString()
    }
}
