package com.willfp.ecomobs.display

import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.spawner
import wiki.ecohub.game.item.FastItemStack
import wiki.ecohub.game.item.fast
import wiki.ecohub.ui.display.DisplayContext
import wiki.ecohub.ui.display.DisplayModule
import wiki.ecohub.ui.display.DisplayPriority
import wiki.ecohub.ui.text.StringUtils
import wiki.ecohub.ui.text.formatEco

private fun FastItemStack.applySpawnerPlaceholders(text: String): String {
    val data = spawner

    return text
        .replace("%mob%", data.mob ?: "")
        .replace("%mob_formatted%", StringUtils.toTitleCase(data.mob?.replace("_", " ").orEmpty()))
        .replace("%delay_min%", data.delayMin.toString())
        .replace("%delay_max%", data.delayMax.toString())
        .replace("%radius%", data.spawnRange.toString())
        .replace("%player_range%", data.playerRange.toString())
        .replace("%count%", data.spawnCount.toString())
        .replace("%max_nearby%", data.maxNearby.toString())
        .replace("%pickup%", data.pickup)
        .replace("%particle%", data.particleAnim ?: "none")
        .replace("%explosion_proof%", data.explosionProof.toString())
        .replace("%no_ai%", data.noAI.toString())
        .replace("%size%", data.stackSize.toString())
        .replace("%max_stack_size%", SpawnerStackSettings.maxSize.toString())
}

object SpawnerItemDisplay : DisplayModule(plugin, DisplayPriority.LOW) {
    override fun display(context: DisplayContext) {
        if (context.getPlayer() == null) return
        val fis = context.getItemStack().fast()
        if (!fis.spawner.isCustomSpawner) return

        val rawTitle = plugin.configYml.getString("spawner-display.title")
        if (rawTitle.isNotEmpty()) {
            fis.setDisplayName(fis.applySpawnerPlaceholders(rawTitle).formatEco(context.getPlaceholderContext()))
        }

        context.getLore().prepend(
            (plugin.configYml.getStrings("spawner-display.lore") +
                    if (fis.spawner.stackSize > 1) {
                        plugin.configYml.getStrings("spawner-display.stacked-lore")
                    } else {
                        emptyList()
                    })
                .map { fis.applySpawnerPlaceholders(it) }
                .map { StringUtils.formatToRichComponent(it, context.getPlaceholderContext()) }
        )
    }
}
