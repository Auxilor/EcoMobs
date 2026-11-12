package com.willfp.ecomobs

import com.willfp.ecomobs.category.MobCategories
import com.willfp.ecomobs.category.spawning.spawnpoints.SpawnPointGenerator
import com.willfp.ecomobs.commands.ecoMobsCommand
import com.willfp.ecomobs.display.SpawnEggDisplay
import com.willfp.ecomobs.display.SpawnerItemDisplay
import com.willfp.ecomobs.goals.entity.EntityGoalRandomTeleport
import com.willfp.ecomobs.goals.target.TargetGoalClosestPlayer
import com.willfp.ecomobs.goals.target.TargetGoalHighestArmorPlayer
import com.willfp.ecomobs.goals.target.TargetGoalHighestHealthPlayer
import com.willfp.ecomobs.goals.target.TargetGoalLastDamagerPlayer
import com.willfp.ecomobs.goals.target.TargetGoalLowestArmorPlayer
import com.willfp.ecomobs.goals.target.TargetGoalLowestHealthPlayer
import com.willfp.ecomobs.goals.target.TargetGoalMostCrowdedPlayer
import com.willfp.ecomobs.goals.target.TargetGoalNotLookingPlayer
import com.willfp.ecomobs.goals.target.TargetGoalRandomPlayer
import com.willfp.ecomobs.goals.target.TargetGoalTopDamagerPlayer
import com.willfp.ecomobs.handler.ChunkHandler
import com.willfp.ecomobs.handler.DamageModifierHandler
import com.willfp.ecomobs.handler.DamageStageHandler
import com.willfp.ecomobs.handler.MountHandler
import com.willfp.ecomobs.handler.PaperSpawnerPickBlockHandler
import com.willfp.ecomobs.handler.PaperWaterTeleportHandler
import com.willfp.ecomobs.handler.SpawnEggHandler
import com.willfp.ecomobs.handler.SpawnTotemHandler
import com.willfp.ecomobs.handler.SpawnerHandler
import com.willfp.ecomobs.handler.SpawnerStackHandler
import com.willfp.ecomobs.handler.SpigotSpawnerPickBlockHandler
import com.willfp.ecomobs.handler.SpigotWaterTeleportHandler
import com.willfp.ecomobs.handler.StackHandler
import com.willfp.ecomobs.handler.SunlightBurningHandler
import com.willfp.ecomobs.handler.SunlightBurningSettings
import com.willfp.ecomobs.handler.VanillaCompatibilityHandlers
import com.willfp.ecomobs.handler.WaterSensitivityHandler
import com.willfp.ecomobs.handler.WaterSensitivitySettings
import com.willfp.ecomobs.integrations.coreprotect.IntegrationCoreProtect
import com.willfp.ecomobs.integrations.levelledmobs.IntegrationLevelledMobs
import com.willfp.ecomobs.integrations.libsdisguises.IntegrationLibsDisguises
import com.willfp.ecomobs.integrations.model.IntegrationModel
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.damage.TopDamagerHandler
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.scheduled.ScheduledSpawnHandler
import com.willfp.ecomobs.scheduled.ScheduledSpawner
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.SpawnerAnimations
import com.willfp.ecomobs.spawner.SpawnerDisplay
import com.willfp.ecomobs.spawner.SpawnerHolograms
import com.willfp.ecomobs.spawner.SpawnerItems
import com.willfp.ecomobs.spawner.SpawnerSettings
import com.willfp.ecomobs.spawner.SpawnerSpawnLoop
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.particle.SpawnerParticleAnimations
import com.willfp.ecomobs.stacking.MobStackTicker
import com.willfp.ecomobs.stacking.StackSettings
import com.willfp.ecomobs.trigger.EcoMobsTriggers
import org.bukkit.entity.Mob
import org.bukkit.event.Listener
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.command.EcoCommand
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.plugin.startup.Prerequisite
import wiki.ecohub.game.entity.ai.EntityGoals
import wiki.ecohub.game.entity.ai.TargetGoals
import wiki.ecohub.game.item.Items
import wiki.ecohub.provider.ExternalProviderLoader
import wiki.ecohub.provider.metrics.EcoMetricsChart
import wiki.ecohub.rules.holder.EntityProvidedHolder
import wiki.ecohub.rules.holder.registerSpecificHolderProvider
import wiki.ecohub.ui.display.DisplayModule
import java.util.logging.Level

internal lateinit var plugin: EcoMobsPlugin
    private set

class EcoMobsPlugin : EcoPlugin() {
    val spawnPointGenerator: SpawnPointGenerator = SpawnPointGenerator(this)
    val topDamagerHandler: TopDamagerHandler = TopDamagerHandler(this)

    init {
        plugin = this
    }

    override fun handleEnable() {
        registerSpecificHolderProvider<Mob> { mob ->
            listOfNotNull(mob.ecoMob?.entityHolder).map { holder ->
                EntityProvidedHolder(holder, mob)
            }
        }
    }

