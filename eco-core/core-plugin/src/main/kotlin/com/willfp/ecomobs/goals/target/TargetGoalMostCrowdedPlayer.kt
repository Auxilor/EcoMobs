package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Player

class TargetGoalMostCrowdedPlayer(
    range: Double,
    interval: Int,
    private val radius: Double
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        val radiusSquared = radius * radius

        return players.maxWithOrNull(
            compareBy<Player> { player ->
                players.count { it !== player && it.location.distanceSquared(player.location) <= radiusSquared }
            }.thenByDescending { it.location.distanceSquared(mob.location) }
        )
    }

    object Deserializer : KeyedDeserializer<TargetGoalMostCrowdedPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "most_crowded_player")

        override fun deserialize(config: Config): TargetGoalMostCrowdedPlayer {
            return TargetGoalMostCrowdedPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1),
                (config.getDoubleOrNull("radius") ?: 5.0).coerceAtLeast(1.0)
            )
        }
    }
}
