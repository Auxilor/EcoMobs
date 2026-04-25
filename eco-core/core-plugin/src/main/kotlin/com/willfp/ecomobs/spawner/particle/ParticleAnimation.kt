package com.willfp.ecomobs.spawner.particle

import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.registry.Registrable
import com.willfp.ecomobs.plugin
import org.bukkit.Location
import org.bukkit.util.Vector

abstract class ParticleAnimation(val id: String) : Registrable {
    protected open val config get() = plugin.configYml.getSubsection("animations.$id")

    private var count: Int = 1

    init {
        @Suppress("LeakingThis")
        SpawnerParticleAnimations.register(this)
    }

    fun spawnParticle(center: Location, tick: Int, particle: SpawnableParticle) {
        val from = getOffset(tick)
        val to = getOffset(tick + 1)
        for (i in 0 until count) {
            val t = if (count == 1) 0.0 else i.toDouble() / (count - 1)
            val offset = from.clone().add(to.clone().subtract(from).multiply(t))
            particle.spawn(center.clone().add(offset))
        }
    }

    protected abstract fun getOffset(tick: Int): Vector

    fun reload() {
        count = config.getInt("count", 1)
        reloadAnimation()
    }

    protected abstract fun reloadAnimation()

    override fun getID(): String = id
}
