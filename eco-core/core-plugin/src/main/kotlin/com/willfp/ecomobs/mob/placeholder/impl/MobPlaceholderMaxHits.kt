package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import com.willfp.ecomobs.mob.stage.DamageStageMode

object MobPlaceholderMaxHits : MobPlaceholder("max_hits") {
    override fun getValue(mob: LivingMob): String {
        val stage = mob.damageStage ?: return "0"

        if (stage.mode != DamageStageMode.HITS) {
            return "0"
        }

        return stage.amount.toNiceString()
    }
}
