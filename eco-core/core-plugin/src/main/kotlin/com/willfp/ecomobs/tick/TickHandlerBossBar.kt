package com.willfp.ecomobs.tick

import com.willfp.eco.util.asAudience
import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.folia.onEntity
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
        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value
            ?: throw IllegalStateException("Entity ${entity.type} has no max health attribute")

        bar.name(mob.displayName.toComponent())
        bar.progress((entity.health / maxHealth).coerceAtMost(1.0).toFloat())

        // Only run every 2 seconds to save CPU
        if (tick % 40 != 0) {
            return
        }

        hideFromEveryone()

        // Nearby entities are in the mob's own region, so these dispatches resolve
        // inline. They go through onEntity anyway for the players on the far side of a
        // region border.
        entity.getNearbyEntities(
            options.radius,
            options.radius,
            options.radius
        ).filterIsInstance<Player>()
            .forEach { player ->
                onEntity(player) {
                    player.asAudience().showBossBar(bar)
                }
            }
    }

    override fun onRemove(mob: LivingMob, tick: Int) {
        hideFromEveryone()
    }

    /**
     * The player list itself is safe to read from anywhere, but each player belongs to
     * whichever region is ticking them, so the bar is hidden on their own thread.
     */
    private fun hideFromEveryone() {
        for (player in Bukkit.getOnlinePlayers()) {
            onEntity(player) {
                player.asAudience().hideBossBar(bar)
            }
        }
    }
}
