package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.EcoMobsPlugin
import org.bukkit.command.CommandSender

class CommandReload(plugin: EcoMobsPlugin) : Subcommand(
    plugin,
    "reload",
    "ecomobs.command.reload",
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