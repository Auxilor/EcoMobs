package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobEggUseEvent
import com.willfp.ecomobs.mob.options.ecoMobEgg
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit
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
import org.bukkit.inventory.ItemStack

object SpawnEggHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val location = event.clickedBlock?.location?.add(0.0, 1.5, 0.0) ?: return

        if (!this.handleSpawnEgg(event.item, location, event.player, EcoMobEggUseEvent.Source.PLAYER)) {
            return
        }

        val hand = event.hand ?: return

        event.isCancelled = true
        event.setUseItemInHand(Event.Result.DENY)

        val item = event.player.equipment.getItem(hand)
        item.amount -= 1
    }

    /**
     * Cancelled for every EcoMobs egg, even when nothing spawns, or vanilla fires the
     * base spawn egg.
     */
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockDispenseEvent) {
        if (event.item.ecoMobEgg == null) {
            return
        }

        event.isCancelled = true

        val facing = (event.block.blockData as Directional).facing
        val location = event.block.getRelative(facing).location.add(0.5, 0.0, 0.5)

        if (this.handleSpawnEgg(event.item, location, null, EcoMobEggUseEvent.Source.DISPENSER)) {
            this.takeEggFromDispenserNextTick(event)
        }
    }

    private fun takeEggFromDispenserNextTick(event: BlockDispenseEvent) {
        val dispenser = event.block.state as? Container ?: return

        plugin.scheduler.at(event.block.location).run {
            val item = dispenser.inventory.find { it?.isSimilar(event.item) == true } ?: return@run
            item.amount--
            dispenser.update()
        }
    }

    private fun handleSpawnEgg(
        item: ItemStack?,
        location: Location,
        player: Player?,
        source: EcoMobEggUseEvent.Source
    ): Boolean {
        val mob = item?.ecoMobEgg ?: return false

        val egg = mob.spawnEgg ?: return false

        val useEvent = EcoMobEggUseEvent(mob, location, player, source)
        Bukkit.getPluginManager().callEvent(useEvent)

        if (useEvent.isCancelled) {
            return false
        }

        return egg.trySpawn(useEvent.location, player) != null
    }
}
