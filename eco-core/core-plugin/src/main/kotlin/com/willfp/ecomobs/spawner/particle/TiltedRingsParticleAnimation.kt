package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.util.NumberUtils
import org.bukkit.util.Vector
import kotlin.math.PI

object TiltedRingsParticleAnimation : ParticleAnimation("tilted_rings") {
    private var spiralsPerSecond: Double = 0.0
    private var radius: Double = 0.0
    private var xOffset: Double = 0.0
    private var yOffset: Double = 0.0

    init {
        reload()
    }

    override fun getOffset(tick: Int): Vector {
        val base = if (tick % 2 == 0) {
            Vector(
                NumberUtils.fastSin(spiralsPerSecond * 4 * PI * tick / 20) * radius,
                0.0,
                NumberUtils.fastCos(spiralsPerSecond * 4 * PI * tick / 20) * radius
            )
        } else {
            Vector(
                0.0,
                NumberUtils.fastSin(spiralsPerSecond * 4 * PI * tick / 20) * radius,
                NumberUtils.fastCos(spiralsPerSecond * 4 * PI * tick / 20) * radius
            )
        }
        return base.rotateAroundY(NumberUtils.fastCos(yOffset))
            .rotateAroundX(NumberUtils.fastCos(xOffset))
    }

    override fun reloadAnimation() {
        spiralsPerSecond = config.getDouble("spirals-per-second")
        radius = config.getDouble("radius")
        xOffset = config.getDouble("x-offset")
        yOffset = config.getDouble("y-offset")
    }
}
