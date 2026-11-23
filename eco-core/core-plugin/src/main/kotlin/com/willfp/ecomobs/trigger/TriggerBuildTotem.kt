package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobTotemBuildEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerBuildTotem : Trigger("ecomobs_build_totem") {
    override val description = "Fires when a player completes a spawn totem and its conditions are met."

    override val categories = setOf("ecomobs")

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who finished the totem.",
        TriggerParameter.LOCATION to "Where the mob is about to spawn.",
        TriggerParameter.TEXT to "The ID of the mob the totem spawns."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobTotemBuildEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.location,
                text = event.mob.id,
                event = event
            )
        )
    }
}
