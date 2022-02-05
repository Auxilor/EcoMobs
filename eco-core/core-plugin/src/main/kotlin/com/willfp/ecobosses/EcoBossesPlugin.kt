package com.willfp.ecobosses

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecobosses.bosses.EcoBosses
import com.willfp.ecobosses.bosses.bossHolders
import com.willfp.ecobosses.commands.CommandEcobosses
import com.willfp.ecobosses.config.EcoBossesYml
import com.willfp.ecobosses.util.DiscoverRecipeListener
import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.event.Listener

class EcoBossesPlugin : LibReforgePlugin(525, 10635, "&9") {
    val ecoBossesYml: EcoBossesYml

    init {
        instance = this
        ecoBossesYml = EcoBossesYml(this)
        registerHolderProvider { it.bossHolders }
    }

    override fun handleReloadAdditional() {
        logger.info(EcoBosses.values().size.toString() + " Bosses Loaded")
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcobosses(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DiscoverRecipeListener(this)
        )
    }

    override fun getMinimumEcoVersion(): String {
        return "6.24.0"
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoBossesPlugin
    }
}
