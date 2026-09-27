package com.willfp.ecomobs.tick

import com.willfp.eco.util.setClientsideDisplayName
import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.stacking.stack
import org.bukkit.entity.Player

class TickHandlerDisplayName : TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 5 != 0) {
            return
        }

        // A stacked mob's nameplate comes from the stacking loop instead, so the two
        // don't fight over the same clientside name.
        if (mob.entity.stack.isStacked) {
            return
        }

        mob.entity.getNearbyEntities(20.0, 20.0, 20.0)
            .filterIsInstance<Player>()
            .forEach { mob.entity.setClientsideDisplayName(it, mob.displayName.toComponent(), false) }
    }
}
