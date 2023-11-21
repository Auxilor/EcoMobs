package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.toDispatcher
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EggDisplay(
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

        val lore = fis.lore.toMutableList()

        val egg = itemStack.bossEgg ?: return

        val lines = egg.spawnConditions
            .filterNot { it.isMet(player.toDispatcher(), SimpleProvidedHolder(egg)) }
            .map { it.notMetLines.map { line -> Display.PREFIX + line } }
            .flatten()


        if (lines.isNotEmpty()) {
            lore.add(Display.PREFIX)
            lore.addAll(lines)
        }

        fis.lore = lore
    }
}
