package com.willfp.ecomobs.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BaseItem(
    private val baseItem: ItemStack,
    private val rawDisplayName: String,
    private val rawLore: List<String>
) {
    private val itemFlags = baseItem.fast().itemFlags

    val item: ItemStack
        get() = display(baseItem.clone(), null, EmptyProvidedHolder)

    fun display(itemStack: ItemStack, player: Player?, holder: ProvidedHolder): ItemStack {
        val fis = itemStack.fast()

        val context = placeholderContext(
            player = player,
            item = itemStack
        )

        // Handle lore
        val lore = rawLore.map { "${Display.PREFIX}${it.formatEco(context)}" }.toMutableList()

        if (player != null) {
            val lines = holder.getNotMetLines(player).map { Display.PREFIX + it }

            if (lines.isNotEmpty()) {
                lore.add(Display.PREFIX)
                lore.addAll(lines)
            }
        }

        lore.addAll(fis.lore)
        fis.lore = lore

        // Handle display name
        fis.displayName = rawDisplayName.formatEco(context)

        // Handle flags
        fis.addItemFlags(*itemFlags.toTypedArray())

        return fis.unwrap()
    }
}
