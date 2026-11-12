package com.willfp.ecomobs.category

import com.willfp.ecomobs.category.impl.ConfigDrivenMobCategory
import com.willfp.ecomobs.config.ConfigViolationException
import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.config.category.RegistrableCategory
import wiki.ecohub.core.plugin.EcoPlugin

object MobCategories : RegistrableCategory<MobCategory>("category", "categories") {
    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        val context = ViolationContext(plugin, "Category $id")

        try {
            val mob = ConfigDrivenMobCategory(
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
