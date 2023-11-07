package com.willfp.ecomobs.category

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.impl.ConfigDrivenMobCategory
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.impl.ConfigDrivenEcoMob
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object MobCategories : RegistrableCategory<MobCategory>("category", "categories") {
    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        val context = ViolationContext(plugin, "Mob $id")

        try {
            val mob = ConfigDrivenMobCategory(
                plugin as EcoMobsPlugin,
                id,
                config,
                context
            )

            registry.register(mob)
        } catch (e: ConfigViolationException) {
            context.log(e.violation)
        }
    }
}
