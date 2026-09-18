package com.willfp.ecomobs.spawner

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * A chunk holding tracked spawners.
 *
 * The world is held by UUID rather than by reference, so an unloaded world isn't kept
 * alive by the index.
 */
data class SpawnerChunk(
    val world: UUID,
    val x: Int,
    val z: Int
)

object PlacedSpawners {
    private val loaded = ConcurrentHashMap<Location, PlacedSpawner>()

    /**
     * The same spawners, grouped by the chunk they sit in.
     *
     * The tick loops walk this rather than [loaded] so that on Folia they can dispatch
     * one task per occupied chunk instead of one per spawner.
     */
    private val byChunk = ConcurrentHashMap<SpawnerChunk, ConcurrentHashMap<Location, PlacedSpawner>>()

    fun set(location: Location, spawner: PlacedSpawner) {
        loaded[location] = spawner
        index(location, spawner)
    }

    /**
     * Tracks a spawner unless one is already tracked there, returning whether it was added.
     */
    fun setIfAbsent(location: Location, spawner: PlacedSpawner): Boolean {
        if (loaded.putIfAbsent(location, spawner) != null) {
            return false
        }

        index(location, spawner)
        return true
    }

    fun contains(location: Location): Boolean = loaded.containsKey(location)

    fun remove(location: Location) {
        if (loaded.remove(location) == null) {
            return
        }

        unindex(location)
    }

    fun removeWorld(world: World) {
        val uid = world.uid

        for (chunk in byChunk.keys) {
            if (chunk.world != uid) {
                continue
            }

            // Removed from the index first, so the locations are still reachable even
            // once the world reference behind them has gone.
            val spawners = byChunk.remove(chunk) ?: continue

            for (location in spawners.keys) {
                loaded.remove(location)
            }
        }
    }

    fun clear() {
        loaded.clear()
        byChunk.clear()
    }

    fun values(): Collection<PlacedSpawner> = loaded.values

    /**
     * Runs [action] once per chunk holding tracked spawners, with the spawners in it.
     *
     * Chunks in worlds that have since unloaded are skipped. The collection passed in is
     * the live one, so it must only be read.
     */
    fun forEachChunk(action: (World, Int, Int, Collection<PlacedSpawner>) -> Unit) {
        for ((chunk, spawners) in byChunk) {
            if (spawners.isEmpty()) {
                continue
            }

            val world = Bukkit.getWorld(chunk.world) ?: continue

            action(world, chunk.x, chunk.z, spawners.values)
        }
    }

    private fun index(location: Location, spawner: PlacedSpawner) {
        val chunk = chunkOf(location) ?: return

        byChunk.computeIfAbsent(chunk) { ConcurrentHashMap() }[location] = spawner
    }

    private fun unindex(location: Location) {
        val chunk = chunkOf(location) ?: return

        byChunk.computeIfPresent(chunk) { _, spawners ->
            spawners.remove(location)

            // Dropped wholesale once empty, so the index doesn't grow a key per chunk
            // that ever held a spawner.
            if (spawners.isEmpty()) null else spawners
        }
    }

    private fun chunkOf(location: Location): SpawnerChunk? {
        if (!location.isWorldLoaded) {
            return null
        }

        val world = location.world ?: return null

        return SpawnerChunk(world.uid, location.blockX shr 4, location.blockZ shr 4)
    }
}
