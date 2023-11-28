package com.willfp.ecomobs.category.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategory
import com.willfp.ecomobs.category.spawning.SpawnMethodFactories
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext

class ConfigDrivenMobCategory(
    private val plugin: EcoMobsPlugin,
    override val id: String,
    private val config: Config,
    private val context: ViolationContext
) : MobCategory {
    override val spawnMethod = SpawnMethodFactories[config.getString("spawning.type")]
        ?.create(
            this,
            config.getSubsection("spawning.${config.getString("spawning.type")}"),
            plugin,
            context.with("spawning").with(config.getString("spawning.type"))
        )
        ?: throw ConfigViolationException(
            ConfigViolation(
                "type",
                "Invalid spawning type"
            )
        ) {
            it.with("spawning")
        }

    override val isPersistent = config.getBool("persistent")

    override fun applyToMob(mob: LivingMob) {
        if (isPersistent) {
            mob.entity.isPersistent = true
            mob.entity.removeWhenFarAway = false
        }
    }

    override fun onRegister() {
        spawnMethod.start()
    }

    override fun onRemove() {
        spawnMethod.stop()
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is ConfigDrivenMobCategory && other.id == this.id
    }
}
