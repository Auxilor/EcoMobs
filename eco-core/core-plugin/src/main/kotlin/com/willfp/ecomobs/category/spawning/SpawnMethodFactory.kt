package com.willfp.ecomobs.category.spawning

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategory
import com.willfp.libreforge.ViolationContext

abstract class SpawnMethodFactory(override val id: String) : KRegistrable {
    abstract fun create(
        category: MobCategory,
        config: Config,
        plugin: EcoMobsPlugin,
        context: ViolationContext
    ): SpawnMethod
}
