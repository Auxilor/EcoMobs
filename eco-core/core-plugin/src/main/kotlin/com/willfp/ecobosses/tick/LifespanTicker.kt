package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss
import com.willfp.ecobosses.events.BossDespawnEvent
import com.willfp.ecobosses.lifecycle.BossLifecycle
import org.bukkit.Bukkit

class LifespanTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val timeLeft = (boss.deathTime - System.currentTimeMillis()) / 1000.0

        if (timeLeft <= 0) {
            val event = BossDespawnEvent(boss)
            Bukkit.getPluginManager().callEvent(event)

            boss.remove()
            boss.boss.handleLifecycle(
                BossLifecycle.DESPAWN,
                boss.entity.location,
                boss.entity
            )
        }
    }
}
