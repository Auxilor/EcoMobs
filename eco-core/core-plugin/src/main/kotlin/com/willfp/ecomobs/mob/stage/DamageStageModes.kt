package com.willfp.ecomobs.mob.stage

import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryHealth
import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryHits
import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryTrigger
import wiki.ecohub.core.registry.Registry

object DamageStageModes : Registry<DamageStageModeFactory>() {
    init {
        register(DamageStageModeFactoryHealth)
        register(DamageStageModeFactoryHits)
        register(DamageStageModeFactoryTrigger)
    }
}
