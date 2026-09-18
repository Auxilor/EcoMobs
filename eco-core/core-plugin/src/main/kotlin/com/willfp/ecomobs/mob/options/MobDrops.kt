package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.util.randDouble
import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class Drop(
    val chance: Double,
    val items: List<TestableItem>
)

data class MobDrops(
    val experience: Int,
    val drops: List<Drop>
) {
    /**
     * Rolls the drop table, without giving anything out. Split from [give] so the drops
     * can be shown to [com.willfp.ecomobs.event.EcoMobDropsEvent] before they land.
     *
     * A drop that hits its chance gives every one of its items to a player, and one of
     * them at random when there is nobody to credit.
     */
    fun roll(player: Player?): MutableList<ItemStack> {
        val rolled = mutableListOf<ItemStack>()

        for (drop in drops) {
            if (randDouble(0.0, 100.0) > drop.chance) {
                continue
            }

            if (player != null) {
                rolled += drop.items.map { it.item }
            } else {
                rolled += drop.items.random().item
            }
        }

        return rolled
    }

    /**
     * Gives out [items] and [xp], to [player] where there is one and on the ground at
     * [location] otherwise.
     */
    fun give(location: Location, player: Player?, items: List<ItemStack>, xp: Int) {
        if (player != null) {
            DropQueue(player)
                .addXP(xp)
                .addItems(items)
                .push()

            return
        }

        val world = location.world ?: throw IllegalStateException("Location has no world")

        for (item in items) {
            world.dropItemNaturally(location, item)
        }

        world.spawn(location, ExperienceOrb::class.java).apply {
            experience = xp
        }
    }
}
