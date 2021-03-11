package com.willfp.ecobosses.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEbreload extends AbstractCommand {
    /**
     * Instantiate a new executor for /ebreload.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEbreload(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ebreload", "ecobosses.reload", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        this.getPlugin().reload();
        this.getPlugin().reload();
        sender.sendMessage(this.getPlugin().getLangYml().getMessage("reloaded"));
    }
}
