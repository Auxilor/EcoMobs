package com.willfp.ecobosses.defence

import com.willfp.ecobosses.bosses.Bosses
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.spigotmc.event.entity.EntityMountEvent

class MountHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityMountEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val boss = Bosses[entity]?.boss ?: return

        if (boss.isPreventingMounts) {
            event.isCancelled = true
        }
    }
}
