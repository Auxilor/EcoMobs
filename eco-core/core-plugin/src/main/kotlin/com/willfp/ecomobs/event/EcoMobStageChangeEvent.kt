package com.willfp.ecomobs.event

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.stage.DamageStage
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a mob finishes a damage stage, after that stage's end effects and before
 * the next one's start effects.
 */
class EcoMobStageChangeEvent(
    override val mob: LivingMob,
    /**
     * The stage that just finished.
     */
    val previousStage: DamageStage,
    /**
     * The stage now in progress, or null if that was the last one and the mob is about
     * to take its killing blow.
     */
    val stage: DamageStage?,
    /**
     * The 1-based position of [stage], or one past the last stage when it is null.
     */
    val stageNumber: Int,
    /**
     * The player whose hit ended the stage, or null if it came from elsewhere.
     */
    val player: Player?
) : Event(), MobEvent {
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}
