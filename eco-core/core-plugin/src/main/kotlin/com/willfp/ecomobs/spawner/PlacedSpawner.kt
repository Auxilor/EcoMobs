package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.event.EcoMobSpawnerTickEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import kotlin.random.Random

/**
 * How many places are tried for a single mob before it is given up on.
 */
private const val SPAWN_ATTEMPTS = 5

/**
 * A spawner EcoMobs tracks.
 *
 * Everything needed to describe the spawner is mirrored here, so anything that only
 * has to name it - holograms, the API - can do that without loading its chunk to read
 * the block state back.
 */
class PlacedSpawner(
    val location: Location,
    val animationId: String?,

    /**
     * The mob the spawner spawns, mirrored from its block state.
     */
    val mobId: String? = null,

    /**
     * How many spawners it stands in for, mirrored from its block state.
     */
    val stackSize: Int = 1
) {
    /**
     * Ticks left until the next spawn attempt.
     */
    private var spawnCooldown = Random.nextInt(SpawnerDefaults.DELAY_MIN, SpawnerDefaults.DELAY_MAX + 1)

    /**
     * The player range from the last spawn cycle, so the countdown doesn't have to read
     * the block state on every tick.
     */
    private var playerRange = SpawnerDefaults.PLAYER_RANGE

    fun tick(tick: Int) {
        val id = animationId?.takeIf { it != "none" } ?: return
        val data = SpawnerAnimations[id] ?: return
        data.animation.spawnParticle(
            location.clone().add(0.5, 0.5, 0.5),
            tick,
            data.particle
        )
    }

    /**
     * Runs the spawner loop, [elapsed] ticks on from the last run.
     */
    fun tickSpawning(elapsed: Int) {
        // Like vanilla, a spawner only counts down while a player is close enough.
        if (SpawnerSettings.checkPlayerRange && !isPlayerInRange()) {
            return
        }

        spawnCooldown -= elapsed

        if (spawnCooldown > 0) {
            return
        }

        val state = location.block.state as? CreatureSpawner ?: return

        playerRange = state.effectivePlayerRange
        spawnCooldown = randomDelay(state)

        // The cycle still runs down while the spawner is switched off, so cutting the
        // power doesn't hand back a spawn that was held.
        if (SpawnerChecks.isDeactivatedByRedstone(location.block)) {
            return
        }

        attemptSpawns(state)
    }

    private fun isPlayerInRange(): Boolean {
        val world = location.world ?: return false
        val range = playerRange.toDouble()
        val rangeSquared = range * range

        // Only the players near the spawner, whose region this tick already owns.
        // world.players would reach players being ticked on other regions.
        return world.getNearbyPlayers(location, range)
            .any { it.location.distanceSquared(location) <= rangeSquared }
    }

    private fun randomDelay(state: CreatureSpawner): Int {
        val min = state.effectiveDelayMin.coerceAtLeast(1)
        val max = state.effectiveDelayMax

        return if (max <= min) min else Random.nextInt(min, max + 1)
    }

    private fun attemptSpawns(state: CreatureSpawner) {
        val mobId = state.effectiveMob ?: return
        val spawnRange = state.effectiveSpawnRange

        if (SpawnerSettings.checkMaxNearby &&
            SpawnerChecks.countNearby(location, spawnRange, mobId) >= state.effectiveMaxNearby
        ) {
            return
        }

        // A stack of spawners spawns as many mobs as it holds, on the one cycle.
        val stackSize = if (SpawnerStackSettings.enabled) state.spawner.stackSize else 1
        val noAI = state.spawner.noAI

        val tickEvent = EcoMobSpawnerTickEvent(location, mobId, state.effectiveSpawnCount, stackSize)
        Bukkit.getPluginManager().callEvent(tickEvent)

        if (tickEvent.isCancelled) {
            return
        }

        val toSpawn = tickEvent.spawnCount.coerceAtLeast(0) * tickEvent.stackSize.coerceAtLeast(0)
        val type = resolveEntityType(mobId)

        repeat(toSpawn) {
            // A mob with nowhere to go is lost rather than retried elsewhere, which is
            // what vanilla does with a failed attempt.
            val spawnLocation = findSpawnLocation(spawnRange, type) ?: return@repeat

            spawnFromSpawner(location, spawnLocation, mobId, noAI)
        }
    }

    /**
     * Somewhere within [spawnRange] that [type] can spawn, or null if nothing tried works.
     */
    private fun findSpawnLocation(spawnRange: Int, type: EntityType?): Location? {
        repeat(SPAWN_ATTEMPTS) {
            val candidate = randomSpawnLocation(spawnRange)

            if (SpawnerChecks.canSpawnAt(candidate, type)) {
                return candidate
            }
        }

        return null
    }

    /**
     * Vanilla's spawn offset: anywhere in the spawn range horizontally, and a block
     * either side of the spawner vertically.
     */
    private fun randomSpawnLocation(spawnRange: Int): Location {
        val x = location.x + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange
        val y = location.y + Random.nextInt(3) - 1
        val z = location.z + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange

        return Location(location.world, x, y, z)
    }
}
