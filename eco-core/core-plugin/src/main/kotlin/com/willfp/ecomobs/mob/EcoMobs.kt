package com.willfp.ecomobs.mob

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.mob.impl.ConfigDrivenEcoMob
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory
import java.util.concurrent.ConcurrentHashMap

object EcoMobs : RegistrableCategory<EcoMob>("mob", "mobs") {
    private val byCategory = ConcurrentHashMap<String, List<EcoMob>>()

    fun inCategory(category: MobCategory): List<EcoMob> = byCategory[category.id].orEmpty()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
        byCategory.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        val context = ViolationContext(plugin, "Mob $id")

        try {
            val mob = ConfigDrivenEcoMob(
                id,
                config,
                context
            )

            registry.register(mob)
            byCategory.merge(mob.category.id, listOf(mob)) { existing, added -> existing + added }
        } catch (e: ConfigViolationException) {
            context.log(e.violation)
        }
    }
}
