package com.willfp.ecomobs.mob

import com.willfp.libreforge.conditions.ConditionList
import org.bukkit.Material

data class SpawnTotem(
    val top: Material,
    val middle: Material,
    val bottom: Material
)

data class SpawnTotemOptions(
    val conditions: ConditionList,
    val totem: SpawnTotem
)
