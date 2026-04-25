package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.util.NumberUtils
import org.bukkit.util.Vector
import kotlin.math.PI

object CircleParticleAnimation : ParticleAnimation("circle") {
    private var spiralsPerSecond: Double = 0.0
    private var radius: Double = 0.0
    private var height: Double = 0.0

    init {
        reload()
    }

    override fun getOffset(tick: Int) = Vector(
        NumberUtils.fastSin(spiralsPerSecond * 2 * PI * tick / 20) * radius,
        height,
        NumberUtils.fastCos(spiralsPerSecond * 2 * PI * tick / 20) * radius
    )

    override fun reloadAnimation() {
        spiralsPerSecond = config.getDouble("spirals-per-second")
        radius = config.getDouble("radius")
        height = config.getDouble("height")
    }
}
