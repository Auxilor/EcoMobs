package com.willfp.ecobosses.bosses

import com.willfp.eco.core.entities.ai.Goal

data class ConfiguredGoal<T : Goal<*>>(
    val priority: Int,
    val goal: T
)
