package com.willfp.ecomobs.mob.stage

import com.willfp.libreforge.effects.Chain
import org.bukkit.entity.Player

internal class DamageStageTracker(
    private val stages: List<DamageStage>,
    private val triggerEffects: (Chain, Player?) -> Unit
) {
    private var index = 0

    var remaining = stages.first().amount
        private set

    val isFinished: Boolean
        get() = index >= stages.size

    val stage: DamageStage
        get() = stages[index.coerceAtMost(stages.size - 1)]

    val stageNumber: Int
        get() = index.coerceAtMost(stages.size - 1) + 1

    val stageProgress: Double
        get() = if (isFinished) 1.0 else 1 - remaining / stage.amount

    val progress: Double
        get() = if (isFinished) 1.0 else (index + stageProgress) / stages.size

    fun start() {
        stages.first().startEffects?.let { triggerEffects(it, null) }
    }

    fun consume(amount: Double, player: Player?): Boolean {
        if (isFinished) {
            return true
        }

        remaining -= amount

        if (remaining > 0) {
            return false
        }

        // Overflow is discarded so a stage never starts partly drained.
        stages[index].endEffects?.let { triggerEffects(it, player) }
        index++

        if (isFinished) {
            return true
        }

        remaining = stages[index].amount
        stages[index].startEffects?.let { triggerEffects(it, player) }
        return false
    }
}
