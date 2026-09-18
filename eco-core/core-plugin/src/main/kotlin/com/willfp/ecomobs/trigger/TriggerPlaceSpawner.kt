package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobSpawnerPlaceEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerPlaceSpawner : Trigger("ecomobs_place_spawner") {
    override val description = "Fires when a player places a custom spawner."

    override val categories = setOf("ecomobs")

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who placed the spawner.",
        TriggerParameter.BLOCK to "The spawner block.",
        TriggerParameter.LOCATION to "Where the spawner was placed.",
        TriggerParameter.TEXT to "The ID of the mob the spawner spawns.",
        TriggerParameter.VALUE to "How many spawners the placed stack holds."
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
    fun handle(event: EcoMobSpawnerPlaceEvent) {
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
