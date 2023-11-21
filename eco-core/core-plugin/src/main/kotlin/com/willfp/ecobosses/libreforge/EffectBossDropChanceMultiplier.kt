package com.willfp.ecobosses.libreforge

import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecobosses.bosses.EcoBoss
import com.willfp.ecobosses.events.BossTryDropItemEvent
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

object EffectBossDropChanceMultiplier : MultiMultiplierEffect<EcoBoss>("boss_drop_chance_multiplier") {
    override val key = "bosses"

    override fun getElement(key: String): EcoBoss? {
        return Bosses.getByID(key)
    }

    override fun getAllElements(): Collection<EcoBoss> {
        return Bosses.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossTryDropItemEvent) {
        val player = event.player ?: return

        val multiplier = getMultiplier(player.toDispatcher(), event.boss)

        event.chance *= multiplier
    }
}
