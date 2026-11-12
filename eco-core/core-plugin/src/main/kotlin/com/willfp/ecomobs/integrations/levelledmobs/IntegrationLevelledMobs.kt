package com.willfp.ecomobs.integrations.levelledmobs

import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.impl.ecoMob
import io.github.arcaneplugins.levelledmobs.events.MobPreLevelEvent
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import wiki.ecohub.provider.ExternalProvider

object IntegrationLevelledMobs : Listener, ExternalProvider {
    private val integration = MobIntegration("levelled-mobs")

    @EventHandler
    fun handle(event: MobPreLevelEvent) {
        val bukkitEntity = event.entity as? Mob ?: return
        val ecoMob = bukkitEntity.ecoMob ?: return

        if (!ecoMob.getIntegrationConfig(integration).getBool("can-level")) {
            event.isCancelled = true
        }
    }

    override fun getPluginName(): String {
        return "LevelledMobs"
    }
}
