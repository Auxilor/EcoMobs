package com.willfp.ecomobs.mob.stage

import com.willfp.libreforge.effects.Chain
import org.bukkit.entity.Player

internal class DamageStageTracker(
    private val stages: List<DamageStage>,
    private val triggerEffects: (Chain, Player?) -> Unit,
    /**
     * Called with the stage that finished, the one now in progress (null once they have
     * all been used up), its 1-based position, and the player who ended the last one.
     */
    private val onStageChange: (DamageStage, DamageStage?, Int, Player?) -> Unit
) {
    var index = 0
        private set

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

    /**
     * Resume from a previously saved position, without triggering any stage effects.
     */
    fun restore(index: Int, remaining: Double) {
        this.index = index.coerceIn(0, stages.size)
        this.remaining = if (isFinished) 0.0 else remaining.coerceIn(0.0, stage.amount)
    }

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
        val finished = stages[index]
        finished.endEffects?.let { triggerEffects(it, player) }
        index++

        if (isFinished) {
            // Floored, so the overshoot from a large count can't be read back as a
            // negative amount remaining.
            remaining = 0.0
            onStageChange(finished, null, index + 1, player)
            return true
        }

        remaining = stages[index].amount
        onStageChange(finished, stages[index], index + 1, player)
        stages[index].startEffects?.let { triggerEffects(it, player) }
        return false
    }
}
