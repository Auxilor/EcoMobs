package com.willfp.ecomobs.spawner

import com.willfp.eco.core.particle.Particles
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.particle.ParticleData
import com.willfp.ecomobs.spawner.particle.SpawnerParticleAnimations

object SpawnerAnimations {
    // Swapped whole on reload rather than cleared and refilled, so a spawner ticking on
    // another region can never read a half-built map.
    @Volatile
    private var animations: Map<String, ParticleData> = emptyMap()

    fun reload() {
        SpawnerParticleAnimations.reload()

        val loaded = mutableMapOf<String, ParticleData>()

        for (id in plugin.configYml.getSubsection("spawner-animations").getKeys(false)) {
            val section = plugin.configYml.getSubsection("spawner-animations.$id")
            val particle = Particles.lookup(section.getString("particle"))
            val anim = SpawnerParticleAnimations[section.getString("type")]
                ?: SpawnerParticleAnimations.SPIRAL
            loaded[id] = ParticleData(particle, anim)
        }

        animations = loaded
    }

    operator fun get(id: String): ParticleData? = animations[id]

    fun keys(): Set<String> = animations.keys
}
