package com.willfp.ecomobs.integrations.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.willfp.eco.core.integrations.Integration
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.impl.ecoMob
import me.lokka30.levelledmobs.events.MobPreLevelEvent
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class IntegrationModelEngine : Listener, Integration {
    private val integration = MobIntegration("model-engine")

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob.mob
        val entity = event.mob.entity

        val modelEngineID = mob.getIntegrationConfig(integration).getStringOrNull("id") ?: return

        val model = ModelEngineAPI.createActiveModel(modelEngineID) ?: return

        val modelled = ModelEngineAPI.createModeledEntity(entity)
        modelled.addModel(model, true)
        modelled.isBaseEntityVisible = true
    }

    override fun getPluginName(): String {
        return "ModelEngine"
    }
}
