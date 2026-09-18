package com.willfp.ecomobs.spawner

import org.bukkit.Location
import org.bukkit.World
import java.util.concurrent.ConcurrentHashMap

object PlacedSpawners {
    private val loaded = ConcurrentHashMap<Location, PlacedSpawner>()

    fun set(location: Location, spawner: PlacedSpawner) {
        loaded[location] = spawner
    }

    fun remove(location: Location) {
        loaded.remove(location)
    }

    fun removeWorld(world: World) {
        loaded.keys.removeIf { it.isWorldLoaded && it.world == world }
    }

    fun clear() {
        loaded.clear()
    }

    fun values(): Collection<PlacedSpawner> = loaded.values
}
