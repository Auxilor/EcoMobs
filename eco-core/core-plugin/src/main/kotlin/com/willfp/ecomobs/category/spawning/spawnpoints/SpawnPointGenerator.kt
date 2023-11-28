package com.willfp.ecomobs.category.spawning.spawnpoints

import com.github.benmanes.caffeine.cache.Caffeine
import com.sun.tools.javac.jvm.ByteCodes.ret
import com.willfp.eco.core.EcoPlugin
import com.willfp.ecomobs.math.Int3
import com.willfp.ecomobs.plugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.Waterlogged
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit

private val spawnPointCache = Caffeine.newBuilder()
    .expireAfterWrite(2, TimeUnit.SECONDS)
    .build<UUID, Set<SpawnPoint>>()

val Player.spawnPoints: Set<SpawnPoint>
    get() = spawnPointCache.get(this.uniqueId) {
        plugin.spawnPointGenerator.generate(this)
    }

class SpawnPointGenerator(
    private val plugin: EcoPlugin
) {
    private val radius = plugin.configYml.getInt("custom-spawning.radius-around-player")
    private val max = plugin.configYml.getInt("custom-spawning.max-points-per-player")
    private val maxAttempts = plugin.configYml.getInt("custom-spawning.max-attempts")
    private val maxMobs = plugin.configYml.getInt("custom-spawning.max-mobs-per-player")

    fun generate(player: Player): Set<SpawnPoint> =
        generate(player.location)

    fun generate(location: Location): Set<SpawnPoint> {
        val points = mutableSetOf<SpawnPoint>()

        val mobsAroundPlayer = location.world
            .getNearbyEntities(location, radius.toDouble(), radius.toDouble(), radius.toDouble())
            .filterIsInstance<Mob>()
            .size

        if (mobsAroundPlayer >= maxMobs) {
            return points
        }

        for (i in 1..max) {
            val point = generateAroundLocation(location) ?: continue
            points.add(point)
        }

        return points
    }

    private fun generateAroundLocation(location: Location): SpawnPoint? {
        val world = location.world ?: return null

        val bottomCorner = Int3(
            location.x.toInt() - radius,
            location.y.toInt() - radius,
            location.z.toInt() - radius
        )

        val topCorner = Int3(
            location.x.toInt() + radius,
            location.y.toInt() + radius,
            location.z.toInt() + radius
        )

        return generateInBox(world, bottomCorner, topCorner)
    }

    private fun generateInBox(world: World, corner1: Int3, corner2: Int3): SpawnPoint? {
        var attempts = 0
        val iterator = (corner1..corner2).randomIterator()

        while (attempts < maxAttempts) {
            attempts++
            val (x, y, z) = iterator.next()

            val block = world.getBlockAt(x, y, z)
            val blockAbove = world.getBlockAt(x, y + 1, z)
            val blockBelow = world.getBlockAt(x, y - 1, z)

            if (blockAbove.isSolid) {
                continue
            }

            // Handle land with a massive boolean expression
            if (
                block.isPassable &&
                !block.isLiquid && block.blockData !is Waterlogged &&
                blockAbove.isPassable &&
                !blockAbove.isLiquid && blockAbove.blockData !is Waterlogged &&
                blockBelow.type.isSolid
            ) {
                return SpawnPoint(block.location.add(0.5, 0.5, 0.5), SpawnPointType.LAND)
            }

            // Handle water
            if (block.type == Material.WATER && blockAbove.type == Material.WATER) {
                return SpawnPoint(block.location.add(0.5, 0.5, 0.5), SpawnPointType.WATER)
            }
        }

        return null
    }
}
