package com.willfp.ecobosses.spawn

import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.ecobosses.util.SpawnTotem
import org.bukkit.Bukkit
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
            lateinit var top: Block
            lateinit var middle: Block
            lateinit var bottom: Block

            // I know this code sucks ass, but I can't be arsed to write it nicely
            when (i) {
                0 -> {
                    top = event.block
                    middle = event.block.getRelative(0, -1, 0)
                    bottom = event.block.getRelative(0, -2, 0)
                }
                1 -> {
                    top = event.block.getRelative(0, 2, 0)
                    middle = event.block.getRelative(0, 1, 0)
                    bottom = event.block
                }
                2 -> {
                    top = event.block.getRelative(0, 1, 0)
                    middle = event.block
                    bottom = event.block.getRelative(0, -1, 0)
                }
            }

            val placedTotem = SpawnTotem(top.type, middle.type, bottom.type)
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

                Bukkit.getPluginManager().callEvent(spawnEvent)

                if (!spawnEvent.isCancelled) {
                    top.type = Material.AIR
                    middle.type = Material.AIR
                    bottom.type = Material.AIR

                    boss.spawn(event.block.location.add(0.0, 1.5, 0.0))
                }
            }
        }
    }
}
