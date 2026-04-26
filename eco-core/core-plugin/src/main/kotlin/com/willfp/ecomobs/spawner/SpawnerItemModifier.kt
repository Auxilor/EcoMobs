package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.spawner.spawnerExplosionProof
import org.bukkit.entity.EntityType

val PICKUP_VALUES = listOf("allow", "silk_touch", "deny")

object SpawnerItemModifier {
    val ATTRIBUTES = listOf("mob", "delay", "radius", "player-radius", "count", "max-nearby", "pickup", "particle", "explosion-proof")

    fun valueCount(attribute: String): Int = if (attribute == "delay") 2 else 1

    fun apply(fis: FastItemStack, attribute: String, args: List<String>): Boolean {
        when (attribute) {
            "mob" -> {
                val mobId = args.getOrNull(0) ?: return false
                val valid = EcoMobs[mobId] != null || runCatching {
                    EntityType.valueOf(mobId.uppercase())
                }.isSuccess
                if (!valid) return false
                fis.spawnerMob = mobId
            }
            "delay" -> {
                val min = args.getOrNull(0)?.toIntOrNull()
                val max = args.getOrNull(1)?.toIntOrNull()
                if (min == null || max == null || min < 0 || min > max) return false
                fis.spawnerDelayMin = min
                fis.spawnerDelayMax = max
            }
            "radius" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                fis.spawnerSpawnRange = value
            }
            "player-radius" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                fis.spawnerPlayerRange = value
            }
            "count" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                fis.spawnerSpawnCount = value
            }
            "max-nearby" -> {
                val value = args.getOrNull(0)?.toIntOrNull()?.takeIf { it >= 1 } ?: return false
                fis.spawnerMaxNearby = value
            }
            "pickup" -> {
                val raw = args.getOrNull(0)?.lowercase() ?: return false
                val value = if (raw == "silk-touch") "silk_touch" else raw
                if (value !in PICKUP_VALUES) return false
                fis.spawnerPickup = value
            }
            "particle" -> {
                val value = args.getOrNull(0)?.lowercase() ?: return false
                if (value != "none" && SpawnerAnimations[value] == null) return false
                fis.spawnerParticleAnim = if (value == "none") null else value
            }
            "explosion-proof" -> {
                val value = args.getOrNull(0)?.lowercase() ?: return false
                if (value != "true" && value != "false") return false
                fis.spawnerExplosionProof = value == "true"
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
        "explosion-proof" -> listOf("true", "false")
        else -> listOf("1", "4", "8", "16")
    }
}
