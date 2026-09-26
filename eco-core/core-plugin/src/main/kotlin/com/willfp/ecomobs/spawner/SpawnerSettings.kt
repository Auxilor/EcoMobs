package com.willfp.ecomobs.spawner

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.particle.impl.EmptyParticle
import com.willfp.ecomobs.plugin
import org.bukkit.World
import org.bukkit.entity.EntityType

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
     * The most sky light a darkness-spawning mob tolerates. Vanilla rolls this against a
     * random threshold each attempt; a flat value is close enough and doesn't make
     * spawns flicker.
     */
    var maxSkyLight = 7
        private set

    /**
     * The mobs that need light kept low rather than kept out. Vanilla stops a blaze or
     * silverfish spawner at light 12, not at light 1.
     */
    var dimLightMobs: Set<EntityType> = setOf(EntityType.BLAZE, EntityType.SILVERFISH)
        private set

    /**
     * The most light, block and sky together, a [dimLightMobs] mob tolerates.
     */
    var dimLightMax = 11
        private set

    /**
     * The dimensions darkness-spawning mobs are held to darkness in. Nether and end
     * monsters spawn in any light, so vanilla only applies it to the overworld.
     */
    var lightEnvironments: Set<World.Environment> = setOf(World.Environment.NORMAL)
        private set

    /**
     * Mobs short enough to fit in a single block, so the block above them being solid
     * doesn't stop them. An approximation of vanilla's hitbox check, which is why the
     * cramped places these spawn in - mineshaft webs, stronghold crawlspaces - still work.
     */
    var shortMobs: Set<EntityType> = emptySet()
        private set

    /**
     * How many places are tried for a single mob before it is given up on.
     */
    var spawnAttempts = 10
        private set

    /**
     * How many blocks above and below the spawner a mob can be placed. Vanilla is 1.
     */
    var verticalRange = 1
        private set

    /**
     * Shown on the spawner when a cycle puts mobs into the world.
     */
    var spawnParticles = CycleParticles.NONE
        private set

    /**
     * Shown on the spawner when the spawn requirements stop a cycle: the nearby cap, the
     * chunk's limit, or nowhere with room or the right light.
     */
    var blockedParticles = CycleParticles.NONE
        private set

    /**
     * Shown on the spawner when a cycle comes round while redstone has it switched off.
     */
    var redstoneParticles = CycleParticles.NONE
        private set

    /**
     * Whether a spawner mob keeps the mount or rider vanilla's spawn randomisation can
     * give it: the chicken under a baby zombie, the skeleton on a spider.
     */
    var allowJockeys = false
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
        maxSkyLight = config.getInt("spawners.checks.max-sky-light").coerceAtLeast(0)
        dimLightMobs = config.entityTypes("spawners.checks.dim-light-mobs")
        dimLightMax = config.getInt("spawners.checks.dim-light-max").coerceAtLeast(0)
        lightEnvironments = config.environments("spawners.checks.light-dimensions")
        shortMobs = config.entityTypes("spawners.checks.short-mobs")

        spawnAttempts = config.getInt("spawners.spawn-attempts").coerceAtLeast(1)
        verticalRange = config.getInt("spawners.vertical-range").coerceAtLeast(0)

        spawnParticles = config.cycleParticles("spawners.cycle-particles.spawn")
        blockedParticles = config.cycleParticles("spawners.cycle-particles.blocked")
        redstoneParticles = config.cycleParticles("spawners.cycle-particles.redstone")

        VanillaSpawnerDefaults.reload(config)

        redstoneDeactivates = config.getBool("spawners.redstone-deactivates")
        allowJockeys = config.getBool("spawners.allow-jockeys")
        maxMobsPerChunk = config.getInt("spawners.max-mobs-per-chunk").coerceAtLeast(0)
        adoptVanillaSpawners = config.getBool("spawners.adopt-vanilla-spawners")

        warnAboutLegacyKeys()
    }

    private fun Config.cycleParticles(path: String): CycleParticles {
        if (!getBool("$path.enabled")) {
            return CycleParticles.NONE
        }

        return CycleParticles(
            Particles.lookup(getString("$path.particle")),
            getInt("$path.amount").coerceAtLeast(1)
        )
    }

    private fun Config.entityTypes(path: String): Set<EntityType> =
        getStrings(path).mapNotNull { name ->
            entityTypeOrNull(name) ?: run {
                plugin.logger.warning("Unknown entity type '$name' in $path, skipping it.")
                null
            }
        }.toSet()

    private fun Config.environments(path: String): Set<World.Environment> =
        getStrings(path).mapNotNull { name ->
            World.Environment.entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: run {
                plugin.logger.warning("Unknown dimension '$name' in $path, skipping it.")
                null
            }
        }.toSet()

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

/**
 * A puff of [particle] shown on a spawner at the end of a spawn cycle, so a player can
 * tell what the spawner did - spawned, was held up, or is switched off.
 */
class CycleParticles(
    val particle: SpawnableParticle,
    val amount: Int
) {
    companion object {
        /**
         * Shows nothing, for a puff that is turned off.
         */
        val NONE = CycleParticles(EmptyParticle(), 0)
    }
}

/**
 * The settings a vanilla spawner ticks with: one the world generated, one from `/setblock`
 * or a world edit, or one EcoMobs has adopted.
 *
 * Read live rather than written into each spawner, so a config change and a reload
 * reaches every vanilla spawner, including ones adopted long ago. A spawner whose own
 * values were changed from vanilla's - a map maker's custom NBT - keeps them instead.
 */
object VanillaSpawnerDefaults {
    /**
     * What the server gives a spawner nobody has configured, used to tell a stock
     * spawner from a customised one.
     */
    object Stock {
        const val DELAY_MIN = 200
        const val DELAY_MAX = 800
        const val SPAWN_COUNT = 4
        const val SPAWN_RANGE = 4
        const val PLAYER_RANGE = 16
        const val MAX_NEARBY = 6
    }

    var delayMin = Stock.DELAY_MIN
        private set
    var delayMax = Stock.DELAY_MAX
        private set
    var spawnCount = Stock.SPAWN_COUNT
        private set
    var spawnRange = Stock.SPAWN_RANGE
        private set
    var playerRange = Stock.PLAYER_RANGE
        private set
    var maxNearby = Stock.MAX_NEARBY
        private set

    internal fun reload(config: Config) {
        delayMin = config.getInt("spawners.vanilla-spawners.delay-min").coerceAtLeast(1)
        delayMax = config.getInt("spawners.vanilla-spawners.delay-max").coerceAtLeast(delayMin)
        spawnCount = config.getInt("spawners.vanilla-spawners.spawn-count").coerceAtLeast(0)
        spawnRange = config.getInt("spawners.vanilla-spawners.spawn-range").coerceAtLeast(0)
        playerRange = config.getInt("spawners.vanilla-spawners.player-range").coerceAtLeast(0)
        maxNearby = config.getInt("spawners.vanilla-spawners.max-nearby").coerceAtLeast(0)
    }
}
