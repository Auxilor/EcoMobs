package com.willfp.ecomobs.spawner

import org.bukkit.Location
import java.util.concurrent.ConcurrentHashMap

object PlacedSpawners {
    private val loaded = ConcurrentHashMap<Location, PlacedSpawner>()

    fun set(location: Location, spawner: PlacedSpawner) {
        loaded[location] = spawner
    }

    fun remove(location: Location) {
        loaded.remove(location)
    }

    fun values(): List<PlacedSpawner> = loaded.values.toList()
}
