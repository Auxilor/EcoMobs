package com.willfp.ecomobs.spawner

import com.willfp.eco.core.particle.Particles
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.particle.ParticleData
import com.willfp.ecomobs.spawner.particle.SpawnerParticleAnimations

object SpawnerAnimations {
    private val animations = mutableMapOf<String, ParticleData>()

    fun reload() {
        animations.clear()
        SpawnerParticleAnimations.reload()

        for (id in plugin.configYml.getSubsection("spawner-animations").getKeys(false)) {
            val section = plugin.configYml.getSubsection("spawner-animations.$id")
            val particle = Particles.lookup(section.getString("particle"))
            val anim = SpawnerParticleAnimations[section.getString("type")]
                ?: SpawnerParticleAnimations.SPIRAL
            animations[id] = ParticleData(particle, anim)
        }
    }

    operator fun get(id: String): ParticleData? = animations[id]

    fun keys(): Set<String> = animations.keys
}
