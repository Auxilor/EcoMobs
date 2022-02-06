package com.willfp.ecobosses.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecobosses.EcoBossesPlugin
import org.bukkit.command.CommandSender

class CommandReload(plugin: EcoBossesPlugin) : Subcommand(
    plugin,
    "reload",
    "ecobosses.command.reload",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        plugin.reload()
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
    }
}