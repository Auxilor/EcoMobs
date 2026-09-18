package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandKillAll : Subcommand(
    plugin,
    "killall",
    "ecomobs.command.killall",
    false
) {
    override fun onExecute(
        sender: CommandSender,
        args: List<String>
    ) {
        // If a mob is specified, only mobs of that type are killed.
        var filter: String? = null

        if (args.isNotEmpty()) {
            val mob = EcoMobs.getByID(args[0].lowercase())

            if (mob == null) {
                sender.sendMessage(plugin.langYml.getMessage("specify-mob"))
                return
            }

            filter = mob.id
        }

        var killed = 0

        // Walked over what EcoMobs tracks rather than over world.entities: no thread
        // owns every region, so there is no thread that can iterate a whole world.
        // Mobs loaded before the plugin enabled are picked up by ChunkHandler, so a
        // loaded mob of ours is a tracked mob of ours.
        for (ecoMob in EcoMobs.values()) {
            if (filter != null && ecoMob.id != filter) {
                continue
            }

            for (living in ecoMob.livingMobs.toList()) {
                onEntity(living.entity) {
                    living.despawn()
                }

                killed++
            }
        }

        sender.sendMessage(
            plugin.langYml.getMessage("killed-mobs")
                .replace("%quantity%", killed.toString())
        )
    }

    override fun tabComplete(
        sender: CommandSender,
        args: List<String>
    ): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], EcoMobs.values().map { it.id }, completions)
        }

        completions.sort()
        return completions
    }
}
