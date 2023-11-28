package com.willfp.ecomobs.category

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.LivingMob

interface MobCategory : KRegistrable {
    val mobs: List<EcoMob>
        get() = EcoMobs.values().filter { it.category == this }

    val spawnMethod: SpawnMethod

    val isPersistent: Boolean

    /**
     * Apply category attributes to [mob].
     */
    fun applyToMob(mob: LivingMob)
}
