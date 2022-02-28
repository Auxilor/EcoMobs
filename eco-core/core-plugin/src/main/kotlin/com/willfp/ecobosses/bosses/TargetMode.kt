package com.willfp.ecobosses.bosses

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class TargetMode(
    val id: String,
    private val function: (Collection<LivingEntity>, Entity) -> LivingEntity?
) {
    init {
        BY_ID[this.id] = this
    }

    fun getTarget(boss: LivingEcoBoss): LivingEntity? {
        val entity = boss.entity ?: return null

        return function(
            entity.getNearbyEntities(
                boss.boss.targetRange,
                boss.boss.targetRange,
                boss.boss.targetRange
            ).filterIsInstance<Player>()
                .filter { listOf(GameMode.SURVIVAL, GameMode.ADVENTURE).contains(it.gameMode) }
                .ifEmpty { return null },
            entity
        )
    }

    companion object {
        private val BY_ID: BiMap<String, TargetMode> = HashBiMap.create()

        val RANDOM = TargetMode("random") { entities, _ ->
            entities.random()
        }

        val CLOSEST = TargetMode("closest") { entities, boss ->
            entities.minByOrNull { it.location.distanceSquared(boss.location) }
        }

        val LOWEST_HEALTH = TargetMode("lowest_health") { entities, _ ->
            entities.minByOrNull { it.health }
        }

        val HIGHEST_HEALTH = TargetMode("highest_health") { entities, _ ->
            entities.maxByOrNull { it.health }
        }

        @JvmStatic
        fun values(): List<TargetMode> {
            return ImmutableList.copyOf(BY_ID.values)
        }

        @JvmStatic
        fun getByID(id: String): TargetMode? {
            return BY_ID[id]
        }
    }
}
