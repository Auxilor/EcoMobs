package com.willfp.ecobosses.bosses

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.ecobosses.EcoBossesPlugin
import com.willfp.libreforge.chains.EffectChains
import org.bukkit.entity.LivingEntity
import java.util.UUID

object Bosses {
    /**
     * Registered bosses.
     */
    private val BY_ID: BiMap<String, EcoBoss> = HashBiMap.create()

    /**
     * Get all registered [EcoBoss]s.
     *
     * @return A list of all [EcoBoss]s.
     */
    @JvmStatic
    fun values(): List<EcoBoss> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Get [EcoBoss] matching ID.
     *
     * @param name The name to search for.
     * @return The matching [EcoBoss], or null if not found.
     */
    @JvmStatic
    fun getByID(name: String): EcoBoss? {
        return BY_ID[name]
    }

    /**
     * Update all [EcoBoss]s.
     *
     * @param plugin Instance of EcoBosses.
     */
    @ConfigUpdater
    @JvmStatic
    fun update(plugin: EcoBossesPlugin) {
        plugin.ecoBossesYml.getSubsections("chains").mapNotNull {
            EffectChains.compile(it, "Effect Chains")
        }

        for (boss in values()) {
            removeBoss(boss)
        }

        for (bossConfig in plugin.ecoBossesYml.getSubsections("bosses")) {
            addNewBoss(EcoBoss(bossConfig, plugin))
        }
    }

    /**
     * Add new [EcoBoss] to EcoBosses.
     *
     * @param set The [EcoBoss] to add.
     */
    @JvmStatic
    fun addNewBoss(set: EcoBoss) {
        BY_ID.remove(set.id)
        BY_ID[set.id] = set
    }

    /**
     * Remove [EcoBoss] from EcoBosses.
     *
     * @param set The [EcoBoss] to remove.
     */
    @JvmStatic
    fun removeBoss(set: EcoBoss) {
        BY_ID.remove(set.id)
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
}
