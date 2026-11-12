package com.willfp.ecomobs.integrations.libsdisguises

import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import wiki.ecohub.provider.ExternalProvider

object IntegrationLibsDisguises : Listener, ExternalProvider {
    private val integration = MobIntegration("libs-disguises")

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val mob = event.mob.mob
        val entity = event.mob.entity

        val disguiseID = mob.getIntegrationConfig(integration).getStringOrNull("id") ?: return

        val disguise = DisguiseAPI.getCustomDisguise(disguiseID) ?: return

        DisguiseAPI.disguiseToAll(entity, disguise)
    }

    override fun getPluginName(): String {
        return "LibsDisguises"
    }
}
