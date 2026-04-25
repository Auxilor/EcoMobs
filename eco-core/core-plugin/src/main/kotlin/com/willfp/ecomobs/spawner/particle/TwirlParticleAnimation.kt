package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.util.NumberUtils
import org.bukkit.util.Vector
import kotlin.math.PI

private fun lerp(start: Double, end: Double, fraction: Double): Double =
    (start * (1 - fraction)) + (end * fraction)

object TwirlParticleAnimation : ParticleAnimation("twirl") {
    private var smallRadius: Double = 0.0
    private var largeRadius: Double = 0.0
    private var ticks: Double = 40.0
    private var startHeight: Double = 0.0
    private var endHeight: Double = 0.0
    private var spiralsPerSecond: Double = 0.0

    init {
        reload()
    }

    override fun getOffset(tick: Int): Vector {
        val radius = lerp(smallRadius, largeRadius, (tick % ticks) / ticks)
        val height = lerp(startHeight, endHeight, (tick % ticks) / ticks)
        val v = Vector(
            NumberUtils.fastSin(spiralsPerSecond * 2 * PI * tick / 20) * radius,
            height,
            NumberUtils.fastCos(spiralsPerSecond * 2 * PI * tick / 20) * radius
        )
        return if (tick % 2 == 0) v.setX(-v.x).setZ(-v.z) else v
    }

    override fun reloadAnimation() {
        smallRadius = config.getDouble("small-radius")
        largeRadius = config.getDouble("large-radius")
        ticks = config.getDouble("ticks")
        startHeight = config.getDouble("start-height")
        endHeight = config.getDouble("end-height")
        spiralsPerSecond = config.getDouble("spirals-per-second")
    }
}
