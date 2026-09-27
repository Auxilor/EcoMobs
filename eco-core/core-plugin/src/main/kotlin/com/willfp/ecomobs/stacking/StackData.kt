package com.willfp.ecomobs.stacking

import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

val stackSizeKey = namespacedKeyOf("ecomobs", "stack_size")

/**
 * How many mobs a mob stands in for, stored in [pdc] so a stack survives chunk
 * unloads, restarts, and anything else that takes the entity out of memory.
 */
@JvmInline
value class StackData(val pdc: PersistentDataContainer) {
    var size: Int
        get() = pdc.get(stackSizeKey, PersistentDataType.INTEGER) ?: 1
        set(value) {
            // A stack of one is just a mob, so it carries no data at all.
            if (value <= 1) {
                pdc.remove(stackSizeKey)
            } else {
                pdc.set(stackSizeKey, PersistentDataType.INTEGER, value)
            }
        }

    val isStacked: Boolean
        get() = size > 1
}

val Mob.stack: StackData
    get() = StackData(persistentDataContainer)
