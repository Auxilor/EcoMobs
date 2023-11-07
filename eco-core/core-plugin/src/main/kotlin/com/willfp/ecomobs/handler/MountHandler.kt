package com.willfp.ecomobs.handler

import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.spigotmc.event.entity.EntityMountEvent

class MountHandler : Listener {
    @EventHandler(
        ignoreCancelled = true
    )
    fun handle(event: EntityMountEvent) {
        val bukkitEntity = event.entity as? Mob ?: return
        val mob = bukkitEntity.ecoMob ?: return

        if (!mob.canMount) {
            event.isCancelled = true
        }
    }
}
