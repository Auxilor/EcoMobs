package com.willfp.illusioner.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.config.IllusionerConfigs;
import com.willfp.illusioner.illusioner.IllusionerManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandIldrop extends AbstractCommand {
    /**
     * Instantiate a new executor for /ildrop.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandIldrop(@NotNull final IllusionerPlugin plugin) {
        super(plugin, "ildrop", "illusioner.drop", true);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            player.sendMessage(this.getPlugin().getLangYml().getMessage("no-item-held"));
            return;
        }

        IllusionerConfigs.DROPS.getConfig().set(String.valueOf(IllusionerManager.OPTIONS.getDrops().size()), itemStack);
        player.sendMessage(this.getPlugin().getLangYml().getMessage("added-drop"));
        player.sendMessage(this.getPlugin().getLangYml().getMessage("reload-plugin"));
    }
}
