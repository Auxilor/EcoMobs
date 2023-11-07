package com.willfp.ecomobs.handler

import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.mob.options.ecoMobEgg
import org.bukkit.Location
import org.bukkit.block.Container
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class SpawnEggHandler(
    private val plugin: EcoMobsPlugin
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val location = event.clickedBlock?.location?.add(0.0, 1.5, 0.0) ?: return

        if (!this.handleSpawnEgg(event.item, location, event.player)) {
            return
        }

        val hand = event.hand ?: return

        event.isCancelled = true
        event.setUseItemInHand(Event.Result.DENY)

        val item = event.player.equipment.getItem(hand)
        item.amount -= 1
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockDispenseEvent) {
        val facing = (event.block.blockData as Directional).facing
        // What does the 1.7 do? I don't know, this is from old EcoBosses code.
        val location = event.block.location.add(facing.direction.multiply(1.7))

        if (!this.handleSpawnEgg(event.item, location, null)) {
            return
        }

        event.isCancelled = true

        val dispenser = event.block.state as? Container ?: return

        // This is needed as the event must finish first,
        // Otherwise the dispenser/dropper thinks the item is already removed from this event.
        plugin.scheduler.run {
            val item = dispenser.inventory.find { it?.isSimilar(event.item) == true } ?: return@run
            item.amount--
            dispenser.update()
        }
    }

    private fun handleSpawnEgg(
        item: ItemStack?,
        location: Location,
        player: Player?
    ): Boolean {
        val mob = item?.ecoMobEgg ?: return false

        val egg = mob.spawnEgg ?: return false

        return egg.trySpawn(location, player) != null
    }
}
