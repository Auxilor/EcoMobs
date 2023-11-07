package com.willfp.ecomobs.mob.options

import net.kyori.adventure.bossbar.BossBar

data class BossBarOptions(
    val color: BossBar.Color,
    val style: BossBar.Overlay,
    val radius: Double
)
