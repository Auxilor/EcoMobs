package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobSpawnerSpawnEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpawnerSpawn : Trigger("ecomobs_spawner_spawn") {
    override val description = "Fires for every mob a spawner is about to spawn."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Dispatched on the spawner's location, as there is no player involved.",
        "A stacked spawner fires this once per mob, so it can fire many times a cycle."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The spawner block.",
        TriggerParameter.LOCATION to "Where the mob is about to appear, offset from the spawner.",
        TriggerParameter.TEXT to "The ID of the mob being spawned."
    )

    override val parameters = setOf(
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobSpawnerSpawnEvent) {
        this.dispatch(
            event.location.toDispatcher(),
            TriggerData(
                block = event.location.block,
                location = event.spawnLocation,
                text = event.mobId,
                event = event
            )
        )
    }
}
