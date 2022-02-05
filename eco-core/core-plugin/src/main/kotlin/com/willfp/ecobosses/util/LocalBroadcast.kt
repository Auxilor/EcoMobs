package com.willfp.ecobosses.util

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

data class LocalBroadcast(
    val messages: Iterable<String>,
    val radius: Double
) {
    fun broadcast(location: Location) {
        if (radius < 0) {
            for (message in messages) {
                Bukkit.broadcastMessage(message)
            }
        } else {
            val world = location.world ?: return
            world.getNearbyEntities(location, radius, radius, radius)
                .filterIsInstance<Player>()
                .forEach { player -> messages.forEach { player.sendMessage(it) } }
        }
    }

    companion object {
        fun fromConfig(config: Config): LocalBroadcast {
            return LocalBroadcast(
                config.getFormattedStrings("message"),
                config.getDouble("radius")
            )
        }
    }
}
