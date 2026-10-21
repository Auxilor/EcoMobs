package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.scheduled.ScheduledPoint
import com.willfp.ecomobs.scheduled.ScheduledSpawnState
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandScheduleList : Subcommand(
    plugin,
    "list",
    "ecomobs.command.schedule.list",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        val mobs = if (args.isEmpty()) {
            EcoMobs.values().filter { it.scheduledSpawn != null }
        } else {
            val mob = EcoMobs[args[0].lowercase()]

            if (mob?.scheduledSpawn == null) {
                sender.sendMessage(plugin.langYml.getMessage("schedule-invalid-mob"))
                return
            }

            listOf(mob)
        }

        val points = mobs.flatMap { it.scheduledSpawn?.points.orEmpty() }

        if (points.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("schedule-none"))
            return
        }

        for (point in points) {
            val state = ScheduledSpawnState[point.key]

            sender.sendMessage(
                plugin.langYml.getMessage("schedule-status", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%mob%", point.mobId)
                    .replace("%point%", point.id)
                    .replace("%alive%", state.mobs.size.toString())
                    .replace("%max%", point.maxAlive.toString())
                    .replace("%next%", next(point, state.nextDue))
            )
        }
    }

    private fun next(point: ScheduledPoint, nextDue: Long): String {
        if (point.world == null) {
            return plugin.langYml.getFormattedString("messages.schedule-no-world")
        }

        val remaining = nextDue - System.currentTimeMillis()

        if (remaining <= 0) {
            return plugin.langYml.getFormattedString("messages.schedule-due")
        }

        return plugin.langYml.getFormattedString("messages.schedule-next-in")
            .replace("%time%", formatDuration(remaining / 1000 + 1))
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }

    override fun tabComplete(
        sender: CommandSender,
        args: List<String>
    ): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                EcoMobs.values().filter { it.scheduledSpawn != null }.map { it.id },
                completions
            )
        }

        completions.sort()
        return completions
    }
}
