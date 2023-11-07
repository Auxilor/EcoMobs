package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

private val tilde = listOf("~")

class CommandSpawn(plugin: EcoMobsPlugin) : Subcommand(
    plugin,
    "spawn",
    "ecomobs.command.spawn",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("specify-mob"))
            return
        }

        val mobID = args[0]
        val mob = EcoMobs.getByID(mobID.lowercase())
        if (mob == null) {
            sender.sendMessage(plugin.langYml.getMessage("specify-mob"))
            return
        }

        var location: Location? = null
        if (sender is Player) {
            location = sender.location
        }

        if (args.size >= 4) {
            val xString = args[1]
            val yString = args[2]
            val zString = args[3]
            val xPos: Double
            val yPos: Double
            val zPos: Double
            if (xString.startsWith("~") && sender is Player) {
                val xDiff = xString.replace("~", "")
                val yDiff = yString.replace("~", "")
                val zDiff = zString.replace("~", "")
                xPos = if (xDiff.isEmpty()) {
                    sender.location.x
                } else {
                    try {
                        sender.location.x + xDiff.toDouble()
                    } catch (e: NumberFormatException) {
                        sender.location.x
                    }
                }
                yPos = if (yDiff.isEmpty()) {
                    sender.location.y
                } else {
                    try {
                        sender.location.y + yDiff.toDouble()
                    } catch (e: NumberFormatException) {
                        sender.location.y
                    }
                }
                zPos = if (zDiff.isEmpty()) {
                    sender.location.z
                } else {
                    try {
                        sender.location.z + yDiff.toDouble()
                    } catch (e: NumberFormatException) {
                        sender.location.z
                    }
                }
            } else {
                try {
                    xPos = xString.toDouble()
                    yPos = yString.toDouble()
                    zPos = zString.toDouble()
                } catch (e: NumberFormatException) {
                    sender.sendMessage(plugin.langYml.getMessage("invalid-location"))
                    return
                }
            }
            location = Location(null, xPos, yPos, zPos)
        }
        var world: World? = null
        if (sender is Player) {
            world = sender.world
        }
        if (args.size >= 5) {
            world = Bukkit.getWorld(args[4])
        }

        if (location == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-location"))
            return
        }

        location.world = world

        if (world == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-world"))
            return
        }

        val living = mob.spawn(location, SpawnReason.COMMAND)

        if (living != null) {
            sender.sendMessage(
                plugin.langYml.getMessage("spawned")
                    .replace("%mob%", living.displayName)
            )
        }
    }

    override fun tabComplete(
        sender: CommandSender,
        args: List<String>
    ): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], EcoMobs.values().map { it.id }, completions)
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(args[1], tilde, completions)
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(args[2], tilde, completions)
        }

        if (args.size == 4) {
            StringUtil.copyPartialMatches(args[3], tilde, completions)
        }

        if (args.size == 5) {
            StringUtil.copyPartialMatches(args[4], Bukkit.getWorlds().map { it.name }, completions)
        }

        completions.sort()
        return completions
    }
}
