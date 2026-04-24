package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.scheduling.EcoWrappedTask
import com.willfp.eco.util.randDouble
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.ecomobs.category.spawning.spawnpoints.SpawnPointType
import com.willfp.ecomobs.category.spawning.spawnpoints.spawnPoints
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object SpawnMethodFactoryCustom : SpawnMethodFactory("custom") {
    override fun create(
        category: MobCategory,
        config: Config,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodCustom(category, config, context)
    }

    class SpawnMethodCustom(
        category: MobCategory,
        config: Config,
        context: ViolationContext
    ) : SpawnMethod(category, config), Listener {
        private val spawnRate = plugin.configYml.getInt("custom-spawning.spawn-rate").toLong()

        private val spawnTypes = config.getStrings("spawn-types")
            .mapNotNull { enumValueOfOrNull<SpawnPointType>(it.uppercase()) }
            .toSet()

        private val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("conditions")
        )

        private val chance = config.getDouble("chance")

        private var task: EcoWrappedTask? = null

        override fun onStart() {
            task = plugin.scheduler.runTaskTimer(spawnRate, spawnRate) {
                tick()
            }
        }

        override fun onStop() {
            task?.cancelTask()
        }

        private fun tick() {
            for (player in Bukkit.getOnlinePlayers()) {
                plugin.scheduler.runTask(player) { // folia issue
                    for (point in player.spawnPoints.filter { it.type in spawnTypes }) {
                        val mob = category.mobs.randomOrNull() ?: continue

                        if (!conditions.areMet(point.location.toDispatcher(), EmptyProvidedHolder)) {
                            continue
                        }

                        if (randDouble(0.0, 100.0) > chance) {
                            continue
                        }

                        point.spawn(mob, SpawnReason.NATURAL)
                    }
                }
            }
        }
    }
}
