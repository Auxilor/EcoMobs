package com.willfp.ecomobs.mob.damage

import com.willfp.eco.util.savedDisplayName
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.libreforge.NamedValue
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
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

class TopDamagerHandler(private val plugin: EcoMobsPlugin) : Listener {
    private val places: Int
        get() = plugin.configYml.getInt("top-damager-places")

    @Suppress("UNCHECKED_CAST")
    private var Mob.topDamagers: List<Damager>
        get() = (this.getMetadata(metaKey).getOrNull(0)?.value() as? List<Damager>) ?: emptyList()
        set(value) {
            this.removeMetadata(metaKey, plugin)
            this.setMetadata(metaKey, plugin.metadataValueFactory.create(value))
        }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return
        val victim = event.entity as? Mob ?: return

        // Staged mobs zero their damage before this runs, so DamageStageHandler credits them.
        if (victim.ecoMob?.usesDamageStages == true) {
            return
        }

        credit(victim, player, event.damage)
    }

    fun credit(victim: Mob, player: Player, amount: Double) {
        if (amount <= 0.0) {
            return
        }

        val uuid = player.uniqueId
        val topDamagers = victim.topDamagers.toMutableList()

        val damager = topDamagers.firstOrNull { it.uuid == uuid } ?: Damager(uuid, 0.0)
        damager.damage += amount
        topDamagers.removeIf { it.uuid == uuid }
        topDamagers.add(damager)
        victim.topDamagers = topDamagers.sortedByDescending { it.damage }
    }

    fun generatePlaceholders(mob: Mob): List<NamedValue> {
        val topDamagers = mob.topDamagers

        return (0 until places).flatMap { index ->
            val damager = topDamagers.getOrNull(index)
            val offlinePlayer = damager?.let { Bukkit.getOfflinePlayer(it.uuid) }

            listOf(
                NamedValue(
                    "top_damager_${index + 1}_name",
                    if (damager == null) "" else offlinePlayer?.name ?: "Unknown"
                ),
                NamedValue(
                    "top_damager_${index + 1}_display",
                    if (damager == null) "" else offlinePlayer?.savedDisplayName ?: "Unknown"
                ),
                NamedValue(
                    "top_damager_${index + 1}_damage",
                    damager?.damage?.toNiceString() ?: "0"
                )
            )
        }
    }
}
