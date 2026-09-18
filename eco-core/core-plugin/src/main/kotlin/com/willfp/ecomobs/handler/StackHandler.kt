package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobStackDeathEvent
import com.willfp.ecomobs.event.EcoMobStackSplitEvent
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.stacking.MobStacks
import com.willfp.ecomobs.stacking.StackSettings
import com.willfp.ecomobs.stacking.stack
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent

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

        val single = event.drops.map { it.clone() }

        repeat(size - 1) {
            event.drops.addAll(single.map { it.clone() })
        }

        event.droppedExp *= size
    }

    /**
     * Drops and XP for the dead mob are left alone, and what's left of the stack comes
     * back as a fresh mob at full health.
     */
    private fun respawnRemainder(mob: Mob, remaining: Int) {
        val location = mob.location
        val ecoMob = mob.ecoMob
        val type = mob.type

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
                spawned.stack.size = left
            }
        }
    }
}
