package com.willfp.illusioner.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.config.Configs;
import com.willfp.illusioner.IllusionerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandIlreload extends AbstractCommand {
    /**
     * Instantiate a new executor for /ilreload.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandIlreload(@NotNull final IllusionerPlugin plugin) {
        super(plugin, "ilreload", "illusioner.reload", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        this.getPlugin().reload();
        sender.sendMessage(Configs.LANG.getMessage("reloaded"));
    }
}
