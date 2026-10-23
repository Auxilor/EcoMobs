package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.display.BaseItem
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpawnEgg internal constructor(
    val mob: EcoMob,
    val conditions: ConditionList,
    val item: BaseItem
) {
    /**
     * Spawns the mob if the egg's conditions are met.
     *
     * With no [player] (a dispenser), the conditions are checked against the nearest
     * player within [DISPENSER_PLAYER_RANGE] blocks; an egg with conditions and no
     * player nearby does not spawn.
     */
    fun trySpawn(location: Location, player: Player?): LivingMob? {
        val conditionPlayer = player ?: location.nearestPlayer()

        val canSpawn = if (conditionPlayer == null) {
            conditions.isEmpty()
        } else {
            conditions.areMetAndTrigger(
                TriggerData(
                    player = conditionPlayer
                ).dispatch(conditionPlayer.toDispatcher())
            )
        }

        return if (canSpawn) mob.spawn(location, SpawnReason.EGG) else null
    }
}

private const val DISPENSER_PLAYER_RANGE = 5.0

private fun Location.nearestPlayer(): Player? {
    val world = this.world ?: return null

    return world.getNearbyEntities(
        this,
        DISPENSER_PLAYER_RANGE,
        DISPENSER_PLAYER_RANGE,
        DISPENSER_PLAYER_RANGE
    ) { it is Player }
        .minByOrNull { it.location.distanceSquared(this) } as? Player
}

private val spawnEggKey = namespacedKeyOf("ecomobs", "spawn_egg")

var ItemStack.ecoMobEgg: EcoMob?
    get() = this.fast().ecoMobEgg
    internal set(value) {
        this.fast().ecoMobEgg = value
    }

var FastItemStack.ecoMobEgg: EcoMob?
    get() {
        val id = this.persistentDataContainer.get(spawnEggKey, PersistentDataType.STRING) ?: return null
        return EcoMobs[id]
    }
    internal set(value) {
        if (value == null) {
            this.persistentDataContainer.remove(spawnEggKey)
        } else {
            this.persistentDataContainer.set(spawnEggKey, PersistentDataType.STRING, value.id)
        }
    }