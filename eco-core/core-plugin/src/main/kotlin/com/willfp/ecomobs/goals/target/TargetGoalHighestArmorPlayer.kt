package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Player

class TargetGoalHighestArmorPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.maxByOrNull { it.armor }
    }

    object Deserializer : KeyedDeserializer<TargetGoalHighestArmorPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "highest_armor_player")

        override fun deserialize(config: Config): TargetGoalHighestArmorPlayer {
            return TargetGoalHighestArmorPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
