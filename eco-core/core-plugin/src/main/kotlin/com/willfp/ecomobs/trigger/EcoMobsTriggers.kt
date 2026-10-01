package com.willfp.ecomobs.trigger

import com.willfp.libreforge.triggers.Triggers

/**
 * The triggers EcoMobs adds to libreforge, so effects in any libreforge plugin can
 * listen for them.
 *
 * Triggers are only enabled once a config asks for one by ID, so registering them all
 * up front costs nothing.
 */
object EcoMobsTriggers {
    fun registerAll() {
        Triggers.register(TriggerBreakSpawner)
        Triggers.register(TriggerBuildTotem)
        Triggers.register(TriggerDamageMob)
        Triggers.register(TriggerMobDrops)
        Triggers.register(TriggerPlaceSpawner)
        Triggers.register(TriggerSpawnerSpawn)
        Triggers.register(TriggerStackDeath)
        Triggers.register(TriggerStackMerge)
        Triggers.register(TriggerStackSpawner)
        Triggers.register(TriggerStageChange)
        Triggers.register(TriggerUnstackSpawner)
        Triggers.register(TriggerUseSpawnEgg)
    }
}
