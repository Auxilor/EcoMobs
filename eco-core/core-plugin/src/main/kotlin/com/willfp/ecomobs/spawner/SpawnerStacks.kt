package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.FastItemStack
import org.bukkit.block.CreatureSpawner

object SpawnerStacks {
    /**
     * Adds up to [amount] spawners from [item] onto the stack at [state], returning how
     * many were actually taken. Zero means the two don't match, or the stack is full.
     */
    fun add(state: CreatureSpawner, item: FastItemStack, amount: Int): Int {
        if (!SpawnerStackSettings.enabled) {
            return 0
        }

        if (!state.spawner.isCustomSpawner || !item.spawner.isCustomSpawner) {
            return 0
        }

        if (!state.spawner.matches(item.spawner)) {
            return 0
        }

        val current = state.spawner.stackSize
        val space = SpawnerStackSettings.maxSize - current

        if (space <= 0 || amount <= 0) {
            return 0
        }

        val taken = minOf(amount, space)

        state.spawner.stackSize = current + taken
        state.update()

        SpawnerHolograms.refresh(state.location)

        return taken
    }
}
