package com.willfp.ecomobs.name

import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.plugin
import net.kyori.adventure.text.Component
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

interface DisplayNameProxy {
    fun setDisplayName(mob: Mob, player: Player, displayName: Component, visible: Boolean)
}

fun Mob.setClientDisplayName(player: Player, name: String, visible: Boolean) =
    plugin.getProxy(DisplayNameProxy::class.java).setDisplayName(this, player, name.toComponent(), visible)
