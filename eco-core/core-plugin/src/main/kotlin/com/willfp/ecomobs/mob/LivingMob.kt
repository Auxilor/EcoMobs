package com.willfp.ecomobs.mob

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.libreforge.triggers.DispatchedTrigger
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
     * Hits remaining before the mob dies. Zero if the mob does not use hits.
     */
    val hitsRemaining: Double

    /**
     * Handle an event.
     */
    fun handleEvent(event: MobEvent, trigger: DispatchedTrigger)

    /**
     * Kill the mob.
     */
    fun kill(player: Player?, removeTracking: Boolean = true)

    /**
     * Despawn the mob.
     */
    fun despawn()
}
