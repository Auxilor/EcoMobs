package com.willfp.ecomobs.spawner

import org.bukkit.Location

class PlacedSpawner(
    val location: Location,
    val animationId: String?
) {
    fun tickAsync(tick: Int) {
        val id = animationId?.takeIf { it != "none" } ?: return
        val data = SpawnerAnimations[id] ?: return
        data.animation.spawnParticle(
            location.clone().add(0.5, 0.5, 0.5),
            tick,
            data.particle
        )
    }
}
