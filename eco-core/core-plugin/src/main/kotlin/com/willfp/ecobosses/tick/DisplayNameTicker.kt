package com.willfp.ecobosses.tick

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecobosses.bosses.LivingEcoBoss
import kotlin.math.ceil

class DisplayNameTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val entity = boss.entity

        val timeLeft = ceil(
            (boss.deathTime - System.currentTimeMillis()) / 1000.0
        ).toInt()

        val formattedTime = String.format("%d:%02d", timeLeft / 60, timeLeft % 60)

        entity.customName = boss.boss.displayName
            .replace("%health%", NumberUtils.format(entity.health))
            .replace("%time%", formattedTime)
            .formatEco()
    }
}
