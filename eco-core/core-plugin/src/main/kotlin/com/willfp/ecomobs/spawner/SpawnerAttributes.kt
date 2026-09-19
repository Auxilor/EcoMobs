package com.willfp.ecomobs.spawner

import com.willfp.ecomobs.mob.EcoMobs
import org.bukkit.entity.EntityType

val PICKUP_VALUES = listOf("allow", "silk_touch", "deny")

/**
 * The attributes a spawner exposes to commands, applied the same way to
 * spawner items and to placed spawner blocks.
 */
object SpawnerAttributes {
    val ATTRIBUTES = listOf("mob", "delay", "radius", "player-radius", "count", "max-nearby", "pickup", "particle", "explosion-proof", "no-ai", "stack-size")

    fun valueCount(attribute: String): Int = if (attribute == "delay") 2 else 1

    fun isValidMob(mobId: String): Boolean =
        EcoMobs[mobId] != null || entityTypeOrNull(mobId) != null

    fun apply(data: SpawnerData, attribute: String, args: List<String>): Boolean {
        when (attribute) {
            "mob" -> {
                val mobId = args.getOrNull(0) ?: return false
                if (!isValidMob(mobId)) return false
                data.mob = mobId
            }
            "delay" -> {
                val min = args.getOrNull(0)?.toIntOrNull()
                val max = args.getOrNull(1)?.toIntOrNull()
                if (min == null || max == null || min < 0 || min > max) return false
                data.delayMin = min
                data.delayMax = max
            }
            "radius" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                data.spawnRange = value
            }
            "player-radius" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                data.playerRange = value
            }
            "count" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                data.spawnCount = value
            }
            "max-nearby" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                data.maxNearby = value
            }
            "pickup" -> {
                val raw = args.getOrNull(0)?.lowercase() ?: return false
                val value = if (raw == "silk-touch") "silk_touch" else raw
                if (value !in PICKUP_VALUES) return false
                data.pickup = value
            }
            "particle" -> {
                val value = args.getOrNull(0)?.lowercase() ?: return false
                if (value != "none" && SpawnerAnimations[value] == null) return false
                data.particleAnim = if (value == "none") null else value
            }
            "explosion-proof" -> {
                val value = args.getOrNull(0)?.lowercase() ?: return false
                if (value != "true" && value != "false") return false
                data.explosionProof = value == "true"
            }
            "no-ai" -> {
                val value = args.getOrNull(0)?.lowercase() ?: return false
                if (value != "true" && value != "false") return false
                data.noAI = value == "true"
            }
            "stack-size" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                data.stackSize = value
            }
            else -> return false
        }
        return true
    }

    fun tabComplete(attribute: String, valueIndex: Int): List<String> = when (attribute) {
        "mob" -> EcoMobs.values().map { it.id } + EntityType.entries.map { it.name.lowercase() }
        "delay" -> if (valueIndex == 0) listOf("200", "400", "800") else listOf("400", "800", "1600")
        "pickup" -> PICKUP_VALUES
        "particle" -> listOf("none") + SpawnerAnimations.keys()
        "explosion-proof", "no-ai" -> listOf("true", "false")
        "stack-size" -> listOf("1", "8", "16", "64")
        else -> listOf("1", "4", "8", "16")
    }
}
