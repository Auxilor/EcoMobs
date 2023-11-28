package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
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

object SpawnMethodFactoryNone : SpawnMethodFactory("replace") {
    override fun create(
        category: MobCategory,
        config: Config,
        plugin: EcoPlugin,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodNone(category, config, plugin)
    }

    class SpawnMethodNone(
        category: MobCategory, config: Config, plugin: EcoPlugin
    ) : SpawnMethod(category, config, plugin) {
        override fun onStart() {

        }

        override fun onStop() {

        }
    }
}
