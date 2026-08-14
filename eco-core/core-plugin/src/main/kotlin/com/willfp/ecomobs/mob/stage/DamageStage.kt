package com.willfp.ecomobs.mob.stage

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.config.validate
import com.willfp.ecomobs.config.validateNotNull
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.enumValueOfOrNull

enum class DamageStageMode {
    HEALTH,
    HITS
}

data class DamageStage(
    val mode: DamageStageMode,
    val amount: Double,
    val playerOnly: Boolean,
    val startEffects: Chain?,
    val endEffects: Chain?
)

fun Config.toDamageStage(key: Int, context: ViolationContext): DamageStage {
    val mode = enumValueOfOrNull<DamageStageMode>(getString("mode").uppercase())
        .validateNotNull(
            ConfigViolation(
                "damage-stages.$key.mode",
                "Invalid damage stage mode"
            )
        )

    val amount = when (mode) {
        DamageStageMode.HEALTH -> getDouble("health")
            .validate { it > 0 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.health",
                    "Stage health must be greater than 0"
                )
            }

        DamageStageMode.HITS -> getInt("required-hits")
            .validate { it >= 1 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.required-hits",
                    "Required hits must be at least 1"
                )
            }
            .toDouble()
    }

    val stageContext = context.with("damage stages").with(key.toString())

    return DamageStage(
        mode,
        amount,
        getBool("player-only"),
        Effects.compileChain(getSubsections("start-effects"), stageContext.with("start effects")),
        Effects.compileChain(getSubsections("end-effects"), stageContext.with("end effects"))
    )
}
