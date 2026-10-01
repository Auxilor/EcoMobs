package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobSpawnerStackEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerStackSpawner : Trigger("ecomobs_stack_spawner") {
    override val description = "Fires when a player adds spawners to a stack."

    override val categories = setOf("ecomobs")

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player stacking the spawners.",
        TriggerParameter.BLOCK to "The spawner block being stacked onto.",
        TriggerParameter.LOCATION to "Where the stack is.",
        TriggerParameter.TEXT to "The ID of the mob the spawner spawns.",
        TriggerParameter.VALUE to "How many spawners are being added.",
        TriggerParameter.ALT_VALUE to "The stack size before the merge."
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
    fun handle(event: EcoMobSpawnerStackEvent) {
        val player = event.player ?: return

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
