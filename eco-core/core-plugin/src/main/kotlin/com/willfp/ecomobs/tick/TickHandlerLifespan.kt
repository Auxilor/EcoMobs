package com.willfp.ecomobs.tick

import com.willfp.ecomobs.event.EcoMobLifespanExpireEvent
import com.willfp.ecomobs.mob.LivingMob
import org.bukkit.Bukkit

class TickHandlerLifespan: TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (mob.ticksLeft > 0) {
            return
        }

        val event = EcoMobLifespanExpireEvent(mob)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        mob.despawn()
    }
}
