package com.willfp.ecomobs.category.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategory
import com.willfp.libreforge.ViolationContext

class ConfigDrivenMobCategory(
    private val plugin: EcoMobsPlugin,
    override val id: String,
    private val config: Config,
    private val context: ViolationContext
) : MobCategory {
}
