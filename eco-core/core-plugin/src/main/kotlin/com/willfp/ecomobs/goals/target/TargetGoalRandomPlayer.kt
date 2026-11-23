package com.willfp.ecomobs.goals.target

import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.serial.KeyedDeserializer
import wiki.ecohub.core.platform.namespacedKeyOf

class TargetGoalRandomPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.random()
    }

    object Deserializer : KeyedDeserializer<TargetGoalRandomPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "random_player")

        override fun deserialize(config: Config): TargetGoalRandomPlayer {
            return TargetGoalRandomPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
