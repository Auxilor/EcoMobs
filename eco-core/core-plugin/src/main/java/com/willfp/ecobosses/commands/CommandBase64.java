package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class CommandBase64 extends Subcommand {
    /**
     * Instantiate a new executor for /ebdrop.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandBase64(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "base64", "ecobosses.command.base64", true);
    }

    @Override
    public CommandHandler getHandler() {
        return (sender, args) -> {
            Player player = (Player) sender;
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            String key = "drop-key";
            YamlConfiguration jank = new YamlConfiguration();
            jank.set(key, itemStack);
            String configString = jank.saveToString();
            String dropString = Base64.getEncoder().encodeToString(configString.getBytes());

            Bukkit.getLogger().info("Copy this into the drops section of your boss yml!");
            Bukkit.getLogger().info("\n" + dropString);

            player.sendMessage(this.getPlugin().getLangYml().getMessage("sent-drop"));
        };
    }
}
