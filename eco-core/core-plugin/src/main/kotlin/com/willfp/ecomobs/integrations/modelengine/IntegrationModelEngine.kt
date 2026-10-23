package com.willfp.ecomobs.integrations.modelengine

import com.willfp.eco.core.integrations.Integration
import com.willfp.eco.core.integrations.modelengine.ModelEngineManager
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object IntegrationModelEngine : Listener, Integration {
    private val integration = MobIntegration("model-engine")

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob.mob
        val entity = event.mob.entity

        val modelEngineID = mob.getIntegrationConfig(integration).getStringOrNull("id") ?: return

        val model = ModelEngineManager.createActiveModel(modelEngineID) ?: return

        val modelled = ModelEngineManager.createModeledEntity(entity) ?: return
        modelled.addModel(model)
        modelled.isBaseEntityVisible = false
    }

    override fun getPluginName(): String {
        return "ModelEngine"
    }
}
