package com.willfp.ecobosses

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.bosses.EggDisplay
import com.willfp.ecobosses.bosses.bossHolders
import com.willfp.ecobosses.commands.CommandEcoBosses
import com.willfp.ecobosses.defence.DamageMultiplierHandler
import com.willfp.ecobosses.defence.ImmunitiesHandler
import com.willfp.ecobosses.defence.MountHandler
import com.willfp.ecobosses.defence.PickupHandler
import com.willfp.ecobosses.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecobosses.libreforge.EffectBossDropChanceMultiplier
import com.willfp.ecobosses.libreforge.TriggerKillBoss
import com.willfp.ecobosses.libreforge.TriggerSpawnBoss
import com.willfp.ecobosses.lifecycle.CompatibilityListeners
import com.willfp.ecobosses.lifecycle.ConsoleLoggers
import com.willfp.ecobosses.lifecycle.DeathListeners
import com.willfp.ecobosses.lifecycle.LifecycleHandlers
import com.willfp.ecobosses.spawn.AutospawnHandler
import com.willfp.ecobosses.spawn.SpawnEggHandler
import com.willfp.ecobosses.spawn.SpawnTotemHandler
import com.willfp.ecobosses.util.DiscoverRecipeListener
import com.willfp.ecobosses.util.TopDamagerListener
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener

class EcoBossesPlugin : LibreforgePlugin() {
    init {
        instance = this
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            Bosses
        )
    }

    override fun handleLoad() {
        Effects.register(EffectBossDropChanceMultiplier)
        Triggers.register(TriggerKillBoss)
        Triggers.register(TriggerSpawnBoss)
    }

    override fun handleEnable() {
        registerHolderProvider { it.bossHolders }
    }

    override fun handleReload() {
        Bosses.getAllAlive().forEach { it.remove() }

        AutospawnHandler.startSpawning(this)
    }

    override fun handleDisable() {
        Bosses.getAllAlive().forEach { it.remove() }
    }

    override fun createDisplayModule(): DisplayModule {
        return EggDisplay(this)
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoBosses(this)
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

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("LevelledMobs") { this.eventManager.registerListener(IntegrationLevelledMobs()) }
        )
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoBossesPlugin
    }
}
