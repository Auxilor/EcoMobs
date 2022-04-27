package com.willfp.ecobosses.util

import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecobosses.EcoBossesPlugin
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

data class Damager(
    val uuid: UUID,
    var damage: Double
)

private const val metaKey = "TOP_DAMAGERS"

@Suppress("UNCHECKED_CAST")
var LivingEntity.topDamagers: List<Damager>
    get() = (this.getMetadata(metaKey).getOrNull(0)?.value() as? List<Damager>) ?: emptyList()
    private set(value) {
        this.removeMetadata(metaKey, EcoBossesPlugin.instance)
        this.setMetadata(metaKey, EcoBossesPlugin.instance.metadataValueFactory.create(value))
    }

class TopDamagerListener : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
        val uuid = event.damager.tryAsPlayer()?.uniqueId ?: return

        val victim = event.entity as? LivingEntity ?: return

        val topDamagers = victim.topDamagers.toMutableList()

        val damager = topDamagers.firstOrNull { it.uuid == uuid } ?: Damager(uuid, 0.0)
        damager.damage += event.damage
        topDamagers.removeIf { it.uuid == uuid }
        topDamagers.add(damager)
        victim.topDamagers = topDamagers.sortedByDescending { it.damage }
    }
}
