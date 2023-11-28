package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.libreforge.ViolationContext

object SpawnMethodFactoryNone : SpawnMethodFactory("replace") {
    override fun create(
        category: MobCategory,
        config: Config,
        plugin: EcoMobsPlugin,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodNone(category, config, plugin)
    }

    class SpawnMethodNone(
        category: MobCategory, config: Config, plugin: EcoMobsPlugin
    ) : SpawnMethod(category, config, plugin) {
        override fun onStart() {

        }

        override fun onStop() {

        }
    }
}
