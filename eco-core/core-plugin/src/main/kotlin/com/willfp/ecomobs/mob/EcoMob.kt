package com.willfp.ecomobs.mob

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.CustomEntity
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.options.SpawnEgg
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import java.util.UUID

interface EcoMob : KRegistrable {
    /**
     * The category of the mob.
     */
    val category: MobCategory

    /**
     * If the mob can mount.
     */
    val canMount: Boolean

    /**
     * The lifespan of the mob, in ticks.
     */
    val lifespan: Int

    /**
     * The raw, unformatted display name.
     */
    val rawDisplayName: String

    /**
     * The spawn egg.
     */
    val spawnEgg: SpawnEgg?

    /**
     * The spawn totem options.
     */
    val totemOptions: SpawnTotemOptions?

    /**
     * The eco custom entity.
     */
    val customEntity: CustomEntity

    /**
     * Get a living mob from a bukkit mob.
     */
    fun getLivingMob(mob: Mob): LivingMob? {
        return getLivingMob(mob.uniqueId)
    }

    /**
     * Get a living mob from a UUID.
     */
    fun getLivingMob(uuid: UUID): LivingMob?

    /**
     * Check if the player can spawn the mob at a location.
     */
    fun canPlayerSpawn(player: Player, spawnReason: SpawnReason, location: Location): Boolean

    /**
     * Spawn the mob at a location.
     */
    fun spawn(location: Location, reason: SpawnReason): LivingMob?

    /**
     * Get the damage modifier for a damage cause.
     */
    fun getDamageModifier(cause: DamageCause): Double

    /**
     * Spawn drops at a location.
     */
    fun spawnDrops(location: Location, player: Player?)

    /**
     * Handle an event.
     */
    fun handleEvent(event: MobEvent, trigger: DispatchedTrigger)

    /**
     * Get the config for a certain integration.
     */
    fun getIntegrationConfig(integration: MobIntegration): Config
}
