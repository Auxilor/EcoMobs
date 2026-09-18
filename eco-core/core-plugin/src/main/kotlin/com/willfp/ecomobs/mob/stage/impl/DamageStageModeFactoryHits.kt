package com.willfp.ecomobs.mob.stage.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.config.validate
import com.willfp.ecomobs.mob.stage.DamageResponse
import com.willfp.ecomobs.mob.stage.DamageStageMode
import com.willfp.ecomobs.mob.stage.DamageStageModeFactory
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

/**
 * A fixed number of hits, whatever the weapon.
 */
object DamageStageModeFactoryHits : DamageStageModeFactory("hits") {
    override val placeholderName = "hits"

    override fun create(
        config: Config,
        key: Int,
        context: ViolationContext
    ): DamageStageMode {
        val amount = config.getInt("required-hits")
            .validate { it >= 1 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.required-hits",
                    "Required hits must be at least 1"
                )
            }

        return HitsStageMode(amount.toDouble(), config.getBool("player-only"))
    }

    class HitsStageMode(
        override val amount: Double,
        private val playerOnly: Boolean
    ) : DamageStageMode(DamageStageModeFactoryHits) {
        override fun respondTo(event: EntityDamageEvent, player: Player?): DamageResponse {
            if (playerOnly && player == null) {
                return DamageResponse.Block
            }

            // Flat 1.0 regardless of finalDamage, so a hits stage with player-only: false
            // burns down just as fast from repeated fire/lava ticks as from player hits.
            return DamageResponse.Drain(1.0)
        }
    }
}
