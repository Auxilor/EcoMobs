package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob

class TickHandlerLifespan: TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (mob.ticksLeft <= 0) {
            mob.despawn()
        }
    }
}
