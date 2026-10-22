package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.scheduled.ScheduledPoint
import com.willfp.ecomobs.scheduled.ScheduledSpawnState
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.time.Duration
import java.util.concurrent.TimeUnit

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
            EcoMobs.scheduled
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
                    .replace(oldValue = "%mob%", newValue = point.mobId)
                    .replace(oldValue = "%point%", newValue = point.id)
                    .replace(oldValue = "%alive%", newValue = state.mobs.size.toString())
                    .replace(oldValue = "%max%", newValue = point.maxAlive.toString())
                    .replace(oldValue = "%next%", newValue = next(point, state.nextDue))
            )
        }
    }

    private fun next(point: ScheduledPoint, nextDue: Long): String {
        val remaining = nextDue - System.currentTimeMillis()

        return when {
            point.world == null -> plugin.langYml.getFormattedString("messages.schedule-no-world")
            remaining <= 0 -> plugin.langYml.getFormattedString("messages.schedule-due")
            else -> plugin.langYml.getFormattedString("messages.schedule-next-in")
                .replace(
                    oldValue = "%time%",
                    newValue = formatDuration(TimeUnit.MILLISECONDS.toSeconds(remaining) + 1)
                )
        }
    }

    private fun formatDuration(seconds: Long): String {
        val duration = Duration.ofSeconds(seconds)
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${duration.toSecondsPart()}s"
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
                EcoMobs.scheduled.map { it.id },
                completions
            )
        }

        completions.sort()
        return completions
    }
}
