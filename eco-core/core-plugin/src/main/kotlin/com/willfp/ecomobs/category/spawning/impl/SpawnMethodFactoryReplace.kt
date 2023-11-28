package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.enumValueOfOrNull
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object SpawnMethodFactoryReplace : SpawnMethodFactory("replace") {
    override fun create(
        category: MobCategory,
        config: Config,
        plugin: EcoMobsPlugin,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodReplace(category, config, plugin)
    }

    class SpawnMethodReplace(
        category: MobCategory,
        config: Config,
        plugin: EcoMobsPlugin
    ) : SpawnMethod(category, config, plugin), Listener {
        private val toReplace = config.getStrings("replace")
            .mapNotNull { enumValueOfOrNull<EntityType>(it.uppercase()) }
            .toSet()

        override fun onStart() {
            plugin.eventManager.registerListener(this)
        }

        override fun onStop() {
            plugin.eventManager.unregisterListener(this)
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        fun handle(event: CreatureSpawnEvent) {
            // Ignore custom spawns
            if (event.spawnReason == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                return
            }

            if (event.entityType !in toReplace) {
                return
            }

            if (Entities.isCustomEntity(event.entity)) {
                return
            }

            val mob = category.mobs.randomOrNull() ?: return


            event.entity.remove()
            mob.spawn(event.entity.location, SpawnReason.NATURAL)
        }
    }
}
