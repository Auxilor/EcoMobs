package com.willfp.ecomobs.goals.target

import com.willfp.ecomobs.plugin
import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.serial.KeyedDeserializer
import wiki.ecohub.core.platform.namespacedKeyOf

class TargetGoalTopDamagerPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return plugin.topDamagerHandler.getDamagers(mob).firstNotNullOfOrNull { damager ->
            players.firstOrNull { it.uniqueId == damager.uuid }
        }
    }

    object Deserializer : KeyedDeserializer<TargetGoalTopDamagerPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "top_damager_player")

        override fun deserialize(config: Config): TargetGoalTopDamagerPlayer {
            return TargetGoalTopDamagerPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
