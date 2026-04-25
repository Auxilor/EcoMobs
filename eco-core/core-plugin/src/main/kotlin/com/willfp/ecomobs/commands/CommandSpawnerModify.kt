package com.willfp.ecomobs.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.PlacedSpawner
import com.willfp.ecomobs.spawner.PlacedSpawners
import com.willfp.ecomobs.spawner.SpawnerAnimations
import com.willfp.ecomobs.spawner.applyVanillaSettings
import com.willfp.ecomobs.spawner.isCustomSpawner
import com.willfp.ecomobs.spawner.spawnerDelayMax
import com.willfp.ecomobs.spawner.spawnerDelayMin
import com.willfp.ecomobs.spawner.spawnerMaxNearby
import com.willfp.ecomobs.spawner.spawnerMob
import com.willfp.ecomobs.spawner.spawnerParticleAnim
import com.willfp.ecomobs.spawner.spawnerPickup
import com.willfp.ecomobs.spawner.spawnerPlayerRange
import com.willfp.ecomobs.spawner.spawnerSpawnCount
import com.willfp.ecomobs.spawner.resolveEntityType
import com.willfp.ecomobs.spawner.spawnerSpawnRange
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

private val ATTRIBUTES = listOf("mob", "delay", "radius", "player-radius", "count", "max-count", "pickup", "particle")
private val PICKUP_VALUES = listOf("allow", "silk_touch", "deny")

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

        if (!player.hasPermission("ecomobs.command.spawner.modify.$attribute")) {
            sender.sendMessage(plugin.langYml.getMessage("no-permission"))
            return
        }

        val heldFis = player.inventory.itemInMainHand.fast()
        val isHeld = heldFis.isCustomSpawner

        var targetState: CreatureSpawner? = null
        var targetLocation = player.location

        if (!isHeld) {
            val targetBlock = player.getTargetBlockExact(5)
            if (targetBlock == null || targetBlock.type != Material.SPAWNER) {
                sender.sendMessage(plugin.langYml.getMessage("spawner-no-target"))
                return
            }
            val state = targetBlock.state as? CreatureSpawner
            if (state == null || !state.isCustomSpawner) {
                sender.sendMessage(plugin.langYml.getMessage("spawner-no-target"))
                return
            }
            targetState = state
            targetLocation = targetBlock.location
        }

        fun invalidValue() = sender.sendMessage(
            plugin.langYml.getMessage("spawner-invalid-value").replace("%attribute%", attribute)
        )

        fun applyBlock() {
            targetState!!.applyVanillaSettings()
            targetState.update()
        }

        when (attribute) {
            "mob" -> {
                val mobId = args.getOrNull(1) ?: return invalidValue()
                val valid = EcoMobs[mobId] != null || runCatching {
                    EntityType.valueOf(mobId.uppercase())
                }.isSuccess
                if (!valid) {
                    sender.sendMessage(plugin.langYml.getMessage("spawner-invalid-mob")); return
                }
                if (isHeld) heldFis.spawnerMob = mobId
                else {
                    targetState!!.spawnerMob = mobId
                    resolveEntityType(mobId)?.let { targetState.spawnedType = it }
                    targetState.update()
                }
            }

            "delay" -> {
                val min = args.getOrNull(1)?.toIntOrNull()
                val max = args.getOrNull(2)?.toIntOrNull()
                if (min == null || max == null || min < 0 || min > max) return invalidValue()
                if (isHeld) {
                    heldFis.spawnerDelayMin = min; heldFis.spawnerDelayMax = max
                } else {
                    targetState!!.spawnerDelayMin = min; targetState.spawnerDelayMax = max; applyBlock()
                }
            }

            "radius" -> {
                val value = args.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 1 } ?: return invalidValue()
                if (isHeld) heldFis.spawnerSpawnRange = value
                else {
                    targetState!!.spawnerSpawnRange = value; applyBlock()
                }
            }

            "player-radius" -> {
                val value = args.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 1 } ?: return invalidValue()
                if (isHeld) heldFis.spawnerPlayerRange = value
                else {
                    targetState!!.spawnerPlayerRange = value; applyBlock()
                }
            }

            "count" -> {
                val value = args.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 1 } ?: return invalidValue()
                if (isHeld) heldFis.spawnerSpawnCount = value
                else {
                    targetState!!.spawnerSpawnCount = value; applyBlock()
                }
            }

            "max-count" -> {
                val value = args.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 1 } ?: return invalidValue()
                if (isHeld) heldFis.spawnerMaxNearby = value
                else {
                    targetState!!.spawnerMaxNearby = value; applyBlock()
                }
            }

            "pickup" -> {
                val raw = args.getOrNull(1)?.lowercase() ?: return invalidValue()
                val value = if (raw == "silk-touch") "silk_touch" else raw
                if (value !in PICKUP_VALUES) return invalidValue()
                if (isHeld) heldFis.spawnerPickup = value
                else {
                    targetState!!.spawnerPickup = value; targetState.update()
                }
            }

            "particle" -> {
                val value = args.getOrNull(1)?.lowercase() ?: return invalidValue()
                if (value != "none" && SpawnerAnimations[value] == null) return invalidValue()
                val stored = if (value == "none") null else value
                if (isHeld) heldFis.spawnerParticleAnim = stored
                else {
                    targetState!!.spawnerParticleAnim = stored
                    targetState.update()
                    PlacedSpawners.set(targetLocation, PlacedSpawner(targetLocation, stored))
                }
            }

            else -> {
                sender.sendMessage(plugin.langYml.getMessage("invalid-command")); return
            }
        }

        if (isHeld) player.updateInventory()

        sender.sendMessage(
            plugin.langYml.getMessage("spawner-modified")
                .replace("%attribute%", attribute)
                .replace("%value%", args.drop(1).joinToString(" "))
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1)
            StringUtil.copyPartialMatches(
                args[0],
                ATTRIBUTES.filter { sender.hasPermission("ecomobs.command.spawner.modify.$it") },
                completions
            )

        if (args.size == 2) {
            when (args[0].lowercase()) {
                "mob" -> StringUtil.copyPartialMatches(
                    args[1],
                    EcoMobs.values().map { it.id } + EntityType.entries.map { it.name.lowercase() },
                    completions
                )

                "pickup" -> StringUtil.copyPartialMatches(args[1], PICKUP_VALUES, completions)
                "particle" -> StringUtil.copyPartialMatches(
                    args[1], listOf("none") + SpawnerAnimations.keys(), completions
                )

                else -> StringUtil.copyPartialMatches(args[1], listOf("1", "4", "8", "16", "200"), completions)
            }
        }

        if (args.size == 3 && args[0].lowercase() == "delay")
            StringUtil.copyPartialMatches(args[2], listOf("200", "400", "800"), completions)

        completions.sort()
        return completions
    }
}
