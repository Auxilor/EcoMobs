package com.willfp.ecobosses

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecobosses.bosses.EcoBosses
import com.willfp.ecobosses.bosses.bossHolders
import com.willfp.ecobosses.commands.CommandEcobosses
import com.willfp.ecobosses.config.EcoBossesYml
import com.willfp.ecobosses.defence.DamageMultiplierHandler
import com.willfp.ecobosses.defence.ImmunitiesHandler
import com.willfp.ecobosses.defence.MountHandler
import com.willfp.ecobosses.defence.PickupHandler
import com.willfp.ecobosses.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecobosses.lifecycle.CompatibilityListeners
import com.willfp.ecobosses.lifecycle.LifecycleHandlers
import com.willfp.ecobosses.spawn.SpawnEggHandler
import com.willfp.ecobosses.spawn.SpawnTotemHandler
import com.willfp.ecobosses.util.DiscoverRecipeListener
import com.willfp.ecobosses.util.TopDamagerListener
import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.event.Listener

class EcoBossesPlugin : LibReforgePlugin(525, 10635, "&9") {
    val ecoBossesYml: EcoBossesYml

    init {
        instance = this
        ecoBossesYml = EcoBossesYml(this)
        registerHolderProvider { it.bossHolders }
    }

    override fun handleReloadAdditional() {
        logger.info(EcoBosses.values().size.toString() + " Bosses Loaded")
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcobosses(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DiscoverRecipeListener(this),
            TopDamagerListener(),
            LifecycleHandlers(),
            SpawnEggHandler(),
            DamageMultiplierHandler(),
            MountHandler(),
            PickupHandler(),
            ImmunitiesHandler(),
            CompatibilityListeners(),
            SpawnTotemHandler()
        )
    }

    override fun loadAdditionalIntegrations(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("LevelledMobs") { this.eventManager.registerListener(IntegrationLevelledMobs()) }
        )
    }

    override fun getMinimumEcoVersion(): String {
        return "6.24.0"
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoBossesPlugin
    }
}
