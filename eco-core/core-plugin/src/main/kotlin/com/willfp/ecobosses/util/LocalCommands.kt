package com.willfp.ecobosses.util

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecobosses.EcoBossesPlugin
import org.bukkit.Bukkit
import org.bukkit.Location

data class LocalCommands(
    val commands: Iterable<String>,
) {
    fun dispatch(location: Location, topDamagers: List<Damager>) {
        val toDispatch = commands.toMutableList()
        toDispatch.replaceAll {
            var command = it

            for (i in 1..20) {
                val damager = topDamagers.getOrNull(i - 1)
                val damage = if (damager?.damage != null) NumberUtils.format(damager.damage) else
                    EcoBossesPlugin.instance.langYml.getFormattedString("na")
                val player = if (damager?.uuid != null) Bukkit.getOfflinePlayer(damager.uuid).savedDisplayName else
                    EcoBossesPlugin.instance.langYml.getFormattedString("na")

                command = command.replace("%damage_${i}%", damage)
                    .replace("%damage_${i}_player%", player)
            }

            command.replace("%x%", location.blockX.toString())
                .replace("%y%", location.blockY.toString())
                .replace("%z%", location.blockZ.toString())
        }

        for (s in toDispatch) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s)
        }
    }
}
