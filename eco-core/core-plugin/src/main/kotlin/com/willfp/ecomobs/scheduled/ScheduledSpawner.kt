package com.willfp.ecomobs.scheduled

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.folia.atRegion
import com.willfp.ecomobs.folia.chunkCentre
import com.willfp.ecomobs.folia.isChunkOwnedByCurrentRegion
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.impl.discard
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.mob.options.ScheduledSpawn
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.isInLoadedChunk
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Mob
import java.util.UUID
import java.util.logging.Level

private const val TICK_INTERVAL = 20L

private fun getEntityLoadedChunksAround(world: World, centreX: Int, centreZ: Int): List<Chunk>? {
    val chunks = mutableListOf<Chunk>()

    for (offsetX in -1..1) {
        for (offsetZ in -1..1) {
            val chunkX = centreX + offsetX
            val chunkZ = centreZ + offsetZ
            val centre = chunkCentre(world, chunkX, chunkZ)
            val isReachable = isChunkOwnedByCurrentRegion(world, chunkX, chunkZ) && centre.isInLoadedChunk
            val chunk = if (isReachable) centre.chunk else null

            if (chunk == null || !chunk.isEntitiesLoaded) {
                return null
            }

            chunks += chunk
        }
    }

    return chunks
}

private fun isEntityTicking(location: Location): Boolean =
    location.isInLoadedChunk && location.chunk.loadLevel == Chunk.LoadLevel.ENTITY_TICKING

/**
 * Spawns every mob's scheduled points from one timer on the global thread.
 *
 * Nothing here loads a chunk: a point whose chunk is not loaded and entity-ticking waits.
 *
 * A failure on one point is logged and that point waits one interval, so it cannot stop the
 * other points or retry every second.
 */
object ScheduledSpawner {
    private var task: EcoTask? = null

    fun start() {
        stop()

        task = plugin.scheduler.global().runTimer(TICK_INTERVAL, TICK_INTERVAL) {
            tick()
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tick() {
        for (mob in EcoMobs.scheduled) {
            val scheduledSpawn = mob.scheduledSpawn ?: continue

            for (point in scheduledSpawn.points) {
                guarded(scheduledSpawn, point) {
                    tickPoint(mob, scheduledSpawn, point)
                }
            }
        }
    }

    private inline fun guarded(scheduledSpawn: ScheduledSpawn, point: ScheduledPoint, block: () -> Unit) {
        runCatching(block).onFailure { exception ->
            plugin.logger.log(
                Level.SEVERE,
                "Scheduled spawn ${point.key} failed, retrying after one interval",
                exception
            )
            ScheduledSpawnState.delay(point.key, scheduledSpawn.nextDue())
        }
    }

    private fun tickPoint(mob: EcoMob, scheduledSpawn: ScheduledSpawn, point: ScheduledPoint) {
        val state = ScheduledSpawnState[point.key]

        for ((uuid, position) in state.mobs) {
            val living = mob.getLivingMob(uuid)

            if (living != null) {
                onEntity(living.entity) {
                    ScheduledSpawnState.moved(point.key, uuid, living.entity.location.toMobPosition())
                }
            } else {
                checkLost(mob, scheduledSpawn, point, uuid, position)
            }
        }

        val location = if (state.canSpawn(point.maxAlive)) point.location else null

        if (location != null && location.isInLoadedChunk) {
            atRegion(location) {
                guarded(scheduledSpawn, point) {
                    trySpawn(mob, scheduledSpawn, point, location)
                }
            }
        }
    }

    private fun checkLost(
        mob: EcoMob,
        scheduledSpawn: ScheduledSpawn,
        point: ScheduledPoint,
        uuid: UUID,
        position: MobPosition
    ) {
        val world = Bukkit.getWorld(position.world) ?: return

        if (!world.isChunkLoaded(position.chunkX, position.chunkZ)) {
            return
        }

        atRegion(world = world, chunkX = position.chunkX, chunkZ = position.chunkZ) {
            if (mob.getLivingMob(uuid) == null) {
                findOrFree(scheduledSpawn, point, uuid, world, position)
            }
        }
    }

    private fun findOrFree(
        scheduledSpawn: ScheduledSpawn,
        point: ScheduledPoint,
        uuid: UUID,
        world: World,
        position: MobPosition
    ) {
        val chunks = getEntityLoadedChunksAround(
            world = world,
            centreX = position.chunkX,
            centreZ = position.chunkZ
        ) ?: return

        val entity = chunks.firstNotNullOfOrNull { chunk ->
            chunk.entities.firstOrNull { it.uniqueId == uuid }
        } as? Mob

        if (entity != null && entity.ecoMob?.getLivingMob(entity) != null) {
            ScheduledSpawnState.moved(point.key, uuid, entity.location.toMobPosition())
        } else {
            ScheduledSpawnState.free(point.key, uuid, scheduledSpawn.nextDue())
        }
    }

    private fun canSpawnAt(scheduledSpawn: ScheduledSpawn, point: ScheduledPoint, location: Location): Boolean =
        isEntityTicking(location) &&
            ScheduledSpawnState[point.key].canSpawn(point.maxAlive) &&
            scheduledSpawn.conditions.areMet(location.toDispatcher(), EmptyProvidedHolder)

    private fun trySpawn(mob: EcoMob, scheduledSpawn: ScheduledSpawn, point: ScheduledPoint, location: Location) {
        if (!canSpawnAt(scheduledSpawn, point, location)) {
            return
        }

        val living = mob.spawn(location, SpawnReason.SCHEDULED)

        if (living == null) {
            ScheduledSpawnState.delay(point.key, scheduledSpawn.nextDue())
            return
        }

        living.entity.isPersistent = true
        living.entity.removeWhenFarAway = false
        living.entity.scheduledPoint = point.key

        val added = ScheduledSpawnState.addIfRoom(
            point.key,
            living.entity.uniqueId,
            location.toMobPosition(),
            point.maxAlive,
            scheduledSpawn.nextDue()
        )

        if (added) {
            sendBroadcast(scheduledSpawn, point, living.displayName, location)
        } else {
            living.discard()
        }
    }

    private fun sendBroadcast(
        scheduledSpawn: ScheduledSpawn,
        point: ScheduledPoint,
        name: String,
        location: Location
    ) {
        if (scheduledSpawn.broadcast.isEmpty()) {
            return
        }

        val lines = scheduledSpawn.broadcast.map { line ->
            line.replace(oldValue = "%mob%", newValue = name)
                .replace(oldValue = "%point%", newValue = point.id)
                .replace(oldValue = "%world%", newValue = point.worldName)
                .replace(oldValue = "%x%", newValue = location.blockX.toString())
                .replace(oldValue = "%y%", newValue = location.blockY.toString())
                .replace(oldValue = "%z%", newValue = location.blockZ.toString())
                .formatEco()
        }

        for (player in Bukkit.getOnlinePlayers()) {
            lines.forEach { player.sendMessage(it) }
        }
    }
}
