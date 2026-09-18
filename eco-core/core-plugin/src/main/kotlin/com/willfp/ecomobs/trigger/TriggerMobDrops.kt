package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobDropsEvent
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerMobDrops : Trigger("ecomobs_mob_drops") {
    override val description = "Fires when an EcoMob's drop table has been rolled, before anything is given out."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Dispatched on the player the drops go to, and on the drop location otherwise.",
        "A stacked mob rolls its table once per mob, so this fires once per roll."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player the drops go to, if there is one.",
        TriggerParameter.LOCATION to "Where the drops land.",
        TriggerParameter.TEXT to "The ID of the mob that dropped them.",
        TriggerParameter.VALUE to "The experience dropped.",
        TriggerParameter.ALT_VALUE to "How many items were rolled."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobDropsEvent) {
        val player = event.player

        // The drop location stands in when the drops fall on the ground.
        val dispatcher: Dispatcher<*> = player?.toDispatcher() ?: event.location.toDispatcher()

        this.dispatch(
            dispatcher,
            TriggerData(
                player = player,
                location = event.location,
                text = event.ecoMob.id,
                value = event.experience.toDouble(),
                altValue = event.drops.size.toDouble(),
                event = event
            )
        )
    }
}
