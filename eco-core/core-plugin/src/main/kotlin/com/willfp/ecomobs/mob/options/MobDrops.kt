package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.util.randDouble
import com.willfp.libreforge.GlobalDispatcher.location
import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player

data class Drop(
    val chance: Double,
    val items: List<TestableItem>
)

data class MobDrops(
    val experience: Int,
    val drops: List<Drop>
) {
    fun drop(location: Location, player: Player?) {
        if (player != null) {
            val queue = DropQueue(player)
                .addXP(experience)

            for (drop in drops) {
                if (randDouble(0.0, 100.0) <= drop.chance) {
                    queue.addItems(drop.items.map { it.item })
                }
            }

            queue.push()
        } else {
            val world = location.world ?: throw IllegalStateException("Location has no world")

            for (drop in drops) {
                if (randDouble(0.0, 100.0) <= drop.chance) {
                    world.dropItemNaturally(location, drop.items.random().item)
                }
            }

            world.spawn(location, ExperienceOrb::class.java).apply {
                experience = experience
            }
        }
    }
}
