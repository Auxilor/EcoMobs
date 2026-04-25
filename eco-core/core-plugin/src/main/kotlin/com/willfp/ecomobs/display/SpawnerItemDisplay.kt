package com.willfp.ecomobs.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.isCustomSpawner
import com.willfp.ecomobs.spawner.spawnerDelayMax
import com.willfp.ecomobs.spawner.spawnerDelayMin
import com.willfp.ecomobs.spawner.spawnerMaxNearby
import com.willfp.ecomobs.spawner.spawnerMob
import com.willfp.ecomobs.spawner.spawnerParticleAnim
import com.willfp.ecomobs.spawner.spawnerPickup
import com.willfp.ecomobs.spawner.spawnerPlayerRange
import com.willfp.ecomobs.spawner.spawnerSpawnCount
import com.willfp.ecomobs.spawner.spawnerSpawnRange
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SpawnerItemDisplay : DisplayModule(plugin, DisplayPriority.LOW) {
    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        if (player == null) return
        val fis = itemStack.fast()
        if (!fis.isCustomSpawner) return

        val context = placeholderContext(player = player, item = itemStack)

        val lore = plugin.configYml.getStrings("spawner-display.lore")
            .map { line ->
                line
                    .replace("%mob%", fis.spawnerMob ?: "")
                    .replace("%delay_min%", fis.spawnerDelayMin.toString())
                    .replace("%delay_max%", fis.spawnerDelayMax.toString())
                    .replace("%radius%", fis.spawnerSpawnRange.toString())
                    .replace("%player_range%", fis.spawnerPlayerRange.toString())
                    .replace("%count%", fis.spawnerSpawnCount.toString())
                    .replace("%max_nearby%", fis.spawnerMaxNearby.toString())
                    .replace("%pickup%", fis.spawnerPickup)
                    .replace("%particle%", fis.spawnerParticleAnim ?: "none")
            }
            .map { Display.PREFIX + it.formatEco(context) }

        fis.lore = lore + fis.lore
    }
}
