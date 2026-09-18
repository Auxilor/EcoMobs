package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobStageChangeEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerStageChange : Trigger("ecomobs_stage_change") {
    override val description = "Fires when an EcoMob finishes one of its damage stages."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Dispatched on the player whose hit ended the stage, and on the mob itself otherwise.",
        "Also fires as the last stage ends, where the value is one past the number of stages."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player whose hit ended the stage, if there was one.",
        TriggerParameter.VICTIM to "The mob whose stage changed.",
        TriggerParameter.LOCATION to "Where the mob is.",
        TriggerParameter.TEXT to "The ID of the mob whose stage changed.",
        TriggerParameter.VALUE to "The 1-based position of the stage now in progress."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: EcoMobStageChangeEvent) {
        val living = event.mob
        val player = event.player

        this.dispatch(
            player?.toDispatcher() ?: living.entity.toDispatcher(),
            TriggerData(
                player = player,
                victim = living.entity,
                location = living.entity.location,
                text = living.mob.id,
                value = event.stageNumber.toDouble(),
                event = event
            )
        )
    }
}
