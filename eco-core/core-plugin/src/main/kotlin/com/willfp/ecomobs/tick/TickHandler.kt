package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob

/**
 * Handle ticking mobs.
 */
interface TickHandler {
    /**
     * Tick the mob.
     */
    fun tick(mob: LivingMob, tick: Int)

    /**
     * Called when the mob is removed.
     */
    fun onRemove(mob: LivingMob, tick: Int) {
        // Override when needed
    }
}
