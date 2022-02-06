package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecobosses.tick.BossTicker
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import java.util.UUID

class LivingEcoBoss(
    plugin: EcoPlugin,
    private val uuid: UUID,
    val boss: EcoBoss,
    private val tickers: Set<BossTicker>
) {
    private val ticker = plugin.runnableFactory.create {
        if (tick()) {
            it.cancel()
        }
    }.apply { runTaskTimer(1, 1) }

    val entity: Mob?
        get() = Bukkit.getEntity(uuid) as? Mob

    val deathTime = System.currentTimeMillis() + (boss.lifespan * 1000)

    private var currentTick = 1 // Start at 1 as 0 is divisible by everything

    private fun tick(): Boolean {
        if (entity == null || entity?.isDead == true) {
            remove()
            return true
        }

        for (ticker in tickers) {
            ticker.tick(this, currentTick)
        }
        currentTick++
        return false
    }

    fun remove() {
        ticker.cancel()
        entity?.remove()
        tickers.forEach { it.onDeath(this, currentTick) }

        boss.markDead(uuid)
    }
}
