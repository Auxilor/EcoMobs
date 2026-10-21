package com.willfp.ecomobs.commands

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.discard
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.scheduled.ScheduledSpawnState
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.util.StringUtil
import java.util.UUID

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
            for (uuid in ScheduledSpawnState.reset(point.key)) {
                val entity = mob.getLivingMob(uuid)?.entity ?: findEntity(uuid) ?: continue

                onEntity(entity) {
                    mob.getLivingMob(uuid)?.discard() ?: entity.remove()
                    ScheduledSpawnState.forgetRetired(point.key, uuid)
                }
            }
        }

        sender.sendMessage(
            plugin.langYml.getMessage("schedule-reset")
                .replace("%quantity%", points.size.toString())
        )
    }

    /**
     * A loaded entity EcoMobs may not be tracking yet, e.g. just after a reload. Off Folia only:
     * on Folia it is removed silently when its chunk next loads.
     */
    private fun findEntity(uuid: UUID): Entity? =
        if (Prerequisite.HAS_FOLIA.isMet) null else Bukkit.getEntity(uuid)

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
