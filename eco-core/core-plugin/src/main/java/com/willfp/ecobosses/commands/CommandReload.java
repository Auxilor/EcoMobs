package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandReload extends Subcommand {
    /**
     * Instantiate a new executor for /ebreload.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandReload(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "reload", "ecobosses.command.reload", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        this.getPlugin().reload();
        this.getPlugin().reload();
        sender.sendMessage(this.getPlugin().getLangYml().getMessage("reloaded"));
    }
}
