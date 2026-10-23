package com.willfp.ecomobs.integrations.model

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.Integration
import com.willfp.eco.core.integrations.model.ModelAnimation
import com.willfp.eco.core.integrations.model.ModelManager
import com.willfp.eco.core.integrations.model.ModelState
import com.willfp.ecomobs.event.EcoMobDamageEvent
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.impl.ecoMob
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Puts a model from one model plugin on mobs that set `integrations.<config key>.id`, and
 * plays the animations under `integrations.<config key>.animations`.
 */
class IntegrationModel(
    configKey: String,
    private val pluginName: String
) : Listener, Integration {
    private val integration = MobIntegration(configKey)

    private val once = ModelAnimation.builder()
        .loopMode(ModelAnimation.LoopMode.ONCE)
        .force(true)
        .build()

    @EventHandler
    fun handle(event: EcoMobSpawnEvent) {
        val entity = event.mob.entity
        val config = event.mob.mob.getIntegrationConfig(integration)
        val modelId = config.getStringOrNull("id")?.takeIf { it.isNotBlank() } ?: return

        if (!ModelManager.attachModel(entity, integration.configKey, modelId)) {
            return
        }
        ModelManager.setBaseEntityVisible(entity, false)

        for ((key, state) in STATE_ANIMATIONS) {
            config.pickAnimation(key)?.let { ModelManager.setStateAnimation(entity, modelId, state, it) }
        }

        play(entity, "spawn")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        (event.damager as? Mob)?.let { play(it, "melee-attack") }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: ProjectileLaunchEvent) {
        (event.entity.shooter as? Mob)?.let { play(it, "ranged-attack") }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EcoMobDamageEvent) {
        play(event.mob.entity, "take-damage")
    }

    /** Plays one of the mob's animations for [key] once, if it has any. */
    private fun play(entity: Mob, key: String) {
        val config = entity.ecoMob?.getIntegrationConfig(integration) ?: return
        val modelId = config.getStringOrNull("id")?.takeIf { it.isNotBlank() } ?: return
        val animation = config.pickAnimation(key) ?: return

        ModelManager.playAnimation(entity, modelId, animation, once)
    }

    /** One name or a list of names under `animations.<key>`, picked at random. */
    private fun Config.pickAnimation(key: String): String? =
        getStrings("animations.$key")
            .ifEmpty { listOfNotNull(getStringOrNull("animations.$key")) }
            .filter { it.isNotBlank() }
            .randomOrNull()

    override fun getPluginName(): String {
        return pluginName
    }

    private companion object {
        val STATE_ANIMATIONS = mapOf(
            "idle" to ModelState.IDLE,
            "move" to ModelState.WALK,
            "death" to ModelState.DEATH
        )
    }
}
