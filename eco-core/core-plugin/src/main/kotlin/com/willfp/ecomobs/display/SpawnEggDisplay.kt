package com.willfp.ecomobs.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.mob.options.ecoMobEgg
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.ItemProvidedHolder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SpawnEggDisplay(
    plugin: EcoPlugin
) : DisplayModule(
    plugin,
    DisplayPriority.LOW
) {
    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        if (player == null) {
            return
        }

        val fis = itemStack.fast()

        val egg = fis.ecoMobEgg?.spawnEgg ?: return

        val notMetLines = egg.conditions
            .getNotMetLines(player, ItemProvidedHolder(BlankHolder, itemStack))
            .map { Display.PREFIX + it }

        fis.lore = fis.lore + notMetLines
    }
}
