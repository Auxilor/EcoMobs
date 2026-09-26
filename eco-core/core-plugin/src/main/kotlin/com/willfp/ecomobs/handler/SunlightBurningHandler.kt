package com.willfp.ecomobs.handler

import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.entityTypeOrNull
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent

/**
 * The sunlight burning settings, cached so the combust listener doesn't re-read the
 * config every time a mob catches fire.
 */
object SunlightBurningSettings {
    var enabled = false
        private set

    /**
     * The mobs that stop burning in daylight while this is on.
     */
    var mobs: Set<EntityType> = emptySet()
        private set

    fun reload() {
        val config = plugin.configYml

        enabled = config.getBool("sunlight-burning.enabled")
        mobs = config.getStrings("sunlight-burning.mobs").mapNotNull { name ->
            entityTypeOrNull(name) ?: run {
                plugin.logger.warning("Unknown entity type '$name' in sunlight-burning.mobs, skipping it.")
                null
            }
        }.toSet()
    }
}

/**
 * Stops undead mobs catching fire in daylight.
 *
 * Sunlight sets a mob alight with a plain [EntityCombustEvent]. Lava, fire, fire aspect
 * and flaming arrows each fire one of its subclasses instead, so only the exact class is
 * cancelled and a zombie still burns in lava.
 */
object SunlightBurningHandler : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: EntityCombustEvent) {
        if (!SunlightBurningSettings.enabled) {
            return
        }

        if (event.javaClass != EntityCombustEvent::class.java) {
            return
        }

        if (event.entityType !in SunlightBurningSettings.mobs) {
            return
        }

        event.isCancelled = true
    }
}
