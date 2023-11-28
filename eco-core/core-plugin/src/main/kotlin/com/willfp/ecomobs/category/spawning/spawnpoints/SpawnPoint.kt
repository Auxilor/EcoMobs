package com.willfp.ecomobs.category.spawning.spawnpoints

import org.bukkit.Location

class SpawnPoint(
    val location: Location,
    val type: SpawnPointType
) {
    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return other is SpawnPoint &&
                this.location == other.location &&
                this.type == other.type
    }
}
