package com.willfp.ecomobs.tick

import com.willfp.eco.util.asAudience
import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.options.BossBarOptions
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class TickHandlerBossBar(
    private val bar: BossBar,
    private val options: BossBarOptions
) : TickHandler {
    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 5 != 0) {
            return
        }

        val entity = mob.entity
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
            ?: throw IllegalStateException("Entity ${entity.type} has no max health attribute")

        bar.name(mob.displayName.toComponent())
        bar.progress((entity.health / maxHealth).coerceAtMost(1.0).toFloat())

        // Only run every 2 seconds to save CPU
        if (tick % 40 != 0) {
            return
        }

        for (player in Bukkit.getOnlinePlayers()) {
            player.asAudience().hideBossBar(bar)
        }

        entity.getNearbyEntities(
            options.radius,
            options.radius,
            options.radius
        ).filterIsInstance<Player>()
            .map { it.asAudience() }
            .forEach { it.showBossBar(bar) }
    }

    override fun onRemove(mob: LivingMob, tick: Int) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.asAudience().hideBossBar(bar)
        }
    }
}
