package com.willfp.ecomobs.mob

import org.bukkit.entity.Mob
import org.bukkit.entity.Player

interface LivingMob {
    val mob: EcoMob

    val entity: Mob

    val isAlive: Boolean

    val displayName: String

    /**
     * Ticks left until removed due to lifespan.
     */
    val ticksLeft: Int

    /**
     * Kill the mob.
     */
    fun kill(player: Player?)

    /**
     * Despawn the mob.
     */
    fun despawn()
}
