package com.willfp.ecobosses.util

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

data class LocalBroadcast(
    val messages: Iterable<String>,
    val radius: Double
) {
    fun broadcast(location: Location, topDamagers: List<Damager>) {
        val toBroadcast = messages.toMutableList()
        toBroadcast.replaceAll {
            var message = it
            for ((index, damager) in topDamagers.withIndex()) {
                message = message.replace("%damage_${index + 1}%", damager.damage.toString())
                    .replace("%damage_${index + 1}_player%", Bukkit.getOfflinePlayer(damager.uuid).savedDisplayName)
            }

            message.formatEco()
        }

        if (radius < 0) {
            for (message in toBroadcast) {
                Bukkit.broadcastMessage(message)
            }
        } else {
            val world = location.world ?: return
            world.getNearbyEntities(location, radius, radius, radius)
                .filterIsInstance<Player>()
                .forEach { player -> toBroadcast.forEach { player.sendMessage(it) } }
        }
    }

    companion object {
        fun fromConfig(config: Config): LocalBroadcast {
            return LocalBroadcast(
                config.getStrings("message"),
                config.getDouble("radius")
            )
        }
    }
}
