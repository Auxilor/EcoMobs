package com.willfp.ecomobs.integrations.bettermodel

import com.willfp.eco.core.integrations.Integration
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object IntegrationBetterModel : Listener, Integration {
    private val integration = MobIntegration("better-model")

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob.mob
        val entity = event.mob.entity

        val betterModelID = mob.getIntegrationConfig(integration).getStringOrNull("id") ?: return

        val model = BetterModel.modelOrNull(betterModelID) ?: return

        model.getOrCreate(BukkitAdapter.adapt(entity))
    }

    override fun getPluginName(): String {
        return "BetterModel"
    }
}
