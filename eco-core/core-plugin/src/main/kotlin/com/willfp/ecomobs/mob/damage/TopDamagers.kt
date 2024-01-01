package com.willfp.ecomobs.mob.damage

import com.willfp.eco.util.savedDisplayName
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.libreforge.NamedValue
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.UUID

/*
Old code mostly ported from EcoBosses, can't be bothered to write it again
 */

data class Damager(
    val uuid: UUID, var damage: Double
)

private const val metaKey = "TOP_DAMAGERS"

class TopDamagerHandler(
    private val plugin: EcoMobsPlugin
) : Listener {
    @Suppress("UNCHECKED_CAST")
    private var Mob.topDamagers: List<Damager>
        get() = (this.getMetadata(metaKey).getOrNull(0)?.value() as? List<Damager>) ?: emptyList()
        set(value) {
            this.removeMetadata(metaKey, plugin)
            this.setMetadata(metaKey, plugin.metadataValueFactory.create(value))
        }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
        val uuid = event.damager.tryAsPlayer()?.uniqueId ?: return
        val victim = event.entity as? Mob ?: return

        val topDamagers = victim.topDamagers.toMutableList()

        val damager = topDamagers.firstOrNull { it.uuid == uuid } ?: Damager(uuid, 0.0)
        damager.damage += event.damage
        topDamagers.removeIf { it.uuid == uuid }
        topDamagers.add(damager)
        victim.topDamagers = topDamagers.sortedByDescending { it.damage }
    }

    fun generatePlaceholders(mob: Mob): List<NamedValue> {
        return mob.topDamagers
            .flatMapIndexed { index, damager ->
                listOf(
                    NamedValue(
                        "top_damager_${index + 1}_name",
                        Bukkit.getOfflinePlayer(damager.uuid).name ?: "Unknown"
                    ),
                    NamedValue(
                        "top_damager_${index + 1}_display",
                        Bukkit.getOfflinePlayer(damager.uuid).savedDisplayName
                    ),
                    NamedValue(
                        "top_damager_${index + 1}_damage",
                        damager.damage.toString()
                    )
                )
            }
    }
}
