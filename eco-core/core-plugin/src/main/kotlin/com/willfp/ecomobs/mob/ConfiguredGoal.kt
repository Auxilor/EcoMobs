package com.willfp.ecomobs.mob

import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.entities.ai.EntityGoal
import com.willfp.eco.core.entities.ai.Goal
import com.willfp.eco.core.entities.ai.TargetGoal
import org.bukkit.entity.Mob

data class ConfiguredGoal<T : Goal<*>>(
    val priority: Int,
    val goal: T
)

fun <T: Mob> EntityController<out T>.addGoal(goal: ConfiguredGoal<out Goal<*>>) {
    @Suppress("UNCHECKED_CAST")
    if (goal.goal is EntityGoal<*>) {
        this.addEntityGoal(goal.priority, goal.goal as EntityGoal<in T>)
    } else {
        this.addTargetGoal(goal.priority, goal.goal as TargetGoal<in T>)
    }
}
