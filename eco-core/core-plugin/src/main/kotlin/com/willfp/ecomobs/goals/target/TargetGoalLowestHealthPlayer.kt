package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Player

class TargetGoalLowestHealthPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.minByOrNull { it.health }
    }

    object Deserializer : KeyedDeserializer<TargetGoalLowestHealthPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "lowest_health_player")

        override fun deserialize(config: Config): TargetGoalLowestHealthPlayer {
            return TargetGoalLowestHealthPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
