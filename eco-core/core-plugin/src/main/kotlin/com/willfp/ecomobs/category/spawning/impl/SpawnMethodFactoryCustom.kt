package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.util.randDouble
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.ecomobs.category.spawning.spawnpoints.SpawnPointType
import com.willfp.ecomobs.category.spawning.spawnpoints.spawnPoints
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
    ) : SpawnMethod(category, config) {
        private val spawnRate = plugin.configYml.getInt("custom-spawning.spawn-rate").toLong()

        private val spawnTypes = config.getStrings("spawn-types")
            .mapNotNull { enumValueOfOrNull<SpawnPointType>(it.uppercase()) }
            .toSet()

        private val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("conditions")
        )

        private val chance = config.getDouble("chance")

        private var task: EcoTask? = null

        override fun onStart() {
            task = plugin.scheduler.global().runTimer(spawnRate, spawnRate) {
                tick()
            }
        }

        override fun onStop() {
            task?.cancel()
        }

        private fun tick() {
            for (player in Bukkit.getOnlinePlayers()) {
                // Spawn points are found by scanning the blocks around the player, and
                // the mobs then go into the world there, so the whole pass runs on the
                // player's own region rather than the global one.
                onEntity(player) {
                    tickPlayer(player)
                }
            }
        }

        private fun tickPlayer(player: Player) {
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
