package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.scheduled.ScheduledSpawnState
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandScheduleReset : Subcommand(
    plugin,
    "reset",
    "ecomobs.command.schedule.reset",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        val mob = args.getOrNull(0)?.let { EcoMobs[it.lowercase()] }
        val scheduledSpawn = mob?.scheduledSpawn

        if (mob == null || scheduledSpawn == null) {
            sender.sendMessage(plugin.langYml.getMessage("schedule-invalid-mob"))
            return
        }

        val points = when (val pointId = args.getOrNull(1)?.lowercase()) {
            null -> emptyList()
            "all" -> scheduledSpawn.points
            else -> scheduledSpawn.points.filter { it.id == pointId }
        }

        if (points.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("schedule-invalid-point"))
            return
        }

        for (point in points) {
            val loaded = ScheduledSpawnState[point.key].mobs.keys
                .mapNotNull { mob.getLivingMob(it) }

            for (living in loaded) {
                onEntity(living.entity) {
                    (living as? LivingMobImpl)?.discard()
                }
            }

            ScheduledSpawnState.reset(point.key, loaded.map { it.entity.uniqueId }.toSet())
        }

        sender.sendMessage(
            plugin.langYml.getMessage("schedule-reset")
                .replace("%quantity%", points.size.toString())
        )
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

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                EcoMobs[args[0].lowercase()]?.scheduledSpawn?.points?.map { it.id }.orEmpty() + "all",
                completions
            )
        }

        completions.sort()
        return completions
    }
}
