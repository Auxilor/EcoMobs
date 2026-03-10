package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.options.SpawnerSettings
import com.willfp.ecomobs.mob.options.ecoMobSpawner
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.SpawnerSpawnEvent

object SpawnSpawnerHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockPlaceEvent) {
        if (event.blockPlaced.type != Material.SPAWNER) {
            return
        }

        val mob = event.itemInHand.ecoMobSpawner ?: return
        val spawnerOptions = mob.spawnerOptions ?: return

        if (!spawnerOptions.canPlace(event.player)) {
            event.isCancelled = true
            return
        }

        val state = event.blockPlaced.state as? CreatureSpawner ?: return
        state.ecoMobSpawner = mob

        // Spawn a temporary entity to get the configured type for display
        val tempLocation = event.blockPlaced.location.add(0.0, 1.0, 0.0)
        val tempEntity = mob.spawn(tempLocation, SpawnReason.COMMAND)?.entity
        if (tempEntity != null) {
            state.spawnedType = tempEntity.type
            tempEntity.remove()
        }

        state.applySettings(spawnerOptions.settings)
        state.update(true, false)
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: SpawnerSpawnEvent) {
        val spawner = event.spawner ?: return
        val mob = spawner.ecoMobSpawner ?: return

        event.isCancelled = true
        event.entity.remove()
        mob.spawn(event.entity.location, SpawnReason.SPAWNER)
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        val spawner = event.block.state as? CreatureSpawner ?: return
        val mob = spawner.ecoMobSpawner ?: return

        event.isDropItems = false
        event.expToDrop = 0

        if (event.player.gameMode == GameMode.CREATIVE) {
            return
        }

        val item = mob.spawnerOptions?.item?.item?.clone() ?: return
        event.block.world.dropItemNaturally(event.block.location.add(0.5, 0.5, 0.5), item)
    }

    private fun CreatureSpawner.applySettings(settings: SpawnerSettings) {
        this.minSpawnDelay = settings.minSpawnDelay
        this.maxSpawnDelay = settings.maxSpawnDelay
        this.spawnCount = settings.spawnCount
        this.maxNearbyEntities = settings.maxNearbyEntities
        this.requiredPlayerRange = settings.requiredPlayerRange
        this.spawnRange = settings.spawnRange
    }
}

