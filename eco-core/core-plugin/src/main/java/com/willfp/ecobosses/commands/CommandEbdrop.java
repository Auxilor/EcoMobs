package com.willfp.ecobosses.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEbdrop extends AbstractCommand {
    /**
     * Instantiate a new executor for /ebdrop.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEbdrop(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ebdrop", "ecobosses.ebdrop", true);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        player.sendMessage(this.getPlugin().getLangYml().getMessage("sent-drop"));
    }
}
