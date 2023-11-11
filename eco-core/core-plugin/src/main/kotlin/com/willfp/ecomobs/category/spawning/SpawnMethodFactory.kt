package com.willfp.ecomobs.category.spawning

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.category.MobCategory

abstract class SpawnMethodFactory(override val id: String) : KRegistrable {
    abstract fun create(category: MobCategory, config: Config, plugin: EcoPlugin): SpawnMethod
}
