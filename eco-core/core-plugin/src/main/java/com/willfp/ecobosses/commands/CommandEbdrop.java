package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        String key = "drop-key";
        YamlConfiguration jank = new YamlConfiguration();
        jank.set(key, itemStack);
        String configString = jank.saveToString();
        String dropString = Base64.getEncoder().encodeToString(configString.getBytes(StandardCharsets.UTF_8));

        Bukkit.getLogger().info("Copy this into the drops section of your boss yml!");
        Bukkit.getLogger().info("\n" + dropString);

        player.sendMessage(this.getPlugin().getLangYml().getMessage("sent-drop"));
    }
}
