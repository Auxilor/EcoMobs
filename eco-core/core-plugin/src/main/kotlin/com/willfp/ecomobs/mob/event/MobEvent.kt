package com.willfp.ecomobs.mob.event

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.ecomobs.plugin
import org.bukkit.event.Listener

abstract class MobEvent(
    final override val id: String
): KRegistrable, Listener {
    val configKey = this.id.replace("_", "-")

    override fun onRegister() {
        plugin.eventManager.registerListener(this)
    }
}
