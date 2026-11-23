package com.willfp.ecomobs.category.spawning

import com.willfp.ecomobs.category.MobCategory
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.registry.KRegistrable

abstract class SpawnMethodFactory(override val id: String) : KRegistrable {
    abstract fun create(
        category: MobCategory,
        config: Config,
        context: ViolationContext
    ): SpawnMethod
}
