package com.willfp.ecomobs.mob

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.impl.UncategorisedMobCategory
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.mob.impl.ConfigDrivenEcoMob
import com.willfp.ecomobs.scheduled.ScheduledSpawnState
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory
import java.util.concurrent.ConcurrentHashMap

object EcoMobs : RegistrableCategory<EcoMob>("mob", "mobs") {
    private val byCategory = ConcurrentHashMap<String, List<EcoMob>>()

    fun inCategory(category: MobCategory): List<EcoMob> = byCategory[category.id].orEmpty()

    /**
     * Every mob with scheduled spawning.
     */
    val scheduled: List<EcoMob>
        get() = values().filter { it.scheduledSpawn != null }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
        byCategory.clear()
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        ScheduledSpawnState.retain(
            registry.values().flatMap { it.scheduledSpawn?.points.orEmpty() }.map { it.key }.toSet()
        )
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

            if (mob.category !== UncategorisedMobCategory) {
                byCategory.merge(mob.category.id, listOf(mob)) { existing, added -> existing + added }
            }
        } catch (e: ConfigViolationException) {
            context.log(e.violation)
        }
    }
}
