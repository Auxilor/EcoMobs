package com.willfp.ecomobs.category.spawning

import com.willfp.ecomobs.category.spawning.impl.SpawnMethodFactoryCustom
import com.willfp.ecomobs.category.spawning.impl.SpawnMethodFactoryNone
import com.willfp.ecomobs.category.spawning.impl.SpawnMethodFactoryReplace
import wiki.ecohub.core.registry.Registry

object SpawnMethodFactories : Registry<SpawnMethodFactory>() {
    init {
        register(SpawnMethodFactoryReplace)
        register(SpawnMethodFactoryNone)
        register(SpawnMethodFactoryCustom)
    }
}
