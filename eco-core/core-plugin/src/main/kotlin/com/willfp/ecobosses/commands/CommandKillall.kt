package com.willfp.ecobosses.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecobosses.EcoBossesPlugin
import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.command.CommandSender

class CommandKillall(plugin: EcoBossesPlugin) : Subcommand(
    plugin,
    "killall",
    "ecobosses.command.killall",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        val alive = Bosses.getAllAlive()

        for (boss in alive) {
            boss.remove()
        }

        sender.sendMessage(
            plugin.langYml.getMessage("killall").replace("%amount%", alive.size.toString())
        )
    }
}