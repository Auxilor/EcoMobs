package com.willfp.ecomobs.integrations.bettermodel

import com.willfp.eco.core.integrations.Integration
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.modelenginebridge.ModelEngineBridge
import kr.toxicity.model.api.BetterModel
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class IntegrationBetterModel : Listener, Integration {
    private val integration = MobIntegration("better-model")

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob.mob
        val entity = event.mob.entity

        val betterModelID = mob.getIntegrationConfig(integration).getStringOrNull("id") ?: return

        val model = BetterModel.modelOrNull(betterModelID) ?: return

        model.getOrCreate(entity)
    }

    override fun getPluginName(): String {
        return "BetterModel"
    }
}
