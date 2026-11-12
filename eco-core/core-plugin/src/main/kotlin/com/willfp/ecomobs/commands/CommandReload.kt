package com.willfp.ecomobs.commands

import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.ui.text.StringUtils

object CommandReload : EcoMobsSubcommand(
    "reload",
    "ecomobs.command.reload",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        sender.sendMessage(
            plugin.langYml.getMessage("reloaded", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%time%", plugin.reloadWithTime().toNiceString())
                .replace("%count%", EcoMobs.values().size.toString())
        )
    }
}