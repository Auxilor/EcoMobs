package com.willfp.ecomobs.category.spawning.impl

import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.plugin
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.enumValueOfOrNull
import wiki.ecohub.game.entity.Entities
import wiki.ecohub.math.randDouble

object SpawnMethodFactoryReplace : SpawnMethodFactory("replace") {
    override fun create(
        category: MobCategory,
        config: Config,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodReplace(category, config)
    }

    class SpawnMethodReplace(
        category: MobCategory,
        config: Config
    ) : SpawnMethod(category, config), Listener {
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

            if (randDouble(0.0, 100.0) > config.getDouble("chance")) {
                return
            }

            val mob = category.mobs.randomOrNull() ?: return

            if (mob.spawn(event.entity.location, SpawnReason.NATURAL) != null) {
                event.entity.remove()
            }
        }
    }
}
