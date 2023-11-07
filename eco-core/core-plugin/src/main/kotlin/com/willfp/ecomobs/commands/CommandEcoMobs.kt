package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecomobs.EcoMobsPlugin
import org.bukkit.command.CommandSender

class CommandEcoMobs(plugin: EcoMobsPlugin) : PluginCommand(
    plugin,
    "ecomobs",
    "ecomobs.command.ecomobs",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }

    init {
        this.addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandSpawn(plugin))
            .addSubcommand(CommandGive(plugin))
    }
}
