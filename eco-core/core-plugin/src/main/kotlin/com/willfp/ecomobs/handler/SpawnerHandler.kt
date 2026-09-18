package com.willfp.ecomobs.handler

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.event.EcoMobSpawnerBreakEvent
import com.willfp.ecomobs.event.EcoMobSpawnerExplodeEvent
import com.willfp.ecomobs.event.EcoMobSpawnerPickBlockEvent
import com.willfp.ecomobs.event.EcoMobSpawnerPlaceEvent
import com.willfp.ecomobs.event.EcoMobSpawnerUnstackEvent
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.SpawnerHolograms
import com.willfp.ecomobs.spawner.SpawnerSettings
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.adoptVanillaSettings
import com.willfp.ecomobs.spawner.applyVanillaSettings
import com.willfp.ecomobs.spawner.resolveEntityType
import com.willfp.ecomobs.spawner.spawner
import com.willfp.ecomobs.spawner.toPlacedSpawner
import com.willfp.ecomobs.spawner.toReplicaSpawnerItem
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
            // A vanilla spawner still needs tracking, as EcoMobs ticks every spawner.
            plugin.scheduler.at(location).run {
                val state = location.block.state as? CreatureSpawner ?: return@run

                PlacedSpawners.sync(state)
                SpawnerHolograms.refresh(location)
            }
            return
        }

        val mobId = placed.spawner.mob ?: return

        val perItem = placed.spawner.stackSize.coerceAtLeast(1)

        // Sneaking places the whole held stack at once, mirroring sneak-break taking it all.
        val extraItems = if (SpawnerStackSettings.enabled && event.player.isSneaking) {
            val maxExtra = (SpawnerStackSettings.maxSize / perItem - 1).coerceAtLeast(0)
            (event.itemInHand.amount - 1).coerceIn(0, maxExtra)
        } else {
            0
        }

        val stackSize = perItem * (extraItems + 1)

        val placeEvent = EcoMobSpawnerPlaceEvent(event.player, location, mobId, stackSize)
        Bukkit.getPluginManager().callEvent(placeEvent)

        if (placeEvent.isCancelled) {
            event.isCancelled = true
            return
        }

        plugin.scheduler.at(location).run {
            val state = location.block.state as? CreatureSpawner ?: return@run

            placed.spawner.copyTo(state.spawner)
            state.spawner.stackSize = stackSize
            state.applyVanillaSettings()
            resolveEntityType(mobId)?.let { state.spawnedType = it }
            state.update()

            PlacedSpawners.sync(state)
            SpawnerHolograms.refresh(location)
        }

        if (extraItems > 0) {
            consumeExtra(event, extraItems)
        }
    }

    /**
     * Takes the [extra] spawner items beyond the one vanilla removes for the placement.
     *
     * Deferred a tick, as a later handler (stacking onto the spawner placed against) can
     * still cancel the placement, in which case nothing extra is taken.
     */
    private fun consumeExtra(event: BlockPlaceEvent, extra: Int) {
        val player = event.player

        if (player.gameMode == GameMode.CREATIVE) {
            return
        }

        plugin.scheduler.on(player).run {
            if (event.isCancelled) {
                return@run
            }

            val item = player.inventory.getItem(event.hand) ?: return@run

            if (item.type != Material.SPAWNER) {
                return@run
            }

            item.amount -= extra.coerceAtMost(item.amount)

            player.inventory.setItem(event.hand, item.takeIf { it.amount > 0 })
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handleSpawn(event: SpawnerSpawnEvent) {
        val state = event.spawner ?: return

        // Trial spawners are their own block with their own rules, and EcoMobs has
        // nothing to do with them.
        if (state.block.type != Material.SPAWNER) {
            return
        }

        // Spawners that never fired a place event (/setblock, world edits, pastes) are
        // otherwise only picked up on chunk load, so register them the first time they tick.
        val spawnerLocation = state.location

        val added = PlacedSpawners.setIfAbsent(
            spawnerLocation,
            state.toPlacedSpawner()
        )

        if (added) {
            SpawnerHolograms.refresh(spawnerLocation)
        }

        // EcoMobs runs the loop for every spawner, so vanilla's attempt is dropped.
        event.isCancelled = true
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

            PlacedSpawners.sync(state)

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

        // A vanilla spawner gives back a replica of itself rather than the empty block
        // vanilla would hand over, so what you pick up is what you were looking at - and
        // stacks like the spawners EcoMobs gives out.
        val item = state.toReplicaSpawnerItem()

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
            // Every spawner is tracked, not only EcoMobs' own, as the loop ticks them all.
            if (blockState !is CreatureSpawner) continue

            // Spawners the world generated carry no EcoMobs data, so they are given
            // their own settings as they come into reach. Written once - a spawner that
            // has been adopted is left alone on every later chunk load.
            if (SpawnerSettings.adoptVanillaSpawners && blockState.adoptVanillaSettings()) {
                blockState.update(true, false)
            }

            PlacedSpawners.set(
                blockState.location,
                blockState.toPlacedSpawner()
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
