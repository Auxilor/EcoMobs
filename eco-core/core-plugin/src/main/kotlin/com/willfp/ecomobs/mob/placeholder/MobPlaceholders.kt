package com.willfp.ecomobs.mob.placeholder

import com.willfp.eco.core.registry.Registry
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.event.impl.MobEventAnyAttack
import com.willfp.ecomobs.mob.event.impl.MobEventDamagePlayer
import com.willfp.ecomobs.mob.event.impl.MobEventDeath
import com.willfp.ecomobs.mob.event.impl.MobEventDespawn
import com.willfp.ecomobs.mob.event.impl.MobEventInteract
import com.willfp.ecomobs.mob.event.impl.MobEventKill
import com.willfp.ecomobs.mob.event.impl.MobEventKillPlayer
import com.willfp.ecomobs.mob.event.impl.MobEventMeleeAttack
import com.willfp.ecomobs.mob.event.impl.MobEventRangedAttack
import com.willfp.ecomobs.mob.event.impl.MobEventSpawn
import com.willfp.ecomobs.mob.event.impl.MobEventTakeDamage
import com.willfp.ecomobs.mob.placeholder.impl.MobPlaceholderHealth
import com.willfp.ecomobs.mob.placeholder.impl.MobPlaceholderHealthPercent
import com.willfp.ecomobs.mob.placeholder.impl.MobPlaceholderMaxHealth
import com.willfp.ecomobs.mob.placeholder.impl.MobPlaceholderTime

object MobPlaceholders : Registry<MobPlaceholder>() {
    init {
        register(MobPlaceholderHealth)
        register(MobPlaceholderMaxHealth)
        register(MobPlaceholderHealthPercent)
        register(MobPlaceholderTime)
    }
}

fun String.formatMobPlaceholders(mob: LivingMob): String {
    var string = this
    MobPlaceholders.forEach { placeholder ->
        string = string.replace("%${placeholder.id}%", placeholder.getValue(mob))
    }
    return string
}
