package com.willfp.ecobosses.spawn

import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.ecobosses.util.SpawnTotem
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class SpawnTotemHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: BlockPlaceEvent) {
        for (i in 0..2) {
            lateinit var block1: Block
            lateinit var block2: Block
            lateinit var block3: Block

            // I know this code sucks ass, but I can't be arsed to write it nicely
            when (i) {
                0 -> {
                    block3 = event.block
                    block2 = event.block.getRelative(0, -1, 0)
                    block1 = event.block.getRelative(0, -2, 0)
                }
                1 -> {
                    block1 = event.block
                    block2 = event.block.getRelative(0, 1, 0)
                    block3 = event.block.getRelative(0, 2, 0)
                }
                2 -> {
                    block2 = event.block
                    block1 = event.block.getRelative(0, -1, 0)
                    block3 = event.block.getRelative(0, 1, 0)
                }
            }

            val placedTotem = SpawnTotem(block1.type, block2.type, block3.type)
            for (boss in Bosses.values()) {
                if (boss.totem == null || boss.disabledTotemWorlds.containsIgnoreCase(event.block.world.name)) {
                    continue
                }

                if (!boss.totem.matches(placedTotem)) {
                    continue
                }

                val player = event.player

                if (!boss.spawnConditions.all { it.condition.isConditionMet(player, it.config) }) {
                    return
                }

                val spawnEvent = BossSpawnEvent(boss, event.block.location, BossSpawnEvent.SpawnReason.TOTEM, player)

                if (!spawnEvent.isCancelled) {
                    block1.type = Material.AIR
                    block2.type = Material.AIR
                    block3.type = Material.AIR

                    boss.spawn(event.block.location.add(0.0, 1.5, 0.0))
                }
            }
        }
    }
}
