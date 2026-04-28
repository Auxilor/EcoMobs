package com.willfp.ecomobs.handler

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.PlacedSpawner
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.applyVanillaSettings
import com.willfp.ecomobs.spawner.entityFromSpawnerKey
import com.willfp.ecomobs.spawner.isCustomSpawner
import com.willfp.ecomobs.spawner.resolveEntityType
import com.willfp.ecomobs.spawner.spawnerDelayMax
import com.willfp.ecomobs.spawner.spawnerDelayMin
import com.willfp.ecomobs.spawner.spawnerExplosionProof
import com.willfp.ecomobs.spawner.spawnerMaxNearby
import com.willfp.ecomobs.spawner.spawnerMob
import com.willfp.ecomobs.spawner.spawnerParticleAnim
import com.willfp.ecomobs.spawner.spawnerPickup
import com.willfp.ecomobs.spawner.spawnerPlayerRange
import com.willfp.ecomobs.spawner.spawnerSpawnCount
import com.willfp.ecomobs.spawner.spawnerSpawnRange
import com.willfp.ecomobs.spawner.toSpawnerItem
import io.papermc.paper.event.player.PlayerPickItemEvent
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType

object SpawnerHandler : Listener {

    @EventHandler(ignoreCancelled = true)
    fun handlePlace(event: BlockPlaceEvent) {
        val fis = event.itemInHand.fast()
        if (!fis.isCustomSpawner) return

        val mobId = fis.spawnerMob ?: return
        val animId = fis.spawnerParticleAnim
        val delayMin = fis.spawnerDelayMin
        val delayMax = fis.spawnerDelayMax
        val spawnCount = fis.spawnerSpawnCount
        val spawnRange = fis.spawnerSpawnRange
        val playerRange = fis.spawnerPlayerRange
        val maxNearby = fis.spawnerMaxNearby
        val pickup = fis.spawnerPickup
        val explosionProof = fis.spawnerExplosionProof
        val location = event.block.location

        plugin.scheduler.run {
            val state = location.block.state as? CreatureSpawner ?: return@run
            state.spawnerMob = mobId
            state.spawnerDelayMin = delayMin
            state.spawnerDelayMax = delayMax
            state.spawnerSpawnCount = spawnCount
            state.spawnerSpawnRange = spawnRange
            state.spawnerPlayerRange = playerRange
            state.spawnerMaxNearby = maxNearby
            state.spawnerPickup = pickup
            state.spawnerParticleAnim = animId
            state.spawnerExplosionProof = explosionProof
            state.applyVanillaSettings()
            resolveEntityType(mobId)?.let { state.spawnedType = it }
            state.update()

            PlacedSpawners.set(location, PlacedSpawner(location, animId))
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleSpawn(event: SpawnerSpawnEvent) {
        val state = event.spawner ?: return
        val mobId = state.spawnerMob ?: return

        event.isCancelled = true
        val location = event.location

        val ecoMob = EcoMobs[mobId]
        if (ecoMob != null) {
            val livingMob = ecoMob.spawn(location, SpawnReason.SPAWNER)
            livingMob?.entity?.persistentDataContainer?.set(entityFromSpawnerKey, PersistentDataType.BYTE, 1)
            return
        }

        val entity = Entities.lookup(mobId).spawn(location)
        entity?.persistentDataContainer?.set(entityFromSpawnerKey, PersistentDataType.BYTE, 1)
    }

    @EventHandler(ignoreCancelled = true)
    fun handleBreak(event: BlockBreakEvent) {
        val block = event.block
        if (block.type != Material.SPAWNER) return

        val state = block.state as? CreatureSpawner ?: return
        if (!state.isCustomSpawner) return

        val player = event.player
        val pickup = state.spawnerPickup

        when (pickup) {
            "deny" -> {
                PlacedSpawners.remove(block.location)
            }

            "allow" -> {
                if (!player.hasPermission("ecomobs.spawner.pickup")) {
                    event.isCancelled = true
                    player.sendMessage(plugin.langYml.getMessage("spawner-cannot-pickup"))
                    return
                }
                event.isDropItems = false
                block.world.dropItemNaturally(block.location, state.toSpawnerItem())
                PlacedSpawners.remove(block.location)
            }

            "silk_touch" -> {
                if (!player.hasPermission("ecomobs.spawner.pickup.silktouch")) {
                    event.isCancelled = true
                    player.sendMessage(plugin.langYml.getMessage("spawner-cannot-pickup"))
                    return
                }
                val hasSilkTouch = player.inventory.itemInMainHand
                    .itemMeta?.enchants?.containsKey(Enchantment.SILK_TOUCH) == true
                if (hasSilkTouch) {
                    event.isDropItems = false
                    block.world.dropItemNaturally(block.location, state.toSpawnerItem())
                }
                PlacedSpawners.remove(block.location)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handlePickBlock(event: PlayerPickItemEvent) {
        val player = event.player
        val target = player.getTargetBlockExact(5) ?: return
        if (target.type != Material.SPAWNER) return
        val state = target.state as? CreatureSpawner ?: return
        if (!state.isCustomSpawner) return
        val item = state.toSpawnerItem()
        Display.display(item, player)
        plugin.scheduler.run {
            player.inventory.setItem(player.inventory.heldItemSlot, item)
        }
    }

    @EventHandler
    fun handleChunkLoad(event: ChunkLoadEvent) {
        for (blockState in event.chunk.tileEntities) {
            if (blockState !is CreatureSpawner) continue
            if (!blockState.isCustomSpawner) continue
            PlacedSpawners.set(
                blockState.location,
                PlacedSpawner(blockState.location, blockState.spawnerParticleAnim)
            )
        }
    }

    @EventHandler
    fun handleChunkUnload(event: ChunkUnloadEvent) {
        for (blockState in event.chunk.tileEntities) {
            if (blockState !is CreatureSpawner) continue
            if (!blockState.isCustomSpawner) continue
            PlacedSpawners.remove(blockState.location)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleEntityExplosion(event: EntityExplodeEvent) {
        event.blockList().removeIf { block ->
            if (block.type != Material.SPAWNER) return@removeIf false
            val state = block.state as? CreatureSpawner ?: return@removeIf false
            if (!state.isCustomSpawner) return@removeIf false
            if (state.spawnerExplosionProof) return@removeIf true
            PlacedSpawners.remove(block.location)
            false
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleBlockExplosion(event: BlockExplodeEvent) {
        event.blockList().removeIf { block ->
            if (block.type != Material.SPAWNER) return@removeIf false
            val state = block.state as? CreatureSpawner ?: return@removeIf false
            if (!state.isCustomSpawner) return@removeIf false
            if (state.spawnerExplosionProof) return@removeIf true
            PlacedSpawners.remove(block.location)
            false
        }
    }
}
