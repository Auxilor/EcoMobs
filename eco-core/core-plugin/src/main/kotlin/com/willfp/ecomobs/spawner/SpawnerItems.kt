package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.ecomobs.plugin
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SPAWNER_SUFFIX = "_spawner"

/**
 * Spawners as eco items, looked up as `ecomobs:<mob>_spawner` anywhere an item
 * lookup is taken - drops, recipes, shops, and other plugins' configs.
 *
 * EcoMob spawners are registered as they load, and everything else - the vanilla
 * entity types - is provided on demand, as there are far too many of those to be
 * worth registering up front. Eco caches whatever the provider hands back, so
 * each one is only built once.
 */
object SpawnerItems : ItemProvider("ecomobs") {
    /**
     * A spawner item for [mobId], with every attribute left at its default.
     */
    fun spawnerItem(mobId: String): ItemStack {
        val fis = ItemStack(Material.SPAWNER).fast()
        fis.spawner.mob = mobId
        return fis.unwrap()
    }

    /**
     * Registers the spawner for [mobId] as a custom item, so that it can be both
     * looked up and recognised without having been looked up first.
     */
    fun register(mobId: String) {
        customItem(mobId).register()
    }

    override fun provideForKey(key: String): TestableItem {
        val mobId = key.removeSuffix(SPAWNER_SUFFIX)

        // Not a spawner lookup at all, or a mob that doesn't exist.
        if (mobId == key || !SpawnerAttributes.isValidMob(mobId)) {
            return EmptyTestableItem()
        }

        return customItem(mobId)
    }

    // Spawners are matched on their mob alone: the rest of the attributes are
    // what a single spawner was given, not what makes it that kind of spawner.
    private fun customItem(mobId: String) = CustomItem(
        plugin.createNamespacedKey("$mobId$SPAWNER_SUFFIX"),
        { it.fast().spawner.mob.equals(mobId, ignoreCase = true) },
        spawnerItem(mobId)
    )
}
