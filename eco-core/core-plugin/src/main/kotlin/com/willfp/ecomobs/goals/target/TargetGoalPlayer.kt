package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.GoalFlag
import com.willfp.eco.core.serialization.KeyedDeserializer
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.GameMode
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.EnumSet

fun interface PlayerSelector {
    /**
     * Choose a player to target from a non-empty list, or null to choose none.
     */
    fun select(mob: Mob, players: List<Player>): Player?
}

/**
 * Periodically targets a player in range, chosen by a [PlayerSelector].
 *
 * Ported from the EcoBosses target modes. Unlike vanilla target goals, the target is
 * kept when no player can be chosen.
 */
class TargetGoalPlayer(
    private val range: Double,
    private val interval: Int,
    private val selector: PlayerSelector
) : CustomGoal<Mob>() {
    private lateinit var mob: Mob
    private var nextSelectTick = 0

    override fun initialize(mob: Mob) {
        this.mob = mob
    }

    override fun canUse(): Boolean {
        return selectTarget()
    }

    override fun canContinueToUse(): Boolean {
        return mob.ticksLived < nextSelectTick || selectTarget()
    }

    private fun selectTarget(): Boolean {
        // The goal selector doesn't run every tick, so this counts lived ticks instead.
        if (mob.ticksLived < nextSelectTick) {
            return false
        }

        nextSelectTick = mob.ticksLived + interval

        val players = mob.getNearbyEntities(range, range, range)
            .filterIsInstance<Player>()
            .filter { it.gameMode == GameMode.SURVIVAL || it.gameMode == GameMode.ADVENTURE }
            .ifEmpty { return false }

        val target = selector.select(mob, players) ?: return false

        if (mob.target != target) {
            mob.target = target
        }

        return true
    }

    override fun getFlags(): EnumSet<GoalFlag> {
        return EnumSet.of(GoalFlag.TARGET)
    }

    class Deserializer(
        private val id: String,
        private val createSelector: (Config) -> PlayerSelector
    ) : KeyedDeserializer<TargetGoalPlayer> {
        override fun getKey() = namespacedKeyOf("ecomobs", id)

        override fun deserialize(config: Config): TargetGoalPlayer {
            return TargetGoalPlayer(
                (config.getDoubleOrNull("range") ?: 40.0).coerceAtLeast(1.0),
                (config.getIntOrNull("interval") ?: 10).coerceAtLeast(1),
                createSelector(config)
            )
        }
    }
}
