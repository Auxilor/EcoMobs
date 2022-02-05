package com.willfp.ecobosses.util

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class BossDrop(
    val chance: Double,
    val drops: Collection<ItemStack>
) {
    fun drop(location: Location, player: Player) {
        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            DropQueue(player)
                .setLocation(location)
                .addItems(drops)
                .push()
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
    fun drop(location: Location, player: Player) {
        DropQueue(player)
            .setLocation(location)
            .addXP(NumberUtils.randInt(minimum, maximum))
            .push()
    }
}
