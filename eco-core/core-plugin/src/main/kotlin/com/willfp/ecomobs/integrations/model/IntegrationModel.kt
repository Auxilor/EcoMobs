package com.willfp.ecomobs.integrations.model

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
import wiki.ecohub.config.Config
import wiki.ecohub.provider.ExternalProvider
import wiki.ecohub.provider.content.ExternalModelService
import wiki.ecohub.provider.content.ModelAnimation
import wiki.ecohub.provider.content.ModelState

/**
 * Puts a model from one model plugin on mobs that set `integrations.<config key>.id`, and
 * plays the animations under `integrations.<config key>.animations`.
 */
class IntegrationModel(
    configKey: String
) : Listener, ExternalProvider {
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

        if (!ExternalModelService.attachModel(entity, integration.configKey, modelId)) {
            return
        }
        ExternalModelService.setBaseEntityVisible(entity, false)

        for ((key, state) in STATE_ANIMATIONS) {
            config.pickAnimation(key)?.let { ExternalModelService.setStateAnimation(entity, modelId, state, it) }
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

    private fun play(entity: Mob, key: String) {
        val config = entity.ecoMob?.getIntegrationConfig(integration) ?: return
        val modelId = config.getStringOrNull("id")?.takeIf { it.isNotBlank() }
        val animation = config.pickAnimation(key)

        if (modelId != null && animation != null) {
            ExternalModelService.playAnimation(entity, modelId, animation, once)
        }
    }

    private fun Config.pickAnimation(key: String): String? =
        getStrings("animations.$key")
            .ifEmpty { listOfNotNull(getStringOrNull("animations.$key")) }
            .filter { it.isNotBlank() }
            .randomOrNull()

    override fun getListeners(): Collection<Listener> = listOf(this)

    private companion object {
        val STATE_ANIMATIONS = mapOf(
            "idle" to ModelState.IDLE,
            "move" to ModelState.WALK,
            "death" to ModelState.DEATH
        )
    }
}
