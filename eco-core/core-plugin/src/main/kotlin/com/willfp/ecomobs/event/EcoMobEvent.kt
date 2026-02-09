package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob

interface EcoMobEvent {
    val mob: LivingMob
}