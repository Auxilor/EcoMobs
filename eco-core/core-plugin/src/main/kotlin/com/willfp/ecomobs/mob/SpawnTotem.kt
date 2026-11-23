package com.willfp.ecomobs.mob

import org.bukkit.Material
import wiki.ecohub.rules.condition.ConditionList

data class SpawnTotem(
    val top: Material,
    val middle: Material,
    val bottom: Material
)

data class SpawnTotemOptions(
    val conditions: ConditionList,
    val totem: SpawnTotem
)
