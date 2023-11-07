package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder

object MobPlaceholderTime : MobPlaceholder("time") {
    override fun getValue(mob: LivingMob): String {
        val ticksLeft = mob.ticksLeft
        val secondsLeft = ticksLeft / 20

        return String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60)
    }
}
