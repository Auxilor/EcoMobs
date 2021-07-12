package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.jetbrains.annotations.NotNull;

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
    public CommandHandler getHandler() {
        return (sender, args) -> {
            this.getPlugin().reload();
            this.getPlugin().reload();
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("reloaded"));
        };
    }
}
