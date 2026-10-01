package com.willfp.ecomobs.display

import com.willfp.eco.core.display.DisplayContext
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.formatEcoRich
import com.willfp.eco.util.titlecase
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.spawner

private fun FastItemStack.applySpawnerPlaceholders(text: String): String {
    val data = spawner

    return text
        .replace("%mob%", data.mob ?: "")
        .replace("%mob_formatted%", data.mob?.replace("_", " ")?.titlecase() ?: "")
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
        if (context.player == null) return
        val fis = context.itemStack.fast()
        if (!fis.spawner.isCustomSpawner) return

        val rawTitle = plugin.configYml.getString("spawner-display.title")
        if (rawTitle.isNotEmpty()) {
            fis.setDisplayName(fis.applySpawnerPlaceholders(rawTitle).formatEco(context.placeholderContext))
        }

        context.lore.prepend(
            (plugin.configYml.getStrings("spawner-display.lore") +
                    if (fis.spawner.stackSize > 1) {
                        plugin.configYml.getStrings("spawner-display.stacked-lore")
                    } else {
                        emptyList()
                    })
                .map { fis.applySpawnerPlaceholders(it) }
                .formatEcoRich(context.placeholderContext)
        )
    }
}
