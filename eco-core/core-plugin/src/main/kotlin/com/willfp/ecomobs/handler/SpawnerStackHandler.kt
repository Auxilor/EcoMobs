package com.willfp.ecomobs.handler

import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.SpawnerStacks
import com.willfp.ecomobs.spawner.spawner
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object SpawnerStackHandler : Listener {

    /**
     * Right-clicking a spawner while holding a matching one adds to its stack.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.hand != EquipmentSlot.HAND) {
            return
        }

        val block = event.clickedBlock ?: return
        if (block.type != Material.SPAWNER) return

        val state = block.state as? CreatureSpawner ?: return
        val held = event.item ?: return

        val taken = tryAdd(event.player, state, held)
        if (taken <= 0) return

        event.isCancelled = true
        consume(event.player, EquipmentSlot.HAND, taken)
    }

    /**
     * Placing a spawner against a matching one adds to its stack instead of placing.
     *
     * The interact handler above normally gets there first; this covers the case where
     * something else lets the interaction through to a real placement.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePlace(event: BlockPlaceEvent) {
        val against = event.blockAgainst
        if (against.type != Material.SPAWNER) return

        val state = against.state as? CreatureSpawner ?: return

        val taken = tryAdd(event.player, state, event.itemInHand)
        if (taken <= 0) return

        event.isCancelled = true
        consume(event.player, event.hand, taken)
    }

    /**
     * Adds one spawner, or the whole held amount when sneaking.
     */
    private fun tryAdd(player: Player, state: CreatureSpawner, held: ItemStack): Int {
        if (!SpawnerStackSettings.enabled) return 0
        if (held.type != Material.SPAWNER) return 0

        val item = held.clone().fast()
        if (!item.spawner.isCustomSpawner) return 0

        val amount = if (player.isSneaking) held.amount else 1

        return SpawnerStacks.add(state, item, amount)
    }

    private fun consume(player: Player, slot: EquipmentSlot, amount: Int) {
        if (player.gameMode == GameMode.CREATIVE) {
            return
        }

        val item = player.inventory.getItem(slot)

        if (item.type != Material.SPAWNER) {
            return
        }

        // Capped, as a listener on EcoMobSpawnerStackEvent can raise the amount taken
        // above what the player is actually holding.
        item.amount -= amount.coerceAtMost(item.amount)

        player.inventory.setItem(slot, item.takeIf { it.amount > 0 })
    }
}
