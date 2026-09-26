package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.event.EcoMobSpawnerTickEvent
import com.willfp.ecomobs.stacking.MobStacks
import com.willfp.ecomobs.stacking.StackSettings
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import kotlin.random.Random

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
    private var spawnCooldown = Random.nextInt(VanillaSpawnerDefaults.delayMin, VanillaSpawnerDefaults.delayMax + 1)

    /**
     * The player range from the last spawn cycle, so the countdown doesn't have to read
     * the block state on every tick.
     */
    private var playerRange = VanillaSpawnerDefaults.playerRange

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
            showParticles(SpawnerSettings.redstoneParticles)
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
        return world.getNearbyEntities(location, range, range, range) { it is Player }
            .any { it.location.distanceSquared(location) <= rangeSquared }
    }

    private fun randomDelay(state: CreatureSpawner): Int {
        val min = state.effectiveDelayMin.coerceAtLeast(1)
        val max = state.effectiveDelayMax

        return if (max <= min) min else Random.nextInt(min, max + 1)
    }

    private fun attemptSpawns(state: CreatureSpawner) {
        when (spawnCycle(state)) {
            CycleResult.SPAWNED -> showParticles(SpawnerSettings.spawnParticles)
            CycleResult.BLOCKED -> showParticles(SpawnerSettings.blockedParticles)
            CycleResult.SKIPPED -> Unit
        }
    }

    /**
     * Runs one spawn cycle. BLOCKED is a cycle the spawn requirements stopped - the
     * nearby cap, the chunk's budget, nowhere to stand - and SKIPPED one that had
     * nothing to spawn or was cancelled by another plugin, which isn't a failure.
     */
    private fun spawnCycle(state: CreatureSpawner): CycleResult {
        val mobId = state.effectiveMob ?: return CycleResult.SKIPPED
        val spawnRange = state.effectiveSpawnRange

        if (SpawnerSettings.checkMaxNearby &&
            SpawnerChecks.countNearby(location, spawnRange, mobId) >= state.effectiveMaxNearby
        ) {
            return CycleResult.BLOCKED
        }

        // A stack of spawners spawns as many mobs as it holds, on the one cycle.
        val stackSize = if (SpawnerStackSettings.enabled) state.spawner.stackSize else 1
        val noAI = state.spawner.noAI

        val tickEvent = EcoMobSpawnerTickEvent(location, mobId, state.effectiveSpawnCount, stackSize)
        Bukkit.getPluginManager().callEvent(tickEvent)

        if (tickEvent.isCancelled) {
            return CycleResult.SKIPPED
        }

        val toSpawn = tickEvent.spawnCount.coerceAtLeast(0) * tickEvent.stackSize.coerceAtLeast(0)

        if (toSpawn <= 0) {
            return CycleResult.SKIPPED
        }

        // How much room the chunk has left for this spawner's mob. Only mobs of the same
        // kind count, and it counts entities, not mobs, so a stack of sixty is one.
        val budget = SpawnerChecks.entityBudget(location, mobId)

        if (budget <= 0) {
            return CycleResult.BLOCKED
        }

        val type = resolveEntityType(mobId)

        // With mob stacking on, the cycle goes in as stack size rather than as an entity
        // per mob, which is what keeps a wall of spawners from filling the world.
        if (StackSettings.enabled) {
            return spawnStacked(mobId, toSpawn, noAI, spawnRange, type)
        }

        var spawnedAny = false

        repeat(minOf(toSpawn, budget)) {
            // A mob with nowhere to go is lost rather than retried elsewhere, which is
            // what vanilla does with a failed attempt.
            val spawnLocation = findSpawnLocation(spawnRange, type) ?: return@repeat

            spawnFromSpawner(location, spawnLocation, mobId, noAI)
            spawnedAny = true
        }

        return if (spawnedAny) CycleResult.SPAWNED else CycleResult.BLOCKED
    }

    /**
     * A puff of [particles] around the spawner, so a player can see what the cycle did
     * rather than wondering whether the spawner is ticking at all.
     *
     * Each particle goes at its own random point in the block, as eco's particles spawn
     * with no spread of their own and would otherwise all land on the one spot.
     */
    private fun showParticles(particles: CycleParticles) {
        val particle = particles.particle

        repeat(particles.amount) {
            particle.spawn(
                location.clone().add(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
            )
        }
    }

    /**
     * Puts the cycle into the nearest stack, or into one new stacked mob when there is
     * no stack nearby to join.
     *
     * Whatever doesn't fit is dropped: a full stack next to the spawner holds it up the
     * same way the nearby cap does, rather than the spawner working around it with more
     * entities.
     */
    private fun spawnStacked(
        mobId: String,
        toSpawn: Int,
        noAI: Boolean,
        spawnRange: Int,
        type: EntityType?
    ): CycleResult {
        if (MobStacks.addToNearbyStack(location, mobId, toSpawn) > 0) {
            return CycleResult.SPAWNED
        }

        val spawnLocation = findSpawnLocation(spawnRange, type) ?: return CycleResult.BLOCKED

        spawnFromSpawner(
            location,
            spawnLocation,
            mobId,
            noAI,
            toSpawn.coerceAtMost(StackSettings.maxSize)
        )

        return CycleResult.SPAWNED
    }

    /**
     * Somewhere within [spawnRange] that [type] can spawn, or null if nothing tried works.
     */
    private fun findSpawnLocation(spawnRange: Int, type: EntityType?): Location? {
        val world = location.world ?: return null

        repeat(SpawnerSettings.spawnAttempts) {
            val candidate = randomSpawnLocation(spawnRange)

            // A spawner on a chunk border can pick a spot in the unloaded chunk next
            // door, and reading its block would load it on the spot.
            if (!world.isChunkLoaded(candidate.blockX shr 4, candidate.blockZ shr 4)) {
                return@repeat
            }

            if (SpawnerChecks.canSpawnAt(candidate, type)) {
                return candidate
            }
        }

        return null
    }

    /**
     * Vanilla's spawn offset: anywhere in the spawn range horizontally, and up to
     * [SpawnerSettings.verticalRange] blocks either side of the spawner vertically.
     */
    private fun randomSpawnLocation(spawnRange: Int): Location {
        val vertical = SpawnerSettings.verticalRange

        val x = location.x + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange
        val y = location.y + Random.nextInt(-vertical, vertical + 1)
        val z = location.z + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange

        return Location(location.world, x, y, z)
    }
}

private enum class CycleResult {
    SPAWNED,
    BLOCKED,
    SKIPPED
}
