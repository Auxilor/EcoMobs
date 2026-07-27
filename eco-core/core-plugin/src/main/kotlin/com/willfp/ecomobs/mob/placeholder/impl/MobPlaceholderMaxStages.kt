package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderMaxStages : MobPlaceholder("max_stages") {
    override fun getValue(mob: LivingMob): String {
        return mob.mob.damageStages.size.toString()
    }
}
