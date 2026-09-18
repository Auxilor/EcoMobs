package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.plugin

/**
 * The spawner settings, cached so the hot loop doesn't re-read the config every tick.
 *
 * EcoMobs ticks every spawner itself, so the requirements the server used to apply are
 * applied here instead. Each check is a toggle, and the defaults are what vanilla does,
 * so a server that never touches them keeps vanilla spawners.
 */
object SpawnerSettings {
    var tickRate = 5L
        private set

    /**
     * Whether a mob needs room where it would spawn. Vanilla requires this.
     */
    var checkSpawnSpace = true
        private set

    /**
     * Whether a mob needs solid ground under it. Vanilla spawners do not require this:
     * they drop the surface rule that natural spawning has, so mobs can appear mid-air.
     */
    var requireSolidGround = false
        private set

    /**
     * Whether the spawner holds off once enough of its mob is already nearby.
     */
    var checkMaxNearby = true
        private set

    /**
     * Whether the spawner only counts down while a player is close enough.
     */
    var checkPlayerRange = true
        private set

    /**
     * Whether mobs that need darkness are held to it, so a torch disables a spawner as
     * it does in vanilla.
     */
    var checkLightLevel = true
        private set

    /**
     * The most block light a darkness-spawning mob tolerates. Vanilla is 0.
     */
    var maxLightLevel = 0
        private set

    /**
     * Whether a powered spawner stops spawning.
     */
    var redstoneDeactivates = true
        private set

    /**
     * Whether a spawner with no EcoMobs data has its own settings written into it, so
     * that it can stack, hold a hologram and be picked up like any EcoMobs spawner.
     *
     * Ticking doesn't need this - the loop reads whichever settings a spawner has - so
     * turning it off leaves dungeon spawners exactly as the world generated them.
     */
    var adoptVanillaSpawners = true
        private set

    /**
     * The most entities a chunk can hold before the spawners in it stop.
     *
     * This counts entities, not mobs. One stacked mob is one entity, however many mobs
     * it stands for: a stack of 60 counts as 1, the same as a single mob on its own. So
     * with mob stacking on, a limit of 50 can still mean thousands of mobs in a chunk.
     *
     * The point of the limit is the number of things the server has to tick, and a
     * stack is one thing to tick.
     *
     * 0 turns the limit off.
     */
    var maxMobsPerChunk = 50
        private set

    fun reload() {
        val config = plugin.configYml

        tickRate = config.getInt("spawners.tick-rate").coerceAtLeast(1).toLong()

        checkSpawnSpace = config.getBool("spawners.checks.spawn-space")
        requireSolidGround = config.getBool("spawners.checks.solid-ground")
        checkMaxNearby = config.getBool("spawners.checks.max-nearby")
        checkPlayerRange = config.getBool("spawners.checks.player-range")
        checkLightLevel = config.getBool("spawners.checks.light-level")
        maxLightLevel = config.getInt("spawners.checks.max-light-level").coerceAtLeast(0)

        redstoneDeactivates = config.getBool("spawners.redstone-deactivates")
        maxMobsPerChunk = config.getInt("spawners.max-mobs-per-chunk").coerceAtLeast(0)
        adoptVanillaSpawners = config.getBool("spawners.adopt-vanilla-spawners")

        warnAboutLegacyKeys()
    }

    /**
     * The two keys that used to pick between the vanilla and EcoMobs spawner loops.
     *
     * Configs are only ever added to, so a server that has run an older version still
     * has them sitting there doing nothing. Said out loud rather than quietly mapped
     * onto the new checks, as `mode: ecomobs` meant a set of them the defaults don't.
     */
    private fun warnAboutLegacyKeys() {
        val config = plugin.configYml

        if (!config.has("spawners.mode") && !config.has("spawners.all-spawners")) {
            return
        }

        plugin.logger.warning(
            "spawners.mode and spawners.all-spawners no longer do anything, and can be " +
                    "deleted from config.yml. EcoMobs now ticks every spawner, and the vanilla " +
                    "requirements it applies are the toggles under spawners.checks - all of " +
                    "which default to what vanilla does. If you were running mode: ecomobs, " +
                    "set spawners.checks.light-level and spawners.checks.spawn-space to false " +
                    "to get that behaviour back."
        )
    }
}
