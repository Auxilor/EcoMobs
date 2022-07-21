package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss

class TargetTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val entity = boss.entity

        if (tick % 10 != 0) {
            return
        }

        entity.target = boss.boss.targetMode.getTarget(boss) ?: return
    }
}
