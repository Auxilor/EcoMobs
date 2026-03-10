package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.StringUtils
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

private val spawnerAmounts = listOf(1, 2, 3, 4, 5).map { it.toString() }

object CommandGiveSpawner : Subcommand(
    plugin,
    "givespawner",
    "ecomobs.command.givespawner",
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
            sender.sendMessage(plugin.langYml.getMessage("specify-mob"))
            return
        }

        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1

        val recipient = Bukkit.getPlayer(args[0])

        if (recipient == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val mob = EcoMobs[args[1]]
        val spawner = mob?.spawnerOptions

        if (mob == null || spawner == null) {
            sender.sendMessage(plugin.langYml.getMessage("specify-mob"))
            return
        }

        sender.sendMessage(
            plugin.langYml.getMessage("gave-spawner", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%mob%", mob.id)
                .replace("%player%", recipient.name)
        )

        DropQueue(recipient)
            .addItem(spawner.item.item.apply { setAmount(amount) })
            .forceTelekinesis()
            .push()
    }

    override fun tabComplete(
        sender: CommandSender,
        args: List<String>
    ): List<String> {
        val completions = mutableListOf<String>()


        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                EcoMobs.values().filter { it.spawnerOptions != null }.map { it.id },
                completions
            )
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(
                args[2],
                spawnerAmounts,
                completions
            )
        }

        completions.sort()
        return completions
    }
}

