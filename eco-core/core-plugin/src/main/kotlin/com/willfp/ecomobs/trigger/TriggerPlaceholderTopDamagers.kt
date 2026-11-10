package com.willfp.ecomobs.trigger

import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder
import org.bukkit.entity.Mob

/**
 * Adds the top damager placeholders to any libreforge trigger whose victim is an EcoMobs
 * mob, so effects in other plugins can read them.
 */
object TriggerPlaceholderTopDamagers : TriggerPlaceholder("ecomobs_top_damagers") {
    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val victim = data.victim as? Mob

        if (victim?.ecoMob == null) {
            return emptyList()
        }

        return plugin.topDamagerHandler.generatePlaceholders(victim)
    }
}
