package com.willfp.ecomobs.goals.entity

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.GoalFlag
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Mob
import java.util.EnumSet

class EntityGoalRandomTeleport(
    private val interval: Int,
    private val range: Int
) : CustomGoal<Mob>() {
    private lateinit var mob: Mob
    private var tick = 0

    override fun initialize(mob: Mob) {
        this.mob = mob
    }

    override fun canUse(): Boolean {
        return true
    }

    override fun tick() {
        tick++

        if (tick % interval != 0) {
            return
        }

        val validLocations = mutableListOf<Location>()

        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val location = mob.location.clone().apply {
                        this.x += x
                        this.y += y
                        this.z += z
                    }
                    val block = location.block

                    // From old EcoBosses code
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

        mob.teleport(validLocations.random())
    }

    override fun getFlags(): EnumSet<GoalFlag> {
        return EnumSet.of(GoalFlag.MOVE)
    }

    object Deserializer : KeyedDeserializer<EntityGoalRandomTeleport> {
        override fun getKey() = namespacedKeyOf("ecomobs", "random_teleport")

        override fun deserialize(p0: Config): EntityGoalRandomTeleport {
            return EntityGoalRandomTeleport(
                p0.getInt("interval").coerceAtLeast(1),
                p0.getInt("range").coerceAtLeast(1)
            )
        }
    }
}
