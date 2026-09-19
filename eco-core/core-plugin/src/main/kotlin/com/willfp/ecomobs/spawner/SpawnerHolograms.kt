package com.willfp.ecomobs.spawner

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.titlecase
import com.willfp.ecomobs.folia.atRegion
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The holograms above stacked spawners.
 *
 * Spawners sitting directly on top of each other share a single hologram at the top of
 * the column, summarising every mob in it.
 */
object SpawnerHolograms {
    private val holograms = ConcurrentHashMap<Location, Hologram>()

    // The column each player is currently being shown, for look-at-only mode.
    private val lookingAt = ConcurrentHashMap<UUID, Location>()

    /**
     * Rebuilds every hologram in the column running through [location].
     *
     * Called after anything that changes a column's shape or contents, including the
     * removal of the spawner at [location] itself.
     */
    fun refresh(location: Location) {
        val world = location.world ?: return
        val x = location.blockX
        val z = location.blockZ

        var bottom = location.blockY
        var top = location.blockY

        while (isTracked(world, x, bottom - 1, z)) {
            bottom--
        }

        while (isTracked(world, x, top + 1, z)) {
            top++
        }

        // The span is cleared wholesale, because a removal can split one column in two.
        for (y in bottom..top) {
            remove(blockLocation(world, x, y, z))
        }

        var y = bottom

        while (y <= top) {
            if (!isTracked(world, x, y, z)) {
                y++
                continue
            }

            var columnTop = y

            while (isTracked(world, x, columnTop + 1, z)) {
                columnTop++
            }

            render(blockLocation(world, x, columnTop, z))

            y = columnTop + 1
        }
    }

    /**
     * Rebuilds every hologram, for when the config has changed under them.
     */
    fun reloadAll() {
        clear()

        for (spawner in PlacedSpawners.values()) {
            val location = spawner.location

            if (!location.isWorldLoaded || !location.isChunkLoaded) {
                continue
            }

            // Called on reload, from the global region. Rebuilding a column reads the
            // spawner block states in it, so it runs on the region owning them.
            atRegion(location) {
                refresh(location)
            }
        }
    }

    fun clear() {
        for (hologram in holograms.values) {
            hologram.remove()
        }

        holograms.clear()
        lookingAt.clear()
    }

    /**
     * Drops the holograms in [chunk], which unload with it.
     */
    fun removeChunk(chunk: Chunk) {
        for (location in holograms.keys.toList()) {
            if (!location.isWorldLoaded || location.world != chunk.world) {
                continue
            }

            if (location.blockX shr 4 != chunk.x || location.blockZ shr 4 != chunk.z) {
                continue
            }

            remove(location)
        }
    }

    fun removeWorld(world: World) {
        for (location in holograms.keys.toList()) {
            if (location.isWorldLoaded && location.world == world) {
                remove(location)
            }
        }
    }

    /**
     * Shows the hologram of the column the player is looking at, and hides the one they
     * looked away from. Only used when the hologram is in look-at-only mode.
     */
    fun updateLookAt(player: Player) {
        val target = player.getTargetBlockExact(SpawnerStackSettings.lookAtDistance)
            ?.location
            ?.let { topOf(it) }

        val previous = lookingAt[player.uniqueId]

        if (previous == target) {
            return
        }

        if (previous != null) {
            holograms[previous]?.hide(player)
        }

        if (target == null) {
            lookingAt.remove(player.uniqueId)
            return
        }

        holograms[target]?.show(player)
        lookingAt[player.uniqueId] = target
    }

    private fun render(top: Location) {
        if (!SpawnerStackSettings.enabled || !SpawnerStackSettings.hologramEnabled) {
            return
        }

        // The hologram is a real entity, so it can only exist while the chunk it sits
        // in is loaded. The lines themselves come from the index, not the block states.
        if (!top.isWorldLoaded || !top.isChunkLoaded) {
            return
        }

        val lines = linesFor(top) ?: return

        val hologram = HologramManager.createHologram(
            top.clone().add(0.5, SpawnerStackSettings.hologramHeight, 0.5),
            HologramOptions.builder()
                .contents(lines)
                .visibleByDefault(!SpawnerStackSettings.lookAtOnly)
                .build()
        )

        holograms[top] = hologram
    }

    /**
     * One line per distinct mob in the column, with the stack sizes summed, largest
     * first. Shown for every spawner, including a lone one standing at a stack of one.
     * Null only when nothing in the column names a mob.
     */
    private fun linesFor(top: Location): List<String>? {
        val world = top.world ?: return null
        val x = top.blockX
        val z = top.blockZ

        val sizes = mutableMapOf<String, Int>()

        var y = top.blockY

        while (isTracked(world, x, y, z)) {
            val spawner = PlacedSpawners[blockLocation(world, x, y, z)]
            val mob = spawner?.mobId

            if (mob != null) {
                sizes[mob] = (sizes[mob] ?: 0) + spawner.stackSize.coerceAtLeast(1)
            }

            y--
        }

        if (sizes.isEmpty()) {
            return null
        }

        val lines = sizes.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { (mob, size) ->
                SpawnerStackSettings.hologramLine
                    .replace("%size%", size.toString())
                    .replace("%max_stack_size%", SpawnerStackSettings.maxSize.toString())
                    .replace("%mob%", mob)
                    .replace("%mob_formatted%", mob.replace("_", " ").titlecase())
                    .formatEco()
            }

        val header = SpawnerStackSettings.hologramHeader

        return if (header.isEmpty()) lines else listOf(header.formatEco()) + lines
    }

    /**
     * The top of the column containing [location], or null if nothing is tracked there.
     */
    private fun topOf(location: Location): Location? {
        val world = location.world ?: return null
        val x = location.blockX
        val z = location.blockZ

        if (!isTracked(world, x, location.blockY, z)) {
            return null
        }

        var top = location.blockY

        while (isTracked(world, x, top + 1, z)) {
            top++
        }

        return blockLocation(world, x, top, z)
    }

    private fun remove(location: Location) {
        holograms.remove(location)?.remove()
        lookingAt.values.removeIf { it == location }
    }

    private fun isTracked(world: World, x: Int, y: Int, z: Int) =
        PlacedSpawners.contains(blockLocation(world, x, y, z))

    private fun blockLocation(world: World, x: Int, y: Int, z: Int) =
        Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}
