package com.willfp.ecomobs.category

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs

interface MobCategory : KRegistrable {
    val mobs: List<EcoMob>
        get() = EcoMobs.values().filter { it.category == this }

    val spawnMethod: SpawnMethod
}
