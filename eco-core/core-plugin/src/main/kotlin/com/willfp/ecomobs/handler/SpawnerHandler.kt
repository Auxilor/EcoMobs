package com.willfp.ecomobs.handler

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.event.EcoMobSpawnerBreakEvent
import com.willfp.ecomobs.event.EcoMobSpawnerExplodeEvent
import com.willfp.ecomobs.event.EcoMobSpawnerPickBlockEvent
import com.willfp.ecomobs.event.EcoMobSpawnerPlaceEvent
import com.willfp.ecomobs.event.EcoMobSpawnerUnstackEvent
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.PlacedSpawner
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.SpawnerHolograms
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.applyVanillaSettings
import com.willfp.ecomobs.spawner.isHandledByEcoMobs
import com.willfp.ecomobs.spawner.isTrackedByEcoMobs
import com.willfp.ecomobs.spawner.resolveEntityType
import com.willfp.ecomobs.spawner.spawnFromSpawner
import com.willfp.ecomobs.spawner.spawner
import com.willfp.ecomobs.spawner.toSpawnerItem
import io.papermc.paper.event.player.PlayerPickItemEvent
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldUnloadEvent

object SpawnerHandler : Listener {

    @EventHandler(ignoreCancelled = true)
    fun handlePlace(event: BlockPlaceEvent) {
        // A snapshot, as the placed item is gone by the time the block state exists.
        val placed = event.itemInHand.clone().fast()
        val location = event.block.location

        if (!placed.spawner.isCustomSpawner) {
            // A vanilla spawner still needs tracking when EcoMobs ticks every spawner.
            plugin.scheduler.at(location).run {
                val state = location.block.state as? CreatureSpawner ?: return@run
                if (state.isTrackedByEcoMobs) {
                    PlacedSpawners.set(location, PlacedSpawner(location, null))
                    SpawnerHolograms.refresh(location)
                }
            }
            return
        }

        val mobId = placed.spawner.mob ?: return
        val animId = placed.spawner.particleAnim

        val placeEvent = EcoMobSpawnerPlaceEvent(event.player, location, mobId, placed.spawner.stackSize)
        Bukkit.getPluginManager().callEvent(placeEvent)

        if (placeEvent.isCancelled) {
            event.isCancelled = true
            return
        }

        plugin.scheduler.at(location).run {
            val state = location.block.state as? CreatureSpawner ?: return@run

            placed.spawner.copyTo(state.spawner)
            state.applyVanillaSettings()
            resolveEntityType(mobId)?.let { state.spawnedType = it }
            state.update()

            PlacedSpawners.set(location, PlacedSpawner(location, animId))
            SpawnerHolograms.refresh(location)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleSpawn(event: SpawnerSpawnEvent) {
        val state = event.spawner ?: return

        // Spawners that never fired a place event (/setblock, world edits, pastes) are
        // otherwise only picked up on chunk load, so register them the first time they tick.
        if (state.isTrackedByEcoMobs) {
            val spawnerLocation = state.location

            val added = PlacedSpawners.setIfAbsent(
                spawnerLocation,
                PlacedSpawner(spawnerLocation, state.spawner.particleAnim)
            )

            if (added) {
                SpawnerHolograms.refresh(spawnerLocation)
            }
        }

        // EcoMobs runs its own loop for this spawner, so vanilla's attempt is dropped.
        if (state.isHandledByEcoMobs) {
            event.isCancelled = true
            return
        }

        val noAI = state.spawner.noAI
        val mobId = state.spawner.mob

        // Vanilla spawns the entity itself, so a spawner with no custom mob still has
        // its AI stripped here, before the mob is added to the world.
        if (mobId == null) {
            if (noAI) {
                (event.entity as? LivingEntity)?.setAI(false)
            }

            return
        }

        event.isCancelled = true

        // Vanilla fires this once per mob it wanted to spawn, so the stack multiplies it.
        val stackSize = if (SpawnerStackSettings.enabled) state.spawner.stackSize else 1

        repeat(stackSize) {
            spawnFromSpawner(state.location, event.location, mobId, noAI)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleBreak(event: BlockBreakEvent) {
        val block = event.block
        if (block.type != Material.SPAWNER) return

        val state = block.state as? CreatureSpawner ?: return
        if (!state.spawner.isCustomSpawner) {
            PlacedSpawners.remove(block.location)
            SpawnerHolograms.refresh(block.location)
            return
        }

        val player = event.player

        // Null means the player cannot break it at all, and has been told why.
        val dropsItem = resolvePickup(player, state) ?: run {
            event.isCancelled = true
            return
        }

        val stackSize = state.spawner.stackSize

        // A stacked spawner gives up one spawner per break, or the whole stack on sneak.
        if (SpawnerStackSettings.enabled && stackSize > 1 && !player.isSneaking) {
            event.isCancelled = true

            val unstackEvent = EcoMobSpawnerUnstackEvent(
                player,
                block.location,
                state.spawner.mob,
                stackSize,
                1,
                dropsItem
            )

            Bukkit.getPluginManager().callEvent(unstackEvent)

            if (unstackEvent.isCancelled) {
                return
            }

            // The block stays put on this path, so the stack always keeps at least one.
            val taken = unstackEvent.amount.coerceIn(1, stackSize - 1)

            state.spawner.stackSize = stackSize - taken
            state.update()

            if (unstackEvent.dropsItem) {
                block.world.dropItemNaturally(block.location, state.toSpawnerItem(taken))
            }

            SpawnerHolograms.refresh(block.location)
            return
        }

        val breakEvent = EcoMobSpawnerBreakEvent(
            player,
            block.location,
            state.spawner.mob,
            stackSize,
            dropsItem
        )

        Bukkit.getPluginManager().callEvent(breakEvent)

        if (breakEvent.isCancelled) {
            event.isCancelled = true
            return
        }

        if (breakEvent.dropsItem) {
            event.isDropItems = false
            block.world.dropItemNaturally(block.location, state.toSpawnerItem())
        }

        PlacedSpawners.remove(block.location)
        SpawnerHolograms.refresh(block.location)
    }

    /**
     * Whether breaking the spawner gives an item back, or null if the player cannot
     * break it, in which case they have already been told why.
     */
    private fun resolvePickup(player: Player, state: CreatureSpawner): Boolean? =
        when (state.spawner.pickup) {
            "allow" -> {
                if (player.hasPermission("ecomobs.spawner.pickup")) {
                    true
                } else {
                    player.sendMessage(plugin.langYml.getMessage("spawner-cannot-pickup"))
                    null
                }
            }

            "silk_touch" -> {
                if (!player.hasPermission("ecomobs.spawner.pickup.silktouch")) {
                    player.sendMessage(plugin.langYml.getMessage("spawner-cannot-pickup"))
                    null
                } else {
                    player.inventory.itemInMainHand
                        .itemMeta?.enchants?.containsKey(Enchantment.SILK_TOUCH) == true
                }
            }

            else -> false
        }

    @EventHandler(ignoreCancelled = true)
    fun handlePickBlock(event: PlayerPickItemEvent) {
        val player = event.player
        if (player.gameMode != GameMode.CREATIVE) return
        val target = player.getTargetBlockExact(5) ?: return
        if (target.type != Material.SPAWNER) return
        val state = target.state as? CreatureSpawner ?: return
        if (!state.spawner.isCustomSpawner) return
        val item = state.toSpawnerItem()

        val pickEvent = EcoMobSpawnerPickBlockEvent(player, target.location, state.spawner.mob, item)
        Bukkit.getPluginManager().callEvent(pickEvent)

        if (pickEvent.isCancelled) {
            event.isCancelled = true
            return
        }

        Display.display(item, player)
        plugin.scheduler.on(player).run {
            player.inventory.setItem(player.inventory.heldItemSlot, item)
        }
    }

    @EventHandler
    fun handleChunkLoad(event: ChunkLoadEvent) {
        indexChunk(event.chunk)
    }

    /**
     * Rebuilds the tracked spawners in [chunk] from the chunk's own block entities.
     *
     * Everything previously tracked there is dropped first, so a spawner broken while the
     * chunk was unloaded, or a mid-session config change, can't leave a stale entry behind.
     */
    fun indexChunk(chunk: Chunk) {
        PlacedSpawners.removeChunk(chunk)

        val loaded = mutableListOf<Location>()

        for (blockState in chunk.tileEntities) {
            if (blockState !is CreatureSpawner) continue
            if (!blockState.isTrackedByEcoMobs) continue

            PlacedSpawners.set(
                blockState.location,
                PlacedSpawner(blockState.location, blockState.spawner.particleAnim)
            )

            loaded += blockState.location
        }

        // Held until the whole chunk is tracked, so columns are seen as complete.
        for (location in loaded) {
            SpawnerHolograms.refresh(location)
        }
    }

    /**
     * Indexes every chunk that is already loaded, for the chunks that were loaded before
     * the plugin enabled and so will never fire a [ChunkLoadEvent].
     */
    fun indexLoadedChunks() {
        for (world in Bukkit.getWorlds()) {
            for (chunk in world.loadedChunks) {
                val corner = Location(world, (chunk.x shl 4).toDouble(), 0.0, (chunk.z shl 4).toDouble())

                // Each chunk is read from the thread that owns it, for Folia.
                plugin.scheduler.at(corner).run {
                    // It can have gone away again before this runs, and reading its block
                    // entities would pull it back in.
                    if (chunk.isLoaded) {
                        indexChunk(chunk)
                    }
                }
            }
        }
    }

    @EventHandler
    fun handleChunkUnload(event: ChunkUnloadEvent) {
        // The index deliberately survives the unload; only the holograms, which are real
        // entities in the chunk, go away with it.
        SpawnerHolograms.removeChunk(event.chunk)
    }

    @EventHandler
    fun handleWorldUnload(event: WorldUnloadEvent) {
        PlacedSpawners.removeWorld(event.world)
        SpawnerHolograms.removeWorld(event.world)
    }

    @EventHandler(ignoreCancelled = true)
    fun handleEntityExplosion(event: EntityExplodeEvent) {
        event.blockList().removeIf(::survivesExplosion)
    }

    @EventHandler(ignoreCancelled = true)
    fun handleBlockExplosion(event: BlockExplodeEvent) {
        event.blockList().removeIf(::survivesExplosion)
    }

    /**
     * Whether [block] is taken out of the explosion's block list, which is how a spawner
     * is left standing. Anything that does go up stops being tracked here.
     */
    private fun survivesExplosion(block: Block): Boolean {
        if (block.type != Material.SPAWNER) return false
        val state = block.state as? CreatureSpawner ?: return false

        if (state.spawner.isCustomSpawner) {
            val explodeEvent = EcoMobSpawnerExplodeEvent(
                block.location,
                state.spawner.mob,
                state.spawner.stackSize,
                state.spawner.explosionProof
            )

            Bukkit.getPluginManager().callEvent(explodeEvent)

            if (explodeEvent.isProtected) {
                return true
            }
        }

        PlacedSpawners.remove(block.location)
        SpawnerHolograms.refresh(block.location)
        return false
    }
}
