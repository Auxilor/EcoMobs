package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace

class TeleportHandler : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val entity = boss.entity ?: return
        if (!boss.boss.canTeleport) {
            return
        }

        if (tick % boss.boss.teleportInterval != 0) {
            return
        }

        val range = boss.boss.teleportRange

        val validLocations = mutableListOf<Location>()

        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val location = entity.location.clone().apply {
                        this.x += x
                        this.y += y
                        this.z += z
                    }
                    val block = location.block

                    if (block.type == Material.AIR && block.getRelative(BlockFace.UP).type == Material.AIR && !(block.getRelative(
                            BlockFace.DOWN
                        ).isLiquid || block.getRelative(BlockFace.DOWN).type == Material.AIR)
                    ) {
                        validLocations.add(location)
                    }
                }
            }
        }

        if (validLocations.isEmpty()) {
            return
        }

        entity.teleport(validLocations.random())
    }
}
