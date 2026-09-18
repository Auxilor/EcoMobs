package com.willfp.ecomobs.stacking

import com.willfp.ecomobs.event.EcoMobStackMergeEvent
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Ageable
import org.bukkit.entity.Mob
import org.bukkit.entity.Tameable

object MobStacks {
    /**
     * Whether [mob] is allowed to stack at all.
     *
     * Only mobs get this far: players, armour stands, items, projectiles and every
     * other entity are ruled out by the type itself.
     */
    fun canStack(mob: Mob): Boolean {
        if (!StackSettings.enabled) {
            return false
        }

        if (!mob.isValid) {
            return false
        }

        if (isBlacklisted(mob)) {
            return false
        }

        if (StackSettings.excludeTamed && (mob as? Tameable)?.isTamed == true) {
            return false
        }

        if (StackSettings.excludeLeashed && mob.isLeashed) {
            return false
        }

        if (StackSettings.excludeMounted && mob.vehicle != null) {
            return false
        }

        if (StackSettings.excludeRidden && mob.passengers.isNotEmpty()) {
            return false
        }

        // EcoMobs paint their names clientside, so a real one always came from elsewhere.
        if (StackSettings.excludeNamed && mob.ecoMob == null && mob.customName() != null) {
            return false
        }

        return true
    }

    private fun isBlacklisted(mob: Mob): Boolean {
        val blacklist = StackSettings.blacklist

        if (blacklist.isEmpty()) {
            return false
        }

        if (mob.type.name.lowercase() in blacklist) {
            return true
        }

        val ecoMobId = mob.ecoMob?.id?.lowercase() ?: return false

        return ecoMobId in blacklist
    }

    /**
     * What decides whether two mobs can merge. Mobs of the same kind stack together,
     * and babies are kept out of adult stacks unless the config says otherwise.
     */
    fun stackKey(mob: Mob): String {
        val id = mob.ecoMob?.id ?: mob.type.name.lowercase()

        if (!StackSettings.matchAge) {
            return id
        }

        val isAdult = (mob as? Ageable)?.isAdult ?: true

        return "$id:$isAdult"
    }

    /**
     * Merges [mob] into the nearest stack that has room for it, returning whether it
     * was absorbed. The absorbed mob is removed; the stack it joined grows by its size.
     */
    fun tryMerge(mob: Mob): Boolean {
        if (!canStack(mob)) {
            return false
        }

        val size = mob.stack.size
        val key = stackKey(mob)
        val radius = StackSettings.radius

        val target = mob.getNearbyEntities(radius, radius, radius)
            .asSequence()
            .filterIsInstance<Mob>()
            .filter { it.uniqueId != mob.uniqueId }
            .filter { canStack(it) }
            .filter { stackKey(it) == key }
            .filter { it.stack.size + size <= StackSettings.maxSize }
            .minByOrNull { it.location.distanceSquared(mob.location) }
            ?: return false

        val mergeEvent = EcoMobStackMergeEvent(mob, target, target.stack.size + size)
        Bukkit.getPluginManager().callEvent(mergeEvent)

        if (mergeEvent.isCancelled) {
            return false
        }

        target.stack.size += size
        remove(mob)

        return true
    }

    /**
     * Adds up to [amount] mobs of [mobId] to the nearest stack around [location] with
     * room for them, returning how many were taken. Zero means there was no stack there
     * to join.
     *
     * This is how a spawner puts a whole cycle into the world without putting an entity
     * per mob into it: the mobs become stack size on something already standing there.
     * Nothing is spawned, so [EcoMobStackMergeEvent] is not called - no mob was absorbed
     * into another.
     */
    fun addToNearbyStack(location: Location, mobId: String, amount: Int): Int {
        if (!StackSettings.enabled || amount <= 0) {
            return 0
        }

        val world = location.world ?: return 0
        val radius = StackSettings.radius
        val maxSize = StackSettings.maxSize

        val target = world.getNearbyEntities(location, radius, radius, radius)
            .asSequence()
            .filterIsInstance<Mob>()
            .filter { it.stack.size < maxSize }
            .filter { isKind(it, mobId) }
            .filter { canStack(it) }
            .minByOrNull { it.location.distanceSquared(location) }
            ?: return 0

        val taken = minOf(amount, maxSize - target.stack.size)

        target.stack.size += taken

        return taken
    }

    /**
     * Whether [mob] is what a spawner set to [mobId] spawns, for both EcoMobs and plain
     * entity types. Babies are left out while babies keep their own stacks, as the mobs
     * a spawner makes are adults.
     */
    private fun isKind(mob: Mob, mobId: String): Boolean {
        if (StackSettings.matchAge && (mob as? Ageable)?.isAdult == false) {
            return false
        }

        val ecoMobId = mob.ecoMob?.id

        if (ecoMobId != null) {
            return ecoMobId.equals(mobId, ignoreCase = true)
        }

        return mob.type.name.equals(mobId, ignoreCase = true)
    }

    /**
     * Removes an absorbed mob, going through EcoMobs' own despawn so its tracking and
     * tick handlers are torn down with it.
     */
    private fun remove(mob: Mob) {
        val living = mob.ecoMob?.getLivingMob(mob)

        if (living != null) {
            living.despawn()
        } else {
            mob.remove()
        }
    }
}
