package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.PlacedSpawner
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.SpawnerAttributes
import com.willfp.ecomobs.spawner.applyVanillaSettings
import com.willfp.ecomobs.spawner.resolveEntityType
import com.willfp.ecomobs.spawner.spawner
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

object CommandSpawnerModify : Subcommand(
    plugin, "modify", "ecomobs.command.spawner.modify", true
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = sender as Player

        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
            return
        }

        val attribute = args[0].lowercase()

        if (attribute !in SpawnerAttributes.ATTRIBUTES) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
            return
        }

        if (!player.hasPermission("ecomobs.command.spawner.modify.$attribute")) {
            sender.sendMessage(plugin.langYml.getMessage("no-permission"))
            return
        }

        val values = args.drop(1)

        fun rejectValue() {
            val message = if (attribute == "mob") {
                plugin.langYml.getMessage("spawner-invalid-mob")
            } else {
                plugin.langYml.getMessage("spawner-invalid-value").replace("%attribute%", attribute)
            }

            sender.sendMessage(message)
        }

        val held = player.inventory.itemInMainHand.fast()

        // Modifies a held spawner item if there is one, otherwise the spawner being looked at.
        if (held.spawner.isCustomSpawner) {
            if (!SpawnerAttributes.apply(held.spawner, attribute, values)) {
                rejectValue()
                return
            }

            player.updateInventory()
        } else {
            val block = player.getTargetBlockExact(5)
            val state = block?.takeIf { it.type == Material.SPAWNER }?.state as? CreatureSpawner

            if (block == null || state == null) {
                sender.sendMessage(plugin.langYml.getMessage("spawner-no-target"))
                return
            }

            if (!SpawnerAttributes.apply(state.spawner, attribute, values)) {
                rejectValue()
                return
            }

            state.applyVanillaSettings()
            state.spawner.mob?.let { mobId ->
                resolveEntityType(mobId)?.let { state.spawnedType = it }
            }
            state.update()

            PlacedSpawners.set(block.location, PlacedSpawner(block.location, state.spawner.particleAnim))
        }

        sender.sendMessage(
            plugin.langYml.getMessage("spawner-modified")
                .replace("%attribute%", attribute)
                .replace("%value%", values.joinToString(" "))
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                SpawnerAttributes.ATTRIBUTES.filter { sender.hasPermission("ecomobs.command.spawner.modify.$it") },
                completions
            )
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                SpawnerAttributes.tabComplete(args[0].lowercase(), 0),
                completions
            )
        }

        if (args.size == 3 && args[0].lowercase() == "delay") {
            StringUtil.copyPartialMatches(
                args[2],
                SpawnerAttributes.tabComplete("delay", 1),
                completions
            )
        }

        completions.sort()
        return completions
    }
}
