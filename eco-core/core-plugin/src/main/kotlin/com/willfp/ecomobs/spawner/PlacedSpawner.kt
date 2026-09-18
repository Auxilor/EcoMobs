package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.event.EcoMobSpawnerTickEvent
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Mob
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
     * Ticks left until the next spawn attempt. Only used in [SpawnerMode.ECOMOBS].
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
     * Runs the EcoMobs spawner loop, [elapsed] ticks on from the last run.
     */
    fun tickSpawning(elapsed: Int) {
        // Like vanilla, a spawner only counts down while a player is close enough.
        if (!isPlayerInRange()) {
            return
        }

        spawnCooldown -= elapsed

        if (spawnCooldown > 0) {
            return
        }

        val state = location.block.state as? CreatureSpawner ?: return

        playerRange = state.requiredPlayerRange
        spawnCooldown = randomDelay(state)

        // A tracked spawner can stop being ours if the config is reloaded under it.
        if (!state.isHandledByEcoMobs) {
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
        val min = state.minSpawnDelay.coerceAtLeast(1)
        val max = state.maxSpawnDelay

        return if (max <= min) min else Random.nextInt(min, max + 1)
    }

    private fun attemptSpawns(state: CreatureSpawner) {
        val mobId = state.spawner.mob ?: state.spawnedType?.name?.lowercase() ?: return

        if (countNearby(state, mobId) >= state.maxNearbyEntities) {
            return
        }

        // A stack of spawners spawns as many mobs as it holds, on the one cycle.
        val stackSize = if (SpawnerStackSettings.enabled) state.spawner.stackSize else 1
        val noAI = state.spawner.noAI

        val tickEvent = EcoMobSpawnerTickEvent(location, mobId, state.spawnCount, stackSize)
        Bukkit.getPluginManager().callEvent(tickEvent)

        if (tickEvent.isCancelled) {
            return
        }

        val toSpawn = tickEvent.spawnCount.coerceAtLeast(0) * tickEvent.stackSize.coerceAtLeast(0)

        repeat(toSpawn) {
            spawnFromSpawner(location, randomSpawnLocation(state.spawnRange), mobId, noAI)
        }
    }

    /**
     * Vanilla's spawn offset, without its light level and block space requirements.
     */
    private fun randomSpawnLocation(spawnRange: Int): Location {
        val x = location.x + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange
        val y = location.y + Random.nextInt(3) - 1
        val z = location.z + 0.5 + (Random.nextDouble() - Random.nextDouble()) * spawnRange

        return Location(location.world, x, y, z)
    }

    private fun countNearby(state: CreatureSpawner, mobId: String): Int {
        val world = location.world ?: return Int.MAX_VALUE

        // Roughly vanilla's check box: the spawn range doubled outwards, a few blocks tall.
        val range = state.spawnRange.toDouble() * 2
        val nearby = world.getNearbyEntities(location, range, 4.0, range)

        val ecoMob = EcoMobs[mobId]

        if (ecoMob != null) {
            return nearby.count { (it as? Mob)?.ecoMob == ecoMob }
        }

        val type = entityTypeOrNull(mobId) ?: return 0

        return nearby.count { it.type == type }
    }
}
