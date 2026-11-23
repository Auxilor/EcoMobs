package com.willfp.ecomobs.category

import com.willfp.ecomobs.category.spawning.SpawnMethod
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.LivingMob
import wiki.ecohub.core.registry.KRegistrable

interface MobCategory : KRegistrable {
    val mobs: List<EcoMob>
        get() = EcoMobs.inCategory(this)

    val spawnMethod: SpawnMethod

    val isPersistent: Boolean

    /**
     * Apply category attributes to [mob].
     */
    fun applyToMob(mob: LivingMob)
}
