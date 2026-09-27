package com.willfp.ecomobs.mob.stage.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.config.validate
import com.willfp.ecomobs.folia.onEntity
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.impl.LivingMobImpl
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.mob.stage.DamageResponse
import com.willfp.ecomobs.mob.stage.DamageStage
import com.willfp.ecomobs.mob.stage.DamageStageMode
import com.willfp.ecomobs.mob.stage.DamageStageModeFactory
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.counters.Counters
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

/**
 * A pool of counts fed by libreforge counters rather than by damage. Anything that can
 * be counted can end the stage: ores mined, mobs killed, levels gained.
 */
object DamageStageModeFactoryTrigger : DamageStageModeFactory("trigger") {
    /**
     * How far the stage reaches from the player who earned a count, in blocks, when the
     * stage doesn't set a radius of its own.
     */
    private const val DEFAULT_RADIUS = 32.0

    override val placeholderName = "count"

    override fun create(
        config: Config,
        key: Int,
        context: ViolationContext
    ): DamageStageMode {
        val amount = config.getDouble("required-count")
            .validate { it > 0 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.required-count",
                    "Required count must be greater than 0"
                )
            }

        val countMethods = config.getSubsections("count-methods")
            .mapNotNull { Counters.compile(it, context.with("count methods")) }
            .validate { it.isNotEmpty() }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.count-methods",
                    "A trigger stage needs at least one valid count method, or it can never end"
                )
            }

        val radius = (config.getDoubleOrNull("radius") ?: DEFAULT_RADIUS)
            .validate { it > 0 }
            .unwrap {
                ConfigViolation(
                    "damage-stages.$key.radius",
                    "Radius must be greater than 0"
                )
            }

        return TriggerStageMode(amount, radius, countMethods)
    }

    class TriggerStageMode(
        override val amount: Double,
        private val radius: Double,
        private val countMethods: List<Counter>
    ) : DamageStageMode(DamageStageModeFactoryTrigger) {
        // Damage never moves a trigger stage, but the hit still lands as a zero-damage
        // one, so the mob flinches and take-damage effects still run.
        override fun respondTo(event: EntityDamageEvent, player: Player?) = DamageResponse.Ignore

        override fun bind(mob: EcoMob, stage: DamageStage) {
            val accumulator = StageCountAccumulator(mob, stage, radius)

            for (counter in countMethods) {
                counter.bind(accumulator)
            }
        }

        override fun unbind() {
            for (counter in countMethods) {
                counter.unbind()
            }
        }
    }

    /**
     * Spends the counts produced by a stage's count methods.
     *
     * Counters are bound once for the whole mob type, so a count arrives knowing only
     * the player who earned it. This routes it from there to whichever mobs of that type
     * are both near the player and actually in the stage it belongs to.
     */
    private class StageCountAccumulator(
        private val mob: EcoMob,
        private val stage: DamageStage,
        private val radius: Double
    ) : Accumulator {
        override fun accept(player: Player, count: Double) {
            if (count <= 0.0) {
                return
            }

            // Nearby lookups belong to the player's own region.
            onEntity(player) {
                for (entity in player.getNearbyEntities(radius, radius, radius)) {
                    val bukkitMob = entity as? Mob ?: continue

                    if (bukkitMob.ecoMob !== mob) {
                        continue
                    }

                    // Draining the stage and running its effects belong to the mob's
                    // region, which is the region its ticker runs on too.
                    onEntity(bukkitMob) {
                        drain(bukkitMob, player, count)
                    }
                }
            }
        }

        private fun drain(bukkitMob: Mob, player: Player, count: Double) {
            val living = mob.getLivingMob(bukkitMob) as? LivingMobImpl ?: return
            val tracker = living.stageTracker ?: return

            // Identity, not equality: two stages configured the same way are still
            // separate stages, and only the one this accumulator belongs to advances.
            if (tracker.isFinished || tracker.stage !== stage) {
                return
            }

            // Credited before the stage is drained, so the count that ends it still
            // places the player who earned it. Counts are credited raw, the same way a
            // hits stage credits one per hit: a stage is ranked in whatever it counts.
            plugin.topDamagerHandler.credit(bukkitMob, player, count)

            if (!tracker.consume(count, player)) {
                return
            }

            // Killed through a damage event so the player is credited as the killer, and
            // so DamageStageHandler turns it into exactly the remaining health.
            // Invulnerability is cleared first, since the mob was very likely being hit
            // as the count landed. TickHandlerDamageStages catches a cancelled event.
            bukkitMob.noDamageTicks = 0
            bukkitMob.damage(1.0, player)
        }
    }
}
