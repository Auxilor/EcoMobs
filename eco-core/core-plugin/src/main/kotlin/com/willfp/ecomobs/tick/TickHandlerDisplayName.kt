package com.willfp.ecomobs.tick

import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.name.setClientDisplayName
import org.bukkit.entity.Player

class TickHandlerDisplayName : TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 5 != 0) {
            return
        }

        mob.entity.getNearbyEntities(20.0, 20.0, 20.0)
            .filterIsInstance<Player>()
            .forEach { mob.entity.setClientDisplayName(it, mob.displayName, false) }
    }
}
