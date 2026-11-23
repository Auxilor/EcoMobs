package com.willfp.ecomobs.goals.target

import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.serial.KeyedDeserializer
import wiki.ecohub.core.platform.namespacedKeyOf

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
