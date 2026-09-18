package com.willfp.ecomobs.mob.stage

import com.willfp.eco.core.registry.Registry
import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryHealth
import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryHits
import com.willfp.ecomobs.mob.stage.impl.DamageStageModeFactoryTrigger

object DamageStageModes : Registry<DamageStageModeFactory>() {
    init {
        register(DamageStageModeFactoryHealth)
        register(DamageStageModeFactoryHits)
        register(DamageStageModeFactoryTrigger)
    }
}
