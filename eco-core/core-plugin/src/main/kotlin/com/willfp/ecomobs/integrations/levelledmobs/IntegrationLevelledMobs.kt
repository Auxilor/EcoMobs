package com.willfp.ecomobs.integrations.levelledmobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.impl.ecoMob
import me.lokka30.levelledmobs.events.MobPreLevelEvent
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class IntegrationLevelledMobs : Listener, Integration {
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
