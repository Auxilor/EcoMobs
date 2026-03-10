package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.display.BaseItem
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpawnSpawner internal constructor(
    val mob: EcoMob,
    val conditions: ConditionList,
    val settings: SpawnerSettings,
    val item: BaseItem
) {
    fun canPlace(player: Player): Boolean {
        return conditions.areMetAndTrigger(
            TriggerData(
                player = player
            ).dispatch(player.toDispatcher())
        )
    }
}

data class SpawnerSettings(
    val minSpawnDelay: Int,
    val maxSpawnDelay: Int,
    val spawnCount: Int,
    val maxNearbyEntities: Int,
    val requiredPlayerRange: Int,
    val spawnRange: Int
)

private val spawnSpawnerItemKey = namespacedKeyOf("ecomobs", "spawn_spawner_item")
private val spawnSpawnerBlockKey = namespacedKeyOf("ecomobs", "spawn_spawner_block")

var ItemStack.ecoMobSpawner: EcoMob?
    get() = this.fast().ecoMobSpawner
    internal set(value) {
        this.fast().ecoMobSpawner = value
    }

var FastItemStack.ecoMobSpawner: EcoMob?
    get() {
        val id = this.persistentDataContainer.get(spawnSpawnerItemKey, PersistentDataType.STRING) ?: return null
        return EcoMobs[id]
    }
    internal set(value) {
        if (value == null) {
            this.persistentDataContainer.remove(spawnSpawnerItemKey)
        } else {
            this.persistentDataContainer.set(spawnSpawnerItemKey, PersistentDataType.STRING, value.id)
        }
    }

var CreatureSpawner.ecoMobSpawner: EcoMob?
    get() {
        val id = this.persistentDataContainer.get(spawnSpawnerBlockKey, PersistentDataType.STRING) ?: return null
        return EcoMobs[id]
    }
    internal set(value) {
        if (value == null) {
            this.persistentDataContainer.remove(spawnSpawnerBlockKey)
        } else {
            this.persistentDataContainer.set(spawnSpawnerBlockKey, PersistentDataType.STRING, value.id)
        }
    }

