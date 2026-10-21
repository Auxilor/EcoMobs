package com.willfp.ecomobs.mob

import com.willfp.eco.core.entities.ai.CustomGoal
import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.Goal
import com.willfp.eco.core.entities.ai.TargetGoal
import org.bukkit.entity.Mob

class ConfiguredGoal<T : Goal<*>>(
    val priority: Int,
    private val goal: T,
    private val createGoal: () -> T?
) {
    /**
     * Custom goals keep state for the mob they run on, so each mob gets its own.
     */
    fun createForMob(): T {
        return if (goal is CustomGoal<*>) createGoal() ?: goal else goal
    }
}

/**
 * Added by list rather than by type, as custom goals are both entity and target goals.
 */
@JvmName("addTargetGoal")
fun <T : Mob> EntityController<out T>.addGoal(goal: ConfiguredGoal<out TargetGoal<*>>) {
    @Suppress("UNCHECKED_CAST")
    this.addTargetGoal(goal.priority, goal.createForMob() as TargetGoal<in T>)
}

@JvmName("addEntityGoal")
fun <T : Mob> EntityController<out T>.addGoal(goal: ConfiguredGoal<out EntityGoal<*>>) {
    @Suppress("UNCHECKED_CAST")
    this.addEntityGoal(goal.priority, goal.createForMob() as EntityGoal<in T>)
}
