package com.willfp.ecomobs.category.spawning

import com.willfp.eco.core.registry.Registry
import com.willfp.ecomobs.category.spawning.impl.SpawnMethodFactoryNatural

object SpawnMethodFactories : Registry<SpawnMethodFactory>() {
    init {
        register(SpawnMethodFactoryNatural)
    }
}
