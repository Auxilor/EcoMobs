package com.willfp.ecomobs.goals.target

import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.serial.KeyedDeserializer
import wiki.ecohub.core.platform.namespacedKeyOf

class TargetGoalLowestArmorPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.minByOrNull { it.armor }
    }

    object Deserializer : KeyedDeserializer<TargetGoalLowestArmorPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "lowest_armor_player")

        override fun deserialize(config: Config): TargetGoalLowestArmorPlayer {
            return TargetGoalLowestArmorPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
