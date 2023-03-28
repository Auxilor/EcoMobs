package com.willfp.ecobosses.tick

import com.willfp.eco.util.asAudience
import com.willfp.eco.util.toComponent
import com.willfp.ecobosses.bosses.LivingEcoBoss
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class BossBarTicker(
    private val bar: BossBar
) : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val entity = boss.entity

        @Suppress("DEPRECATION")
        bar.name(entity.customName!!.toComponent())
        bar.progress((entity.health / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value).toFloat())

        if (tick % 40 == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                player.asAudience().hideBossBar(bar)
            }
            entity.getNearbyEntities(
                boss.boss.bossBarRadius,
                boss.boss.bossBarRadius,
                boss.boss.bossBarRadius
            ).filterIsInstance<Player>()
                .map { it.asAudience() }
                .forEach { it.showBossBar(bar) }
        }

        if (tick % 10 != 0) {
            return
        }

        entity.target = boss.boss.targetMode.getTarget(boss) ?: return
    }

    override fun onDeath(boss: LivingEcoBoss, tick: Int) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.asAudience().hideBossBar(bar)
        }
    }
}
