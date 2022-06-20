package com.willfp.ecobosses.util

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecobosses.bosses.EcoBoss
import com.willfp.ecobosses.events.BossTryDropItemEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

data class BossDrop(
    val chance: Double,
    val drops: Collection<ItemStack>
) {
    fun drop(boss: EcoBoss, location: Location, player: Player?) {
        val event = BossTryDropItemEvent(boss, location, drops.toMutableList(), chance, player)

        Bukkit.getPluginManager().callEvent(event)

        if (NumberUtils.randFloat(0.0, 100.0) < event.chance) {
            if (player != null) {
                DropQueue(player)
                    .setLocation(event.location)
                    .addItems(event.items)
                    .push()
            } else {
                for (drop in event.items) {
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

data class MultipleRewards(
    val chance: Double,
    val message: String,
    val messageRadius: Double,
    val commands: Collection<String>,
    val items: Collection<ItemStack>,
    val xpMinimum: Int,
    val xpMaximum: Int
) {
    fun reward(player: Player): Boolean {
        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            //Run commands for a player
            for (command in commands) {
                Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command.replace("%player%", player.name)
                )
            }

            //Drop items to a player
            DropQueue(player)
                .setLocation(player.location)
                .addItems(items)
                .push()

            //Give XP to a player
            player.giveExp(NumberUtils.randInt(xpMinimum, xpMaximum))

            //Broadcast message to nearby players
            if(messageRadius > 0){
                for (nearbyPlayer in player.getNearbyEntities(messageRadius, messageRadius, messageRadius)
                    .filterIsInstance<Player>())
                {
                    nearbyPlayer.sendMessage(message.replace("%player%", player.name).formatEco())
                }
                player.sendMessage(message.replace("%player%", player.name).formatEco())
            }

            return true
        }
        return false
    }
}