package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob

interface MobEvent {
    val mob: LivingMob
}