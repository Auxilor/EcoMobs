package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.SpawnTotem
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class SpawnTotemHandler : Listener {
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
            SpawnTotem(block.type, blockBelow.type, blockTwoBelow.type),
            SpawnTotem(blockTwoAbove.type, blockAbove.type, block.type),
            SpawnTotem(blockAbove.type, block.type, blockBelow.type)
        )

        for (totem in totemPositions) {
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

                mob.spawn(location, SpawnReason.TOTEM)
            }
        }
    }
}
