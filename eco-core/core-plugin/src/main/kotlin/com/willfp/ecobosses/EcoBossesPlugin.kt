package com.willfp.ecobosses

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.bosses.EggDisplay
import com.willfp.ecobosses.bosses.bossHolders
import com.willfp.ecobosses.commands.CommandEcobosses
import com.willfp.ecobosses.defence.DamageMultiplierHandler
import com.willfp.ecobosses.defence.ImmunitiesHandler
import com.willfp.ecobosses.defence.MountHandler
import com.willfp.ecobosses.defence.PickupHandler
import com.willfp.ecobosses.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecobosses.lifecycle.CompatibilityListeners
import com.willfp.ecobosses.lifecycle.ConsoleLoggers
import com.willfp.ecobosses.lifecycle.DeathListeners
import com.willfp.ecobosses.lifecycle.LifecycleHandlers
import com.willfp.ecobosses.spawn.AutospawnHandler
import com.willfp.ecobosses.spawn.SpawnEggHandler
import com.willfp.ecobosses.spawn.SpawnTotemHandler
import com.willfp.ecobosses.util.DiscoverRecipeListener
import com.willfp.ecobosses.util.TopDamagerListener
import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.event.Listener

class EcoBossesPlugin : LibReforgePlugin() {
    init {
        instance = this
        registerHolderProvider { it.bossHolders }
    }

    override fun handleEnableAdditional() {
        this.copyConfigs("bosses")
    }

    override fun handleReloadAdditional() {
        Bosses.getAllAlive().forEach { it.remove() }

        logger.info(Bosses.values().size.toString() + " Bosses Loaded")

        AutospawnHandler.startSpawning(this)
    }

    override fun handleDisableAdditional() {
        Bosses.getAllAlive().forEach { it.remove() }
    }

    override fun createDisplayModule(): DisplayModule {
        return EggDisplay(this)
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
            SpawnEggHandler(this),
            DamageMultiplierHandler(),
            MountHandler(),
            PickupHandler(),
            ImmunitiesHandler(),
            CompatibilityListeners(),
            SpawnTotemHandler(),
            DeathListeners(),
            ConsoleLoggers(this)
        )
    }

    override fun loadAdditionalIntegrations(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("LevelledMobs") { this.eventManager.registerListener(IntegrationLevelledMobs()) }
        )
    }

    override fun getMinimumEcoVersion(): String {
        return "6.35.1"
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoBossesPlugin
    }
}
