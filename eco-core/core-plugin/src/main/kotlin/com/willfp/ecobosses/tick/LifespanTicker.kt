package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.BossLifecycle
import com.willfp.ecobosses.bosses.LivingEcoBoss

class LifespanTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val timeLeft = (boss.deathTime - System.currentTimeMillis()) / 1000.0

        if (timeLeft <= 0) {
            boss.kill(BossLifecycle.DESPAWN)
        }
    }
}
