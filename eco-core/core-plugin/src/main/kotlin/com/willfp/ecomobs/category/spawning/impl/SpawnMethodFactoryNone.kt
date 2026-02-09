package com.willfp.ecomobs.category.spawning.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.category.spawning.SpawnMethodFactory
import com.willfp.libreforge.ViolationContext

object SpawnMethodFactoryNone : SpawnMethodFactory("none") {
    override fun create(
        category: MobCategory,
        config: Config,
        context: ViolationContext
    ): SpawnMethod {
        return SpawnMethodNone(category, config)
    }

    class SpawnMethodNone(
        category: MobCategory, config: Config
    ) : SpawnMethod(category, config) {
        override fun onStart() {

        }

        override fun onStop() {

        }
    }
}
