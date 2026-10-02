package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender

object CommandScheduleList : Subcommand(
    plugin, "list", "ecomobs.command.schedule.list", false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}
