package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender

object CommandSpawner : Subcommand(
    plugin, "spawner", "ecomobs.command.spawner", false
) {
    init {
        addSubcommand(CommandSpawnerGive)
        addSubcommand(CommandSpawnerModify)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
