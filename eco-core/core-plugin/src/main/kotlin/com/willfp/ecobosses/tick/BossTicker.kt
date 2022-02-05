package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss

interface BossTicker {
    fun tick(
        boss: LivingEcoBoss,
        tick: Int
    ) {
        // Can be overridden when needed.
    }

    fun onDeath(
        boss: LivingEcoBoss,
        tick: Int
    ) {
        // Can be overridden when needed.
    }
}
