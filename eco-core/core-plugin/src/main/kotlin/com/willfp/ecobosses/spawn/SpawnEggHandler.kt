package com.willfp.ecobosses.spawn

import com.willfp.ecobosses.bosses.bossEgg
import com.willfp.ecobosses.events.BossSpawnEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class SpawnEggHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val item = event.item ?: return
        val boss = item.bossEgg ?: return

        event.isCancelled = true
        event.setUseItemInHand(Event.Result.DENY)

        val location = event.clickedBlock?.location?.add(0.0, 1.5, 0.0) ?: return

        val player = event.player

        if (!boss.spawnConditions.all { it.condition.isConditionMet(player, it.config) }) {
            return
        }

        val spawnEvent = BossSpawnEvent(boss, location, BossSpawnEvent.SpawnReason.EGG)

        Bukkit.getPluginManager().callEvent(spawnEvent)

        if (spawnEvent.isCancelled) {
            return
        }

        if (event.hand == EquipmentSlot.HAND) {
            val hand = event.player.inventory.itemInMainHand
            hand.amount = hand.amount - 1
        } else {
            val hand = event.player.inventory.itemInOffHand
            hand.amount = hand.amount - 1
        }

        boss.spawn(location)
    }
}