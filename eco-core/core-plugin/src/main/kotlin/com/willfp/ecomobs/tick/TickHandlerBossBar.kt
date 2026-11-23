package com.willfp.ecomobs.tick

import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.options.BossBarOptions
import com.willfp.ecomobs.plugin
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import wiki.ecohub.game.entity.getAttributeValue
import wiki.ecohub.game.entity.player.asAudience
import wiki.ecohub.ui.text.toComponent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val warnedTypes = ConcurrentHashMap.newKeySet<EntityType>()

class TickHandlerBossBar(
    private val bar: BossBar,
    private val options: BossBarOptions
) : TickHandler {
    private val viewers = ConcurrentHashMap<UUID, Player>()

    override fun tick(mob: LivingMob, tick: Int) {
        if (tick % 5 != 0) {
            return
        }

        val entity = mob.entity
        val maxHealth = entity.getAttributeValue("minecraft:max_health")

        if (maxHealth == null) {
            if (warnedTypes.add(entity.type)) {
                plugin.logger.warning("${entity.type} has no max health, so its boss bar is not shown")
            }
            return
        }

        bar.name(mob.displayName.toComponent())
        bar.progress((entity.health / maxHealth).coerceAtMost(1.0).toFloat())

        // Only run every 2 seconds to save CPU
        if (tick % 40 == 0) {
            updateViewers(entity)
        }
    }

    private fun updateViewers(entity: LivingEntity) {
        val nearby = entity.getNearbyEntities(
            options.radius,
            options.radius,
            options.radius
        ).filterIsInstance<Player>()
            .associateBy { it.uniqueId }

        for ((uuid, player) in viewers) {
            if (uuid !in nearby) {
                viewers.remove(uuid)
                hide(player)
            }
        }

        // Nearby entities are in the mob's own region, so these dispatches resolve
        // inline. They go through onEntity anyway for the players on the far side of a
        // region border.
        for ((uuid, player) in nearby) {
            if (viewers.put(uuid, player) !== player) {
                onEntity(player) {
                    player.asAudience().showBossBar(bar)
                }
            }
        }
    }

    override fun onRemove(mob: LivingMob, tick: Int) {
        for (player in viewers.values) {
            hide(player)
        }
        viewers.clear()
    }

    /**
     * Each player belongs to whichever region is ticking them, so the bar is hidden on
     * their own thread.
     */
    private fun hide(player: Player) {
        if (!player.isOnline) {
            return
        }

        onEntity(player) {
            player.asAudience().hideBossBar(bar)
        }
    }
}
