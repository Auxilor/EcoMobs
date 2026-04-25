package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender

object CommandEcoMobs : PluginCommand(
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
        this.addSubcommand(CommandReload)
            .addSubcommand(CommandSpawn)
            .addSubcommand(CommandGive)
            .addSubcommand(CommandSpawner)
    }
}
