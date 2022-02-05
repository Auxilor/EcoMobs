package com.willfp.ecobosses.util

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

data class BossDrop(
    val chance: Double,
    val drops: Collection<ItemStack>
) {
    fun drop(location: Location, player: Player?) {
        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            if (player != null) {
                DropQueue(player)
                    .setLocation(location)
                    .addItems(drops)
                    .push()
            } else {
                for (drop in drops) {
                    location.world?.dropItemNaturally(location, drop)
                }
            }
        }
    }
}

data class CommandReward(
    val chance: Double,
    val commands: Collection<String>
) {
    fun reward(player: Player) {
        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            for (command in commands) {
                Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command.replace("%player%", player.name)
                )
            }
        }
    }
}

data class XpReward(
    val minimum: Int,
    val maximum: Int
) {
    fun modify(event: EntityDeathEvent) {
        event.droppedExp = NumberUtils.randInt(minimum, maximum)
    }
}
