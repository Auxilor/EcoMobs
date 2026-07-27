package com.willfp.ecomobs.mob

import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.stage.DamageStage
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
     * The current damage stage, or null if the mob does not use stages.
     */
    val damageStage: DamageStage?

    /**
     * The 1-based position of the current damage stage. Zero if the mob does not use stages.
     */
    val damageStageNumber: Int

    /**
     * Progress through the current damage stage, from 0 to 1.
     */
    val damageStageProgress: Double

    /**
     * Hits remaining in the current stage. Zero unless the current stage is in hits mode.
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
