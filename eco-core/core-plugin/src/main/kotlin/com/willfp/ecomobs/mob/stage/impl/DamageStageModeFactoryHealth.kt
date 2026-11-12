package com.willfp.ecomobs.mob.stage.impl

import com.willfp.ecomobs.config.validate
import com.willfp.ecomobs.mob.stage.DamageResponse
import com.willfp.ecomobs.mob.stage.DamageStageMode
import com.willfp.ecomobs.mob.stage.DamageStageModeFactory
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import wiki.ecohub.config.Config
import wiki.ecohub.config.ConfigViolation
import wiki.ecohub.config.ViolationContext

/**
 * A pool of damage. Every point of damage from any source drains it, whoever or
 * whatever dealt it.
 */
object DamageStageModeFactoryHealth : DamageStageModeFactory("health") {
    override fun create(
        config: Config,
        key: Int,
        context: ViolationContext
    ): DamageStageMode {
        val amount = config.getDouble("health")
            .validate { it > 0 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.health",
                    "Stage health must be greater than 0"
                )
            }

        return HealthStageMode(amount)
    }

    class HealthStageMode(
        override val amount: Double
    ) : DamageStageMode(DamageStageModeFactoryHealth) {
        override fun respondTo(event: EntityDamageEvent, player: Player?): DamageResponse {
            val damage = event.finalDamage

            if (damage <= 0.0) {
                return DamageResponse.Block
            }

            return DamageResponse.Drain(damage)
        }
    }
}
