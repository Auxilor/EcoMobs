package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.SpawnerItemModifier
import com.willfp.ecomobs.spawner.spawnerDelayMax
import com.willfp.ecomobs.spawner.spawnerDelayMin
import com.willfp.ecomobs.spawner.spawnerMaxNearby
import com.willfp.ecomobs.spawner.spawnerMob
import com.willfp.ecomobs.spawner.spawnerPickup
import com.willfp.ecomobs.spawner.spawnerPlayerRange
import com.willfp.ecomobs.spawner.spawnerSpawnCount
import com.willfp.ecomobs.spawner.spawnerSpawnRange
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

object CommandSpawnerGive : Subcommand(
    plugin, "give", "ecomobs.command.spawner.give", false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("needs-player"))
            return
        }
        if (args.size == 1) {
            sender.sendMessage(plugin.langYml.getMessage("spawner-invalid-mob"))
            return
        }

        val recipient = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val mobId = args[1]
        val validMob = EcoMobs[mobId] != null || runCatching {
            EntityType.valueOf(mobId.uppercase())
        }.isSuccess

        if (!validMob) {
            sender.sendMessage(plugin.langYml.getMessage("spawner-invalid-mob"))
            return
        }

        val amount = args.getOrNull(2)?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val tailStart = if (args.getOrNull(2)?.toIntOrNull() != null) 3 else 2
        val tail = args.drop(tailStart)

        val item = ItemStack(Material.SPAWNER, amount)
        val fis = item.fast()
        fis.spawnerMob = mobId
        fis.spawnerDelayMin = 200
        fis.spawnerDelayMax = 800
        fis.spawnerSpawnCount = 4
        fis.spawnerSpawnRange = 4
        fis.spawnerPlayerRange = 16
        fis.spawnerMaxNearby = 6
        fis.spawnerPickup = "deny"

        var i = 0
        while (i < tail.size) {
            val attribute = tail[i].lowercase()
            if (attribute == "mob" || attribute !in SpawnerItemModifier.ATTRIBUTES) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
                return
            }
            if (!sender.hasPermission("ecomobs.command.spawner.modify.$attribute")) {
                sender.sendMessage(plugin.langYml.getMessage("no-permission"))
                return
            }
            val value = SpawnerItemModifier.valueCount(attribute)
            val valueArgs = tail.subList(i + 1, minOf(i + 1 + value, tail.size))
            if (valueArgs.size < value) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
                return
            }
            if (!SpawnerItemModifier.apply(fis, attribute, valueArgs)) {
                sender.sendMessage(
                    plugin.langYml.getMessage("spawner-invalid-value").replace("%attribute%", attribute)
                )
                return
            }
            i += 1 + value
        }

        DropQueue(recipient)
            .addItem(fis.unwrap())
            .forceTelekinesis()
            .push()

        sender.sendMessage(
            plugin.langYml.getMessage("spawner-give")
                .replace("%player%", recipient.name)
                .replace("%mob%", mobId)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1)
            StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().map { it.name }, completions)

        if (args.size == 2)
            StringUtil.copyPartialMatches(
                args[1],
                EcoMobs.values().map { it.id } + EntityType.entries.map { it.name.lowercase() },
                completions
            )

        if (args.size == 3)
            StringUtil.copyPartialMatches(
                args[2],
                listOf("1", "2", "3", "4", "5") + SpawnerItemModifier.ATTRIBUTES
                    .filter { it != "mob" && sender.hasPermission("ecomobs.command.spawner.modify.$it") },
                completions
            )

        if (args.size >= 4) {
            val tailStart = if (args[2].toIntOrNull() != null) 3 else 2
            val tail = args.drop(tailStart)
            val cursorIndex = tail.size - 1
            val usedAttributes = mutableSetOf<String>()

            var i = 0
            while (i <= cursorIndex) {
                val token = tail[i].lowercase()
                if (i == cursorIndex) {
                    StringUtil.copyPartialMatches(
                        token,
                        SpawnerItemModifier.ATTRIBUTES.filter { attr ->
                            attr != "mob" && attr !in usedAttributes &&
                                sender.hasPermission("ecomobs.command.spawner.modify.$attr")
                        },
                        completions
                    )
                    break
                }
                if (token !in SpawnerItemModifier.ATTRIBUTES || token == "mob") {
                    i++
                    continue
                }
                val value = SpawnerItemModifier.valueCount(token)
                usedAttributes.add(token)
                if (i + value >= cursorIndex) {
                    val valueIndex = cursorIndex - i - 1
                    StringUtil.copyPartialMatches(
                        tail[cursorIndex],
                        SpawnerItemModifier.tabComplete(token, valueIndex),
                        completions
                    )
                    break
                }
                i += 1 + value
            }
        }

        completions.sort()
        return completions
    }
}
