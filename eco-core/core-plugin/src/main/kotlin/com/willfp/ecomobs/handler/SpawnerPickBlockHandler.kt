package com.willfp.ecomobs.handler

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.event.EcoMobSpawnerPickBlockEvent
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.spawner
import com.willfp.ecomobs.spawner.toReplicaSpawnerItem
import io.papermc.paper.event.player.PlayerPickItemEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.inventory.ItemStack

/**
 * What picking the spawner [player] is looking at gives them.
 *
 * A vanilla spawner gives back a replica of itself rather than the empty block vanilla
 * would hand over, so what you pick up is what you were looking at - and stacks like
 * the spawners EcoMobs gives out.
 *
 * Null when there is nothing for EcoMobs to do. [PickResult.cancelled] is true when a
 * plugin cancelled the pick, in which case the pick shouldn't happen at all.
 */
private fun pickSpawner(player: Player): PickResult? {
    if (player.gameMode != GameMode.CREATIVE) return null
    val target = player.getTargetBlockExact(5) ?: return null
    if (target.type != Material.SPAWNER) return null
    val state = target.state as? CreatureSpawner ?: return null

    val item = state.toReplicaSpawnerItem()

    val pickEvent = EcoMobSpawnerPickBlockEvent(player, target.location, state.spawner.mob, item)
    Bukkit.getPluginManager().callEvent(pickEvent)

    if (pickEvent.isCancelled) {
        return PickResult(item, cancelled = true)
    }

    Display.display(item, player)

    return PickResult(item, cancelled = false)
}

private class PickResult(val item: ItemStack, val cancelled: Boolean)

/**
 * Creative pick-block on Paper, which has an event for it.
 *
 * Only registered on Paper: the event class doesn't exist on Spigot.
 */
object PaperSpawnerPickBlockHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPickItemEvent) {
        val player = event.player
        val result = pickSpawner(player) ?: return

        if (result.cancelled) {
            event.isCancelled = true
            return
        }

        plugin.scheduler.on(player).run {
            player.inventory.setItem(player.inventory.heldItemSlot, result.item)
        }
    }
}

/**
 * Creative pick-block on Spigot, which has no event for it.
 *
 * A creative client does its own pick-block and tells the server which item to put in
 * the slot, which arrives as an [InventoryCreativeEvent] carrying a plain spawner. That
 * item is swapped for the replica while the player is looking at a spawner.
 *
 * The server can't tell that apart from taking a blank spawner out of the creative menu
 * while looking at one, so that gives the replica too.
 */
object SpigotSpawnerPickBlockHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: InventoryCreativeEvent) {
        val player = event.whoClicked as? Player ?: return
        val cursor = event.cursor

        if (cursor.type != Material.SPAWNER) return

        // Already carrying EcoMobs data, so it is an EcoMobs spawner item, not a pick.
        if (cursor.fast().spawner.isCustomSpawner) return

        val result = pickSpawner(player) ?: return

        if (result.cancelled) {
            event.isCancelled = true
            return
        }

        event.setCursor(result.item)
    }
}
