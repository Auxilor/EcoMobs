package com.willfp.ecomobs

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.entities.ai.EntityGoals
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.util.toSingletonList
import com.willfp.ecomobs.category.MobCategories
import com.willfp.ecomobs.category.spawning.spawnpoints.SpawnPointGenerator
import com.willfp.ecomobs.commands.CommandEcoMobs
import com.willfp.ecomobs.display.SpawnEggDisplay
import com.willfp.ecomobs.goals.entity.EntityGoalRandomTeleport
import com.willfp.ecomobs.handler.DamageModifierHandler
import com.willfp.ecomobs.handler.MountHandler
import com.willfp.ecomobs.handler.SpawnEggHandler
import com.willfp.ecomobs.handler.SpawnTotemHandler
import com.willfp.ecomobs.handler.VanillaCompatibilityHandlers
import com.willfp.ecomobs.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecomobs.integrations.libsdisguises.IntegrationLibsDisguises
import com.willfp.ecomobs.integrations.modelengine.IntegrationModelEngine
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.damage.TopDamagerHandler
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.EmptyProvidedHolder.holder
import com.willfp.libreforge.EntityProvidedHolder
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerSpecificHolderProvider
import org.bukkit.entity.Mob
import org.bukkit.event.Listener

internal lateinit var plugin: EcoMobsPlugin
    private set

class EcoMobsPlugin : LibreforgePlugin() {
    val spawnPointGenerator = SpawnPointGenerator(this)
    val topDamagerHandler = TopDamagerHandler(this)

    init {
        plugin = this
    }

    override fun handleEnable() {
        registerSpecificHolderProvider<Mob> {
            it.ecoMob?.entityHolder.toSingletonList().map { holder ->
                EntityProvidedHolder(holder, it)
            }
        }
    }

    override fun handleLoad() {
        EntityGoals.register(EntityGoalRandomTeleport.Deserializer)
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            MobCategories,
            EcoMobs,
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DamageModifierHandler(),
            MountHandler(),
            VanillaCompatibilityHandlers(),
            DiscoverRecipeListener(this),
            SpawnEggHandler(this),
            SpawnTotemHandler(),
            topDamagerHandler
        )
    }

    override fun createDisplayModule(): DisplayModule {
        return SpawnEggDisplay(this)
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("LevelledMobs") { this.eventManager.registerListener(IntegrationLevelledMobs()) },
            IntegrationLoader("ModelEngine") { this.eventManager.registerListener(IntegrationModelEngine()) },
            IntegrationLoader("LibsDisguises") { this.eventManager.registerListener(IntegrationLibsDisguises()) },
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoMobs(this)
        )
    }
}
