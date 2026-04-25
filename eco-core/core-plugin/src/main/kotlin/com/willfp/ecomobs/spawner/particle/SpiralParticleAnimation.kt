package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.util.NumberUtils
import org.bukkit.util.Vector
import kotlin.math.PI

object SpiralParticleAnimation : ParticleAnimation("spiral") {
    private var spiralsPerSecond: Double = 0.0
    private var risesPerSecond: Double = 0.0
    private var radius: Double = 0.0
    private var height: Double = 0.0

    init {
        reload()
    }

    override fun getOffset(tick: Int) = Vector(
        NumberUtils.fastSin(spiralsPerSecond * 2 * PI * tick / 20) * radius,
        -NumberUtils.fastCos(risesPerSecond * 2 * PI * tick / 20) * height,
        NumberUtils.fastCos(spiralsPerSecond * 2 * PI * tick / 20) * radius
    )

    override fun reloadAnimation() {
        spiralsPerSecond = config.getDouble("spirals-per-second")
        risesPerSecond = config.getDouble("rises-per-second")
        radius = config.getDouble("radius")
        height = config.getDouble("height")
    }
}
