package com.willfp.ecomobs.mob.stage

import com.willfp.ecomobs.mob.EcoMob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

/**
 * What a damage event does to the stage it lands on.
 */
sealed interface DamageResponse {
    /**
     * Take [amount] off the stage. The mob's health is left to follow stage progress,
     * so the event's own damage is spent entirely on the stage.
     */
    @JvmInline
    value class Drain(val amount: Double) : DamageResponse

    /**
     * Leave the stage where it is, and let the hit land as a zero-damage one, so the
     * hurt animation, knockback, invulnerability ticks, and take-damage effects all
     * still run.
     */
    data object Ignore : DamageResponse

    /**
     * Leave the stage where it is, and cancel the event outright.
     */
    data object Block : DamageResponse
}

/**
 * One stage's worth of a mode: how much it needs, what damage does to it, and where its
 * progress comes from if not from damage.
 *
 * Built by a [DamageStageModeFactory], one instance per stage in a mob's config, so a
 * mode is free to hold whatever parsed state it needs as fields.
 */
abstract class DamageStageMode(
    val factory: DamageStageModeFactory
) {
    /**
     * How much the stage takes before it ends.
     */
    abstract val amount: Double

    /**
     * What a damage event does to the stage.
     */
    abstract fun respondTo(event: EntityDamageEvent, player: Player?): DamageResponse

    /**
     * Bind whatever feeds the stage other than damage.
     *
     * Called once per mob type, when the mob is registered, not once per spawned mob.
     * Anything bound here must be released again in [unbind].
     */
    open fun bind(mob: EcoMob, stage: DamageStage) {
        // Override when the mode has a source of progress other than damage
    }

    /**
     * Release everything [bind] set up.
     */
    open fun unbind() {
        // Override when the mode has a source of progress other than damage
    }
}
