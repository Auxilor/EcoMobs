package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecobosses.lifecycle.BossLifecycle
import com.willfp.ecobosses.tick.BossTicker
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import java.util.UUID

class LivingEcoBoss(
    plugin: EcoPlugin,
    private val uuid: UUID,
    val boss: EcoBoss,
    val tickers: Set<BossTicker>
) {
    init {
        plugin.runnableFactory.create {
            if (tick()) {
                it.cancel()
            }
        }
    }

    val entity: Mob?
        get() = Bukkit.getEntity(uuid) as? Mob

    val deathTime = System.currentTimeMillis() + (boss.lifespan * 1000)

    private var currentTick = 0

    private fun tick(): Boolean {
        if (entity == null || entity?.isDead == true) {
            for (ticker in tickers) {
                ticker.onDeath(this, currentTick)
            }
            boss.markDead(uuid)
            return true
        }

        for (ticker in tickers) {
            ticker.tick(this, currentTick)
        }
        currentTick++
        return false
    }

    fun kill(reason: BossLifecycle) {
        if (reason == BossLifecycle.SPAWN) {
            throw IllegalArgumentException("Spawn is not a death lifecycle!")
        }

        entity?.remove()
        boss.markDead(uuid)
    }
}
