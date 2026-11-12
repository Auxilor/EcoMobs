package com.willfp.ecomobs.goals.target

import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.config.serial.KeyedDeserializer
import wiki.ecohub.core.platform.namespacedKeyOf

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
