package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.stacking.stack
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
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
object SpawnerChecks {
    /**
     * The mobs that need light kept low rather than kept out. Vanilla stops a blaze or
     * silverfish spawner at light 12, not at light 1.
     */
    private val DIM_LIGHT_MOBS = setOf(EntityType.BLAZE, EntityType.SILVERFISH)

    private const val DIM_LIGHT_MAX = 11

    /**
     * How much sky light counts as dark. Vanilla rolls this against a random threshold
     * each attempt; a flat value is close enough and doesn't make spawns flicker.
     */
    private const val MAX_SKY_LIGHT = 7

    /**
     * Mobs short enough to fit in a single block, so the block above them being solid
     * doesn't stop them. An approximation of vanilla's hitbox check, which is why the
     * cramped places these spawn in - mineshaft webs, stronghold crawlspaces - still work.
     */
    private val SHORT_MOBS = setOf(
        EntityType.CAVE_SPIDER,
        EntityType.SILVERFISH,
        EntityType.ENDERMITE,
        EntityType.SLIME,
        EntityType.MAGMA_CUBE,
        EntityType.BAT,
        EntityType.CHICKEN,
        EntityType.RABBIT,
        EntityType.BEE,
        EntityType.PARROT,
        EntityType.VEX,
        EntityType.ALLAY,
        EntityType.FROG,
        EntityType.TADPOLE,
        EntityType.TURTLE,
        EntityType.AXOLOTL,
        EntityType.COD,
        EntityType.SALMON,
        EntityType.TROPICAL_FISH,
        EntityType.PUFFERFISH
    )

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

        val ecoMob = EcoMobs[mobId]

        if (ecoMob != null) {
            return nearby.filterIsInstance<Mob>()
                .filter { it.ecoMob == ecoMob }
                .sumOf { it.stack.size }
        }

        val type = entityTypeOrNull(mobId) ?: return 0

        return nearby.filter { it.type == type }
            .sumOf { (it as? Mob)?.stack?.size ?: 1 }
    }

    /**
     * How many more entities the chunk at [location] can take before its spawners stop.
     *
     * This counts entities, not mobs. One stacked mob is one entity, however many mobs
     * it stands for: a stack of 60 counts as 1, the same as a single mob on its own. So
     * a limit of 50 is 50 things in the chunk, which with stacking on can be thousands
     * of mobs.
     *
     * The limit is about how much the server has to tick, and a stack is one thing to
     * tick, which is why it is counted the way it is.
     */
    fun entityBudget(location: Location): Int {
        val max = SpawnerSettings.maxMobsPerChunk

        if (max <= 0) {
            return Int.MAX_VALUE
        }

        val current = location.chunk.entities.count { it is Mob }

        return (max - current).coerceAtLeast(0)
    }

    private fun hasRoomFor(block: Block, type: EntityType?): Boolean {
        if (!block.isPassable) {
            return false
        }

        if (type in SHORT_MOBS) {
            return true
        }

        return block.getRelative(BlockFace.UP).isPassable
    }

    private fun isDarkEnoughFor(block: Block, type: EntityType?): Boolean {
        if (type == null) {
            return true
        }

        if (type in DIM_LIGHT_MOBS) {
            return block.lightLevel <= DIM_LIGHT_MAX
        }

        // Nether and end monsters spawn in any light, so only the overworld's mobs are
        // held to darkness.
        if (block.world.environment != World.Environment.NORMAL) {
            return true
        }

        if (!needsDarkness(type)) {
            return true
        }

        return block.lightFromBlocks <= SpawnerSettings.maxLightLevel &&
                block.lightFromSky <= MAX_SKY_LIGHT
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
