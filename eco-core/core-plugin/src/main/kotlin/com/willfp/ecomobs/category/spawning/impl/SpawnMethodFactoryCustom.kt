package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.util.randDouble
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.ecomobs.category.spawning.spawnpoints.SpawnPointType
import com.willfp.ecomobs.category.spawning.spawnpoints.spawnPoints
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.scheduler.BukkitTask

object SpawnMethodFactoryCustom : SpawnMethodFactory("custom") {
    override fun create(
        category: MobCategory,
        config: Config,
        plugin: EcoPlugin,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodCustom(category, config, plugin, context)
    }

    class SpawnMethodCustom(
        category: MobCategory,
        config: Config,
        plugin: EcoPlugin,
        context: ViolationContext
    ) : SpawnMethod(category, config, plugin), Listener {
        private val spawnRate = plugin.configYml.getInt("custom-spawning.spawn-rate").toLong()

        private val spawnTypes = config.getStrings("spawn-types")
            .mapNotNull { enumValueOfOrNull<SpawnPointType>(it.uppercase()) }
            .toSet()

        private val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("conditions")
        )

        private val chance = config.getDouble("chance")

        private var task: BukkitTask? = null

        override fun onStart() {
            task = plugin.scheduler.runTimer(spawnRate, spawnRate) {
                tick()
            }
        }

        override fun onStop() {
            task?.cancel()
        }

        private fun tick() {
            for (player in Bukkit.getOnlinePlayers()) {
                for (point in player.spawnPoints.filter { it.type in spawnTypes }) {
                    if (randDouble(0.0, 100.0) > chance) {
                        continue
                    }

                    if (!conditions.areMet(point.location.toDispatcher(), EmptyProvidedHolder)) {
                        continue
                    }

                    val mob = category.mobs.randomOrNull() ?: continue
                    mob.spawn(point.location, SpawnReason.NATURAL)
                }
            }
        }
    }
}
