package com.willfp.ecomobs.mob

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.mob.impl.ConfigDrivenEcoMob
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object EcoMobs : RegistrableCategory<EcoMob>("mob", "mobs") {
    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        val context = ViolationContext(plugin, "Mob $id")

        try {
            val mob = ConfigDrivenEcoMob(
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
