package com.willfp.ecomobs.category.impl

import com.willfp.eco.core.config.Configs
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.impl.SpawnMethodFactoryNone
import com.willfp.ecomobs.mob.LivingMob

/**
 * The category of mobs that don't set one: they never spawn naturally and aren't persistent.
 *
 * It is never registered, so it never appears among the configured categories.
 */
object UncategorisedMobCategory : MobCategory {
    override val id = "none"

    override val spawnMethod = SpawnMethodFactoryNone.SpawnMethodNone(this, Configs.empty())

    override val isPersistent = false

    override fun applyToMob(mob: LivingMob) {
    }
}
