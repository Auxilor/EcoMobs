package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpawnEgg internal constructor(
    val mob: EcoMob,
    val conditions: ConditionList,
    private val backingItem: ItemStack,
    private val rawDisplayName: String,
    private val rawLore: List<String>
) {
    fun getItem(player: Player?): ItemStack {
        return backingItem.clone().modify {
            this.setDisplayName(rawDisplayName.formatEco(player))
            this.addLoreLines(rawLore.formatEco(player))
        }
    }

    fun trySpawn(location: Location, player: Player?): LivingMob? {
        if (player != null) {
            val canSpawn = conditions.areMetAndTrigger(
                TriggerData(
                    player = player
                ).dispatch(player)
            )

            if (!canSpawn) {
                return null
            }
        }

        return mob.spawn(location, SpawnReason.EGG)
    }
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