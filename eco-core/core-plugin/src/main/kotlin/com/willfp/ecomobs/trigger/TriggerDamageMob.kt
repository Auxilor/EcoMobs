package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.event.EcoMobDamageEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerDamageMob : Trigger("ecomobs_damage_mob") {
    override val description = "Fires when an EcoMob takes damage, after the mob's own damage modifiers."

    override val categories = setOf("ecomobs", "combat")

    override val additionalInfo = listOf(
        "Dispatched on the damager when a player dealt the damage, and on the mob itself otherwise."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.PLAYER to "The player who dealt the damage, if there was one.",
        TriggerParameter.VICTIM to "The EcoMob that was damaged.",
        TriggerParameter.LOCATION to "Where the mob was damaged.",
        TriggerParameter.TEXT to "The ID of the mob that was damaged.",
        TriggerParameter.VALUE to "The damage dealt."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EcoMobDamageEvent) {
        val living = event.mob
        val player = event.player

        this.dispatch(
            player?.toDispatcher() ?: living.entity.toDispatcher(),
            TriggerData(
                player = player,
                victim = living.entity,
                location = living.entity.location,
                text = living.mob.id,
                value = event.damage,
                event = event
            )
        )
    }
}
