package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobStackDeathEvent
import com.willfp.ecomobs.event.EcoMobStackSplitEvent
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.stacking.MobStacks
import com.willfp.ecomobs.stacking.StackSettings
import com.willfp.ecomobs.spawner.entityFromSpawnerKey
import com.willfp.ecomobs.stacking.stack
import org.bukkit.Bukkit
import org.bukkit.entity.Ageable
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.loot.Lootable
import org.bukkit.persistence.PersistentDataType
import java.util.Random

object StackHandler : Listener {

    /**
     * Every spawn method ends up here, so stacking doesn't need wiring per method.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleSpawn(event: CreatureSpawnEvent) {
        val mob = event.entity as? Mob ?: return

        // A tick later, so whatever spawned the mob has finished setting it up. EcoMobs
        // in particular are only marked as such after the spawn event has been called.
        plugin.scheduler.on(mob).run {
            MobStacks.tryMerge(mob)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleDeath(event: EntityDeathEvent) {
        val mob = event.entity as? Mob ?: return

        val size = mob.stack.size
        if (size <= 1) {
            return
        }

        val deathEvent = EcoMobStackDeathEvent(mob, size, StackSettings.killWholeStack)
        Bukkit.getPluginManager().callEvent(deathEvent)

        if (deathEvent.killWholeStack) {
            multiplyRewards(event, mob, size)
        } else {
            respawnRemainder(mob, size - 1)
        }

        if (StackSettings.hideDeathAnimation) {
            hideCorpse(mob)
        }
    }

    /**
     * Takes the corpse away a tick after the kill, so the twenty-tick death animation
     * never plays out next to what's left of the stack.
     *
     * Deferred rather than removed here, as taking the entity out during its own death
     * event loses the XP it was about to drop.
     */
    private fun hideCorpse(mob: Mob) {
        plugin.scheduler.at(mob.location).run {
            mob.remove()
        }
    }

    private fun multiplyRewards(event: EntityDeathEvent, mob: Mob, size: Int) {
        val ecoMob = mob.ecoMob

        // EcoMobs clear the vanilla drops and spawn their own, so the rest of the stack
        // is paid out by rolling their drop table again rather than by copying the list.
        if (ecoMob != null) {
            val location = mob.location
            val killer = mob.killer

            repeat(size - 1) {
                ecoMob.spawnDrops(location, killer)
            }

            return
        }

        val extra = size - 1

        // Each mob in the stack is paid out by rolling its loot table for itself, so a
        // one-in-a-hundred drop is one in a hundred sixty-four times over rather than
        // sixty-four of whatever the one mob that died happened to roll.
        val rolled = rollLoot(mob, extra)

        if (rolled != null) {
            event.drops.addAll(rolled)
        } else {
            // Nothing to roll: a mob without a loot table, or one whose drops another
            // plugin has already written. The list it left is the only guide there is.
            val single = event.drops.map { it.clone() }

            repeat(extra) {
                event.drops.addAll(single.map { it.clone() })
            }
        }

        // Experience isn't rolled - vanilla hands out a fixed amount per mob - so the
        // stack's worth is that amount per mob in it.
        event.droppedExp *= size
    }

    /**
     * Rolls [mob]'s own loot table [times] over, or null when it hasn't got one.
     *
     * Equipment the dead mob was wearing isn't part of a loot table, so it isn't handed
     * out again - each mob in the stack would have rolled its own gear, and there is no
     * way to know what the rest of them had.
     */
    private fun rollLoot(mob: Mob, times: Int): List<ItemStack>? {
        if (times <= 0) {
            return emptyList()
        }

        val table = (mob as? Lootable)?.lootTable ?: return null

        val context = LootContext.Builder(mob.location)
            .lootedEntity(mob)
            .killer(mob.killer)
            .build()

        val random = Random()
        val rolled = mutableListOf<ItemStack>()

        repeat(times) {
            rolled += table.populateLoot(random, context)
        }

        return rolled
    }

    /**
     * Drops and XP for the dead mob are left alone, and what's left of the stack comes
     * back as a fresh mob at full health.
     */
    private fun respawnRemainder(mob: Mob, remaining: Int) {
        val location = mob.location
        val ecoMob = mob.ecoMob
        val type = mob.type

        // Read off the dying mob, as it is gone by the time the replacement is spawned.
        // Without this the rest of a no-ai stack comes back with its AI, and the rest of
        // a spawner's stack comes back looking like it was never from a spawner.
        val wasAware = mob.isAware
        val wasAdult = (mob as? Ageable)?.isAdult ?: true
        val fromSpawner = mob.persistentDataContainer.has(entityFromSpawnerKey, PersistentDataType.BYTE)

        val splitEvent = EcoMobStackSplitEvent(mob, location, remaining)
        Bukkit.getPluginManager().callEvent(splitEvent)

        if (splitEvent.isCancelled) {
            return
        }

        val left = splitEvent.remaining

        if (left <= 0) {
            return
        }

        plugin.scheduler.at(location).run {
            val spawned = if (ecoMob != null) {
                ecoMob.spawn(location, SpawnReason.NATURAL)?.entity
            } else {
                location.world?.spawnEntity(location, type) as? Mob
            }

            if (spawned != null) {
                spawned.isAware = wasAware

                if (!wasAdult) {
                    (spawned as? Ageable)?.setBaby()
                }

                if (fromSpawner) {
                    spawned.persistentDataContainer.set(entityFromSpawnerKey, PersistentDataType.BYTE, 1)
                }

                spawned.stack.size = left
            }
        }
    }
}
