package com.willfp.ecomobs.folia

import com.willfp.eco.core.Eco
import com.willfp.eco.core.Prerequisite
import com.willfp.ecomobs.plugin
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/**
 * Dispatch helpers for running work on the region that owns it.
 *
 * Off Folia every one of these runs the block inline, on the calling thread, which is
 * the point: eco's Bukkit task context is a plain `runTask`, so routing through the
 * scheduler unconditionally would push every dispatched block a tick later on Paper and
 * Spigot. [Eco.isOwnedByCurrentRegion] returns true unconditionally off Folia, so the
 * inline branch is the only one those servers ever take.
 *
 * On Folia the block still runs inline whenever the calling thread already owns the
 * region, so a dispatch only costs a hop when it actually needs one.
 *
 * These are for work that must happen on the owning region. Work that must be *deferred*
 * until after the current event finishes wants [com.willfp.eco.core.scheduling.Scheduler]
 * directly, not these.
 */

/**
 * Run [block] on the thread owning the region containing [location].
 */
internal inline fun atRegion(location: Location, crossinline block: () -> Unit) {
    if (Eco.get().isOwnedByCurrentRegion(location)) {
        block()
    } else {
        plugin.scheduler.at(location).run { block() }
    }
}

/**
 * Run [block] on the thread owning the region containing a chunk.
 */
internal inline fun atRegion(
    world: World,
    chunkX: Int,
    chunkZ: Int,
    crossinline block: () -> Unit
) {
    if (!Prerequisite.HAS_FOLIA.isMet) {
        block()
        return
    }

    // The centre of the chunk, only to ask which region owns it.
    val probe = Location(world, chunkX * 16.0 + 8.0, 0.0, chunkZ * 16.0 + 8.0)

    if (Eco.get().isOwnedByCurrentRegion(probe)) {
        block()
    } else {
        plugin.scheduler.at(world, chunkX, chunkZ).run { block() }
    }
}

/**
 * Run [block] on the thread owning [entity]. Players count as entities here: their
 * inventories, positions and the world immediately around them all belong to the same
 * region.
 */
internal inline fun onEntity(entity: Entity, crossinline block: () -> Unit) {
    if (Eco.get().isOwnedByCurrentRegion(entity)) {
        block()
    } else {
        plugin.scheduler.on(entity).run { block() }
    }
}
