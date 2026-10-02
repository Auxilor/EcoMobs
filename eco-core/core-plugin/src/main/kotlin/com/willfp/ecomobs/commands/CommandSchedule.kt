package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender

object CommandSchedule : Subcommand(
    plugin, "schedule", "ecomobs.command.schedule", false
) {
    init {
        addSubcommand(CommandScheduleList)
        addSubcommand(CommandScheduleReset)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
