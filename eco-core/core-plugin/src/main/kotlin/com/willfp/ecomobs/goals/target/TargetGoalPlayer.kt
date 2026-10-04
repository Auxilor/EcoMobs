package com.willfp.ecomobs.goals.target

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.GoalFlag
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.EnumSet

/**
 * Periodically targets a player in range, chosen by [select].
 *
 * Ported from the EcoBosses target modes. Unlike vanilla target goals, the target is
 * kept when no player can be chosen.
 */
abstract class TargetGoalPlayer(
    private val range: Double,
    private val interval: Int
) : CustomGoal<Mob>() {
    protected lateinit var mob: Mob
    private var nextSelectTick = 0

    /**
     * Choose a player to target from a non-empty list, or null to choose none.
     */
    protected abstract fun select(players: List<Player>): Player?

    protected val Player.armor: Double
        get() = getAttribute(Attribute.ARMOR)?.value ?: 0.0

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

        val target = select(players) ?: return false

        if (mob.target != target) {
            mob.target = target
        }

        return true
    }

    override fun getFlags(): EnumSet<GoalFlag> {
        return EnumSet.of(GoalFlag.TARGET)
    }
}
