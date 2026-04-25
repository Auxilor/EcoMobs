package com.willfp.ecomobs.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
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

        val lore = listOf(
            "${Display.PREFIX}&8Mob: &f${fis.spawnerMob}",
            "${Display.PREFIX}&8Delay: &f${fis.spawnerDelayMin}-${fis.spawnerDelayMax} ticks",
            "${Display.PREFIX}&8Radius: &f${fis.spawnerSpawnRange}",
            "${Display.PREFIX}&8Player Range: &f${fis.spawnerPlayerRange}",
            "${Display.PREFIX}&8Count: &f${fis.spawnerSpawnCount}",
            "${Display.PREFIX}&8Max Nearby: &f${fis.spawnerMaxNearby}",
            "${Display.PREFIX}&8Pickup: &f${fis.spawnerPickup}",
            "${Display.PREFIX}&8Particle: &f${fis.spawnerParticleAnim ?: "none"}"
        )

        fis.lore = lore + fis.lore
    }
}
