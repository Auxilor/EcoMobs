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
import com.willfp.ecomobs.spawner.isCustomSpawner
import com.willfp.ecomobs.spawner.spawnerDelayMax
import com.willfp.ecomobs.spawner.spawnerDelayMin
import com.willfp.ecomobs.spawner.spawnerMaxNearby
import com.willfp.ecomobs.spawner.spawnerMob
import com.willfp.ecomobs.spawner.spawnerParticleAnim
import com.willfp.ecomobs.spawner.spawnerExplosionProof
import com.willfp.ecomobs.spawner.spawnerPickup
import com.willfp.ecomobs.spawner.spawnerPlayerRange
import com.willfp.ecomobs.spawner.spawnerSpawnCount
import com.willfp.ecomobs.spawner.spawnerSpawnRange

private fun FastItemStack.applySpawnerPlaceholders(text: String): String =
    text
        .replace("%mob%", spawnerMob ?: "")
        .replace("%mob_formatted%", spawnerMob?.replace("_", " ")?.titlecase() ?: "")
        .replace("%delay_min%", spawnerDelayMin.toString())
        .replace("%delay_max%", spawnerDelayMax.toString())
        .replace("%radius%", spawnerSpawnRange.toString())
        .replace("%player_range%", spawnerPlayerRange.toString())
        .replace("%count%", spawnerSpawnCount.toString())
        .replace("%max_nearby%", spawnerMaxNearby.toString())
        .replace("%pickup%", spawnerPickup)
        .replace("%particle%", spawnerParticleAnim ?: "none")
        .replace("%explosion_proof%", spawnerExplosionProof.toString())

object SpawnerItemDisplay : DisplayModule(plugin, DisplayPriority.LOW) {
    override fun display(context: DisplayContext) {
        if (context.player == null) return
        val fis = context.itemStack.fast()
        if (!fis.isCustomSpawner) return

        val rawTitle = plugin.configYml.getString("spawner-display.title")
        if (rawTitle.isNotEmpty()) {
            fis.setDisplayName(fis.applySpawnerPlaceholders(rawTitle).formatEco(context.placeholderContext))
        }

        context.lore.prepend(
            plugin.configYml.getStrings("spawner-display.lore")
                .map { fis.applySpawnerPlaceholders(it) }
                .formatEcoRich(context.placeholderContext)
        )
    }
}
