package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.ecomobs.event.EcoMobSpawnerStackEvent
import org.bukkit.Bukkit
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

        val stackEvent = EcoMobSpawnerStackEvent(
            state.location,
            state.spawner.mob,
            current,
            minOf(amount, space)
        )

        Bukkit.getPluginManager().callEvent(stackEvent)

        if (stackEvent.isCancelled) {
            return 0
        }

        // Capped again, as a listener is free to raise the amount past what fits.
        val taken = stackEvent.amount.coerceIn(0, space)

        if (taken <= 0) {
            return 0
        }

        state.spawner.stackSize = current + taken
        state.update()

        SpawnerHolograms.refresh(state.location)

        return taken
    }
}
