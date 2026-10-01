package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobStackDeathEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerStackDeath : Trigger("ecomobs_stack_death") {
    override val description = "Fires when a stacked mob dies, before the stack is paid out or split."

    override val categories = setOf("ecomobs", "combat")

    override val additionalInfo = listOf(
        "Dispatched on the killer where there is one, and on the mob itself otherwise.",
        "Only fires for mobs that stand in for more than one mob."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who killed the mob, if there was one.",
        TriggerParameter.VICTIM to "The mob that died.",
        TriggerParameter.LOCATION to "Where the mob died.",
        TriggerParameter.TEXT to "The ID of the EcoMob, or empty for a vanilla mob.",
        TriggerParameter.VALUE to "The size of the stack that died."
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
    fun handle(event: EcoMobStackDeathEvent) {
        val entity = event.entity
        val killer = entity.killer

        this.dispatch(
            killer?.toDispatcher() ?: entity.toDispatcher(),
            TriggerData(
                player = killer,
                victim = entity,
                location = entity.location,
                text = entity.ecoMob?.id,
                value = event.size.toDouble(),
                event = event
            )
        )
    }
}
