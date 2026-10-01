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
import java.util.concurrent.ConcurrentHashMap

/*
Old code mostly ported from EcoBosses, can't be bothered to write it again
 */

data class Damager(
    val uuid: UUID, var damage: Double
)

class TopDamagerHandler(private val plugin: EcoMobsPlugin) : Listener {
    private val places: Int
        get() = plugin.configYml.getInt("top-damager-places")

    /**
     * Damage per mob, keyed by the mob's UUID.
     *
     * Held here rather than in Bukkit metadata, whose backing store is one server-wide
     * HashMap that every region thread would be writing to at once. Entries are dropped
     * in [forget] when the mob is removed.
     */
    private val damagers = ConcurrentHashMap<UUID, List<Damager>>()

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return
        val victim = event.entity as? Mob ?: return

        // Only our own mobs are credited: nothing reads the damage of anything else, and
        // crediting every mob on the server would grow the map without bound.
        val ecoMob = victim.ecoMob ?: return

        // Staged mobs zero their damage before this runs, so DamageStageHandler credits them.
        if (ecoMob.usesDamageStages) {
            return
        }

        credit(victim, player, event.damage)
    }

    fun credit(victim: Mob, player: Player, amount: Double) {
        if (amount <= 0.0) {
            return
        }

        val uuid = player.uniqueId

        // A single atomic rebuild, so two players damaging the same mob from either side
        // of a region border can't lose each other's contribution.
        damagers.compute(victim.uniqueId) { _, existing ->
            val updated = (existing ?: emptyList())
                .filter { it.uuid != uuid }
                .toMutableList()

            val previous = existing?.firstOrNull { it.uuid == uuid }?.damage ?: 0.0

            updated.add(Damager(uuid, previous + amount))
            updated.sortedByDescending { it.damage }
        }
    }

    /**
     * Drops the damage recorded against a mob, once it is gone.
     */
    fun forget(uuid: UUID) {
        damagers.remove(uuid)
    }

    fun generatePlaceholders(mob: Mob): List<NamedValue> {
        val topDamagers = damagers[mob.uniqueId] ?: emptyList()

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
