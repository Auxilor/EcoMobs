package com.willfp.ecobosses.util

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecobosses.EcoBossesPlugin
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

            for (i in 1..20) {
                val damager = topDamagers.getOrNull(i - 1)
                val damage = if (damager?.damage != null) NumberUtils.format(damager.damage) else
                    EcoBossesPlugin.instance.langYml.getFormattedString("na")
                val player = if (damager?.uuid != null) Bukkit.getOfflinePlayer(damager.uuid).savedDisplayName else
                    EcoBossesPlugin.instance.langYml.getFormattedString("na")

                message = message.replace("%damage_${i}%", damage)
                    .replace("%damage_${i}_player%", player)
            }

            message = message.replace("%x%", location.blockX.toString())
                .replace("%y%", location.blockY.toString())
                .replace("%z%", location.blockZ.toString())

            message.formatEco()
        }

        if (radius < 0) {
            for (message in toBroadcast) {
                @Suppress("DEPRECATION")
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
