package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobSpawnerUnstackEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerUnstackSpawner : Trigger("ecomobs_unstack_spawner") {
    override val description = "Fires when a player takes spawners off a stack, leaving the block in place."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Breaking the last spawner, or the whole stack at once, fires " +
                "ecomobs_break_spawner instead."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player taking the spawners.",
        TriggerParameter.BLOCK to "The spawner block left behind.",
        TriggerParameter.LOCATION to "Where the stack is.",
        TriggerParameter.TEXT to "The ID of the mob the spawner spawns.",
        TriggerParameter.VALUE to "How many spawners are being taken.",
        TriggerParameter.ALT_VALUE to "The stack size before the break."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobSpawnerUnstackEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = event.location.block,
                location = event.location,
                text = event.mobId,
                value = event.amount.toDouble(),
                altValue = event.currentSize.toDouble(),
                event = event
            )
        )
    }
}
