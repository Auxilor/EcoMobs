package com.willfp.ecobosses.integrations.levelledmobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.ecobosses.bosses.Bosses
import me.lokka30.levelledmobs.events.MobPreLevelEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class IntegrationLevelledMobs : Listener, Integration {
    @EventHandler
    fun handle(event: MobPreLevelEvent) {
        if (Bosses[event.entity] != null) {
            event.isCancelled = true
        }
    }

    override fun getPluginName(): String {
        return "LevelledMobs"
    }
}
