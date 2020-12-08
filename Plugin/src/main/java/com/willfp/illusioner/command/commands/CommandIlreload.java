package com.willfp.illusioner.command.commands;

import com.willfp.illusioner.command.AbstractCommand;
import com.willfp.illusioner.config.ConfigManager;
import com.willfp.illusioner.util.internal.Loader;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandIlreload extends AbstractCommand {
    public CommandIlreload() {
        super("ilreload", "illusioner.reload", false);
    }

    @Override
    public void onExecute(CommandSender sender, List<String> args) {
        Loader.reload();
        sender.sendMessage(ConfigManager.getLang().getMessage("reloaded"));
    }
}
