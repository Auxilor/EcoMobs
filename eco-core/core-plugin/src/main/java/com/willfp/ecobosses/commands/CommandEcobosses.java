package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandEcobosses extends PluginCommand {
    /**
     * Instantiate a new executor for /ebdrop.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEcobosses(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ecobosses", "ecobosses.command.ecobosses", true);
        this.addSubcommand(new CommandReload(plugin))
                .addSubcommand(new CommandKillall(plugin))
                .addSubcommand(new CommandSpawn(plugin))
                .addSubcommand(new CommandBase64(plugin));
    }

    @Override
    public CommandHandler getHandler() {
        return (sender, args) -> {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-command"));
        };
    }
}
