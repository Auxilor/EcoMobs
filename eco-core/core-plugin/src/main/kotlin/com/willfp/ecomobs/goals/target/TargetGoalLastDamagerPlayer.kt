package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.plugin
import org.bukkit.entity.Player

class TargetGoalLastDamagerPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return plugin.topDamagerHandler.getLastDamager(mob)?.let { uuid ->
            players.firstOrNull { it.uniqueId == uuid }
        }
    }

    object Deserializer : KeyedDeserializer<TargetGoalLastDamagerPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "last_damager_player")

        override fun deserialize(config: Config): TargetGoalLastDamagerPlayer {
            return TargetGoalLastDamagerPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
