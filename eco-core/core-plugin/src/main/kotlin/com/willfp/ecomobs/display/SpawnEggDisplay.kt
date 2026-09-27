package com.willfp.ecomobs.display

import com.willfp.eco.core.display.DisplayContext
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.ecomobs.mob.options.ecoMobEgg
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.ItemProvidedHolder

object SpawnEggDisplay : DisplayModule(
    plugin,
    DisplayPriority.LOW
) {
    override fun display(context: DisplayContext) {
        val player = context.player ?: return
        val egg = context.itemStack.fast().ecoMobEgg?.spawnEgg ?: return

        egg.item.display(context, player, ItemProvidedHolder(BlankHolder, context.itemStack))
    }
}
