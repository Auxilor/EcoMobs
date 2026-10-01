package com.willfp.ecomobs.mob.stage

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.config.validateNotNull
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effects

/**
 * One phase of a staged fight.
 *
 * Everything about *how* the stage is drained lives in its [mode]; the stage itself only
 * knows how to announce that it started and ended.
 */
class DamageStage(
    val mode: DamageStageMode,
    val startEffects: Chain?,
    val endEffects: Chain?
) {
    /**
     * How much the stage takes before it ends, in whatever the mode counts.
     */
    val amount: Double
        get() = mode.amount
}

fun Config.toDamageStage(key: Int, context: ViolationContext): DamageStage {
    val factory = DamageStageModes[getString("mode").lowercase()]
        .validateNotNull(
            ConfigViolation(
                "damage-stages.$key.mode",
                "Invalid damage stage mode"
            )
        )

    val stageContext = context.with("damage stages").with(key.toString())

    return DamageStage(
        factory.create(this, key, stageContext),
        Effects.compileChain(getSubsections("start-effects"), stageContext.with("start effects")),
        Effects.compileChain(getSubsections("end-effects"), stageContext.with("end effects"))
    )
}
