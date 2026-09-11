package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.mobKey
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType
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

        for (world in Bukkit.getWorlds()) {
            for (entity in world.entities.filterIsInstance<Mob>()) {
                // Read the ID from the entity itself rather than from tracking, so that mobs
                // spawned before a restart or reload are still killed.
                val id = entity.persistentDataContainer.get(mobKey, PersistentDataType.STRING)
                    ?: continue

                if (filter != null && id != filter) {
                    continue
                }

                val livingMob = EcoMobs[id]?.getLivingMob(entity)

                if (livingMob != null) {
                    livingMob.despawn()
                } else {
                    entity.remove()
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
