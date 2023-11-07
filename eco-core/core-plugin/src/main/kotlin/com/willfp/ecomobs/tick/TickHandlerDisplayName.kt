package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob

class TickHandlerDisplayName: TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 5 != 0) {
            return
        }

        @Suppress("DEPRECATION")
        mob.entity.customName = mob.displayName
    }
}
