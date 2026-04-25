package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.core.registry.Registry

object SpawnerParticleAnimations : Registry<ParticleAnimation>() {
    val CIRCLE: ParticleAnimation = CircleParticleAnimation
    val SPIRAL: ParticleAnimation = SpiralParticleAnimation
    val DOUBLE_SPIRAL: ParticleAnimation = DoubleSpiralParticleAnimation
    val TILTED_RINGS: ParticleAnimation = TiltedRingsParticleAnimation
    val TWIRL: ParticleAnimation = TwirlParticleAnimation

    fun reload() {
        this.forEach { it.reload() }
    }
}
