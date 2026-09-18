package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobSpawnerBreakEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerBreakSpawner : Trigger("ecomobs_break_spawner") {
    override val description = "Fires when a player breaks a custom spawner and the whole thing comes up."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Taking part of a stack off without the block coming up fires " +
                "ecomobs_unstack_spawner instead."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who broke the spawner.",
        TriggerParameter.BLOCK to "The spawner block.",
        TriggerParameter.LOCATION to "Where the spawner was.",
        TriggerParameter.TEXT to "The ID of the mob the spawner spawns.",
        TriggerParameter.VALUE to "How many spawners the broken stack held."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobSpawnerBreakEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = event.location.block,
                location = event.location,
                text = event.mobId,
                value = event.stackSize.toDouble(),
                event = event
            )
        )
    }
}
