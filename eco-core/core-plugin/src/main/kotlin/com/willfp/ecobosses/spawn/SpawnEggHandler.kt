package com.willfp.ecobosses.spawn

import com.willfp.ecobosses.EcoBossesPlugin
import com.willfp.ecobosses.bosses.bossEgg
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.libreforge.conditions.isMet
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
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class SpawnEggHandler(
    private val plugin: EcoBossesPlugin
) : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (!this.handleSpawnEgg(event.item, event.clickedBlock?.location?.add(0.0, 1.5, 0.0), event.player)) {
            return
        }

        event.isCancelled = true
        event.setUseItemInHand(Event.Result.DENY)

        if (event.hand == EquipmentSlot.HAND) {
            val hand = event.player.inventory.itemInMainHand
            hand.amount = hand.amount - 1
        } else {
            val hand = event.player.inventory.itemInOffHand
            hand.amount = hand.amount - 1
        }
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: BlockDispenseEvent) {
        val facing = (event.block.blockData as Directional).facing
        val location = event.block.location.add(facing.direction.multiply(1.7))

        if (!this.handleSpawnEgg(event.item, location, null)) return

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
        location: Location?,
        player: Player?
    ): Boolean {
        val boss = item?.bossEgg ?: return false

        if (location == null) {
            return false
        }

        if (player != null) {
            if (!boss.spawnConditions.isMet(player)) {
                return false
            }
        }

        val spawnEvent = BossSpawnEvent(boss, location, BossSpawnEvent.SpawnReason.EGG, player)
        Bukkit.getPluginManager().callEvent(spawnEvent)

        if (spawnEvent.isCancelled) {
            return false
        }

        boss.spawn(location)

        return true
    }
}
