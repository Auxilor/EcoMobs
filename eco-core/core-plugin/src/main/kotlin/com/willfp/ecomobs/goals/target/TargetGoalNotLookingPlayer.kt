package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Player

class TargetGoalNotLookingPlayer(
    range: Double,
    interval: Int
) : TargetGoalPlayer(range, interval) {
    override fun select(players: List<Player>): Player? {
        return players.filter { player ->
            player.eyeLocation.direction.dot(mob.eyeLocation.toVector().subtract(player.eyeLocation.toVector())) < 0
        }.minByOrNull { it.location.distanceSquared(mob.location) }
    }

    object Deserializer : KeyedDeserializer<TargetGoalNotLookingPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", "not_looking_player")

        override fun deserialize(config: Config): TargetGoalNotLookingPlayer {
            return TargetGoalNotLookingPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1)
            )
        }
    }
}
