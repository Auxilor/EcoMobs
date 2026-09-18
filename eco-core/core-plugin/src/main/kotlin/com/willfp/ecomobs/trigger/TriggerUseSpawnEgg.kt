package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobEggUseEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerUseSpawnEgg : Trigger("ecomobs_use_spawn_egg") {
    override val description = "Fires when a player spawns an EcoMob with a spawn egg."

    override val categories = setOf("ecomobs")

    override val additionalInfo = listOf(
        "Only fires for eggs used by a player. Eggs fired from a dispenser have nobody " +
                "to dispatch on, so they do not fire this trigger."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who used the egg.",
        TriggerParameter.LOCATION to "Where the mob is about to spawn.",
        TriggerParameter.TEXT to "The ID of the mob the egg spawns."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobEggUseEvent) {
        val player = event.player ?: return

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
