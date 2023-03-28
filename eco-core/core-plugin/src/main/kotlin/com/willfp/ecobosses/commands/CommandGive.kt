package com.willfp.ecobosses.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.stream.Collectors

class CommandGive(plugin: EcoPlugin) : Subcommand(
    plugin,
    "give",
    "ecobosses.command.give",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("needs-player"))
            return
        }
        if (args.size == 1) {
            sender.sendMessage(plugin.langYml.getMessage("needs-boss"))
            return
        }
        var amount = 1
        if (args.size > 2) {
            amount = args[2].toIntOrNull() ?: amount
        }

        val recieverName = args[0]
        val reciever = Bukkit.getPlayer(recieverName)
        if (reciever == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }
        val key = args[1]
        val boss = Bosses.getByID(key)

        if (boss?.spawnEgg == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-boss"))
            return
        }

        var message = plugin.langYml.getMessage("give-success")
        message = message.replace("%boss%", boss.id.key).replace("%recipient%", reciever.name)
        sender.sendMessage(message)

        val itemStack = boss.spawnEgg!!

        itemStack.amount = amount
        reciever.inventory.addItem(itemStack)
    }

    override fun tabComplete(
        sender: CommandSender,
        args: List<String>
    ): List<String> {
        val completions = mutableListOf<String>()
        if (args.isEmpty()) {
            return BOSS_NAMES
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }
                .collect(Collectors.toList()), completions)
            return completions
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(args[1], BOSS_NAMES, completions)
            completions.sort()
            return completions
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(args[2], NUMBERS, completions)
            completions.sortWith { s1: String, s2: String ->
                val t1 = s1.toInt()
                val t2 = s2.toInt()
                t1 - t2
            }
            return completions
        }
        return ArrayList(0)
    }

    companion object {
        /**
         * The cached names.
         */
        private val BOSS_NAMES: List<String>
            get() = Bosses.values().map { it.id.key }


        /**
         * The cached numbers.
         */
        private val NUMBERS = listOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "10",
            "32",
            "64"
        )
    }
}