    override fun handleLoad() {
        EntityGoals.register(EntityGoalRandomTeleport.Deserializer)
        TargetGoals.register(TargetGoalClosestPlayer.Deserializer)
        TargetGoals.register(TargetGoalHighestArmorPlayer.Deserializer)
        TargetGoals.register(TargetGoalHighestHealthPlayer.Deserializer)
        TargetGoals.register(TargetGoalLastDamagerPlayer.Deserializer)
        TargetGoals.register(TargetGoalLowestArmorPlayer.Deserializer)
        TargetGoals.register(TargetGoalLowestHealthPlayer.Deserializer)
        TargetGoals.register(TargetGoalMostCrowdedPlayer.Deserializer)
        TargetGoals.register(TargetGoalNotLookingPlayer.Deserializer)
        TargetGoals.register(TargetGoalRandomPlayer.Deserializer)
        TargetGoals.register(TargetGoalTopDamagerPlayer.Deserializer)
        Items.registerProvider(SpawnerItems)
        EcoMobsTriggers.registerAll()
    }

    override fun loadConfigCategories(): List<NativeConfigCategory> {
        return listOf(
            MobCategories,
            EcoMobs,
        )
    }

    override fun handleReload() {
        SpawnerAnimations.reload()
        SpawnerStackSettings.reload()
        SpawnerSettings.reload()
        // Chunks loaded before the plugin enabled never fire a ChunkLoadEvent, and a
        // config change can flip which spawners are tracked, so the index is rebuilt here.
        SpawnerHandler.indexLoadedChunks()
        SpawnerDisplay.start()
        SpawnerSpawnLoop.start()
        StackSettings.reload()
        WaterSensitivitySettings.reload()
        SunlightBurningSettings.reload()
        MobStackTicker.start()
        ScheduledSpawner.start()
        SpawnerHolograms.reloadAll()
    }

    override fun handleDisable() {
        runCatching {
            SpawnerDisplay.stop()
            SpawnerSpawnLoop.stop()
            MobStackTicker.stop()
            ScheduledSpawner.stop()
            SpawnerHolograms.clear()
            PlacedSpawners.clear()
        }.onFailure { exception ->
            logger.log(Level.SEVERE, "Could not clean up on disable", exception)
        }

        ScheduledSpawnHandler.saveOnDisable()
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DamageModifierHandler,
            DamageStageHandler,
            MountHandler,
            VanillaCompatibilityHandlers,
            DiscoverRecipeListener,
            SpawnEggHandler,
            SpawnTotemHandler,
            topDamagerHandler,
            SpawnerHandler,
            WaterSensitivityHandler,
            SunlightBurningHandler,
            SpawnerStackHandler,
            StackHandler,
            ChunkHandler,
            ScheduledSpawnHandler
        ) + platformListeners()
    }

    /**
     * The listeners that differ between Paper and Spigot. Paper's own events don't exist
     * on Spigot, so a listener naming one can only be registered on Paper; Spigot gets a
     * workaround built from Bukkit's events instead.
     */
    private fun platformListeners(): List<Listener> =
        if (Prerequisite.HAS_PAPER.isMet) {
            listOf(
                PaperSpawnerPickBlockHandler,
                PaperWaterTeleportHandler
            )
        } else {
            listOf(
                SpigotSpawnerPickBlockHandler,
                SpigotWaterTeleportHandler
            )
        }

    override fun loadDisplayModules(): List<DisplayModule> {
        return listOf(SpawnEggDisplay, SpawnerItemDisplay)
    }

    override fun getExternalProviderLoaders(): List<ExternalProviderLoader> {
        return listOf(
            ExternalProviderLoader("LevelledMobs") { IntegrationLevelledMobs },
            ExternalProviderLoader("ModelEngine") { IntegrationModel(configKey = "model-engine") },
            ExternalProviderLoader("BetterModel") { IntegrationModel(configKey = "better-model") },
            ExternalProviderLoader("LibsDisguises") { IntegrationLibsDisguises },
            ExternalProviderLoader("CoreProtect") { IntegrationCoreProtect },
        )
    }

    override fun loadPluginCommands(): List<EcoCommand> {
        return listOf(
            ecoMobsCommand()
        )
    }

    override fun getCustomCharts() = listOf(
        EcoMetricsChart.SingleLine("total_mobs") { EcoMobs.values().size },
        EcoMetricsChart.SingleLine("total_mob_categories") { MobCategories.values().size },
        EcoMetricsChart.SingleLine("total_particle_animations") { SpawnerParticleAnimations.values().size },
        EcoMetricsChart.SingleLine("placed_spawners") { PlacedSpawners.values().size },
        EcoMetricsChart.SingleLine("max_mobs_per_player") { configYml.getInt("max-mobs-per-player") }
    )
}
