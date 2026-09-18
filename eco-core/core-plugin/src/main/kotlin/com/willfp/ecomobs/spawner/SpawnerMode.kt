package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.plugin
import com.willfp.libreforge.enumValueOfOrNull
import org.bukkit.block.CreatureSpawner

/**
 * How spawners are ticked.
 */
enum class SpawnerMode {
    /**
     * The server ticks spawners, and EcoMobs only replaces what they spawn. Light
     * levels, block space, and every other vanilla spawn requirement still apply.
     */
    VANILLA,

    /**
     * EcoMobs ticks spawners itself, ignoring light levels and block space.
     */
    ECOMOBS
}

/**
 * The spawner settings, cached so the hot loop doesn't re-read the config every tick.
 */
object SpawnerSettings {
    var mode = SpawnerMode.VANILLA
        private set

    var allSpawners = false
        private set

    var tickRate = 5L
        private set

    fun reload() {
        mode = enumValueOfOrNull<SpawnerMode>(plugin.configYml.getString("spawners.mode").uppercase())
            ?: SpawnerMode.VANILLA
        allSpawners = plugin.configYml.getBool("spawners.all-spawners")
        tickRate = plugin.configYml.getInt("spawners.tick-rate").coerceAtLeast(1).toLong()
    }
}

/**
 * Whether EcoMobs ticks this spawner in place of the server.
 */
val CreatureSpawner.isHandledByEcoMobs: Boolean
    get() = SpawnerSettings.mode == SpawnerMode.ECOMOBS &&
            (SpawnerSettings.allSpawners || spawner.isCustomSpawner)

/**
 * Whether this spawner belongs in [PlacedSpawners]. EcoMobs spawners are always
 * tracked for their particle animations; vanilla ones only when EcoMobs ticks them.
 */
val CreatureSpawner.isTrackedByEcoMobs: Boolean
    get() = spawner.isCustomSpawner || isHandledByEcoMobs
