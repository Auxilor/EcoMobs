package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.plugin

/**
 * The spawner stacking settings, cached so the display loop doesn't re-read the config.
 */
object SpawnerStackSettings {
    var enabled = false
        private set

    var maxSize = 64
        private set

    var hologramEnabled = true
        private set

    var lookAtOnly = false
        private set

    var lookAtDistance = 5
        private set

    var hologramHeight = 1.5
        private set

    var hologramLine = ""
        private set

    var hologramHeader = ""
        private set

    fun reload() {
        val config = plugin.configYml

        enabled = config.getBool("spawner-stacking.enabled")
        maxSize = config.getInt("spawner-stacking.max-size").coerceAtLeast(2)

        hologramEnabled = config.getBool("spawner-stacking.hologram.enabled")
        lookAtOnly = config.getBool("spawner-stacking.hologram.look-at-only")
        lookAtDistance = config.getInt("spawner-stacking.hologram.look-at-distance").coerceAtLeast(1)
        hologramHeight = config.getDouble("spawner-stacking.hologram.height")
        hologramLine = config.getString("spawner-stacking.hologram.line")
        hologramHeader = config.getString("spawner-stacking.hologram.header")
    }
}
