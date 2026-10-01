package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.entities.ai.TargetGoals
import com.willfp.ecomobs.plugin
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object PlayerTargetGoals {
    private val deserializers = listOf(
        goal("closest_player") { mob, players ->
            players.minByOrNull { it.location.distanceSquared(mob.location) }
        },

        goal("random_player") { _, players ->
            players.random()
        },

        goal("lowest_health_player") { _, players ->
            players.minByOrNull { it.health }
        },

        goal("highest_health_player") { _, players ->
            players.maxByOrNull { it.health }
        },

        goal("lowest_armor_player") { _, players ->
            players.minByOrNull { it.armor }
        },

        goal("highest_armor_player") { _, players ->
            players.maxByOrNull { it.armor }
        },

        goal("top_damager_player") { mob, players ->
            plugin.topDamagerHandler.getDamagers(mob).firstNotNullOfOrNull { damager ->
                players.firstOrNull { it.uniqueId == damager.uuid }
            }
        },

        goal("last_damager_player") { mob, players ->
            plugin.topDamagerHandler.getLastDamager(mob)?.let { uuid ->
                players.firstOrNull { it.uniqueId == uuid }
            }
        },

        TargetGoalPlayer.Deserializer("most_crowded_player") { config ->
            val radiusSquared = (config.getDoubleOrNull("radius") ?: 5.0).coerceAtLeast(1.0).let { it * it }

            PlayerSelector { mob, players ->
                players.maxWithOrNull(
                    compareBy<Player> { player ->
                        players.count { it !== player && it.location.distanceSquared(player.location) <= radiusSquared }
                    }.thenByDescending { it.location.distanceSquared(mob.location) }
                )
            }
        },

        goal("not_looking_player") { mob, players ->
            players.filter { player ->
                player.eyeLocation.direction.dot(mob.eyeLocation.toVector().subtract(player.eyeLocation.toVector())) < 0
            }.minByOrNull { it.location.distanceSquared(mob.location) }
        }
    )

    private val Player.armor: Double
        get() = getAttribute(Attribute.ARMOR)?.value ?: 0.0

    private fun goal(id: String, selector: PlayerSelector) = TargetGoalPlayer.Deserializer(id) { selector }

    fun registerAll() {
        for (deserializer in deserializers) {
            TargetGoals.register(deserializer)
        }
    }
}
