package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEcobosses extends PluginCommand {
    /**
     * Instantiate a new executor for /ebdrop.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEcobosses(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ecobosses", "ecobosses.command.ecobosses", false);
        this.addSubcommand(new CommandReload(plugin))
                .addSubcommand(new CommandKillall(plugin))
                .addSubcommand(new CommandSpawn(plugin))
                .addSubcommand(new CommandGive(plugin));
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-command"));
    }
}
