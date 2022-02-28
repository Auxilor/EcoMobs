package com.willfp.ecobosses.util

import org.bukkit.Material

data class SpawnTotem(
    val top: Material,
    val middle: Material,
    val bottom: Material
) {
    fun matches(totem: SpawnTotem): Boolean {
        return this.top == totem.top
                && this.middle == totem.middle
                && this.bottom == totem.bottom
    }
}
