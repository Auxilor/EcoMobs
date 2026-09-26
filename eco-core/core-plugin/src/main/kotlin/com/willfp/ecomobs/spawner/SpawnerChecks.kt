package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.stacking.stack
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.entity.Monster

/**
 * The requirements a spawner spawn has to meet, which EcoMobs applies itself now that
 * it ticks every spawner in place of the server.
 *
 * A vanilla spawner runs a relaxed version of the ordinary spawn check: the mob still
 * needs room and still has to obey its own light rule, but the surface requirement that
 * natural spawning has is dropped, which is why spawner mobs can appear mid-air.
 */
/**
 * Whether the chunk this location is in is loaded, without loading it to find out.
 *
 * Paper has this as Location.isChunkLoaded; Spigot doesn't, so it is read from the world.
 */
val Location.isInLoadedChunk: Boolean
    get() = world?.isChunkLoaded(blockX shr 4, blockZ shr 4) == true

object SpawnerChecks {
    /**
     * Whether the spawner at [block] is switched off by redstone.
     */
    fun isDeactivatedByRedstone(block: Block): Boolean =
        SpawnerSettings.redstoneDeactivates && (block.isBlockPowered || block.isBlockIndirectlyPowered)

    /**
     * Whether [location] is somewhere [type] can spawn.
     *
     * [type] is null for a mob that can't be resolved to an entity type, in which case
     * only the checks that don't depend on what is spawning are applied.
     */
    fun canSpawnAt(location: Location, type: EntityType?): Boolean {
        val block = location.block

        if (SpawnerSettings.checkSpawnSpace && !hasRoomFor(block, type)) {
            return false
        }

        if (SpawnerSettings.requireSolidGround && !block.getRelative(BlockFace.DOWN).type.isSolid) {
            return false
        }

        if (SpawnerSettings.checkLightLevel && !isDarkEnoughFor(block, type)) {
            return false
        }


        return true
    }

    /**
     * How many of [mobId] are already around the spawner at [location].
     *
     * Roughly vanilla's check box: the spawn range doubled outwards, a few blocks tall.
     * A stacked mob counts as every mob it stands in for, as otherwise a stack of sixty
     * reads as one and the cap is never reached.
     */
    fun countNearby(location: Location, spawnRange: Int, mobId: String): Int {
        val world = location.world ?: return Int.MAX_VALUE

        val range = spawnRange.toDouble() * 2
        val nearby = world.getNearbyEntities(location, range, 4.0, range)

        val matches = mobMatcher(mobId) ?: return 0

        return nearby.filter(matches)
            .sumOf { (it as? Mob)?.stack?.size ?: 1 }
    }

    /**
     * How many more of [mobId] the chunk at [location] can take before the spawners for
     * that mob in it stop.
     *
     * Only mobs of the spawner's own kind count, matched the same way as [countNearby]:
     * the same EcoMob, or for a vanilla spawner the same entity type. A zombie farm
     * filling a chunk doesn't hold up the skeleton spawner next to it, and a player's
     * pets and villagers never stop a spawner at all.
     *
     * This counts entities, not mobs. One stacked mob is one entity, however many mobs
     * it stands for: a stack of 60 counts as 1, the same as a single mob on its own. So
     * a limit of 50 is 50 things in the chunk, which with stacking on can be thousands
     * of mobs.
     *
     * The limit is about how much the server has to tick, and a stack is one thing to
     * tick, which is why it is counted the way it is.
     */
    fun entityBudget(location: Location, mobId: String): Int {
        val max = SpawnerSettings.maxMobsPerChunk

        if (max <= 0) {
            return Int.MAX_VALUE
        }

        val matches = mobMatcher(mobId) ?: return max

        val current = location.chunk.entities.count { it is Mob && matches(it) }

        return (max - current).coerceAtLeast(0)
    }

    /**
     * Whether an entity is what a spawner set to [mobId] spawns: that EcoMob, or for a
     * plain entity type, any entity of that type. Null when [mobId] is neither.
     */
    private fun mobMatcher(mobId: String): ((Entity) -> Boolean)? {
        val ecoMob = EcoMobs[mobId]

        if (ecoMob != null) {
            return { it is Mob && it.ecoMob == ecoMob }
        }

        val type = entityTypeOrNull(mobId) ?: return null

        return { it.type == type }
    }

    private fun hasRoomFor(block: Block, type: EntityType?): Boolean {
        if (!block.isPassable) {
            return false
        }

        if (type in SpawnerSettings.shortMobs) {
            return true
        }

        return block.getRelative(BlockFace.UP).isPassable
    }

    private fun isDarkEnoughFor(block: Block, type: EntityType?): Boolean {
        if (type == null) {
            return true
        }

        if (type in SpawnerSettings.dimLightMobs) {
            return block.lightLevel <= SpawnerSettings.dimLightMax
        }

        // Nether and end monsters spawn in any light, so by default only the overworld's
        // mobs are held to darkness.
        if (block.world.environment !in SpawnerSettings.lightEnvironments) {
            return true
        }

        if (!needsDarkness(type)) {
            return true
        }

        // lightLevel is the brightness vanilla's darkness check reads: sky light dimmed
        // for the time of day, and block light, whichever is higher. lightFromSky is
        // the raw sky light, which is 15 under open sky at midnight as well as noon, and
        // so would hold every outdoor spawner off forever.
        return block.lightFromBlocks <= SpawnerSettings.maxLightLevel &&
                block.lightLevel <= SpawnerSettings.maxSkyLight
    }

    /**
     * Whether [type] is one of the mobs that only spawns in the dark. Animals, villagers
     * and the rest ignore light entirely, as they do from a vanilla spawner.
     */
    private fun needsDarkness(type: EntityType): Boolean {
        val entityClass = type.entityClass ?: return false

        return Monster::class.java.isAssignableFrom(entityClass)
    }
}
