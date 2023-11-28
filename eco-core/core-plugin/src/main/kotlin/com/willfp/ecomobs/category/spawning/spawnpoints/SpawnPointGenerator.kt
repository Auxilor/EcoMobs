package com.willfp.ecomobs.category.spawning.spawnpoints

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.ecomobs.math.Int3
import com.willfp.ecomobs.plugin
import org.bukkit.Material
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

    fun generate(player: Player): Set<SpawnPoint> {
        val points = mutableSetOf<SpawnPoint>()

        val mobsAroundPlayer = player.location.world
            .getNearbyEntities(player.location, radius.toDouble(), radius.toDouble(), radius.toDouble())
            .filterIsInstance<Mob>()
            .size

        if (mobsAroundPlayer >= maxMobs) {
            return points
        }

        for (i in 1..max) {
            val point = generatePoint(player) ?: continue
            points.add(point)
        }

        return points
    }

    private fun generatePoint(player: Player): SpawnPoint? {
        val playerLocation = player.location
        val world = playerLocation.world ?: return null

        val bottomCorner = Int3(
            playerLocation.x.toInt() - radius, playerLocation.y.toInt() - radius, playerLocation.z.toInt() - radius
        )

        val topCorner = Int3(
            playerLocation.x.toInt() + radius, playerLocation.y.toInt() + radius, playerLocation.z.toInt() + radius
        )

        var attempts = 0
        val iterator = (bottomCorner..topCorner).randomIterator()

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
