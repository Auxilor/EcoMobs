package com.willfp.ecomobs.display

import com.willfp.ecomobs.mob.options.ecoMobEgg
import com.willfp.ecomobs.plugin
import wiki.ecohub.game.item.fast
import wiki.ecohub.rules.holder.BlankHolder
import wiki.ecohub.rules.holder.ItemProvidedHolder
import wiki.ecohub.ui.display.DisplayContext
import wiki.ecohub.ui.display.DisplayModule
import wiki.ecohub.ui.display.DisplayPriority

object SpawnEggDisplay : DisplayModule(
    plugin,
    DisplayPriority.LOW
) {
    override fun display(context: DisplayContext) {
        val player = context.getPlayer() ?: return
        val egg = context.getItemStack().fast().ecoMobEgg?.spawnEgg ?: return

        egg.item.display(context, player, ItemProvidedHolder(BlankHolder, context.getItemStack()))
    }
}
