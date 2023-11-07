package com.willfp.ecomobs.mob.event

import com.willfp.eco.core.registry.Registry
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

object MobEvents : Registry<MobEvent>() {
    init {
        register(MobEventAnyAttack)
        register(MobEventDamagePlayer)
        register(MobEventDeath)
        register(MobEventDespawn)
        register(MobEventInteract)
        register(MobEventKill)
        register(MobEventKillPlayer)
        register(MobEventMeleeAttack)
        register(MobEventRangedAttack)
        register(MobEventSpawn)
        register(MobEventTakeDamage)
    }
}
