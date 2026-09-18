package com.willfp.ecomobs.spawner

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Every spawner EcoMobs tracks, whether or not its chunk is loaded right now.
 *
 * The index outlives chunk unloads on purpose: it is rebuilt a chunk at a time whenever
 * one loads, and every consumer skips spawners in unloaded chunks, so a chunk going away
 * and coming back can't lose a spawner. Dropping entries on unload instead would leave
 * every spawner in a chunk that was already loaded when the plugin enabled untracked,
 * as no [org.bukkit.event.world.ChunkLoadEvent] is ever fired for those.
 */
object PlacedSpawners {
    private data class ChunkPos(val world: UUID, val x: Int, val z: Int)

    private val loaded = ConcurrentHashMap<Location, PlacedSpawner>()

    /**
     * The tracked locations in each chunk, so a chunk can be rebuilt without walking
     * every spawner on the server.
     */
    private val byChunk = ConcurrentHashMap<ChunkPos, MutableSet<Location>>()

    fun set(location: Location, spawner: PlacedSpawner) {
        loaded[location] = spawner
        index(location)
    }

    /**
     * Tracks a spawner unless one is already tracked there, returning whether it was added.
     */
    fun setIfAbsent(location: Location, spawner: PlacedSpawner): Boolean {
        if (loaded.putIfAbsent(location, spawner) != null) {
            return false
        }

        index(location)
        return true
    }

    /**
     * The spawner tracked at [location], or null if there isn't one.
     *
     * The spawner is returned whether or not its chunk is loaded, so callers that touch
     * the world still need to check that themselves.
     */
    operator fun get(location: Location): PlacedSpawner? = loaded[location]

    /**
     * Every spawner tracked in [chunk], as an unmodifiable snapshot.
     */
    fun inChunk(chunk: Chunk): Collection<PlacedSpawner> =
        inChunk(chunk.world, chunk.x, chunk.z)

    /**
     * Every spawner tracked in the chunk at [chunkX], [chunkZ] in [world], without
     * needing the chunk itself, and so without loading it.
     *
     * The returned collection is an unmodifiable snapshot: tracking a spawner is done
     * through [set] and [remove], never by writing to this.
     */
    fun inChunk(world: World, chunkX: Int, chunkZ: Int): Collection<PlacedSpawner> {
        val locations = byChunk[ChunkPos(world.uid, chunkX, chunkZ)] ?: return emptyList()

        return Collections.unmodifiableList(locations.mapNotNull { loaded[it] })
    }

    fun contains(location: Location): Boolean = loaded.containsKey(location)

    fun remove(location: Location) {
        if (loaded.remove(location) == null) {
            return
        }

        val pos = chunkPosOf(location) ?: return

        byChunk.computeIfPresent(pos) { _, locations ->
            locations.remove(location)
            locations.ifEmpty { null }
        }
    }

    /**
     * Drops everything tracked in [chunk], for rebuilding it from the chunk's own
     * block entities.
     */
    fun removeChunk(chunk: Chunk) {
        val locations = byChunk.remove(ChunkPos(chunk.world.uid, chunk.x, chunk.z)) ?: return

        for (location in locations) {
            loaded.remove(location)
        }
    }

    fun removeWorld(world: World) {
        loaded.keys.removeIf { it.isWorldLoaded && it.world == world }
        byChunk.keys.removeIf { it.world == world.uid }
    }

    fun clear() {
        loaded.clear()
        byChunk.clear()
    }

    fun values(): Collection<PlacedSpawner> = loaded.values

    private fun index(location: Location) {
        val pos = chunkPosOf(location) ?: return

        byChunk.computeIfAbsent(pos) { ConcurrentHashMap.newKeySet() }
            .add(location)
    }

    private fun chunkPosOf(location: Location): ChunkPos? {
        if (!location.isWorldLoaded) {
            return null
        }

        val world = location.world ?: return null

        return ChunkPos(world.uid, location.blockX shr 4, location.blockZ shr 4)
    }
}
