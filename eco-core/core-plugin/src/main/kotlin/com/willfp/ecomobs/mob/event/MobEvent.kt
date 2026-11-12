package com.willfp.ecomobs.mob.event

import com.willfp.ecomobs.plugin
import org.bukkit.event.Listener
import wiki.ecohub.core.registry.KRegistrable

abstract class MobEvent(
    final override val id: String
): KRegistrable, Listener {
    val configKey = this.id.replace("_", "-")

    override fun onRegister() {
        plugin.eventManager.registerListener(this)
    }
}
