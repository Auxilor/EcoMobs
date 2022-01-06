package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.util.BossUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandKillall extends Subcommand {
    /**
     * Instantiate a new executor for /ebspawn.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandKillall(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "killall", "ecobosses.command.killall", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        boolean force = false;
        if (args.size() == 1) {
            force = args.get(0).equalsIgnoreCase("force");
        }

        sender.sendMessage(this.getPlugin().getLangYml().getMessage("killall").replace("%amount%", String.valueOf(BossUtils.killAllBosses(force))));
    }
}
