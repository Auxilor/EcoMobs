package com.willfp.ecomobs.tick

import com.willfp.eco.util.setClientsideDisplayName
import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.stacking.stack
import kotlin.math.abs

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

        val location = mob.entity.location
        val players = mob.entity.trackedBy.filter {
            val playerLocation = it.location
            abs(playerLocation.x - location.x) <= 20 &&
                abs(playerLocation.y - location.y) <= 20 &&
                abs(playerLocation.z - location.z) <= 20
        }
        if (players.isEmpty()) {
            return
        }

        val name = mob.displayName.toComponent()
        for (player in players) {
            mob.entity.setClientsideDisplayName(player, name, false)
        }
    }
}
