package com.willfp.ecomobs.event

import org.bukkit.entity.Mob

/**
 * An event about a stack of mobs. Vanilla mobs stack too, so these carry the bukkit
 * entity rather than a [com.willfp.ecomobs.mob.LivingMob].
 */
interface MobStackEvent {
    /**
     * The mob that stands in for the stack.
     */
    val entity: Mob
}
