package com.willfp.ecomobs.stacking

import com.willfp.ecomobs.plugin

/**
 * The stacking settings, cached so the hot loop doesn't re-read the config every tick.
 */
object StackSettings {
    var enabled = false
        private set

    var radius = 8.0
        private set

    var maxSize = 64
        private set

    var sweepRate = 100
        private set

    var killWholeStack = false
        private set

    var hideDeathAnimation = false
        private set

    var matchAge = true
        private set

    var nameplate = ""
        private set

    var blacklist = emptySet<String>()
        private set

    var excludeTamed = true
        private set

    var excludeLeashed = true
        private set

    var excludeMounted = true
        private set

    var excludeRidden = true
        private set

    var excludeNamed = true
        private set

    fun reload() {
        val config = plugin.configYml

        enabled = config.getBool("stacking.enabled")
        radius = config.getDouble("stacking.radius")
        maxSize = config.getInt("stacking.max-size").coerceAtLeast(2)
        sweepRate = config.getInt("stacking.sweep-rate").coerceAtLeast(1)
        killWholeStack = config.getBool("stacking.kill-whole-stack")
        hideDeathAnimation = config.getBool("stacking.hide-death-animation")
        matchAge = config.getBool("stacking.match-age")
        nameplate = config.getString("stacking.nameplate")
        blacklist = config.getStrings("stacking.blacklist").map { it.lowercase() }.toSet()

        excludeTamed = config.getBool("stacking.exclude.tamed")
        excludeLeashed = config.getBool("stacking.exclude.leashed")
        excludeMounted = config.getBool("stacking.exclude.mounted")
        excludeRidden = config.getBool("stacking.exclude.ridden")
        excludeNamed = config.getBool("stacking.exclude.named")
    }
}
