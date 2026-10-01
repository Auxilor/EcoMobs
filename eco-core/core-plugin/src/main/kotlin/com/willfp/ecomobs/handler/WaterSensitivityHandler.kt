package com.willfp.ecomobs.handler

import com.destroystokyo.paper.event.entity.EndermanEscapeEvent
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.entityTypeOrNull
import org.bukkit.Location
import org.bukkit.entity.Enderman
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTeleportEvent

/**
 * The water sensitivity settings, cached so the damage listener doesn't re-read the
 * config on every hit.
 */
object WaterSensitivitySettings {
    var enabled = false
        private set

    /**
     * The mobs that stop taking water damage while this is on.
     */
    var mobs: Set<EntityType> = emptySet()
        private set

    fun reload() {
        val config = plugin.configYml

        enabled = config.getBool("water-sensitivity.enabled")
        mobs = config.getStrings("water-sensitivity.mobs").mapNotNull { name ->
            entityTypeOrNull(name) ?: run {
                plugin.logger.warning("Unknown entity type '$name' in water-sensitivity.mobs, skipping it.")
                null
            }
        }.toSet()
    }
}

/**
 * Stops water hurting the mobs that normally can't stand it - endermen and blazes.
 *
 * Water, rain and bubble columns all hurt these mobs as drowning damage, which is what
 * is cancelled. An enderman teleports away from that damage even when it is cancelled,
 * which [PaperWaterTeleportHandler] or [SpigotWaterTeleportHandler] stops.
 */
object WaterSensitivityHandler : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handleDamage(event: EntityDamageEvent) {
        if (!WaterSensitivitySettings.enabled) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.DROWNING) {
            return
        }

        if (event.entityType !in WaterSensitivitySettings.mobs) {
            return
        }

        event.isCancelled = true
    }

}

/**
 * Stops an enderman teleporting away from water, on Paper, which says why an enderman
 * is teleporting.
 *
 * Only registered on Paper: the event class doesn't exist on Spigot.
 */
object PaperWaterTeleportHandler : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: EndermanEscapeEvent) {
        if (!WaterSensitivitySettings.enabled) {
            return
        }

        if (event.reason != EndermanEscapeEvent.Reason.DROWN) {
            return
        }

        if (event.entityType !in WaterSensitivitySettings.mobs) {
            return
        }

        event.isCancelled = true
    }
}

/**
 * Stops an enderman teleporting away from water, on Spigot, which doesn't say why an
 * enderman is teleporting.
 *
 * Any teleport while the enderman is wet is cancelled instead: in water, or out in the
 * rain. That also holds back the teleports it would make for other reasons while wet -
 * dodging an arrow, getting away from a player - which Paper can tell apart and this
 * can't.
 */
object SpigotWaterTeleportHandler : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: EntityTeleportEvent) {
        if (!WaterSensitivitySettings.enabled) {
            return
        }

        val enderman = event.entity as? Enderman ?: return

        if (enderman.type !in WaterSensitivitySettings.mobs) {
            return
        }

        if (!enderman.isInWater && !isInRain(enderman.location)) {
            return
        }

        event.isCancelled = true
    }

    /**
     * Whether rain is falling on [location]: a storm, and nothing overhead. Close enough
     * to vanilla's check, which also knows which biomes don't get rain.
     */
    private fun isInRain(location: Location): Boolean {
        val world = location.world ?: return false

        if (!world.hasStorm()) {
            return false
        }

        return world.getHighestBlockYAt(location) <= location.blockY
    }
}
