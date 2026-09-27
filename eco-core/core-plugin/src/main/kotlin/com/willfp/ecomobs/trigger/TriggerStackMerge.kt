package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobStackMergeEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerStackMerge : Trigger("ecomobs_stack_merge") {
    override val description = "Fires when a mob is about to be absorbed into a nearby stack."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Dispatched on the mob being merged into, as there is no player involved.",
        "Vanilla mobs stack too, so the text is empty unless the stack is of EcoMobs."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The mob being merged into.",
        TriggerParameter.LOCATION to "Where the stack is.",
        TriggerParameter.TEXT to "The ID of the EcoMob, or empty for a vanilla mob.",
        TriggerParameter.VALUE to "The size the stack ends up at."
    )

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobStackMergeEvent) {
        val target = event.target

        this.dispatch(
            target.toDispatcher(),
            TriggerData(
                victim = target,
                location = target.location,
                text = target.ecoMob?.id,
                value = event.size.toDouble(),
                event = event
            )
        )
    }
}
