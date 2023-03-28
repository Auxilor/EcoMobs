package com.willfp.ecobosses.bosses

import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.registry.Registry
import com.willfp.ecobosses.EcoBossesPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.configs.LegacyLocation
import com.willfp.libreforge.separatorAmbivalent
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.io.File
import java.util.UUID

object Bosses : ConfigCategory("boss", "bosses") {
    /** Registered bosses. */
    private val registry = Registry<EcoBoss>()

    override val legacyLocation = LegacyLocation(
        "ecobosses.yml",
        "bosses"
    )

    /**
     * Get all registered [EcoBoss]s.
     *
     * @return A list of all [EcoBoss]s.
     */
    @JvmStatic
    fun values(): List<EcoBoss> {
        return ImmutableList.copyOf(registry.values())
    }

    /**
     * Get [EcoBoss] matching ID.
     *
     * @param name The name to search for.
     * @return The matching [EcoBoss], or null if not found.
     */
    @JvmStatic
    fun getByID(name: String): EcoBoss? {
        return registry[name]
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(EcoBoss(id, config, plugin as EcoBossesPlugin))
    }

    /**
     * Get all currently alive [EcoBoss]es.
     *
     * @return All living bosses.
     */
    @JvmStatic
    fun getAllAlive(): Set<LivingEcoBoss> {
        val entities = mutableSetOf<LivingEcoBoss>()

        for (boss in values()) {
            entities.addAll(boss.getAllAlive())
        }

        return entities
    }

    /**
     * Get [LivingEcoBoss].
     *
     * @return The boss, or null if not a boss.
     */
    operator fun get(uuid: UUID): LivingEcoBoss? {
        for (boss in values()) {
            val found = boss[uuid]

            if (found != null) {
                return found
            }
        }

        return null
    }

    /**
     * Get [LivingEcoBoss].
     *
     * @return The boss, or null if not a boss.
     */
    operator fun get(entity: LivingEntity): LivingEcoBoss? {
        return get(entity.uniqueId)
    }

    /** If an entity is a boss. */
    val Entity?.isBoss: Boolean
        get() {
            if (this !is LivingEntity) {
                return false
            }

            return Bosses[this] != null
        }
}
