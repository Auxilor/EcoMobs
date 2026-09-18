package com.willfp.ecomobs.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.titlecase
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.spawner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
}

object SpawnerItemDisplay : DisplayModule(plugin, DisplayPriority.LOW) {
    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        if (player == null) return
        val fis = itemStack.fast()
        if (!fis.spawner.isCustomSpawner) return

        val context = placeholderContext(player = player, item = itemStack)

        val rawTitle = plugin.configYml.getString("spawner-display.title")
        if (rawTitle.isNotEmpty()) {
            fis.setDisplayName(fis.applySpawnerPlaceholders(rawTitle).formatEco(context))
        }

        val lore = plugin.configYml.getStrings("spawner-display.lore")
            .map { Display.PREFIX + fis.applySpawnerPlaceholders(it).formatEco(context) }

        fis.lore = lore + fis.lore
    }
}
