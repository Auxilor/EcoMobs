package com.willfp.ecomobs.category.spawning.spawnpoints

import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.SpawnReason
import org.bukkit.Location

class SpawnPoint(
    val location: Location,
    val type: SpawnPointType
) {
    private var hasBeenUsed = false

    /**
     * Spawn a mob at this spawnpoint.
     */
    fun spawn(mob: EcoMob, reason: SpawnReason): LivingMob? {
        if (hasBeenUsed) {
            return null
        }

        hasBeenUsed = true

        return mob.spawn(location, reason)
    }

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
