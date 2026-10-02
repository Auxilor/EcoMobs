package com.willfp.ecomobs.scheduled

import com.willfp.eco.core.Eco
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.folia.atRegion
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.mob.options.ScheduledSpawn
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.isInLoadedChunk
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.toDispatcher
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Mob
import java.util.UUID

/**
 * Spawns every mob's scheduled points from one timer on the global thread.
 *
 * Nothing here loads a chunk: a point whose chunk is not loaded and entity-ticking waits.
 */
object ScheduledSpawner {
    private var task: EcoTask? = null

    fun start() {
        stop()

        task = plugin.scheduler.global().runTimer(20, 20) {
            tick()
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tick() {
        for (mob in EcoMobs.values()) {
            val scheduledSpawn = mob.scheduledSpawn ?: continue

            for (point in scheduledSpawn.points) {
                tickPoint(mob, scheduledSpawn, point)
            }
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

        if (state.mobs.size >= point.maxAlive || System.currentTimeMillis() < state.nextDue) {
            return
        }

        val location = point.location ?: return

        if (!location.isInLoadedChunk) {
            return
        }

        atRegion(location) {
            trySpawn(mob, scheduledSpawn, point, location)
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

        atRegion(world, position.chunkX, position.chunkZ) {
            if (mob.getLivingMob(uuid) != null) {
                return@atRegion
            }

            val chunks = mutableListOf<Chunk>()

            for (offsetX in -1..1) {
                for (offsetZ in -1..1) {
                    val chunkX = position.chunkX + offsetX
                    val chunkZ = position.chunkZ + offsetZ
                    val probe = Location(world, chunkX * 16.0 + 8.0, 0.0, chunkZ * 16.0 + 8.0)

                    if (!Eco.get().isOwnedByCurrentRegion(probe) || !world.isChunkLoaded(chunkX, chunkZ)) {
                        return@atRegion
                    }

                    val chunk = world.getChunkAt(chunkX, chunkZ)

                    if (!chunk.isEntitiesLoaded) {
                        return@atRegion
                    }

                    chunks += chunk
                }
            }

            val entity = chunks.firstNotNullOfOrNull { chunk ->
                chunk.entities.firstOrNull { it.uniqueId == uuid }
            } as? Mob

            val ecoMob = entity?.ecoMob

            if (entity != null && ecoMob != null) {
                ecoMob.getLivingMob(entity)
                ScheduledSpawnState.moved(point.key, uuid, entity.location.toMobPosition())
                return@atRegion
            }

            ScheduledSpawnState.free(point.key, uuid, scheduledSpawn.nextDue())
        }
    }

    private fun trySpawn(mob: EcoMob, scheduledSpawn: ScheduledSpawn, point: ScheduledPoint, location: Location) {
        if (!location.isInLoadedChunk) {
            return
        }

        if (location.world.getChunkAt(location).loadLevel != Chunk.LoadLevel.ENTITY_TICKING) {
            return
        }

        val state = ScheduledSpawnState[point.key]

        if (state.mobs.size >= point.maxAlive || System.currentTimeMillis() < state.nextDue) {
            return
        }

        if (!scheduledSpawn.conditions.areMet(location.toDispatcher(), EmptyProvidedHolder)) {
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

        if (!added) {
            (living as? LivingMobImpl)?.discard()
            return
        }

        sendBroadcast(scheduledSpawn, point, living.displayName, location)
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

        val lines = scheduledSpawn.broadcast.map {
            it.replace("%mob%", name)
                .replace("%point%", point.id)
                .replace("%world%", point.worldName)
                .replace("%x%", location.blockX.toString())
                .replace("%y%", location.blockY.toString())
                .replace("%z%", location.blockZ.toString())
                .formatEco()
        }

        for (player in Bukkit.getOnlinePlayers()) {
            lines.forEach { player.sendMessage(it) }
        }
    }
}
