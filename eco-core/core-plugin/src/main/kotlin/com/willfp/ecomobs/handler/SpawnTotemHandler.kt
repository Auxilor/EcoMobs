package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobTotemBuildEvent
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.SpawnTotem
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object SpawnTotemHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockPlaceEvent) {
        val block = event.block
        val blockBelow = block.getRelative(0, -1, 0)
        val blockTwoBelow = block.getRelative(0, -2, 0)
        val blockAbove = block.getRelative(0, 1, 0)
        val blockTwoAbove = block.getRelative(0, 2, 0)

        val player = event.player
        val location = block.location.add(0.0, 1.5, 0.0)

        val totemPositions = listOf(
            listOf(block, blockBelow, blockTwoBelow),
            listOf(blockTwoAbove, blockAbove, block),
            listOf(blockAbove, block, blockBelow)
        )

        for (totemBlocks in totemPositions) {
            val (top, middle, bottom) = totemBlocks
            val totem = SpawnTotem(top.type, middle.type, bottom.type)

            for (mob in EcoMobs.values()) {
                val options = mob.totemOptions ?: continue

                if (options.totem != totem) {
                    continue
                }

                if (!options.conditions.areMetAndTrigger(
                        TriggerData(
                            player = player
                        ).dispatch(player.toDispatcher())
                    )
                ) {
                    continue
                }

                val buildEvent = EcoMobTotemBuildEvent(mob, totem, location, player)
                Bukkit.getPluginManager().callEvent(buildEvent)

                if (buildEvent.isCancelled) {
                    continue
                }

                if (mob.spawn(buildEvent.location, SpawnReason.TOTEM) == null) {
                    continue
                }

                plugin.scheduler.at(block.location).run {
                    totemBlocks.forEach { it.type = Material.AIR }
                }

                return
            }
        }
    }
}
