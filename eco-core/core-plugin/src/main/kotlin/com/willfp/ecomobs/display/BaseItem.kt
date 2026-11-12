package com.willfp.ecomobs.display

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import wiki.ecohub.game.item.fast
import wiki.ecohub.rules.holder.EmptyProvidedHolder
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.ui.display.Display
import wiki.ecohub.ui.display.DisplayContext
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.text.StringUtils
import wiki.ecohub.ui.text.formatEco

class BaseItem(
    private val baseItem: ItemStack,
    private val rawDisplayName: String,
    private val rawLore: List<String>
) {
    private val itemFlags = baseItem.fast().getItemFlags()

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

        lore.addAll(fis.getLore())
        fis.setLore(lore)

        // Handle display name
        fis.setDisplayName(rawDisplayName.formatEco(context))

        // Handle flags
        fis.addItemFlags(*itemFlags.toTypedArray())

        return fis.unwrap()
    }

    fun display(context: DisplayContext, player: Player, holder: ProvidedHolder) {
        val fis = context.getItemStack().fast()

        val lore = rawLore
            .map { StringUtils.formatToRichComponent(it, context.getPlaceholderContext()) }
            .toMutableList()
        val lines = holder.getNotMetLineComponents(player)

        if (lines.isNotEmpty()) {
            lore.add(Component.empty())
            lore.addAll(lines)
        }

        context.getLore().prepend(lore)

        fis.setDisplayName(rawDisplayName.formatEco(context.getPlaceholderContext()))

        fis.addItemFlags(*itemFlags.toTypedArray())
    }
}
