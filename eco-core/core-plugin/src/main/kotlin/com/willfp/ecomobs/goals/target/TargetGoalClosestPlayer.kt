package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Player

class TargetGoalClosestPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.minByOrNull { it.location.distanceSquared(mob.location) }
    }

    object Deserializer : KeyedDeserializer<TargetGoalClosestPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "closest_player")

        override fun deserialize(config: Config): TargetGoalClosestPlayer {
            return TargetGoalClosestPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
